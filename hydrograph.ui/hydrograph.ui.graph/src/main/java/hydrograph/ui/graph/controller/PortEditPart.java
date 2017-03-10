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

import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.ConnectionDragCreationTool;
import org.eclipse.swt.graphics.Color;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.graph.figure.ComponentFigure;
import hydrograph.ui.graph.figure.ELTColorConstants;
import hydrograph.ui.graph.figure.PortFigure;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Port;
import hydrograph.ui.graph.model.PortAlignmentEnum;

/**
 * The Class PortEditPart.
 * 
 * @author Bitwise
 */
public class PortEditPart extends AbstractGraphicalEditPart {
	public Port getCastedModel() {
		return (Port) getModel();
	}

	@Override
	protected IFigure createFigure() {
		
		ComponentFigure componentFigure = ((ComponentEditPart) getParent()).getComponentFigure();
		Component component = ((ComponentEditPart) getParent()).getCastedModel();
		PortFigure port = null;

		Color borderColor = CustomColorRegistry.INSTANCE.getColorFromRegistry( ELTColorConstants.DARK_GREY_RGB[0], ELTColorConstants.DARK_GREY_RGB[1], ELTColorConstants.DARK_GREY_RGB[2]);
		Point portPoint = null;
		
		int height = component.getSize().height-componentFigure.getComponentLabelMargin();
		int width = component.getSize().width;
		
		
		int margin = componentFigure.getComponentLabelMargin();
		port =  new PortFigure(borderColor, getCastedModel().getSequence(), getCastedModel().getNumberOfPortsOfThisType(),getCastedModel().getTerminal(),
				getCastedModel().getLabelOfPort(), getCastedModel().getPortAlignment());	
		
		portPoint = getPortLocation(getCastedModel().getNumberOfPortsOfThisType(), getCastedModel().getPortType(),
				getCastedModel().getSequence(), height, width, margin, getCastedModel().getPortAlignment());
		
		Point  tmpPoint = new Point(componentFigure.getLocation().x+portPoint.x , componentFigure.getLocation().y+portPoint.y);
		port.setLocation(tmpPoint);
		componentFigure.setAnchors(port.getAnchor());
		return port;
	}

	

	private Point getPortLocation(int totalPortsOfThisType, String type, int sequence, int height, int width, int margin, 
			PortAlignmentEnum portAlignment){

		if (portAlignment == null) {
			if (StringUtils.equalsIgnoreCase(type,Constants.INPUT_SOCKET_TYPE))
				portAlignment = PortAlignmentEnum.LEFT;
			else if (StringUtils.equalsIgnoreCase(type,Constants.OUTPUT_SOCKET_TYPE))
				portAlignment = PortAlignmentEnum.RIGHT;
			else if (StringUtils.equalsIgnoreCase(type,Constants.UNUSED_SOCKET_TYPE))
				portAlignment = PortAlignmentEnum.BOTTOM;
		}
		
		Point p = null ;
		int portOffsetFactor = totalPortsOfThisType+1;
		int portHeightOffset=height/portOffsetFactor;
		int portWidthOffset=width/portOffsetFactor;
		int xLocation=0, yLocation=0;

		if(PortAlignmentEnum.LEFT.equals(portAlignment)){
			xLocation=0;
			yLocation=portHeightOffset*(sequence+1) - 4 + margin;
		}else if(PortAlignmentEnum.RIGHT.equals(portAlignment)){
			xLocation=width-27;
			yLocation=portHeightOffset*(sequence+1) - 4 + margin;
		}else if (PortAlignmentEnum.BOTTOM.equals(portAlignment)){
			if(totalPortsOfThisType == 1){
				xLocation = 43;
				
			}else if(totalPortsOfThisType > 1){
				xLocation = portWidthOffset*(sequence+1) - 6;
			}
			yLocation=height  + margin - 8 - 8;
			
		}
		p=new Point(xLocation, yLocation);
		return p;
	}
	
	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public DragTracker getDragTracker(Request request) {
		getViewer().select(this);
		return new ConnectionDragCreationTool();
	}
	
	public void adjustPortFigure(Point componentLocation) { 
		
		ComponentFigure componentFigure = ((ComponentEditPart) getParent()).getComponentFigure();
		Component component = ((ComponentEditPart) getParent()).getCastedModel();
		
		int height = component.getSize().height-componentFigure.getComponentLabelMargin();
		int width = component.getSize().width;
		
		int margin = componentFigure.getComponentLabelMargin(); 
		Point portPoint = getPortLocation(getCastedModel().getNumberOfPortsOfThisType(), getCastedModel().getPortType(),
				getCastedModel().getSequence(), height, width, margin, getCastedModel().getPortAlignment());
		
		Point newPortLoc = new Point(portPoint.x+componentLocation.x, portPoint.y+componentLocation.y);
				
		getFigure().setLocation(newPortLoc);
	}
	public PortFigure getPortFigure() {
		return (PortFigure) getFigure();
	}
	

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		((PortFigure)getFigure()).setLabelOfPort(getCastedModel().getLabelOfPort());
		
		for(Entry<String,Long> entry : getCastedModel().getParent().getWatcherTerminals().entrySet())
		{
			if(StringUtils.equals(getCastedModel().getTerminal(),entry.getKey()))
			{
				getCastedModel().setWatched(true);
				getPortFigure().setWatched(true);
				getPortFigure().repaint();
			}
		}
		
		getFigure().repaint();
	}
	
	@Override
	public void deactivate() {
		if (isActive()) {
			getPortFigure().disposeFont();
			super.deactivate();
		}
	}
}
