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

import org.eclipse.draw2d.Graphics;
import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;


/**
 *  The Class LinkCommand.
 *  @author Bitwise
 */
public class LinkCommand extends Command{
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(LinkCommand.class);
	
	/** The connection instance. */
	private Link connection;
	private Component source, target;
	private String sourceTerminal, targetTerminal;

	/**
	 * Instantiate a command that can create a connection between two shapes.
	 * @param source the source endpoint (a non-null Shape instance)
	 * @param lineStyle the desired line style. See Connection#setLineStyle(int) for details
	 * @throws IllegalArgumentException if source is null
	 * @see Link#setLineStyle(int)
	 */

	public LinkCommand() {
		super("connection");
	}

	/**
	 * Instantiates a new link command.
	 * 
	 * @param source
	 *            the source
	 * @param lineStyle
	 *            the line style
	 */
	public LinkCommand(Component source, int lineStyle) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		setLabel("Connection");
		this.source = source;
	}

	@Override
	public boolean canExecute() {
		
		if(source!=null){
			//disallow the link to itself
			if (source.equals(target)) {
				return false;
			}

			//Out port restrictions

			for (PortDetails p:source.getPortDetails())
			{
				for(Port port:p.getPorts().values()){
					String portTerminal=port.getTerminal();
					if(portTerminal.equals(sourceTerminal) && port.getPortAlignment().equals(p.getPortAlignment())){
						if(p.isAllowMultipleLinks() || 
								!source.isOutputPortEngaged(sourceTerminal)){
							logger.debug("connectable source {}", sourceTerminal);
						}else{
							logger.debug("non-connectable source {}",sourceTerminal);
							return false;
						}
					}
				}
				
			}

		}	

		//In port restrictions
		if(target!=null){
			
			for (PortDetails p:target.getPortDetails())
			{
				for(Port port:p.getPorts().values()){
					String portTerminal=port.getTerminal();
					if(portTerminal.equals(targetTerminal) && port.getPortAlignment().equals(p.getPortAlignment())){
						if(p.isAllowMultipleLinks() ||
								!target.isInputPortEngaged(targetTerminal)){
							logger.debug("connectable target {}",targetTerminal);
						}else{
							logger.debug("non-connectable target {}",targetTerminal);
							return false;
						}
					}
				}
				
			}
		}


		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {

		if(source!=null && target!=null){

			connection.setSource(source);
			connection.setSourceTerminal(sourceTerminal);
			connection.setLineStyle(Graphics.LINE_SOLID);
			connection.attachSource();

			source.engageOutputPort(sourceTerminal);
			
		}
		if(target!=null){

			connection.setTarget(target);
			connection.setTargetTerminal(targetTerminal);
			connection.setLineStyle(Graphics.LINE_SOLID);
			connection.attachTarget();

			target.engageInputPort(targetTerminal);
			
		}
		
	}
	

	public void setTarget(Component target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		this.target = target;
	}

	public void setSource(Component newSource) {
		if (newSource == null) {
			throw new IllegalArgumentException();
		}
		source = newSource;
	}

	public void setSourceTerminal(String newSourceTerminal) {
		sourceTerminal = newSourceTerminal;
	}

	public void setTargetTerminal(String newTargetTerminal) {
		targetTerminal = newTargetTerminal;
	}


	public void setConnection(Link link) {

		connection = link;
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {

		source = connection.getSource();
		target = connection.getTarget();

		if(source!=null && target!=null){
			logger.debug("Undo link creation");
			sourceTerminal = connection.getSourceTerminal();
			targetTerminal = connection.getTargetTerminal();

			connection.detachSource();
			connection.detachTarget();

			source.freeOutputPort(connection.getSourceTerminal());
			target.freeInputPort(connection.getTargetTerminal());
		}

	}
}