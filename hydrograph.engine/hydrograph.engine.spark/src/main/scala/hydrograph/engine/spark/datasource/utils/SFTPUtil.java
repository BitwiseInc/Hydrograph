package hydrograph.engine.spark.datasource.utils;

import com.jcraft.jsch.*;
import com.jcraft.jsch.Logger;
import hydrograph.engine.core.component.entity.RunFileTransferEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.log4j.*;


/**
 * Created by damodharraop on 8/1/2017.
 */
public class SFTPUtil {
    static final org.apache.log4j.Logger log= org.apache.log4j.Logger.getLogger(SFTPUtil.class.getName());
    public static byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };
    public void upload(RunFileTransferEntity runFileTransferEntity) {
        log.debug("Start SFTPUtil upload");
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        ChannelSftp sftpChannel = null;
        ZipInputStream  zip=null;
        FileInputStream fin=null;
        int retryAttempt=0;
        int i;
        File filecheck=new File(runFileTransferEntity.getInputFilePath());
        if(runFileTransferEntity.getFailOnError())
        if(!(filecheck.isFile()||filecheck.isDirectory())&&!(runFileTransferEntity.getInputFilePath().contains("hdfs://"))){
         throw new SFTPUtilException("Invalid input filepath");
        }

        if(runFileTransferEntity.getRetryAttempt()==0)
            retryAttempt=1;
        else
            retryAttempt=runFileTransferEntity.getRetryAttempt();

        for (i = 0; i < retryAttempt; i++) {

            try {

                if (runFileTransferEntity.getPrivateKeyPath() != null) {
                    jsch.addIdentity(runFileTransferEntity.getPrivateKeyPath());
                }
                session = jsch.getSession(runFileTransferEntity.getUserName(), runFileTransferEntity.getHostName(), runFileTransferEntity.getPortNo());
                session.setConfig("PreferredAuthentications",
                        "publickey,keyboard-interactive,password");
                session.setConfig("StrictHostKeyChecking", "no");
                if (runFileTransferEntity.getPassword() != null) {
                    session.setPassword(runFileTransferEntity.getPassword());
                }
                if (runFileTransferEntity.getTimeOut() > 0) {
                    session.setTimeout(runFileTransferEntity.getTimeOut());
                }

                session.connect();
                channel = session.openChannel("sftp");
                channel.connect();
                sftpChannel = (ChannelSftp) channel;
                sftpChannel.setFilenameEncoding(runFileTransferEntity.getEncoding());
                sftpChannel.cd(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/"));

                if(runFileTransferEntity.getInputFilePath().contains("hdfs://")){
                    String inputPath= runFileTransferEntity.getInputFilePath();
                    File f= new File("/tmp");
                    if(!f.exists())
                    f.mkdir();
                    String s1= inputPath.substring(7,inputPath.length());
                    String s2=s1.substring(0,s1.indexOf("/"));
                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://"+s2);
                    FileSystem hdfsFileSystem = FileSystem.get(conf);
                    Path local = new Path("/tmp");
                    String s= inputPath.substring(7,inputPath.length());
                    String hdfspath= s.substring(s.indexOf("/"),s.length());

                    File dir = new File(hdfspath);
                    if(hdfsFileSystem.isDirectory(new Path(hdfspath))) {

                        InputStream is= null;
                        OutputStream os=null;
                        String localDirectory=hdfspath.substring(hdfspath.lastIndexOf("/")+1);
                        FileStatus[] fileStatus = hdfsFileSystem.listStatus(new Path(runFileTransferEntity.getInputFilePath()));
                        Path[] paths = FileUtil.stat2Paths(fileStatus);
                        File dirs=null;

                        try{
                            String folderName=hdfspath.substring(hdfspath.lastIndexOf("/")+1);

                            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                            String dateWithoutTime = df.format(new Date()).toString();
                            java.util.Random ran=new Random();
                            String tempFolder="ftp_sftp_"+ System.nanoTime() + "_" +  ran.nextInt(1000);
                            dirs=new File("/tmp/"+tempFolder);
                            boolean success = dirs.mkdirs();
                            for(Path file: paths) {

                                is = hdfsFileSystem.open(file);
                                os = new BufferedOutputStream(new FileOutputStream(dirs+""+File.separatorChar+file.getName()));
                                IOUtils.copyBytes(is, os, conf);
                            }
                            try {

                                sftpChannel.cd( folderName );
                            }
                            catch ( SftpException e ) {
                                sftpChannel.mkdir( folderName );
                                sftpChannel.cd( folderName );
                            }
                            for(File files: dirs.listFiles()){

                                if(files.isFile())
                                    if(files.isFile()) {

                                        sftpChannel.put(new FileInputStream(files), files.getName());

                                    }


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
                        Path hdfs = new Path(hdfspath);
                        hdfsFileSystem.copyToLocalFile(false, hdfs, local);
                        int index = inputPath.replaceAll(Matcher.quoteReplacement("\\"), "/").lastIndexOf('/');
                        String file_name = runFileTransferEntity.getInputFilePath().substring(index + 1);
                         fin = new FileInputStream("/tmp/" + file_name);
                        sftpChannel.cd(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/"));
                        sftpChannel.put(fin, file_name);
                        i = i + 1;
                        fin.close();
                    }

                }
                else {
                    java.nio.file.Path file = new File(runFileTransferEntity.getInputFilePath()).toPath();
                    if(Files.isDirectory(file)) {

                        File f=new File(file.toAbsolutePath().toString());
                        String folderName = new File(runFileTransferEntity.getInputFilePath()).getName();
                       sftpChannel.cd(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/"));
                        try {



                            sftpChannel.cd( folderName );
                        }
                        catch ( SftpException e ) {
                            sftpChannel.cd(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/"));
                            sftpChannel.mkdir( folderName );
                            sftpChannel.cd( folderName );
                        }

                        for(File files: f.listFiles()){

                            if(files.isFile())
                            sftpChannel.put( new FileInputStream(files), files.getName());


                        }




                    }
                    else{
                        int index = runFileTransferEntity.getInputFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/").lastIndexOf('/');
                        String file_name = runFileTransferEntity.getInputFilePath().substring(index + 1);
                         fin = new FileInputStream(runFileTransferEntity.getInputFilePath());
                        sftpChannel.cd(runFileTransferEntity.getOutFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/"));
                            sftpChannel.put(fin, file_name);
                            fin.close();
                    }
                }
            }
            catch (JSchException e) {
                if (e.getMessage().compareTo("Auth fail") == 0) {
                    if(runFileTransferEntity.getFailOnError())
                        throw new SFTPUtilException(e.getMessage());
                }
                else
                {
                    try {
                        Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                    } catch (Exception e1) {

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
            finally {
                try{
                    if(zip!=null)
                   zip.close();
                    if(fin!=null)
                        fin.close();
                }
                catch(IOException ioe){

                }
            }

            break;

        }


        if (i == runFileTransferEntity.getRetryAttempt()) {

            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
            if(runFileTransferEntity.getFailOnError())
                throw new SFTPUtilException("File transfer failed");
        }


        if (sftpChannel != null) {
            sftpChannel.disconnect();
        }
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }

        log.debug("Fininished SFTPUtil upload");
    }

    public void download(RunFileTransferEntity runFileTransferEntity)  {
        log.debug("Start SFTPUtil download");

        File filecheck = new File(runFileTransferEntity.getOutFilePath());
        if(runFileTransferEntity.getFailOnError())
        if (!(filecheck.exists() && filecheck.isDirectory())&&!(runFileTransferEntity.getOutFilePath().contains("hdfs://"))) {
            throw new SFTPUtilException("invalid outputpath");
        }
        boolean fail_if_exist=false;
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        ChannelSftp sftpChannel = null;
        int retryAttempt=0;
        int i;
        if(runFileTransferEntity.getRetryAttempt()==0)
            retryAttempt=1;
        else
            retryAttempt=runFileTransferEntity.getRetryAttempt();
        for (i = 0; i < retryAttempt; i++) {

            try {
                if (runFileTransferEntity.getPrivateKeyPath() != null) {
                    jsch.addIdentity(runFileTransferEntity.getPrivateKeyPath());
                }
                session = jsch.getSession(runFileTransferEntity.getUserName(), runFileTransferEntity.getHostName(), runFileTransferEntity.getPortNo());
                session.setConfig("PreferredAuthentications",
                        "publickey,keyboard-interactive,password");
                session.setConfig("StrictHostKeyChecking", "no");
                if (runFileTransferEntity.getPassword() != null) {
                    session.setPassword(runFileTransferEntity.getPassword());
                }
                if (runFileTransferEntity.getTimeOut() > 0) {
                    session.setTimeout(runFileTransferEntity.getTimeOut());
                }

                session.connect();
                channel = session.openChannel("sftp");
                channel.connect();
                sftpChannel = (ChannelSftp) channel;
                sftpChannel.setFilenameEncoding(runFileTransferEntity.getEncoding());
                if(runFileTransferEntity.getOutFilePath().contains("hdfs://"))
                {
                    String outputPath= runFileTransferEntity.getOutFilePath();
                    String s1= outputPath.substring(7,outputPath.length());
                    String s2=s1.substring(0,s1.indexOf("/"));
                    File f= new File("/tmp");
                    if(!f.exists())
                    f.mkdir();

                    int index = runFileTransferEntity.getInputFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/").lastIndexOf('/');
                    String file_name = runFileTransferEntity.getInputFilePath().substring(index + 1);
                    String file_loc = runFileTransferEntity.getInputFilePath().substring(0, index);

                    sftpChannel.cd(file_loc.replaceAll(Matcher.quoteReplacement("\\"), "/"));

                    File isfile=new File(runFileTransferEntity.getOutFilePath()+File.separatorChar + file_name);
                    if(runFileTransferEntity.getOverwrite().equalsIgnoreCase("Overwrite If Exists")){
                        FileOutputStream  fout = new FileOutputStream("/tmp/"+file_name);
                        sftpChannel.get(file_name, fout);
                        fout.close();

                    }
                    else {
                        if ((isfile.exists() && !isfile.isDirectory())) {
                            FileOutputStream  fout = new FileOutputStream("/tmp/"+file_name);
                            sftpChannel.get(file_name, fout);
                            fout.close();
                        }
                        else{
                            fail_if_exist=true;
                            throw new SFTPUtilException("file already exist");
                        }


                    }


                }
                else {
                    int index = runFileTransferEntity.getInputFilePath().replaceAll(Matcher.quoteReplacement("\\"), "/").lastIndexOf('/');
                    String file_name = runFileTransferEntity.getInputFilePath().substring(index + 1);
                    String file_loc = runFileTransferEntity.getInputFilePath().substring(0, index);
                    sftpChannel.cd(file_loc.replaceAll(Matcher.quoteReplacement("\\"), "/"));
                    sftpChannel.setFilenameEncoding(runFileTransferEntity.getEncoding());

                    File isfile=new File(runFileTransferEntity.getOutFilePath() +File.separatorChar+ file_name);
                    if(runFileTransferEntity.getOverwrite().equalsIgnoreCase("Overwrite If Exists")){
                        FileOutputStream fout = new FileOutputStream(runFileTransferEntity.getOutFilePath()+ File.separatorChar+ file_name);
                        sftpChannel.get(file_name, fout);
                        fout.close();

                    }
                    else {
                        if (!(isfile.exists() && !isfile.isDirectory())) {
                            FileOutputStream fout = new FileOutputStream(runFileTransferEntity.getOutFilePath() +File.separatorChar+ file_name);
                            sftpChannel.get(file_name, fout);
                            fout.close();
                        }
                        else{
                            fail_if_exist=true;
                            throw new SFTPUtilException("File Already exist");
                        }


                    }


                }
            }
            catch (JSchException e) {
                if (e.getMessage().compareTo("Auth fail") == 0) {
                    if(runFileTransferEntity.getFailOnError())
                        throw new SFTPUtilException(e.getMessage());

                }

                {
                    try {
                        Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                    } catch (Exception e1) {

                    }
                    continue;
                }

            } catch (Exception e) {
                if (fail_if_exist){
                    throw new SFTPUtilException("File already exist");
                }
                try {
                    Thread.sleep(runFileTransferEntity.getRetryAfterDuration());
                } catch (Exception e1) {

                }
                continue;
            }
            catch (Error e){
                throw new SFTPUtilException(e);
            }

            break;

        }


        if (i == runFileTransferEntity.getRetryAttempt()) {

            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
            if(runFileTransferEntity.getFailOnError())
                throw new SFTPUtilException("File transfer failed");
        }


        if (sftpChannel != null) {
            sftpChannel.disconnect();
        }
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }

        log.debug("Fininished SFTPUtil download");
    }


    private static class SFTPUtilException extends RuntimeException {
        public SFTPUtilException() {
        }

        public SFTPUtilException(String message) {
            super(message);
        }

        public SFTPUtilException(String message, Throwable cause) {
            super(message, cause);
        }

        public SFTPUtilException(Throwable cause) {
            super(cause);
        }

        public SFTPUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
