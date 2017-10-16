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

import hydrograph.engine.jaxb.commontypes.TrueFalse;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeBaseRecord;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeOutputComponent;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.commontypes.TypeTrueFalse;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.common.util.PathUtility;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.impl.OutputFileDelimitedConverter;
import hydrograph.ui.engine.exceptions.SchemaException;
import hydrograph.ui.engine.xpath.ComponentXpath;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.engine.xpath.ComponentsAttributeAndValue;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
/**
 * 
 * Converter for output type component.
 *
 */
public abstract class OutputConverter extends Converter {

	public OutputConverter(Component comp) {
		super(comp);
	}
	
	/** The Constant LOGGER. */
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OutputFileDelimitedConverter.class);

	/*
	 * prepare xml and adding In Socket. 
	 */
	@Override
	public void prepareForXML() {
		super.prepareForXML();
		((TypeOutputComponent) baseComponent).getInSocket().addAll(getOutInSocket());
	}

	/**
	 * Returs the {@link List} of classes of type {@link TypeOutputInSocket}
	 * 
	 * @return {@link TypeOutputInSocket}
	 * @throws SchemaException
	 */
	protected abstract List<TypeOutputInSocket> getOutInSocket();

	/**
	 * Converts String value to {@link TypeBaseRecord}
	 * 
	 * @return {@link TypeBaseRecord}
	 * @throws SchemaException
	 */
	protected TypeBaseRecord getSchema() {
		logger.debug("Genrating TypeBaseRecord data for {}", properties.get(Constants.PARAM_NAME));
		TypeBaseRecord typeBaseRecord = new TypeBaseRecord();
		Schema schema = (Schema) properties.get(PropertyNameConstants.SCHEMA.value());
		if (schema != null) {
			if ( schema.getIsExternal()) {
				TypeExternalSchema typeExternalSchema = new TypeExternalSchema();
				if(PathUtility.INSTANCE.isAbsolute(schema.getExternalSchemaPath())
						|| ParameterUtil.startsWithParameter(schema.getExternalSchemaPath(), Path.SEPARATOR)){
					typeExternalSchema.setUri("../" + schema.getExternalSchemaPath());
				}
				else{
					typeExternalSchema.setUri("../"+schema.getExternalSchemaPath());
				}
				typeBaseRecord.setName(Constants.EXTERNAL_SCHEMA);
				typeBaseRecord.getFieldOrRecordOrIncludeExternalSchema().add(typeExternalSchema);
			}
			else{
				typeBaseRecord.setName(Constants.INTERNAL_SCHEMA);
				typeBaseRecord.getFieldOrRecordOrIncludeExternalSchema().addAll(getFieldOrRecord(schema.getGridRow()));
			}
		}
		return typeBaseRecord;
	}
	
	/**
	 * Caches the parameter value for later substitution in engine xml.
	 * @param propertyName
	 * @return
	 */
	protected TypeTrueFalse getTrueFalse(String propertyName) {
		logger.debug("Getting TrueFalse Value for {}={}", new Object[] {propertyName, properties.get(propertyName) });
		TypeTrueFalse typeTrueFalse = new TypeTrueFalse();
		if (StringUtils.isNotBlank((String) properties.get(propertyName))) {
			try{
				Object object = properties.get(propertyName);
				typeTrueFalse.setValue(TrueFalse.fromValue(StringUtils.lowerCase((String)object)));
			}
			catch(IllegalArgumentException exception){
				ComponentXpath.INSTANCE.getXpathMap().put((ComponentXpathConstants.COMPONENT_XPATH_BOOLEAN.value()
							.replace(ID, componentName))
							.replace(Constants.PARAM_PROPERTY_NAME,	propertyName),
								new ComponentsAttributeAndValue(null, properties.get(propertyName).toString()));
				typeTrueFalse.setValue(TrueFalse.TRUE);
			}
		}
		return typeTrueFalse;
	}

	/**
	 * Prepare the Fields/Records for shcema
	 * 
	 * @param list
	 * @return {@link List}
	 * 
	 */
	protected abstract List<TypeBaseField> getFieldOrRecord(List<GridRow> list);
}
