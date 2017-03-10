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

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import hydrograph.ui.graph.figure.CommentBoxFigure;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.policy.CommentBoxDirectEditPolicy;

/**
 * 
 * Default implementation for {@link AbstractGraphicalEditPart} and {@link PropertyChangeListener}
 * 
 * CommentBoxEditPart is a controller class for comment box and The documentation here is targeted at subclassing this
 * class. Callers of public API should refer to the interface's documentation.
 * 
 * @author Bitwise
 * 
 */
public class CommentBoxEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener

{
	private AccessibleEditPart accessibleEditPart;
	private String LABEL_CONTENTS = "labelContents";
	private String SIZE = "Size";
	private String LOCATION = "Location";
	private String LABEL = "Label";

	/**
	 * Upon activation, attach to the model element as a property change listener.
	 */
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			((CommentBox) getModel()).addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (!isActive())
			return;
		super.deactivate();
		((CommentBox) getModel()).removePropertyChangeListener(this);
	}

	@Override
	protected AccessibleEditPart getAccessibleEditPart() {
		if (accessibleEditPart == null)
			accessibleEditPart = createAccessible();
		return accessibleEditPart;
	}

	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getValue(AccessibleControlEvent e) {
				e.result = getLabel().getLabelContents();
			}

			public void getName(AccessibleEvent e) {
				e.result = LABEL;
			}
		};
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new CommentBoxDirectEditPolicy());
	}

	@Override
	protected IFigure createFigure() {
		CommentBoxFigure label = new CommentBoxFigure();
		Point loc = getLabel().getLocation();
		Dimension size = getLabel().getSize();
		Rectangle r = new Rectangle(loc, size);
		label.setBounds(r);
		if (label.getSize() != getLabel().getSize()) {
			label.setSize(getLabel().getSize());
		}
		return label;
	}

	/**
	 * returns the model of comment box
	 * 
	 * @return CommentBox
	 */
	private CommentBox getLabel() {
		return (CommentBox) getModel();
	}

	/**
	 * return figure
	 * 
	 * @return figure
	 */
	public IFigure getCommentBoxFigure() {
		return (CommentBoxFigure) getFigure();
	}

	@Override
	public void performRequest(Request request) {
		CommentBoxEditor commentBoxEditor = new CommentBoxEditor(
				((CommentBoxFigure) getFigure()).getComponentCanvas().getCanvasControl().getShell(), 0,
				((CommentBoxFigure) getFigure()), getLabel());
		commentBoxEditor.open();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (StringUtils.equalsIgnoreCase(prop, LABEL_CONTENTS)) {
			refreshVisuals();
		} else if (StringUtils.equalsIgnoreCase(prop, SIZE) || StringUtils.equalsIgnoreCase(prop, LOCATION)) {
			Point loc = getLabel().getLocation();
			Dimension size = getLabel().getSize();
			Rectangle r = new Rectangle(loc, size);
			((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
			refreshVisuals();
		}
	}

	@Override
	protected void refreshVisuals() {
		((CommentBoxFigure) getFigure()).setText(getLabel().getLabelContents(),(CommentBox)getModel());
	}

}
