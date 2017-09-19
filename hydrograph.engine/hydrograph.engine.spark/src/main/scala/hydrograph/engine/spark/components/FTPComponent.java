
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

package hydrograph.engine.spark.components;

/**
 * Created for FTPComponent on 8/3/2017.
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
