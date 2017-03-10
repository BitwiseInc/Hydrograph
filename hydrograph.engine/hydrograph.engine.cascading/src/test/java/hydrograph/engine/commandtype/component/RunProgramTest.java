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
package hydrograph.engine.commandtype.component;

import hydrograph.engine.commandtype.component.RunProgram;
import hydrograph.engine.core.component.entity.RunProgramEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RunProgramTest {

	private RunProgram runProgram;

	@Before
	public void setUp() {
		RunProgramEntity runProgramEntity = new RunProgramEntity();
		runProgramEntity.setComponentId("run-program");
		runProgramEntity.setBatch("0");
		runProgramEntity.setCommand("dir");
		try {
			runProgram = new RunProgram(runProgramEntity);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testRunProgram() {
		int status = runProgram.getStatus();
		Assert.assertEquals(0, status);
	}
}
