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

import hydrograph.ui.graph.command.LinkCommand;
import hydrograph.ui.graph.command.LinkReconnectSourceCommand;
import hydrograph.ui.graph.command.LinkReconnectTargetCommand;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;


/**
 * The Class NodeConnectionEditPolicy.
 */
public class NodeConnectionEditPolicy extends GraphicalNodeEditPolicy{

	@Override
	protected Command getConnectionCreateCommand(
			CreateConnectionRequest request) {

		LinkCommand command = new LinkCommand();
		command.setConnection(new Link());
		command.setSource(getComponentEditPart().getCastedModel());
		ConnectionAnchor ctor = getComponentEditPart().getSourceConnectionAnchor(
				request);
		if (ctor == null)
			return null;

		command.setSourceTerminal(getComponentEditPart()
				.mapConnectionAnchorToTerminal(ctor));

		request.setStartCommand(command);
		return command;

	}

	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {

		LinkCommand command = (LinkCommand) request
				.getStartCommand();
		command.setTarget(getComponentEditPart().getCastedModel());
		ConnectionAnchor ctor = getComponentEditPart().getTargetConnectionAnchor(
				request);
		if (ctor == null)
			return null;


		command.setTargetTerminal(getComponentEditPart()
				.mapConnectionAnchorToTerminal(ctor));

		return command;
	}

	@Override
	protected Command getReconnectSourceCommand(
			ReconnectRequest request) {
		
		Link link=(Link)request.getConnectionEditPart().getModel();
		Component comp=getComponentEditPart().getCastedModel();
		
		LinkReconnectSourceCommand cmd=new LinkReconnectSourceCommand(link);
		cmd.setOldSource(link);
		cmd.setNewSource(comp);
		ConnectionAnchor anchor=getComponentEditPart().getSourceConnectionAnchor(request);
		if(anchor==null){
			return null;
		}
		cmd.setNewSourceTerminal(getComponentEditPart().mapConnectionAnchorToTerminal(anchor));
		
	
		return cmd;
	}
	@Override
	protected Command getReconnectTargetCommand(
			ReconnectRequest request) {
		Link link=(Link)request.getConnectionEditPart().getModel();
		Component component=getComponentEditPart().getCastedModel();
		ConnectionAnchor anchor=getComponentEditPart().getTargetConnectionAnchor(request);
		if(anchor==null){
			return null;
		}
		LinkReconnectTargetCommand command=new LinkReconnectTargetCommand(link);
		command.setOldTarget(link);
		command.setNewTarget(component);
		command.setNewTargetTerminal(getComponentEditPart().mapConnectionAnchorToTerminal(anchor));
		
		return command;
	}


	protected ComponentEditPart getComponentEditPart() {
		return (ComponentEditPart) getHost();
	}


}
