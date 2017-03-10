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

package hydrograph.ui.propertywindow.widgets.utility;



import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

/**
 * The Class GridComparator.
 * 
 * @author Bitwise
 */

public class GridComparator implements Comparator<GridRow> {

	@Override
	public int compare(GridRow o1, GridRow o2) {
		   	int flag = o1.getFieldName().compareTo(o2.getFieldName());
	        if(o1.getDataType() !=null && flag==0) flag = o1.getDataType().compareTo(o2.getDataType());
	        if(o1.getScaleType()  !=null && flag==0) flag = o1.getScaleType().compareTo(o2.getScaleType());
	        if(o1.getDataTypeValue()!=null  && flag==0) flag = o1.getDataTypeValue().compareTo(o2.getDataTypeValue());
	        if(o1.getDateFormat() != null && flag==0) flag = o1.getDateFormat().compareTo(o2.getDateFormat());
	        if(o1.getDescription() != null && flag==0) flag = o1.getDescription().compareTo(o2.getDescription());
	        if(o1.getPrecision() !=null && flag==0) flag = o1.getPrecision().compareTo(o2.getPrecision());
	        if(o1.getScale() !=null && flag==0) flag = o1.getScale().compareTo(o2.getScale());
	        if(o1.getScaleTypeValue() !=null && flag==0) flag = o1.getScaleTypeValue().compareTo(o2.getScaleTypeValue());
	        	
	        if(MixedSchemeGridRow.class.isAssignableFrom(o1.getClass()) && MixedSchemeGridRow.class.isAssignableFrom(o2.getClass())){
	        	if(flag==0){ 
	        		if(StringUtils.isNotBlank(((MixedSchemeGridRow) o1).getLength()) && StringUtils.isNotBlank(((MixedSchemeGridRow) o2).getLength()))
	        			flag = ((MixedSchemeGridRow) o1).getLength().compareTo(((MixedSchemeGridRow) o2).getLength());
	        	}
	        	if(flag==0) {
	        		if(StringUtils.isNotBlank(((MixedSchemeGridRow) o1).getDelimiter()) && StringUtils.isNotBlank(((MixedSchemeGridRow) o2).getDelimiter()))
	        			flag = ((MixedSchemeGridRow) o1).getDelimiter().compareTo(((MixedSchemeGridRow) o2).getDelimiter());
	        	}
	        	return flag;
	        }
	        
	        if(FixedWidthGridRow.class.isAssignableFrom(o1.getClass()) && FixedWidthGridRow.class.isAssignableFrom(o2.getClass())){
	        	if(StringUtils.isNotBlank(((FixedWidthGridRow) o1).getLength()))
	        			if(flag==0) flag = ((FixedWidthGridRow) o1).getLength().compareTo(((FixedWidthGridRow) o2).getLength());
	        	return flag;
	        }
	        if(GenerateRecordSchemaGridRow.class.isAssignableFrom(o1.getClass()) && GenerateRecordSchemaGridRow.class.isAssignableFrom(o2.getClass())){
	        	if(flag==0) flag = ((GenerateRecordSchemaGridRow) o1).getLength().compareTo(((GenerateRecordSchemaGridRow) o2).getLength());
	        	if(flag==0) flag = ((GenerateRecordSchemaGridRow) o1).getRangeFrom().compareTo(((GenerateRecordSchemaGridRow) o2).getRangeFrom());
	        	if(flag==0) flag = ((GenerateRecordSchemaGridRow) o1).getRangeTo().compareTo(((GenerateRecordSchemaGridRow) o2).getRangeTo());
	        	if(flag==0) flag = ((GenerateRecordSchemaGridRow) o1).getDefaultValue().compareTo(((GenerateRecordSchemaGridRow) o2).getDefaultValue());
	        }
	        
	        return flag;
	}

}
