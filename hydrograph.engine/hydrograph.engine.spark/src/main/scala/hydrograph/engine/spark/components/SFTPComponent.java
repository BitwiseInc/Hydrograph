package hydrograph.engine.spark.components;

import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import java.io.Serializable;
import hydrograph.engine.spark.components.base.CommandComponentSparkFlow;
import hydrograph.engine.spark.datasource.utils.SFTPUtil;
import org.apache.log4j.Logger;

/**
 * Created by damodharraop on 8/3/2017.
 */
public class SFTPComponent extends CommandComponentSparkFlow implements Serializable {

    static final Logger log = Logger.getLogger(SFTPComponent.class.getName());
    private AssemblyEntityBase assemblyEntityBase;
    private RunFileTransferEntity runFileTransferEntity;

    public SFTPComponent(AssemblyEntityBase assemblyEntityBase) {
        this.assemblyEntityBase = assemblyEntityBase;
        runFileTransferEntity = (RunFileTransferEntity) assemblyEntityBase;
    }

    @Override
    public void execute() {
        log.debug("Start executing SFTPComponent");
        SFTPUtil sftpUtil = new SFTPUtil();
        if (runFileTransferEntity.getFileOperation().equals("upload")) {
            sftpUtil.upload(runFileTransferEntity);
        } else {
            sftpUtil.download(runFileTransferEntity);
        }
        log.debug("Finished executing SFTPComponent");
    }
}