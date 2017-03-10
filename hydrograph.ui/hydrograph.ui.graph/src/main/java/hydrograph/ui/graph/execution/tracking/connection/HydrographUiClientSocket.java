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

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.google.gson.Gson;

import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.execution.tracking.utils.TrackingStatusUpdateUtils;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class HydrographUiClientSocket is use to get get execution tracking status and update the job canvas.
 */
@ClientEndpoint
public class HydrographUiClientSocket {

	/** The session. */
	private Session session;

	/** The logger. */
	private Logger logger = LogFactory.INSTANCE.getLogger(HydrographUiClientSocket.class);


	/**
	 * On open.
	 *
	 * @param session the session
	 */
	@OnOpen
	public void onOpen(Session session) {
		logger.info("Connected to server");
		this.session = session;
	}

	/**
	 * 
	 * Called by web socket server, message contain execution tracking status that updated on job canvas.
	 *
	 * @param message the message
	 * @param session the session
	 */
	@OnMessage
	public void updateJobTrackingStatus(String message, Session session) { 

		final String status = message; 
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Gson gson = new Gson();
				ExecutionStatus executionStatus=gson.fromJson(status, ExecutionStatus.class);
				IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
				IEditorReference[] refs = page.getEditorReferences();
				for (IEditorReference ref : refs){
					IEditorPart editor = ref.getEditor(false);
					if(editor instanceof ELTGraphicalEditor){
						ELTGraphicalEditor editPart=(ELTGraphicalEditor)editor;
						if(editPart.getJobId().equals(executionStatus.getJobId()) || (((editPart.getContainer()!=null) && 
								(editPart.getContainer().getUniqueJobId().equals(executionStatus.getJobId()))) && editPart.getContainer().isOpenedForTracking() )){
								TrackingStatusUpdateUtils.INSTANCE.updateEditorWithCompStatus(executionStatus, (ELTGraphicalEditor)editor,false);
						}
					}
				}
			}
		});
	}

	/**
	 * On close.
	 *
	 * @param reason the reason
	 * @param session the session
	 */
	@OnClose
	public void onClose(CloseReason reason, Session session) {
		logger.info("Closing a WebSocket due to {}", reason.getReasonPhrase());
	} 

	/**
	 * Send message to remote server.
	 *
	 * @param str the str
	 */
	public void sendMessage(String str) {
		try {
			session.getBasicRemote().sendText(str);
		} catch (IOException e) {
			logger.error("Failed to sent connection request: ", e);
		}
	}
}
