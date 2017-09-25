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
package hydrograph.ui.engine.ui.converter.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commandtypes.FileOperationChoice;
import hydrograph.engine.jaxb.commandtypes.SFTP;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.converter.CommandUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.FTPComponent;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class SFTPUiConverter UI converter for SFTP component
 * @author Bitwise
 *
 */
public class SFTPUiConverter extends CommandUiConverter{
	private static final Logger LOGGER = LogFactory.INSTANCE
			.getLogger(SFTPUiConverter.class);
	
	public SFTPUiConverter(TypeBaseComponent typeBaseComponent,
			Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new FTPComponent();
		this.propertyMap = new LinkedHashMap<>();	
	}
	
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching COMMAND-Properties for -{}", componentName);
		super.prepareUIXML();
		String port="";
		SFTP sftp = (SFTP) typeBaseComponent;
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(sftp.getId());
		
		propertyMap.put(Constants.BATCH, sftp.getBatch());
		
		setValueInPropertyMap(PropertyNameConstants.FTP_HOST.value(), sftp.getHostName() == null ? "" : sftp.getHostName());
		if(sftp.getPortNo() != null){
			if(StringUtils.isNotBlank(getValue(PropertyNameConstants.FTP_PORT.value()))){
				port=getValue(PropertyNameConstants.FTP_PORT.value());
			}else if(sftp.getPortNo()!=null && sftp.getPortNo().getValue()!=null ){
				port=String.valueOf(sftp.getPortNo().getValue());
			}
		}
		
		FTPProtocolDetails ftpProtocolDetails = new FTPProtocolDetails(Constants.SFTP, sftp.getHostName(), port);
		propertyMap.put(Constants.PROTOCOL_SELECTION, ftpProtocolDetails);
		
		
		Map<String, FTPAuthOperationDetails> authDetails = new HashMap<String, FTPAuthOperationDetails>();
		setValueInPropertyMap(PropertyNameConstants.FTP_USER_NAME.value(), sftp.getUserName() == null ? "" : sftp.getUserName());
		setValueInPropertyMap(PropertyNameConstants.PASSWORD.value(),
				sftp.getPassword() == null ? "" : sftp.getPassword());
		setValueInPropertyMap("privateKeyPath",
				sftp.getPrivateKeyPath() == null ? "" : sftp.getPrivateKeyPath());
		
		FTPAuthOperationDetails authDetailsValue = new FTPAuthOperationDetails(sftp.getUserName(), sftp.getPassword(), 
				sftp.getPrivateKeyPath(), "", "", Constants.SFTP);
		if(sftp.getPassword() != null){
			authDetails.put(Constants.STAND_AUTH, authDetailsValue);
		}else{
			authDetails.put("User ID and Key", authDetailsValue);
		}
		//authentication
		propertyMap.put(PropertyNameConstants.FTP_AUTH.value(), authDetails);
		
		try {
			BigInteger timeOut = sftp.getTimeOut().getValue();
			setValueInPropertyMap(PropertyNameConstants.TIME_OUT.value(),
					sftp.getTimeOut() == null ? "" : timeOut);
			BigInteger retryAtttempt = sftp.getRetryAttempt().getValue();
			setValueInPropertyMap(PropertyNameConstants.RETRY_ATTEMPT.value(),
					sftp.getRetryAttempt() == null ? "" : retryAtttempt);
		} catch (Exception exception) {
			LOGGER.error("Failed to set the widget value" + exception);
		}
		
		Map<String, FTPAuthOperationDetails> operationDetails = new HashMap<String, FTPAuthOperationDetails>();
		FTPAuthOperationDetails authOperationDetails  = new FTPAuthOperationDetails(sftp.getInputFilePath(), 
				sftp.getOutputFilePath(), "", "",sftp.getOverwritemode(), Constants.SFTP);
		FileOperationChoice operationChoice = sftp.getFileOperation();
		if(operationChoice.getDownload() != null){
			operationDetails.put(operationChoice.getDownload().toString(), authOperationDetails);
		}else{
			operationDetails.put(operationChoice.getUpload().toString(), authOperationDetails);
		}
		
		propertyMap.put(PropertyNameConstants.FTP_OPERATION.value(), operationDetails);
		propertyMap.put(PropertyNameConstants.ENCODING.value(), sftp.getEncoding().getValue().value());
		propertyMap.put(PropertyNameConstants.FAIL_ON_ERROR.value(), sftp.isFailOnError());
		
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(Constants.SFTP);
		
	}

	@Override
	protected Map<String, String> getRuntimeProperties() {
		return null;
	}
	
	private void setValueInPropertyMap(String propertyName,Object value){
		if(StringUtils.isNotBlank(getValue(propertyName))){
			value=getValue(propertyName);
		}else{
			value = getParameterValue(propertyName,value);
		}
		propertyMap.put(propertyName, value);
	}

}
