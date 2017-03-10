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

package hydrograph.ui.graph.utility;

import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.communication.debugservice.DebugServiceClient;
import hydrograph.ui.dataviewer.utilities.Utils;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobManager;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;

/**
 * Data viewer utility
 * 
 * @author Bitwise
 *
 */
public class DataViewerUtility {
	private Logger logger = LogFactory.INSTANCE.getLogger(DataViewerUtility.class);
	
	public static DataViewerUtility INSTANCE = new DataViewerUtility();
	
	private DataViewerUtility(){
		
	}
	
	public void closeDataViewerWindows() {
		List<DebugDataViewer> dataViewerList = new ArrayList<>(); 
		dataViewerList.addAll(JobManager.INSTANCE.getDataViewerMap().values());
				
		JobManager.INSTANCE.getDataViewerMap().clear();		
		Iterator<DebugDataViewer> iterator = dataViewerList.iterator();
		while(iterator.hasNext()){
			DebugDataViewer daDebugDataViewer = (DebugDataViewer) iterator.next();
			daDebugDataViewer.close();			
			iterator.remove();
		}
	}
	
	public void closeDataViewerWindows(Job job) {
		if(job==null){
			return;
		}
		
		List<DebugDataViewer> dataViewerList = new ArrayList<>(); 
		dataViewerList.addAll(JobManager.INSTANCE.getDataViewerMap().values());
				
		Iterator<DebugDataViewer> iterator = dataViewerList.iterator();
		while(iterator.hasNext()){
			
			DebugDataViewer daDebugDataViewer = (DebugDataViewer) iterator.next();
			String windowName=(String) daDebugDataViewer.getDataViewerWindowTitle();
			if(StringUtils.startsWith(windowName, job.getConsoleName().replace(".", "_"))){
				daDebugDataViewer.close();	
				JobManager.INSTANCE.getDataViewerMap().remove(windowName);
				iterator.remove();
			}
			
		}
	}
	
	public void deletePreviousRunsDataviewCsvXmlFiles(Job job){
		if(job==null){
			return;			
		}
		String dataViewerDirectoryPath = Utils.INSTANCE.getDataViewerDebugFilePath();

		IPath path = new Path(dataViewerDirectoryPath);
		boolean deleted = false;
		String dataViewerSchemaFilePathToBeDeleted = "";
		if(path.toFile().isDirectory()){
			String[] fileList = path.toFile().list();
			for (String file: fileList){
				
				if(file.contains(job.getUniqueJobId())){
					if (OSValidator.isWindows()){
						dataViewerSchemaFilePathToBeDeleted = dataViewerDirectoryPath+ "\\" + file;
					}else{
						dataViewerSchemaFilePathToBeDeleted = dataViewerDirectoryPath+ "/" + file;
					}
					path = new Path(dataViewerSchemaFilePathToBeDeleted);
					if(path.toFile().exists()){
						deleted = path.toFile().delete();
						if(deleted){
							logger.debug("Deleted Data Viewer file {}", dataViewerSchemaFilePathToBeDeleted);
						}else{
							logger.warn("Unable to delete Viewer file {}", dataViewerSchemaFilePathToBeDeleted);
						}
					}
				}
			}
		}
	}
	
	public void deletePreviousRunsBasePathDebugFiles(Job job){
		if(job==null){
			return;
		}
		if(Utils.INSTANCE.isPurgeViewDataPrefSet()){
			try {
				DebugServiceClient.INSTANCE.deleteBasePathFiles(job.getHost(),
						job.getPortNumber(), job.getUniqueJobId(), job.getBasePath(),
						job.getUserId(), job.getPassword(),job.isRemoteMode());
			} catch (NumberFormatException e) {
				logger.warn("Unable to delete debug Base path file", e);
			} catch (HttpException e) {
				logger.warn("Unable to delete debug Base path file", e);
			} catch (MalformedURLException e) {
				logger.warn("Unable to delete debug Base path file", e);
			} catch (IOException e) {
				logger.warn("Unable to delete debug Base path file", e);
			}

		}
	}
}
