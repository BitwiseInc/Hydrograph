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


package hydrograph.ui.graph.debugconverter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.SubjobDetails;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.graph.schema.propagation.SchemaPropagation;
import hydrograph.ui.graph.utility.ViewDataUtils;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.GridRowLoader;

/**
 * This class is used for schema file operations at watchers
 *  
 * @author  Bitwise
 *
 */
public class SchemaHelper {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(SchemaHelper.class);
	public static SchemaHelper INSTANCE = new SchemaHelper();

	
	private SchemaHelper() {
	}
	
	/**
	 * This function will write schema in xml file
	 * @param schemaFilePath
	 * @throws CoreException 
	 */
	public void exportSchemaFile(String schemaFilePath) throws CoreException{
		Map<String, SubjobDetails> componentNameAndLink = new HashMap();
		List<String> oldComponentIdList = new LinkedList<>();
		File file = null;
		String socketName = null;
		String componentName;
		String component_Id = null;
		
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(activeEditor instanceof ELTGraphicalEditor){
			ELTGraphicalEditor editor=(ELTGraphicalEditor) activeEditor;
			if(editor!=null){
				GraphicalViewer	graphicalViewer =(GraphicalViewer) ((GraphicalEditor)editor).getAdapter(GraphicalViewer.class);
				for (Object objectEditPart : graphicalViewer.getEditPartRegistry().values()){
					if(objectEditPart instanceof ComponentEditPart){
						List<Link> links = ((ComponentEditPart) objectEditPart).getCastedModel().getSourceConnections();
						for(Link link : links){
							 Component component = link.getSource();
							 if(StringUtils.equalsIgnoreCase(component.getComponentName(), Constants.SUBJOB_COMPONENT)){
								 Link inputLink=component.getInputLinks().get(0);
								 String previousComponent=inputLink.getSource().getComponentId();
								 ViewDataUtils.getInstance().subjobParams(componentNameAndLink, component, new StringBuilder(), link.getSourceTerminal());
								 removeDuplicateKeys(oldComponentIdList, componentNameAndLink);
								 createDebugXmls(component,schemaFilePath,component.getComponentId(),previousComponent);
								 for(Entry<String, SubjobDetails> entry : componentNameAndLink.entrySet()){
									String comp_soc = entry.getKey();
									oldComponentIdList.add(comp_soc);
									String[] split = StringUtils.split(comp_soc, "/.");
									component_Id = split[0];
									for(int i = 1;i<split.length-1;i++){
										component_Id = component_Id + "." + split[i];
									}
									socketName = entry.getValue().getTargetTerminal();
								 }
								 	componentName = component_Id;
								}else{
									componentName = link.getSource().getComponentId();
									socketName = link.getSourceTerminal();
								}
							 Object obj = link.getSource().getComponentEditPart();
							 List<PortEditPart> portEditPart = ((EditPart) obj).getChildren();
							 
							 for(AbstractGraphicalEditPart part : portEditPart){
								 if(part instanceof PortEditPart){
									 boolean isWatch = ((PortEditPart)part).getPortFigure().isWatched();
									 if(isWatch && link.getSourceTerminal().equals(((PortEditPart)part).getPortFigure().getTerminal())){
										if(StringUtils.isNotBlank(schemaFilePath)){
											String path = schemaFilePath.trim() + "_" + componentName + "_" + socketName;
											String filePath=((IPath)new Path(path)).addFileExtension(Constants.XML_EXTENSION_FOR_IPATH).toString();
											List<GridRow> gridRowList = getSchemaGridRowList(link);
											file = new File(filePath);
											GridRowLoader gridRowLoader = new GridRowLoader(Constants.GENERIC_GRID_ROW, file);
											gridRowLoader.exportXMLfromGridRowsWithoutMessage(gridRowList);
											logger.debug("schema file created for : {}, {}", componentName, socketName);
										}
									}
								 }
							 }
						}
					}
				}
			}
		}
	}
	
	private void createDebugXmls(Component component, String schemaFilePath, String componentId,
			String previousComponent) {
		Container container = (Container) component.getSubJobContainer().get(Constants.CONTAINER);
		ComponentsOutputSchema componentsOutputSchema = null;
		for (Component componentObject : container.getUIComponentList()) {
			if (componentObject instanceof SubjobComponent) {
				Link link = componentObject.getInputLinks().get(0);
				String previousComponentObject = componentId + Constants.DOT_SEPERATOR
						+ link.getSource().getComponentId();
				createDebugXmls(componentObject, schemaFilePath,
						componentId + Constants.DOT_SEPERATOR + componentObject.getComponentId(),
						previousComponentObject);
			}

			componentsOutputSchema = getComponentOutputSchema(componentsOutputSchema, componentObject);

			Map<String, Long> watchPoints = componentObject.getWatcherTerminals();
			if (watchPoints != null && !watchPoints.isEmpty()) {
				if (componentObject instanceof InputSubjobComponent) {
					createXmlCorrospondingToEachWatchPoint(schemaFilePath, previousComponent, componentsOutputSchema,
							componentObject);
				} else {
					createXmlCorrospondingToEachWatchPoint(schemaFilePath, componentId, componentsOutputSchema,
							componentObject);
				}
			}
		}
	}
	
	private ComponentsOutputSchema getComponentOutputSchema(ComponentsOutputSchema componentsOutputSchema,
			Component componentObject) {
		HashMap<String, ComponentsOutputSchema> componentOutputSchema = (HashMap<String, ComponentsOutputSchema>) componentObject
				.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
		Set<String> keys = componentOutputSchema.keySet();
		for (String key : keys) {
			componentsOutputSchema = componentOutputSchema.get(key);
		}
		return componentsOutputSchema;
	}


	private void createXmlCorrospondingToEachWatchPoint(String schemaFilePath, String componentId,
			ComponentsOutputSchema componentsOutputSchema, Component componentObject) {
		String path;
		String componentObjectId = null;
		path = getFilePath(schemaFilePath, componentId, componentObject, componentObjectId);
		String xmlFilePath = ((IPath) new Path(path)).addFileExtension(Constants.XML_EXTENSION_FOR_IPATH).toString();
		List<GridRow> gridRowList = componentsOutputSchema.getGridRowList();
		File file = new File(xmlFilePath);
		GridRowLoader gridRowLoader = new GridRowLoader(Constants.GENERIC_GRID_ROW, file);
		gridRowLoader.exportXMLfromGridRowsWithoutMessage(gridRowList);
	}

	private String getFilePath(String schemaFilePath, String componentId, Component componentObject,
			String componentObjectId) {
		String path;
		if (componentObject instanceof InputSubjobComponent) {
			path = schemaFilePath.trim() + Constants.UNDERSCORE_SEPERATOR + componentId + Constants.UNDERSCORE_SEPERATOR
					+ componentObject.getWatcherTerminals().toString().replace("{", "").split("=")[0];
		} else if (componentObject instanceof SubjobComponent) {
			Map<String, Long> watchPoints = componentObject.getWatcherTerminals();
			if (watchPoints != null && !watchPoints.isEmpty()) {
				componentObjectId = getComponentId(componentObject);
			}
			path = schemaFilePath.trim() + Constants.UNDERSCORE_SEPERATOR + componentId + Constants.DOT_SEPERATOR
					+ componentObject.getComponentId() + Constants.DOT_SEPERATOR + componentObjectId;

		} else {
			path = schemaFilePath.trim() + Constants.UNDERSCORE_SEPERATOR + componentId + Constants.DOT_SEPERATOR
					+ componentObject.getComponentId() + Constants.UNDERSCORE_SEPERATOR
					+ componentObject.getWatcherTerminals().toString().replace("{", "").split("=")[0];
		}
		return path;
	}
	
	private String getComponentId(Component component) {
		String componentId = "";
		Component componentPrevToOutput = null;
		String portNumber = null;
		Component outputSubjobComponent = (Component) component.getSubJobContainer().get(Messages.OUTPUT_SUBJOB_COMPONENT);
		if (outputSubjobComponent != null) {
			for (Link link : outputSubjobComponent.getTargetConnections()) {
				componentPrevToOutput = link.getSource();
				if (Constants.SUBJOB_COMPONENT.equals(componentPrevToOutput.getComponentName())) {
					componentId = componentPrevToOutput.getComponentId() + Constants.DOT_SEPERATOR
							+ getComponentId(componentPrevToOutput);
				} else {
					portNumber = link.getTargetTerminal().replace(Messages.IN_PORT_TYPE, Messages.OUT_PORT_TYPE);
					componentId = componentPrevToOutput.getComponentId() + "_" + portNumber;
				}
			}
		}
		return componentId;
	}
	private List<GridRow> getSchemaGridRowList(Link link) {
		List<GridRow> gridRowList = new ArrayList<GridRow>();
		ComponentsOutputSchema componentsOutputSchema = SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
		if (componentsOutputSchema != null && !componentsOutputSchema.getGridRowList().isEmpty()) {
			gridRowList.addAll(componentsOutputSchema.getGridRowList());
		} else {
			Schema schema = (Schema) link.getSource().getProperties().get(Constants.SCHEMA);
			if (schema != null && schema.getGridRow() != null) {
				gridRowList.addAll(schema.getGridRow());
			}
		}
		return gridRowList;
	}
	 
	private void removeDuplicateKeys(List<String> oldKeys, Map<String, SubjobDetails> componentNameAndLink){
		if(componentNameAndLink.size() > 1){
			oldKeys.forEach(field -> componentNameAndLink.remove(field));
		}
	}
	 
	/**
	 * This function will return file path with validation
	 * @param path
	 * @return validPath
	 */
	public String validatePath(String path){
		String validPath = null;
			if(StringUtils.endsWith(path,File.separator)){
				return path;
			}else{
				validPath = path + File.separator;
				return validPath;
			}
	}
}
