/** *****************************************************************************
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
  * ******************************************************************************/
package hydrograph.engine.spark.components

/**
  * Created by vaijnathp on 12/26/2016.
  */
/** *****************************************************************************
  * Copyright 2016 Capital One Services, LLC and Bitwise, Inc.
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * http://www.apache.org/licenses/LICENSE-2.0
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * ******************************************************************************/

import hydrograph.engine.core.component.entity.RunProgramEntity
import hydrograph.engine.spark.components.utils.OSValidatorUtil
import org.junit.{Assert, Before, Test}

/**
  * The Class RunProgramTest.
  *
  * @author Bitwise
  *
  */
class RunProgramTest {
  var runProgram: RunProgramComponent = null


  @Before def setUp() {
    val runProgramEntity = new RunProgramEntity
    runProgramEntity.setComponentId("run-program")
    runProgramEntity.setBatch("0")
    if (OSValidatorUtil.isWindows)
      runProgramEntity.setCommand("dir")
    else
      runProgramEntity.setCommand("ls")
    try {
      runProgram = new RunProgramComponent(runProgramEntity)
    }
    catch {
      case e: Throwable => {
        throw new RuntimeException(e)
      }
    }
  }

  @Test
  def testRunProgram() {
    runProgram.execute()
    val status: Int = runProgram.getStatus
    Assert.assertEquals(0, status)
  }
}
