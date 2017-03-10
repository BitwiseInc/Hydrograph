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

 
package hydrograph.ui.common.interfaces.parametergrid;

import java.util.List;

import hydrograph.ui.datastructures.parametergrid.ParameterFile;

/**
 * The Interface DefaultGEFCanvas.
 * A base interface for Hydrograph canvas related tasks.
 * 
 * @author Bitwise
 */
public interface DefaultGEFCanvas {
	
	/**
	 * Gets the parameter file.
	 * 
	 * @return the parameter file
	 */
	public String getParameterFile();
	
	/**
	 * Gets the XML string.
	 * 
	 * @return the XML string
	 */
	public String getXMLString();
	
	/**
	 * Gets the latest parameter list.
	 * 
	 * @return the latest parameter list
	 */
	public List<String> getLatestParameterList();
	
	/**
	 * Gets the current parameter file path.
	 * 
	 * @return the current parameter file path
	 */
	public String getCurrentParameterFilePath();
	
	/**
	 * Sets the current parameter file path.
	 * 
	 * @param currentParameterFilePath
	 *            the new current parameter file path
	 */
	public void setCurrentParameterFilePath(String currentParameterFilePath);
	
	/**
	 * Gets the active project.
	 * 
	 * @return the active project
	 */
	public String getActiveProject();
	
	/**
	 * Gets the job name.
	 * 
	 * @return the job name
	 */
	public String getJobName();
	
	/**
	 * Disable running job resource.
	 */
	public void disableRunningJobResource();
	
	/**
	 * Enable running job resource.
	 */
	public void enableRunningJobResource();
	
	/**
	 * Sets the stop button status.
	 * 
	 * @param enabled
	 *            the new stop button status
	 */
	public void setStopButtonStatus(boolean enabled);
	
	/**
	 * Gets the stop button status.
	 * 
	 * @return the stop button status
	 */
	public boolean getStopButtonStatus();
	
	/**
	 * Restore menu tool context items state.
	 */
	public void restoreMenuToolContextItemsState();

	/**
	 * 
	 * Add job level parameter files in component canvas
	 * 
	 * @param jobLevelParamterFiles
	 */
	public void addJobLevelParamterFiles(List<ParameterFile> jobLevelParamterFiles);

	/**
	 * 
	 * Get list of job level parameter files
	 * 
	 * @return
	 */
	public List<ParameterFile> getJobLevelParamterFiles(); 
	
	/**
	 * Returns unique job id
	 * 
	 * @return unique job id
	 */
	public String getUniqueJobId();
	
	public void saveParamterFileSequence(List<ParameterFile> parameterFiles);
	
	public List<ParameterFile> getParamterFileSequence();
	
}
