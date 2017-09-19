package hydrograph.engine.spark.components;

import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.spark.components.base.CommandComponentSparkFlow;
import hydrograph.engine.spark.datasource.utils.AWSS3Util;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Created by damodharraop on 9/7/2017.
 */
public class AWSS3FileTransferComponent extends CommandComponentSparkFlow implements Serializable {

    static final Logger log=Logger.getLogger(FTPComponent.class.getName());
    private AssemblyEntityBase assemblyEntityBase;
    private RunFileTransferEntity runFileTransferEntity;

    public AWSS3FileTransferComponent(AssemblyEntityBase assemblyEntityBase) {
        this.assemblyEntityBase = assemblyEntityBase;
        runFileTransferEntity=(RunFileTransferEntity)assemblyEntityBase;
    }

    @Override
    public void execute() {
        log.debug("Start executing AWSS3FileTransferComponent");
        AWSS3Util awss3Util=new AWSS3Util();
        if(runFileTransferEntity.getFileOperation().equals("upload")){
            awss3Util.upload(runFileTransferEntity);
        }
        else{
            awss3Util.download(runFileTransferEntity);
        }
        log.debug("Finished executing AWSS3FileTransferComponent");
    }

}