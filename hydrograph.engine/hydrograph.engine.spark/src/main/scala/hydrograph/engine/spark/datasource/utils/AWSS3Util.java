package hydrograph.engine.spark.datasource.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
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
 * Created by damodharraop on 9/7/2017.
 */
public class AWSS3Util {
    static final Logger log=Logger.getLogger(AWSS3Util.class.getName());
    public static byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };

    public void upload(RunFileTransferEntity runFileTransferEntity) {
        log.debug("Start AWSS3Util upload");
        int retryAttempt = 0;
        int i;
        java.nio.file.Path inputfile = new File(runFileTransferEntity.getLocalPath()).toPath();
        String keyName=inputfile.getFileName().toString();
        String amazonFileUploadLocationOriginal=null;
        FileInputStream stream=null;
        File filecheck=new File(runFileTransferEntity.getLocalPath());
        if(runFileTransferEntity.getFailOnError())
        if(!(filecheck.isFile()||filecheck.isDirectory())&&!(runFileTransferEntity.getLocalPath().contains("hdfs://"))){
            throw new AWSUtilException("invalid localpath");
        }

        if (runFileTransferEntity.getRetryAttempt() == 0)
            retryAttempt = 1;
        else
            retryAttempt = runFileTransferEntity.getRetryAttempt();


        for (i = 0; i < retryAttempt; i++) {
            log.info("AWSS3Util Upload Connection Attempt: " + (i + 1));
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

                }
                else{
                    if(filepath.lastIndexOf(".")==-1){
                        s3folderName=filepath;
                    }
                }
                if(s3folderName!=null) {
                    amazonFileUploadLocationOriginal = runFileTransferEntity.getBucketName() + "/" + s3folderName;
                }
                else{
                    amazonFileUploadLocationOriginal=runFileTransferEntity.getBucketName();
                }


           File f=new File(runFileTransferEntity.getLocalPath());

            if(runFileTransferEntity.getLocalPath().contains("hdfs://")){
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
                                PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal + "/" + folderName, files.getName(), new FileInputStream(files), objectMetadata);
                                PutObjectResult result = s3Client.putObject(putObjectRequest);
                            }


                        }
                    }

                    catch(IOException e){
                        e.printStackTrace();
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

                    PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, stream, objectMetadata);
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
                            PutObjectRequest putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal + "/" + folderName, files.getName(), new FileInputStream(files), objectMetadata);


                            PutObjectResult result = s3Client.putObject(putObjectRequest);
                        }


                    }

                }
                else {
                    PutObjectRequest putObjectRequest=null;

                    stream = new FileInputStream(runFileTransferEntity.getLocalPath());
                         putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, keyName, stream, objectMetadata);
                    PutObjectResult result = s3Client.putObject(putObjectRequest);
                }
            }
        }

            catch (AmazonServiceException e) {
                if (e.getStatusCode() == 403 || e.getStatusCode() == 404) {
                    if(runFileTransferEntity.getFailOnError())
                    throw new AWSUtilException("Incorrect details provided");

                }

                {
                    try {
                        Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                    } catch (Exception e1) {
                        e.printStackTrace();
                    }
                    continue;
                }

            } catch (Exception e) {
                try {
                    Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                } catch (Exception e1) {

                }
                continue;
            }
            catch(Error err){
                throw new AWSUtilException(err);
            }

            break;
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
            throw new AWSUtilException("Invalid localpath");
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
                    if(filepath.lastIndexOf(".")==-1){
                        s3folderName=filepath;
                    }
                    else{
                        keyName=filepath;
                    }
                }
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
                        IOUtils.copy(objectContent, new FileOutputStream("/tmp/" + keyName));
                    }
                    else {
                        if (!(fexist.exists() && !fexist.isDirectory())) {
                            S3ObjectInputStream objectContent = object.getObjectContent();
                            IOUtils.copy(objectContent, new FileOutputStream(runFileTransferEntity.getLocalPath()+File.separatorChar + keyName));
                        }
                        else{
                            fail_if_exist=true;
                            throw new AWSUtilException("file already exist");
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
                        IOUtils.copy(objectContent, new FileOutputStream(runFileTransferEntity.getLocalPath() +File.separatorChar+ keyName));
                    }

                    else {
                        if (!(fexist.exists() && !fexist.isDirectory())) {
                            S3ObjectInputStream objectContent = object.getObjectContent();
                            IOUtils.copy(objectContent, new FileOutputStream(runFileTransferEntity.getLocalPath() +File.separatorChar+keyName));
                        }
                        else{
                            fail_if_exist=true;
                            throw new AWSUtilException("file already exist");
                        }
                    }

                }
            }

            catch (AmazonServiceException e) {
                if (e.getStatusCode() == 403 || e.getStatusCode() == 404) {
                    if(runFileTransferEntity.getFailOnError())
                    throw new AWSUtilException("Incorrect details provided");

                }

                {
                    try {
                        Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                    } catch (Exception e1) {
                        e.printStackTrace();
                    }
                    continue;
                }

            }
            catch (Error e){
                throw new AWSUtilException(e);
            }
            catch (Exception e) {
                try {
                    Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                } catch (Exception e1) {

                }
                catch (Error err){
                    throw new AWSUtilException(err);
                }
                continue;
            }

            break;
        }
        if (i == runFileTransferEntity.getRetryAttempt()) {
            if(runFileTransferEntity.getFailOnError())
          throw new AWSUtilException("File transfer failed");
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

