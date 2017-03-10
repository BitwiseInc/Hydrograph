package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.LimitEntity
import hydrograph.engine.spark.components.base.StraightPullComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.DataFrame
import org.slf4j.LoggerFactory
/**
  * The Class LimitComponent.
  *
  * @author Bitwise
  *
  */
class LimitComponent(limitEntity : LimitEntity, componentsParams : BaseComponentParams) extends StraightPullComponentBase with Serializable{

  val LOG = LoggerFactory.getLogger(classOf[LimitComponent])

  override def createComponent(): Map[String, DataFrame] = {
    try {
      LOG.trace(limitEntity.toString);
      val limit = limitEntity.getMaxRecord.toInt
      val output = componentsParams.getDataFrame.limit(limit)
      val outSocketId = limitEntity.getOutSocketList.get(0).getSocketId

      LOG.info("Created Limit component "
        + limitEntity.getComponentId + "with records " + limitEntity.getMaxRecord.toInt)

      Map(outSocketId -> output)

    } catch {
      case e: RuntimeException => LOG.error("Error in Limit component : " + limitEntity.getComponentId, e); throw e
    }
  }
}
