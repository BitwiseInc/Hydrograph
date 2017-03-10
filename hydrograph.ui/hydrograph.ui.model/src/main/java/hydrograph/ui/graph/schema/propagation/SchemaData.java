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
package hydrograph.ui.graph.schema.propagation;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;


/**
 * Class SchemaData.
 * @author Bitwise
 */
public class SchemaData {

	
	/**
	 * @param component
	 * @return Map of input schema.
	 */
	public  Map<String, List<FixedWidthGridRow>> getInputSchema(Component component){
	    if(component!=null){
			Map<String, List<FixedWidthGridRow>>  inputSchemaMap =new TreeMap<>();
			Map<String, Schema> previousSchemaMap=(TreeMap<String, Schema>)component.getProperties()
					.get(Constants.PREVIOUS_COMPONENT_OLD_SCHEMA);
		if(previousSchemaMap!=null)
		{
			for(Link link:component.getTargetConnections()){
				Schema schema=previousSchemaMap.get(link.getTargetTerminal());
				if(schema!=null&&schema.getGridRow()!=null)
				{
					List<FixedWidthGridRow> fixedSchema=
							SchemaPropagation.INSTANCE.convertGridRowsSchemaToFixedSchemaGridRows(schema.getGridRow());
				inputSchemaMap.put(link.getTargetTerminal(),fixedSchema);
				}
				}
		}
			
		else
		{	
		
		for(Link link:component.getTargetConnections()){
			ComponentsOutputSchema componentsOutputSchema=SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
			if (componentsOutputSchema!=null){
			inputSchemaMap.put(link.getTargetTerminal(),componentsOutputSchema.getFixedWidthGridRowsOutputFields());
			}
		}
		
		}
		return inputSchemaMap;
		}
		else{
			return null;
		}
	}
}


