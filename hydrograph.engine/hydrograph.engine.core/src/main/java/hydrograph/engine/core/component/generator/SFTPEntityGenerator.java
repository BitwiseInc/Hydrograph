package hydrograph.engine.core.component.generator;


import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.generator.base.CommandComponentGeneratorBase;
import hydrograph.engine.jaxb.commandtypes.SFTP;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

import static hydrograph.engine.core.constants.Constants.DEFAULT_SFTP_PORT;

/**
 * Created by damodharraop on 8/3/2017.
 */
public class SFTPEntityGenerator extends CommandComponentGeneratorBase{

    private SFTP sftp;
    private RunFileTransferEntity runFileTransferEntity;

    public SFTPEntityGenerator (TypeBaseComponent typeCommandComponent) {
        super(typeCommandComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        sftp=(SFTP)baseComponent;

    }

    @Override
    public void createEntity() {
        runFileTransferEntity=new RunFileTransferEntity();
    }

    @Override
    public void initializeEntity() {
        runFileTransferEntity.setHostName(sftp.getHostName());
        runFileTransferEntity.setUserName(sftp.getUserName());
        runFileTransferEntity.setInputFilePath(sftp.getInputFilePath());
        runFileTransferEntity.setOutFilePath(sftp.getOutputFilePath());
        if(sftp.getFileOperation().getDownload()!=null)
            runFileTransferEntity.setFileOperation("download");
        else
            runFileTransferEntity.setFileOperation("upload");
        if(sftp.getPassword()!=null)
        runFileTransferEntity.setPassword(sftp.getPassword());
        else
            runFileTransferEntity.setPrivateKeyPath(sftp.getPrivateKeyPath());
           if(sftp.getPortNo()!=null)
            runFileTransferEntity.setPortNo(sftp.getPortNo());
        else
            runFileTransferEntity.setPortNo(DEFAULT_SFTP_PORT);

        if(sftp.getTimeOut()!=null)
            runFileTransferEntity.setTimeOut(sftp.getTimeOut());

        if (sftp.getRetryAfterDuration()!=null)
            runFileTransferEntity.setRetryAfterDuration(sftp.getRetryAfterDuration());
        if (sftp.getRetryAttempt()!=null)
            runFileTransferEntity.setRetryAttempt(sftp.getRetryAttempt());
        runFileTransferEntity.setEncoding( sftp.getEncoding() != null ? sftp.getEncoding().getValue().value() : "UTF-8");
        if(sftp.isFailOnError()!=null){
            runFileTransferEntity.setFailOnError(sftp.isFailOnError());
        }
        if(sftp.getOverwritemode()!=null){
            runFileTransferEntity.setOverwrite(sftp.getOverwritemode());
        }
    }


    @Override
    public AssemblyEntityBase getEntity() {
        return runFileTransferEntity;
    }
}
