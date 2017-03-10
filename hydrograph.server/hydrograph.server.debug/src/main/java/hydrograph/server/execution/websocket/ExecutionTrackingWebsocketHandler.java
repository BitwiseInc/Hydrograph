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
package hydrograph.server.execution.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import hydrograph.server.execution.datastructure.Constants;
import hydrograph.server.execution.datastructure.ExecutionStatus;

/**
 * 
 * @author Bitwise
 *
 */
@WebSocket
public class ExecutionTrackingWebsocketHandler {

	/** The all sessions. */
	private static Map<Session, Map<String, String>> allSessions = new HashMap<>();

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(ExecutionTrackingWebsocketHandler.class);

	@OnWebSocketConnect
	public void onConnect(Session session) throws Exception {

		allSessions.put(session, new HashMap<String, String>());

		logger.debug("Starting a session" + session.toString());

	}

	@OnWebSocketMessage
	public void trackingCommunicator(Session session, String msg) throws IOException {
		logger.debug("In trackingCommunicator method trigged on websoket messsage ");

		Gson gson = new Gson();
		logger.debug("Converting execution json to object ");
		ExecutionStatus executionStatus = gson.fromJson(msg, ExecutionStatus.class);

		logger.debug("Execution Tracking Request - " + executionStatus.toString());

		/** Mapping session and it client id,to track Ui and engine session */
		allSessions.get(session).put(Constants.CLIENTID, executionStatus.getClientId());

		/**
		 * Ui Client send connection request and save jobid with it session in
		 * map.
		 */

		getTrackingStatus(executionStatus, session);

		/**
		 * Status posted by engine client will delivered to requested UI client
		 * based on Job Id.
		 */
		postTrackingStatus(executionStatus, session);

		/**
		 * UI will send kill request along with job id, server intern call
		 * engine client to kill the job. For remote and remote debug run
		 */
		killRequestedJob(executionStatus, session);
	}

	private void killRequestedJob(ExecutionStatus executionStatus, Session session) {

		if (Constants.KILL.equalsIgnoreCase(executionStatus.getType())) {

			logger.info("Kill request received for job - " + executionStatus.getJobId());

			final String jobId = executionStatus.getJobId().trim();

			for (Session openSession : allSessions.keySet()) {

				if (openSession.isOpen()) {
					try {

						if (allSessions.get(openSession).get(Constants.CLIENTID) != null) {

							if (((String) allSessions.get(openSession).get(Constants.CLIENTID))
									.equalsIgnoreCase(Constants.ENGINE_CLIENT + executionStatus.getJobId())) {

								logger.debug("Before sending kill" + jobId);
								openSession.getRemote().sendStringByFuture("");
								logger.debug("After sending kill" + jobId);
							}
						}
					} catch (Exception e) {
						logger.error("Failed to send kill request for - " + jobId, e);
					}
				}
			}
		}

	}

	private void postTrackingStatus(ExecutionStatus executionStatus, Session session) {

		if (Constants.POST.equalsIgnoreCase(executionStatus.getType())) {

			logger.debug("Posting tracking status to ui for job id :" + executionStatus.getJobId());

			for (Session openSession : allSessions.keySet()) {

				try {
					if (openSession.isOpen()) {
						if (null!=allSessions.get(openSession).get(Constants.JOBID) ) {
							if (((String) allSessions.get(openSession).get(Constants.JOBID))
									.equalsIgnoreCase(executionStatus.getJobId())) {
								try {
									logger.debug("Converting to json from object before sending ");
									Gson gson = new Gson();
									openSession.getRemote().sendStringByFuture(gson.toJson(executionStatus));
									logger.debug("Posted tracking status to ui successfully ");
								} catch (Exception e) {
									logger.error("Failed to send postTrackingStatus request for -"
											+ executionStatus.getJobId(), e);
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("Failed to send postTrackingStatus request for -" + executionStatus.getJobId(), e);
				}
			}
		}
	}

	private void getTrackingStatus(ExecutionStatus executionStatus, Session session) {
		if (Constants.GET.equalsIgnoreCase(executionStatus.getType())) {

			logger.debug(" Mapping session and it job id ");
			allSessions.get(session).put(Constants.JOBID, executionStatus.getJobId());

		}
	}

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		allSessions.remove(session);
		logger.debug("Closing a WebSocket due to " + reason + "status code " + statusCode);
	}
}
