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

 
package hydrograph.ui.graph.factory;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.slf4j.Logger;

import hydrograph.ui.graph.controller.CommentBoxEditPart;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.ComponentLabelEditPart;
import hydrograph.ui.graph.controller.ContainerEditPart;
import hydrograph.ui.graph.controller.LinkEditPart;
import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.ComponentLabel;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.Port;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * Factory class to create the edit part for the given model.
 */
public class ComponentsEditPartFactory implements EditPartFactory{
	private static Logger logger = LogFactory.INSTANCE.getLogger(ComponentsEditPartFactory.class);
	/**
	 * Creates edit parts for given model.
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		// get EditPart for model element
		EditPart part = null;
		if (model instanceof Container) {
			part = new ContainerEditPart();
		}
		else if (model instanceof Component) {
			part = new ComponentEditPart();
		}
		else if (model instanceof Link) {
			part = new LinkEditPart();
		}
		else if (model instanceof Port){
			part = new PortEditPart();
		}
		else if (model instanceof ComponentLabel){
			part = new ComponentLabelEditPart();
		}
		else if (model instanceof CommentBox){
			part = new CommentBoxEditPart();
		}
		else{
			logger.error("Can't create edit part for model element {}", ((model != null) ? model.getClass().getName() : "null"));
			throw new RuntimeException("Can't create edit part for model element: "	+ 
						((model != null) ? model.getClass().getName() : "null"));
		}
		logger.trace("Created edit part for : {}", model.getClass().getName()); 
		// store model element in EditPart
		part.setModel(model);
		return part;
	}
}
