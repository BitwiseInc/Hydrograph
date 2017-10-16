/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package hydrograph.engine.core.component.generator;

import hydrograph.engine.core.component.entity.RunFileTransferEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.generator.base.CommandComponentGeneratorBase;
import hydrograph.engine.jaxb.commandtypes.S3FileTransfer;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

/**
 * Created for S3FileTransferEntityGenerator on 9/7/2017.
 */
public class S3FileTransferEntityGenerator extends CommandComponentGeneratorBase {
    private S3FileTransfer s3FileTransfer;
    private RunFileTransferEntity runFileTransferEntity;
    public S3FileTransferEntityGenerator (TypeBaseComponent typeCommandComponent) {
        super(typeCommandComponent);
    }
    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        s3FileTransfer=(S3FileTransfer)baseComponent;

    }

    @Override
    public void createEntity() {
        runFileTransferEntity=new RunFileTransferEntity();
    }

    @Override
    public void initializeEntity() {
        if(s3FileTransfer.getFileOperation().getDownload()!=null)
            runFileTransferEntity.setFileOperation("download");
        else
            runFileTransferEntity.setFileOperation("upload");
        if(s3FileTransfer.getCrediationalPropertiesFile()!=null){
            runFileTransferEntity.setCrediationalPropertiesFile(s3FileTransfer.getCrediationalPropertiesFile());
        }
        else{
            runFileTransferEntity.setAccessKeyID(s3FileTransfer.getAccessKeyID());
            runFileTransferEntity.setSecretAccessKey(s3FileTransfer.getSecretAccessKey());
        }
        runFileTransferEntity.setLocalPath(s3FileTransfer.getLocalPath());
        runFileTransferEntity.setBucketName(s3FileTransfer.getBucketName());
        if(s3FileTransfer.getFolderNameInBucket()!=null)
        runFileTransferEntity.setFolder_name_in_bucket(s3FileTransfer.getFolderNameInBucket());
        runFileTransferEntity.setRegion(s3FileTransfer.getRegion());
        if(s3FileTransfer.getKeyName()!=null)
        runFileTransferEntity.setKeyName(s3FileTransfer.getKeyName());
        if(s3FileTransfer.getTimeOut()!=null)
            runFileTransferEntity.setTimeOut(s3FileTransfer.getTimeOut().getValue().intValue());

        if (s3FileTransfer.getRetryAfterDuration()!=null)
            runFileTransferEntity.setRetryAfterDuration(s3FileTransfer.getRetryAfterDuration().getValue().intValue());
        if (s3FileTransfer.getRetryAttempt()!=null)
            runFileTransferEntity.setRetryAttempt(s3FileTransfer.getRetryAttempt().getValue().intValue());
        runFileTransferEntity.setEncoding( s3FileTransfer.getEncoding() != null ? s3FileTransfer.getEncoding().getValue().value() : "UTF-8");
        if(s3FileTransfer.isFailOnError()!=null){
            runFileTransferEntity.setFailOnError(s3FileTransfer.isFailOnError());
        }
        if(s3FileTransfer.getOverwritemode()!=null){
            runFileTransferEntity.setOverwrite(s3FileTransfer.getOverwritemode());
        }

    }

    @Override
    public AssemblyEntityBase getEntity() {
        return runFileTransferEntity;
    }

}
