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

 
package hydrograph.ui.engine.converter;

import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeBaseRecord;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeInputComponent;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.common.util.PathUtility;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.exceptions.SchemaException;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
/**
 * 
 * Converter for input type component.
 *
 */
public abstract class InputConverter extends Converter {
	public InputConverter(Component comp) {
		super(comp);
	}

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(InputConverter.class);
	
	@Override
	public void prepareForXML() {
		super.prepareForXML();
		((TypeInputComponent)baseComponent).getOutSocket().addAll(getInOutSocket());
	
		
	}

	/**
	 * Returs the {@link List} of classes of type {@link TypeInputOutSocket}
	 * @return {@link List}
	 * @throws SchemaException
	 */
	protected abstract List<TypeInputOutSocket> getInOutSocket();

	
	/** Converts String value to {@link TypeBaseRecord}
	 * @return {@link TypeBaseRecord}
	 * @throws SchemaException
	 */
	protected TypeBaseRecord getSchema(){
		LOGGER.debug("Genrating TypeBaseRecord data for {}", properties.get(Constants.PARAM_NAME));
		TypeBaseRecord typeBaseRecord = new TypeBaseRecord();
		Schema schema=  (Schema) properties.get(PropertyNameConstants.SCHEMA.value());
		if(schema!=null){
		if(schema.getIsExternal()){
			TypeExternalSchema typeExternalSchema=new TypeExternalSchema();
			if(PathUtility.INSTANCE.isAbsolute(schema.getExternalSchemaPath()) 
					|| ParameterUtil.startsWithParameter(schema.getExternalSchemaPath(), Path.SEPARATOR))
				typeExternalSchema.setUri("../"+schema.getExternalSchemaPath());
			else
				typeExternalSchema.setUri("../"+schema.getExternalSchemaPath());
			typeBaseRecord.setName("External");
			typeBaseRecord.getFieldOrRecordOrIncludeExternalSchema().add(typeExternalSchema);
		}else{
			typeBaseRecord.setName("Internal");
			typeBaseRecord.getFieldOrRecordOrIncludeExternalSchema().addAll(getFieldOrRecord(schema.getGridRow()));	
		}}else
			typeBaseRecord.setName("Internal");
		return typeBaseRecord;
	}

	/**
	 * Prepare the Fields/Records for shcema
	 * @param list 
	 * @return {@link List}
	 *
	 */
	protected abstract List<TypeBaseField> getFieldOrRecord(List<GridRow> list);

	
	
}

