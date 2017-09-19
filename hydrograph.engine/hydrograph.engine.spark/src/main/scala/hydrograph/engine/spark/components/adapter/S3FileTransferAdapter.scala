package hydrograph.engine.spark.components.adapter

import hydrograph.engine.core.component.generator.S3FileTransferEntityGenerator
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent
import hydrograph.engine.spark.components.AWSS3FileTransferComponent
import hydrograph.engine.spark.components.adapter.base.RunProgramAdapterBase
import hydrograph.engine.spark.components.base.CommandComponentSparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams

/**
  * Created by damodharraop on 9/7/2017.
  */
class S3FileTransferAdapter(typeBaseComponent: TypeBaseComponent) extends RunProgramAdapterBase{

  private var s3FileTransferEntityGenerator:S3FileTransferEntityGenerator=null;
  private var awsS3FileTransferComponent:AWSS3FileTransferComponent=null;

  override def createGenerator(): Unit = {

    s3FileTransferEntityGenerator = new S3FileTransferEntityGenerator(typeBaseComponent)

  }
  override def createComponent(baseComponentParams: BaseComponentParams): Unit = {

    awsS3FileTransferComponent=new AWSS3FileTransferComponent(s3FileTransferEntityGenerator.getEntity);
  }
  override def getComponent(): CommandComponentSparkFlow = awsS3FileTransferComponent

}
