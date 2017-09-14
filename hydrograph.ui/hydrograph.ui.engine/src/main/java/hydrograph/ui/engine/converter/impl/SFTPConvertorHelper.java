package hydrograph.ui.engine.converter.impl;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commandtypes.FileOperationChoice;
import hydrograph.engine.jaxb.commandtypes.FileTransferBase.Encoding;
import hydrograph.engine.jaxb.commandtypes.SFTP;
import hydrograph.engine.jaxb.commontypes.BooleanValueType;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.engine.util.FTPUtil;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

public class SFTPConvertorHelper{
	public static final Logger logger = LogFactory.INSTANCE.getLogger(SFTPConvertorHelper.class);
	private Component component;
	protected Map<String, Object> properties = new LinkedHashMap<String, Object>();
	protected ConverterHelper converterHelper;

	public SFTPConvertorHelper(Component component) {
		this.component = component;
		this.properties = component.getProperties();
		converterHelper = new ConverterHelper(component);
	}
	
	public void prepareForXML(TypeBaseComponent typeBaseComponent) {
		logger.debug("Generating XML for :{}",	properties.get(Constants.PARAM_NAME));
		typeBaseComponent.setId(component.getComponentId());
		String componentId = component.getComponentId();
		componentId=component.getComponentId();
		
		SFTP sftp = (SFTP) typeBaseComponent;
		if(StringUtils.isNotBlank((String) properties.get("host_Name"))){
			sftp.setHostName(String.valueOf(properties.get("host_Name")));
		}
		
		addAuthenticationDetails(sftp);
		
		BigInteger portValue =FTPUtil.INSTANCE.getPortValue("port_No", componentId, properties);
		if(portValue != null){
			sftp.setPortNo(portValue.intValue());
		}
		
		BigInteger connectionTimeOut = FTPUtil.INSTANCE.getPortValue("timeOut", componentId, properties);
		if(connectionTimeOut != null){
			sftp.setTimeOut(connectionTimeOut.intValue());
		}
		
		BigInteger noOfRetries = FTPUtil.INSTANCE.getPortValue("retryAttempt", componentId, properties);
		if(noOfRetries != null){
			sftp.setRetryAttempt(noOfRetries.intValue());
		}
		
		addSFtpOperationDetails(sftp);
		
		Encoding encoding = new Encoding();
		encoding.setValue(FTPUtil.INSTANCE.getCharset("encoding", component.getComponentName(), properties));
		sftp.setEncoding(encoding);
		BooleanValueType failOnError = FTPUtil.INSTANCE.getBoolean("failOnError", component.getComponentName(), properties);
		sftp.setFailOnError(failOnError.isValue());
		
	}
	
	/**
	 * authentication details
	 * @param ftp
	 */
	private void addAuthenticationDetails(SFTP sftp){
		Map<String, FTPAuthOperationDetails> authDetails = (Map<String, FTPAuthOperationDetails>) properties.get("authentication");
		FTPAuthOperationDetails authenticationDetails = null;
		if(authDetails != null && !authDetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : authDetails.entrySet()){
				authenticationDetails = map.getValue();
			}
			sftp.setUserName(authenticationDetails.getField1());
			if(authenticationDetails.getField2() != null && StringUtils.isNotBlank(authenticationDetails.getField2())){
				sftp.setPassword(authenticationDetails.getField2());
			}
			if(authenticationDetails.getField3() != null && StringUtils.isNotBlank(authenticationDetails.getField3())){
				sftp.setPrivateKeyPath(authenticationDetails.getField3());
			}
		}
	}
	
	/** 
	 *  file operation details
	 * @param ftp
	 */
	private void addSFtpOperationDetails(SFTP sftp){
		Map<String, FTPAuthOperationDetails> fileOperationDetaildetails =  (Map<String, FTPAuthOperationDetails>) properties.get("operation");
		FileOperationChoice fileOperationChoice = new FileOperationChoice();
		FTPAuthOperationDetails authOperationDetails = null;
		if(fileOperationDetaildetails!=null && !fileOperationDetaildetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : fileOperationDetaildetails.entrySet()){
				authOperationDetails = map.getValue();
				if(StringUtils.equalsIgnoreCase(map.getKey(), "Get Files")){
					fileOperationChoice.setDownload(map.getKey());
					sftp.setInputFilePath(authOperationDetails.getField2());
					sftp.setOutputFilePath(authOperationDetails.getField1());
				}else{
					fileOperationChoice.setUpload(map.getKey());
					sftp.setInputFilePath(authOperationDetails.getField1());
					sftp.setOutputFilePath(authOperationDetails.getField2());
				}
			}
			sftp.setFileOperation(fileOperationChoice);
			sftp.setOverwritemode(authOperationDetails.getField5());
		}
	}

}
