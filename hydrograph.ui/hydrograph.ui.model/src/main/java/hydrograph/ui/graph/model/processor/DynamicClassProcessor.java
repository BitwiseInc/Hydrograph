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

 
package hydrograph.ui.graph.model.processor;

import hydrograph.ui.common.component.config.Component;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.Hashtable;

import org.slf4j.Logger;


/**
 * The Class DynamicClassProcessor.
 */
public class DynamicClassProcessor{
	public static final DynamicClassProcessor INSTANCE = new DynamicClassProcessor(); 

	private static final Logger logger = LogFactory.INSTANCE.getLogger(DynamicClassProcessor.class);
	private Hashtable<String, Class<?>> classMapStringToClass = new Hashtable<String, Class<?>>();
	private Hashtable<Class<?>, String> classMapClassToString = new Hashtable<Class<?>, String>();
	
	/**
	 * Contains.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return true, if successful
	 */
	public boolean contains(Class<?> clazz) {
		return classMapStringToClass.containsValue(clazz);
	}
	
	/**
	 * Contains.
	 * 
	 * @param clazzName
	 *            the clazz name
	 * @return true, if successful
	 */
	public boolean contains(String clazzName) {
		return classMapClassToString.containsValue(clazzName);
	}
	
	private void put(String clazzName, Class<?> clazz){
		classMapStringToClass.put(clazzName, clazz);
		classMapClassToString.put(clazz, clazzName);
	}
	
	/**
	 * Gets the clazz name.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the clazz name
	 */
	public String getClazzName(Class<?> clazz){
		return classMapClassToString.get(clazz);
	}
	
	/**
	 * Gets the clazz.
	 * 
	 * @param className
	 *            the class name
	 * @return the clazz
	 */
	public Class<?> getClazz(String className){
		return classMapStringToClass.get(className);
	}
	
	/**
	 * Creates the class.
	 * 
	 * @param componentConfig
	 *            the component config
	 * @return the class
	 */
	public Class<?> createClass(Component componentConfig){
		if(contains(componentConfig.getName())){
			return getClazz(componentConfig.getName());
		}
		else{
			Class<?> clazz;
			try {
				clazz = this.getClass().getClassLoader().loadClass(Constants.COMPONENT_PACKAGE_PREFIX + componentConfig.getName());
			} catch (ClassNotFoundException exception) {
				logger.error("Failed to load component {} due to {}", componentConfig.getName(), exception);
				throw new RuntimeException();
			}
			INSTANCE.put(componentConfig.getName(), clazz);
			return clazz;
		}
	}
}