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

import hydrograph.ui.graph.figure.ComponentFigure;
import hydrograph.ui.graph.figure.PortFigure;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;


/**
 * 
 * @author Bitwise
 * Oct 15, 2015
 * 
 */

public class ComponentSelectionPolicy extends SelectionEditPolicy {

	protected List handles;
	
	@Override
	protected void hideSelection() {		
		((ComponentFigure)getHostFigure()).setComponentColorAndBorder();
		hideLabelSelection();
	}

	@Override
	protected void showSelection() {
		((ComponentFigure)getHostFigure()).setSelectedComponentColorAndBorder();
		showLabelSelection();
		
	}

	
	private void showLabelSelection(){
		List<Figure> childrenFigures = ((ComponentFigure)getHostFigure()).getChildren();
		if (!childrenFigures.isEmpty()){
			for(Figure figure:childrenFigures)
			{
				if(figure instanceof PortFigure)
					((PortFigure) figure).selectPort();
			}
		}
	}
	
	private void hideLabelSelection(){
		List<Figure> childrenFigures = ((ComponentFigure)getHostFigure()).getChildren();
		if (!childrenFigures.isEmpty()){
			for(Figure figure:childrenFigures)
			{
				if(figure instanceof PortFigure)
					((PortFigure) figure).deSelectPort();
			}
		}
	}

}
