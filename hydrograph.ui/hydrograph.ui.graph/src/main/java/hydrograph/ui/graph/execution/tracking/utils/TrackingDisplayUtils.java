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

package hydrograph.ui.graph.execution.tracking.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.util.ConfigFileReader;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.PreferenceConstants;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.LinkEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobStatus;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.ComponentExecutionStatus;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class TrackingDisplayUtils.
 */
public class TrackingDisplayUtils {

	/** The instance. */
	public static TrackingDisplayUtils INSTANCE = new TrackingDisplayUtils();

	private long DELAY=7000;
	/** The logger. */
	private static Logger logger = LogFactory.INSTANCE.getLogger(TrackingDisplayUtils.class);
	
	/** The Constant LOCAL_URL. */
	private static final String WEBSOCKET_LOCAL_HOST = "WEBSOCKET_LOCAL_HOST";
	
	/** The Constant WEBSOCKET_ROUTE. */
	private static final String WEBSOCKET_UI_ROUTE = "WEBSOCKET_UI_ROUTE";

   //Execution tracking config properties	
	private static final String EXECUTION_TRACKING_PORT = "EXECUTION_TRACKING_PORT";

	
	/** The local host. */
	private String localHost;

	/** The websocket route. */
	private String websocketRoute;
	public static final String WEB_SOCKET = "ws://";
	private static final String COLON = ":";
	/**
	 * Instantiates a new tracking display utils.
	 */
	private TrackingDisplayUtils() {
	}

	/**
	 * Clears the status of all components. Also Initiates record count to 0.
	 */
	public void clearTrackingStatus() {

		ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI
				.getWorkbench().getWorkbenchWindows()[0].getActivePage()
				.getActiveEditor();
		if (editor != null && editor instanceof ELTGraphicalEditor) {
			editor.closeAllSubJobLinkedEditors();
			String currentJobName = editor.getActiveProject() + "." + editor.getJobName();	
			Job job = editor.getJobInstance(currentJobName);			
			if(job!=null){			
				job.setJobStatus(JobStatus.PENDING);			
			}
			clearTrackingStatusForEditor(editor);
		}
	}

	/**
	 * Clear tracking status.
	 * 
	 * @param jobId
	 *            the job id
	 */
	public void clearTrackingStatus(String jobId) {

		ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI
				.getWorkbench().getWorkbenchWindows()[0].getActivePage()
				.getActiveEditor();
		if (editor != null && editor instanceof ELTGraphicalEditor
				&& (editor.getJobId().equals(jobId))) {
			editor.closeAllSubJobLinkedEditors();
			String currentJobName = editor.getActiveProject() + "." + editor.getJobName();	
			Job job = editor.getJobInstance(currentJobName);			
			clearTrackingStatusForEditor(editor);
		}
	}

	/**
	 * Clear tracking status for editor.
	 * 
	 * @param editor
	 *            the editor
	 */
	public void clearTrackingStatusForEditor(ELTGraphicalEditor editor) {
		GraphicalViewer graphicalViewer = (GraphicalViewer) ((GraphicalEditor) editor)
				.getAdapter(GraphicalViewer.class);
		for (Iterator<EditPart> ite = graphicalViewer.getEditPartRegistry()
				.values().iterator(); ite.hasNext();) {
			EditPart editPart = ite.next();
			if (editPart instanceof ComponentEditPart) {
				Component component = ((ComponentEditPart) editPart)
						.getCastedModel();
				component.updateStatus(ComponentExecutionStatus.BLANK.value());
			} else if (editPart instanceof LinkEditPart) {
				((LinkEditPart) editPart).clearRecordCountAtPort();
			}
		}
	}

	/**
	 * Gets the executiontracking port no.
	 * 
	 * @return the executiontracking port no
	 */
	public String getExecutiontrackingPortNo() {
		String portNumber = null;
			Properties properties = ConfigFileReader.INSTANCE.getCommonConfigurations();
			if (StringUtils.isNotBlank(properties.getProperty(EXECUTION_TRACKING_PORT))
				&& StringUtils.isNotBlank(properties.getProperty(WEBSOCKET_LOCAL_HOST))
				&& StringUtils.isNotBlank(properties.getProperty(WEBSOCKET_UI_ROUTE))) {
				
				portNumber = properties.getProperty(EXECUTION_TRACKING_PORT);
				localHost = properties.getProperty(WEBSOCKET_LOCAL_HOST);
				websocketRoute = properties.getProperty(WEBSOCKET_UI_ROUTE);
			}
		return portNumber;
	}

	/**
	 * Gets the web socket remote url.
	 * 
	 * @return the web socket remote url
	 */
	public String getWebSocketRemoteUrl(Job job) {
		String remoteUrl = null;
		String portNo = getPortFromPreference();
		if (job.isRemoteMode()&& PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.USE_REMOTE_CONFIGURATION)) {
			
			portNo = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_PORT_NO);
			if(StringUtils.isBlank(portNo)){
				portNo = PreferenceConstants.DEFAULT_PORT_NO;
			}
			
			String remoteHost = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_HOST);
			remoteUrl = WEB_SOCKET + remoteHost + COLON + portNo + websocketRoute;
		} else {
			if (job.isRemoteMode()) {
				portNo = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_PORT_NO);
				if(StringUtils.isBlank(portNo)){
					portNo = PreferenceConstants.DEFAULT_PORT_NO;
				}
			}
			remoteUrl = WEB_SOCKET + job.getHost() + COLON + portNo + websocketRoute;
		}
		return remoteUrl;
	}

	/**
	 * Gets the web socket local host.
	 * 
	 * @return the web socket local host
	 */
	public String getWebSocketLocalHost() {
		String portNo = getPortFromPreference();
		String localUrl = localHost + portNo + websocketRoute;
		return localUrl;

	}

	/**
	 * Gets the port from preference.
	 * 
	 * @return the port from preference
	 */	
	public String getPortFromPreference() {
		getExecutiontrackingPortNo();
		String portNo = PlatformUI.getPreferenceStore().getString(PreferenceConstants.LOCAL_PORT_NO);
		if(StringUtils.isBlank(portNo)){
			portNo = PreferenceConstants.DEFAULT_PORT_NO;;
		}		
		return portNo;
	}

	/**
	 * @return remote port number
	 */
	public String getRemotePortFromPreference(){
		 String port = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_PORT_NO);
		 if(StringUtils.isBlank(port)){
			 port = PreferenceConstants.DEFAULT_PORT_NO;
			}
		 return port;
	}
	/**
	 * This function will be return process ID which running on defined port.
	 * 
	 * @return the service port pid
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String getServicePortPID(Properties properties) throws IOException {
		int portNumber = Integer.parseInt(properties.getProperty(EXECUTION_TRACKING_PORT));
		if (OSValidator.isWindows()) {
			ProcessBuilder builder = new ProcessBuilder(new String[] { "cmd",
					"/c", "netstat -a -o -n |findstr :" + portNumber });
			Process process = builder.start();
			InputStream inputStream = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String str = bufferedReader.readLine();
			str = StringUtils.substringAfter(str, "LISTENING");
			str = StringUtils.trim(str);
			return str;
		}
		return "";
	}

	/**
	 * 
	 * Return tools Installation path
	 * 
	 * @return {@link String}
	 */
	public String getInstallationPath() {
		String installationPath = XMLConfigUtil.CONFIG_FILES_PATH + "/logger/JobTrackingLog";
		if (OSValidator.isWindows()) {
			if (installationPath.startsWith("/")) {
				installationPath = installationPath.replaceFirst("/", "").replace("/", "\\");
			}
		}
		return installationPath;

	}
	
	/**
	 * 
	 * Close websocket client connection.
	 * @param session
	 */
	public void closeWebSocketConnection(Session session){
		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e1) {
		}
		if (session != null  && session.isOpen()) {
			try {
				CloseReason closeReason = new CloseReason(CloseCodes.NORMAL_CLOSURE,"Closed");
				session.close(closeReason);
				logger.info("Session closed");
			} catch (IOException e) {
				logger.error("Fail to close connection ",e);
			}
			
		}

	}
/**
 * 
 * @param job
 */
 public void changeStatusForExecTracking(Job job){
	 
		if(job!=null){			
			job.setJobStatus(JobStatus.PENDING);			
		}
 }
}
