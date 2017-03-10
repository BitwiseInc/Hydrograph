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

package hydrograph.ui.communication.debugservice.method;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.google.gson.Gson;

import hydrograph.ui.common.datastructures.dataviewer.JobDetails;
import hydrograph.ui.common.util.PreferenceConstants;
import hydrograph.ui.communication.debugservice.constants.DebugServiceMethods;
import hydrograph.ui.communication.debugservice.constants.DebugServicePostParameters;
import hydrograph.ui.datastructures.metadata.MetaDataDetails;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The class Provider
 * Provides handle for Debug service post methods
 * 
 * @author Bitwise
 *
 */
public class Provider {	
	private static String POST_PROTOCOL="http";
	public static Provider INSTANCE = new Provider();
	private static Logger LOGGER = LogFactory.INSTANCE.getLogger(Provider.class);
	private Provider(){
		
	}

	/**
	 * 
	 * Get post method to delete csv debug file
	 * 
	 * @param jobDetails
	 * @return {@link PostMethod}
	 * @throws NumberFormatException
	 * @throws MalformedURLException
	 */
	public PostMethod getDeleteDebugFileMethod(JobDetails jobDetails) throws NumberFormatException, MalformedURLException {
		
		URL url = new URL(POST_PROTOCOL, getHost(jobDetails), getPortNo(jobDetails), DebugServiceMethods.DELETE_DEBUG_CSV_FILE);
		
		PostMethod postMethod = new PostMethod(url.toString());
		postMethod.addParameter(DebugServicePostParameters.JOB_ID, jobDetails.getUniqueJobID());
		postMethod.addParameter(DebugServicePostParameters.COMPONENT_ID, jobDetails.getComponentID());
		postMethod.addParameter(DebugServicePostParameters.SOCKET_ID, jobDetails.getComponentSocketID());
		
		LOGGER.debug("Calling debug service for deleting csv debug file url :: "+url);
		
		return postMethod;
	}

	private Integer getPortNo(JobDetails jobDetails) {
		String localPortNo = PlatformUI.getPreferenceStore().getString(PreferenceConstants.LOCAL_PORT_NO);
		if(StringUtils.isBlank(localPortNo)){
			localPortNo =PreferenceConstants.DEFAULT_PORT_NO;
		}
		
		
		String remotePortNo = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_PORT_NO);
		if(StringUtils.isBlank(remotePortNo)){
			remotePortNo = PreferenceConstants.DEFAULT_PORT_NO;
		}
		if(jobDetails.isRemote() && StringUtils.isNotBlank(remotePortNo)){
			return	Integer.parseInt(remotePortNo);
		}else
			return Integer.parseInt(localPortNo);
	}

	private String getHost(JobDetails jobDetails) {
		String remoteHost=PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_HOST);
			if(jobDetails.isRemote() && PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.USE_REMOTE_CONFIGURATION)){
			return remoteHost;
		}
		return jobDetails.getHost();
	}

	/**
	 * 
	 * Get post method to get csv debug file
	 * 
	 * @param jobDetails
	 * @param fileSize
	 * @return {@link PostMethod}
	 * @throws NumberFormatException
	 * @throws MalformedURLException
	 */
	public PostMethod getDebugFileMethod(JobDetails jobDetails,String fileSize) throws NumberFormatException, MalformedURLException {
		
		URL url = new URL(POST_PROTOCOL, getHost(jobDetails), getPortNo(jobDetails), DebugServiceMethods.GET_DEBUG_FILE_PATH);
				
		PostMethod postMethod = new PostMethod(url.toString());
		postMethod.addParameter(DebugServicePostParameters.JOB_ID, jobDetails.getUniqueJobID());
		postMethod.addParameter(DebugServicePostParameters.COMPONENT_ID, jobDetails.getComponentID());
		postMethod.addParameter(DebugServicePostParameters.SOCKET_ID, jobDetails.getComponentSocketID());
		postMethod.addParameter(DebugServicePostParameters.BASE_PATH, jobDetails.getBasepath());
		postMethod.addParameter(DebugServicePostParameters.USER_ID, jobDetails.getUsername());
		postMethod.addParameter(DebugServicePostParameters.DEBUG_SERVICE_PWD, jobDetails.getPassword());
		postMethod.addParameter(DebugServicePostParameters.FILE_SIZE, fileSize);
		postMethod.addParameter(DebugServicePostParameters.HOST_NAME, getHost(jobDetails));
		
		LOGGER.debug("Calling debug service to get csv debug file from url :: "+url);
		
		return postMethod;
	}
	
	/**
	 * 
	 * Get post method to delete basepath debug files
	 * 
	 * @param host
	 * @param port
	 * @param unique job ID
	 * @param base path
	 * @param User
	 * @param password
	 * @param isRemote 
	 * @return {@link PostMethod}
	 * @throws NumberFormatException
	 * @throws MalformedURLException
	 */
	public PostMethod getDeleteBasePathFileMethod(String host, String port, String uniqJobID, String basePath, String user, String password, boolean isRemote
			) throws NumberFormatException, MalformedURLException {
		
		if (isRemote) {
			
			port =PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_PORT_NO);
			if(StringUtils.isBlank(port)){
				port = PreferenceConstants.DEFAULT_PORT_NO;
			}
			
				if (PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.USE_REMOTE_CONFIGURATION)) {
					host = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_HOST);
			}
		}
		
		URL url = new URL(POST_PROTOCOL, host, Integer.valueOf(port), DebugServiceMethods.DELETE_BASEPATH_FILES);
		
		PostMethod postMethod = new PostMethod(url.toString());
		postMethod.addParameter(DebugServicePostParameters.JOB_ID, uniqJobID);
		postMethod.addParameter(DebugServicePostParameters.BASE_PATH, basePath);
		postMethod.addParameter(DebugServicePostParameters.USER_ID, user);
		postMethod.addParameter(DebugServicePostParameters.DEBUG_SERVICE_PWD, password);
		
		LOGGER.debug("Calling debug service to delete basepath debug files through url :: "+url);
		
		return postMethod;
	}
	
	/**
	 * Method to get file based on the filter condition
	 * @param jsonString Filter condition string
	 * @param jobDetails
	 * @return
	 * @throws NumberFormatException
	 * @throws MalformedURLException
	 */
	public PostMethod getFilteredFileMethod(String jsonString,JobDetails jobDetails) throws NumberFormatException, MalformedURLException {
		URL url = new URL(POST_PROTOCOL,getHost(jobDetails),getPortNo(jobDetails), DebugServiceMethods.GET_FILTERED_FILE_PATH);
		PostMethod postMethod = new PostMethod(url.toString());
		postMethod.addParameter(DebugServicePostParameters.FILTER_JSON_OBJECT,jsonString);
		
		LOGGER.debug("Calling debug service to get file based on the filter condition through url :: "+url);
		
		return postMethod;
	}
	
	/**
	 * Method to get hive table details
	 * @param jsonString Db name and table details
	 * @param jobDetails
	 * @param userCredentials 
	 * @return
	 * @throws NumberFormatException
	 * @throws MalformedURLException
	 */
	public PostMethod readMetaDataMethod(MetaDataDetails metaDataDetails, String host, String port) throws NumberFormatException, MalformedURLException {
		
		URL url = new URL(POST_PROTOCOL,host,Integer.valueOf(port),DebugServiceMethods.READ_METASTORE);
		PostMethod postMethod = new PostMethod(url.toString());
		Gson gson = new Gson();
        postMethod.addParameter(DebugServicePostParameters.REQUEST_PARAMETERS, gson.toJson(metaDataDetails) );
      
        LOGGER.debug("Calling Metadata service to get Metadata details through url :: "+url);
        
		return postMethod;
	}

	public PostMethod getConnectionStatus(MetaDataDetails metaDataDetails, String host, String port) throws NumberFormatException, MalformedURLException {
		
		URL url = new URL(POST_PROTOCOL,host,Integer.valueOf(port),DebugServiceMethods.TEST_CONNECTION);
		PostMethod postMethod = new PostMethod(url.toString());
		Gson gson = new Gson();
        postMethod.addParameter(DebugServicePostParameters.REQUEST_PARAMETERS, gson.toJson(metaDataDetails) );
      
        LOGGER.debug("Calling service to test connection for database components through url :: "+url);
        
		return postMethod;
	}
	
}
