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

 
package hydrograph.ui.graph.editor;

import java.awt.MouseInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.graph.command.ComponentCreateCommand;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.tooltip.tooltips.PaletteToolTip;


/**
 * The listener interface for receiving paletteContainer events. The class that is interested in processing a
 * paletteContainer event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addPaletteContainerListener<code> method. When
 * the paletteContainer event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see PaletteContainerEvent
 */
public class PaletteContainerListener implements MouseListener, MouseTrackListener , MouseMoveListener {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(PaletteContainerListener.class);
	private final PaletteViewer viewer;
	private Point defaultComponentLocation = new Point(0, 0);
	private Component genericComponent;
	private GraphicalViewer graphicalViewer;
	private PaletteToolTip paletteToolTip;
	private static final int TOOLTIP_SHOW_DELAY=600;
	
	public static LinkedHashMap<String, Point> compoLocationList;
	static{
		compoLocationList = new LinkedHashMap<>();
	}
	
	/**
	 * Get all components from Job canvas and populate/update componentLocationList
	 */
	public List<Component> getCanvasCompAndUpdateCompList(){
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		List<Component> compListFromCanvas = ((ELTGraphicalEditor) page.getActiveEditor()).getContainer().getUIComponentList();
		logger.debug("Existing components from Job canvas");
		for (int i=0; i<compListFromCanvas.size(); i++){
			String currentCompLabel = compListFromCanvas.get(i).getComponentLabel().getLabelContents();
			Point currentCompLocation = compListFromCanvas.get(i).getLocation();
			compoLocationList.put(currentCompLabel, currentCompLocation);
			logger.debug("Added/updated component {} at location: ({},{})", new Object[]{currentCompLabel, currentCompLocation.x, currentCompLocation.y});
		}
		return compListFromCanvas;
	}
	
	/**
	 * Remove the component from compoLocationList if it is removed
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateComponentListForDeletedComponents(List<Component> compListFromCanvas){
		if (compoLocationList.size() <= 0)
			return;
		
		Iterator iterator = ((HashMap<String, Point>) compoLocationList.clone()).entrySet().iterator();
		while (iterator.hasNext()){
			boolean compFound = false;
			Map.Entry entry = (Map.Entry)iterator.next();
			for (int i=0; i<compListFromCanvas.size(); i++){
				String currentCompLabel = compListFromCanvas.get(i).getComponentLabel().getLabelContents();
				if (entry.getKey().equals(currentCompLabel)){
					compFound = true;
					break;
				}
			}
			if (! compFound){
				logger.debug("Removing component:" + entry.getKey());
				compoLocationList.remove(entry.getKey());
			}
		}
	}
	
	/**
	 * Return last added component from job canvas
	 * @return
	 */
	public Entry<String, Point> getLastAddedComponentFromCanvas(){
		Entry<String, Point> lastAddedCompEntry = null;
		Iterator<Entry<String, Point>> i = compoLocationList.entrySet().iterator();
		while (i.hasNext()){
			lastAddedCompEntry = i.next();
		}
		
		return lastAddedCompEntry;
	}
	
	/**
	 * Iterate all components and compare the possible position to make sure that
	 * it is not overlapping any of the existing component
	 * 
	 * @param calculatedPoint
	 * @param compListFromCanvas
	 * @param isStop
	 * @return
	 */
	public Point getFinalLocationForComponent(Point calculatedPoint, List<Component> compListFromCanvas, boolean isStop){		
		if (compListFromCanvas.size() == 0)
			return calculatedPoint;
		
		boolean compOverlapping = false;
		List<Component> subCompList = null;
		Component currentComponent = null;
		for (int j=0; j<compListFromCanvas.size(); j++){
			Rectangle existingDimension = new Rectangle(compListFromCanvas.get(j).getLocation(), compListFromCanvas.get(j).getSize());
			Rectangle newDimention = new Rectangle(calculatedPoint, new Dimension(calculatedPoint.x, calculatedPoint.y));
			subCompList = compListFromCanvas.subList(j+1, compListFromCanvas.size());
			currentComponent = compListFromCanvas.get(j);
			if (! existingDimension.intersects(newDimention)){
				logger.debug("Component " + compListFromCanvas.get(j).getComponentLabel().getLabelContents() + " is not overlapping ");
			}else{
				logger.debug("Component " + compListFromCanvas.get(j).getComponentLabel().getLabelContents() + " is Overlapping");
				compOverlapping = true;
				break;
			}
			
		}
		if (compOverlapping){
			calculatedPoint = new  Point(currentComponent.getLocation().x + 5, currentComponent.getLocation().y + currentComponent.getSize().height + 5);
		}
		return getFinalLocationForComponent(calculatedPoint, subCompList, false);
	}
	
	public Point calculateNewComponentLocation(Entry<String, Point> lastAddedComp, List<Component> compListFromCanvas){
		
		/**
		 * Initialize the X and Y coordinate to zero for first component on job canvas
		 */
		int LastCompXPoint = 0;
		int LastCompYPoint = 0;
		if (lastAddedComp != null){
			/**
			 * If there are components on job canvas then initialize X and Y coordinate.
			 */
			String lastAddedCompLabel = lastAddedComp.getKey();
			Point lastAdeddCompLocation = lastAddedComp.getValue();
			LastCompXPoint = lastAdeddCompLocation.x;
			LastCompYPoint = lastAdeddCompLocation.y;
			logger.debug("Last added component: " + lastAddedCompLabel + " Location: (" + LastCompXPoint + "," + LastCompYPoint + ")");
		}
		
		/**
		 * Populate the X and Y coordinate for new component to have it just below the last component which was added in job canvas.
		 * This is not final position of new component as there is posibility that rectangle of new component may overlap on existing component.
		 */
		int newCompYPoint = (LastCompYPoint == 0 && lastAddedComp == null)? LastCompYPoint : (LastCompYPoint );
		int newCompXPoint = (LastCompXPoint == 0 && lastAddedComp == null)? LastCompXPoint : (LastCompXPoint + genericComponent.getSize().width + 10);
		logger.debug("New component's possible location: (" + newCompXPoint + "," + newCompYPoint + ")");
		
		/**
		 * Finalize and return the position of new component
		 */
		Point newCompLocation = new Point(newCompXPoint , newCompYPoint);
		logger.debug("Checking is possible position is overlapping with any of the existing component");
		newCompLocation = getFinalLocationForComponent(newCompLocation, compListFromCanvas, false);
		compoLocationList.put(genericComponent.getComponentLabel().getLabelContents(), newCompLocation);
		logger.debug("Final position of new component = (" + newCompLocation.x + "," + newCompLocation.y + ")");
		return newCompLocation;
	}
	
	public Point getLocationForNewComponent(){
		List<Component> compListFromCanvas = getCanvasCompAndUpdateCompList();
		updateComponentListForDeletedComponents(compListFromCanvas);
		Entry<String, Point> lastAddedComponent = getLastAddedComponentFromCanvas();
		Point calculatedNewCompPosition = calculateNewComponentLocation(lastAddedComponent, compListFromCanvas);
		
		return calculatedNewCompPosition;
	}
	
	/**
	 * Instantiates a new palette container listener.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param graphicalViewer
	 *            the graphical viewer
	 */
	public PaletteContainerListener(PaletteViewer viewer, GraphicalViewer graphicalViewer) {
		this.graphicalViewer = graphicalViewer;
		this.viewer = viewer;
	}
	
	@Override
	public void mouseUp(MouseEvent e) {
		logger.debug("Hiding tooltip");
		hidePaletteToolTip();
	}

	@Override
	public void mouseDown(MouseEvent e) {
		viewer.getEditDomain().getActiveTool().deactivate();
		graphicalViewer.getEditDomain().getActiveTool().deactivate();
	}

	@Override
	public void mouseDoubleClick(MouseEvent mouseEvent) {
		CreateRequest componentRequest = getComponentRequest(mouseEvent);
		placeComponentOnCanvasByDoubleClickOnPalette(componentRequest);
		logger.info(
				"Component is positioned at respective x and y location"
						+ defaultComponentLocation.getCopy().x + 20 + " and "
						+ defaultComponentLocation.getCopy().y + 20);
		logger.info(
				"Component is positioned at respective x and y location"
						+ defaultComponentLocation.getCopy().x + 20 + " and "
						+ defaultComponentLocation.getCopy().y + 20);
		
		viewer.getEditDomain().loadDefaultTool();
		graphicalViewer.getEditDomain().loadDefaultTool();

	}

	/**
	 * 
	 * Create component request
	 * 
	 * @param mouseEvent
	 * @return
	 */
	private CreateRequest getComponentRequest(MouseEvent mouseEvent) {
		EditPart paletteInternalController = viewer.findObjectAt(new Point(
				mouseEvent.x, mouseEvent.y));

		CreateRequest componentRequest = setGenericComponent(paletteInternalController);

		setComponentRequestParams(componentRequest);

		return componentRequest;
	}

	
	
	private void setComponentRequestParams(CreateRequest componentRequest) {
		componentRequest.setSize(genericComponent.getSize());

		Point newCompDefaultLocation = getLocationForNewComponent();
		defaultComponentLocation.setLocation(newCompDefaultLocation.x, newCompDefaultLocation.y);
		
		componentRequest.setLocation(defaultComponentLocation);
	}

	private void placeComponentOnCanvasByDoubleClickOnPalette(
			CreateRequest componentRequest) {
		GraphicalViewer graphViewer = graphicalViewer;

		ComponentCreateCommand createComponent = new ComponentCreateCommand(
				(hydrograph.ui.graph.model.Component) componentRequest
						.getNewObject(),
				(Container) graphViewer.getContents().getModel(),
				new Rectangle(componentRequest.getLocation(), componentRequest
						.getSize()));

		graphViewer.getEditDomain().getCommandStack().execute(createComponent);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		// Do Nothing
	}

	@Override
	public void mouseExit(MouseEvent e) {
		logger.debug("Hiding tooltip");
		hidePaletteToolTip();
	}

	/**
	 * Hide Tooltip
	 */
	private void hidePaletteToolTip(){
		if(paletteToolTip!=null){
			java.awt.Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
			if(!paletteToolTip.getBounds().contains(mouseLocation.x, mouseLocation.y)){
				logger.debug("hiding tooltip");
				paletteToolTip.setVisible(false);				
			}else{
				logger.debug("Not hiding tooltip as mouse pointer is on tooltip");			
			}
		}
	}
	
	
	/**
	 * 
	 * Show tooltip
	 * 
	 * @param toolTipMessage - text message to be display on tooltip
	 */
	private void showPaletteToolTip(String toolTipMessage) {
		paletteToolTip = new PaletteToolTip(Display.getDefault());
		java.awt.Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		paletteToolTip.setLocation(mouseLocation.x + 11 , mouseLocation.y +7);
		paletteToolTip.setToolTipText(toolTipMessage);
		paletteToolTip.setVisible(true);
	}


	@Override
	public void mouseHover(MouseEvent e) {
		final java.awt.Point mouseLocation1 = MouseInfo.getPointerInfo().getLocation();
		EditPart paletteInternalController = viewer.findObjectAt(new Point(
				e.x, e.y));

		if(paletteInternalController.getModel() instanceof CombinedTemplateCreationEntry){

			setGenericComponent(paletteInternalController);

			// Hide tooltip if already showing
			hidePaletteToolTip();
			
			showToolTipWithDelay(mouseLocation1);
		}
	}

	/**
	 * 
	 * set genericComponent to selected/hovered component in palette
	 * 
	 * @param paletteInternalController
	 * @return
	 */
	private CreateRequest setGenericComponent(EditPart paletteInternalController) {
		CombinedTemplateCreationEntry addedPaletteTool = (CombinedTemplateCreationEntry) paletteInternalController
				.getModel();

			CreateRequest componentRequest = new CreateRequest();
			componentRequest.setFactory(new SimpleFactory((Class) addedPaletteTool
					.getTemplate()));

		genericComponent = (Component) componentRequest
				.getNewObject();
		
		logger.debug("genericComponent - " + genericComponent.toString());
		
		return componentRequest;
	}

	/**
	 * 
	 * Show tooltip with some delay
	 * 
	 * @param mouseLocation1 - to check if mouse is not moved between the delay
	 */
	private void showToolTipWithDelay(final java.awt.Point mouseLocation1) {
		Display.getDefault().timerExec(TOOLTIP_SHOW_DELAY, new Runnable() {
			public void run() {
				java.awt.Point mouseLocation2 = MouseInfo.getPointerInfo().getLocation();
				
				if(mouseLocation1.equals(mouseLocation2)){
					showPaletteToolTip(genericComponent.getComponentDescription());
					paletteToolTip.setComponentName(genericComponent.getComponentName());
				}
				
		    }
		});
	}
	
	@Override
	public void mouseMove(MouseEvent e) {		
		if(paletteToolTip!=null){			
			org.eclipse.swt.graphics.Rectangle newBounds = getToolTipBoundsFromMouseLocation();
			java.awt.Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
			if(!newBounds.contains(mouseLocation.x,mouseLocation.y)){
				hidePaletteToolTip();
			}else{
				logger.trace("Near tooltip area");
			}
				
		}else{
			logger.trace("PaletteToolTip= null");
		}
	}

	/**
	 * 
	 * Calculate tooltip bounds from mouse location to prevent hiding tooltip from mouseMove event on palette
	 * 
	 * @return
	 */
	private org.eclipse.swt.graphics.Rectangle getToolTipBoundsFromMouseLocation() {
		org.eclipse.swt.graphics.Rectangle tooltipBounds = paletteToolTip.getBounds();
		org.eclipse.swt.graphics.Rectangle newBounds = new org.eclipse.swt.graphics.Rectangle(tooltipBounds.x - 11, tooltipBounds.y - 7, tooltipBounds.width, tooltipBounds.height);
		return newBounds;
	}	
}
