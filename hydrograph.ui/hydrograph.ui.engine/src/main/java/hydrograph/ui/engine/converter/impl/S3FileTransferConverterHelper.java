package hydrograph.ui.engine.converter.impl;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commandtypes.FileOperationChoice;
import hydrograph.engine.jaxb.commandtypes.S3FileTransfer;
import hydrograph.engine.jaxb.commandtypes.S3FileTransfer.Encoding;
import hydrograph.engine.jaxb.commontypes.BooleanValueType;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.engine.util.FTPUtil;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

public class S3FileTransferConverterHelper{
	public static final Logger logger = LogFactory.INSTANCE.getLogger(S3FileTransferConverterHelper.class);
	private Component component;
	protected Map<String, Object> properties = new LinkedHashMap<String, Object>();
	protected ConverterHelper converterHelper;
	
	public S3FileTransferConverterHelper(Component component) {
		this.component = component;
		this.properties = component.getProperties();
		converterHelper = new ConverterHelper(component);
	}

	
	public void prepareForXML(TypeBaseComponent typeBaseComponent) {
		logger.debug("Generating XML for :{}",	properties.get(Constants.PARAM_NAME));
		typeBaseComponent.setId(component.getComponentId());
		String componentId = component.getComponentId();
		componentId=component.getComponentId();
		
		S3FileTransfer s3FileTransfer = (S3FileTransfer) typeBaseComponent;
		
		addAuthenticationDetails(s3FileTransfer);
		
		BigInteger connectionTimeOut = FTPUtil.INSTANCE.getPortValue("timeOut", componentId, properties);
		if(connectionTimeOut != null){
			s3FileTransfer.setTimeOut(connectionTimeOut.intValue());
		}
		
		BigInteger noOfRetries = FTPUtil.INSTANCE.getPortValue("retryAttempt", componentId, properties);
		if(noOfRetries != null){
			s3FileTransfer.setRetryAttempt(noOfRetries.intValue());
		}
		
		//operation
		addSFtpOperationDetails(s3FileTransfer);
		
		Encoding encoding = new Encoding();
		encoding.setValue(FTPUtil.INSTANCE.getCharset("encoding", component.getComponentName(), properties));
		s3FileTransfer.setEncoding(encoding);
		BooleanValueType failOnError = FTPUtil.INSTANCE.getBoolean("failOnError", component.getComponentName(), properties);
		s3FileTransfer.setFailOnError(failOnError.isValue());
	}
	
	/**
	 * authentication details
	 * @param ftp
	 */
	private void addAuthenticationDetails(S3FileTransfer s3FileTransfer){
		Map<String, FTPAuthOperationDetails> authDetails = (Map<String, FTPAuthOperationDetails>) properties.get("authentication");
		FTPAuthOperationDetails authenticationDetails = null;
		if(authDetails != null && !authDetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : authDetails.entrySet()){
				authenticationDetails = map.getValue();
				if(StringUtils.equalsIgnoreCase(map.getKey(), "AWS S3 Access Key")){
					if(StringUtils.isNotBlank(authenticationDetails.getField1())){
						s3FileTransfer.setAccessKeyID(authenticationDetails.getField1());
					}
					if(StringUtils.isNotBlank(authenticationDetails.getField2())){
						s3FileTransfer.setSecretAccessKey(authenticationDetails.getField2());
					}
				}else{
					if(StringUtils.isNotBlank(authenticationDetails.getField2())){
						s3FileTransfer.setCrediationalPropertiesFile(authenticationDetails.getField2());
					}
				}
			}
		}
	}
	
	/** 
	 *  file operation details
	 * @param ftp
	 */
	private void addSFtpOperationDetails(S3FileTransfer s3FileTransfer){
		Map<String, FTPAuthOperationDetails> fileOperationDetaildetails =  (Map<String, FTPAuthOperationDetails>) properties.get("operation");
		FileOperationChoice fileOperationChoice = new FileOperationChoice();
		FTPAuthOperationDetails authOperationDetails = null;
		if(fileOperationDetaildetails!=null && !fileOperationDetaildetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : fileOperationDetaildetails.entrySet()){
				if(map.getKey().contains("Get Files")){
					fileOperationChoice.setDownload(map.getKey());
					authOperationDetails = map.getValue();
					s3FileTransfer.setOverwritemode(authOperationDetails.getField5());
				}else{
					authOperationDetails = map.getValue();
					fileOperationChoice.setUpload(map.getKey());
				}
			}
			s3FileTransfer.setFileOperation(fileOperationChoice);
			s3FileTransfer.setLocalPath(authOperationDetails.getField1());
			s3FileTransfer.setBucketName(authOperationDetails.getField2());
			s3FileTransfer.setFolderNameInBucket(authOperationDetails.getField3());
			s3FileTransfer.setRegion(authOperationDetails.getField4());
		}
	}
}
