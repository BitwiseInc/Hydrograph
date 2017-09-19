package hydrograph.engine.spark.components;

/**
 * Created by damodharraop on 8/3/2017.
 */


import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import java.io.Serializable;
import hydrograph.engine.spark.components.base.CommandComponentSparkFlow;
import org.apache.log4j.Logger;
import hydrograph.engine.spark.datasource.utils.FTPUtil;

public class FTPComponent extends CommandComponentSparkFlow implements Serializable {

    static final Logger log=Logger.getLogger(FTPComponent.class.getName());
    private AssemblyEntityBase assemblyEntityBase;
    private RunFileTransferEntity runFileTransferEntity;

    public FTPComponent(AssemblyEntityBase assemblyEntityBase) {
        this.assemblyEntityBase = assemblyEntityBase;
        runFileTransferEntity=(RunFileTransferEntity)assemblyEntityBase;
    }

    @Override
    public void execute() {
            log.debug("Start executing FTPComponent");
            FTPUtil ftpUtil=new FTPUtil();
            if(runFileTransferEntity.getFileOperation().equals("upload")){
                ftpUtil.upload(runFileTransferEntity);
            }
            else{
                ftpUtil.download(runFileTransferEntity);
            }
        log.debug("Finished executing FTPComponent");
        }

}
