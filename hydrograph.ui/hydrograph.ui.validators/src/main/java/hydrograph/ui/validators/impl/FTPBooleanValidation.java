package hydrograph.ui.validators.impl;

import java.util.List;
import java.util.Map;

import hydrograph.ui.datastructure.property.FixedWidthGridRow;

public class FTPBooleanValidation implements IValidator{
	private String errorMessage;

	@Override
	public boolean validateMap(Object object, String propertyName,
			Map<String, List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if(propertyMap != null && !propertyMap.isEmpty()){ 
			return validate(propertyMap.get(propertyName), propertyName,inputSchemaMap,false);
		}
		return false;
	}

	@Override
	public boolean validate(Object object, String propertyName, Map<String, List<FixedWidthGridRow>> inputSchemaMap,
			boolean isJobFileImported) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

}
