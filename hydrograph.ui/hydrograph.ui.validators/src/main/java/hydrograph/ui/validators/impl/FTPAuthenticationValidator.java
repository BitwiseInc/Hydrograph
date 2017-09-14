package hydrograph.ui.validators.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;

public class FTPAuthenticationValidator implements IValidator{
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
		Map<String, FTPAuthOperationDetails> additionalParam = null;
		boolean matchS3 = false;
		if(object != null && Map.class.isAssignableFrom(object.getClass())){
				additionalParam = (Map<String, FTPAuthOperationDetails>) object;
				if (!additionalParam.isEmpty()) {
					for(Map.Entry<String, FTPAuthOperationDetails> map : additionalParam.entrySet()){
						FTPAuthOperationDetails details = map.getValue();
						if(map.getKey().contains("S3")){
							if(StringUtils.equalsIgnoreCase("AWS S3 Property File", map.getKey())){
									if(details.getField2() == null || details.getField2().isEmpty()){
										errorMessage = propertyName + " is mandatory";
										return false;
									}
							}else {
								if(details.getField1() == null || details.getField1().isEmpty()){
									errorMessage = propertyName + " is mandatory";
									return false;
								}
								if(details.getField2() == null || details.getField2().isEmpty()){
									errorMessage = propertyName + " is mandatory";
									return false;
								}
							}
						}else{
								if(details.getField1() == null || details.getField1().isEmpty()){
									errorMessage = propertyName + " is mandatory";
									return false;
								}
								if(details.getField2() == null || details.getField2().isEmpty()){
									errorMessage = propertyName + " is mandatory";
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

}
