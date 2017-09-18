package hydrograph.ui.validators.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;

public class FTPProtocolSelectionValidator implements IValidator{
	private String errorMessage;

	@Override
	public boolean validateMap(Object object, String propertyName,
			Map<String, List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if (propertyMap != null && !propertyMap.isEmpty()) {
			return validate(propertyMap.get(propertyName), propertyName, inputSchemaMap,false);
		}
		return false;
	}

	@Override
	public boolean validate(Object object, String propertyName, Map<String, List<FixedWidthGridRow>> inputSchemaMap,
			boolean isJobFileImported) {
		FTPProtocolDetails protocolDetails = null;
		if(object instanceof FTPProtocolDetails){
			if(object != null && !object.equals("")){
				protocolDetails = (FTPProtocolDetails) object;
				if(!StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), "AWS S3 HTTPS")){
					if(protocolDetails.getHost() != null && protocolDetails.getPort() != null){
						if(StringUtils.isBlank(protocolDetails.getHost())){
							return false;
						}
						if(!validatePort(protocolDetails.getPort(), "Port")){
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	
	private boolean validatePort(String text, String propertyName){
		if(StringUtils.isNotBlank(text)){
			Matcher matcher=Pattern.compile("[\\d]*").matcher(text);
			if((matcher.matches())|| 
				((StringUtils.startsWith(text, "@{") && StringUtils.endsWith(text, "}")) &&
						!StringUtils.contains(text, "@{}"))){
				return true;
			}
			errorMessage = propertyName + " is mandatory";
		}
		errorMessage = propertyName + " is not integer value or valid parameter";
		return false;
	}

}
