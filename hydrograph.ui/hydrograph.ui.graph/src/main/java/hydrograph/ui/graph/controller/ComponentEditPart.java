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

 
package hydrograph.ui.graph.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.component.config.Policy;
import hydrograph.ui.common.component.config.Property;
import hydrograph.ui.common.datastructures.tooltip.PropertyToolTipInformation;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.figure.ComponentBorder;
import hydrograph.ui.graph.figure.ComponentFigure;
import hydrograph.ui.graph.figure.ELTFigureConstants;
import hydrograph.ui.graph.figure.PortFigure;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.ComponentLabel;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.processor.DynamicClassProcessor;
import hydrograph.ui.graph.propertywindow.ELTPropertyWindow;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.graph.utility.SubJobUtility;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * The Class ComponentEditPart.
 * @author Bitwise
 */
public class ComponentEditPart extends AbstractGraphicalEditPart implements NodeEditPart, PropertyChangeListener {
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ComponentEditPart.class);
	private static final String PHASE="phase";
	private static final String BATCH="batch";
	private Font componentLabelFont;

	/**
	 * Upon activation, attach to the model element as a property change
	 * listener.
	 */
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			((Component) getModel()).addPropertyChangeListener(this);
			((Component) getModel()).setComponentEditPart(this);
			
		}
	
		backwardCompatibilityForChangingPhaseToBatch();
		backwardCompatibilityForLoadingComponentId();
		
		
	}
    
	/*
     * Refreshes the subjob container when job is load into memory.
     */
	private void reloadSubjobContainer() {
		
		IPath subJobFilePath = null;
		Container container = null;
		InputStream inputStream =null;
		FileInputStream fileInputStream=null;
		Component subJobComponent =(Component) getModel();
		subJobFilePath=new Path(((String)subJobComponent.getProperties().get(Constants.JOB_PATH)));
		
	try {
		if (ResourcesPlugin.getWorkspace().getRoot().getFile(subJobFilePath).exists()) {
				inputStream = ResourcesPlugin.getWorkspace().getRoot().getFile(subJobFilePath).getContents();
			container = (Container)CanvasUtils.INSTANCE.fromXMLToObject(inputStream);
		} else if (subJobFilePath !=null && isFileExistsOnLocalFileSystem(subJobFilePath)) {
			fileInputStream = new FileInputStream(subJobFilePath.toFile());
			container = (Container) CanvasUtils.INSTANCE.fromXMLToObject(fileInputStream);
		}
		 
				
		subJobComponent.getSubJobContainer().put(Constants.SUBJOB_CONTAINER, container);
				
			for (Component component : container.getUIComponentList()){
			
				if(Constants.OUTPUT_SUBJOB.equalsIgnoreCase(component.getComponentName())){
					//output connector
					subJobComponent.getSubJobContainer().put(Constants.OUTPUT_SUBJOB, component);	
					component.getSubJobContainer().put(Constants.SUBJOB_COMPONENT, subJobComponent);
				}
				
			}
					
		} catch (CoreException coreException) {
			logger.error(coreException.getMessage());
		} catch (FileNotFoundException fileNotFoundExpion) {
			logger.error(fileNotFoundExpion.getMessage());
		}
	}

	// Temp method for loading component's Id.
	private void backwardCompatibilityForLoadingComponentId() {
		if(StringUtils.isBlank(getCastedModel().getComponentId())){
			getCastedModel().setComponentId(getCastedModel().getComponentLabel().getLabelContents());
		}
	}

	private void backwardCompatibilityForChangingPhaseToBatch() {
		if(getCastedModel().getProperties().get(PHASE)!=null)
		{
			getCastedModel().getProperties().put(BATCH,getCastedModel().getProperties().remove(PHASE));
		}
		
	}
	
	/**
	 * Upon deactivation, detach from the model element as a property change
	 * listener.
	 */
	@Override
	public void deactivate() {
		if (isActive()) {
			if(componentLabelFont!=null){
				componentLabelFont.dispose();
			}
			super.deactivate();
			((Component) getModel()).removePropertyChangeListener(this);
			getComponentFigure().disposeFonts();
		}
	}

	@Override
	protected void createEditPolicies() {
		String componentName = DynamicClassProcessor.INSTANCE.getClazzName(getModel().getClass());
		try {
			for (hydrograph.ui.common.component.config.Component component : XMLConfigUtil.INSTANCE.getComponentConfig()) {
				if (component.getName().equalsIgnoreCase(componentName)) {
					applyGeneralPolicy(component);
				}
			}
		} catch (Exception e) {
			logger.error("Error while creating edit policies", e);
		} 
	}

	/**
	 * Apply general policy.
	 * 
	 * @param component
	 *            the component
	 * @throws Exception
	 *             the exception
	 */
	public void applyGeneralPolicy(
			hydrograph.ui.common.component.config.Component component)
			throws Exception {

		for (Policy generalPolicy : XMLConfigUtil.INSTANCE.getPoliciesForComponent(component)) {
			try {
				AbstractEditPolicy editPolicy = (AbstractEditPolicy) Class.forName(generalPolicy.getValue()).newInstance();
				installEditPolicy(generalPolicy.getName(), editPolicy);
			} catch (ClassNotFoundException| InstantiationException | IllegalAccessException exception) {
				logger.error("Failed to apply policies", exception);
				throw exception;
			}
		}
	}

	@Override
	protected IFigure createFigure() {
		IFigure figure = createFigureForModel();
		figure.setOpaque(true); // non-transparent figure
    	updateSubjobComponent(figure);
		return figure;
	}

	
	/**
	 * updates version and validity status icon of subjob component
	 * @param figure
	 */
	public void updateSubjobComponent(IFigure figure) {
		LinkedHashMap<String, Object> properties = getCastedModel().getProperties();
		if (StringUtils.equals(getCastedModel().getComponentName(), Constants.SUBJOB_COMPONENT)) {
			
			if(null==getCastedModel().getSubJobContainer().get(Constants.SUBJOB_CONTAINER)&&
					getCastedModel().getSubJobContainer().size()==0){
			if(StringUtils.isNotBlank((String)(getCastedModel().getProperties().get(Constants.JOB_PATH)))){
			
				reloadSubjobContainer();
			
			}
			}
			SubJobUtility graphUtility=new SubJobUtility();
			graphUtility.updateSubjobCompVersion(getCastedModel());
		}
		String status = (String) properties.get(Component.Props.VALIDITY_STATUS.getValue());
		((ComponentFigure)figure).setPropertyStatus(status);
	}

	private IFigure createFigureForModel() {
		String componentName = DynamicClassProcessor.INSTANCE
				.getClazzName(getModel().getClass());
		
		String canvasIconPath = XMLConfigUtil.INSTANCE.getComponent(componentName).getCanvasIconPath();
		
		String label = (String) getCastedModel().getPropertyValue(Component.Props.NAME_PROP.getValue());
		String acronym = XMLConfigUtil.INSTANCE.getComponent(componentName).getAcronym();
		return new ComponentFigure(getCastedModel(), canvasIconPath, label, acronym, getCastedModel().getProperties());
	}

	public Component getCastedModel() {
		return (Component) getModel();
	}

	@Override
	protected List getModelChildren() {
		// return a list of models
		return ((Component)getModel()).getChildren();
	}
	
	/**
	 * Map connection anchor to terminal.
	 * 
	 * @param c
	 *            the c
	 * @return the string
	 */
	public final String mapConnectionAnchorToTerminal(ConnectionAnchor c) {

		return getComponentFigure().getConnectionAnchorName(c);
	}

	
	@Override
	protected List<Link> getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}

	@Override
	protected List<Link> getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {

		Link wire = (Link) connection.getModel();
		return getComponentFigure().getConnectionAnchor(
				wire.getSourceTerminal());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getComponentFigure().getSourceConnectionAnchorAt(pt);
		
	}

	protected ComponentFigure getComponentFigure() {
		return (ComponentFigure) getFigure();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {

		Link wire = (Link) connection.getModel();
		return getComponentFigure().getConnectionAnchor(
				wire.getTargetTerminal());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getComponentFigure().getTargetConnectionAnchorAt(pt);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		String prop = evt.getPropertyName();
		if (Component.Props.SIZE_PROP.equalsTo(prop)
				|| Component.Props.LOCATION_PROP.equalsTo(prop) || Component.Props.EXECUTION_STATUS.equalsTo(prop)) {
			refreshVisuals();
		} else if (Component.Props.OUTPUTS.equalsTo(prop)) {
			refreshSourceConnections();
		} else if (Component.Props.INPUTS.equalsTo(prop)) {
			refreshTargetConnections();
		}
	}

	@Override
	protected void refreshVisuals() {
		// notify parent container of changed position & location
		// if this line is removed, the XYLayoutManager used by the parent
		// container
		// (the Figure of the ShapesDiagramEditPart), will not know the bounds
		// of this figure and will not draw it correctly.
		Component component = getCastedModel();
		ComponentFigure componentFigure = getComponentFigure();
				
		ComponentBorder componentBorder = new ComponentBorder(componentFigure.getBorderColor(), 0, componentFigure.getComponentLabelMargin());
		componentFigure.setBorder(componentBorder);
		List<AbstractGraphicalEditPart> childrenEditParts = getChildren();
		if (!childrenEditParts.isEmpty()){
			ComponentLabelEditPart lLabelEditPart = null;
			PortEditPart portEditPart = null;
			for(AbstractGraphicalEditPart part:childrenEditParts)
			{
				if(part instanceof ComponentLabelEditPart){
					lLabelEditPart = (ComponentLabelEditPart) part;
					lLabelEditPart.refreshVisuals();
				}
				
				if(part instanceof PortEditPart){
					portEditPart = (PortEditPart) part;
					portEditPart.refreshVisuals();
				}
			}
			
		}
		
		Rectangle bounds = new Rectangle(getCastedModel().getLocation(),
				getCastedModel().getSize());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), bounds);
		
		componentFigure.updateComponentStatus(getCastedModel().getStatus());
		componentFigure.repaint();
		 
		if(component.getTooltipInformation() == null){
			addTooltipInfoToComponent();
		}
		
		component.updateTooltipInformation();
		componentFigure.setPropertyToolTipInformation(component.getTooltipInformation());
	}

	private void addTooltipInfoToComponent() {
		String componentName = DynamicClassProcessor.INSTANCE.getClazzName(getModel().getClass());
		hydrograph.ui.common.component.config.Component components = XMLConfigUtil.INSTANCE.getComponent(componentName);
		//attach tooltip information to component
		Map<String,PropertyToolTipInformation> tooltipInformation = new LinkedHashMap<>();
		for(Property property : components.getProperty()){
			tooltipInformation.put(property.getName(),new PropertyToolTipInformation(property.getName(), property.getShowAsTooltip().value(), property.getTooltipDataType().value()));
		}
		getCastedModel().setTooltipInformation(tooltipInformation);
	}

	@Override
	public void performRequest(Request req) {
		
		// Opens Property Window only on Double click.
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			ComponentFigure componentFigure=this.getComponentFigure();
			componentFigure.terminateToolTipTimer();
			ELTPropertyWindow propertyWindow = new ELTPropertyWindow(getModel());
			propertyWindow.open();
			
			if(Constants.SUBJOB_COMPONENT.equalsIgnoreCase(getCastedModel().getComponentName())){
				SubJobUtility subJobUtility=new SubJobUtility();
				subJobUtility.updateSubjobPropertyAndGetSubjobContainer(this,null,null,true);
			} 
			if(propertyWindow.isPropertyChanged()){
				updateSubjobVersion();
			}
			updateComponentView(propertyWindow);
			super.performRequest(req);
		}
	}

	private void refreshComponentStatusOfAllComponent() {
		for(Component component:getCastedModel().getParent().getUIComponentList()){
			if(component != null){
			((ComponentEditPart)component.getComponentEditPart()).updateComponentStatus();
			}
		}
	}

	private void updateSubjobVersion() {
		Component currentComponent=getCastedModel();
		if(currentComponent!=null && currentComponent.getParent().isCurrentGraphSubjob()){
			currentComponent.getParent().updateSubjobVersion();
		}
	}

	public void changePortSettings() {
		if(getCastedModel().isChangeInPortsCntDynamically()){
			setInPortsCountDynamically();
		}
		if(getCastedModel().isChangeOutPortsCntDynamically()){
			setOutPortsCountDynamically();
		}
		if(getCastedModel().isChangeUnusedPortsCntDynamically()){
			setUnusedPortsCountDynamically();
		}
		refresh();
		
		adjustExistingPorts();
	}


	private void setInPortsCountDynamically(){
		int prevInPortCount = getCastedModel().getInPortCount();
		int outPortCount = 0, newInPortCount = 0;
		if (getCastedModel().getProperties().get("inPortCount") != null) {
			newInPortCount = Integer.parseInt((String.valueOf(getCastedModel().getProperties().get("inPortCount"))));
		}
		if (getCastedModel().getProperties().get("outPortCount") != null) {
			outPortCount = Integer.parseInt(String.valueOf(getCastedModel().getProperties().get("outPortCount")));
		}
		if(prevInPortCount != newInPortCount){
		int inPortCountToBeApplied = newInPortCount!=prevInPortCount ? newInPortCount : prevInPortCount;

		ComponentFigure compFig = (ComponentFigure)getFigure();
		compFig.setHeight(inPortCountToBeApplied, outPortCount);

		Dimension newSize = new Dimension(compFig.getWidth(), compFig.getHeight()+getCastedModel().getComponentLabelMargin());
		getCastedModel().setSize(newSize);
		refresh();


			if(prevInPortCount < newInPortCount){
				//Increment the ports
				getCastedModel().changeInPortCount(newInPortCount);
				adjustExistingPorts();
				getCastedModel().incrementLeftSidePorts(newInPortCount, prevInPortCount);

			}else{
				//decrement the ports
				List<String> portsToBeRemoved = populateInPortsToBeRemoved(prevInPortCount, newInPortCount);
				getCastedModel().decrementPorts(portsToBeRemoved);
				compFig.decrementAnchors(portsToBeRemoved);
				getCastedModel().changeInPortCount(newInPortCount);

			}
		}	
	}
	

	private void setOutPortsCountDynamically(){
		int prevOutPortCount = getCastedModel().getOutPortCount();
		int inPortCount = 0, newOutPortCount = 0;

		if (getCastedModel().getProperties().get("inPortCount") != null)
		{
			inPortCount = Integer.parseInt(String.valueOf(getCastedModel().getProperties().get("inPortCount")));
		}

		if (getCastedModel().getProperties().get("outPortCount") != null)
		{
			newOutPortCount = Integer.parseInt(String.valueOf(getCastedModel().getProperties().get("outPortCount")));
		}
		if(prevOutPortCount != newOutPortCount){
		int outPortCountToBeApplied = newOutPortCount!=prevOutPortCount ? newOutPortCount : prevOutPortCount;

		ComponentFigure compFig = (ComponentFigure)getFigure();
		compFig.setHeight(inPortCount, outPortCountToBeApplied);

		Dimension newSize = new Dimension(compFig.getWidth(), compFig.getHeight()+getCastedModel().getComponentLabelMargin());
		getCastedModel().setSize(newSize);
		refresh();
		
		

			if(prevOutPortCount < newOutPortCount){
				//Increment the ports
				getCastedModel().changeOutPortCount(newOutPortCount);
				adjustExistingPorts();
				getCastedModel().incrementRightSidePorts(newOutPortCount, prevOutPortCount);

			}else{
				//decrement the ports
				List<String> portsToBeRemoved = populateOutPortsToBeRemoved(prevOutPortCount, newOutPortCount);
				getCastedModel().decrementPorts(portsToBeRemoved);
				compFig.decrementAnchors(portsToBeRemoved);
				getCastedModel().changeOutPortCount(newOutPortCount);

			}
		}
	}
	
	private void setUnusedPortsCountDynamically(){
		
		int prevUnusedportCount = getCastedModel().getUnusedPortCount();
		int newUnunsedPortCount=0;
			if(getCastedModel().getProperties().get("unusedPortCount")!=null)
			{
			 newUnunsedPortCount=Integer.parseInt((String)getCastedModel().getProperties().get("unusedPortCount"));
			}
			
			if(prevUnusedportCount != newUnunsedPortCount){
		int unusedPortCountToBeApplied = newUnunsedPortCount!=prevUnusedportCount ? newUnunsedPortCount : prevUnusedportCount;		

		ComponentFigure compFig = (ComponentFigure)getFigure();
		compFig.setWidth(unusedPortCountToBeApplied);

		Dimension newSize = new Dimension(compFig.getWidth(), compFig.getHeight()+getCastedModel().getComponentLabelMargin());
		getCastedModel().setSize(newSize);
		refresh();

			if(prevUnusedportCount < newUnunsedPortCount){
				//Increment the ports
				getCastedModel().changeUnusedPortCount(newUnunsedPortCount);
				adjustExistingPorts();
				getCastedModel().incrementBottomSidePorts(newUnunsedPortCount, prevUnusedportCount);
			}else{
				//decrement the ports
				List<String> portsToBeRemoved = populateUnusedPortsToBeRemoved(prevUnusedportCount, newUnunsedPortCount);
				getCastedModel().decrementPorts(portsToBeRemoved);
				compFig.decrementAnchors(portsToBeRemoved);
				getCastedModel().changeUnusedPortCount(newUnunsedPortCount);

			}
		}
	}
	
	private void adjustExistingPorts(){
		List<AbstractGraphicalEditPart> childrenEditParts = getChildren();
		PortEditPart portEditPart = null;
		for(AbstractGraphicalEditPart part:childrenEditParts)
		{
			if(part instanceof PortEditPart){ 
				portEditPart = (PortEditPart) part;
				portEditPart.adjustPortFigure(getCastedModel().getLocation());
			}
		}
	}
	
	private void selectPorts() {
		ComponentFigure compFig = (ComponentFigure)getFigure();
		List<Figure> childrenFigures = compFig.getChildren();
		if (!childrenFigures.isEmpty()){
			for(Figure figure:childrenFigures)
			{
				if(figure instanceof PortFigure)
					((PortFigure) figure).selectPort();
			}
		}
	}

	
	private List<String> populateInPortsToBeRemoved(int prevCount, int newCount){
		int keyCount = newCount;
		List<String> oldKeys = new ArrayList<>();
		List<String> oldInKeys = new ArrayList<>();
		List<String> newInKeys = new ArrayList<>();
		List<String> inKeysToBeRemoved = new ArrayList<>();

		oldKeys.addAll(getCastedModel().getPorts().keySet());

		for (String key : oldKeys) {
			if(key.contains(Constants.INPUT_SOCKET_TYPE))
				oldInKeys.add(key);
		}

		for(int i=0; i<keyCount; i++)
		{
			newInKeys.add(Constants.INPUT_SOCKET_TYPE+i);
		}
		for (String key : oldInKeys) {
			if(!newInKeys.contains(key))
				inKeysToBeRemoved.add(key);
		}
		return inKeysToBeRemoved;
	}
	
	private List<String> populateOutPortsToBeRemoved(int prevCount, int newCount){
		int keyCount = newCount;
		List<String> oldKeys = new ArrayList<>();
		List<String> oldOutKeys = new ArrayList<>();
		List<String> newOutKeys = new ArrayList<>();
		List<String> outKeysToBeRemoved = new ArrayList<>();

		oldKeys.addAll(getCastedModel().getPorts().keySet());

		for (String key : oldKeys) {
			if(key.contains(Constants.OUTPUT_SOCKET_TYPE))
				oldOutKeys.add(key);
		}

		for(int i=0; i<keyCount; i++)
		{
			newOutKeys.add(Constants.OUTPUT_SOCKET_TYPE+i);
		}
		for (String key : oldOutKeys) {
			if(!newOutKeys.contains(key))
				outKeysToBeRemoved.add(key);
		}
		return outKeysToBeRemoved;
	}
	
	private List<String> populateUnusedPortsToBeRemoved(int prevCount, int newCount){
		int keyCount = newCount;
		List<String> oldKeys = new ArrayList<>();
		List<String> oldUnusedKeys = new ArrayList<>();
		List<String> newUnusedKeys = new ArrayList<>();
		List<String> unusedKeysToBeRemoved = new ArrayList<>();

		oldKeys.addAll(getCastedModel().getPorts().keySet());

		for (String key : oldKeys) {
			if(key.contains(Constants.UNUSED_SOCKET_TYPE))
				oldUnusedKeys.add(key);
		}

		for(int i=0; i<keyCount; i++)
		{
			newUnusedKeys.add(Constants.UNUSED_SOCKET_TYPE+i);
		}
		for (String key : oldUnusedKeys) {
			if(!newUnusedKeys.contains(key))
				unusedKeysToBeRemoved.add(key);
		}
		return unusedKeysToBeRemoved;
	}
	
	private void adjustComponentFigure(Component component, ComponentFigure componentFigure){
		Dimension d = null;
		//an extra space has been added at the end of the component's name for handling component renaming issue#1370.
		String label = (String) component.getPropertyValue(Component.Props.NAME_PROP.getValue());
		ComponentLabel componentLabel = component.getComponentLabel();
		if(componentLabelFont==null){
			componentLabelFont = new Font( Display.getDefault(), ELTFigureConstants.labelFont, 10,
				SWT.NORMAL );
		}
		int labelLength = TextUtilities.INSTANCE.getStringExtents(label, componentLabelFont).width;
		component.setComponentLabel(label);
		if(labelLength >= ELTFigureConstants.compLabelOneLineLengthLimit && !componentFigure.isIncrementedHeight()){
			component.setSize(new Dimension(component.getSize().width, component.getSize().height +ELTFigureConstants.componentOneLineLabelMargin));
			//component.setSize(new Dimension(100, 82));
			componentLabel.setSize(new Dimension(componentLabel.getSize().width, componentLabel.getSize().height +ELTFigureConstants.componentOneLineLabelMargin));
			componentFigure.setIncrementedHeight(true);
			component.setComponentLabelMargin(ELTFigureConstants.componentTwoLineLabelMargin);
			componentFigure.setComponentLabelMargin(ELTFigureConstants.componentTwoLineLabelMargin);
		}else if(labelLength < ELTFigureConstants.compLabelOneLineLengthLimit && componentFigure.isIncrementedHeight()){
			component.setSize(new Dimension(component.getSize().width, component.getSize().height-ELTFigureConstants.componentOneLineLabelMargin));
			componentLabel.setSize(new Dimension(componentLabel.getSize().width, componentLabel.getSize().height -ELTFigureConstants.componentOneLineLabelMargin));
			componentFigure.setIncrementedHeight(false);
			component.setComponentLabelMargin(ELTFigureConstants.componentOneLineLabelMargin);
			componentFigure.setComponentLabelMargin(ELTFigureConstants.componentOneLineLabelMargin);
		}else if(labelLength < ELTFigureConstants.compLabelOneLineLengthLimit ){
			component.setSize(new Dimension(component.getSize().width, component.getSize().height));
		}
		componentFigure.repaint();
		
	}
	
	private void adjustComponentLabelPosition(){
		List<AbstractGraphicalEditPart> childrenEditParts = getChildren();
		for(AbstractGraphicalEditPart part:childrenEditParts)
		{
			if(part instanceof ComponentLabelEditPart){ 
				ComponentLabelEditPart componentLabelEditPart = (ComponentLabelEditPart) part;
				componentLabelEditPart.adjustLabelFigure(getCastedModel().getLocation(), getCastedModel().getSize());
				break;
			}
		}
	}
	/**
	 * Updates the status of a component.
	 */
	public void updateComponentStatus(){
		Component component = this.getCastedModel();
		LinkedHashMap<String, Object> properties = component.getProperties();
		String statusName = Component.Props.VALIDITY_STATUS.getValue();
		if(properties.containsKey(statusName)){
			((ComponentFigure)this.getFigure()).setPropertyStatus((String)properties.get(statusName));
			this.getFigure().repaint();
		}
	}
	
	/**
	 * Updates/refresh's the status, label position, port settings and tool tip information of a component.
	 * 
	 * @param eltPropertyWindow ELTPropertyWindow instance is passed
	 */
	public void updateComponentView(ELTPropertyWindow eltPropertyWindow)
	{
		String currentStatus=(String) getCastedModel().getProperties().get(Component.Props.VALIDITY_STATUS.getValue());
		adjustComponentFigure(getCastedModel(), getComponentFigure());
		
		changePortSettings();
		
		adjustComponentLabelPosition();
		
		if(!StringUtils.equals(Constants.UPDATE_AVAILABLE,currentStatus)){
			updateComponentStatus();			
		}
		refresh();
		
		adjustExistingPorts();

		selectPorts();
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof ELTGraphicalEditor){
			ELTGraphicalEditor eltGraphicalEditor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(eltPropertyWindow.isPropertyChanged()){
				eltGraphicalEditor.setDirty(true);
				getCastedModel().updateTooltipInformation();
			}
		}
		refreshComponentStatusOfAllComponent();
	}
	
	public boolean isFileExistsOnLocalFileSystem(IPath jobFilePath){
		if (ResourcesPlugin.getWorkspace().getRoot().getFile(jobFilePath).exists())
			return true;
		else if (jobFilePath.toFile().exists())
			return true;
		return false;
	}

}
