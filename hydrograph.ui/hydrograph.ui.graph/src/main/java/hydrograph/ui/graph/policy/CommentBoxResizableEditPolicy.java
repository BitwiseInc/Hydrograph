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
package hydrograph.ui.graph.policy;

import java.util.Iterator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.graph.figure.CommentBoxFeedbackFigure;
import hydrograph.ui.graph.model.CommentBox;


/**
 * The Class CommentBoxResizableEditPolicy.
 * 
 * @author Bitwise
 * 
 */
public class CommentBoxResizableEditPolicy
	extends ResizableEditPolicy
{

/**
 * Creates the figure used for feedback.
 * @return the new feedback figure
 */
protected IFigure createDragSourceFeedbackFigure(){
	IFigure figure = createFigure((GraphicalEditPart)getHost(), null);
	
	figure.setBounds(getInitialFeedbackBounds());
	addFeedback(figure);
	return figure;
}
/**
 * Returns the CustomFeedbackFigure.
 *  
 * @return the CustomFeedbackFigure
 */
protected IFigure createFigure(GraphicalEditPart part, IFigure parent) {
	IFigure child = getCustomFeedbackFigure(part.getModel());
		
	if (parent != null)
		parent.add(child);

	Rectangle childBounds = part.getFigure().getBounds().getCopy();
	
	IFigure walker = part.getFigure().getParent();
	
	while (walker != ((GraphicalEditPart)part.getParent()).getFigure()) {
		walker.translateToParent(childBounds);
		walker = walker.getParent();
	}
	
	child.setBounds(childBounds);
	
	Iterator i = part.getChildren().iterator();
	
	while (i.hasNext())
		createFigure((GraphicalEditPart)i.next(), child);
	
	return child;
}

/**
 * Returns the CustomFeedbackFigure.
 *  
 * @return the CustomFeedbackFigure
 */
	protected IFigure getCustomFeedbackFigure(Object modelPart) {
		IFigure figure; 
		
		 if (modelPart instanceof CommentBox)
			figure = new CommentBoxFeedbackFigure();
	
		else {
			figure = new RectangleFigure();
			((RectangleFigure)figure).setXOR(true);
			((RectangleFigure)figure).setFill(true);
			figure.setBackgroundColor(CustomColorRegistry.INSTANCE.getColorFromRegistry( 31, 31, 31));
			figure.setForegroundColor(ColorConstants.white);
		}
		
		return figure;
	}
	
	/**
	 * Returns the layer used for displaying feedback.
	 *  
	 * @return the feedback layer
	 */
	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}
	
	/**
	 * Returns the bounds of the host's figure by reference to be used to
	 * calculate the initial location of the feedback. The returned Rectangle
	 * should not be modified. Uses handle bounds if available.
	 * 
	 * @return the host figure's bounding Rectangle
	 */
	protected Rectangle getInitialFeedbackBounds() {
		return getHostFigure().getBounds();
	}

}
