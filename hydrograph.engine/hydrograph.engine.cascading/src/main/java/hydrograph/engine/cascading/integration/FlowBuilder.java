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
package hydrograph.engine.cascading.integration;

import cascading.cascade.Cascade;
import cascading.cascade.CascadeConnector;
import cascading.flow.FlowConnector;
import cascading.flow.FlowDef;
import cascading.flow.process.ProcessFlow;
import cascading.flow.tez.Hadoop2TezFlowConnector;
import cascading.tuple.hadoop.BigDecimalSerialization;
import cascading.tuple.hadoop.TupleSerializationProps;
import hydrograph.engine.adapters.base.*;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.commandtype.component.BaseCommandComponent;
import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.helper.JAXBTraversal;
import hydrograph.engine.utilities.ComponentParameterBuilder;
import hydrograph.engine.utilities.PlatformHelper;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class FlowBuilder {

    private static final Logger LOG = LoggerFactory
            .getLogger(FlowBuilder.class);

    public FlowBuilder() {
    }

    public void buildFlow(RuntimeContext runtimeContext) {
        LOG.info("Building flow");
        JAXBTraversal traversal = runtimeContext.getTraversal();
        Properties hadoopProps = runtimeContext.getHadoopProperties();
        HydrographJob hydrographJob = runtimeContext.getHydrographJob();
        addSerializations(hadoopProps);

        FlowConnector flowConnector = PlatformHelper
                .getHydrographEngineFlowConnector(hadoopProps);

        Cascade[] cascades = new Cascade[traversal.getFlowsNumber().size()];
        int batchIndex = -1;
        String flowName = "flow";
        for (String batch : traversal.getFlowsNumber()) {
            batchIndex = batchIndex + 1;
            FlowContext flowContext = new FlowContext(hydrographJob, traversal,
                    hadoopProps);
            traverseAndConnect(runtimeContext, flowContext, batch);
            FlowDef flowDef = flowContext.getFlowDef();
            if (flowConnector instanceof Hadoop2TezFlowConnector) {
                flowDef = PlatformHelper
                        .addJarsToClassPathInCaseOfTezExecution(flowDef);
            }
            if (flowDef.getSources().size() > 0) {
                flowContext.getCascadeDef().addFlow(
                        flowConnector.connect(flowDef));
            }
            cascades[batchIndex] = (new CascadeConnector().connect(flowContext
                    .getCascadeDef().setName(flowName + "_" + batchIndex)));
            runtimeContext.setFlowContext(batch, flowContext);
        }
        runtimeContext.setCascade(cascades);

    }

    private void addSerializations(Properties hadoopProps) {
        TupleSerializationProps.addSerialization(hadoopProps,
                BigDecimalSerialization.class.getName());
    }

    private void traverseAndConnect(RuntimeContext runtimeContext,
                                    FlowContext flowContext, String batch) {

        JAXBTraversal traversal = flowContext.getTraversal();

        for (String componentId : traversal.getOrderedComponentsList(batch)) {
            LOG.info("Building parameters for " + componentId);

            ComponentParameters cp = null;
            BaseAdapter adapterBase = runtimeContext
                    .getAssemblyGeneratorMap().get(componentId);
            if (adapterBase instanceof InputAdapterBase) {
                cp = new ComponentParameterBuilder.Builder(componentId,
                        new ComponentParameters(), flowContext, runtimeContext)
                        .setFlowdef().setJobConf().setSchemaFields().build();
            } else if (adapterBase instanceof OutputAdapterBase) {
                cp = new ComponentParameterBuilder.Builder(componentId,
                        new ComponentParameters(), flowContext, runtimeContext)
                        .setFlowdef().setInputPipes().setInputFields().setSchemaFields().build();
            } else if (adapterBase instanceof StraightPullAdapterBase) {
                cp = new ComponentParameterBuilder.Builder(componentId,
                        new ComponentParameters(), flowContext, runtimeContext)
                        .setFlowdef().setJobConf().setInputPipes().setSchemaFields()
                        .setInputFields().build();
            } else if (adapterBase instanceof OperationAdapterBase) {
                cp = new ComponentParameterBuilder.Builder(componentId,
                        new ComponentParameters(), flowContext, runtimeContext)
                        .setFlowdef().setJobConf().setInputPipes().setUDFPath()
                        .setInputFields().setSchemaFields().build();
            } else if (adapterBase instanceof CommandAdapterBase) {
                BaseCommandComponent commandComponent = ((CommandAdapterBase) adapterBase)
                        .getComponent();
                flowContext.getCascadeDef().addFlow(
                        new ProcessFlow(componentId, commandComponent));
                continue;
            }

//			((ComponentGeneratorBase) assemblyGeneratorBase).createAssembly(cp);
//			BaseComponent component = ((ComponentGeneratorBase) assemblyGeneratorBase)
//					.getAssembly();

//			flowContext.getAssemblies().put(componentId, component);

            ((ComponentAdapterBase) adapterBase).createAssembly(cp);
            BaseComponent component = ((ComponentAdapterBase) adapterBase).getAssembly();
            flowContext.getAssemblies().put(componentId, component);

            LOG.info("Assembly creation completed for " + componentId);
        }
    }


    public void cleanup(List<String> tmpPathList, RuntimeContext runtimeContext) {
        if (tmpPathList != null) {
            for (String tmpPath : tmpPathList) {

                Path fullPath = new Path(tmpPath);
                // do not delete the root directory
                if (fullPath.depth() == 0)
                    continue;
                FileSystem fileSystem;

                LOG.info("Deleting temp path:" + tmpPath);
                try {
                    fileSystem = FileSystem.get(runtimeContext.getJobConf());

                    fileSystem.delete(fullPath, true);
                } catch (NullPointerException exception) {
                    // hack to get around npe thrown when fs reaches root directory
                    // if (!(fileSystem instanceof NativeS3FileSystem))
                    throw new RuntimeException(exception);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

}
