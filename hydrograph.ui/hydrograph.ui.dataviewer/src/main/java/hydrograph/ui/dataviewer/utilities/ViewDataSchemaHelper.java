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
package hydrograph.ui.dataviewer.utilities;

import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.Fields;
import hydrograph.ui.common.schema.Schema;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;


/**
 * The Class ViewDataSchemaHelper.
 * Used for schema file operations at watchers
 *  
 * @author  Bitwise
 *
 */
public class ViewDataSchemaHelper {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ViewDataSchemaHelper.class);
	public static ViewDataSchemaHelper INSTANCE = new ViewDataSchemaHelper();
	
	
	private ViewDataSchemaHelper() {
	}
	
	/**
	 * This function will read schema file and return schema fields
	 * @param schemaFilePath
	 * @return Fields
	 */
	public Fields getFieldsFromSchema(String schemaFilePath){
		Fields fields = null;
		if(StringUtils.isNotBlank(schemaFilePath)){
			String filePath=((IPath)new Path(schemaFilePath)).removeFileExtension().addFileExtension(Constants.XML_EXTENSION_FOR_IPATH).toString();
			File file = new File(filePath);
			if(file.exists()){
				try {
					JAXBContext jaxbContext = JAXBContext.newInstance(Schema.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					Schema schema = (Schema) jaxbUnmarshaller.unmarshal(file);
					fields = schema.getFields();
					for(Field field : fields.getField()){
						logger.debug("Type:{}, Name:{}, Format:{}" + field.getType(),field.getName(),field.getFormat());
					}
				} catch (JAXBException jaxbException) {
					logger.error("Invalid xml file: ", jaxbException);
				}
			}
		}
		return fields;
	}
}
