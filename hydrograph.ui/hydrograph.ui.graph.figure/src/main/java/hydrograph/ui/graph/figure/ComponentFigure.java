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

package hydrograph.ui.graph.figure;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.datastructures.tooltip.PropertyToolTipInformation;
import hydrograph.ui.common.interfaces.tooltip.ComponentCanvas;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.common.validator.Validator;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Component.ValidityStatus;
import hydrograph.ui.graph.model.ComponentExecutionStatus;
import hydrograph.ui.graph.model.PortAlignmentEnum;
import hydrograph.ui.graph.model.PortDetails;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.utility.SubjobUtility;
import hydrograph.ui.tooltip.tooltips.ComponentTooltip;

/**
 * The Class ComponentFigure.
 * 
 * @author Bitwise
 * 
 */
public class ComponentFigure extends Figure implements Validator {
	

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ComponentFigure.class);

	private final XYLayout layout;
	private int height = 0, width = 0;

	private HashMap<String, FixedConnectionAnchor> connectionAnchors;
	private List<FixedConnectionAnchor> inputConnectionAnchors;
	private List<FixedConnectionAnchor> outputConnectionAnchors;

	private int totalPortsAtLeftSide = 0, totalPortsAtRightSide = 0, totalPortsAtBottonSide = 0;

	private Color borderColor;
	private Color selectedBorderColor;
	private Color componentColor;
	private Color selectedComponentColor;
	private boolean incrementedHeight;
	private int componentLabelMargin;
	private String canvasIconPath;
    private Component component;
	private String propertyStatus;

	private Map<String, PropertyToolTipInformation> propertyToolTipInformation;
	private ComponentCanvas componentCanvas;
	private ComponentTooltip componentToolTip;
	private org.eclipse.swt.graphics.Rectangle componentBounds;
	private static final int TOOLTIP_SHOW_DELAY = 800;
	private Display display;
	private Runnable timer;

	private String acronym;
	private ComponentExecutionStatus componentStatus;
	private LinkedHashMap<String, Object> componentProperties;
	private Font labelFont, acronymFont;

	private Image canvasIcon;

	private Image statusImage;

	private Image compStatusImage;

	/**
	 * Instantiates a new component figure.
	 * 
	 * @param portDetails
	 *            the port details
	 * @param cIconPath
	 *            the canvas icon path
	 * @param label
	 * 			  Label to be displayed on component
	 * @param acronym
	 * 			  Acronym to be displayed on component
	 * @param linkedHashMap
	 * 			  the properties of components
	 */
	
		public ComponentFigure(Component component, String cIconPath, String label, String acronym,
				LinkedHashMap<String, Object> properties) {
		this.canvasIconPath = XMLConfigUtil.CONFIG_FILES_PATH + cIconPath;
		this.acronym = acronym;
		this.componentProperties = properties;
		layout = new XYLayout();
		setLayoutManager(layout);

		labelFont = new Font(Display.getDefault(), ELTFigureConstants.labelFont, 9, SWT.NORMAL);
		int labelLength = TextUtilities.INSTANCE.getStringExtents(label, labelFont).width;

		if (labelLength >= ELTFigureConstants.compLabelOneLineLengthLimitForText) {
			this.componentLabelMargin = ELTFigureConstants.componentTwoLineLabelMargin;
			this.incrementedHeight = true;
		} else if (labelLength < ELTFigureConstants.compLabelOneLineLengthLimitForText) {
			this.componentLabelMargin = ELTFigureConstants.componentOneLineLabelMargin;
			this.incrementedHeight = false;
		}

		canvasIcon = new Image(null, canvasIconPath);
		this.component=component;
		connectionAnchors = new HashMap<String, FixedConnectionAnchor>();
		inputConnectionAnchors = new ArrayList<FixedConnectionAnchor>();
		outputConnectionAnchors = new ArrayList<FixedConnectionAnchor>();

		setInitialColor();
		setComponentColorAndBorder();

		for (PortDetails pDetail : component.getPortDetails()) {
			setPortCount(pDetail);
			setHeight(totalPortsAtLeftSide, totalPortsAtRightSide);
			setWidth(totalPortsAtBottonSide);
		}

		acronymFont = new Font(Display.getDefault(), ELTFigureConstants.labelFont, 8, SWT.BOLD);
		setFont(acronymFont);
		setForegroundColor(org.eclipse.draw2d.ColorConstants.black);

		componentCanvas = getComponentCanvas();
		attachMouseListener();
	}

	private ComponentCanvas getComponentCanvas() {

		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof ComponentCanvas)
			return (ComponentCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
		else
			return null;
	}
   
	private void setInitialColor() {
		componentColor = CustomColorRegistry.INSTANCE.getColorFromRegistry( ELTColorConstants.LIGHT_GREY_RGB[0], ELTColorConstants.LIGHT_GREY_RGB[1], ELTColorConstants.LIGHT_GREY_RGB[2]);
		borderColor = CustomColorRegistry.INSTANCE.getColorFromRegistry( ELTColorConstants.DARK_GREY_RGB[0], ELTColorConstants.DARK_GREY_RGB[1], ELTColorConstants.DARK_GREY_RGB[2]);
		selectedComponentColor = CustomColorRegistry.INSTANCE.getColorFromRegistry( ELTColorConstants.COMPONENT_BACKGROUND_SELECTED_RGB[0], ELTColorConstants.COMPONENT_BACKGROUND_SELECTED_RGB[1], ELTColorConstants.COMPONENT_BACKGROUND_SELECTED_RGB[2]);
		selectedBorderColor = CustomColorRegistry.INSTANCE.getColorFromRegistry( ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[0], ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[1], ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[2]);
	}

	/**
	 * Sets the component color and border.
	 */
	public void setComponentColorAndBorder() {
		setBackgroundColor(componentColor);
		setBorder(new ComponentBorder(borderColor, 0, componentLabelMargin));
	}

	/**
	 * Sets the selected component color and border.
	 */
	public void setSelectedComponentColorAndBorder() {
		setBackgroundColor(selectedComponentColor);
		setBorder(new ComponentBorder(selectedBorderColor, 1, componentLabelMargin));
	}

	private void setPortCount(PortDetails portDetails) {
		if(PortAlignmentEnum.LEFT.equals(portDetails.getPortAlignment())){
			totalPortsAtLeftSide = totalPortsAtLeftSide + portDetails.getNumberOfPorts();
		} else if (PortAlignmentEnum.RIGHT.equals(portDetails.getPortAlignment())) {
			totalPortsAtRightSide = totalPortsAtRightSide + portDetails.getNumberOfPorts();
		} else if (PortAlignmentEnum.BOTTOM.equals(portDetails.getPortAlignment())) {
			totalPortsAtBottonSide = totalPortsAtBottonSide + portDetails.getNumberOfPorts();
		}

	}

	/**
	 * 
	 * Set component height
	 * 
	 * @param totalPortsofInType
	 * @param totalPortsOfOutType
	 */
	public void setHeight(int totalPortsofInType, int totalPortsOfOutType) {
		int heightFactor = totalPortsofInType > totalPortsOfOutType ? totalPortsofInType : totalPortsOfOutType;
		this.height = (heightFactor + 1) * 27;
	}

	/**
	 * 
	 * Get component Height
	 * 
	 * @return int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * Set component width
	 * 
	 * @param totalPortsofUnusedType
	 */
	public void setWidth(int totalPortsOfUnusedType) {
		int widthFactor = totalPortsOfUnusedType;
		this.width = 100;
		if (widthFactor > 1)
			this.width = (widthFactor + 1) * 33;
	}

	/**
	 * 
	 * Get component width
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * Get component border color
	 * 
	 * @return {@link Color}
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * 
	 * Set component border color
	 * 
	 * @param borderColor
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	
	/**
	 * 
	 * Set connection anchor
	 * 
	 * @param fCAnchor
	 */
	public void setAnchors(FixedConnectionAnchor fCAnchor) {
		connectionAnchors.put(fCAnchor.getTerminal(), fCAnchor);
		if (PortAlignmentEnum.RIGHT.value().equalsIgnoreCase(fCAnchor.getAlignment()) || PortAlignmentEnum.BOTTOM.value().equalsIgnoreCase(fCAnchor.getAlignment()))
			outputConnectionAnchors.add(fCAnchor);
		else
			inputConnectionAnchors.add(fCAnchor);
	}

	/**
	 * 
	 * Decrement anchors
	 * 
	 * @param portsToBeRemoved
	 */
	public void decrementAnchors(List<String> portsToBeRemoved) {

		for (String portRemove : portsToBeRemoved) {
			connectionAnchors.remove(portRemove);
		}

		for (String portRemove : portsToBeRemoved) {
			for (Iterator<FixedConnectionAnchor> iterator = outputConnectionAnchors.iterator(); iterator.hasNext();) {
				FixedConnectionAnchor fca = iterator.next();
				if (fca.getTerminal().equals(portRemove)) {
					// Remove the current element from the iterator and the list.
					iterator.remove();
				}
			}
		}
		for (String portRemove : portsToBeRemoved) {
			for (Iterator<FixedConnectionAnchor> iterator = inputConnectionAnchors.iterator(); iterator.hasNext();) {
				FixedConnectionAnchor fca = iterator.next();
				if (fca.getTerminal().equals(portRemove)) {
					// Remove the current element from the iterator and the list.
					iterator.remove();
				}
			}
		}
	}

	@Override
	protected void paintFigure(Graphics graphics) {

		Rectangle r = getBounds().getCopy();
		graphics.translate(r.getLocation());

		Rectangle q = new Rectangle(4, 4 + componentLabelMargin, r.width - 8, r.height - 8 - componentLabelMargin);
		graphics.fillRoundRectangle(q, 5, 5);

		graphics.drawImage(canvasIcon, new Point(q.width / 2 - 16, q.height / 2 + componentLabelMargin - 11));
		drawPropertyStatus(graphics);
		if((StringUtils.equalsIgnoreCase(component.getCategory(), Constants.TRANSFORM)
			&& !StringUtils.equalsIgnoreCase(component.getComponentName(), Constants.FILTER)
			&& !StringUtils.equalsIgnoreCase(component.getComponentName(), Constants.UNIQUE_SEQUENCE))){
			PropertyToolTipInformation propertyToolTipInformation;	
			if(component.isContinuousSchemaPropogationAllow()){
			    drawSchemaPropogationInfoImageIfSchemaPropogationBreaks(graphics);
			    propertyToolTipInformation = createPropertyToolTipInformation(
			    		Messages.CONTINUOUS_SCHEMA_PROPAGATION_STOPPED, Constants.SHOW_TOOLTIP);
		    }
			else{
				 propertyToolTipInformation = createPropertyToolTipInformation("", Constants.HIDE_TOOLTIP);
			}
			 component.getTooltipInformation().put(Constants.ISSUE_PROPERTY_NAME,propertyToolTipInformation );
		}
		else if(StringUtils.equalsIgnoreCase(Constants.UNION_ALL,component.getComponentName())){
			PropertyToolTipInformation propertyToolTipInformation;	
			if(component.getProperties().get(Constants.IS_UNION_ALL_COMPONENT_SYNC) != null
					&& StringUtils.equalsIgnoreCase(((String)component.getProperties()
							.get(Constants.IS_UNION_ALL_COMPONENT_SYNC)), Constants.FALSE))		   {
				drawSchemaPropogationInfoImageIfSchemaPropogationBreaks(graphics);
				propertyToolTipInformation =createPropertyToolTipInformation(Messages.INPUTS_SCHEMA_ARE_NOT_IN_SYNC, Constants.SHOW_TOOLTIP);
		   }
		   else{
			   propertyToolTipInformation =createPropertyToolTipInformation("", Constants.HIDE_TOOLTIP);   
			}
		    component.getTooltipInformation().put(Constants.ISSUE_PROPERTY_NAME,propertyToolTipInformation );
		}
		else if(component instanceof SubjobComponent){
			boolean isTransformComponentPresent=SubjobUtility.INSTANCE.checkIfSubJobHasTransformOrUnionAllComponent(component);
			PropertyToolTipInformation propertyToolTipInformation;
			if(isTransformComponentPresent){	
				drawSchemaPropogationInfoImageIfSchemaPropogationBreaks(graphics);
				propertyToolTipInformation = createPropertyToolTipInformation(
						Messages.CONTINUOUS_SCHEMA_PROPAGATION_STOPPED_IN_SUBJOB, Constants.SHOW_TOOLTIP); 
			}
			else{
			  propertyToolTipInformation =createPropertyToolTipInformation("", Constants.HIDE_TOOLTIP);   
			}
			component.getTooltipInformation().put(Constants.ISSUE_PROPERTY_NAME,propertyToolTipInformation );
		}	
		graphics.drawText(acronym, new Point((q.width - (acronym.length()*5))/2, q.height / 2 + componentLabelMargin - 23));
       
		if (componentProperties != null && componentProperties.get(StringUtils.lowerCase(Constants.BATCH)) != null) {
			if (String.valueOf(componentProperties.get(StringUtils.lowerCase(Constants.BATCH))).length() > 2){
				graphics.drawText(
						StringUtils.substring(
								String.valueOf(componentProperties.get(StringUtils.lowerCase(Constants.BATCH))), 0, 2)
								+ "..", new Point(q.width - 16, q.height+ getComponentLabelMargin()-20));
			}
			else{
				graphics.drawText(String.valueOf(componentProperties.get(StringUtils.lowerCase(Constants.BATCH))),
						new Point(q.width - 14, q.height+ getComponentLabelMargin()-20));
			}
		}
		
		trackExecution(graphics);
	}
 
	  private PropertyToolTipInformation createPropertyToolTipInformation(String message,String showHide){
		  PropertyToolTipInformation propertyToolTipInformation= new PropertyToolTipInformation(Constants.ISSUE_PROPERTY_NAME, showHide, 
					Constants.TOOLTIP_DATATYPE);
		  propertyToolTipInformation.setPropertyValue(message);
		  return propertyToolTipInformation;
	  }
  
  private void trackExecution(Graphics graphics) {
		Rectangle rectangle = getBounds().getCopy();
		if(componentStatus!=null){
			if (componentStatus.equals(ComponentExecutionStatus.BLANK)){
				compStatusImage = null;
			}else if (componentStatus.equals(ComponentExecutionStatus.PENDING)){
				compStatusImage =ImagePathConstant.COMPONENT_PENDING_ICON.getImageFromRegistry();
			}else if (componentStatus.equals(ComponentExecutionStatus.RUNNING)){
				compStatusImage =ImagePathConstant.COMPONENT_RUNNING_ICON.getImageFromRegistry();
			}else if (componentStatus.equals(ComponentExecutionStatus.SUCCESSFUL)){
				compStatusImage =ImagePathConstant.COMPONENT_SUCCESS_ICON.getImageFromRegistry();
			}else if (componentStatus.equals(ComponentExecutionStatus.FAILED)){
				compStatusImage = ImagePathConstant.COMPONENT_FAILED_ICON.getImageFromRegistry();
			}
		}
		if (compStatusImage != null) {
			graphics.drawImage(compStatusImage, new Point (8, rectangle.height - 22));
		}
	}

	/**
	 * Update component status.
	 * 
	 * @param status
	 *            the status
	 */
	public void updateComponentStatus(ComponentExecutionStatus status){
		componentStatus = status;
	}
	
	/**
	 * Draws the status image to right corner of the component
	 * 
	 * @param graphics
	 */
	private void drawPropertyStatus(Graphics graphics) {
		Rectangle rectangle = getBounds().getCopy();
		if (StringUtils.isNotBlank(getPropertyStatus()) && getPropertyStatus().equals(ValidityStatus.WARN.name())) {
			statusImage = ImagePathConstant.COMPONENT_WARN_ICON.getImageFromRegistry();
		} else if (StringUtils.isNotBlank(getPropertyStatus()) && getPropertyStatus().equals(ValidityStatus.ERROR.name())) {
			statusImage = ImagePathConstant.COMPONENT_ERROR_ICON.getImageFromRegistry();
		} else if (StringUtils.isNotBlank(getPropertyStatus()) && getPropertyStatus().equals(Constants.UPDATE_AVAILABLE)) {
			statusImage = ImagePathConstant.COMPONENT_UPDATE_ICON.getImageFromRegistry();
		} else if (StringUtils.isNotBlank(getPropertyStatus()) && getPropertyStatus().equals(ValidityStatus.VALID.name())){
			statusImage=null;
		}
		logger.trace("Component has {} property status.", getPropertyStatus());
		if (statusImage != null && !statusImage.isDisposed()) {
			graphics.drawImage(statusImage, new Point(rectangle.width - 25, 8 + componentLabelMargin));
		}
	}
	
	
	/**
	 * @param Draw the schema Propogation status image to left top corner of the component. 
	 */
	private void drawSchemaPropogationInfoImageIfSchemaPropogationBreaks(Graphics graphics)
	{
		Rectangle rectangle=getBounds().getCopy();
		
		graphics.drawImage(ImagePathConstant.SCHEMA_PROPOGATION_STOP_ICON.getImageFromRegistry(), new Point(rectangle.width - 90, 8 + componentLabelMargin));
	}
	
	/**
	 * Calls dispose method on Fonts. Called by EditPart.
	 */
	public void disposeFonts(){
		if(labelFont!=null){
			this.labelFont.dispose();
		}
		if(acronymFont!=null){
			this.acronymFont.dispose();
		}
	}
	
	/**
	 * Gets the connection anchor.
	 * 
	 * @param terminal
	 *            the terminal
	 * @return the connection anchor
	 */
	public ConnectionAnchor getConnectionAnchor(String terminal) {

		return connectionAnchors.get(terminal);
	}

	/**
	 * Gets the connection anchor name.
	 * 
	 * @param connectionAnchor
	 *            the connectionAnchor
	 * @return the connection anchor name
	 */
	public String getConnectionAnchorName(ConnectionAnchor connectionAnchor) {

		Set<String> keys = connectionAnchors.keySet();
		String key;
		Iterator<String> it = keys.iterator();

		while (it.hasNext()) {
			key = it.next();
			if (connectionAnchors.get(key).equals(connectionAnchor))
				return key;
		}
		return null;
	}

	private ConnectionAnchor closestAnchor(Point p, List<FixedConnectionAnchor> connectionAnchors) {
		ConnectionAnchor closest = null;
		double min = Double.MAX_VALUE;
		for (ConnectionAnchor c : connectionAnchors) {
			double d = p.getDistance(c.getLocation(null));
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	/**
	 * Gets the source connection anchor at given point.
	 * 
	 * @param point
	 *            the point
	 * @return the source connection anchor at given point.
	 */
	public ConnectionAnchor getSourceConnectionAnchorAt(Point point) {
		return closestAnchor(point, outputConnectionAnchors);
	}

	/**
	 * Gets the target connection anchor at.
	 * 
	 * @param point
	 *            the point
	 * @return the target connection anchor at
	 */
	public ConnectionAnchor getTargetConnectionAnchorAt(Point point) {
		return closestAnchor(point, inputConnectionAnchors);
	}

	@Override
	public void validate() {
		super.validate();

		if (isValid())
			return;

	}

	@Override
	public String getPropertyStatus() {
		return propertyStatus;
	}

	@Override
	public void setPropertyStatus(String status) {
		this.propertyStatus = status;
	}

	/**
	 * 
	 * Returns true if the height of the component is incremented
	 * 
	 * @return
	 */
	public boolean isIncrementedHeight() {
		return incrementedHeight;
	}

	/**
	 * 
	 * Set whether height of the component is incremented
	 * 
	 * @param incrementedHeight
	 */
	public void setIncrementedHeight(boolean incrementedHeight) {
		this.incrementedHeight = incrementedHeight;
	}

	/**
	 * 
	 * Get component label margin
	 * 
	 * @return
	 */
	public int getComponentLabelMargin() {
		return componentLabelMargin;
	}

	/**
	 * 
	 * Set component label margin
	 * 
	 * @param componentLabelMargin
	 */
	public void setComponentLabelMargin(int componentLabelMargin) {
		this.componentLabelMargin = componentLabelMargin;
	}

	/**
	 * 
	 * terminate tooltip timer
	 * 
	 */
	public void terminateToolTipTimer() {
		if (display != null && timer != null) {
			display.timerExec(-1, timer);
		}

	}

	
	private void hideToolTip() {
		if (componentCanvas != null) {
			if (componentCanvas.getComponentTooltip() != null) {
				componentCanvas.getComponentTooltip().setVisible(false);
				componentCanvas.issueToolTip(null, null);
			}
		}

		if (componentToolTip != null) {
			componentToolTip.setVisible(false);
			componentToolTip = null;
			componentBounds = null;
		}
		componentCanvas = null;
	}

	/**
	 * 
	 * Set tooltip information
	 * 
	 * @param propertyToolTipInformation
	 */
	public void setPropertyToolTipInformation(Map<String, PropertyToolTipInformation> propertyToolTipInformation) {
		this.propertyToolTipInformation = propertyToolTipInformation;
	}

	private ComponentTooltip getStatusToolTip(Shell parent, org.eclipse.swt.graphics.Point location) {
		ComponentTooltip tooltip = new ComponentTooltip(component,parent, Constants.CLICK_TO_FOCUS, propertyToolTipInformation);
		tooltip.setSize(300, 100);
		tooltip.setLocation(location);
		return tooltip;
	}

	private ComponentTooltip getToolBarToolTip(Shell parent, org.eclipse.swt.graphics.Rectangle toltipBounds) {
		ToolBarManager toolBarManager = new ToolBarManager();
		ComponentTooltip tooltip = new ComponentTooltip(component,parent, toolBarManager, propertyToolTipInformation);
		org.eclipse.swt.graphics.Point location = new org.eclipse.swt.graphics.Point(toltipBounds.x, toltipBounds.y);
		tooltip.setLocation(location);
		tooltip.setSize(toltipBounds.width + 20, toltipBounds.height + 20);
		return tooltip;
	}

	private void setStatusToolTipFocusListener() {

		componentToolTip.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// Do nothing
			}

			@Override
			public void focusGained(FocusEvent e) {
				showToolBarToolTip();
			}
		});
	}

	private void showStatusToolTip(org.eclipse.swt.graphics.Point location) {

		if (!componentCanvas.isFocused())
			return;

		componentCanvas = getComponentCanvas();
		if (componentCanvas.getComponentTooltip() == null) {
			componentToolTip = getStatusToolTip(componentCanvas.getCanvasControl().getShell(), location);
			componentBounds = getComponentBounds();
			componentCanvas.issueToolTip(componentToolTip, componentBounds);

			componentToolTip.setVisible(true);
			setStatusToolTipFocusListener();
			org.eclipse.swt.graphics.Point tooltipSize = componentToolTip.computeSizeHint();
			// componentToolTip.setSizeConstraints(300, 100);
			if (tooltipSize.x > 300) {
				tooltipSize.x = 300;
			}
			componentToolTip.setSize(tooltipSize.x, tooltipSize.y);

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			double width = screenSize.getWidth();
			double height = screenSize.getHeight();

			int newX, newY;
			int offset = 10;
			if ((componentToolTip.getBounds().x + componentToolTip.getBounds().width) > width) {
				newX = componentToolTip.getBounds().x
						- (int) ((componentToolTip.getBounds().x + componentToolTip.getBounds().width) - width)
						- offset;
			} else {
				newX = componentToolTip.getBounds().x;
			}

			if ((componentToolTip.getBounds().y + componentToolTip.getBounds().height) > height) {
				newY = componentToolTip.getBounds().y - getBounds().height - componentToolTip.getBounds().height
						- offset;
			} else {
				newY = componentToolTip.getBounds().y;
			}
			org.eclipse.swt.graphics.Point newLocation = new org.eclipse.swt.graphics.Point(newX, newY);
			componentToolTip.setLocation(newLocation);

		}
	}

	private org.eclipse.swt.graphics.Rectangle getComponentBounds() {
		Rectangle tempComponuntBound = getBounds();
		org.eclipse.swt.graphics.Rectangle componentBound = new org.eclipse.swt.graphics.Rectangle(
				tempComponuntBound.x, tempComponuntBound.y, tempComponuntBound.width, tempComponuntBound.height);
		return componentBound;
	}

	private void hideToolTip2() {
		if (componentCanvas != null) {
			if (componentCanvas.getComponentTooltip() != null) {
				componentCanvas.getComponentTooltip().setVisible(false);
				    if(componentCanvas!=null)
					componentCanvas.issueToolTip(null, null);
			}
		}

		if (componentToolTip != null) {
			componentToolTip.setVisible(false);
			componentToolTip = null;
			componentBounds = null;
		}
		componentCanvas = null;
	}

	private void showToolBarToolTip() {
		org.eclipse.swt.graphics.Rectangle toltipBounds = componentToolTip.getBounds();

		hideToolTip();

		componentCanvas = getComponentCanvas();

		componentToolTip = getToolBarToolTip(componentCanvas.getCanvasControl().getShell(), toltipBounds);
		componentBounds = getComponentBounds();
		componentCanvas.issueToolTip(componentToolTip, componentBounds);

		componentToolTip.setVisible(true);

		componentToolTip.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				hideToolTip2();
			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		componentToolTip.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				hideToolTip2();
			}
		});
		componentToolTip.setFocus();
	}

	private org.eclipse.swt.graphics.Point getToolTipLocation(org.eclipse.swt.graphics.Point reletiveMouseLocation,
			org.eclipse.swt.graphics.Point mouseLocation, Rectangle rectangle) {
		int subtractFromMouseX, addToMouseY;

		subtractFromMouseX = reletiveMouseLocation.x - rectangle.x;
		addToMouseY = (rectangle.y + rectangle.height) - reletiveMouseLocation.y;

		return new org.eclipse.swt.graphics.Point((mouseLocation.x - subtractFromMouseX),
				(mouseLocation.y + addToMouseY));
	}

	private void attachMouseListener() {
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(org.eclipse.draw2d.MouseEvent arg0) {
				// Do nothing
			}

			@Override
			public void mouseHover(org.eclipse.draw2d.MouseEvent arg0) {
				arg0.consume();
				final org.eclipse.swt.graphics.Point location1 = new org.eclipse.swt.graphics.Point(arg0.x, arg0.y);
				java.awt.Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
				final org.eclipse.swt.graphics.Point location = new org.eclipse.swt.graphics.Point(mouseLocation.x,
						mouseLocation.y);
				componentCanvas = getComponentCanvas();
				display = componentCanvas.getCanvasControl().getShell().getDisplay();
				timer = new Runnable() {
					public void run() {
						// if(componentCanvas.isToolTipTimerRunning())
						java.awt.Point mouseLocation2 = MouseInfo.getPointerInfo().getLocation();
						org.eclipse.swt.graphics.Point location2 = new org.eclipse.swt.graphics.Point(mouseLocation2.x,
								mouseLocation2.y);

						org.eclipse.swt.graphics.Point perfectToolTipLocation = getToolTipLocation(location1,
								location2, getBounds());

						if (location2.equals(location)) {
							showStatusToolTip(perfectToolTipLocation);
							// showStatusToolTip(location);
						}

					}
				};
				display.timerExec(TOOLTIP_SHOW_DELAY, timer);

			}

			@Override
			public void mouseExited(org.eclipse.draw2d.MouseEvent arg0) {
				// Do nothing
			}

			@Override
			public void mouseEntered(org.eclipse.draw2d.MouseEvent arg0) {
				// Do nothing
			}

			@Override
			public void mouseDragged(org.eclipse.draw2d.MouseEvent arg0) {
				// Do nothing
			}
		});

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(org.eclipse.draw2d.MouseEvent arg0) {

			}

			@Override
			public void mousePressed(org.eclipse.draw2d.MouseEvent arg0) {
				hideToolTip();
			}

			@Override
			public void mouseDoubleClicked(org.eclipse.draw2d.MouseEvent arg0) {
				hideToolTip();
			}
		});
	}

	
}
