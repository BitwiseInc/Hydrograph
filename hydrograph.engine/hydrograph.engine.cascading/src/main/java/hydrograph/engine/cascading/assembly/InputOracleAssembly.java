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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.cascading.assembly;

import cascading.jdbc.db.OracleDBInputFormat;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.InputRDBMSEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputOracleAssembly extends InputRDBMSAssembly {


    private static final long serialVersionUID = 1L;
    private static Logger LOG = LoggerFactory
            .getLogger(InputOracleAssembly.class);

    public InputOracleAssembly(InputRDBMSEntity baseComponentEntity,
                               ComponentParameters componentParameters) {
        super(baseComponentEntity, componentParameters);
    }

    @Override
    public void intializeRdbmsSpecificDrivers() {
        inputFormatClass = OracleDBInputFormat.class;
        if (inputRDBMSEntity.getDriverType().equalsIgnoreCase("thin")) {
            driverName = "oracle.jdbc.OracleDriver";
        }

        jdbcURL = "jdbc:oracle:" + inputRDBMSEntity.getDriverType() + "://@" + inputRDBMSEntity.getHostName() + ":"
                + inputRDBMSEntity.getPort() + ":" + inputRDBMSEntity.getSid();
    }

}