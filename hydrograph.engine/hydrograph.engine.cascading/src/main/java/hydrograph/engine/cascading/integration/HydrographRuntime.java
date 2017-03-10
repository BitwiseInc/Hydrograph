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
import cascading.flow.Flow;
import cascading.property.AppProps;
import hydrograph.engine.component.mapping.ComponentAdapterFactory;
import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.core.HydrographRuntimeService;
import hydrograph.engine.core.flowmanipulation.FlowManipulationContext;
import hydrograph.engine.core.flowmanipulation.FlowManipulationHandler;
import hydrograph.engine.core.helper.JAXBTraversal;
import hydrograph.engine.core.props.PropertiesLoader;
import hydrograph.engine.core.schemapropagation.SchemaFieldHandler;
import hydrograph.engine.core.utilities.CommandLineOptionsProcessor;
import hydrograph.engine.core.utilities.GeneralUtilities;
import hydrograph.engine.flow.utils.ExecutionTrackingListener;
import hydrograph.engine.hadoop.utils.HadoopConfigProvider;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.utilities.ExecutionTrackingUtilities;
import hydrograph.engine.utilities.HiveMetastoreTokenProvider;
import hydrograph.engine.utilities.UserClassLoader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings({"rawtypes"})
public class HydrographRuntime implements HydrographRuntimeService {

    final String EXECUTION_TRACKING = "hydrograph.execution.tracking";
    private static final String OPTION_DOT_PATH = "dotpath";
    private Properties hadoopProperties = new Properties();
    private ExecutionTrackingListener executionTrackingListener;
    private FlowBuilder flowBuilder;
    private RuntimeContext runtimeContext;
    private String[] args;
    private PropertiesLoader config;
    private FlowManipulationContext flowManipulationContext;
    private static Logger LOG = LoggerFactory.getLogger(HydrographRuntime.class);

    public void executeProcess(String[] args, HydrographJob hydrographJob) {
        this.args = args != null ? args.clone() : null;
        config = PropertiesLoader.getInstance();
        LOG.info("Invoking initialize on runtime service");
        initialize(config.getRuntimeServiceProperties(), this.args, hydrographJob, null,
                null);
        LOG.info("Preparation started");
        prepareToExecute();
        LOG.info("Preparation completed. Now starting execution");
        LOG.info("Execution Started");
        execute();
        LOG.info("Execution Complete");
        oncomplete();
    }

    public void initialize(Properties config, String[] args, HydrographJob hydrographJob, String jobId, String UDFPath) {

        AppProps.setApplicationName(hadoopProperties, hydrographJob.getJAXBObject().getName());

        hadoopProperties.putAll(config);

        Configuration conf = new HadoopConfigProvider(hadoopProperties).getJobConf();

        SchemaFieldHandler schemaFieldHandler = new SchemaFieldHandler(
                hydrographJob.getJAXBObject().getInputsOrOutputsOrStraightPulls());

        flowManipulationContext = new FlowManipulationContext(hydrographJob, args, schemaFieldHandler,
                jobId);

        FlowManipulationHandler flowManipulationHandler = new FlowManipulationHandler();

        hydrographJob = flowManipulationHandler.execute(flowManipulationContext);

        if (hydrographJob.getJAXBObject().getRuntimeProperties() != null
                && hydrographJob.getJAXBObject().getRuntimeProperties().getProperty() != null) {
            for (Property property : hydrographJob.getJAXBObject().getRuntimeProperties().getProperty()) {
                hadoopProperties.put(property.getName(), property.getValue());
            }
        }

        JAXBTraversal traversal = new JAXBTraversal(hydrographJob.getJAXBObject());

        if (traversal.isHiveComponentPresentInFlow()) {
            try {
                HiveMetastoreTokenProvider.obtainTokenForHiveMetastore(conf);
            } catch (TException e) {
                throw new HydrographRuntimeException(e);
            } catch (IOException e) {
                throw new HydrographRuntimeException(e);
            }
        }

        String[] otherArgs;
        try {
            otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        } catch (IOException e) {
            throw new HydrographRuntimeException(e);
        }

        String argsString = "";
        for (String arg : otherArgs) {
            argsString = argsString + " " + arg;
        }
        LOG.info("After processing arguments are:" + argsString);
        this.args = otherArgs;
        // setJar(otherArgs);

        hadoopProperties.putAll(conf.getValByRegex(".*"));

        ComponentAdapterFactory componentAdapterFactory = new ComponentAdapterFactory(hydrographJob.getJAXBObject());

        flowBuilder = new FlowBuilder();

        runtimeContext = new RuntimeContext(hydrographJob, traversal, hadoopProperties, componentAdapterFactory,
                flowManipulationContext.getSchemaFieldHandler(), UDFPath);

        LOG.info(
                "Graph '" + runtimeContext.getHydrographJob().getJAXBObject().getName() + "' initialized successfully");
    }

    @Override
    public void prepareToExecute() {
        flowBuilder.buildFlow(runtimeContext);

        if (GeneralUtilities.IsArgOptionPresent(args, OPTION_DOT_PATH)) {
            writeDotFiles();
        }

    }

    @Override
    public void execute() {
        if (GeneralUtilities.IsArgOptionPresent(args, CommandLineOptionsProcessor.OPTION_NO_EXECUTION)) {
            LOG.info(CommandLineOptionsProcessor.OPTION_NO_EXECUTION + " option is provided so skipping execution");
            return;
        }
        if (ExecutionTrackingUtilities.getExecutionTrackingClass(EXECUTION_TRACKING) != null) {
            executionTrackingListener = (ExecutionTrackingListener) UserClassLoader.loadAndInitClass(
                    ExecutionTrackingUtilities.getExecutionTrackingClass(EXECUTION_TRACKING), "execution tracking");
            executionTrackingListener.addListener(runtimeContext);
        }
        for (Cascade cascade : runtimeContext.getCascade()) {
            cascade.complete();
        }
    }

    @Override
    public void oncomplete() {
        flowBuilder.cleanup(flowManipulationContext.getTmpPath(), runtimeContext);
    }

    /**
     * Returns the statistics of components in a job.
     *
     * @see hydrograph.engine.execution.tracking.ComponentInfo
     */
    @Override
    public Object getExecutionStatus() {
        if (executionTrackingListener != null)
            return executionTrackingListener.getStatus();
        return null;
    }

    public Cascade[] getFlow() {
        return runtimeContext.getCascadingFlows();
    }

    private void writeDotFiles() {

        String[] paths = GeneralUtilities.getArgsOption(args, OPTION_DOT_PATH);

        if (paths == null) {
            throw new HydrographRuntimeException(
                    OPTION_DOT_PATH + " option is provided but is not followed by path");
        }
        String basePath = paths[0];
        LOG.info("Dot files will be written under " + basePath);

        String flowDotPath = basePath + "/" + runtimeContext.getHydrographJob().getJAXBObject().getName() + "/"
                + "flow";
        String flowStepDotPath = basePath + "/" + runtimeContext.getHydrographJob().getJAXBObject().getName() + "/"
                + "flowstep";

        int batchCounter = 0;
        for (Cascade cascadingFlow : runtimeContext.getCascadingFlows()) {
            for (Flow flows : cascadingFlow.getFlows()) {
                flows.writeDOT(flowDotPath + "_" + batchCounter);
                flows.writeStepsDOT(flowStepDotPath + "_" + batchCounter);
                batchCounter++;
            }
        }
    }


    public class HydrographRuntimeException extends RuntimeException {

        /**
         *
         */
        private static final long serialVersionUID = -7891832980227676974L;

        public HydrographRuntimeException(String msg) {
            super(msg);
        }

        public HydrographRuntimeException(Throwable e) {
            super(e);
        }
    }

    /**
     * Method to kill the job
     */
    @Override
    public void kill() {
        LOG.info("Kill signal received");
        if (runtimeContext.getCascade() != null) {
            for (Cascade cascade : runtimeContext.getCascade()) {
                LOG.info("Killing Cascading jobs: " + cascade.getID());
                cascade.stop();
            }
        } else
            LOG.info("No cascading jobs present to kill. Exiting code.");
        System.exit(0);
    }
}