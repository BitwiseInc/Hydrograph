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

package hydrograph.ui.graph.action;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.propertywindow.ELTPropertyWindow;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;


public class ComponentPropertiesAction extends SelectionAction{


	public ComponentPropertiesAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		super.init();
		setText(Messages.PROPERTIES);
		setId(Constants.COMPONENT_PROPERTIES_ID);
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		ISelection selection = getSelection();
		if(selection instanceof IStructuredSelection){		
		IStructuredSelection currentSelectedComponent = (IStructuredSelection) getSelection();
		if (currentSelectedComponent.getFirstElement() instanceof ComponentEditPart)
			return true;
		}
		return false;
	}
	
	@Override
	public void run() {
		super.run();
		IStructuredSelection currentSelectedComponent = (IStructuredSelection) getSelection();
		if (currentSelectedComponent.getFirstElement() instanceof ComponentEditPart) {
			ComponentEditPart componentEditPart = ((ComponentEditPart) currentSelectedComponent.getFirstElement());
			Object componentModel=componentEditPart.getCastedModel();
			ELTPropertyWindow eltPropertyWindow = new ELTPropertyWindow(componentModel);
			eltPropertyWindow.open(); 
			
			componentEditPart.updateComponentView(eltPropertyWindow);
		}
	}
}
