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
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.UnionAllEntity
import hydrograph.engine.core.component.entity.elements.OutSocket
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.testing.wrapper.{DataBuilder, Fields}
import org.junit.Test

/**
  * The Class UnionAllComponentTest.
  *
  * @author Bitwise
  *
  */
class UnionAllComponentTest {

  @Test
  def TestSimpleUnionAllComponentWorking(): Unit ={

    val df1= new DataBuilder(Fields(List("col1","col2","col3","col4")).applyTypes(List(classOf[String],
      classOf[String],classOf[String],classOf[String]))).addData(List
    ("1",
      "C2R1",
      "C3Rx", "C4R1"))
      .addData(List("2", "C2R2", "C3Rx", "C4R2"))
      .addData(List("3", "C2R3", "C3Rx", "C4R3"))
      .build()


    val df2= new DataBuilder(Fields(List("col1","col2","col3","col4"))).addData(List("4", "C2R1", "C3Rx", "C4R1"))
      .addData(List("5", "C2R2", "C3Rx", "C4R2"))
      .addData(List("6", "C2R3", "C3Rx", "C4R3"))
      .build()

    val unionAllEntity: UnionAllEntity = new UnionAllEntity
    unionAllEntity.setComponentId("unionAll")
    unionAllEntity.setOutSocket(new OutSocket("out1"))

    val cp= new BaseComponentParams
    cp.addinputDataFrame(df1)
    cp.addinputDataFrame(df2)

    /*val unionDF=new UnionAllComponent(unionAllEntity,cp).createComponent()

    val rows=Bucket( Fields(List("col1","col2","col3","col4")),unionDF.get("out1").get).result()

    Assert.assertEquals(6,rows.size)*/
  }

}
