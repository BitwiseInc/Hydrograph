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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.exceptions.ConverterNotFoundException;
import hydrograph.ui.engine.exceptions.UnknownComponentException;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;


/**
 * Factory class for creating Converter instances for particular component
 * 
 */
public class ConverterFactory {
	public static final ConverterFactory INSTANCE = new ConverterFactory();
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ConverterFactory.class);

	private ConverterFactory() {
	}

	public Converter getConverter(Component component) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		try {
			logger.debug("Getting converter for :{}", component.getProperties().get(Constants.PARAM_NAME));
			return (Converter) Class.forName(component.getConverter()).getDeclaredConstructor(Component.class)
					.newInstance(component);
		} catch (ClassNotFoundException exception) {
			logger.error("Exception Occured getting Converter for {}, {}:", new Object[] {
					component.getProperties().get(Constants.PARAM_NAME), exception });
			if (component.getComponentName().equalsIgnoreCase(Constants.UNKNOWN_COMPONENT)){
				return null;
//				throw new UnknownComponentException(component.getComponentName(), exception);
			}
			else
				throw new ConverterNotFoundException(component.getPrefix(), exception);
		}
	}
}
