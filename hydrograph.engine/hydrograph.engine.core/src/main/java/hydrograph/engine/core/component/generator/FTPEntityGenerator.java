/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/

package hydrograph.engine.core.component.generator;

import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.generator.base.CommandComponentGeneratorBase;
import hydrograph.engine.jaxb.commandtypes.FTP;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

import static hydrograph.engine.core.constants.Constants.DEFAULT_FTP_PORT;

/**
 * Created for FTPEntityGenerator on 8/3/2017.
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
            runFileTransferEntity.setPortNo(ftp.getPortNo().getValue().intValue());
        else
            runFileTransferEntity.setPortNo(DEFAULT_FTP_PORT);
        if(ftp.getTimeOut()!=null) {
            runFileTransferEntity.setTimeOut(ftp.getTimeOut().getValue().intValue());
        }
        if (ftp.getRetryAfterDuration()!=null)
        runFileTransferEntity.setRetryAfterDuration(ftp.getRetryAfterDuration().getValue().intValue());
        if (ftp.getRetryAttempt()!=null)
        runFileTransferEntity.setRetryAttempt(ftp.getRetryAttempt().getValue().intValue());


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