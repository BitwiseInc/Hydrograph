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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.model.PortAlignmentEnum;

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * The Class PortFigure.
 * Represents the figure class for the component ports.
 * 
 * @author Bitwise
 */
public class PortFigure extends Figure {

	private Color portColor, componentBorderSelectedColor, componentBorder, watchColor;
	private String terminal;
	private FixedConnectionAnchor anchor;
	private String labelOfPort;
	private static boolean displayPortLabels;
	private boolean isWatched;
	private PortAlignmentEnum portAlignment;
	private Font portLabelFont;

	/**
	 * Instantiates a new port figure.
	 * 
	 * @param portColor
	 *            the port color
	 * @param portSeq 
	 * 			Sequence of the port
	 * @param totalPorts
	 * 			Total number of ports
	 * @param portTerminal
	 *            the port terminal
	 * @param labelOfPort 
	 * 			label to be displayed for port
	
	 */
	public PortFigure(Color portColor, int portSeq,
			int totalPorts, String portTerminal, String labelOfPort, PortAlignmentEnum alignment) {
		
		if (alignment == null) {
			if (StringUtils.contains(portTerminal, Constants.INPUT_SOCKET_TYPE))
				alignment = PortAlignmentEnum.LEFT;
			else if (StringUtils.contains(portTerminal, Constants.OUTPUT_SOCKET_TYPE))
				alignment = PortAlignmentEnum.RIGHT;
			else if (StringUtils.contains(portTerminal, Constants.UNUSED_SOCKET_TYPE))
				alignment = PortAlignmentEnum.BOTTOM;
		}
		
		this.portColor = portColor;
		this.terminal = portTerminal;
		this.anchor = new FixedConnectionAnchor(this, alignment.value(), totalPorts,
				portSeq, portTerminal);
		this.labelOfPort=labelOfPort;
		this.portAlignment = alignment;
		////to define the height and width of in, out and unused port 
		setPortDimension();

        portLabelFont = new Font(Display.getDefault(),ELTFigureConstants.labelFont, 8, SWT.NORMAL);
		setFont(portLabelFont);
		setForegroundColor(ColorConstants.black);
		
		componentBorder = new Color(null, ELTColorConstants.DARK_GREY_RGB[0], ELTColorConstants.DARK_GREY_RGB[1], ELTColorConstants.DARK_GREY_RGB[2]);
		componentBorderSelectedColor = new Color(null, ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[0], ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[1], ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[2]);
		watchColor = new Color(null, ELTColorConstants.WATCH_COLOR_RGB[0], ELTColorConstants.WATCH_COLOR_RGB[1], ELTColorConstants.WATCH_COLOR_RGB[2]);
		
		//NOTE : to Suppress the component tooltip when user hover the mouse on Port 
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
			}

			@Override
			public void mouseHover(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
			}
		});

	}
	  //to define the height and width of in, out and unused port 
		private void setPortDimension() {
			if(PortAlignmentEnum.LEFT.equals(portAlignment)){
				getBounds().setSize(new Dimension(27,10));
			}
			else if(PortAlignmentEnum.RIGHT.equals(portAlignment)){
				getBounds().setSize(new Dimension(27,10));
			}
			else if(PortAlignmentEnum.BOTTOM.equals(portAlignment)){
				getBounds().setSize(new Dimension(24,16));
			}		
		}
	
	/**
	 * Gets the port color.
	 * 
	 * @return the port color
	 */
	public Color getPortColor() {
		return portColor;
	}
	
	
	
	/**
	 * Gets the label of port.
	 * 
	 * @return the label of port
	 */
	public String getLabelOfPort() {
		return labelOfPort;
	}
	
	/**
	 * Sets the label of port.
	 * 
	 * @param label
	 *            the new label of port
	 */
	public void setLabelOfPort(String label) {
		this.labelOfPort=label;
	}
	
	/**
	 * Checks if port is watched.
	 * 
	 * @return true, if port is watched
	 */
	public boolean isWatched() {
		return isWatched;
	}
	
	/**
	 * Sets the watched.
	 * 
	 * @param isWatched
	 *            the new watched
	 */
	public void setWatched(boolean isWatched) {
		this.isWatched = isWatched;
	}
	
	/**
	 * Checks if portlabels can be displayed..
	 * 
	 * @return true, if portlabels can be displayed.
	 */
	public boolean isDisplayPortlabels() {
		return displayPortLabels;
	}
	
	/**
	 * Calls dispose method on Fonts. Called by EditPart.
	 */
	public void disposeFont(){
		if(this.portLabelFont!=null){
			this.portLabelFont.dispose();
		}
	}
	
	
	/**
	 * Sets whether to display portlabels. If set to true, port labels will be displayed in canvas.
	 * 
	 * @param toggleValue
	 *            the new display portlabels
	 */
	public void setDisplayPortlabels(boolean toggleValue) {
		PortFigure.displayPortLabels = toggleValue;
	}
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if(isWatched)
			setBackgroundColor(watchColor);
		Rectangle r = getBounds().getCopy();
		if(PortAlignmentEnum.LEFT.equals(portAlignment)){
			graphics.fillRectangle(getBounds().getLocation().x-20, getBounds()
					.getLocation().y-1, r.width, r.height-2);
		}else if(PortAlignmentEnum.RIGHT.equals(portAlignment)){
			graphics.fillRectangle(getBounds().getLocation().x+20, getBounds()
					.getLocation().y-1, r.width, r.height-2);
		}else if(PortAlignmentEnum.BOTTOM.equals(portAlignment)){
			graphics.fillRectangle(getBounds().getLocation().x-16, getBounds()
					.getLocation().y+10, r.width,r.height);
		}
			
			
		if(isDisplayPortlabels()){
			if(PortAlignmentEnum.LEFT.equals(portAlignment)){
				graphics.drawText(labelOfPort,new Point(getBounds().getLocation().x+8,getBounds()
						.getLocation().y-0.2));
			}else if(PortAlignmentEnum.RIGHT.equals(portAlignment)){
				graphics.drawText(labelOfPort,new Point(getBounds().getLocation().x,getBounds()
						.getLocation().y-0.2));
			}else if(PortAlignmentEnum.BOTTOM.equals(portAlignment)){
				graphics.drawText(labelOfPort,new Point(getBounds().getLocation().x,getBounds()
						.getLocation().y-0.2));
			}	
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PortFigure) {
			PortFigure pf = (PortFigure) o;
			if (pf.getParent() == this.getParent()
					&& pf.getTerminal() == this.getTerminal()

					)
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		int var1 = terminal.length();
		int sequence = terminal.charAt(terminal.length() - 1);
		int var2 = portColor.hashCode();
		int var3 = labelOfPort.hashCode();
		result = 31 * result + var1;
		result = 31 * result + sequence;
		result = 31 * result + var2;
		result = 31 * result + var3;
		if(portAlignment!=null){
			int var4 = portAlignment.hashCode();
			result = 31 * result + var4;
			}
		return result;

	}

	/**
	 * Select port on the canvas by changing the background color of the port.
	 */
	public void selectPort() {
		if(!isWatched)
		setBackgroundColor(componentBorderSelectedColor);
	}
	
	/**
	 * Deselect port by reverting the background color of the port.
	 */
	public void deSelectPort() {
		if(!isWatched)
		setBackgroundColor(componentBorder);
	}

	/**
	 * Change watcher color.
	 * Typically when user adds the watcher on this port.
	 */
	public void changeWatcherColor(){
		setBackgroundColor(watchColor);
	}
	
	/**
	 * Removes the watcher color.
	 * Typically when user removes the watcher on this port.
	 */
	public void removeWatcherColor(){
		setBackgroundColor(componentBorder);
	}
	@Override
	public void validate() {
		super.validate();

		if (isValid())
			return;

	}

	/**
	 * Gets the handle bounds.
	 * 
	 * @return the handle bounds
	 */
	public Rectangle getHandleBounds() {
		return getBounds().getCopy();
	}

	/**
	 * Gets the terminal.
	 * 
	 * @return the terminal
	 */
	public String getTerminal() {
		return terminal;
	}

	/**
	 * Gets the anchor.
	 * 
	 * @return the anchor
	 */
	public FixedConnectionAnchor getAnchor() {
		return anchor;
	}

	@Override
	public String toString() {

		return "\n******************************************" + "\nTerminal: "
				+ this.terminal + "\nParent Figure: " + this.getParent()
				+ "\nHashcode: " + hashCode()
				+ "\n******************************************\n";
	}

}
