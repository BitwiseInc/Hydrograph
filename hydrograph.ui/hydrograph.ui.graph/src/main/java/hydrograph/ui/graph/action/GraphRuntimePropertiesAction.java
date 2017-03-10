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
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.graph.controller.ContainerEditPart;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;


public class GraphRuntimePropertiesAction  extends SelectionAction{
	private PasteAction pasteAction;
	
	public GraphRuntimePropertiesAction(IWorkbenchPart part, IAction action) {
		super(part);
		this.pasteAction = (PasteAction) action;
		setLazyEnablementCalculation(true);
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedObjects()!=null && getSelectedObjects().size()==1 && getSelectedObjects().get(0) instanceof ContainerEditPart)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
	 */
	@Override
	protected void init() {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		super.init();
		setText(Constants.GRAPH_PROPERTY); 
		setId(Constants.GRAPH_PROPERTY);
		setHoverImageDescriptor(getImageDisDescriptor());
		setImageDescriptor(getImageDisDescriptor());
		setDisabledImageDescriptor(getImageDisDescriptor());
		setEnabled(false);
	}
		
	private ImageDescriptor getImageDisDescriptor() {
		ImageDescriptor imageDescriptor = new ImageDescriptor() {

			@Override
			public ImageData getImageData() {
				return new ImageData(XMLConfigUtil.INSTANCE.CONFIG_FILES_PATH+"/icons/graph_property_window.png");
			}
		};
		return imageDescriptor;
	}
	
	@Override
	public void run() {
		IHandlerService handlerService = (IHandlerService)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
		try {
		  handlerService.executeCommand(Constants.GRAPH_PROPERTY_COMMAND_ID, null);
		  } catch ( ExecutionException| NotDefinedException| NotEnabledException| NotHandledException ex) {
		    throw new RuntimeException(Constants.GRAPH_PROPERTY_COMMAND_ID + "not found");
		    
		    }
	}

}
