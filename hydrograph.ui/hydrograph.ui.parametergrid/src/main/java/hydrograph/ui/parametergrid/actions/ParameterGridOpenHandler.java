/********************************************************************************
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
 ******************************************************************************/

package hydrograph.ui.parametergrid.actions;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.util.MultiParameterFileUIUtils;
import hydrograph.ui.datastructures.parametergrid.ParameterFile;
import hydrograph.ui.datastructures.parametergrid.filetype.ParamterFileTypes;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.parametergrid.constants.ErrorMessages;
import hydrograph.ui.parametergrid.constants.MessageType;
import hydrograph.ui.parametergrid.dialog.MultiParameterFileDialog;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;



/**
 * 
 * Handler to open parameter grid
 * 
 * @author Bitwise
 * 
 */
public class ParameterGridOpenHandler extends AbstractHandler {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ParameterGridOpenHandler.class);
	/**
	 * 
	 * Returns active editor as {@link DefaultGEFCanvas}
	 * 
	 * @return {@link DefaultGEFCanvas}
	 */
	private DefaultGEFCanvas getComponentCanvas() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas){
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
		}
		else{
			return null;
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		File jobSpecificParamFile = new File(getComponentCanvas().getParameterFile());
		if(!jobSpecificParamFile.exists()){
			try {
				jobSpecificParamFile.createNewFile();
			} catch (IOException e) {
				logger.debug("Unable to create job specific file ", e);
				MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);

				messageBox.setText(MessageType.ERROR.messageType());
				messageBox.setMessage(ErrorMessages.UNABLE_TO_POPULATE_PARAM_FILE + e.getMessage());
				messageBox.open();
			}
		}

		String activeProjectLocation = MultiParameterFileUIUtils.getActiveProjectLocation();

		List<ParameterFile> parameterFileList = getParameterFileList(activeProjectLocation);

		parameterFileList.addAll(getComponentCanvas().getJobLevelParamterFiles());
		
		MultiParameterFileDialog parameterFileDialog = new MultiParameterFileDialog(new Shell(), activeProjectLocation);
		parameterFileDialog.setParameterFiles(parameterFileList);
		parameterFileDialog.setJobLevelParamterFiles(getComponentCanvas().getJobLevelParamterFiles());
		parameterFileDialog.open();

		return null;
	}

	private List<ParameterFile> getParameterFileList(String activeProjectLocation) {
		List<ParameterFile> parameterFileList = new LinkedList<>();
        updateParameterFileListWithJobSpecificFile(parameterFileList,activeProjectLocation);

		return parameterFileList;
	}

	private void updateParameterFileListWithJobSpecificFile(List<ParameterFile> parameterFileList, String activeProjectLocation) {
		parameterFileList.add(new ParameterFile(getComponentCanvas().getJobName(),ParamterFileTypes.JOB_SPECIFIC));
	}
}
