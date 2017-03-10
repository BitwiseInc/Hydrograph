package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.DiscardEntity
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.slf4j.LoggerFactory

/**
  * The Class DiscardComponent.
  *
  * @author Bitwise
  *
  */
class DiscardComponent(discardEntity: DiscardEntity, componentsParams: BaseComponentParams) extends SparkFlow
{
  val LOG = LoggerFactory.getLogger(classOf[DiscardComponent])

  override def execute(): Unit = {
    try {
      LOG.trace(discardEntity.toString)
      LOG.info("Created Discard Component "+ discardEntity.getComponentId
        + " in Batch "+ discardEntity.getBatch )
      componentsParams.getDataFrame().count()
    } catch {
      case ex: Exception => LOG.error("Error in Discard component " + discardEntity.getComponentId, ex)
        throw ex
    }
  }
}
