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
package hydrograph.engine.spark.executiontracking.plugin

import hydrograph.engine.core.flowmanipulation.{FlowManipulationContext, ManipulatorListener}
import hydrograph.engine.core.utilities.SocketUtilities
import hydrograph.engine.execution.tracking.ComponentInfo
import hydrograph.engine.jaxb.commontypes._
import hydrograph.engine.jaxb.operationstypes.Executiontracking
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.execution.tracking.{ComponentMapping, JobInfo}
import hydrograph.engine.spark.flow.RuntimeContext
import org.apache.spark.scheduler._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * The Class ExecutionTrackingPlugin.
  *
  * @author Bitwise
  *
  */
class Component(val compId:String,val compName:String,val batch:String,val outSocket:String,val newComponentId:String,val inSocketsPresent:Boolean){

  override def equals(obj: scala.Any): Boolean = {
    var flag = false;
    val emp = obj.asInstanceOf[Component]
    if( emp.compId.equals(compId))
      flag = true;
    return flag;
  }

  override def hashCode():Int = compId.hashCode
}

object Component{
  def apply(compId: String, compName: String, batch: String, outSocket: String, newComponentId: String,inSocketsPresent: Boolean): Component
  = new Component(compId, compName, batch, outSocket, newComponentId,inSocketsPresent)
}

class ExecutionTrackingPlugin extends ExecutionTrackingListener with ManipulatorListener with HydrographCommandListener{

  var jobInfo:JobInfo=null

  val LOG = LoggerFactory.getLogger(classOf[ExecutionTrackingPlugin])

  override def execute(manipulationContext: FlowManipulationContext): java.util.List[TypeBaseComponent] = {
    manipulationContext.getJaxbMainGraph

    val jaxbObjectList =new ListBuffer[TypeBaseComponent];
    jaxbObjectList.++=(manipulationContext.getJaxbMainGraph.asScala.toIterator)

    manipulationContext.getJaxbMainGraph.asScala.foreach(typeBaseComponent=> {

      if(typeBaseComponent.isInstanceOf[TypeCommandComponent]){
        ComponentMapping.addComponent(Component(typeBaseComponent.getId,typeBaseComponent.getName,typeBaseComponent.getBatch,"NoSocketId","NoExecutionTrackingComponent",false))
        val list : ListBuffer[String] = new ListBuffer[String]
        val map : mutable.HashMap[String,ListBuffer[String]] = new mutable.HashMap[String,ListBuffer[String]]
        list+=typeBaseComponent.getId
        map.put(typeBaseComponent.getId,list)
        ComponentMapping.addComps(map)
      }

      val outSocketList = TrackComponentUtils.getOutSocketListofComponent(typeBaseComponent).asScala.toList
      outSocketList.foreach(outSocket => {
        val trackContext = new TrackContext
        trackContext.setFromComponentId(typeBaseComponent.getId)
        trackContext.setBatch(typeBaseComponent.getBatch)
        trackContext.setComponentName(typeBaseComponent.getName)
        trackContext.setFromOutSocketId(outSocket.getSocketId)
        trackContext.setFromOutSocketType(outSocket.getSocketType)
        val executiontracking: Executiontracking = TrackComponentUtils.generateFilterAfterEveryComponent(trackContext,
          jaxbObjectList.asJava, manipulationContext.getSchemaFieldMap)

        val component: TypeBaseComponent = TrackComponentUtils.getComponent(jaxbObjectList.asJava,
          trackContext.getFromComponentId, trackContext.getFromOutSocketId)
        SocketUtilities.updateComponentInSocket(component, trackContext.getFromComponentId, trackContext
          .getFromOutSocketId, executiontracking.getId, "out0")

        val inSocketList= TrackComponentUtils
          .extractInSocketListOfComponents(typeBaseComponent)
        val inSocketsPresent = inSocketList.size()>0

        ComponentMapping.addComponent(Component(typeBaseComponent.getId,typeBaseComponent.getName,typeBaseComponent.getBatch,outSocket.getSocketId,executiontracking.getId,inSocketsPresent))

        jaxbObjectList += executiontracking
      })
    })

    jaxbObjectList.foreach(typeBaseComponent=>{
      if(typeBaseComponent.isInstanceOf[TypeOutputComponent]){
        val inSocketList= TrackComponentUtils
          .extractInSocketList(typeBaseComponent.asInstanceOf[TypeOutputComponent].getInSocket)
        inSocketList.asScala.foreach(inSocket=>{
          ComponentMapping.
            addComponent(Component(typeBaseComponent.getId,typeBaseComponent.getName,typeBaseComponent.getBatch,"NoSocketId",inSocket.getFromComponentId,false))


          var listOfPrevComps : mutable.ListBuffer[String] = new mutable.ListBuffer[String]
          val outCompID = typeBaseComponent.getId
          val prevComp = TrackComponentUtils.getCurrentComponent(jaxbObjectList.asJava,inSocket.getFromComponentId,inSocket.getFromSocketId)
          listOfPrevComps+=inSocket.getFromComponentId
          val mapOfOutCompAndPrevComps = getFlowMapFromOuputComp(prevComp,jaxbObjectList,outCompID,listOfPrevComps)
          ComponentMapping.addComps(mapOfOutCompAndPrevComps)
        })
      }

    })

    jaxbObjectList.asJava
  }

  def getFlowMapFromOuputComp(typeBaseComponent: TypeBaseComponent,jaxbObjectList : ListBuffer[TypeBaseComponent],outCompID : String,listOfPrevComps : mutable.ListBuffer[String]):mutable.HashMap[String,ListBuffer[String]]= {

    var outCompAndPrevCompsMap : mutable.HashMap[String,ListBuffer[String]] = new mutable.HashMap[String,ListBuffer[String]]()
    val tempInSocketList = TrackComponentUtils.extractInSocketListOfComponents(typeBaseComponent)
    tempInSocketList.asScala.foreach(inSock1=>{
      val prevComp = TrackComponentUtils.getCurrentComponent(jaxbObjectList.asJava,inSock1.getFromComponentId,                      inSock1.getFromSocketId)
      if(!prevComp.isInstanceOf[TypeInputComponent]){
        listOfPrevComps+=prevComp.getId
        getFlowMapFromOuputComp(prevComp,jaxbObjectList,outCompID,listOfPrevComps)
      }
      else{
        listOfPrevComps+=prevComp.getId
      }
      outCompAndPrevCompsMap.put(outCompID,listOfPrevComps)

    })
    return outCompAndPrevCompsMap

  }



  //  override def addListener(sparkSession: SparkSession): Unit = super.addListener(sparkSession)
  override def addListener(runtimeContext: RuntimeContext): Unit = {
    jobInfo = new JobInfo(ComponentMapping.getListOfComponents())
    jobInfo.createComponentInfos()
    runtimeContext.sparkSession.sparkContext.addSparkListener(this)

  }

  override def onApplicationStart(applicationStart: SparkListenerApplicationStart) {
  }

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd) {
  }


  override def onJobStart(jobStart: SparkListenerJobStart) {

  }

  override def onJobEnd(jobEnd: SparkListenerJobEnd): Unit = {
  }

  override def onStageSubmitted(stageSubmitted: SparkListenerStageSubmitted) {

    jobInfo.updateStatusOfComponentsOnStageSubmitted(stageSubmitted)
  }


  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted) {

    jobInfo.updateStatusOfComponents(stageCompleted)

  }


  override def onTaskGettingResult(taskGettingResult: SparkListenerTaskGettingResult) {

    jobInfo.storeComponentStatsForTaskGettingResult(taskGettingResult)
  }


  override def onTaskEnd(taskEnd: SparkListenerTaskEnd) {
    jobInfo.storeComponentStatsForTaskEnd(taskEnd)

  }

  override def onBlockUpdated(blockUpdated: SparkListenerBlockUpdated) {
  }

  override def onTaskStart(taskStart: SparkListenerTaskStart) {

    jobInfo.storeComponentStatsForTaskStart(taskStart)
  }

  override def getStatus(): java.util.List[ComponentInfo] = {
    return jobInfo.getStatus()
  }

  override def end(flow: SparkFlow): Unit = {

    val flowName = flow.getSparkFlowName()
    val mapOfOutCompsAndPrevComps = ComponentMapping.getComps()
    mapOfOutCompsAndPrevComps.filter(flow=>flow._1.equals(flowName))
      .foreach(lastComponentAndPrevComponentsMap=>{lastComponentAndPrevComponentsMap._2
        .foreach(eachComp=>{
          jobInfo.componentInfoList.asScala
            .filter(compInfo => {
              compInfo.getComponentId.equals(eachComp)
            }).foreach(component=>{
            if(component.getCurrentStatus.equals("PENDING")){
              component.setCurrentStatus("SUCCESSFUL")
            }
          })
        })
        jobInfo.componentInfoList.asScala.filter(comp=>comp.getComponentId.equals( lastComponentAndPrevComponentsMap._1)).foreach(component=>{
          if(component.getCurrentStatus.equals("PENDING")){
            component.setCurrentStatus("SUCCESSFUL")
          }
        })

      })
  }


  override def start(flow: SparkFlow): Unit = {
    val flowName = flow.getSparkFlowName()
    val mapOfOutCompsAndPrevComps = ComponentMapping.getComps()
    mapOfOutCompsAndPrevComps.filter(flow=>flow._1.equals(flowName))
      .foreach(lastComponentAndPrevComponentsMap=>lastComponentAndPrevComponentsMap._2
        .foreach(eachComp=>{
          jobInfo.componentInfoList.asScala
            .filter(compInfo => {
              compInfo.getComponentId.equals(eachComp)
            }).foreach(component=>{
            if(component.getCurrentStatus == null){
              component.setCurrentStatus("PENDING")
            }
            else if(!component.getCurrentStatus.equals("SUCCESSFUL") && !component.getCurrentStatus.equals("RUNNING") && !component.getCurrentStatus.equals("FAILED")){
              component.setCurrentStatus("PENDING")
            }
          })
        }))

    jobInfo.componentInfoList.asScala
      .filter(compInfo => {
        compInfo.getComponentId.equals(flowName)
      }).foreach(component=>{
      if(component.getCurrentStatus == null){
        component.setCurrentStatus("PENDING")
      }
      else if(!component.getCurrentStatus.equals("SUCCESSFUL") && !component.getCurrentStatus.equals("RUNNING") && !component.getCurrentStatus.equals("FAILED")){
        component.setCurrentStatus("PENDING")
      }
    })
  }

  override def failComponentsOfFlow(sparkFlow: SparkFlow): Unit = {
    val flowName = sparkFlow.getSparkFlowName()
    val mapOfOutCompsAndPrevComps = ComponentMapping.getComps()
    mapOfOutCompsAndPrevComps.filter(flow=>flow._1.equals(flowName))
      .foreach(lastComponentAndPrevComponentsMap=>lastComponentAndPrevComponentsMap._2
        .foreach(eachComp=>{
          jobInfo.componentInfoList.asScala
            .filter(compInfo => {
              compInfo.getComponentId.equals(eachComp)
            }).foreach(component=>{
            if(component.getCurrentStatus == null){
              component.setCurrentStatus("")
            }
            else if (component.getCurrentStatus.equals("RUNNING") || component.getCurrentStatus.equals("PENDING")){
              component.setCurrentStatus("FAILED")
            }
          })
        }))
    jobInfo.componentInfoList.asScala
      .filter(compInfo => {
        compInfo.getComponentId.equals(flowName)
      }).foreach(component=>{
      if(component.getCurrentStatus == null){
        component.setCurrentStatus("")
      }
      /*else if (component.getCurrentStatus.equals("RUNNING") || component.getCurrentStatus.equals("PENDING")){
        component.setCurrentStatus("FAILED")
      }*/
      else{
        component.setCurrentStatus("FAILED")
        component.setProcessedRecordCount("NoSocketId",0)
        component.setStatusPerSocketMap("NoSocketId","FAILED")
      }
    })

    jobInfo.componentInfoList.asScala.forall(component=>{
      if(component.getCurrentStatus == null || component.getCurrentStatus.equals("PENDING")){
        component.setCurrentStatus("")
        true
      }
      else{true}
    })
  }
}