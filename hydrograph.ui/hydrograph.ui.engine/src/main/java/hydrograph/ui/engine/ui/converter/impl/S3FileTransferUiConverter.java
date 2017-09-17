package hydrograph.ui.engine.ui.converter.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commandtypes.FileOperationChoice;
import hydrograph.engine.jaxb.commandtypes.S3FileTransfer;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.engine.ui.converter.CommandUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.FTPComponent;
import hydrograph.ui.logging.factory.LogFactory;

public class S3FileTransferUiConverter extends CommandUiConverter{
	private static final Logger LOGGER = LogFactory.INSTANCE
			.getLogger(S3FileTransferUiConverter.class);
	
	public S3FileTransferUiConverter(TypeBaseComponent typeBaseComponent,
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
		S3FileTransfer S3FileTransfer = (S3FileTransfer) typeBaseComponent;
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(S3FileTransfer.getId());
		
		propertyMap.put(Constants.BATCH, S3FileTransfer.getBatch());
		
		FTPProtocolDetails ftpProtocolDetails = new FTPProtocolDetails("AWS S3 HTTPS", null, null);
		propertyMap.put("protocolSelection", ftpProtocolDetails);
		
		setValueInPropertyMap("accessKeyID",S3FileTransfer.getAccessKeyID() == null ? "" : S3FileTransfer.getAccessKeyID());
		setValueInPropertyMap("secretAccessKey",S3FileTransfer.getSecretAccessKey() == null ? "" : S3FileTransfer.getSecretAccessKey());
		setValueInPropertyMap("crediationalPropertiesFile",S3FileTransfer.getCrediationalPropertiesFile() == null ? "" 
				: S3FileTransfer.getCrediationalPropertiesFile());
		
		Map<String, FTPAuthOperationDetails> authDetails = new HashMap<String, FTPAuthOperationDetails>();
		if(S3FileTransfer.getSecretAccessKey() != null || S3FileTransfer.getAccessKeyID() != null){
			FTPAuthOperationDetails authDetailsValue = new FTPAuthOperationDetails(S3FileTransfer.getAccessKeyID(), S3FileTransfer.getSecretAccessKey(), 
					S3FileTransfer.getCrediationalPropertiesFile(), "", "");
			authDetails.put("AWS S3 Access Key", authDetailsValue);
		}else{
			FTPAuthOperationDetails authDetailsValue = new FTPAuthOperationDetails("", S3FileTransfer.getCrediationalPropertiesFile(), 
					"", "", "");
			authDetails.put("AWS S3 Property File", authDetailsValue);
		}
		//authentication
		propertyMap.put("authentication", authDetails);
		
		setValueInPropertyMap("timeOut",
				S3FileTransfer.getTimeOut() == null ? "" : S3FileTransfer.getTimeOut().intValue());
		setValueInPropertyMap("retryAttempt",
				S3FileTransfer.getRetryAttempt() == null ? "" : S3FileTransfer.getRetryAttempt().intValue());
		
		setValueInPropertyMap("localPath",S3FileTransfer.getLocalPath() == null ? "" : S3FileTransfer.getLocalPath());
		setValueInPropertyMap("bucketName",S3FileTransfer.getBucketName() == null ? "" : S3FileTransfer.getBucketName());
		setValueInPropertyMap("folder_name_in_bucket",S3FileTransfer.getFolderNameInBucket() == null ? "" : S3FileTransfer.getFolderNameInBucket());
		setValueInPropertyMap("region",S3FileTransfer.getRegion() == null ? "" : S3FileTransfer.getRegion());
		
		Map<String, FTPAuthOperationDetails> operationDetails = new HashMap<String, FTPAuthOperationDetails>();
		
		FTPAuthOperationDetails authOperationDetails;
		if(!S3FileTransfer.getOverwritemode().contains("Select")){
			authOperationDetails  = new FTPAuthOperationDetails(S3FileTransfer.getLocalPath(), S3FileTransfer.getBucketName(), 
					S3FileTransfer.getFolderNameInBucket(), S3FileTransfer.getRegion(), S3FileTransfer.getOverwritemode());
		}else{
			authOperationDetails  = new FTPAuthOperationDetails(S3FileTransfer.getLocalPath(), S3FileTransfer.getBucketName(), 
					S3FileTransfer.getFolderNameInBucket(), S3FileTransfer.getRegion(), S3FileTransfer.getOverwritemode());
		}
		
		FileOperationChoice operationChoice = S3FileTransfer.getFileOperation();
		if(operationChoice.getDownload() != null){
			operationDetails.put(operationChoice.getDownload().toString(), authOperationDetails);
		}else{
			operationDetails.put(operationChoice.getUpload().toString(), authOperationDetails);
		}
		//operation
		propertyMap.put("operation", operationDetails);
		
		propertyMap.put("encoding", S3FileTransfer.getEncoding().getValue().value());
		propertyMap.put("failOnError", S3FileTransfer.isFailOnError());
		
		uiComponent.setProperties(propertyMap);
		uiComponent.setType("S3FileTransfer");
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
