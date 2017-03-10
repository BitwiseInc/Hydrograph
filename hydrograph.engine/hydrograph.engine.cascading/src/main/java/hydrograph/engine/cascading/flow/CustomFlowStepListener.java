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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.cascading.flow;

import cascading.flow.Flow;
import cascading.flow.FlowStep;
import cascading.flow.FlowStepListener;
import cascading.stats.FlowStepStats;
import cascading.stats.hadoop.HadoopStepStats;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapreduce.JobID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("rawtypes")
public class CustomFlowStepListener implements FlowStepListener {

	Flow[] flows;
	private static final Logger LOG = LoggerFactory
			.getLogger(CustomFlowStepListener.class);

	public CustomFlowStepListener(Flow[] cascadingFlows) {
		flows = cascadingFlows.clone();
	}

	@Override
	public void onStepCompleted(FlowStep arg0) {

	}

	@Override
	public void onStepRunning(FlowStep arg0) {

	}

	@Override
	public void onStepStarting(FlowStep arg0) {
	}

	@Override
	public void onStepStopping(FlowStep arg0) {

	}

	@Override
	public boolean onStepThrowable(FlowStep flowstep, Throwable arg1) {
		for (Flow flow : flows) {
			List<FlowStepStats> flows = flow.getFlowStats().getFlowStepStats();
			for (FlowStepStats flowStat : flows) {
				HadoopStepStats stats = (HadoopStepStats) flowStat;
				try {
					RunningJob runningJob = stats.getJobStatusClient();
					if (runningJob != null) {
						JobID jobID = runningJob.getID();
						LOG.error("Killing Job " + jobID.getId());
						runningJob.killJob();
						LOG.info("Job: '" + jobID.getId() + "' started at: "
								+ stats.getStartTime()
								+ " killed successfully!");
					}
				} catch (Exception e) {
					LOG.error("", e);
					throw new RuntimeException(e);
				}
			}
		}
		return true;
	}

}
