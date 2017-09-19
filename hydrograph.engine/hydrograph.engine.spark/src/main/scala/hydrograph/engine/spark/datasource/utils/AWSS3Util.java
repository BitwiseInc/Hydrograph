/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/

package hydrograph.engine.spark.datasource.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.*;


import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.esotericsoftware.minlog.Log;
import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.zip.ZipInputStream;


/**
 * Created for  AWSS3Util on 9/7/2017.
 */
public class AWSS3Util {
    static final Logger log=Logger.getLogger(AWSS3Util.class.getName());
    boolean done;
    public void upload(RunFileTransferEntity runFileTransferEntity) {
        log.debug("Start AWSS3Util upload");
        int retryAttempt = 0;
        int i;

        java.nio.file.Path inputfile = new File(runFileTransferEntity.getLocalPath()).toPath();
        String keyName=inputfile.getFileName().toString();
        log.info("keyName is: "+keyName);
        log.info("bucket name is:"+runFileTransferEntity.getBucketName());
        log.info("Folder Name is"+runFileTransferEntity.getFolder_name_in_bucket());

        String amazonFileUploadLocationOriginal=null;
        FileInputStream stream=null;
        File filecheck=new File(runFileTransferEntity.getLocalPath());
        if(runFileTransferEntity.getFailOnError())
        if(!(filecheck.isFile()||filecheck.isDirectory())&&!(runFileTransferEntity.getLocalPath().contains("hdfs://"))){
            Log.error("Invalid local path.Please provide valid path");
            throw new AWSUtilException("Invalid local path");
        }

        if (runFileTransferEntity.getRetryAttempt() == 0)
            retryAttempt = 1;
        else
            retryAttempt = runFileTransferEntity.getRetryAttempt();


        for (i = 0; i < retryAttempt; i++) {
            log.info("connection attempt: "+(i+1));
            try {
                AmazonS3 s3Client = null;
                ClientConfiguration clientConf = new ClientConfiguration();
                clientConf.setProtocol(Protocol.HTTPS);
                if (runFileTransferEntity.getCrediationalPropertiesFile() == null) {
                    BasicAWSCredentials creds = new BasicAWSCredentials(runFileTransferEntity.getAccessKeyID(), runFileTransferEntity.getSecretAccessKey());
                    s3Client = AmazonS3ClientBuilder.standard().withClientConfiguration(clientConf).withRegion(runFileTransferEntity.getRegion()).withCredentials(new AWSStaticCredentialsProvider(creds)).build();
                }
                else {
                    File securityFile = new File(runFileTransferEntity.getCrediationalPropertiesFile());
                    PropertiesCredentials creds = new PropertiesCredentials(securityFile);

                    s3Client = AmazonS3ClientBuilder.standard().withClientConfiguration(clientConf).withRegion(runFileTransferEntity.getRegion()).withCredentials(new AWSStaticCredentialsProvider(creds)).build();
                }

                String s3folderName=null;
                String filepath =  runFileTransferEntity.getFolder_name_in_bucket();
                log.debug("file path name"+filepath);
                s3folderName=filepath;

                if(s3folderName!=null&&!s3folderName.trim().equals("")) {
                    amazonFileUploadLocationOriginal = runFileTransferEntity.getBucketName() + "/" + s3folderName;
                }
                else{
                    amazonFileUploadLocationOriginal=runFileTransferEntity.getBucketName();
                }


           File f=new File(runFileTransferEntity.getLocalPath());

            if(runFileTransferEntity.getLocalPath().contains("hdfs://")){
                log.debug("Provided HDFS local path ");
                String inputPath= runFileTransferEntity.getLocalPath();
                String s1= inputPath.substring(7,inputPath.length());
                String s2=s1.substring(0,s1.indexOf("/"));
                File file= new File("/tmp");
                if(!file.exists())
                file.mkdir();
                Configuration conf = new Configuration();
                conf.set("fs.defaultFS", "hdfs://"+s2);
                FileSystem hdfsFileSystem = FileSystem.get(conf);
                Path local = new Path("/tmp");
                String s= inputPath.substring(7,inputPath.length());
                String hdfspath= s.substring(s.indexOf("/"),s.length());
                Path hdfs = new Path(hdfspath);
                ObjectMetadata objectMetadata = new ObjectMetadata();
                if (runFileTransferEntity.getEncoding() != null)
                    objectMetadata.setContentEncoding(runFileTransferEntity.getEncoding());
                File dir = new File(hdfspath);
                if(hdfsFileSystem.isDirectory(new Path(hdfspath))) {
                    InputStream is= null;
                    OutputStream os=null;
                    String localDirectory=hdfspath.substring(hdfspath.lastIndexOf("/")+1);
                    FileStatus[] fileStatus = hdfsFileSystem.listStatus(new Path(runFileTransferEntity.getLocalPath()));
                    Path[] paths = FileUtil.stat2Paths(fileStatus);
                    File dirs=null;

                    try{
                        String folderName=hdfspath.substring(hdfspath.lastIndexOf("/")+1);

                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        String dateWithoutTime = df.format(new Date()).toString();
                        Random ran = new Random();
                        String tempFolder="ftp_sftp_"+ System.nanoTime() + "_" +  ran.nextInt(1000);
                        dirs=new File("/tmp/"+tempFolder);

                        boolean success = dirs.mkdirs();
                        for(Path files: paths) {
                            is = hdfsFileSystem.open(files);
                            os = new BufferedOutputStream(new FileOutputStream(dirs+"/"+files.getName()));
                            org.apache.hadoop.io.IOUtils.copyBytes(is, os, conf);
                    }

                        for(File files: dirs.listFiles()){

                            if(files.isFile()) {
                                s3Client.putObject(new PutObjectRequest(amazonFileUploadLocationOriginal + "/" + folderName, files.getName(),files));
                            }


                        }
                    }

                    catch(IOException e){
                        Log.error("IOException occured while transfering the file",e);
                    }
                    finally{
                        org.apache.hadoop.io.IOUtils.closeStream(is);
                        org.apache.hadoop.io.IOUtils.closeStream(os);
                        if(dirs!=null){


                            FileUtils.deleteDirectory(dirs);
                        }

                    }

                }
                else {
                    hdfsFileSystem.copyToLocalFile(false, hdfs, local);
                    stream = new FileInputStream("/tmp/" + f.getName());
                    File S3file=new File("/tmp/" + f.getName());

                    PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, file);
                    PutObjectResult result = s3Client.putObject(putObjectRequest);
                }
                }else {

                ObjectMetadata objectMetadata = new ObjectMetadata();
                if(runFileTransferEntity.getEncoding()!=null)
                    objectMetadata.setContentEncoding(runFileTransferEntity.getEncoding());

                if(Files.isDirectory(inputfile) ) {

                    File fileloc = new File(inputfile.toAbsolutePath().toString());
                    String folderName = new File(runFileTransferEntity.getLocalPath()).getName();
                    for(File files: fileloc.listFiles()){

                        if(files.isFile()) {
                            PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal + "/" + folderName, files.getName(), files);


                            PutObjectResult result = s3Client.putObject(putObjectRequest);
                        }


                    }

                }
                else {
                    PutObjectRequest putObjectRequest=null;
                        File file=new File(runFileTransferEntity.getLocalPath());
                    stream = new FileInputStream(runFileTransferEntity.getLocalPath());
                         putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, file);
                    PutObjectResult result = s3Client.putObject(putObjectRequest);
                }
            }

        }

            catch (AmazonServiceException e) {
                if (e.getStatusCode() == 403 || e.getStatusCode() == 404) {
                    if(runFileTransferEntity.getFailOnError())
                        Log.error("Incorrect details provided.Please provide valid details",e);
                    throw new AWSUtilException("Incorrect details provided");

                }

                {
                    try {
                        Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                    } catch (Exception e1) {
                        Log.error("Exception occured while sleeping the thread");
                    }
                    continue;
                }

            } catch (Exception e) {
                log.error("error while transferring file",e);
                try {
                    Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                } catch (Exception e1) {
                    Log.error("Exception occured while sleeping the thread");
                }
                continue;
            }
            catch(Error err){
                Log.error("Error occured while uploading the file",err);
                throw new AWSUtilException(err);
            }
            done=true;
            break;
        }
        if(runFileTransferEntity.getFailOnError()&& !done){
            log.error("File transfer failed");
            throw new AWSUtilException("File transfer failed");
        } else if(!done) {
            log.error("File transfer failed but mentioned fail on error as false");
        }
        if (i == runFileTransferEntity.getRetryAttempt()) {
            if(runFileTransferEntity.getFailOnError())
            throw new AWSUtilException("File transfer failed");
        }
        log.debug("Finished AWSS3Util upload");
    }


    public void download(RunFileTransferEntity runFileTransferEntity) {
        log.debug("Start AWSS3Util download");

        File filecheck = new File(runFileTransferEntity.getLocalPath());
        if(runFileTransferEntity.getFailOnError())
        if (!(filecheck.exists() && filecheck.isDirectory())&&!(runFileTransferEntity.getLocalPath().contains("hdfs://"))) {
            throw new AWSUtilException("Invalid local path");
        }
        boolean fail_if_exist=false;
        int retryAttempt = 0;
        int i;
        String amazonFileUploadLocationOriginal = null;
        String keyName=null;
        if (runFileTransferEntity.getRetryAttempt() == 0)
            retryAttempt = 1;
        else
            retryAttempt = runFileTransferEntity.getRetryAttempt();
        for (i = 0; i < retryAttempt; i++) {
            log.info("connection attempt: "+(i+1));
            try {

                AmazonS3 s3Client = null;
                ClientConfiguration clientConf = new ClientConfiguration();
                clientConf.setProtocol(Protocol.HTTPS);
                if (runFileTransferEntity.getCrediationalPropertiesFile() == null) {
                    BasicAWSCredentials creds = new BasicAWSCredentials(runFileTransferEntity.getAccessKeyID(), runFileTransferEntity.getSecretAccessKey());
                    s3Client = AmazonS3ClientBuilder.standard().withClientConfiguration(clientConf).withRegion(runFileTransferEntity.getRegion()).withCredentials(new AWSStaticCredentialsProvider(creds)).build();
                }
                else {

                    File securityFile = new File(runFileTransferEntity.getCrediationalPropertiesFile());

                    PropertiesCredentials creds = new PropertiesCredentials(securityFile);

                    s3Client = AmazonS3ClientBuilder.standard().withClientConfiguration(clientConf).withRegion(runFileTransferEntity.getRegion()).withCredentials(new AWSStaticCredentialsProvider(creds)).build();
                }
                String s3folderName=null;
                String filepath =  runFileTransferEntity.getFolder_name_in_bucket();
                if(filepath.lastIndexOf("/")!=-1) {
                    s3folderName=filepath.substring(0, filepath.lastIndexOf("/"));
                    keyName=filepath.substring( filepath.lastIndexOf("/")+1);

                }
                else{

                        keyName=filepath;

                }
                log.debug("keyName is: "+keyName);
                log.debug("bucket name is:"+runFileTransferEntity.getBucketName());
                log.debug("Folder Name is"+runFileTransferEntity.getFolder_name_in_bucket());
                if(s3folderName!=null) {
                    amazonFileUploadLocationOriginal = runFileTransferEntity.getBucketName() + "/" + s3folderName;
                }
                else{
                    amazonFileUploadLocationOriginal=runFileTransferEntity.getBucketName();
                }
                if(runFileTransferEntity.getLocalPath().contains("hdfs://")) {
                    String outputPath = runFileTransferEntity.getLocalPath();
                    String s1= outputPath.substring(7,outputPath.length());
                    String s2=s1.substring(0,s1.indexOf("/"));
                    File f = new File("/tmp");
                    if(!f.exists())
                    f.mkdir();




                    GetObjectRequest request = new GetObjectRequest(amazonFileUploadLocationOriginal,
                            keyName);
                    S3Object object = s3Client.getObject(request);
                    if(runFileTransferEntity.getEncoding()!=null)
                    object.getObjectMetadata().setContentEncoding(runFileTransferEntity.getEncoding());
                    File fexist=new File(runFileTransferEntity.getLocalPath()+File.separatorChar + keyName);
                    if(runFileTransferEntity.getOverwrite().trim().equalsIgnoreCase("Overwrite If Exists")){
                        S3ObjectInputStream objectContent = object.getObjectContent();
                        IOUtils.copyLarge(objectContent, new FileOutputStream("/tmp/" + keyName));
                    }
                    else {
                        if (!(fexist.exists() && !fexist.isDirectory())) {
                            S3ObjectInputStream objectContent = object.getObjectContent();
                            IOUtils.copyLarge(objectContent, new FileOutputStream(runFileTransferEntity.getLocalPath()+File.separatorChar + keyName));
                        }
                        else{
                            fail_if_exist=true;
                            Log.error("File already exists");
                            throw new AWSUtilException("File already exists");
                        }
                    }

                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://"+s2);
                    FileSystem hdfsFileSystem = FileSystem.get(conf);

                    String s= outputPath.substring(7,outputPath.length());
                    String hdfspath= s.substring(s.indexOf("/"),s.length());

                    Path local = new Path("/tmp/"+keyName);
                    Path hdfs = new Path(hdfspath);
                    hdfsFileSystem.copyFromLocalFile(local,hdfs);



                }
                else {

                    GetObjectRequest request = new GetObjectRequest(amazonFileUploadLocationOriginal,
                            keyName);
                    S3Object object = s3Client.getObject(request);
                    if(runFileTransferEntity.getEncoding()!=null)
                        object.getObjectMetadata().setContentEncoding(runFileTransferEntity.getEncoding());
                    File fexist=new File(runFileTransferEntity.getLocalPath()+File.separatorChar + keyName);
                    if(runFileTransferEntity.getOverwrite().trim().equalsIgnoreCase("Overwrite If Exists")){
                        S3ObjectInputStream objectContent = object.getObjectContent();
                        IOUtils.copyLarge(objectContent,new FileOutputStream(runFileTransferEntity.getLocalPath() +File.separatorChar+ keyName));
                    }

                    else {
                        if (!(fexist.exists() && !fexist.isDirectory())) {
                            S3ObjectInputStream objectContent = object.getObjectContent();
                            IOUtils.copyLarge(objectContent,new FileOutputStream(runFileTransferEntity.getLocalPath() +File.separatorChar+keyName));
                        }
                        else{
                            fail_if_exist=true;
                            Log.error("File already exists");
                            throw new AWSUtilException("File already exists");
                        }
                    }

                }
            }

            catch (AmazonServiceException e) {
                log.error("Amazon Service Exception",e);
                if (e.getStatusCode() == 403 || e.getStatusCode() == 404) {
                    if(runFileTransferEntity.getFailOnError()) {
                        Log.error("Incorrect details provided.Please provide correct details", e);
                        throw new AWSUtilException("Incorrect details provided");
                    }
                    else
                    {
                        Log.error("Unknown amezon exception occured", e);
                    }

                }

                {
                    try {
                        Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                    } catch (Exception e1) {
                       Log.error("Exception occured while sleeping the thread");
                    }
                    continue;
                }

            }
            catch (Error e){
                Log.error("Error occured while sleeping the thread");
                throw new AWSUtilException(e);
            }
            catch (Exception e) {
                log.error("error while transfering file",e);
                try {
                    Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                } catch (Exception e1) {

                }
                catch (Error err){
                    Log.error("Error occured while downloading");
                    throw new AWSUtilException(err);
                }
                continue;
            }
            done=true;
            break;
        }

        if(runFileTransferEntity.getFailOnError()&& !done){
            log.error("File transfer failed");
            throw new AWSUtilException("File transfer failed");
        } else if(!done) {
            log.error("File transfer failed but mentioned fail on error as false");
        }
        if (i == runFileTransferEntity.getRetryAttempt()) {
            if(runFileTransferEntity.getFailOnError()) {
                throw new AWSUtilException("File transfer failed");
            }
        }
        log.debug("Finished AWSS3Util download");
    }

    private static class AWSUtilException extends RuntimeException {
        public AWSUtilException() {
        }

        public AWSUtilException(String message) {
            super(message);
        }

        public AWSUtilException(String message, Throwable cause) {
            super(message, cause);
        }

        public AWSUtilException(Throwable cause) {
            super(cause);
        }

        public AWSUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}

