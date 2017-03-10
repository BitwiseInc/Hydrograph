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
package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import hydrograph.ui.common.datastructures.property.database.DatabaseParameterType;
import hydrograph.ui.common.util.PreferenceConstants;
import hydrograph.ui.communication.debugservice.DebugServiceClient;
import hydrograph.ui.datastructures.metadata.MetaDataDetails;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.customwidgets.metastore.HiveTableSchema;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * The Class DataBase Utility
 * @author Bitwise
 *
 */
public class DataBaseUtility {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(DataBaseUtility.class);
	private static DataBaseUtility INSTANCE = new DataBaseUtility();

	
	
	/**
	 * 
	 */
	private DataBaseUtility() {
	}
	
	/**
	 * Static 'instance' method
	 *
	 */
	public static DataBaseUtility getInstance( ) {
      return INSTANCE;
	}
	
	
	
	/**
	 * @param dataBaseTables
	 * @param parameterType
	 * @return
	 */
	
	public HiveTableSchema extractDatabaseDetails(List<String> dataBaseTables, DatabaseParameterType parameterType, String host){
		String jsonResponse = "";
		HiveTableSchema databaseTableSchema = null;
		String port_no = getServicePort();
		
		try {
			jsonResponse = DebugServiceClient.INSTANCE.readMetaStoreDb(getMetaDataDetails(dataBaseTables,parameterType),host,port_no);
			ObjectMapper mapper = new ObjectMapper();
			databaseTableSchema = mapper.readValue(jsonResponse, HiveTableSchema.class);
		} catch (NumberFormatException | HttpException exception) {
			logger.error("Json to object Mapping issue ", exception);
			WidgetUtility.createMessageBox(jsonResponse.toString(),Messages.ERROR , SWT.ICON_ERROR);
		}catch (UnknownHostException exception) {
			logger.error("Json to object Mapping issue ", exception);
			WidgetUtility.createMessageBox(Messages.INVALID_HOST_NAME+" : "+ host,Messages.ERROR , SWT.ICON_ERROR);
		}catch (IOException e) {
			logger.error("Json to object Mapping issue ", e);
			WidgetUtility.createMessageBox(jsonResponse.toString(),Messages.ERROR , SWT.ICON_ERROR);
		}catch (Exception e) {
			logger.error("Json to object Mapping issue ", e);
			WidgetUtility.createMessageBox(e.toString(),Messages.ERROR , SWT.ICON_ERROR);
		}
		return databaseTableSchema;
	}
	
	/**
	 * @param dataBaseTables
	 * @param parameterType
	 * @return
	 */
	
	public String testDBConnection(DatabaseParameterType parameterType, String host){
		String jsonResponse = "";
		String port_no = getServicePort();
		
		String connection_response = "";
		try {
			jsonResponse = DebugServiceClient.INSTANCE.connectToDatabase(getTestConnectionDetails(parameterType),host,port_no);
			connection_response=new String(jsonResponse.getBytes());
		} catch (NumberFormatException | HttpException exception) {
			logger.error("Json to object Mapping issue ", exception);
		} catch (UnknownHostException exception) {
			logger.error("Json to object Mapping issue ", exception.toString());
		}catch (IOException e) {
			logger.error("Json to object Mapping issue ", e);
		}catch (Exception e) {
			logger.error("Json to object Mapping issue ", e);
		}
		return connection_response;
	}
	
	/**
	 * @return host value
	 */
	public String getServiceHost(){
		return PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_HOST);
	}
	
	/**
	 * @return port value
	 */
	public String getServicePort(){
		String port = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_PORT_NO);
		if(StringUtils.isBlank(port)){
			port = PreferenceConstants.DEFAULT_PORT_NO;
		}
		return  port;
	}
	
	/***
	 * 
	 * @param dataBaseTables
	 * @param parameterType
	 * @return connectionDetails
	 */
	private MetaDataDetails getMetaDataDetails(List<String> dataBaseTables,DatabaseParameterType parameterType) {
		MetaDataDetails connectionDetails = new MetaDataDetails();
        connectionDetails.setDbType(parameterType.getDataBaseType());
        connectionDetails.setHost(parameterType.getHostName());
        connectionDetails.setPort(parameterType.getPortNo());
        connectionDetails.setUserId(parameterType.getUserName());
        connectionDetails.setPassword(parameterType.getPassword());
        connectionDetails.setDatabase(parameterType.getDatabaseName());
        connectionDetails.setTableName(dataBaseTables.get(0));
        connectionDetails.setDriverType(parameterType.getJdbcName());
        connectionDetails.setSid(parameterType.getSid());
		return connectionDetails;
	}
	
	/**
	 * 
	 * @param parameterType
	 * @return connectionDetails
	 */
	private MetaDataDetails getTestConnectionDetails(DatabaseParameterType parameterType) {
		MetaDataDetails testConnectionDetails = new MetaDataDetails();
        testConnectionDetails.setDbType(parameterType.getDataBaseType());
        testConnectionDetails.setHost(parameterType.getHostName());
        testConnectionDetails.setPort(parameterType.getPortNo());
        testConnectionDetails.setUserId(parameterType.getUserName());
        testConnectionDetails.setPassword(parameterType.getPassword());
        testConnectionDetails.setDatabase(parameterType.getDatabaseName());
        testConnectionDetails.setDriverType(parameterType.getJdbcName());
        testConnectionDetails.setSid(parameterType.getSid());
		return testConnectionDetails;
	}
	
}
