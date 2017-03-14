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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
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

import com.thoughtworks.xstream.XStream;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.Port;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * @author Bitwise
 *
 */
public class DebugHelper {

	private static Logger logger = LogFactory.INSTANCE.getLogger(DebugHelper.class);
	public static DebugHelper INSTANCE = new DebugHelper();
	private List<String> subjobWatcherList; 

	/**
	 * This function used to return subgraph component_Id and socket_Id
	 *
	 */
	public List<String> getSubgraphComponent(Component component) throws CoreException{
		Container container=null;
		subjobWatcherList=new ArrayList<>();
		if(StringUtils.equalsIgnoreCase(component.getComponentName(), Constants.SUBJOB_COMPONENT)){
			String subgraphFilePath=(String) component.getProperties().get(Constants.JOB_PATH);
			if(StringUtils.isNotBlank(subgraphFilePath)){
				IPath jobPath=new Path(subgraphFilePath);
				if(jobPath.toFile().exists()){
					XStream xs = new XStream();
					container=(Container) xs.fromXML(jobPath.toFile());
					List<Link> links = null;
					for(Component component_temp :container.getUIComponentList()){
						if(StringUtils.equalsIgnoreCase(component_temp.getComponentLabel().getLabelContents(), Constants.OUTPUT_SUBJOB)){
							links=component_temp.getTargetConnections();
						}
				}	
					if (links != null) {
						for (Link str : links) {
							String sub_comp = str.getSource().getComponentLabel().getLabelContents();
							String sub_comp_port = str.getSourceTerminal();
							subjobWatcherList.add(sub_comp + "." + sub_comp_port);
						}
					}
					return subjobWatcherList;
				}
				else{
					if(ResourcesPlugin.getWorkspace().getRoot().getFile(jobPath).exists()){
						XStream xs = new XStream();
					container=(Container) xs.fromXML(ResourcesPlugin.getWorkspace().getRoot().getFile(jobPath).getContents(true));
					List<Link> links = null;
					
					for(Component component_temp :container.getUIComponentList()){
						if(StringUtils.equalsIgnoreCase(component_temp.getComponentLabel().getLabelContents(), Constants.OUTPUT_SUBJOB)){
							links=component_temp.getTargetConnections();
							break;
						}
					}
						if (links != null) {
							for (Link link : links) {
								Map<String, Port> map = link.getSource().getPorts();
								for (Entry<String, Port> entry : map.entrySet()) {
									Port port = entry.getValue();
									String type = port.getPortType();
									if ((type.equalsIgnoreCase("out") || type.equalsIgnoreCase("unused"))
											&& port.isWatched()) {
										Component component2 = port.getParent();
										String subjob_componenetId = component2.getComponentLabel().getLabelContents();
										String subjob__socketId = port.getTerminal();
										subjobWatcherList.add(subjob_componenetId + "." + subjob__socketId);
									}
								}
							}
						}
					return subjobWatcherList;
					
				  }
				}
			  }
			}
		return null;
	}
	
	/**
	 * This function returns that watcher is added on selected port
	 *
	 */
	public boolean checkWatcher(Component selectedComponent, String portName) {
		EditPart editPart = (EditPart) selectedComponent.getComponentEditPart();
		List<PortEditPart> portEdit = editPart.getChildren();
		for(AbstractGraphicalEditPart part : portEdit){
			if(part instanceof PortEditPart){
				String portLabel = ((PortEditPart) part).getCastedModel().getTerminal();
				if(portLabel.equals(portName)){
					return  ((PortEditPart) part).getPortFigure().isWatched();
				}
			}
		}
					
		return false;
	}
	
	/**
	 * This function will check watch point in the graph and return true if any watch point exist 
	 *
	 */
	public boolean hasMoreWatchPoints(){
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(activeEditor instanceof ELTGraphicalEditor){
			ELTGraphicalEditor editor=(ELTGraphicalEditor) activeEditor;
			if(editor!=null){
				GraphicalViewer	graphicalViewer =(GraphicalViewer) ((GraphicalEditor)editor).getAdapter(GraphicalViewer.class);
				for (Object objectEditPart : graphicalViewer.getEditPartRegistry().values()){
					if(objectEditPart instanceof ComponentEditPart){
						List<PortEditPart> portEditParts = ((EditPart) objectEditPart).getChildren();
						for(AbstractGraphicalEditPart part : portEditParts) {	
							if(part instanceof PortEditPart){
								boolean isWatch = ((PortEditPart)part).getPortFigure().isWatched();
								if(isWatch){
									return isWatch;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
}