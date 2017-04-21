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

 
package hydrograph.ui.propertywindow.handlers;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.runconfig.RunConfigDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;


/**
 * The Class Component.
 * 
 * @author Bitwise
 */

public class RunConfigHandler extends AbstractHandler implements IHandler {

	private Logger logger = LogFactory.INSTANCE.getLogger(RunConfigHandler.class);
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		 
		 RunConfigDialog runConfig = new RunConfigDialog(Display.getDefault().getActiveShell());
			try{
				runConfig.open();
			}catch(IllegalArgumentException e){
				MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", e.getMessage());
				logger.error("Failed to start run configuration : ",e);
			}
			catch(Exception e){
				MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Warning", "Please save the graph before setting the run configuration.");
				logger.error("Failed to start run configuration : ",e);
			}
		return null;
	}

}
