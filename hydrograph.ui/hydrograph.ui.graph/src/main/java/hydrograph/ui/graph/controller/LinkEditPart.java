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

import hydrograph.ui.graph.figure.ELTFigureConstants;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.policy.LinkEditPolicy;
import hydrograph.ui.graph.policy.LinkEndPointEditPolicy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RoutingAnimator;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;


/**
 * The Class LinkEditPart.
 * @author Bitwise
 */
public class LinkEditPart extends AbstractConnectionEditPart
		implements PropertyChangeListener {
	
	private Font recordCountFont;
	/**
	 * Upon activation, attach to the model element as a property change
	 * listener.
	 */
	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			((Link) getModel()).addPropertyChangeListener(this);
		}
	}

	/**
	 * Upon deactivation, detach from the model element as a property change
	 * listener.
	 */
	@Override
	public void deactivate() {
		if (isActive()) {
			if(recordCountFont!=null){
				recordCountFont.dispose();
			}
			super.deactivate();
			((Link) getModel())
					.removePropertyChangeListener(this);
		}
	}

	@Override
	protected IFigure createFigure() {

		PolylineConnection connection = (PolylineConnection) super
				.createFigure();
		connection.addRoutingListener(RoutingAnimator.getDefault());
		connection.setTargetDecoration(new PolygonDecoration());
		connection.setLineStyle(getCastedModel().getLineStyle());
		return connection;
	}

	@Override
	protected void createEditPolicies() {
		// Selection handle edit policy. Makes the connection show a feedback,
		// when selected by the user.
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new LinkEndPointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new LinkEditPolicy());
	}

	private Link getCastedModel() {
		return (Link) getModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String prop = event.getPropertyName();
		if ("record_count".equals(prop))
		{
			refreshVisuals();
		} 
	}
	
	@Override
	protected void refreshVisuals() {
		Connection connection = (Connection) getFigure();
		Boolean labelAlreadyExists = false;
		if(recordCountFont==null || recordCountFont.isDisposed()){
			recordCountFont = new Font(Display.getDefault(), ELTFigureConstants.labelFont, 8, SWT.NORMAL);
		}
		for(Object figure:connection.getChildren()){
			if(figure instanceof Label){
				labelAlreadyExists = true;
				((Label) figure).setFont(recordCountFont);
				((Label) figure).setText(getCastedModel().getRecordCount());
			}
		}
		if(!labelAlreadyExists){
			Label endLabel = new Label(getCastedModel().getRecordCount());
			endLabel.setFont(recordCountFont);
			endLabel.setText(getCastedModel().getRecordCount());
			ConnectionEndpointLocator ce = new ConnectionEndpointLocator(connection, false);
			connection.add(endLabel,ce);
		}
		
		connection.repaint();
	}
	
	/**
	 * Clear record count from job canvas after rerun or refresh.
	 */
	public void clearRecordCountAtPort(){
		List<Figure> labels = new ArrayList<>(); 
		Connection connection = (Connection) getFigure();
		for(Object figure:connection.getChildren()){
			if(figure instanceof Label){
				labels.add((Figure) figure);
			}
		}
		for (Figure label : labels) {
			connection.remove(label);
		}
	}
	
}
