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

public class FTPProtocolConverter extends CommandConverter{

	public FTPProtocolConverter(Component component) {
		super(component);
		this.component = component;
		this.properties = component.getProperties();
		converterHelper = new ConverterHelper(component);
	}
	
	@Override
	public void prepareForXML() {
		//super.prepareForXML();
		FTPProtocolDetails protocolDetails = (FTPProtocolDetails) properties.get("protocolSelection");
		if(protocolDetails == null){
			return;
		}
		if(StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), "FTP")){
			this.baseComponent = new FTP();
			baseComponent.setBatch((String) properties.get(Constants.PARAM_BATCH));
			FTPConverterHelper converter = new FTPConverterHelper(component);
			converter.prepareForXML(baseComponent);
		}else if(StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), "SFTP")){
			this.baseComponent = new SFTP();
			baseComponent.setBatch((String) properties.get(Constants.PARAM_BATCH));
			SFTPConvertorHelper convertor = new SFTPConvertorHelper(component);
			convertor.prepareForXML(baseComponent);
		}else if(StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), "AWS S3 HTTPS")){
			this.baseComponent = new S3FileTransfer();
			baseComponent.setBatch((String) properties.get(Constants.PARAM_BATCH));
			S3FileTransferConverterHelper fileTransferConverter = new S3FileTransferConverterHelper(component);
			fileTransferConverter.prepareForXML(baseComponent);
		}
	}

}
