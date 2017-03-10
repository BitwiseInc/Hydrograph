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

package hydrograph.ui.propertywindow.widgets.customwidgets;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.InputHivePartitionColumn;
import hydrograph.ui.datastructure.property.InputHivePartitionKeyValues;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialog.hiveInput.HivePartitionKeyValueDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.widgets.Display;
/**
 * 
 * @author Bitwise
 *
 */
public class HiveInputSingleColumnWidget extends SingleColumnWidget {
	
	
	private InputHivePartitionKeyValues hivePartitionKeyValues;
	private Schema metaStoreExtractedSchema=null;

	public HiveInputSingleColumnWidget(
			ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps,
			PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
	}



	protected void intialize(ComponentConfigrationProperty componentConfigProp) {
		propertyName = componentConfigProp.getPropertyName();
		setProperties(componentConfigProp.getPropertyName(), componentConfigProp.getPropertyValue());
	}

	
	
	@Override
	protected List<String> getPropagatedSchema() {
		List<String> list = new ArrayList<String>();
		Schema schema = (Schema) getComponent().getProperties().get(
				Constants.SCHEMA_PROPERTY_NAME);
		
		if ((null==schema ||(schema!=null&&schema.getGridRow().size()==0)) && null!= metaStoreExtractedSchema) {
			schema=metaStoreExtractedSchema.clone();
		}
		
		if (schema != null && schema.getGridRow() != null) {
			List<GridRow> gridRows = schema.getGridRow();
			if (gridRows != null) {
				for (GridRow gridRow : gridRows) {
					list.add(gridRow.getFieldName());
				}
			}
		}
		return list;
	}

	@Override
	protected void onDoubleClick() {
		
		HivePartitionKeyValueDialog fieldDialog=new HivePartitionKeyValueDialog(Display.getCurrent().getActiveShell(), propertyDialogButtonBar);
		fieldDialog.setComponentName(gridConfig.getComponentName());
		if (getProperties().get(propertyName) == null) {
			
			InputHivePartitionKeyValues inputHivePartitionKeyValues = new InputHivePartitionKeyValues();
			List<String> keys= new ArrayList<>();
			List<InputHivePartitionColumn> keyValues = new ArrayList<>();
			inputHivePartitionKeyValues.setKey(keys);
			inputHivePartitionKeyValues.setKeyValues(keyValues);
			
			setProperties(propertyName, (InputHivePartitionKeyValues)inputHivePartitionKeyValues);
		}
		fieldDialog.setRuntimePropertySet(hivePartitionKeyValues);
		fieldDialog.setSourceFieldsFromPropagatedSchema(getPropagatedSchema());
		fieldDialog.open();
		setProperties(propertyName,fieldDialog.getRuntimePropertySet());
	
	}
	
	
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> property = new LinkedHashMap<>();
		property.put(propertyName, this.hivePartitionKeyValues);
		return property;
	}
	
	
	private void setProperties(String propertyName, Object properties) {
		
		this.propertyName = propertyName;
		this.hivePartitionKeyValues = (InputHivePartitionKeyValues)properties;
		
	}
	
	@Override
	public void refresh(Object value) {
		
		List<Object> tempList=(ArrayList<Object>)value;
		
		metaStoreExtractedSchema = (Schema)tempList.get(1);
		
		InputHivePartitionKeyValues inputHivePartitionKeyValues = new InputHivePartitionKeyValues();
		List<String> keys= (ArrayList<String>)tempList.get(0);
		List<InputHivePartitionColumn> keyValues = new ArrayList<>();
		inputHivePartitionKeyValues.setKey(trim(keys));
		inputHivePartitionKeyValues.setKeyValues(keyValues);
		setProperties(propertyName, (InputHivePartitionKeyValues)inputHivePartitionKeyValues);
	}



	private List<String> trim(List<String> keys) {
		List<String> temp = new ArrayList<>();
		for (String key : keys) {
			key=key.trim();
			temp.add(key);
		}
		
		return temp;
	}

}



