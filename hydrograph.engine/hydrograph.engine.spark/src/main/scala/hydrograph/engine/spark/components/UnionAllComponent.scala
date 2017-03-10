package hydrograph.engine.spark.components

import java.util.Set

import hydrograph.engine.core.component.entity.UnionAllEntity
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.components.base.StraightPullComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.DataFrame
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * The Class UnionAllComponent.
  *
  * @author Bitwise
  *
  */
class UnionAllComponent(unionAllEntity: UnionAllEntity, componentsParams: BaseComponentParams)
  extends StraightPullComponentBase {
  val LOG = LoggerFactory.getLogger(classOf[UnionAllComponent])

  override def createComponent(): Map[String, DataFrame] = {
    LOG.trace(unionAllEntity.toString)
    try {
      val dataFrameList = componentsParams.getDataFrameList()
      val schemaFieldList = componentsParams.getSchemaFieldList()
      validateInputFields(schemaFieldList)
      val df = merge(dataFrameList)
      val outSocketId = unionAllEntity.getOutSocketList.get(0).getSocketId
      LOG.info("Created UnionAll component "+ unionAllEntity.getComponentId
        + " in batch "+ unionAllEntity.getBatch )
      Map(outSocketId -> df)
    } catch {
      case ex: Exception => LOG.error("Error in UnionAll component " + unionAllEntity.getComponentId, ex)
        throw ex
    }

  }

  def merge(dataFrameList: ListBuffer[DataFrame]): DataFrame = {
    def combine(dataFrame: DataFrame, acc: Int): DataFrame = {
      if (dataFrameList.size <= acc) dataFrame
      else combine(dataFrame.union(dataFrameList(acc).select(dataFrame.columns.head,dataFrame.columns.tail:_*)), acc + 1)
    }
    combine(dataFrameList(0), 1)
  }

  @throws(classOf[SchemaMismatchException])
  def validateInputFields(schemaFieldList: ListBuffer[Set[SchemaField]]) = {

    val refSchema = schemaFieldList(0)

    schemaFieldList.tail.foreach { sf =>
    {
      if (refSchema.size != sf.size) {
        LOG.error("Component:" + unionAllEntity.getComponentId()
          + " - Different schema is defined for input sockets. For UnionAll component schema of all input sockets should be same.")
        throw new SchemaMismatchException("Component:" + unionAllEntity.getComponentId()
          + " - Different schema is defined for input sockets. For UnionAll component schema of all input sockets should be same.");
      }
      if (!refSchema.containsAll(sf)) {
        LOG.error("Component:" + unionAllEntity.getComponentId()
          + " - Different schema is defined for input sockets. For UnionAll component schema of all input sockets should be same.")
        throw new SchemaMismatchException("Component:" + unionAllEntity.getComponentId()
          + " - Different schema is defined for input sockets. For UnionAll component schema of all input sockets should be same.");
      }
    }
    }
  }

  class SchemaMismatchException(msg: String) extends RuntimeException(msg: String)

}

