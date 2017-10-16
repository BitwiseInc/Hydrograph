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

package hydrograph.ui.engine.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.ui.repository.UIComponentRepo;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;

/**
 * The class SubjobUiConverterUtil
 * 
 * @author Bitwise
 * 
 */
/**
 * @author soniar
 *
 */
public class SubjobUiConverterUtil {
	private static String LIMIT_COMPONENT = "LimitComponent";
	private static String CLONE_COMPONENT = "CloneComponent";
	private static String UNION_ALL_COMPONENT = "UnionallComponent";
	
	/**
	 * @param subJobXMLPath
	 * @param parameterFilePath
	 * @param parameterFile
	 * @param subJobFile
	 * @param importFromPath
	 * @param subjobPath
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws CoreException
	 * @throws FileNotFoundException
	 */
	public static Container createSubjobInSpecifiedFolder(IPath subJobXMLPath, IPath parameterFilePath, IFile parameterFile,
			IFile subJobFile, IPath importFromPath,String subjobPath) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, JAXBException, ParserConfigurationException,
			SAXException, IOException, CoreException, FileNotFoundException {
		UiConverterUtil converterUtil = new UiConverterUtil();
		Object[] subJobContainerArray=null;
			IFile xmlFile = ResourcesPlugin.getWorkspace().getRoot().getFile(subJobXMLPath);
			File file = new File(xmlFile.getFullPath().toString());
		if (file.exists()) {
			subJobContainerArray = converterUtil.convertToUiXml(importFromPath.toFile(), subJobFile, parameterFile,true);
		} else {
			IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(parameterFilePath.segment(1));
			IFolder iFolder = iProject.getFolder(subjobPath.substring(0, subjobPath.lastIndexOf('/')));
			if (!iFolder.exists()) {
				iFolder.create(true, true, new NullProgressMonitor());
			}
			IFile subjobXmlFile = iProject.getFile(subjobPath);
			subJobContainerArray = converterUtil.convertToUiXml(importFromPath.toFile(), subJobFile, parameterFile,true);
			if (!subjobXmlFile.exists() && subJobContainerArray[1] == null) {
				subjobXmlFile.create(new FileInputStream(importFromPath.toString()), true, new NullProgressMonitor());
			}
		}
		return (Container) subJobContainerArray[0];
	}
	
	/**
	 * @param subjobPath
	 * @param propertyMap
	 * @return
	 */
	public static IPath getSubjobPath(String subjobPath, LinkedHashMap<String, Object> propertyMap) {
		IPath path = null;
		if(StringUtils.isNotBlank(subjobPath)){
			path=new Path(subjobPath);
			path=path.removeFileExtension();
			path=path.addFileExtension(Constants.JOB_EXTENSION_FOR_IPATH);
			propertyMap.put(Constants.PATH,path.toString());
		}
		return path;	
	}
	
	
	/**
	 * @param subJobContainer
	 * @return
	 */
	public static Component getOutputSubJobConnectorReference(Container subJobContainer) {
		for(Component component:subJobContainer.getUIComponentList()){
			if(StringUtils.equals(Constants.OUTPUT_SOCKET_FOR_SUBJOB, component.getType())){
				return component;
		}
	  }
		return null;
  }

	
	/**
	 * @param container
	 * @return
	 */
	public static Component getInputSubJobConnectorReference(Container container) {
		for(Component component:container.getUIComponentList()){
			if(StringUtils.equals(Constants.INPUT_SOCKET_FOR_SUBJOB, component.getType())){
				return component;
		  }
		}
		return null;
	}
	
	/**
	 * @param uiComponent
	 * @param container
	 * @param currentRepository
	 * @param name_suffix
	 * @param componentName
	 * @param propertyMap
	 */
	public static void setUiComponentProperties(Component uiComponent, Container container, UIComponentRepo currentRepository, String name_suffix, String componentName, LinkedHashMap<String, Object> propertyMap) {
		uiComponent.setType(Constants.SUBJOB_ACTION);
		uiComponent.setCategory(Constants.SUBJOB_COMPONENT_CATEGORY);
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		uiComponent.setComponentLabel(componentName);
		uiComponent.setParent(container);
		currentRepository.getComponentUiFactory().put(componentName, uiComponent);
		uiComponent.setProperties(propertyMap);
	}
	
	/**
	 * @param exception
	 * @param message
	 */
	public static void showMessageBox(Exception exception, String message) {
		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR);
		messageBox.setMessage(message + "\n" + exception.getMessage());
		messageBox.open();
	}
	
	
	/**
	 * @param logger
	 * @param properties
	 * @param componentName
	 * @return
	 */
	public static Map<String, String> getRunTimeProperties(Logger logger, TypeProperties properties, String componentName) {
		logger.debug("Generating Subjob Properties for -{}", componentName);
		Map<String, String> runtimeMap = null;
		TypeProperties typeProperties = properties;
		if (typeProperties != null) {
			runtimeMap = new LinkedHashMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}
}
