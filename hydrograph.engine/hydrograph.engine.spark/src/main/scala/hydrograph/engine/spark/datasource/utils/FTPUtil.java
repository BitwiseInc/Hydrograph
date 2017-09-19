package hydrograph.engine.spark.datasource.utils;


/**
 * Created by damodharraop on 8/2/2017.
 */
import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class FTPUtil {
    static final Logger log=Logger.getLogger(FTPUtil.class.getName());

    public static byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };

    public void upload(RunFileTransferEntity runFileTransferEntity) {
        log.debug("Start FTPUtil upload");

        FTPClient ftpClient = new FTPClient();
        ftpClient.enterLocalPassiveMode();
        ftpClient.setBufferSize(1024000);

        int retryAttempt = runFileTransferEntity.getRetryAttempt();
        int attemptCount = 1;
        int i = 0;

        InputStream inputStream=null;
        boolean login=false;
        File filecheck=new File(runFileTransferEntity.getInputFilePath());
        if(runFileTransferEntity.getFailOnError()) {
            if (!(filecheck.isFile() || filecheck.isDirectory()) && !(runFileTransferEntity.getInputFilePath().contains("hdfs://"))) {
                throw new FTPUtilException("invalid input file path");
            }
        }

        boolean done = false;
        for (i = 0; i < retryAttempt; i++) {
            try {

                if (runFileTransferEntity.getTimeOut() != 0)
                    if(runFileTransferEntity.getEncoding()!=null)
                        ftpClient.setControlEncoding(runFileTransferEntity.getEncoding());
                ftpClient.setConnectTimeout(runFileTransferEntity.getTimeOut());
                ftpClient.connect(runFileTransferEntity.getHostName(), runFileTransferEntity.getPortNo());
                login=ftpClient.login(runFileTransferEntity.getUserName(), runFileTransferEntity.getPassword());
                if(!login){
                    throw new FTPUtilException("Invalid FTP details");
                }
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                if(runFileTransferEntity.getInputFilePath().contains("hdfs://"))
                {

                    String inputPath= runFileTransferEntity.getInputFilePath();

                    String s1= inputPath.substring(7,inputPath.length());

                    String s2=s1.substring(0,s1.indexOf("/"));

                    int index = runFileTransferEntity.getInputFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/").lastIndexOf('/');

                    String file_name = runFileTransferEntity.getInputFilePath().substring(index + 1);

                    File f= new File("/tmp");
                    if(!f.exists())
                    f.mkdir();
                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://"+s2);
                    FileSystem hdfsFileSystem = FileSystem.get(conf);
                    Path local = new Path("/tmp");
                    String s= inputPath.substring(7,inputPath.length());
                    String hdfspath= s.substring(s.indexOf("/"),s.length());
                    File dir = new File(hdfspath);
                    Random ran = new Random();
                    String tempFolder="ftp_sftp_"+ System.nanoTime() + "_" +  ran.nextInt(1000);
                    File dirs=new File("/tmp/"+tempFolder);
                    boolean success = dirs.mkdirs();
                    if(hdfsFileSystem.isDirectory(new Path(hdfspath))) {

                        InputStream is= null;
                        OutputStream os=null;
                        String localDirectory=hdfspath.substring(hdfspath.lastIndexOf("/")+1);
                        FileStatus[] fileStatus = hdfsFileSystem.listStatus(new Path(runFileTransferEntity.getInputFilePath()));
                        Path[] paths = FileUtil.stat2Paths(fileStatus);


                        try{
                           String folderName=hdfspath.substring(hdfspath.lastIndexOf("/")+1);
                            Path hdfs = new Path(hdfspath);




                            for(Path file: paths) {

                                is = hdfsFileSystem.open(file);
                                os = new BufferedOutputStream(new FileOutputStream(dirs+ "" + File.separatorChar+file.getName()));
                                IOUtils.copyBytes(is, os, conf);
                            }
                            ftpClient.changeWorkingDirectory(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/"));
                            ftpClient.removeDirectory(folderName);
                            ftpClient.makeDirectory(folderName);
                            ftpClient.changeWorkingDirectory(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/")+File.separatorChar+folderName);
                            for(File files: dirs.listFiles()){

                                if(files.isFile())
                                    ftpClient.storeFile(files.getName().toString(), new FileInputStream(files));


                            }
                            }

                        catch(IOException e){
                            e.printStackTrace();
                        }
                        finally{
                            IOUtils.closeStream(is);
                            IOUtils.closeStream(os);
                            if(dirs!=null){


                                FileUtils.deleteDirectory(dirs);
                            }

                        }

                    }
                    else{
                        try {
                            Path hdfs = new Path(hdfspath);
                            hdfsFileSystem.copyToLocalFile(false, hdfs, local);
                          inputStream = new FileInputStream(dirs + file_name);


                            ftpClient.storeFile(file_name, inputStream);
                        }
                        catch (Exception e){

                        }
                        finally {
                            FileUtils.deleteDirectory(dirs);
                        }
                    }



                }

                else {
                    java.nio.file.Path file = new File(runFileTransferEntity.getInputFilePath()).toPath();
                    if(Files.isDirectory(file)) {
                            File dir=new File(runFileTransferEntity.getInputFilePath());
                        String folderName=new File(runFileTransferEntity.getInputFilePath()).getName();


                        ftpClient.changeWorkingDirectory(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/"));
                        try {
                            ftpClient.removeDirectory(folderName);
                        }
                        catch (IOException e){

                        }
                        ftpClient.makeDirectory(folderName);

                        ftpClient.changeWorkingDirectory(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/")+"/"+folderName);
                        for(File files: dir.listFiles()){

                            if(files.isFile())
                                ftpClient.storeFile(files.getName().toString(), new FileInputStream(files));


                        }



                        }

                    else {

                         inputStream = new FileInputStream(runFileTransferEntity.getInputFilePath());
                        ftpClient.changeWorkingDirectory(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/"));
                        int index = runFileTransferEntity.getInputFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/").lastIndexOf('/');
                        String file_name = runFileTransferEntity.getInputFilePath().substring(index + 1);
                            ftpClient.storeFile(file_name, inputStream);
                    }

                }
            } catch (Exception e) {
                if(!login&&runFileTransferEntity.getFailOnError()){
                   throw new FTPUtilException("Inavlid FTP details");
                }
                try {
                    Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                } catch (Exception e1) {

                }
                continue;
            }
            finally {
                try {
                    if(inputStream!=null)
                    inputStream.close();
                }
                catch (IOException ioe){

                }
            }
            break;
        }

        if (i == runFileTransferEntity.getRetryAttempt()) {

            try {
                if (ftpClient != null) {
                    ftpClient.logout();
                    ftpClient.disconnect();

                }
            }

            catch (Exception e) {


            }
            catch (Error e){
                throw new RuntimeException(e);
            }
            if(runFileTransferEntity.getFailOnError())
                throw new RuntimeException("File transfer failed");

        }


        log.debug("Finished FTPUtil upload");
    }

    public void download(RunFileTransferEntity runFileTransferEntity) {
        log.debug("Start FTPUtil download");

        File filecheck = new File(runFileTransferEntity.getOutFilePath());
        if (!(filecheck.exists() && filecheck.isDirectory())&&!(runFileTransferEntity.getOutFilePath().contains("hdfs://"))) {
            throw new RuntimeException("invalid output path");
        }
        boolean fail_if_exist=false;
        FTPClient ftpClient = new FTPClient();
        int retryAttempt = runFileTransferEntity.getRetryAttempt();
        int attemptCount = 1;
        int i = 0;
        boolean login=false;
        boolean done = false;
        for (i = 0; i < retryAttempt; i++) {
            try {

                if (runFileTransferEntity.getTimeOut() != 0)
                    ftpClient.setConnectTimeout(runFileTransferEntity.getTimeOut());
                ftpClient.connect(runFileTransferEntity.getHostName(), runFileTransferEntity.getPortNo());

                 login=ftpClient.login(runFileTransferEntity.getUserName(), runFileTransferEntity.getPassword());

                 if(!login){
                     throw new RuntimeException("Invalid FTP details");
                 }
                ftpClient.enterLocalPassiveMode();
                if(runFileTransferEntity.getEncoding()!=null)
                    ftpClient.setControlEncoding(runFileTransferEntity.getEncoding());
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                if (runFileTransferEntity.getOutFilePath().contains("hdfs://")) {
                    String outputPath = runFileTransferEntity.getOutFilePath();
                    String s1= outputPath.substring(7,outputPath.length());
                    String s2=s1.substring(0,s1.indexOf("/"));
                    File f = new File("/tmp");
                    if(!f.exists()) {
                        f.mkdir();
                    }

                    int index = runFileTransferEntity.getInputFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/").lastIndexOf('/');
                    String file_name = runFileTransferEntity.getInputFilePath().substring(index + 1);

                    File isfile=new File(runFileTransferEntity.getOutFilePath()+"\\" + file_name);
                    if(runFileTransferEntity.getOverwrite().equalsIgnoreCase("Overwrite If Exists")) {

                        OutputStream outputStream = new FileOutputStream("/tmp/" + file_name);
                        done = ftpClient.retrieveFile(runFileTransferEntity.getInputFilePath(), outputStream);
                        outputStream.close();
                    }
                    else{
                        if (!(isfile.exists() && !isfile.isDirectory())) {
                            OutputStream outputStream = new FileOutputStream("/tmp/" + file_name);

                            done = ftpClient.retrieveFile(runFileTransferEntity.getInputFilePath(), outputStream);
                            outputStream.close();
                        }
                        else{
                            fail_if_exist=true;
                            throw new RuntimeException("File already exist");
                        }
                    }

                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS","hdfs://"+s2);
                    FileSystem hdfsFileSystem = FileSystem.get(conf);

                    String s = outputPath.substring(7, outputPath.length());
                    String hdfspath = s.substring(s.indexOf("/"), s.length());

                    Path local = new Path("/tmp/" + file_name);
                    Path hdfs = new Path(hdfspath);
                    hdfsFileSystem.copyFromLocalFile(local, hdfs);


                } else {
                    int index = runFileTransferEntity.getInputFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/").lastIndexOf('/');
                    String file_name = runFileTransferEntity.getInputFilePath().substring(index + 1);

                    File isfile=new File(runFileTransferEntity.getOutFilePath() +File.separatorChar+ file_name);
                    if(runFileTransferEntity.getOverwrite().equalsIgnoreCase("Overwrite If Exists")) {

                        OutputStream outputStream = new FileOutputStream(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/") + "/" + file_name);
                        done = ftpClient.retrieveFile(runFileTransferEntity.getInputFilePath(), outputStream);
                        outputStream.close();
                    }
                    else{


                            if (!(isfile.exists() && !isfile.isDirectory())) {

                                OutputStream outputStream = new FileOutputStream(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/") +File.separatorChar+ file_name);

                                done = ftpClient.retrieveFile(runFileTransferEntity.getInputFilePath(), outputStream);
                                outputStream.close();
                            } else {
                                fail_if_exist=true;
                                throw new FTPUtilException("File already exist");

                            }


                    }
                }
            }

            catch (Exception e) {
                if(!login){
                    throw new FTPUtilException("Inavlid FTP details");
                }
                if (fail_if_exist)
                    throw new FTPUtilException("invalid input file path");
                try {
                    Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                }
                catch (Exception e1) {

                }
                catch (Error err){
                    throw new FTPUtilException(err);
                }
                continue;
            }
            break;

        }

        if (i == runFileTransferEntity.getRetryAttempt()) {

            try {
                if (ftpClient != null) {
                    ftpClient.logout();
                    ftpClient.disconnect();

                }
            } catch (Exception e) {


            }
            if(runFileTransferEntity.getFailOnError())
                throw new FTPUtilException("File transfer failed ");

        }

        log.debug("Finished FTPUtil download");

    }

    private static class FTPUtilException extends RuntimeException {
        public FTPUtilException() {
        }

        public FTPUtilException(String message) {
            super(message);
        }

        public FTPUtilException(String message, Throwable cause) {
            super(message, cause);
        }

        public FTPUtilException(Throwable cause) {
            super(cause);
        }

        public FTPUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}