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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

import hydrograph.ui.common.util.CustomColorRegistry;
/**
 * The Class CommentBoxFeedbackFigure.
 * 
 * @author Bitwise
 * 
 */
public class CommentBoxFeedbackFigure
	extends BentCornerFigure
{

	/**
	 * Paints this Figure's primary representation, or background
	 * 
	 * @param graphics
	 *            The Graphics used to paint
	 */
protected void paintFigure(Graphics graphics) {
	Rectangle rect = getBounds().getCopy();
	
	graphics.setXORMode(true);
	graphics.setForegroundColor(ColorConstants.white);
	graphics.setBackgroundColor(CustomColorRegistry.INSTANCE.getColorFromRegistry( 31, 31, 31));
	
	graphics.translate(getLocation());
	
	PointList outline = new PointList();
	
	outline.addPoint(0, 0);
	outline.addPoint(rect.width - getCornerSize(), 0);
	outline.addPoint(rect.width - 1, getCornerSize());
	outline.addPoint(rect.width - 1, rect.height - 1);
	outline.addPoint(0, rect.height - 1);
	
	graphics.fillPolygon(outline); 
	
	// draw the inner outline
	PointList innerLine = new PointList();

	innerLine.addPoint(rect.width - getCornerSize() - 1, 0);
	innerLine.addPoint(rect.width - getCornerSize() - 1, getCornerSize());
	innerLine.addPoint(rect.width - 1, getCornerSize());
	innerLine.addPoint(rect.width - getCornerSize() - 1, 0);
	innerLine.addPoint(0, 0);
	innerLine.addPoint(0, rect.height - 1);
	innerLine.addPoint(rect.width - 1, rect.height - 1);
	innerLine.addPoint(rect.width - 1, getCornerSize());

	graphics.drawPolygon(innerLine);
	
	graphics.drawLine(rect.width - getCornerSize() - 1, 0, rect.width - 1, getCornerSize());
	
	graphics.translate(getLocation().getNegated());
}
}
