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
package hydrograph.engine.spark.components.adapter

import hydrograph.engine.core.component.entity.base.HiveEntityBase
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase
import hydrograph.engine.core.component.generator.{InputFileHiveParquetEntityGenerator, InputFileHiveTextEntityGenerator}
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent
import hydrograph.engine.jaxb.inputtypes.HiveTextFile
import hydrograph.engine.spark.components.InputHiveComponent
import hydrograph.engine.spark.components.adapter.base.InputAdapterBase
import hydrograph.engine.spark.components.base.InputComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams

/**
  * The Class InputHiveAdapter.
  *
  * @author Bitwise
  *
  */
class InputHiveAdapter(typeBaseComponent: TypeBaseComponent) extends InputAdapterBase{

  private var inputHiveComponent:InputHiveComponent=null
  private var generator : InputComponentGeneratorBase=null


  override def getComponent(): InputComponentBase = inputHiveComponent

  override def createGenerator(): Unit = {
    generator=mapComponent(typeBaseComponent)
  }

  override def createComponent(baseComponentParams: BaseComponentParams): Unit = {
    inputHiveComponent=new InputHiveComponent((generator.getEntity).asInstanceOf[HiveEntityBase],baseComponentParams)
  }

  def mapComponent(typeBaseComponent: TypeBaseComponent): InputComponentGeneratorBase={

    if(typeBaseComponent.isInstanceOf[HiveTextFile] )
      new InputFileHiveTextEntityGenerator(typeBaseComponent)
    else
      new InputFileHiveParquetEntityGenerator(typeBaseComponent)

  }
}
