package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.UniqueSequenceEntity
import hydrograph.engine.core.component.utils.OperationUtils
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.handler.OperationHelper
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.transformation.userfunctions.base.TransformBase
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
/**
  * The Class UniqueSequenceComponent.
  *
  * @author Bitwise
  *
  */
class UniqueSequenceComponent(uniqueSequenceEntity: UniqueSequenceEntity, baseComponentParams: BaseComponentParams) extends OperationComponentBase with OperationHelper[TransformBase] with Serializable {

  val LOG = LoggerFactory.getLogger(classOf[UniqueSequenceComponent])

  /**
    * These method creates spark component for generating unique sequence
    *
    * @return Map[String, DataFrame]
    */
  override def createComponent(): Map[String, DataFrame] = {

    LOG.trace(uniqueSequenceEntity.toString)
    try {
      val passThroughFields = OperationUtils.getPassThrougFields(uniqueSequenceEntity.getOutSocketList.get(0).getPassThroughFieldsList, baseComponentParams.getDataFrame().schema.map(_.name)).asScala.toArray[String]

      val inputColumn = new Array[Column](passThroughFields.size)

      passThroughFields.zipWithIndex.foreach(
        passThroughField => {
          inputColumn(passThroughField._2) = column(passThroughField._1)
        })

      val operationField = uniqueSequenceEntity.getOperation.getOperationOutputFields.get(0)
      val df = baseComponentParams.getDataFrame().select(inputColumn: _*).withColumn(operationField, monotonically_increasing_id())

      val outSocketId = uniqueSequenceEntity.getOutSocketList.get(0).getSocketId

      LOG.info("Created Unique Sequence component " + uniqueSequenceEntity.getComponentId
        +" in batch " + uniqueSequenceEntity.getBatch )

      Map(outSocketId -> df)
    } catch {
      case ex: Exception =>
        LOG.error("Error in Unique Sequence component " + uniqueSequenceEntity.getComponentId, ex)
        throw new RuntimeException ("Error in Unique Sequence component",ex)
    }
  }
}

