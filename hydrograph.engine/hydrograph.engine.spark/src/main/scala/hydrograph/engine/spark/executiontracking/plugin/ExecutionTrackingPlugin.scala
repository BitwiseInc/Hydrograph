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

import hydrograph.engine.flowmanipulation.{FlowManipulationContext, ManipulatorListener}
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
  * The Class Component.
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

  //  override def addListener(runtimeContext: RuntimeContext): Unit = {
  //    runtimeContext.sparkSession.sparkContext.addSparkListener(this)
  //
  //    ComponentMapping.generateComponentAndPreviousrMap(runtimeContext)
  //  }


  //  override def addListener(sparkSession: SparkSession): Unit = super.addListener(sparkSession)
  override def addListener(runtimeContext: RuntimeContext): Unit = {
    jobInfo = new JobInfo(ComponentMapping.getListOfComponents())
    jobInfo.createComponentInfos()
    runtimeContext.sparkSession.sparkContext.addSparkListener(this)

  }

  override def onApplicationStart(applicationStart: SparkListenerApplicationStart) {
    //    println("+++++++++++++++++++++Application Start+++++++++++++++++++++")
    //    println("Spark ApplicationStart: " + applicationStart.appName);
    //    println("Spark ApplicationId: " + applicationStart.appId);
    //    println("Spark ApplicationAttemptId: " + applicationStart.appAttemptId.get);
    //    println("Spark ApplicationDriverLogs: " + applicationStart.driverLogs.get.values.foreach(f => println(f)));
    //    println("+++++++++++++++++++++Application Start End+++++++++++++++++++++")
  }

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd) {
    //        println("+++++++++++++++++++++Application End+++++++++++++++++++++")


    /*jobInfo.componentInfoList.asScala.foreach(c=>{
      if(!c.getCurrentStatus.equals("FAILED") && c.getStageId.equals(-1)){
        c.setCurrentStatus("SUCCESSFUL")
      }

    })*/
    //        println("Spark ApplicationEnd: " + applicationEnd.time+" msec");
  }


  override def onJobStart(jobStart: SparkListenerJobStart) {
    //        println("+++++++++++++++++++++Job Start+++++++++++++++++++++")
    //    println(s"Job id : ${jobStart.jobId} ")
    //    println(s"Job stage ids : ${jobStart.stageIds.foreach(f => print(f + " , "))} ")
    //    print("Job stage Info : ")
    /*jobStart.stageInfos.foreach(f => {
      print(" Stage id "+ f.stageId )
    })*/

    //    println(s"Job properties : ${jobStart.properties} ")
    //    println("+++++++++++++++++++++Job Start End+++++++++++++++++++++")


  }

  override def onJobEnd(jobEnd: SparkListenerJobEnd): Unit = {
    //    println("+++++++++++++++++++++Job End Start+++++++++++++++++++++")
    //    println(s"Job id : ${jobEnd.jobId} ")
    //    println(s"Job end result: ${jobEnd.jobResult}")
    //
    //    println("+++++++++++++++++++++Job End End+++++++++++++++++++++")
  }

  override def onStageSubmitted(stageSubmitted: SparkListenerStageSubmitted) {
    //        println("+++++++++++++++++++++Stage Submit Start+++++++++++++++++++++")
    //
    //    println("Stage Id : "+stageSubmitted.stageInfo.stageId)
    //    println("Stage details: "+stageSubmitted.stageInfo.details)
    //        println("Stage name: "+stageSubmitted.stageInfo.name)

    jobInfo.updateStatusOfComponentsOnStageSubmitted(stageSubmitted)
    //    println("Stage no of Tasks : "+stageSubmitted.stageInfo.numTasks)
    //    println("Stage Rdd Info : " + stageSubmitted.stageInfo.rddInfos.mkString)
    //    println("Stage Rdd Info Name: "+stageSubmitted.stageInfo.rddInfos.foreach(f=>f.name))
    //    println("Stage Rdd Info Id: "+stageSubmitted.stageInfo.rddInfos.foreach(f=>f.id))
    //    println("Records Read "+stageSubmitted.stageInfo.taskMetrics.inputMetrics.recordsRead)
    //    println("Records Written "+stageSubmitted.stageInfo.taskMetrics.outputMetrics.recordsWritten)
    //    println("Stage Submit Properties "+stageSubmitted.properties)
    //        println("+++++++++++++++++++++Stage Submit End+++++++++++++++++++++")
  }


  override def onStageCompleted(stageCompleted: SparkListenerStageCompleted) {
    //        LOG.info("+++++++++++++++++++++Stage Complete Start+++++++++++++++++++++")
    //        println("attemptId :"+stageCompleted.stageInfo.attemptId)

    jobInfo.updateStatusOfComponents(stageCompleted)

    //        println("stageId :"+stageCompleted.stageInfo.stageId)

    //    println("failureReason :"+stageCompleted.stageInfo.failureReason)
    //        stageCompleted.stageInfo.accumulables.foreach(f =>{

    //          if(f._2.name.get.startsWith("filter")){
    //            LOG.info("Acc long Value= "+f._1)
    //          LOG.info("Acc iD : " + f._2.id + " Acc name : " + f._2.name.get + " Acc value : " + f._2.value.get + " Acc update : "
    //            + f
    //            ._2.update)
    //        })
    //    LOG.info("+++++++++++++++++++++Stage Complete end+++++++++++++++++++++")
    /*println(s"Stage ${stageCompleted.stageInfo.stageId} completed with ${stageCompleted.stageInfo.numTasks} tasks.")*/
    //    println("+++++++++++++++++++++Stage Complete Start+++++++++++++++++++++")
    //    println("attemptId :"+stageCompleted.stageInfo.attemptId)
    //    println("stageId :"+stageCompleted.stageInfo.stageId)
    //    println("Stage Name :"+stageCompleted.stageInfo.name)
    //                println("Stage Details:"+stageCompleted.stageInfo.details)
    //    println("Stage Rdd Info Name: "+stageCompleted.stageInfo.rddInfos.foreach(f=>f.name))
    //    println("Stage Rdd Info Scope: "+stageCompleted.stageInfo.rddInfos.foreach(f=>f.scope))
    //    println("submissionTime :"+stageCompleted.stageInfo.submissionTime+" ms")
    //    println("Completion Time :"+stageCompleted.stageInfo.completionTime+" ms")
    //    println("numTasks :"+stageCompleted.stageInfo.numTasks)
    //        println("recordsWritten :"+stageCompleted.stageInfo.taskMetrics.outputMetrics.recordsWritten)
    //    println("recordsRead :"+stageCompleted.stageInfo.taskMetrics.inputMetrics.recordsRead)
    //    println("Input Size :"+(stageCompleted.stageInfo.taskMetrics.inputMetrics.bytesRead).toInt/(1024*1024)+" MB")

    //    LOG.info("+++++++++++++++++++++Stage Complete end+++++++++++++++++++++")

    /*jobListener.activeJobs.values.foreach(
      f => {
        println("job id: " +f.jobId)
        println("job group: " +f.jobGroup)
        println("job status: " +f.status)
        println("job stage ids: " +f.stageIds.foreach(id => print(id + " , ")))
        println("job active stages : " +f.numActiveStages)
        println("job no. of tasks: " +f.numTasks)
        println("job active tasks: " +f.numActiveTasks)
        println("job completed tasks: " +f.numCompletedTasks)
        println("job completion time : " +f.completionTime);


      })*/
    //    stageCompleted
    //    println("------------------------- STAGE COMPLELTED---------------------------")
  }


  override def onTaskGettingResult(taskGettingResult: SparkListenerTaskGettingResult) {
    //        println("+++++++++++++++++++++Task Getting Result Start+++++++++++++++++++++")
    //        println("Task geeting result status : " + taskGettingResult.taskInfo.status)

    jobInfo.storeComponentStatsForTaskGettingResult(taskGettingResult)
    //    getStatus().asScala.foreach(println)
    //    println("Task geeting result id : " + taskGettingResult.taskInfo.taskId)
    //        println("+++++++++++++++++++++Task Getting Result End+++++++++++++++++++++")
  }


  override def onTaskEnd(taskEnd: SparkListenerTaskEnd) {
    //    LOG.info("+++++++++++++++++++++Task End Start+++++++++++++++++++++")

   /* println(" Task End Task id: " + taskEnd.taskInfo.taskId)
    taskEnd.taskInfo.accumulables.foreach(f => {
        LOG.info("Acc iD : " + f.id + " Acc name : " + f.name.get + " Acc value : " + f.value.get + " Acc update : "
          + f
          .update)
    })*/
    jobInfo.storeComponentStatsForTaskEnd(taskEnd)
//        getStatus().asScala.foreach(println)
    //    println("Task id: " + taskEnd.taskInfo.taskId)
    ////    println("Task Accumulables Info: " + taskEnd.taskInfo.accumulables.mkString)
    //    println("Task attemptNo: " + taskEnd.taskInfo.attemptNumber)
    //    println("Task duration: " + taskEnd.taskInfo.duration)
    //    println("Task launchTime: " + taskEnd.taskInfo.launchTime)
    //        println("Task Status: " + taskEnd.taskInfo.status)
    //    println("Task executorId: " + taskEnd.taskInfo.executorId)
    //    println("Task host: " + taskEnd.taskInfo.host)
    //    println("Task host: " + taskEnd.taskInfo.taskLocality)
    //    println("Task type: " + taskEnd.taskType)
    //    println("Task end reason: " + taskEnd.reason)
    //    println("Task result size: " + taskEnd.taskMetrics.resultSize)
    //    println("Task records written: " + taskEnd.taskMetrics.outputMetrics.recordsWritten)
    //    println("Task stage Id : " + taskEnd.stageId)
    //    println("Task records read: " + taskEnd.taskMetrics.inputMetrics.recordsRead)
    //    LOG.info("+++++++++++++++++++++Task End End+++++++++++++++++++++")

  }

  override def onBlockUpdated(blockUpdated: SparkListenerBlockUpdated) {
    //        println("Spark Block Update name: " + blockUpdated.blockUpdatedInfo.blockId.name);
    //        println("Spark Block Update executorId: " + blockUpdated.blockUpdatedInfo.blockManagerId.executorId);
    //        println("Spark Block Update asRDDId.get: " + blockUpdated.blockUpdatedInfo.blockId.asRDDId.get);
    //        println("Spark Block Update asRDDId.get.name: " + blockUpdated.blockUpdatedInfo.blockId.asRDDId.get.name);
  }

  override def onTaskStart(taskStart: SparkListenerTaskStart) {
    //        println("+++++++++++++++++++++Task Start Start+++++++++++++++++++++")
    //        println("TaskStart id: " + taskStart.taskInfo.taskId)
    //    println("id: " + taskStart.taskInfo.id)
   /* println(" Task Start Task id: " + taskStart.taskInfo.taskId)
    taskStart.taskInfo.accumulables.foreach(f => {
      LOG.info("Acc iD : " + f.id + " Acc name : " + f.name.get + " Acc value : " + f.value.get + " Acc update : "
        + f
        .update)
    })*/
    /*taskStart.taskInfo.accumulables.foreach(f => {
      LOG.info("Acc iD : " + f.id + " Acc name : " + f.name.get + " Acc value : " + f.value.get + " Acc update : "
        + f
        .update)
    })*/

    jobInfo.storeComponentStatsForTaskStart(taskStart)
//        getStatus().asScala.foreach(println)
    //    println("Task attemptNo: " + taskStart.taskInfo.attemptNumber)
    //    //        println("Task duration: " + taskStart.taskInfo.duration)
    //    println("Task launchTime: " + taskStart.taskInfo.launchTime)
    //        println("Task Status: " + taskStart.taskInfo.status)
    //    println("Task executorId: " + taskStart.taskInfo.executorId)
    //    println("Task stage Id : " + taskStart.stageId)
    //        println("+++++++++++++++++++++Task Start End+++++++++++++++++++++")
  }

  override def getStatus(): java.util.List[ComponentInfo] = {
    return jobInfo.getStatus()
  }

  override def end(flow: SparkFlow): Unit = {

    /*if(flow.isInstanceOf[CommandComponentSparkFlow]){
      val cFlow = flow.asInstanceOf[CommandComponentSparkFlow]
      val Status : String = if(cFlow.exitStatus==0){"SUCCESSFUL"}
      else if(cFlow.exitStatus == -1)"FAILED"
      else "PENDING"

      jobInfo.getComponentInfoMap().filter(co=>{
        co.inSocketsPresent.equals(false) && co.outSocket.equals("NoSocketId") && co.newComponentId.equals("NoExecutionTrackingComponent")
      }).foreach(comp=>{
        val alreadyPresentCompInfo  = jobInfo.componentInfoList.asScala
          .filter(compInfo=> compInfo.getComponentId.equals(comp.compId))

        if (alreadyPresentCompInfo.size > 0) {
          jobInfo.componentInfoList.asScala
            .filter(compInfo => {
              compInfo.getComponentId.equals(comp.compId)
            }).foreach(componentInfo => {
            componentInfo.setStageId(0)
            componentInfo.setStatusPerSocketMap(comp.outSocket, Status)
            componentInfo.setCurrentStatus(Status)
          })
        }
      })
    }*/
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
      else if (component.getCurrentStatus.equals("RUNNING") || component.getCurrentStatus.equals("PENDING")){
        component.setCurrentStatus("FAILED")
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