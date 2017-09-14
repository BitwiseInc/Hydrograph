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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commandtypes.FTP;
import hydrograph.engine.jaxb.commandtypes.FileOperationChoice;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.converter.CommandUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.FTPComponent;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The class FTPConverterUi
 * @author bitwise
 *
 */
public class FTPConverterUi extends CommandUiConverter{
	private static final Logger LOGGER = LogFactory.INSTANCE
			.getLogger(FTPConverterUi.class);
	
	public FTPConverterUi(TypeBaseComponent typeBaseComponent,
			Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new FTPComponent();
		this.propertyMap = new LinkedHashMap<>();	
	}

	@Override
	public void prepareUIXML() {
		LOGGER.debug("Fetching COMMAND-Properties for -{}", componentName);
		super.prepareUIXML();
		FTP ftp = (FTP) typeBaseComponent;
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(ftp.getId());
		
		propertyMap.put(Constants.BATCH, ftp.getBatch());
		propertyMap.put("protocol", "FTP");
		setValueInPropertyMap("host_Name",
				ftp.getHostName() == null ? "" : ftp.getHostName());

		setValueInPropertyMap("port_No",
				ftp.getPortNo() == null ? "" : ftp.getPortNo());
		
		Map<String, FTPAuthOperationDetails> authDetails = new HashMap<String, FTPAuthOperationDetails>();
		setValueInPropertyMap("user_Name", ftp.getUserName() == null ? "" : ftp.getUserName());
		setValueInPropertyMap(PropertyNameConstants.PASSWORD.value(),
				
				ftp.getPassword() == null ? "" : ftp.getPassword());
		FTPAuthOperationDetails authDetailsValue = new FTPAuthOperationDetails(ftp.getUserName(), ftp.getPassword(), "", "", "");
		if(ftp.getPassword() != null){
			authDetails.put("Basic Auth", authDetailsValue);
		}else{
			authDetails.put("User Id & Key", authDetailsValue);
		}
		
		//authentication
		propertyMap.put("authentication", authDetails);
		
		setValueInPropertyMap("timeOut",
				ftp.getTimeOut() == null ? "" : ftp.getTimeOut().intValue());
		setValueInPropertyMap("retryAttempt",
				ftp.getRetryAttempt() == null ? "" : ftp.getRetryAttempt().intValue());
		
		Map<String, FTPAuthOperationDetails> operationDetails = new HashMap<String, FTPAuthOperationDetails>();
		FTPAuthOperationDetails authOperationDetails  = new FTPAuthOperationDetails(ftp.getInputFilePath(), 
				ftp.getOutputFilePath(), "", "",ftp.getOverwritemode());
		FileOperationChoice operationChoice = ftp.getFileOperation();
		if(operationChoice.getDownload() != null){
			operationDetails.put(operationChoice.getDownload().toString(), authOperationDetails);
		}else{
			operationDetails.put(operationChoice.getUpload().toString(), authOperationDetails);
		}
		
		propertyMap.put("operation", operationDetails);
		
		propertyMap.put("encoding", ftp.getEncoding().getValue().value());
		propertyMap.put("failOnError", ftp.isFailOnError());
		
		uiComponent.setProperties(propertyMap);
		uiComponent.setType("FTP");
	}
	
	private void setValueInPropertyMap(String propertyName,Object value){
		value = getParameterValue(propertyName,value);
		propertyMap.put(propertyName, value);
	}
	
	
	@Override
	protected Map<String, String> getRuntimeProperties() {
		return null;
	}

}
