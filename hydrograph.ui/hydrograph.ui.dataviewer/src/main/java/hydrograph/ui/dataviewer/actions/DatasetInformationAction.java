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
package hydrograph.ui.dataviewer.actions;


import java.util.HashMap;
import java.util.Map;

import hydrograph.ui.common.datastructures.dataviewer.JobDetails;
import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.FieldDataTypes;
import hydrograph.ui.common.schema.Fields;
import hydrograph.ui.dataviewer.adapters.DataViewerAdapter;
import hydrograph.ui.dataviewer.constants.AdapterConstants;
import hydrograph.ui.dataviewer.datasetinformation.DatasetInformationVO;
import hydrograph.ui.dataviewer.datasetinformation.DatasetInformationDialog;
import hydrograph.ui.dataviewer.filter.FilterHelper;
import hydrograph.ui.dataviewer.preferencepage.ViewDataPreferencesVO;
import hydrograph.ui.dataviewer.utilities.ViewDataSchemaHelper;
import hydrograph.ui.dataviewer.window.DebugDataViewer;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

/**
 * The Class DatasetInformationAction.
 * Responsible to show information for View Data debug file
 * @author Bitwise
 */

public class DatasetInformationAction extends Action {

	private static final String LABEL = "Dataset &Information";
	private final String DEBUG_DATA_FILE_EXTENTION=".csv";
	private DebugDataViewer debugDataViewer;
	/**
	 *
	 * @param debugDataViewer
	 */
	
	public DatasetInformationAction(DebugDataViewer debugDataViewer) {
		super(LABEL);
		this.debugDataViewer=debugDataViewer;
		
	}

	@Override
	public void run() {
		
			ViewDataPreferencesVO viewDataPreferencesVO;
			DataViewerAdapter csvAdapter;
			String debugFileLocation= new String();
			String debugFileName="";
			double downloadedFileSize;
			JobDetails jobDetails = debugDataViewer.getJobDetails();
 	
			debugFileLocation = debugDataViewer.getDebugFileLocation();
			debugFileName = debugDataViewer.getDebugFileName();
			downloadedFileSize = debugDataViewer.getDownloadedFileSize();
			csvAdapter=debugDataViewer.getDataViewerAdapter();
			viewDataPreferencesVO=debugDataViewer.getViewDataPreferences();
  	
			DatasetInformationDialog datasetInformationDetailDialog = new DatasetInformationDialog(Display.getCurrent().getActiveShell());
 
			DatasetInformationVO datasetInformationVO = new DatasetInformationVO();
			datasetInformationVO.setChunkFilePath(debugFileLocation + debugFileName + DEBUG_DATA_FILE_EXTENTION);
			datasetInformationVO.setDelimeter(viewDataPreferencesVO.getDelimiter());
			datasetInformationVO.setEdgeNode(jobDetails.getHost());
			datasetInformationVO.setNoOfRecords(Long.toString(csvAdapter.getRowCount()));
			datasetInformationVO.setPageSize(Integer.toString(viewDataPreferencesVO.getPageSize()));
			datasetInformationVO.setAcctualFileSize(String.valueOf(downloadedFileSize));
			datasetInformationVO.setQuote(viewDataPreferencesVO.getQuoteCharactor());
			datasetInformationVO.setViewDataFilePath(jobDetails.getBasepath());
			datasetInformationVO.setSizeOfData(Integer.toString(viewDataPreferencesVO.getFileSize()));
			datasetInformationVO.setUserName(jobDetails.getUsername());
			datasetInformationDetailDialog.setData(datasetInformationVO,debugDataViewer,jobDetails);
		if (debugDataViewer.getConditions() != null) {
			StringBuffer remoteFilterCondition = FilterHelper.INSTANCE.getCondition(debugDataViewer.getConditions()
					.getRemoteConditions(), getFieldsAndTypes(), debugDataViewer.getConditions()
					.getRemoteGroupSelectionMap(), true);
			StringBuffer localFilterCondition = FilterHelper.INSTANCE.getCondition(debugDataViewer.getConditions()
					.getLocalConditions(), getFieldsAndTypes(), debugDataViewer.getConditions()
					.getLocalGroupSelectionMap(), true);
			datasetInformationVO.setLocalFilter(localFilterCondition.toString());
			datasetInformationVO.setRemoteFilter(remoteFilterCondition.toString());
		}
			datasetInformationDetailDialog.open();
	}
	private Map<String, String> getFieldsAndTypes() {
		Map<String, String> fieldsAndTypes = new HashMap<>();
		String debugFileName = debugDataViewer.getDebugFileName();
		String debugFileLocation = debugDataViewer.getDebugFileLocation();

		Fields dataViewerFileSchema = ViewDataSchemaHelper.INSTANCE.getFieldsFromSchema(debugFileLocation
				+ debugFileName + AdapterConstants.SCHEMA_FILE_EXTENTION);
		for (Field field : dataViewerFileSchema.getField()) {
			FieldDataTypes fieldDataTypes = field.getType();
			fieldsAndTypes.put(field.getName(), fieldDataTypes.value());
		}
		return fieldsAndTypes;
	}
}
