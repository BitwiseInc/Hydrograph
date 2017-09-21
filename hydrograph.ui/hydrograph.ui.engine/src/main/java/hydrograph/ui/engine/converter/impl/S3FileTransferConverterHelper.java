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
import hydrograph.engine.jaxb.commandtypes.S3FileTransfer;
import hydrograph.engine.jaxb.commandtypes.S3FileTransfer.Encoding;
import hydrograph.engine.jaxb.commontypes.BooleanValueType;
import hydrograph.engine.jaxb.commontypes.ElementValueIntegerType;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.engine.util.FTPUtil;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class S3FileTransferConverterHelper to convert for S3 protocol
 * @author Bitwise
 *
 */
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
		
		BigInteger connectionTimeOut = FTPUtil.INSTANCE.getPortValue(PropertyNameConstants.TIME_OUT.value(), componentId, properties);
		ElementValueIntegerType timeout = new ElementValueIntegerType();
		timeout.setValue(connectionTimeOut);
		s3FileTransfer.setTimeOut(timeout);
		
		BigInteger noOfRetries = FTPUtil.INSTANCE.getPortValue(PropertyNameConstants.RETRY_ATTEMPT.value(), componentId, properties);
		ElementValueIntegerType retries = new ElementValueIntegerType();
		retries.setValue(noOfRetries);
		s3FileTransfer.setRetryAttempt(retries);
		
		//operation
		addSFtpOperationDetails(s3FileTransfer);
		
		Encoding encoding = new Encoding();
		encoding.setValue(FTPUtil.INSTANCE.getCharset(PropertyNameConstants.ENCODING.value(), component.getComponentName(), properties));
		s3FileTransfer.setEncoding(encoding);
		BooleanValueType failOnError = FTPUtil.INSTANCE.getBoolean(PropertyNameConstants.FAIL_ON_ERROR.value(), 
				component.getComponentName(), properties);
		s3FileTransfer.setFailOnError(failOnError.isValue());
	}
	
	/**
	 * authentication details
	 * @param ftp
	 */
	private void addAuthenticationDetails(S3FileTransfer s3FileTransfer){
		Map<String, FTPAuthOperationDetails> authDetails = (Map<String, FTPAuthOperationDetails>) properties
				.get(PropertyNameConstants.FTP_AUTH.value());
		FTPAuthOperationDetails authenticationDetails = null;
		if(authDetails != null && !authDetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : authDetails.entrySet()){
				authenticationDetails = map.getValue();
				if(StringUtils.equalsIgnoreCase(map.getKey(), Constants.AWS_S3_KEY)){
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
		Map<String, FTPAuthOperationDetails> fileOperationDetaildetails =  (Map<String, FTPAuthOperationDetails>) properties
				.get(PropertyNameConstants.FTP_OPERATION.value());
		FileOperationChoice fileOperationChoice = new FileOperationChoice();
		FTPAuthOperationDetails authOperationDetails = null;
		if(fileOperationDetaildetails!=null && !fileOperationDetaildetails.isEmpty()){
			for(Map.Entry<String, FTPAuthOperationDetails> map : fileOperationDetaildetails.entrySet()){
				if(map.getKey().contains(Constants.GET_FILE)){
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
