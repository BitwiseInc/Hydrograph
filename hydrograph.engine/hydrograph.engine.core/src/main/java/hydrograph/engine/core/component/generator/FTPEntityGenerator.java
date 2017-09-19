package hydrograph.engine.core.component.generator;

import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.generator.base.CommandComponentGeneratorBase;
import hydrograph.engine.jaxb.commandtypes.FTP;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

import static hydrograph.engine.core.constants.Constants.DEFAULT_FTP_PORT;

/**
 * Created by damodharraop on 8/3/2017.
 */
public class FTPEntityGenerator extends CommandComponentGeneratorBase {

    private FTP ftp;
    private RunFileTransferEntity runFileTransferEntity;

    public FTPEntityGenerator (TypeBaseComponent typeCommandComponent) {
        super(typeCommandComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        ftp=(FTP)baseComponent;
    }

    @Override
    public void createEntity() {
        runFileTransferEntity=new RunFileTransferEntity();
    }

    @Override
    public void initializeEntity() {
        runFileTransferEntity.setHostName(ftp.getHostName());
        runFileTransferEntity.setUserName(ftp.getUserName());
        runFileTransferEntity.setInputFilePath(ftp.getInputFilePath());
        runFileTransferEntity.setOutFilePath(ftp.getOutputFilePath());
        if(ftp.getFileOperation().getDownload()!=null)
        runFileTransferEntity.setFileOperation("download");
        else
            runFileTransferEntity.setFileOperation("upload");
        runFileTransferEntity.setPassword(ftp.getPassword());
        if(ftp.getPortNo()!=null)
            runFileTransferEntity.setPortNo(ftp.getPortNo());
        else
            runFileTransferEntity.setPortNo(DEFAULT_FTP_PORT);
        if(ftp.getTimeOut()!=null) {
            runFileTransferEntity.setTimeOut(ftp.getTimeOut());
        }
        if (ftp.getRetryAfterDuration()!=null)
        runFileTransferEntity.setRetryAfterDuration(ftp.getRetryAfterDuration());
        if (ftp.getRetryAttempt()!=null)
        runFileTransferEntity.setRetryAttempt(ftp.getRetryAttempt());


            runFileTransferEntity.setEncoding( ftp.getEncoding() != null ? ftp.getEncoding().getValue().value() : "UTF-8");
        if(ftp.isFailOnError()!=null){
            runFileTransferEntity.setFailOnError(ftp.isFailOnError());
        }
        if(ftp.getOverwritemode()!=null){
            runFileTransferEntity.setOverwrite(ftp.getOverwritemode());
        }




    }


    @Override
    public AssemblyEntityBase getEntity() {
        return runFileTransferEntity;
    }
}