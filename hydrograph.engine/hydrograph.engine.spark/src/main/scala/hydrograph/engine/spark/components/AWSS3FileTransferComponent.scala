/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/

package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.{RunFileTransferEntity, RunProgramEntity}
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase
import hydrograph.engine.spark.components.base.CommandComponentSparkFlow
import hydrograph.engine.spark.datasource.utils.AWSS3Util
import org.slf4j.LoggerFactory

/**
  * Created for AWSS3FileTransferComponent   on 9/27/2017.
  */
class AWSS3FileTransferComponent(assemblyEntityBase: AssemblyEntityBase) extends CommandComponentSparkFlow with Serializable  {
  val LOG = LoggerFactory.getLogger(classOf[AWSS3FileTransferComponent])
  var runFileTransferEntity: RunFileTransferEntity = assemblyEntityBase.asInstanceOf[RunFileTransferEntity]
  override def execute() = {
    LOG.debug("Start executing AWSS3FileTransferComponent")
    val awss3Util = new AWSS3Util
    if (runFileTransferEntity.getFileOperation == "upload")
      awss3Util.upload(runFileTransferEntity)
    else
      awss3Util.download(runFileTransferEntity)
    LOG.debug("Finished executing AWSS3FileTransferComponent")
  }


}
