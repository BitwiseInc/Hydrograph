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
package hydrograph.ui.engine.ui.converter.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.operationstypes.Subjob;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.engine.exceptions.EngineException;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.converter.UiConverter;
import hydrograph.ui.engine.ui.exceptions.ComponentNotFoundException;
import hydrograph.ui.engine.ui.util.SubjobUiConverterUtil;
import hydrograph.ui.engine.ui.util.UiConverterUtil;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.widgets.utility.SubjobUtility;

/**
 * Converter to convert jaxb subjob object of operation type into subjob component
 *
 *@author BITWISE
 */
public class OperationSubJobUiConverter extends UiConverter {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OperationSubJobUiConverter.class);
	private Subjob subjob;
	
	public OperationSubJobUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new SubjobComponent();
		this.propertyMap = new LinkedHashMap<>();
		subjob = (Subjob) typeBaseComponent;
	}
	
	@Override
	public void prepareUIXML(){
		logger.debug("Fetching Input-Delimited-Properties for {}", componentName);
		super.prepareUIXML();
		IPath subJobPath = SubjobUiConverterUtil.getSubjobPath(subjob.getPath().getUri(), propertyMap);
		IPath subJobXMLPath = new Path(subjob.getPath().getUri());
		IPath parameterFilePath = parameterFile.getFullPath().removeLastSegments(1)
				.append(subJobPath.removeFileExtension().lastSegment()).addFileExtension(Constants.PROPERTIES);
		IFile parameterFile = ResourcesPlugin.getWorkspace().getRoot().getFile(parameterFilePath);
		Container subJobContainer = null;
		try {
			if (!subJobXMLPath.isAbsolute()) {
				IFile subJobFile = ResourcesPlugin.getWorkspace().getRoot().getFile(subJobPath);
				IPath importFromPath = new Path(sourceXmlPath.getAbsolutePath());
				importFromPath = importFromPath.removeLastSegments(1).append(subJobXMLPath.lastSegment());
				subJobContainer = SubjobUiConverterUtil.createSubjobInSpecifiedFolder(subJobXMLPath, parameterFilePath,
						parameterFile, subJobFile, importFromPath, subjob.getPath().getUri());
			} else {
				File jobFile = new File(subJobPath.toString());
				File subJobFile = new File(subjob.getPath().getUri());
				UiConverterUtil converterUtil = new UiConverterUtil();
				subJobContainer = converterUtil.convertSubjobToUiXml(subJobFile, jobFile, parameterFile);
			}

			Component inputSubjobComponent = SubjobUiConverterUtil.getInputSubJobConnectorReference(subJobContainer);
			inputSubjobComponent.getSubJobContainer().put(Constants.SUBJOB_COMPONENT, uiComponent);
			inputSubjobComponent.getProperties().put(Constants.SCHEMA_TO_PROPAGATE,
					new LinkedHashMap<String, ComponentsOutputSchema>());

			uiComponent.getSubJobContainer().put(Constants.INPUT_SUBJOB, inputSubjobComponent);
			Component outputSubjobComponent = SubjobUiConverterUtil.getOutputSubJobConnectorReference(subJobContainer);
			uiComponent.getSubJobContainer().put(Constants.OUTPUT_SUBJOB, outputSubjobComponent);
			
			outputSubjobComponent.getSubJobContainer().put(Constants.SUBJOB_COMPONENT, uiComponent);
			
			if (outputSubjobComponent.getProperties().get(Constants.SCHEMA_TO_PROPAGATE) != null) {
				propertyMap.put(Constants.SCHEMA_TO_PROPAGATE,
						outputSubjobComponent.getProperties().get(Constants.SCHEMA_TO_PROPAGATE));
			} else {
				outputSubjobComponent.getProperties().put(Constants.SCHEMA_TO_PROPAGATE,
						new LinkedHashMap<String, ComponentsOutputSchema>());
			}

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | EngineException | IOException | CoreException
				| ComponentNotFoundException exception) {
			logger.error("Error occurred while importing "+subJobXMLPath.lastSegment()+" file in workspace", exception);
			SubjobUiConverterUtil.showMessageBox(exception, "Error occurred while importing "+subJobXMLPath.lastSegment()+" file in workspace");
		} catch (JAXBException | ParserConfigurationException | SAXException exception) {
			logger.error("Error occurred while importing "+subJobXMLPath.lastSegment()+" file in workspace", exception);
			SubjobUiConverterUtil.showMessageBox(exception, "Invalid XML File.");
		}

		propertyMap.put(Constants.RUNTIME_PROPERTY_NAME, getRuntimeProperties());
		getInPort((TypeOperationsComponent) typeBaseComponent);
		getOutPort((TypeOperationsComponent) typeBaseComponent);
		SubjobUiConverterUtil.setUiComponentProperties(uiComponent, container, currentRepository, name_suffix,
				componentName, propertyMap);
		SubjobUtility.INSTANCE.showOrHideErrorSymbolOnComponent(subJobContainer,uiComponent);
	}

	
	@Override
	protected Map<String, String> getRuntimeProperties() {
		return SubjobUiConverterUtil.getRunTimeProperties(logger,subjob.getSubjobParameter(),componentName);
	}
	
	private void getInPort(TypeOperationsComponent operationsComponent) {
		logger.debug("Generating InPut Ports for -{}", componentName);
		int count = 0;
		if (operationsComponent.getInSocket() != null) {
			for (TypeBaseInSocket inSocket : operationsComponent.getInSocket()) {
				uiComponent.engageInputPort(inSocket.getId());
				currentRepository.getComponentLinkList().add(
						new LinkingData(inSocket.getFromComponentId(), operationsComponent.getId(), inSocket
								.getFromSocketId(), inSocket.getId()));
				count++;
			}
			propertyMap.put(Constants.INPUT_PORT_COUNT_PROPERTY, count);
			uiComponent.completeInputPortSettings(count);
		}
	}

	private void getOutPort(TypeOperationsComponent operationsComponent) {
		logger.debug("Generating OutPut Ports for -{}", componentName);
		int count = 0;
		if (operationsComponent.getOutSocket() != null) {
			for (TypeOperationsOutSocket outSocket : operationsComponent.getOutSocket()) {
				uiComponent.engageOutputPort(outSocket.getId());
				count++;
			}
			propertyMap.put(Constants.OUTPUT_PORT_COUNT_PROPERTY, count);
			uiComponent.completeOutputPortSettings(count);
		}
	}
}
