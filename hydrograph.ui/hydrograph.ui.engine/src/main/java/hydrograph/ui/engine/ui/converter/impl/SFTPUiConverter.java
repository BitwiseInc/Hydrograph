package hydrograph.ui.engine.ui.converter.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
		String port;
		SFTP sftp = (SFTP) typeBaseComponent;
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(sftp.getId());
		
		propertyMap.put(Constants.BATCH, sftp.getBatch());
		
		setValueInPropertyMap(PropertyNameConstants.FTP_HOST.value(), sftp.getHostName() == null ? "" : sftp.getHostName());
		setValueInPropertyMap(PropertyNameConstants.FTP_PORT.value(), sftp.getPortNo() == null ? "" : sftp.getPortNo());
		
		if(sftp.getPortNo() != null){
			port = sftp.getPortNo()+"";
		}else{
			port = "";
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
				sftp.getPrivateKeyPath(), "", "");
		if(sftp.getPassword() != null){
			authDetails.put(Constants.STAND_AUTH, authDetailsValue);
		}else{
			authDetails.put("User ID and Key", authDetailsValue);
		}
		//authentication
		propertyMap.put(PropertyNameConstants.FTP_AUTH.value(), authDetails);
		
		setValueInPropertyMap(PropertyNameConstants.TIME_OUT.value(),
				sftp.getTimeOut() == null ? "" : sftp.getTimeOut().intValue());
		setValueInPropertyMap(PropertyNameConstants.RETRY_ATTEMPT.value(),
				sftp.getRetryAttempt() == null ? "" : sftp.getRetryAttempt().intValue());
		
		Map<String, FTPAuthOperationDetails> operationDetails = new HashMap<String, FTPAuthOperationDetails>();
		FTPAuthOperationDetails authOperationDetails  = new FTPAuthOperationDetails(sftp.getInputFilePath(), 
				sftp.getOutputFilePath(), "", "",sftp.getOverwritemode());
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
		value = getParameterValue(propertyName,value);
		propertyMap.put(propertyName, value);
	}

}
