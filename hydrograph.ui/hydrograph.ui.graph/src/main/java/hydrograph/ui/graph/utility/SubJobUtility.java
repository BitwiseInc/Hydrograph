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

 
package hydrograph.ui.graph.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.slf4j.Logger;

import hydrograph.ui.common.datastructures.tooltip.PropertyToolTipInformation;
import hydrograph.ui.common.util.CanvasDataAdapter;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.util.ConverterUtil;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.figure.ComponentFigure;
import hydrograph.ui.graph.handler.JobCreationPage;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.PortAlignmentEnum;
import hydrograph.ui.graph.model.PortDetails;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.graph.model.components.OutputSubjobComponent;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.graph.schema.propagation.SchemaPropagation;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.widgets.utility.SubjobUtility;


/**
 * The Class SubJobUtility contain business logic to create sub graph.
 */
public class SubJobUtility {

	/** The cache input subjob comp. */
	private Map<Component, Integer> inputSubjobCompCache = new LinkedHashMap<>();

	/** The cache out subjob comp. */
	private Map<Component, List<String>> outputSubjobCompCache = new LinkedHashMap<>();

	/** The Constant logger. */
	private static final Logger logger = LogFactory.INSTANCE.getLogger(SubJobUtility.class);

	/**
	 * Open sub graph save dialog.
	 * 
	 * @return the i file
	 */
	public IFile openSubJobSaveDialog() {
		IFile iFile = null;
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(Messages.JOB_WIZARD_ID);
		if (descriptor != null) {
			IWizard wizard = null;
			try {
				wizard = descriptor.createWizard();
			} catch (CoreException coreException) {
				logger.error("Error while opening create job wizard", coreException);
			}
			WizardDialog wizardDialog = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
			wizardDialog.setTitle(wizard.getWindowTitle());
			wizardDialog.open();
			JobCreationPage jobCreationPage = (JobCreationPage) wizardDialog.getSelectedPage();
			iFile = jobCreationPage.getNewFile();
		}
		return iFile;
	}

	/**
	 * Gets the current editor.
	 * 
	 * @return the current editor
	 */
	public static ELTGraphicalEditor getCurrentEditor() {
		ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		if (editor != null)
			return editor;
		else
			return new ELTGraphicalEditor();
	}

	/**
	 * Do save as sub graph.
	 * 
	 * @param file
	 *            the file
	 * @param container
	 *            the container
	 * @return the i file
	 */
	public IFile doSaveAsSubJob(IFile file, Container container) {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ConverterUtil.INSTANCE.convertToXML(container, false, null, null);
		
			if (file != null) {
				CanvasUtils.INSTANCE.fromObjectToXML(container,out);
				
				file.create(new ByteArrayInputStream(out.toByteArray()), true, null);
				
				getCurrentEditor().genrateTargetXml(file, null, container);
				getCurrentEditor().setDirty(false);
			}
		} catch (Exception e ) {
			MessageDialog.openError(new Shell(), "Error", "Exception occured while saving the graph -\n" + e.getMessage());
		}
		finally {
			try {
				out.close();
			} catch (IOException ioException) {
				logger.warn("Exception occurred while closing stream");
			}
		}
		return file;
	}

	/**
	 * Creates the dynamic input port.
	 * 
	 * @param inLinks
	 *            the in links
	 * @param edComponentEditPart
	 *            the ed component edit part
	 */
	public void createDynamicInputPort(List<Link> inLinks, ComponentEditPart edComponentEditPart) {
		int sourceTerminal;
		for (int i = 0; i < inLinks.size(); i++) {
			Component oldTarget = inLinks.get(i).getTarget();
			sourceTerminal = getInPortCount(oldTarget);
			inLinks.get(i).getSource();
			Link link = inLinks.get(i);
			link.detachTarget();
			link.setTarget(edComponentEditPart.getCastedModel());
			link.setTargetTerminal(Constants.INPUT_SOCKET_TYPE + i);
			oldTarget.freeInputPort(link.getTargetTerminal());
			oldTarget.disconnectInput(link);
			link.attachTarget();
			edComponentEditPart.getCastedModel().engageInputPort(Constants.INPUT_SOCKET_TYPE + i);
			edComponentEditPart.refresh();
			Integer returnedValue = inputSubjobCompCache.put(oldTarget, sourceTerminal);
			if (returnedValue != null  && returnedValue>sourceTerminal) {
				inputSubjobCompCache.put(oldTarget, returnedValue);
			}
		}
	}

	private int getInPortCount(Component oldTarget) {
		PortDetails portDetails = oldTarget.getPortDetails(PortAlignmentEnum.LEFT);
		if ((portDetails != null && portDetails.isAllowMultipleLinks())
				&& (oldTarget.getTargetConnections()!=null && !oldTarget.getTargetConnections().isEmpty())) {
		return	oldTarget.getTargetConnections().size();
		} 
		return	oldTarget.getInPortCount();
	}

	/**
	 * Creates the dynamic output port.
	 * 
	 * @param outLinks
	 *            the out links
	 * @param edComponentEditPart
	 *            the component edit part
	 * @return the map
	 */
	public Map<Component, List<String>> createDynamicOutputPort(List<Link> outLinks,
			ComponentEditPart edComponentEditPart) {
		List<String> targetTerminal = new ArrayList<>();
		for (int i = 0; i < outLinks.size(); i++) {
			Component oldSource = outLinks.get(i).getSource();
			Link link = outLinks.get(i);

			List<String> returnedValue = outputSubjobCompCache.put(oldSource, targetTerminal);
			if (returnedValue == null) {
				targetTerminal = new ArrayList<>();
				targetTerminal.add(link.getSourceTerminal());
				outputSubjobCompCache.put(oldSource, targetTerminal);
			} else
				targetTerminal.add(link.getSourceTerminal());

			link.detachSource();
			link.setSource(edComponentEditPart.getCastedModel());
			link.setSourceTerminal(Constants.OUTPUT_SOCKET_TYPE + i);
			oldSource.freeOutputPort(link.getTargetTerminal());
			oldSource.disconnectOutput(link);
			link.attachSource();
			edComponentEditPart.getCastedModel().engageOutputPort(Constants.OUTPUT_SOCKET_TYPE + i);
			edComponentEditPart.refresh();

		}
		return outputSubjobCompCache;

	}

	/**
	 * Update sub graph model properties.
	 * 
	 * @param edComponentEditPart
	 *            the ed component edit part
	 * @param inPort
	 *            the in port
	 * @param outPort
	 *            the out port
	 * @param file
	 *            the file
	 */
	public void updateSubJobModelProperties(ComponentEditPart edComponentEditPart, int inPort, int outPort, IFile file) {
		edComponentEditPart.getCastedModel().completeInputPortSettings(inPort);
		edComponentEditPart.getCastedModel().completeOutputPortSettings(outPort);
		ComponentFigure compFig = (ComponentFigure) edComponentEditPart.getFigure();
		compFig.setHeight(inPort, outPort);
		Dimension newSize = new Dimension(compFig.getWidth(), compFig.getHeight()
				+ edComponentEditPart.getCastedModel().getComponentLabelMargin());

		edComponentEditPart.getCastedModel().setSize(newSize);

		String subJobFilePath = file.getFullPath().toString();
		edComponentEditPart.getCastedModel().getProperties().put(Constants.PATH, subJobFilePath.substring(1));
		if (inPort != 0 && outPort != 0)
			edComponentEditPart.getCastedModel().getProperties().put(Constants.TYPE, Constants.OPERATION);
		if (inPort != 0 && outPort == 0)
			edComponentEditPart.getCastedModel().getProperties().put(Constants.TYPE, Constants.OUTPUT);
		if (inPort == 0 && outPort != 0)
			edComponentEditPart.getCastedModel().getProperties().put(Constants.TYPE, Constants.INPUT);
		if (inPort == 0 && outPort == 0)
			edComponentEditPart.getCastedModel().getProperties().put(Constants.TYPE, Constants.STANDALONE_SUBJOB);
		edComponentEditPart.refresh();
	}

	/**
	 * Create sub graph xml,open the subjob in new editor and return subjob container.
	 * 
	 * @param componentEditPart
	 *            the component edit part
	 * @param clipboardList
	 *            the clipboard list
	 * @param file
	 *            the file
	 * @return
	 */
	
	public Container createSubJobXmlAndGetContainer(ComponentEditPart componentEditPart, List clipboardList, IFile file) {
		Container container = new Container();
		/*
		 * Add sub graph join component in subjob that use to link main graph with sub graph.
		 */
		Component inputSubComponent = SubJobPortLinkUtilty.addInputSubJobComponentAndLink(container,
				inputSubjobCompCache, clipboardList);
		Component outSubComponent = SubJobPortLinkUtilty.addOutputSubJobComponentAndLink(container,
				inputSubjobCompCache, outputSubjobCompCache, clipboardList);

		/*
		 * Add all remaining component those not linked with main graph.
		 */
		for (Object object : clipboardList) {
			container.addSubJobChild((Component) object);
		}

		doSaveAsSubJob(file, container);
		inputSubComponent.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, new HashMap<>());
		propogateSchemaToSubjob((componentEditPart.getCastedModel()), outSubComponent);
		updateParametersInGrid((componentEditPart.getCastedModel()), file.getFullPath());
		componentEditPart.getCastedModel().getSubJobContainer().put(Constants.SUBJOB_CONTAINER, container);
	    return container;
	}

	/**
	 * Propagate schema to subjob.
	 * 
	 * @param subjobComponent
	 *            the subjob component
	 * @param component
	 *            the component
	 */
	public void propogateSchemaToSubjob(Component subjobComponent, Component component) {
		ComponentsOutputSchema componentsOutputSchema1=null;
		if (Constants.INPUT_SUBJOB.equalsIgnoreCase(component.getComponentName())) {
			Map<String, ComponentsOutputSchema> inputSchemaMap = new HashMap<String, ComponentsOutputSchema>();
			for (Link innerLink : component.getSourceConnections()) {
				Link mainLink = null;
				for (Link link : subjobComponent.getTargetConnections()) {
					if (link.getTargetTerminal().replaceAll(Constants.INPUT_SOCKET_TYPE, Constants.OUTPUT_SOCKET_TYPE)
							.equalsIgnoreCase(innerLink.getSourceTerminal())) {
						mainLink = link;
						 componentsOutputSchema1 = SchemaPropagation.INSTANCE
								.getComponentsOutputSchema(mainLink);
						inputSchemaMap.put(innerLink.getSourceTerminal(), componentsOutputSchema1);
					}
				}

			}
			component.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, inputSchemaMap);
			subjobComponent.getSubJobContainer().put(Constants.INPUT_SUBJOB, component);
			SchemaPropagation.INSTANCE.continuousSchemaPropagation(subjobComponent, inputSchemaMap);
		}
		if (Constants.OUTPUT_SUBJOB.equalsIgnoreCase(component.getComponentName())) {
			boolean intializeSchemaMap=true;
			Map<String, ComponentsOutputSchema> outputSchemaMap = new HashMap<String, ComponentsOutputSchema>();
			for (Link innerLink : component.getTargetConnections()) {
				ComponentsOutputSchema componentsOutputSchema = SchemaPropagation.INSTANCE
						.getComponentsOutputSchema(innerLink);
				outputSchemaMap.put(
						innerLink.getTargetTerminal().replaceAll(Constants.INPUT_SOCKET_TYPE,
								Constants.OUTPUT_SOCKET_TYPE), componentsOutputSchema);
				if(StringUtils.equalsIgnoreCase(Constants.STRAIGHTPULL,innerLink.getSource().getCategory())
						&&StringUtils.equalsIgnoreCase(Constants.FILTER,innerLink.getSource().getComponentName())
						&&StringUtils.equalsIgnoreCase(Constants.UNIQUE_SEQUENCE,innerLink.getSource().getComponentName()))
					intializeSchemaMap=false;
			}	
		
			component.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, outputSchemaMap);
			subjobComponent.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, outputSchemaMap);
			subjobComponent.getSubJobContainer().put(Constants.OUTPUT_SUBJOB, component);
			component.getSubJobContainer().put(Constants.SUBJOB_COMPONENT, subjobComponent);
			if(intializeSchemaMap)
			SubjobUtility.INSTANCE.initializeSchemaMapForInputSubJobComponent(subjobComponent, component);
		}
	}

	/**
	 * Update subjob port.
	 * 
	 * @param componentEditPart
	 *            the component edit part
	 */
	public Container updateSubjobPropertyAndGetSubjobContainer(ComponentEditPart componentEditPart, String filePath,
			Component selectedSubjobComponent,boolean updateOnlyErrorSymbol) {
		IPath jobFileIPath = null;
		Container container = null;
		InputStream inputStream =null;
		FileInputStream fileInputStream=null;
		if (StringUtils.isNotBlank(filePath) && selectedSubjobComponent != null) {
			jobFileIPath = new Path(filePath);
		} else if (componentEditPart != null
				&& componentEditPart.getCastedModel().getProperties().get(Constants.PATH_PROPERTY_NAME) != null) {
			filePath = componentEditPart.getCastedModel().getProperties().get(Constants.PATH_PROPERTY_NAME).toString();
			jobFileIPath = new Path(filePath);
			selectedSubjobComponent = componentEditPart.getCastedModel();
		}

		if (StringUtils.isNotBlank(filePath) && !isFileContainsParameter(jobFileIPath)) {
			try {

				if (ResourcesPlugin.getWorkspace().getRoot().getFile(jobFileIPath).exists()) {
					inputStream = ResourcesPlugin.getWorkspace().getRoot().getFile(jobFileIPath).getContents();
					container = (Container)CanvasUtils.INSTANCE.fromXMLToObject(inputStream);
				} else if (jobFileIPath !=null && isFileExistsOnLocalFileSystem(jobFileIPath)) {
					fileInputStream = new FileInputStream(jobFileIPath.toFile());
					container = (Container) CanvasUtils.INSTANCE.fromXMLToObject(fileInputStream);
				}

				if(updateOnlyErrorSymbol){
					SubjobUtility.INSTANCE.showOrHideErrorSymbolOnComponent(container,selectedSubjobComponent);
				}else{
					updateContainerAndSubjob(container, selectedSubjobComponent, jobFileIPath);
				}

			} catch (CoreException | IOException e) {
				logger.error("Cannot update subgrap-component's property..", e);
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Invalid graph file.");
			}
			finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (fileInputStream != null) {
						fileInputStream.close();
					}
				} catch (IOException e) {
					logger.warn("Exception occurred while closing stream");
				}
			}
		}
		selectedSubjobComponent.getSubJobContainer().put(Constants.SUBJOB_CONTAINER, container);
		return container;
	}

	public void updateContainerAndSubjob(Container subJobContainer, Component selectedSubjobComponent,
			IPath jobFileIPath) {
		int inPort = 0;
		int outPort = 0;
		if (subJobContainer != null && selectedSubjobComponent != null && jobFileIPath != null) {
			  for (Component subComponent : subJobContainer.getUIComponentList()) {
				if (Constants.INPUT_SUBJOB.equalsIgnoreCase(subComponent.getComponentName())) {
					inPort = subComponent.getOutPortCount();
					break;
				  }
			}
			  boolean intializeSchemaMap=true;
			  for (Component subComponent : subJobContainer.getUIComponentList()) {
				if (Constants.OUTPUT_SUBJOB.equalsIgnoreCase(subComponent.getComponentName())) {
					outPort = subComponent.getInPortCount();
					for(Link innerLink:subComponent.getTargetConnections())
					{
						if(StringUtils.equalsIgnoreCase(Constants.STRAIGHTPULL,innerLink.getSource().getCategory())
								&&StringUtils.equalsIgnoreCase(Constants.FILTER,innerLink.getSource().getComponentName())
								&&StringUtils.equalsIgnoreCase(Constants.UNIQUE_SEQUENCE,innerLink.getSource().getComponentName()))
							{
							intializeSchemaMap=false;
							break;
							}
					}
					if(intializeSchemaMap)
					SubjobUtility.INSTANCE.initializeSchemaMapForInputSubJobComponent(selectedSubjobComponent, subComponent);	
					break;
				   }
			}

			selectedSubjobComponent.getProperties().put(Constants.INPUT_PORT_COUNT_PROPERTY, String.valueOf(inPort));
			selectedSubjobComponent.getProperties()
					.put(Constants.OUTPUT_PORT_COUNT_PROPERTY, String.valueOf(outPort));
			updateSubjobType(selectedSubjobComponent, inPort, outPort);
			updateParametersInGrid(selectedSubjobComponent, jobFileIPath);
			linkSubJobToMainGraph(selectedSubjobComponent, subJobContainer);
			selectedSubjobComponent.getProperties().put(Constants.SUBJOB_VERSION,
					subJobContainer.getSubjobVersion());
		}
	}

	public void updateParametersInGrid(Component selectedSubjobComponent, IPath subJobJobFileIPath) {
		Map<String, String> parameterPropertyMap = (Map<String, String>) selectedSubjobComponent.getProperties().get(
				Constants.RUNTIME_PROPERTY_NAME);
		if (parameterPropertyMap == null){
			parameterPropertyMap = new HashMap<String, String>();
		}
		String content = getCurrentEditor().getStringValueFromXMLFile(subJobJobFileIPath);
		CanvasDataAdapter canvasDataAdapter = new CanvasDataAdapter(content);
		canvasDataAdapter.fetchData();
		for (String parameterName : canvasDataAdapter.getParameterList()) {
			if (!parameterPropertyMap.containsKey(parameterName))
				parameterPropertyMap.put(parameterName, "");
		}
		selectedSubjobComponent.getProperties().put(Constants.RUNTIME_PROPERTY_NAME, parameterPropertyMap);
	}


	
	
	
	private void updateSubjobType(Component selectedSubjobComponent, int inPort, int outPort) {

		if (inPort > 0 && outPort > 0)
			selectedSubjobComponent.getProperties().put(Constants.TYPE, Constants.OPERATION);
		else if (inPort > 0 && outPort == 0)
			selectedSubjobComponent.getProperties().put(Constants.TYPE, Constants.OUTPUT);
		else if (inPort == 0 && outPort > 0)
			selectedSubjobComponent.getProperties().put(Constants.TYPE, Constants.INPUT);
		else if (inPort == 0 && outPort == 0)
			selectedSubjobComponent.getProperties().put(Constants.TYPE, Constants.STANDALONE_SUBJOB);

	}

	/**
	 * Link sub graph to main graph.
	 * 
	 * @param selectedSubjobComponent
	 *            the selected subjob component
	 * @param container
	 *            the container
	 */
	private void linkSubJobToMainGraph(Component selectedSubjobComponent, Container container) {
		 for (Component component : container.getUIComponentList()){
			if (Constants.INPUT_SUBJOB.equalsIgnoreCase(component.getComponentName())
					|| Constants.OUTPUT_SUBJOB.equalsIgnoreCase(component.getComponentName()))
				propogateSchemaToSubjob(selectedSubjobComponent, component);
	   }
	}

	public static boolean isFileExistsOnLocalFileSystem(IPath jobFilePath){
		if (ResourcesPlugin.getWorkspace().getRoot().getFile(jobFilePath).exists())
			return true;
		else if (jobFilePath.toFile().exists())
			return true;
		return false;
	}

	public static boolean isFileContainsParameter(IPath jobFileIPath) {
		Matcher matchs = Pattern.compile(Constants.PARAMETER_REGEX).matcher(jobFileIPath.toOSString());
		if (matchs.find()) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
					"Cannot update subjob component property. " + "\nParameter present in file path.");
			return true;
		}
		return false;
	}

	/**
	 * This methods updates version in subjob component and sets status as update-available.
	 *  
	 * @param subjobComponent
	 */
	private boolean updateVersionOfSubjob(Component subJobComponent) {
		IPath jobFileIPath = null;
		String filePath = null;
		Container subJobContainer = null;
		boolean isVersionChanged = false;
		InputStream inputStream=null;
		FileInputStream fileInputStream=null;
		int versionStoredInSubjobComponent = 0;
		if (subJobComponent != null && subJobComponent.getProperties().get(Constants.PATH_PROPERTY_NAME) != null
				&& subJobComponent.getProperties().get(Constants.SUBJOB_VERSION) != null) {
			versionStoredInSubjobComponent = Integer.valueOf(String.valueOf(subJobComponent.getProperties().get(
					Constants.SUBJOB_VERSION)));
			filePath = (String) subJobComponent.getProperties().get(Constants.PATH_PROPERTY_NAME);
			jobFileIPath = new Path(filePath);
			try {
				if (ResourcesPlugin.getWorkspace().getRoot().getFile(jobFileIPath).exists()) {
					 inputStream= ResourcesPlugin.getWorkspace().getRoot().getFile(jobFileIPath).getContents();
					subJobContainer = (Container) CanvasUtils.INSTANCE.fromXMLToObject(inputStream);
				} else {
					if (isFileExistsOnLocalFileSystem(jobFileIPath)) {
						fileInputStream = new FileInputStream(jobFileIPath.toFile());
						subJobContainer = (Container) CanvasUtils.INSTANCE.fromXMLToObject(fileInputStream);
					}
				}
				if (subJobContainer != null && subJobComponent != null && subJobContainer.getSubjobVersion() != versionStoredInSubjobComponent) {
					isVersionChanged = true;
					subJobComponent.getProperties().put(Component.Props.VALIDITY_STATUS.getValue(),
							Constants.UPDATE_AVAILABLE);
				}

			} catch (CoreException |IOException exception) {

				logger.error("Exception occurred while updating Subjob version", exception);
			}
			finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (fileInputStream != null) {
						fileInputStream.close();
					}
				} catch (IOException e) {
					logger.warn("Exception occurred while closing stream");
				}
			}
		}
		return isVersionChanged;
	}
	
	
	public void updateSubjobCompVersion(Component subJobComponent){
		updateSubjobVersionForRefresh(subJobComponent);
	}
	
	/**
	 * @param subJobComponent
	 * @return
	 */
	private boolean updateSubjobVersionForRefresh(Component subJobComponent){
		boolean isVersionChanged = false;
		
		Container container = (Container) subJobComponent.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);	
		
		
		if (subJobComponent != null && subJobComponent.getProperties().get(Constants.PATH_PROPERTY_NAME) != null
				&& subJobComponent.getProperties().get(Constants.SUBJOB_VERSION) != null) {
			
			isVersionChanged = updateVersionOfSubjob(subJobComponent);
			
			for(Object object:container.getChildren()){
				if(object instanceof Component){
					Component component=(Component) object;
					if( !(component.getComponentName().equals(Messages.INPUT_SUBJOB_COMPONENT)) && 
							!(component.getComponentName().equals(Messages.OUTPUT_SUBJOB_COMPONENT))){
						if(Constants.SUBJOB_COMPONENT.equals(component.getComponentName())){
							isVersionChanged = updateSubjobVersionForRefresh(component);
						}
						
						if(isVersionChanged){
							subJobComponent.getProperties().put(Component.Props.VALIDITY_STATUS.getValue(), Constants.UPDATE_AVAILABLE);
							PropertyToolTipInformation information = createPropertyToolTipInformation(Messages.SUBJOB_REFRESH_INFO, Constants.SHOW_TOOLTIP);
							if(subJobComponent.getTooltipInformation() != null){
								subJobComponent.getTooltipInformation().put(Constants.SUBJOB_TOOLTIP_INFO, information);
							}
						}
					}
				}
			}
		}
		
		return isVersionChanged;
	}

	public PropertyToolTipInformation createPropertyToolTipInformation(String message,String showHide){
		  PropertyToolTipInformation propertyToolTipInformation= new PropertyToolTipInformation(Constants.ISSUE_PROPERTY_NAME, showHide, 
					Constants.TOOLTIP_DATATYPE);
		  propertyToolTipInformation.setPropertyValue(message);
		  return propertyToolTipInformation;
	  }
	
	/**
	 * This method set continuousSchema propagation flag to true until it encounters transform or union All component.
	 * @param component through which continuous propagation starts. 
	 */
	public void setFlagForContinuousSchemaPropogation(Component component) {
		for(Link link:component.getSourceConnections())
		{
			Component nextComponent=link.getTarget();
			
			while(StringUtils.equalsIgnoreCase(nextComponent.getCategory(), Constants.STRAIGHTPULL)
					||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.FILTER)	
					 ||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.UNIQUE_SEQUENCE)
					 ||nextComponent instanceof SubjobComponent
					 ||nextComponent instanceof OutputSubjobComponent
					)
			{
				if(StringUtils.equalsIgnoreCase(Constants.UNION_ALL,nextComponent.getComponentName()))
				{
					if(!SubjobUtility.INSTANCE.isUnionAllInputSchemaInSync(nextComponent))
					{	
					nextComponent.getProperties().put(Constants.IS_UNION_ALL_COMPONENT_SYNC,Constants.FALSE);
					((ComponentEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
					break;
					}
					else
					{	
					nextComponent.getProperties().put(Constants.IS_UNION_ALL_COMPONENT_SYNC,Constants.TRUE);	
					((ComponentEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
					}
				}
				Schema schema=(Schema)nextComponent.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
				if(schema==null)
				schema=new Schema();	
				ComponentsOutputSchema outputSchema=SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
				if(schema.getGridRow()==null)
				{
					List<GridRow> gridRows=new ArrayList<>();
					schema.setGridRow(gridRows);
				}	
				schema.getGridRow().clear();
				schema.getGridRow().addAll(outputSchema.getBasicGridRowsOutputFields());
				if(!StringUtils.equalsIgnoreCase(Constants.SUBJOB_COMPONENT_CATEGORY, nextComponent.getCategory()))
				nextComponent.getProperties().put(Constants.SCHEMA_PROPERTY_NAME,schema);
				nextComponent.setContinuousSchemaPropogationAllow(true);
				if(nextComponent instanceof SubjobComponent)
				{	
					Container container=(Container)nextComponent.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
					for(Component subjobComponent:container.getUIComponentList())
					{
						if(subjobComponent instanceof InputSubjobComponent)
						{
							SubjobUtility.INSTANCE.initializeSchemaMapForInputSubJobComponent(subjobComponent,nextComponent);
							setFlagForContinuousSchemaPropogation(subjobComponent);
							break;
						}
					}
					((ComponentEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
				}
				else if(nextComponent instanceof OutputSubjobComponent)
				{
					Component subJobComponent=(Component)nextComponent.getSubJobContainer().get(Constants.SUBJOB_COMPONENT);
					if(subJobComponent!=null)
					SubjobUtility.INSTANCE.initializeSchemaMapForInputSubJobComponent(subJobComponent, nextComponent);
					setFlagForContinuousSchemaPropogation(subJobComponent);
					
				}	
				if(!nextComponent.getSourceConnections().isEmpty())
				{
				   if(nextComponent.getSourceConnections().size()==1)
			    	{
				     if(nextComponent instanceof SubjobComponent)
				     {
					   if(!SubjobUtility.INSTANCE.checkIfSubJobHasTransformOrUnionAllComponent(nextComponent))
					    {
						nextComponent=nextComponent.getSourceConnections().get(0).getTarget();	
					    }
					   else
					   {
						((ComponentEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();   
						break;   
					   }
				     }	
				    else
				    {
				    nextComponent=nextComponent.getSourceConnections().get(0).getTarget();
				    }
				   }
			       else
				   {
					setFlagForContinuousSchemaPropogation(nextComponent);
					break;
				   }
				}
				else
				break;
			}
			if(StringUtils.equalsIgnoreCase(nextComponent.getCategory(),Constants.TRANSFORM))
			{
				nextComponent.setContinuousSchemaPropogationAllow(true);
				((ComponentEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
			}
		}
	}
}