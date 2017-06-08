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
package hydrograph.engine.spark.flow

import java.io.IOException
import java.util.Properties

import hydrograph.engine.core.core.{HydrographJob, HydrographRuntimeService}
import hydrograph.engine.core.flowmanipulation.{FlowManipulationContext, FlowManipulationHandler}
import hydrograph.engine.core.helper.JAXBTraversal
import hydrograph.engine.core.props.OrderedProperties
import hydrograph.engine.core.schemapropagation.SchemaFieldHandler
import hydrograph.engine.core.utilities.OrderedPropertiesHelper
import hydrograph.engine.spark.components.adapter.factory.AdapterFactory
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.executiontracking.plugin.{CommandComponentsDefaultPlugin, ExecutionTrackingListener, ExecutionTrackingPlugin, HydrographCommandListener}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.matching.Regex

/**
  * The Class HydrographRuntime.
  *
  * @author Bitwise
  *
  */

class HydrographRuntime extends HydrographRuntimeService {

  private val EXECUTION_TRACKING: String = "hydrograph.execution.tracking"
  private val LOG: Logger = LoggerFactory.getLogger(classOf[HydrographRuntime])
  private var flowManipulationContext: FlowManipulationContext = null;
  //  private var flows: mutable.LinkedHashSet[SparkFlow] = null
  private var flowBuilder:FlowBuilder = null
  var executionTrackingListener : ExecutionTrackingListener = null
  var hydrographListener : HydrographCommandListener = new CommandComponentsDefaultPlugin


  override def kill(): Unit = {
    LOG.info("Kill signal received")
    if (RuntimeContext.instance.sparkSession != null) {
      LOG.info("Killing Spark jobs")
      RuntimeContext.instance.sparkSession.stop()
    }
    else {
      LOG.info("No Spark jobs present to kill. Exiting code.")
      System.exit(0)
    }
  }

  override def initialize(properties: Properties, args: Array[String], hydrographJob: HydrographJob,
                          jobId: String, udfPath: String): Unit = {

    val configProperties = getSparkProperties(hydrographJob,properties)

    val sparkSessionBuilder: SparkSession.Builder = SparkSession.builder()
      .appName(hydrographJob.getJAXBObject.getName)
      .config(configProperties)


    val schemaFieldHandler = new SchemaFieldHandler(
      hydrographJob.getJAXBObject().getInputsOrOutputsOrStraightPulls())

    val flowManipulationContext = new FlowManipulationContext(hydrographJob, args, schemaFieldHandler, jobId)

    val flowManipulationHandler = new FlowManipulationHandler

    val  updatedHydrographJob=flowManipulationHandler.execute(flowManipulationContext)

    val adapterFactory = AdapterFactory(updatedHydrographJob.getJAXBObject)

    val traversal = new JAXBTraversal(updatedHydrographJob.getJAXBObject());

    val sparkSession: SparkSession = enableHiveSupport(sparkSessionBuilder, traversal, properties).getOrCreate()

    getAndSetHadoopProperties(sparkSession,hydrographJob,properties)

    val runtimeContext = RuntimeContext(adapterFactory, traversal, updatedHydrographJob,
      flowManipulationContext.getSchemaFieldHandler, sparkSession)



    val EXECUTION_TRACKING = "hydrograph.execution.tracking";

 //        val oproperties = OrderedPropertiesHelper.getOrderedProperties("RegisterPlugin.properties")
 //        val executionTrackingPluginName = oproperties.getProperty(EXECUTION_TRACKING)
 //        val trackingInstance = Class.forName(executionTrackingPluginName).newInstance()
 //        executionTrackingListener = trackingInstance.asInstanceOf[ExecutionTrackingPlugin]
 //        executionTrackingListener.addListener(runtimeContext)


    if (getExecutionTrackingClass(EXECUTION_TRACKING) != null) {
      /*var */executionTrackingListener = classLoader(getExecutionTrackingClass(EXECUTION_TRACKING)).asInstanceOf[ExecutionTrackingListener]
      val trackingInstance = Class.forName(getExecutionTrackingClass(EXECUTION_TRACKING)).newInstance()
      executionTrackingListener = trackingInstance.asInstanceOf[ExecutionTrackingPlugin]
      executionTrackingListener.addListener(runtimeContext)
      hydrographListener = trackingInstance.asInstanceOf[ExecutionTrackingPlugin]
    }

  }

  def enableHiveSupport(sessionBuilder: SparkSession.Builder, traversal: JAXBTraversal, properties: Properties): SparkSession.Builder = {
    LOG.trace("In method checkAndEnableHiveSupport()")
    if (traversal.isHiveComponentPresentInFlow) {
      LOG.debug("Hive components are present in flow. Enabling Hive support in SparkSession with warehouse location "+properties.getProperty("hydrograph.hive.warehouse"))
      sessionBuilder
        .config("spark.sql.warehouse.dir", properties.getProperty("hydrograph.hive.warehouse"))
        .enableHiveSupport()
    } else {
      sessionBuilder
        .config("spark.sql.warehouse.dir", properties.getProperty("hydrograph.tmp.warehouse"))
    }
    sessionBuilder
  }

  override def prepareToExecute(): Unit = {
    LOG.info("Building spark flows")
    flowBuilder = FlowBuilder(RuntimeContext.instance, hydrographListener)
    LOG.info("Spark flows built successfully")
  }

  override def execute(): Unit = {
    /*if (GeneralUtilities.IsArgOptionPresent(args, CommandLineOptionsProcessor.OPTION_NO_EXECUTION)) {
      LOG.info(CommandLineOptionsProcessor.OPTION_NO_EXECUTION + " option is provided so skipping execution")
      return
    }*/
    /*for (sparkFlow <- flows) {
        try{
          hydrographListener.start(sparkFlow)
          //        HydrographFlowPlugin.getComps()
          sparkFlow.execute()
          hydrographListener.end(sparkFlow)
          /* for(accumulator <- sparkFlow.getAccumulatorOnFlow()){
             accumulator.reset()
           }*/
        }
        catch{case e: Exception => {
          hydrographListener.failComponentsOfFlow(sparkFlow)
          //                executionTrackingListener.getStatus().asScala.foreach(println)
          throw e
        }
      }
    }*/

    flowBuilder.buildAndExecuteFlows()
    //    RuntimeContext.instance.sparkSession.sparkContext.longAccumulator
    RuntimeContext.instance.sparkSession.stop()
    //        executionTrackingListener.getStatus().asScala.foreach(println)
  }

  override def getExecutionStatus: AnyRef = {
    if (executionTrackingListener != null)
      return executionTrackingListener.getStatus()
    return null
  }

  override def oncomplete(): Unit = {
    //Deleting TempPath For Debug
    if (flowManipulationContext != null && flowManipulationContext.getTmpPath != null) {
      flowManipulationContext.getTmpPath.asScala.foreach(tmpPath => {
        val fullPath: Path = new Path(tmpPath)
        // do not delete the root directory
        if (fullPath.depth != 0) {
          var fileSystem: FileSystem = null
          LOG.info("Deleting temp path:" + tmpPath)
          try {
            fileSystem = FileSystem.get(RuntimeContext.instance.sparkSession.sparkContext.hadoopConfiguration)
            //            fileSystem.delete(fullPath, true)
          }
          catch {
            case exception: NullPointerException => {
              throw new RuntimeException(exception)
            }
            case e: IOException => {
              throw new RuntimeException(e)
            }
          }
        }
      })
    }
  }

  def classLoader[T](className: String): T = {
    val clazz = Class.forName(className).getDeclaredConstructors
    clazz(0).setAccessible(true)
    clazz(0).newInstance().asInstanceOf[T]
  }

  def getSparkProperties(hydrographJob: HydrographJob, properties: Properties): SparkConf = {
    val configProperties = new SparkConf()
    val sparkProperties = properties.entrySet().asScala
    for (property <- sparkProperties) {
      if(property.getKey.toString.startsWith("spark.") || property.getKey.toString.startsWith("hydrograph.")){
        configProperties.set(property.getKey.toString.trim, property.getValue.toString.trim)
      }
    }
    val runttimeProperties = hydrographJob.getJAXBObject().getRuntimeProperties
    if (runttimeProperties != null) {
      for (runtimeProperty <- runttimeProperties.getProperty.asScala) {
        if(runtimeProperty.getName.startsWith("spark.") || runtimeProperty.getName.startsWith("hydrograph.")){
          configProperties.set(runtimeProperty.getName.trim, runtimeProperty.getValue.trim)
        }
      }
    }

    configProperties
  }

  def getAndSetHadoopProperties(sparkSession: SparkSession, hydrographJob: HydrographJob, properties: Properties): Unit = {

    val configProperties = sparkSession.sparkContext.hadoopConfiguration
    val sparkProperties = properties.entrySet().asScala
    for (property <- sparkProperties) {

      property.getKey.toString match {
        case a if(!a.startsWith("spark.")) => {
          val pattern: Regex = "(.*)\\((.*)\\)".r
          val matchIterator = pattern.findAllIn(property.getValue.toString)
          if (matchIterator.hasNext) {
            while (matchIterator.hasNext) {
              matchIterator.group(1) match {
                case x if (x.endsWith("OrElse")) => {
                  val arrayOfParams: Array[String] = matchIterator.group(2).split(",")
                  configProperties.set(property.getKey.toString.trim, sys.env.getOrElse(arrayOfParams(0).trim.replaceAll("\"", ""), arrayOfParams(1).trim.replaceAll("\"", "")).trim)
                }
                case y if (y.endsWith("get")) => {
                  configProperties.set(property.getKey.toString.trim, sys.env.get(matchIterator.group(2).replaceAll("\"", "")).get.trim)
                }
              }
              matchIterator.next()
            }
          }
          else{
            configProperties.set(property.getKey.toString.trim, property.getValue.toString.trim)
          }
        }
        case _ =>
      }

    }
    val runttimeProperties = hydrographJob.getJAXBObject().getRuntimeProperties
    if (runttimeProperties != null) {
      for (runtimeProperty <- runttimeProperties.getProperty.asScala) {
        runtimeProperty.getName match {
          case a if(!a.startsWith("spark.")) => {
            val pattern: Regex = "(.*)\\((.*)\\)".r
            val matchIterator = pattern.findAllIn(runtimeProperty.getValue)
            if (matchIterator.hasNext) {
              while (matchIterator.hasNext) {
                matchIterator.group(1) match {
                  case x if (x.endsWith("OrElse")) => {
                    val arrayOfParams: Array[String] = matchIterator.group(2).split(",")
                    configProperties.set(runtimeProperty.getName.trim, sys.env.getOrElse(arrayOfParams(0).replaceAll("\"", ""), arrayOfParams(1).replaceAll("\"", "")).trim)
                  }
                  case y if (y.endsWith("get")) => {
                    configProperties.set(runtimeProperty.getName.trim, sys.env.get(matchIterator.group(2).replaceAll("\"", "")).get.trim)

                  }
                }
                matchIterator.next()
              }
            }
            else{
              configProperties.set(runtimeProperty.getName.trim, runtimeProperty.getValue.trim)
            }
          }
          case _ =>
        }
      }
    }
  }


  def getExecutionTrackingClass(executionTrackingKey: String): String = {
    var properties: OrderedProperties = new OrderedProperties
    try {
      properties = OrderedPropertiesHelper.getOrderedProperties("RegisterPlugin.properties")
    }
    catch {

      case e: IOException => {
        throw new RuntimeException("Error reading the properties file: RegisterPlugin.properties" , e)
      }
    }
    properties.getProperty(executionTrackingKey)
  }
}