/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
import hydrograph.engine.jaxb.commontypes.ElementValueIntegerType;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.engine.util.FTPUtil;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class SFTPConvertorHelper to convert for SFTP protocol
 * @author Bitwise
 *
 */
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
		
		FTPProtocolDetails protocolDetails = (FTPProtocolDetails) properties.get(PropertyNameConstants.PROTOCOL_SELECTION.value());
		if(protocolDetails != null){
			if(protocolDetails.getHost() != null){
				sftp.setHostName(protocolDetails.getHost());
			}
			if(protocolDetails.getPort() != null){
				BigInteger portValue = FTPUtil.INSTANCE.getPortParam(PropertyNameConstants.FTP_PORT.value(), 
						componentId, properties);
				ElementValueIntegerType portParam = new ElementValueIntegerType();
				portParam.setValue(portValue);
				sftp.setPortNo(portParam);
			}
		}
		
		addAuthenticationDetails(sftp);
		
		BigInteger connectionTimeOut = FTPUtil.INSTANCE.getPortValue(PropertyNameConstants.TIME_OUT.value(), componentId, properties);
		ElementValueIntegerType timeout = new ElementValueIntegerType();
		timeout.setValue(connectionTimeOut);
		sftp.setTimeOut(timeout);
		
		BigInteger noOfRetries = FTPUtil.INSTANCE.getPortValue(PropertyNameConstants.RETRY_ATTEMPT.value(), componentId, properties);
		ElementValueIntegerType retries = new ElementValueIntegerType();
		retries.setValue(noOfRetries);
		sftp.setRetryAttempt(retries);
		
		addSFtpOperationDetails(sftp);
		
		Encoding encoding = new Encoding();
		encoding.setValue(FTPUtil.INSTANCE.getCharset(PropertyNameConstants.ENCODING.value(), component.getComponentName(), properties));
		sftp.setEncoding(encoding);
		BooleanValueType failOnError = FTPUtil.INSTANCE.getBoolean(PropertyNameConstants.FAIL_ON_ERROR.value(), 
				component.getComponentName(), properties);
		sftp.setFailOnError(failOnError.isValue());
		
	}
	
	/**
	 * authentication details
	 * @param ftp
	 */
	private void addAuthenticationDetails(SFTP sftp){
		Map<String, FTPAuthOperationDetails> authDetails = (Map<String, FTPAuthOperationDetails>) properties
				.get(PropertyNameConstants.FTP_AUTH.value());
		FTPAuthOperationDetails authenticationDetails = null;
		if(authDetails != null && !authDetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : authDetails.entrySet()){
				authenticationDetails = map.getValue();
				if(StringUtils.equalsIgnoreCase(map.getKey(), "User ID and Key")){
					if(authenticationDetails.getField2() != null && StringUtils.isNotBlank(authenticationDetails.getField2())){
						sftp.setPrivateKeyPath(authenticationDetails.getField2());
					}
				}else{
					if(authenticationDetails.getField2() != null && StringUtils.isNotBlank(authenticationDetails.getField2())){
						sftp.setPassword(authenticationDetails.getField2());
					}
				}
				sftp.setUserName(authenticationDetails.getField1());
			}
		}
	}
	
	/** 
	 *  file operation details
	 * @param ftp
	 */
	private void addSFtpOperationDetails(SFTP sftp){
		Map<String, FTPAuthOperationDetails> fileOperationDetaildetails =  (Map<String, FTPAuthOperationDetails>) properties
				.get(PropertyNameConstants.FTP_OPERATION.value());
		FileOperationChoice fileOperationChoice = new FileOperationChoice();
		FTPAuthOperationDetails authOperationDetails = null;
		if(fileOperationDetaildetails!=null && !fileOperationDetaildetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : fileOperationDetaildetails.entrySet()){
				authOperationDetails = map.getValue();
				if(StringUtils.equalsIgnoreCase(map.getKey(), Constants.GET_FILE)){
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
