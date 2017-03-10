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

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;


/**
 * The Class ComponentBorder.
 * Used to provide borders to the components in the canvas.
 * 
 * @author Bitwise
 */
public class ComponentBorder extends AbstractBorder {

	private Insets insets;
	private Color borderColor;
	private int lineWidth;
	private int labelMargin;
	
	/**
	 * Instantiates a new component border.
	 * 
	 * @param borderColor
	 *            the border color
	 */
	public ComponentBorder(Color borderColor){
		this.borderColor = borderColor;
		insets=new Insets();
	}
	
	/**
	 * Instantiates a new component border.
	 * 
	 * @param borderColor
	 *            the border color
	 * @param lineWidth
	 *            the line width
	 * @param margin
	 *            the margin from component label
	 */
	public ComponentBorder(Color borderColor,int lineWidth, int margin){
		this.borderColor = borderColor;
		this.lineWidth = lineWidth;
		this.labelMargin = margin;
		insets=new Insets();
	}
	
	/**
	 * Instantiates a new component border.
	 * 
	 * @param borderColor
	 *            the border color
	 * @param lineWidth
	 *            the line width
	 */
	public ComponentBorder(Color borderColor,int lineWidth){
		this.borderColor = borderColor;
		this.lineWidth = lineWidth;
		insets=new Insets();
	}
	@Override
	public Insets getInsets(IFigure figure) {
		return insets;
	}

	@Override
	public void paint(IFigure figure, Graphics g, Insets in) {
		
		Rectangle r = figure.getBounds().getCopy();
		
		g.setForegroundColor(borderColor);

		if(lineWidth!=0){
			g.setLineWidth(lineWidth);
		}
		
		//top
		g.drawLine(r.x+4+4, r.y+4+labelMargin, r.right() - 5-4, r.y+4+labelMargin);
		
		//Bottom
		g.drawLine(r.x+4+4, r.bottom()-5, r.right() - 5-4, r.bottom()-5);
		
		//Left
		g.drawLine(r.x+4, r.y + 4+4+labelMargin, r.x+4, r.bottom() - 5-4);
		
		//right
		g.drawLine(r.right() - 5, r.bottom() - 5-4, r.right() - 5, r.y + 4+4+labelMargin);
		
		//----------Arcs at corners---------------------------
		
		//top right
		g.drawArc(r.right() - 5-4-4, r.y + 4 + labelMargin, 8, 8, 0, 90);
		
		//bottom left
		g.drawArc(r.x+4, r.bottom()-5-4-4, 8, 8, 180, 90);
		
		//bottom right
		g.drawArc(r.right() - 5-4-4, r.bottom()-5-4-4, 8, 8, 0, -90);
		
		//top left
		g.drawArc(r.x+4, r.y + 4 + labelMargin, 8, 8, 180, -90);
		
		
		r.getExpanded(new Insets(0, 0, 0, 0));
		r.expand(1, 1);
		
		
	}

	/**
	 * Gets the label margin.
	 * 
	 * @return the label margin
	 */
	public int getLabelMargin() {
		return labelMargin;
	}

	/**
	 * Sets the label margin.
	 * 
	 * @param labelMargin
	 *            the new label margin
	 */
	public void setLabelMargin(int labelMargin) {
		this.labelMargin = labelMargin;
	}

}

