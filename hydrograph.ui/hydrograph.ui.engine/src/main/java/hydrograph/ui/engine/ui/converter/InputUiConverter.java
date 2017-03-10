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

 
package hydrograph.ui.engine.ui.converter;

import hydrograph.engine.jaxb.commontypes.TypeInputComponent;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

/**
 * The class InputUiConverter
 * 
 * @author Bitwise
 * 
 */

public abstract class InputUiConverter extends UiConverter {
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(InputUiConverter.class);

	@Override
	/**
	 * Generate common properties of input components.
	 * 
	 * @param 
	 *            
	 * @return 
	 */
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching common properties for " + componentName);
		getOutPort((TypeInputComponent) typeBaseComponent);
		propertyMap.put(PropertyNameConstants.RUNTIME_PROPERTIES.value(), getRuntimeProperties());
	}

	/**
	 * Create output ports for input component.
	 * 
	 * @param TypeInputComponent
	 *            input-component's object generated from Jaxb classes.
	 * @return
	 */
	protected void getOutPort(TypeInputComponent inputComponent) {
		LOGGER.debug("Generating OutPut ports for " + componentName);
		if (inputComponent.getOutSocket() != null) {
			for (TypeInputOutSocket outSocket : inputComponent.getOutSocket()) {
				uiComponent.engageOutputPort(outSocket.getId());
				if (outSocket.getSchema() != null) {
					propertyMap.put(PropertyNameConstants.SCHEMA.value(), getSchema(outSocket));
				}
				
			}
		}
	}

	/**
	 * Create schema for for Input Component.
	 * @param TypeInputOutSocket
	 *            the TypeInputOutSocket i.e output port on which schema is applied, every output port has its own
	 *            schema.
	 * @return Object
	 */
	protected abstract Object getSchema(TypeInputOutSocket outSocket);
	
	/**
	 * Stores ComponentOutputSchema in component property for schema propagation. 
	 * 
	 * @param outSocketId
	 * @param gridRowList
	 */
	protected void saveComponentOutputSchema(String outSocketId, List<GridRow> gridRowList) {
		if (outSocketId != null && gridRowList != null && !gridRowList.isEmpty()) {
			ComponentsOutputSchema componentsOutputSchema = new ComponentsOutputSchema();
			for (GridRow gridRow : gridRowList) {
				componentsOutputSchema.addSchemaFields(gridRow);
			}
			Map<String, ComponentsOutputSchema> schemaMap = new LinkedHashMap<String, ComponentsOutputSchema>();
			schemaMap.put(outSocketId, componentsOutputSchema);
			propertyMap.put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);
		}
	}


}