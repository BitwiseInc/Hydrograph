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

package hydrograph.server.execution.tracking.client.main;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import com.google.gson.Gson;

import hydrograph.engine.commandline.utilities.HydrographService;
import hydrograph.engine.execution.tracking.ComponentInfo;
import hydrograph.server.execution.tracking.client.HydrographEngineCommunicatorSocket;
import hydrograph.server.execution.tracking.client.logger.ExecutionTrackingFileLogger;
import hydrograph.server.execution.tracking.server.status.datastructures.ComponentStatus;
import hydrograph.server.execution.tracking.server.status.datastructures.Constants;
import hydrograph.server.execution.tracking.server.status.datastructures.ExecutionStatus;
import hydrograph.server.execution.tracking.utils.ExecutionTrackingUtils;

/**
 * The Class HydrographMain use to execute job also post tracking status if
 * execution tracking flag is enable.
 * 
 * @author Bitwise
 */
public class HydrographMain {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(HydrographMain.class);
	private ExecutorService executorService;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		
		HydrographMain hydrographMain = new HydrographMain();
		final Timer timer = new Timer();
		final CountDownLatch latch = new CountDownLatch(1);
		try{
			Session session = null;
			boolean isExecutionTracking = false;
			String[] argsList = args;
			List<String> argumentList = new ArrayList<String>( Arrays.asList(args));
			final String jobId = hydrographMain.getJobId(argumentList);
			
			getLogLevel(argumentList).ifPresent(x-> 
			{
				if(!x.equalsIgnoreCase(String.valueOf(logger.getLevel()))){
					setLoglevel(x);
				}else{
					Optional.empty();
				}
				
			});
			
			logger.info("Argument List: " + argumentList.toString());
			
			String 	trackingClientSocketPort = hydrographMain.getTrackingClientSocketPort(argumentList);
			
			if (argumentList.contains(Constants.IS_TRACKING_ENABLE)) {
				int index = argumentList.indexOf(Constants.IS_TRACKING_ENABLE);
				isExecutionTracking = Boolean.valueOf(argsList[index + 1]);
				argumentList = removeItemFromIndex(index,argumentList);
			}
			
			if(argumentList.contains(Constants.TRACKING_CLIENT_SOCKET_PORT)){
				int index = argumentList.indexOf(Constants.TRACKING_CLIENT_SOCKET_PORT);
				argumentList = removeItemFromIndex(index,argumentList);
			}
			
			argsList = argumentList.toArray(new String[argumentList.size()]);
	
			logger.debug("Execution tracking enabled - " + isExecutionTracking);
			logger.info("Tracking Client Port: " + trackingClientSocketPort);
	
			/**
			 * Start new thread to run job
			 */
			final HydrographService execution = new HydrographService();
	
			FutureTask task = hydrographMain.executeGraph(latch, jobId, argsList, execution,isExecutionTracking);
					
			hydrographMain.executorService = Executors.newSingleThreadExecutor();
			hydrographMain.executorService.submit(task);
			
			if (isExecutionTracking) {
				//If tracking is enabled, start to post execution tracking status.
				final HydrographEngineCommunicatorSocket socket = new HydrographEngineCommunicatorSocket(execution);
				session = hydrographMain.connectToServer(socket, jobId, trackingClientSocketPort);
				hydrographMain.sendExecutionTrackingStatus(latch, session, jobId, timer, execution,socket);
			}
			
			//waiting for execute graph thread 
			task.get();
			
		}catch(Exception exp){
			logger.info("Getting exception from HydrographMain");
			throw new RuntimeException(exp);
		}finally{
			//cleanup threads --> executor thread and timer thread 
			logger.info("HydrographMain releasing resources");
			if(!hydrographMain.executorService.isShutdown() && !hydrographMain.executorService.isTerminated()){
				hydrographMain.executorService.shutdown();
			}
			timer.cancel();
			
		}
	}
 /**
  * 
  * 
  * @param latch
  * @param jobId
  * @param argsFinalList
  * @param execution
  * @param isExecutionTracking
  * @return
  */
	private FutureTask executeGraph(final CountDownLatch latch, final String jobId, final String[] argsFinalList,
			final HydrographService execution, final boolean isExecutionTracking) {
		logger.trace("Creating executor thread");
	
		return new FutureTask(new Runnable() {
			public void run() {
				try {
					logger.debug("Executing the job from execute graph");
					execution.executeGraph(argsFinalList);
					if(isExecutionTracking){
						latch.await();
					}
				    
				} catch (Exception e) {
					logger.error("JOB FAILED :",e);
					if(isExecutionTracking){
						try {
							latch.await();
						} catch (InterruptedException e1) {
							logger.error("job fail :",e1);
						}
					}
					throw new RuntimeException(e);
				}
			}
		},null);
	
	}

	/**
	 * 	
	 * @param latch
	 * @param session
	 * @param jobId
	 * @param timer
	 * @param execution
	 * @param socket
	 * @throws IOException
	 */
	private void sendExecutionTrackingStatus(final CountDownLatch latch, Session session,final String jobId, final Timer timer, final HydrographService execution,final HydrographEngineCommunicatorSocket socket)
			throws IOException {
			try {
				TimerTask task = new TimerTask() {
					ExecutionStatus previousExecutionStatus=null;
					@Override
					public void run() {
						List<ComponentInfo> componentInfos = execution.getStatus();
						if(!componentInfos.isEmpty()){
							List<ComponentStatus> componentStatusList = new ArrayList<ComponentStatus>();
							for (ComponentInfo componentInfo : componentInfos) {
								ComponentStatus componentStatus = new ComponentStatus(componentInfo.getComponentId(),componentInfo.getComponentName(),
										componentInfo.getCurrentStatus(),componentInfo.getBatch(), componentInfo.getProcessedRecords());
								componentStatusList.add(componentStatus);
							}
							ExecutionStatus executionStatus = new ExecutionStatus(componentStatusList);
							executionStatus.setJobId(jobId);
							executionStatus.setClientId(Constants.ENGINE_CLIENT+jobId);
							executionStatus.setType(Constants.POST);
							Gson gson = new Gson();
							try {
								if(previousExecutionStatus==null || !executionStatus.equals(previousExecutionStatus)){
									socket.sendMessage(gson.toJson(executionStatus));
									previousExecutionStatus=executionStatus;
								}
							} catch (IOException e) {
								logger.error("Fail to send status for job - " + jobId, e);
								timer.cancel();
							}

							if(StringUtils.isNotBlank(jobId) ){
								//moved this after sendMessage in order to log even if the service is not running 
								ExecutionTrackingFileLogger.INSTANCE.log(jobId, executionStatus);
							}
						 
						}
						if(!execution.getJobRunningStatus()){
							timer.cancel();
							latch.countDown();
						}
					}
				};
				
				timer.schedule(task, 0l, ExecutionTrackingUtils.INSTANCE.getStatusFrequency());
				latch.await();
			
			} catch (Throwable t) {
				logger.error("Failure in job - " + jobId, t);
				timer.cancel();
				throw new RuntimeException(t);
			} finally {

				if (session != null && session.isOpen()) {
					logger.debug("Closing Websocket engine client");
					CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Session Closed");
					session.close(closeReason);
				}
			}
	}

	/**
	 * Gets the job id.
	 *
	 * @param argumentList
	 *            the argument list
	 * @return the job id
	 */
	private String getJobId(List<String> argumentList) {
		if (argumentList.contains(Constants.JOBID_KEY)) {
			return argumentList.get(argumentList.indexOf(Constants.JOBID_KEY) + 1);
		}
		return null;
	}
	
	/**
	 * Gets the tracking socket port number.
	 *
	 * @param argumentList
	 *            the argument list
	 * @return the job id
	 */
	private String getTrackingClientSocketPort(List<String> argumentList) {
		if (argumentList.contains(Constants.TRACKING_CLIENT_SOCKET_PORT)) {
			return argumentList.get(argumentList.indexOf(Constants.TRACKING_CLIENT_SOCKET_PORT) + 1);
		}
		return null;
	}
	
	private Session connectToServer(HydrographEngineCommunicatorSocket socket,String jobId, String trackingClientSocketPort){
		ClientManager client = ClientManager.createClient();
		Session session=null;
			try {
				session = client.connectToServer(socket,new URI(ExecutionTrackingUtils.INSTANCE.getTrackingUrl(trackingClientSocketPort)));
				socket.sendMessage(getConnectionReq(jobId));
			} catch (DeploymentException e) {
				logger.error("Fail to connect to server",e);
			} catch (URISyntaxException e) {
				logger.error("Fail to connect to server",e);
			} catch (IOException e) {
				logger.error("Fail to connect to server",e);
			}
			
		
		return session;

	}
	
	private String getConnectionReq(String jobId){
		ExecutionStatus executionStatus = new ExecutionStatus(Collections.<ComponentStatus> emptyList());
		executionStatus.setJobId(jobId);
		executionStatus.setClientId(Constants.ENGINE_CLIENT+jobId);
		executionStatus.setType(Constants.POST);
		Gson gson = new Gson();
		return gson.toJson(executionStatus);
	}
	
	private static List<String> removeItemFromIndex(int index,List<String> argList){
		
		argList.remove(index); /* removing parameter name */
		argList.remove(index); /* removing parameter value */
		
		return argList;
		
	}
	/**
	 * 
	 * @param argumentList
	 * @return
	 */
	private static Optional<String> getLogLevel(List<String> argumentList){
		
		if (argumentList.contains(Constants.JOB_LOG_LEVEL)) {
			return Optional.of(argumentList.get(argumentList.indexOf(Constants.JOB_LOG_LEVEL) + 1));
		}
		return Optional.empty();
		
		
	} 
	/**
	 * 
	 * @param loglevel
	 */
	private static void setLoglevel(String loglevel) {
        
		switch (loglevel) {
		case "Fatal":
			logger.setLevel(Level.FATAL);
			break;
		case "Error":
			logger.setLevel(Level.ERROR);
			break;
		case "Warn":
			logger.setLevel(Level.WARN);
			break;
		case "Info":
			logger.setLevel(Level.INFO);
			break;
		case "Debug":
			logger.setLevel(Level.DEBUG);
			break;
		case "Trace":
			logger.setLevel(Level.TRACE);
			break;
		default:
			logger.setLevel(Level.ALL);
			break;

		}

	}
}