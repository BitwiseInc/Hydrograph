
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

package hydrograph.engine.spark.components.adapter

import hydrograph.engine.core.component.generator.S3FileTransferEntityGenerator
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent
import hydrograph.engine.spark.components.AWSS3FileTransferComponent
import hydrograph.engine.spark.components.adapter.base.RunProgramAdapterBase
import hydrograph.engine.spark.components.base.CommandComponentSparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams

/**
  * Created for S3FileTransferAdapter on 9/7/2017.
  */
class S3FileTransferAdapter(typeBaseComponent: TypeBaseComponent) extends RunProgramAdapterBase{

  private var s3FileTransferEntityGenerator:S3FileTransferEntityGenerator=null;
  private var awsS3FileTransferComponent:AWSS3FileTransferComponent=null;

  override def createGenerator(): Unit = {

    s3FileTransferEntityGenerator = new S3FileTransferEntityGenerator(typeBaseComponent)

  }
  override def createComponent(baseComponentParams: BaseComponentParams): Unit = {

    awsS3FileTransferComponent=new AWSS3FileTransferComponent(s3FileTransferEntityGenerator.getEntity);
  }
  override def getComponent(): CommandComponentSparkFlow = awsS3FileTransferComponent

}
