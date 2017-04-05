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
package hydrograph.engine.execution.tracking;

import hydrograph.engine.commandline.utilities.HydrographService;
import hydrograph.engine.helper.StatusHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * The Class ExecutionTrackingJoinXmlTest.
 *
 * @author Bitwise
 *
 */
public class ExecutionTrackingJoinXmlTest {

	static HydrographService hydrographService;
	static StatusHelper statusHelper;
	static int returnCode;

	@BeforeClass
	public static void hydrographService() throws Exception {
		String[] args = { "-xmlpath", "testData/XMLFiles/Joinexample.xml" };
		hydrographService = new HydrographService();
		returnCode = hydrographService.executeGraph(args);
		statusHelper = new StatusHelper(hydrographService.getStatus());
	}

	@Test
	public void isJobSuccessfulAndReturnCodeZero() {
		Assert.assertEquals(returnCode, 0);
	}

	@Test
	public void itShouldTestInput1Component() {
		Assert.assertEquals(statusHelper.getComponentId("input1"), "input1");
		Assert.assertEquals(statusHelper.getCurrentStatus("input1"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getProcessedRecords("input1").get("out0"), new Long(5));
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("input1").get("out0"), "SUCCESSFUL");
	}

	@Test
	public void itShouldTestInput2Component() {
		Assert.assertEquals(statusHelper.getComponentId("input2"), "input2");
		Assert.assertEquals(statusHelper.getCurrentStatus("input2"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getProcessedRecords("input2").get("out0"), new Long(5));
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("input2").get("out0"), "SUCCESSFUL");
	}

	@Test
	public void itShouldTestInput3Component() {
		Assert.assertEquals(statusHelper.getComponentId("input3"), "input3");
		Assert.assertEquals(statusHelper.getCurrentStatus("input3"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getProcessedRecords("input3").get("out0"), new Long(5));
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("input3").get("out0"), "SUCCESSFUL");
	}

	@Test
	public void itShouldTestJoinComponent() {
		Assert.assertEquals(statusHelper.getComponentId("join"), "join");
		Assert.assertEquals(statusHelper.getCurrentStatus("join"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getProcessedRecords("join").get("out0"), new Long(5));
		Assert.assertEquals(statusHelper.getProcessedRecords("join").get("unused0"), new Long(1));
		Assert.assertEquals(statusHelper.getProcessedRecords("join").get("unused1"), new Long(0));
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("join").get("out0"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("join").get("unused0"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("join").get("unused1"), "SUCCESSFUL");
	}

	@Test
	public void itShouldTestOutput1Component() {
		Assert.assertEquals(statusHelper.getComponentId("output1"), "output1");
		Assert.assertEquals(statusHelper.getCurrentStatus("output1"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getProcessedRecords("output1").get("NoSocketId"), new Long(5));
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("output1").get("NoSocketId"), "SUCCESSFUL");
	}

	@Test
	public void itShouldTestOutput2Component() {
		Assert.assertEquals(statusHelper.getComponentId("output2"), "output2");
		Assert.assertEquals(statusHelper.getCurrentStatus("output2"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getProcessedRecords("output2").get("NoSocketId"), new Long(1));
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("output2").get("NoSocketId"), "SUCCESSFUL");
	}

	@Test
	public void itShouldTestOutput3Component() {
		Assert.assertEquals(statusHelper.getComponentId("output3"), "output3");
		Assert.assertEquals(statusHelper.getCurrentStatus("output3"), "SUCCESSFUL");
		Assert.assertEquals(statusHelper.getProcessedRecords("output3").get("NoSocketId"), new Long(0));
		Assert.assertEquals(statusHelper.getStatusPerSocketMap("output3").get("NoSocketId"), "SUCCESSFUL");
	}

}
