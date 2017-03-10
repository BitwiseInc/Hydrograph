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

package hydrograph.ui.dataviewer.support;

import hydrograph.ui.dataviewer.adapters.DataViewerAdapter;
import hydrograph.ui.dataviewer.constants.Messages;
import hydrograph.ui.dataviewer.constants.StatusConstants;
import hydrograph.ui.dataviewer.constants.ControlConstants;
import hydrograph.ui.dataviewer.datastructures.StatusMessage;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * The Class StatusManager.
 * Provides mechanism for Data viewer status and controls manager
 * 
 * @author Bitwise
 *
 */
public class StatusManager {
	private static final String STATUS_MESSAGE_SEPARATOR = " | ";
	private StatusLineManager statusLineManager;
	private DataViewerAdapter dataViewerAdapter;
	private Map<String,Control> windowControls;
	
	/**
	 * 
	 * Get data viewer status bar
	 * 
	 * @return {@link StatusLineManager}
	 */
	public StatusLineManager getStatusLineManager() {
		return statusLineManager;
	}
	
	/**
	 * 
	 * Set {@link DataViewerAdapter}
	 * 
	 * @param dataViewerAdapter
	 */
	public void setDataViewerAdapter(DataViewerAdapter dataViewerAdapter) {
		this.dataViewerAdapter = dataViewerAdapter;
	}

	/**
	 * 
	 * Set list of window controls
	 * 
	 * @param windowControls
	 */
	public void setWindowControls(Map<String,Control> windowControls) {
		this.windowControls = windowControls;
	}

	/**
	 * 
	 * Set {@link StatusLineManager}
	 * 
	 * @param statusLineManager
	 */
	public void setStatusLineManager(StatusLineManager statusLineManager) {
		this.statusLineManager = statusLineManager;
	}	
	
	/**
	 * 
	 * Set status in status data viewer status bar
	 * 
	 * @param status
	 */
	public void setStatus(StatusMessage status) {
		
		statusLineManager.setErrorMessage(null);
		
		if (status.getReturnCode() == StatusConstants.ERROR) {
			statusLineManager.setErrorMessage(status.getStatusMessage());
			return;
		}
		
		if (status.getReturnCode() == StatusConstants.PROGRESS) {
			statusLineManager.setMessage(status.getStatusMessage());
			return;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		if (dataViewerAdapter.getFileData().size() != 0) {
			stringBuilder.append(Messages.SHOWING_RECORDS_FROM + " " + (dataViewerAdapter.getOffset() + 1) + " to "
					+ (dataViewerAdapter.getOffset() + dataViewerAdapter.getFileData().size())
					+ STATUS_MESSAGE_SEPARATOR);
		} else {
			stringBuilder.append(Messages.NO_MATCHING_RECORDS + STATUS_MESSAGE_SEPARATOR);
		}
		
		
		if (dataViewerAdapter.getRowCount() != null) {
			stringBuilder.append(Messages.ROW_COUNT + " " + dataViewerAdapter.getRowCount()
					+ STATUS_MESSAGE_SEPARATOR);
		} 
		
		if (!StringUtils.isEmpty(status.getStatusMessage())){
			stringBuilder.append(status.getStatusMessage() + STATUS_MESSAGE_SEPARATOR);
		}
			
		
				
		
		statusLineManager.setMessage(stringBuilder.toString().substring(0,
				stringBuilder.length() - 2));
		
		updatePageNumberDisplayPanel();
	}
	
	/**
	 * Append status message
	 * 
	 * @param message
	 */
	public void appendStatusMessage(String message) {
		statusLineManager.setMessage(STATUS_MESSAGE_SEPARATOR + message);
	}
		
	/**
	 * 
	 * Enable/Disable jump page panel from data viewer window 
	 * 
	 * @param enabled
	 */
	public void enableJumpPagePanel(boolean enabled){
		windowControls.get(ControlConstants.JUMP_BUTTON).setEnabled(enabled);
		windowControls.get(ControlConstants.JUMP_TEXT).setEnabled(enabled);
		if(dataViewerAdapter.getRowCount()!=null){
			if(((long)dataViewerAdapter.getTotalNumberOfPages()) == dataViewerAdapter.getCurrentPageNumber()){
				windowControls.get(ControlConstants.NEXT_BUTTON).setEnabled(false);
			}
		}
	}
	
	/**
	 * 
	 * Enable/Disable NEXT/PREVIOUS page buttons from data viewer window 
	 * 
	 * @param enabled
	 */
	public void enablePageSwitchPanel(boolean enabled){
		windowControls.get(ControlConstants.PREVIOUS_BUTTON).setEnabled(enabled);
		windowControls.get(ControlConstants.NEXT_BUTTON).setEnabled(enabled);
	}
	
	/**
	 * 
	 * Enable/Disable NEXT page buttons from data viewer window 
	 * 
	 * @param enabled
	 */
	public void enableNextPageButton(boolean enabled){
		windowControls.get(ControlConstants.NEXT_BUTTON).setEnabled(enabled);
		
	}
	
	/**
	 * 
	 * Enable/Disable PREVIOUS page buttons from data viewer window 
	 * 
	 * @param enabled
	 */
	public void enablePreviousPageButton(boolean enabled){
		windowControls.get(ControlConstants.PREVIOUS_BUTTON).setEnabled(enabled);
	}
	
	/**
	 * 
	 * Enable/Disable Pagination Panel from data viewer window 
	 * 
	 * @param enabled
	 */
	public void enablePaginationPanel(boolean enabled){
		for(String control:windowControls.keySet()){
			windowControls.get(control).setEnabled(enabled);
		}
	}
	
	/**
	 * Update page number display panel from data viewer window 
	 * 
	 */
	public void updatePageNumberDisplayPanel(){
		((Text)windowControls.get(ControlConstants.PAGE_NUMBER_DISPLAY)).setText(dataViewerAdapter.getPageStatus());
	}

	/**
	 * 
	 * Set all controls enabled or disabled based status of row count
	 * 
	 * @param enabled
	 */
	public void setAllWindowControlsEnabled(boolean enabled) {
		for(String control: windowControls.keySet()){
			windowControls.get(control).setEnabled(enabled);
		}
		
		if(dataViewerAdapter.getRowCount()==null){
			enableJumpPagePanel(false);
		}else{		
			if(((long)dataViewerAdapter.getTotalNumberOfPages()) == dataViewerAdapter.getCurrentPageNumber()){
				enableNextPageButton(false);
			}
		}
		
		if(dataViewerAdapter.getCurrentPageNumber()==1){
			enablePreviousPageButton(false);
		}
	}

	/**
	 * 
	 * Clear jump page text box from data viewer window 
	 * 
	 */
	public void clearJumpToPageText(){
		((Text)windowControls.get(ControlConstants.JUMP_TEXT)).setText("");
	}
	
	/**
	 * Enables initial pagination panel controls
	 * 
	 */
	public void enableInitialPaginationContols(){
		enablePageSwitchPanel(true);
		if(dataViewerAdapter.getRowCount()!=null){
			if(((long)dataViewerAdapter.getTotalNumberOfPages()) == dataViewerAdapter.getCurrentPageNumber()){
				enableNextPageButton(false);
			}else{
				enableNextPageButton(true);
			}
		}
		
		if(dataViewerAdapter.getCurrentPageNumber()==1){
			enablePreviousPageButton(false);
		}
		if(dataViewerAdapter.getRowCount()==null){
			enableJumpPagePanel(false);
		}else{
			enableJumpPagePanel(true);
		}
	}	
}

