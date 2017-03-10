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

package hydrograph.ui.graph.execution.tracking.connection;

import java.net.URI;
import java.util.Collections;

import javax.websocket.Session;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;

import com.google.gson.Gson;

import hydrograph.ui.common.util.PreferenceConstants;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.execution.tracking.utils.TrackingDisplayUtils;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Class HydrographServerConnection. use to connect web-socket server for execution tracking status.
 * @author Bitwise
 */
public class HydrographServerConnection {

	/** The logger. */
	private static Logger logger = LogFactory.INSTANCE
			.getLogger(HydrographServerConnection.class);

	/** The counter. */
	private int counter = 0;
	
	/** The selection. */
	private int selection;


	/**
	 * Instantiates HydrographUiClientSocket and establishes connection with
	 * server in order to get execution status.
	 *
	 * @param job the job
	 * @param jobID the job id
	 * @param url the url
	 * @return Session
	 */
	public Session connectToServer(final Job job, String jobID, String url) {
		Session session = null;
		counter++;
		try {
			HydrographUiClientSocket socket = new HydrographUiClientSocket();
			ClientManager clientManager = ClientManager.createClient();
			session = clientManager.connectToServer(socket, new URI(url));
			socket.sendMessage(getStatusReq(jobID));
			return session;

		} catch (Throwable t) {
			if (counter > 1) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							messageDialogForExecutionTracking(job, Display
									.getDefault().getActiveShell());

						}
					});
					
					logger.error("Error while connection to server");
				}else{
					session = connectToServer(job, jobID, url);

			}
			return session;
		}
	}

	/**
	 * Instantiates HydrographUiClientSocket and establishes connection with
	 * server in order to kill the job.
	 *
	 * @param jobID the job id
	 * @param url the url
	 * @return Session
	 * @throws Throwable the throwable
	 */
	public Session connectToKillJob(String jobID, String url) throws Throwable {

		try {

			HydrographUiClientSocket socket = new HydrographUiClientSocket();
			ClientManager clientManager = ClientManager.createClient();
			Session session = clientManager.connectToServer(socket,
					new URI(url));
			socket.sendMessage(getKillReq(jobID));
			Thread.sleep(3000);
			return session;

		} catch (Throwable t) {
			throw t;
		}
	}

	/**
	 * Gets the status req.
	 *
	 * @param jobID the job id
	 * @return the status req
	 */
	private String getStatusReq(String jobID) {
		ExecutionStatus executionStatus = new ExecutionStatus(
				Collections.EMPTY_LIST);
		executionStatus.setJobId(jobID);
		executionStatus.setType("get");
		executionStatus.setClientId("ui-client");
		Gson gson = new Gson();
		return gson.toJson(executionStatus);
	}

	/**
	 * Gets the kill req.
	 *
	 * @param jobID the job id
	 * @return the kill req
	 */
	private String getKillReq(String jobID) {
		ExecutionStatus executionStatus = new ExecutionStatus(
				Collections.EMPTY_LIST);
		executionStatus.setJobId(jobID);
		executionStatus.setType("kill");
		executionStatus.setClientId("ui-client");
		Gson gson = new Gson();
		return gson.toJson(executionStatus);
	}

	/**
	 * Opens a message dialog in case if there are issues while making
	 * connection to server.
	 *
	 * @param job the job
	 * @param shell the shell
	 */
	public void messageDialogForExecutionTracking(Job job, Shell shell) {
		String portNo;
		String host;
		if(job.isRemoteMode()){
			portNo = TrackingDisplayUtils.INSTANCE.getRemotePortFromPreference();
			if(StringUtils.isBlank(portNo)){
				portNo = PreferenceConstants.DEFAULT_PORT_NO;
			}
			if(PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.USE_REMOTE_CONFIGURATION)){
				host = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_HOST);
			}else{
				host = job.getHost();
			}
			
		}else{
			portNo = TrackingDisplayUtils.INSTANCE.getPortFromPreference();
			if(StringUtils.isBlank(portNo)){
				portNo = PreferenceConstants.DEFAULT_PORT_NO;
			}
			host = job.getHost();
		}
		String msg = "Execution tracking can't be displayed as connection refused on host: "
				+ host + " with port no: " + portNo;
		MessageDialog dialog = new MessageDialog(shell, "Warning", null, msg,
				SWT.ICON_WARNING, new String[] { "OK" }, 0);
		dialog.open();
	}

	/**
	 * Gets the selection.
	 *
	 * @return the choice either continue / kill job from Execution tracking
	 *         dialog.
	 */
	public int getSelection() {
		return selection;
	}
}
