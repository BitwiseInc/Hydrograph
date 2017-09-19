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

import org.apache.commons.lang.StringUtils;

import hydrograph.engine.jaxb.commandtypes.FTP;
import hydrograph.engine.jaxb.commandtypes.S3FileTransfer;
import hydrograph.engine.jaxb.commandtypes.SFTP;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.engine.converter.CommandConverter;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.graph.model.Component;

/**
 * The Class FTPProtocolConverter used to call helper as per protocol
 * @author Bitwise
 *
 */
public class FTPProtocolConverter extends CommandConverter{

	public FTPProtocolConverter(Component component) {
		super(component);
		this.component = component;
		this.properties = component.getProperties();
		converterHelper = new ConverterHelper(component);
	}
	
	@Override
	public void prepareForXML() {
		FTPProtocolDetails protocolDetails;
		if(properties.get(Constants.PROTOCOL_SELECTION).equals("")){
			this.baseComponent = new FTP();
			baseComponent.setBatch((String) properties.get(Constants.PARAM_BATCH));
			FTPConverterHelper converter = new FTPConverterHelper(component);
			converter.prepareForXML(baseComponent);
		}else{
			protocolDetails = (FTPProtocolDetails) properties.get(Constants.PROTOCOL_SELECTION);
			if(StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), Constants.FTP)){
				this.baseComponent = new FTP();
				baseComponent.setBatch((String) properties.get(Constants.PARAM_BATCH));
				FTPConverterHelper converter = new FTPConverterHelper(component);
				converter.prepareForXML(baseComponent);
			}else if(StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), Constants.SFTP)){
				this.baseComponent = new SFTP();
				baseComponent.setBatch((String) properties.get(Constants.PARAM_BATCH));
				SFTPConvertorHelper convertor = new SFTPConvertorHelper(component);
				convertor.prepareForXML(baseComponent);
			}else if(StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), Constants.AWS_S3)){
				this.baseComponent = new S3FileTransfer();
				baseComponent.setBatch((String) properties.get(Constants.PARAM_BATCH));
				S3FileTransferConverterHelper fileTransferConverter = new S3FileTransferConverterHelper(component);
				fileTransferConverter.prepareForXML(baseComponent);
			}
		}
	}

}
