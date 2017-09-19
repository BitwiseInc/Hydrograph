package hydrograph.engine.core.component.entity;

import hydrograph.engine.core.component.entity.base.OperationEntityBase;

/**
 * Created by damodharraop on 8/1/2017.
 */
public class RunFileTransferEntity extends OperationEntityBase {

    private String hostName;
    private String userName;
    private String password;
    private String privateKeyPath;
    private String inputFilePath;
    private String outFilePath;
    private String fileOperation;
    private int portNo;
    private String fileTransfer;
    private int timeOut;
    private int retryAfterDuration;
    private int retryAttempt;
    private String accessKeyID;
    private String secretAccessKey;
    private String crediationalPropertiesFile;
    private String localPath;
    private String bucketName;
    private String folder_name_in_bucket;
    private String region;
    private String keyName;
    private Boolean failOnError;
    private String encoding;
    private String overwrite;

    public String getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(String overwrite) {
        this.overwrite = overwrite;
    }

    public Boolean getFailOnError() {
        return failOnError;
    }

    public void setFailOnError(Boolean failOnError) {
        this.failOnError = failOnError;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKeyID() {
        return accessKeyID;
    }

    public void setAccessKeyID(String accessKeyID) {
        this.accessKeyID = accessKeyID;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public String getCrediationalPropertiesFile() {
        return crediationalPropertiesFile;
    }

    public void setCrediationalPropertiesFile(String crediationalPropertiesFile) {
        this.crediationalPropertiesFile = crediationalPropertiesFile;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFolder_name_in_bucket() {
        return folder_name_in_bucket;
    }

    public void setFolder_name_in_bucket(String folder_name_in_bucket) {
        this.folder_name_in_bucket = folder_name_in_bucket;
    }

    public String getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(String encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }

    private String encryptionMethod;

    public int getRetryAfterDuration() {
        return retryAfterDuration;
    }

    public void setRetryAfterDuration(int retryAfterDuration) {
        this.retryAfterDuration = retryAfterDuration;
    }

    public int getRetryAttempt() {
        return retryAttempt;
    }

    public void setRetryAttempt(int retryAttempt) {
        this.retryAttempt = retryAttempt;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getFileTransfer() {
        return fileTransfer;
    }

    public void setFileTransfer(String fileTransfer) {
        this.fileTransfer = fileTransfer;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }

    public String getFileOperation() {
        return fileOperation;
    }

    public void setFileOperation(String fileOperation) {
        this.fileOperation = fileOperation;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutFilePath() {
        return outFilePath;
    }

    public void setOutFilePath(String outFilePath) {
        this.outFilePath = outFilePath;
    }
}
