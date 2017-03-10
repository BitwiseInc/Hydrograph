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
package hydrograph.engine.spark.execution.tracking

import hydrograph.engine.execution.tracking
import hydrograph.engine.execution.tracking.ComponentInfo
import hydrograph.engine.spark.executiontracking.plugin.Component
import org.apache.spark.scheduler._

import scala.collection.JavaConverters._
import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * The Class JobInfo.
  *
  * @author Bitwise
  *
  */
class JobInfo(listOfComponents: mutable.ListBuffer[Component]) {

  val componentInfoList = new java.util.ArrayList[tracking.ComponentInfo]
  val componentInfoHashSet = new mutable.HashSet[tracking.ComponentInfo]


  def storeComponentStatsForTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
    generateStatsForTaskEnd(taskEnd)
  }

  def storeComponentStatsForTaskStart(taskStart: SparkListenerTaskStart): Unit = {
    generateStatsForTaskStart(taskStart)
  }

  def storeComponentStatsForTaskGettingResult(taskGettingResult: SparkListenerTaskGettingResult): Unit = {
    generateStatsForTaskGettingResult(taskGettingResult)
  }

  def updateStatusOfComponentsOnStageSubmitted(stageSubmitted: SparkListenerStageSubmitted): Unit = {
    val Status = if(stageSubmitted.stageInfo.failureReason.equals(None))"SUCCESSFUL"
    else "FAILED"
    val listOfStatus : mutable.ListBuffer[String] = new ListBuffer[String]
    componentInfoList.asScala.filter(cInfo => cInfo.getStageId.
      equals(stageSubmitted.stageInfo.stageId)).
      foreach(compInfo=>{

        if(compInfo.getStatusPerSocketMap().values().iterator().hasNext){
          for(value <- compInfo.getStatusPerSocketMap().values().asScala){
            listOfStatus+=value
          }}

        if(listOfStatus.contains("FAILED")){
          for(key : String <- compInfo.getStatusPerSocketMap().keySet().asScala){
            compInfo.setProcessedRecordCount(key,-1)
          }
          compInfo.setCurrentStatus("FAILED")
        }
        else if (listOfStatus.contains("RUNNING")) {
          compInfo.setCurrentStatus("RUNNING")
        }
        else if (listOfStatus.contains("SUCCESSFUL")) {
          compInfo.setCurrentStatus("SUCCESSFUL")
        }

      })

  }


  def updateStatusOfComponents(stageCompleted: SparkListenerStageCompleted): Unit = {
    val Status = if(stageCompleted.stageInfo.failureReason.equals(None))"SUCCESSFUL"
    else "FAILED"
    val listOfStatus : mutable.ListBuffer[String] = new ListBuffer[String]
    componentInfoList.asScala.filter(cInfo => cInfo.getStageId.
      equals(stageCompleted.stageInfo.stageId)).
      foreach(compInfo=>{

        if(compInfo.getStatusPerSocketMap().values().iterator().hasNext){
          for(value <- compInfo.getStatusPerSocketMap().values().asScala){
            listOfStatus+=value
          }}

        if(listOfStatus.contains("FAILED")){
          for(key : String <- compInfo.getStatusPerSocketMap().keySet().asScala){
            compInfo.setProcessedRecordCount(key,-1)
          }
          compInfo.setCurrentStatus("FAILED")
        }
        else if (listOfStatus.contains("RUNNING")) {
          compInfo.setCurrentStatus("RUNNING")
        }
        else if (listOfStatus.contains("SUCCESSFUL")) {
          compInfo.setCurrentStatus("SUCCESSFUL")
        }

      })

    //logic for status of component whose compInfo is not generated
    /*componentInfoList.asScala.filter(cI=>cI.getStageId.equals(-1)).foreach(co=>{
      co.setCurrentStatus("SUCCESSFUL")
      for(key : String <- co.getStatusPerSocketMap().keySet().asScala){
        co.setStatusPerSocketMap(key,"SUCCESSFUL")
      }
    })*/

  }

  private def generateStatsForTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
    val Status = if(taskEnd.taskInfo.status.equals("SUCCESS"))"SUCCESSFUL"
    else taskEnd.taskInfo.status

    taskEnd.taskInfo.accumulables.foreach(f => {

      listOfComponents.filter(c => c.newComponentId.equals(f.name.get)).foreach(component => {


        val alreadyPresentCompInfo  = componentInfoList.asScala
          .filter(compInfo=> compInfo.getComponentId.equals(component.compId))

        if (alreadyPresentCompInfo.size > 0) {

          componentInfoList.asScala
            .filter(compInfo => {
              compInfo.getComponentId.equals(component.compId) && compInfo.getCurrentStatus() != null && !compInfo.getCurrentStatus().equals("FAILED")
            }).foreach(componentInfo => {
            componentInfo.setStageId(taskEnd.stageId)
            componentInfo.setComponentId(component.compId)
            componentInfo.setBatch(component.batch)
            componentInfo.setComponentName(component.compName)
            var recordCount = 0L
            var countsList = new mutable.ListBuffer[Long]
            if(f.value.get.isInstanceOf[java.lang.Long]){
              println("LongAcc Name : " + f.name + "LongAcc Value : "+ f.value)
            }
            else{


              val tempMap = f.value.iterator.next().asInstanceOf[HashMap[Int, HashMap[Int, (Long, Int)]]]


              for((stageId,hashmap)<-tempMap){
                var sumCount = 0L
                  for((intval,longintmap)<-hashmap){
                    sumCount += longintmap._1
                    recordCount = longintmap._1
                  }
                countsList+=sumCount
              }
            }

            var finalCount = max(countsList.toList).get


            componentInfo.setProcessedRecordCount(component.outSocket, finalCount.asInstanceOf[Long])
            componentInfo.setStatusPerSocketMap(component.outSocket, Status)
            if(Status.equals("FAILED")){
              componentInfo.setCurrentStatus("FAILED")
              componentInfo.setProcessedRecordCount(component.outSocket, -1)
            }
            else if(componentInfo.getCurrentStatus!=null && componentInfo.getCurrentStatus.equals("PENDING")){
              componentInfo.setCurrentStatus("RUNNING")
            }

          })

          //logic for status of component whose compInfo is not generated
          /*componentInfoList.asScala
            .filter(c=>c.getCurrentStatus.equals("PENDING")).foreach(ci=>
            {ci.setCurrentStatus("RUNNING")
              for(key : String <- ci.getStatusPerSocketMap().keySet().asScala){
                       ci.setStatusPerSocketMap(key,"RUNNING")
                   }
            })*/
        }
      })
    })
  }

  private def generateStatsForTaskStart(taskStart: SparkListenerTaskStart): Unit = {
    val Status = if(taskStart.taskInfo.status.equals("SUCCESS"))"SUCCESSFUL"
    else taskStart.taskInfo.status

    taskStart.taskInfo.accumulables.foreach(f => {
      listOfComponents.filter(c => c.newComponentId.equals(f.name.get)).foreach(component => {


        val alreadyPresentCompInfo  = componentInfoList.asScala
          .filter(compInfo=> compInfo.getComponentId.equals(component.compId))

        if (alreadyPresentCompInfo.size > 0) {

          componentInfoList.asScala
            .filter(compInfo => {
              compInfo.getComponentId.equals(component.compId) && !compInfo.getCurrentStatus().equals("FAILED")
            }).foreach(componentInfo => {
            componentInfo.setStageId(taskStart.stageId)
            componentInfo.setComponentId(component.compId)
            componentInfo.setBatch(component.batch)
            componentInfo.setComponentName(component.compName)

            var recordCount = 0L
            var countsList = new mutable.ListBuffer[Long]

            if(f.value.get.isInstanceOf[java.lang.Long]){
            }
            else{
              val tempMap = f.value.iterator.next().asInstanceOf[HashMap[Int, HashMap[Int, (Long, Int)]]]

              for((stageId,hashmap)<-tempMap){
                var sumCount = 0L
                  for((intval,longintmap)<-hashmap){
                    sumCount += longintmap._1
                    recordCount = longintmap._1
                  }
                countsList+=sumCount
              }
            }

            var finalCount = max(countsList.toList).get

            componentInfo.setProcessedRecordCount(component.outSocket, finalCount.asInstanceOf[Long])
            componentInfo.setStatusPerSocketMap(component.outSocket, Status)
            if(Status.equals("FAILED")){
              componentInfo.setCurrentStatus("FAILED")
              componentInfo.setProcessedRecordCount(component.outSocket, -1)
            }
            else if(componentInfo.getCurrentStatus!=null && componentInfo.getCurrentStatus.equals("PENDING")){
              componentInfo.setCurrentStatus("RUNNING")
            }
          })
        }
      })
    })
  }

  private def generateStatsForTaskGettingResult(taskStart: SparkListenerTaskGettingResult): Unit = {
  }


  def getStatus(): java.util.List[ComponentInfo] = {
    componentInfoList //componentInfoMap.values.toList
  }

  def createComponentInfos(): Unit ={

    listOfComponents.foreach({ comp=>
      val componentInfo = new ComponentInfo()
      componentInfo.setStageId(-1)
      componentInfo.setComponentId(comp.compId)
      componentInfo.setBatch(comp.batch)
      componentInfo.setComponentName(comp.compName)
      componentInfo.setProcessedRecordCount(comp.outSocket,0)
      componentInfo.setStatusPerSocketMap(comp.outSocket,"")
      componentInfo.setCurrentStatus("")
      componentInfoList.add(componentInfo)
    })
  }

  def getComponentInfoMap(): mutable.ListBuffer[Component] ={
    listOfComponents
  }

  def max(xs: List[Long]): Option[Long] = xs match {
    case Nil => None
    case List(x: Long) => Some(x)
    case x :: y :: rest => max( (if (x > y) x else y) :: rest )
  }


}