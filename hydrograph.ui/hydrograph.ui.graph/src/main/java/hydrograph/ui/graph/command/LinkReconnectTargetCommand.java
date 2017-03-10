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
 * The Class LinkReconnectTargetCommand.
 */
public class LinkReconnectTargetCommand extends Command{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(LinkReconnectTargetCommand.class);
	
	Link link;
	private Component oldTarget;
	private Component newTarget;
	private String oldTargetTerminal;
	private String newTargetTerminal;
	private final Component oldSource;
	
	/**
	 * Instantiates a new link reconnect target command.
	 * 
	 * @param link
	 *            the link
	 */
	public LinkReconnectTargetCommand(Link link){
		if (link == null) {
			throw new IllegalArgumentException();
		}
		this.link=link;
		this.oldSource=link.getSource();
		this.oldTarget = link.getTarget();
		setLabel("Target Reconnection");
	}
	
	
	@Override
	public boolean canExecute(){
		
		if(newTarget != null){
			if(newTarget.equals(oldSource)){
				return false;
			}
			
			for (PortDetails p : newTarget.getPortDetails())
			{
				for(Port port:p.getPorts().values()){
					String portTerminal=port.getTerminal();
					if(portTerminal.equals(newTargetTerminal)){
						if(p.isAllowMultipleLinks() ||
								!newTarget.isInputPortEngaged(newTargetTerminal)){
							logger.debug("Reconnectable source {}", newTargetTerminal);
						}else{
							return false;
						}
					}
				}
			}
		}

		return true;
	}
	
	@Override
	public void execute(){
		if(newTarget != null){
			link.detachTarget();
			link.getTarget().freeInputPort(link.getTargetTerminal());
			
			link.setTarget(newTarget);
			link.setTargetTerminal(newTargetTerminal);
						
			oldTarget.freeInputPort(link.getTargetTerminal());
			oldTarget.disconnectInput(link);

			link.attachTarget();
			newTarget.engageInputPort(newTargetTerminal);
			
		}
	}
	
	public void setNewTarget(Component linkTarget) {
		if (linkTarget == null) {
			throw new IllegalArgumentException();
		}
		newTarget = linkTarget;
	}
	
	public void setNewTargetTerminal(String newTargetTerminal){
		this.newTargetTerminal=newTargetTerminal;
	}
	
	public void setOldTarget(Link w){
		oldTarget=w.getTarget();
		oldTargetTerminal=w.getTargetTerminal();
	}
	
	@Override
	public void redo() {
		execute();
	}
	
	@Override
	public void undo(){
		
		newTarget=link.getTarget();
		newTargetTerminal=link.getTargetTerminal();
		newTarget.disconnectInput(link);
		newTarget.freeInputPort(link.getTargetTerminal());
		link.detachTarget();
		
		link.setTarget(oldTarget);
		link.setTargetTerminal(oldTargetTerminal);
		link.attachTarget();
		
	}

}
