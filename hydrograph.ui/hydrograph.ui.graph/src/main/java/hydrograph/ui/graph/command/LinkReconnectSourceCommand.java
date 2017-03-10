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

 
package hydrograph.ui.graph.command;

import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.Port;
import hydrograph.ui.graph.model.PortDetails;
import hydrograph.ui.logging.factory.LogFactory;

import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;


/**
 * The Class LinkReconnectSourceCommand.
 */
public class LinkReconnectSourceCommand extends Command {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(LinkReconnectSourceCommand.class);
	
	private final Link link;

	private Component newSource;
	private String newSourceTerminal;
	private String oldSourceTerminal;
	private Component oldSource;
	private final  Component oldTarget;

	
	/**
	 * Instantiates a new link reconnect source command.
	 * 
	 * @param link
	 *            the link
	 */
	public LinkReconnectSourceCommand(Link link) {
		if (link == null) {
			throw new IllegalArgumentException();
		}
		this.link = link;
		this.oldSource = link.getSource();
		this.oldTarget = link.getTarget();
		setLabel("Source Reconnection");
	}

	@Override
	public boolean canExecute() {
		
		if (newSource != null){
			if (newSource.equals(oldTarget)) {
				return false;
			}

		// Out Port
		
		for (PortDetails p : newSource.getPortDetails()) {
			for(Port port:p.getPorts().values()){
				String portTerminal=port.getTerminal();
				if (portTerminal.equals(newSourceTerminal)) {
					if (p.isAllowMultipleLinks()
							|| !newSource.isOutputPortEngaged(newSourceTerminal)) {
						logger.debug("Reconnectable source {}", newSourceTerminal);
					} else{
						return false;
					}
				}
			}
		}
	 return true;
   }
		else{
			return false;
		}
	}

	@Override
	public void execute() {
		if (newSource != null) {
			link.detachSource();
			link.getSource().freeOutputPort(link.getSourceTerminal());
			
			link.setSource(newSource);
			link.setSourceTerminal(newSourceTerminal);
			
			oldSource.freeOutputPort(link.getSourceTerminal());
			oldSource.disconnectOutput(link);
			
			link.attachSource();
			newSource.engageOutputPort(newSourceTerminal);
			
			
		}

	}

	public void setNewSource(Component linkSource) {
		if (linkSource == null) {
			throw new IllegalArgumentException();
		}
		newSource = linkSource;
		
	}

	public void setNewSourceTerminal(String newSourceTerminal) {
		this.newSourceTerminal = newSourceTerminal;
	}
	
	public void setOldSource(Link w) {
		oldSource = w.getSource();
		oldSourceTerminal = w.getSourceTerminal();
	}
	
	@Override
	public void redo() {
		execute();
	}
	
	
	@Override
	public void undo(){
	
		newSource=link.getSource();
		logger.debug("New source is :{}", newSource.getProperties().get("name"));
		newSourceTerminal=link.getSourceTerminal();
		newSource.disconnectOutput(link);
		newSource.freeOutputPort(link.getSourceTerminal());
		link.detachSource();
		
		link.setSource(oldSource);
		logger.debug("Old source is :{}", oldSource.getProperties().get("name"));
		link.setSourceTerminal(oldSourceTerminal);
		link.attachSource();
		
	}
}