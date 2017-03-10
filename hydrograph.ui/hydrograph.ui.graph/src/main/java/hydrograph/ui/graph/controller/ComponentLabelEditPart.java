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


import hydrograph.ui.graph.figure.ComponentLabelFigure;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.ComponentLabel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;


/**
 * The Class ComponentLabelEditPart.
 * @author Bitwise
 */

public class ComponentLabelEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener{
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getValue(AccessibleControlEvent e) {
				e.result = getLogicLabel().getLabelContents();
			}

			public void getName(AccessibleEvent e) {
				e.result = "Label";
			}
		};
	}

	
	protected IFigure createFigure() {
		ComponentLabelFigure label = new ComponentLabelFigure(1);
		Component component = ((ComponentEditPart) getParent()).getCastedModel();
		String compLabel = component.getComponentLabel().getLabelContents();
		
		label.setText(compLabel);
		Point p = new Point(((component.getSize().width/2) - 49), 1);
		label.setLocation(p);
		return label;
	}

	private ComponentLabel getLogicLabel() {
		return (ComponentLabel) getModel();
	}


	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("labelContents"))//$NON-NLS-1$
			refreshVisuals();
		else if (evt.getPropertyName().equalsIgnoreCase("size"))
			refreshVisuals();
	}

	protected void refreshVisuals() {
		
		((ComponentLabelFigure) getFigure()).setText(getLogicLabel()
				.getLabelContents());
		super.refreshVisuals();
	}


	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void deactivate() {
		if (isActive()) {
			((ComponentLabelFigure) getFigure()).disposeFont();
			super.deactivate();
		}
	}

	
	/**
	 * Adjusts the position of the component label at center in case if port count is increased
	 * 
	 * @param component's position
	 * @param component's size
	 */
	public void adjustLabelFigure(Point compLocation, Dimension compSize) {
		Point p = new Point((compLocation.x + 1 +(compSize.width/2) - 50), (compLocation.y + 1));
		getFigure().setLocation(p);
		
	}

}
