package hydrograph.ui.engine.converter.impl;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commandtypes.FTP;
import hydrograph.engine.jaxb.commandtypes.FileOperationChoice;
import hydrograph.engine.jaxb.commandtypes.FileTransferBase.Encoding;
import hydrograph.engine.jaxb.commontypes.BooleanValueType;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.engine.util.FTPUtil;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

public class FTPConverterHelper{
	public static final Logger logger = LogFactory.INSTANCE.getLogger(FTPConverterHelper.class);
	private Component component;
	protected Map<String, Object> properties = new LinkedHashMap<String, Object>();
	protected ConverterHelper converterHelper;

	public FTPConverterHelper(Component component) {
		this.component = component;
		this.properties = component.getProperties();
		converterHelper = new ConverterHelper(component);
	}

	public void prepareForXML(TypeBaseComponent typeBaseComponent) {
		logger.debug("Generating XML for :{}",	properties.get(Constants.PARAM_NAME));
		typeBaseComponent.setId(component.getComponentId());
		FTP ftp = (FTP) typeBaseComponent;
		String componentId = component.getComponentId();
		componentId=component.getComponentId();
		
		
		FTPProtocolDetails protocolDetails = (FTPProtocolDetails) properties.get("protocolSelection");
		if(protocolDetails != null){
			if(protocolDetails.getHost() != null){
				ftp.setHostName(protocolDetails.getHost());
			}
			if(protocolDetails.getPort() != null){
				int port = NumberUtils.toInt(protocolDetails.getPort(), 21);
				ftp.setPortNo(port);
			}
		}
		
		//authenticate
		addAuthenticationDetails(ftp);
		
		
		BigInteger connectionTimeOut = FTPUtil.INSTANCE.getPortValue("timeOut", componentId, properties);
		if(connectionTimeOut != null){
			ftp.setTimeOut(connectionTimeOut.intValue());
		}
		
		BigInteger noOfRetries = FTPUtil.INSTANCE.getPortValue("retryAttempt", componentId, properties);
		if(noOfRetries != null){
			ftp.setRetryAttempt(noOfRetries.intValue());
		}
		//operations
		addFtpOperationDetails(ftp);
		
		Encoding encoding = new Encoding();
		encoding.setValue(FTPUtil.INSTANCE.getCharset("encoding", component.getComponentName(), properties));
		ftp.setEncoding(encoding);
		BooleanValueType failOnError = FTPUtil.INSTANCE.getBoolean("failOnError", component.getComponentName(), properties);
		ftp.setFailOnError(failOnError.isValue());
	}
	
	
	
	/**
	 * authentication details
	 * @param ftp
	 */
	private void addAuthenticationDetails(FTP ftp){
		Map<String, FTPAuthOperationDetails> authDetails = (Map<String, FTPAuthOperationDetails>) properties.get("authentication");
		FTPAuthOperationDetails authenticationDetails = null;
		if(authDetails != null && !authDetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : authDetails.entrySet()){
				authenticationDetails = map.getValue();
			}
			ftp.setUserName(authenticationDetails.getField1());
			ftp.setPassword(authenticationDetails.getField2());
		}
	}
	
	/** 
	 *  file operation details
	 * @param ftp
	 */
	private void addFtpOperationDetails(FTP ftp){
		Map<String, FTPAuthOperationDetails> fileOperationDetaildetails =  (Map<String, FTPAuthOperationDetails>) properties.get("operation");
		FileOperationChoice fileOperationChoice = new FileOperationChoice();
		FTPAuthOperationDetails authOperationDetails = null;
		if(fileOperationDetaildetails!=null && !fileOperationDetaildetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : fileOperationDetaildetails.entrySet()){
				authOperationDetails = map.getValue();
				if(StringUtils.equalsIgnoreCase(map.getKey(), "Get Files")){
					fileOperationChoice.setDownload(map.getKey());
					ftp.setInputFilePath(authOperationDetails.getField2());
					ftp.setOutputFilePath(authOperationDetails.getField1());
				}else{
					fileOperationChoice.setUpload(map.getKey());
					ftp.setInputFilePath(authOperationDetails.getField1());
					ftp.setOutputFilePath(authOperationDetails.getField2());
				}
			}
			ftp.setFileOperation(fileOperationChoice);
			ftp.setOverwritemode(authOperationDetails.getField5());
		}
	}
	
	
	
}
