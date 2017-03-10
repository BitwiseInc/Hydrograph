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
package hydrograph.server.execution.tracking.client;

import hydrograph.engine.commandline.utilities.HydrographService;
import hydrograph.server.execution.tracking.server.status.datastructures.ExecutionStatus;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.apache.log4j.Logger;



/**
 * 
 * Call Engine API to get status of execution tracking also use to kill the job
 * @author Bitwise
 *
 */
@ClientEndpoint
public class HydrographEngineCommunicatorSocket {

	private CountDownLatch latch = new CountDownLatch(1);
	private Session session;
	private ExecutionStatus executionStatus;
	private HydrographService execution;
	final static Logger logger = Logger.getLogger(HydrographEngineCommunicatorSocket.class);;

	
	public HydrographEngineCommunicatorSocket(HydrographService execution) {
		this.execution = execution;
	}
	
	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected to server");
		this.session = session;
		latch.countDown();
	}
	/**
	 * Client onMessage get called to kill the job 
	 * @param message
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.info("Trying to kill the job");
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (execution != null) {
					logger.info("Job killed successfully");
					execution.kill();
					timer.cancel();
				}
			}
				
		};
		timer.schedule(task, 0l, 600);
	}

	@OnClose
	public void onClose(CloseReason reason, Session session) {
		logger.info("Closing a WebSocket due to " + reason.getReasonPhrase());
	}

	/**
	 * lock thread
	 * @return
	 */
	public CountDownLatch getLatch() {
		return latch;
	}

	public void sendMessage(String str) throws IOException {
		logger.debug("CLIENT MESSAGE :"+str);
		if(session!=null && session.isOpen()){
			session.getBasicRemote().sendText(str);
		}
	}

	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}
}