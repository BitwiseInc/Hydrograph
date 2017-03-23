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
package hydrograph.engine.commandline.utilities;

import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.core.HydrographRuntimeService;
import hydrograph.engine.core.props.PropertiesLoader;
import hydrograph.engine.core.utilities.CommandLineOptionsProcessor;
import hydrograph.engine.core.xmlparser.HydrographXMLInputService;
import hydrograph.engine.core.xmlparser.XmlParsingUtils;
import hydrograph.engine.execution.tracking.ComponentInfo;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Locale;
/**
 * The Class HydrographExecution.
 *
 * @author Bitwise
 *
 */
public class HydrographExecution {

    private static Logger LOG = LoggerFactory.getLogger(HydrographExecution.class);
    private PropertiesLoader propertiesLoader;
    private HydrographRuntimeService runtimeService;
    private HydrographXMLInputService hydrographXmlInputService;
    private HydrographJob hydrographJob;

    public HydrographExecution() {
        this.propertiesLoader = PropertiesLoader.getInstance();
        this.hydrographXmlInputService = new HydrographXMLInputService();
        loadService();
    }

    public static void main(String args[]) throws Exception {
        if (GeneralCommandLineUtilities.IsArgOptionPresent(args, CommandLineOptionsProcessor.OPTION_HELP)) {
            GeneralCommandLineUtilities.printUsage();
        } else {
            HydrographExecution execution = new HydrographExecution();
            execution.run(args);
        }
    }

    public void run(String[] args) throws Exception {
        setLogLevelOnRootLogger(CommandLineOptionsProcessor.getLogLevel(args).toUpperCase(Locale.ENGLISH));
        hydrographJob = createHydrographJob(args);
        initialization(args, hydrographJob,
                XmlParsingUtils.getJobId(args), XmlParsingUtils.getUDFPath(args));
        prepareToExecute();
        finalExecute();
    }

    public List<ComponentInfo> getExecutionStatus() {
        return (List<ComponentInfo>) runtimeService.getExecutionStatus();
    }

    private HydrographJob createHydrographJob(String[] args) throws JAXBException {
        LOG.info("Invoking input service");
        return hydrographXmlInputService.parseHydrographJob(
                propertiesLoader.getInputServiceProperties(), args);
    }

    private void initialization(String[] args, HydrographJob bhsGraph,
                                String jobId, String udfPath) {
        LOG.info("Invoking initialize on runtime service");
        runtimeService.initialize(
                propertiesLoader.getRuntimeServiceProperties(), args, bhsGraph,
                jobId, udfPath);
    }

    private void setLogLevelOnRootLogger(String loggingLevel) {
        LogManager.getRootLogger().setLevel(Level.toLevel(loggingLevel));
    }

    private void prepareToExecute() {
        runtimeService.prepareToExecute();
        LOG.info("Preparation completed. Now starting execution");
    }

    private void finalExecute() {
        try {
            runtimeService.execute();
            LOG.info("Graph '" + hydrographJob.getJAXBObject().getName()
                    + "' executed successfully!");
        } finally {
            LOG.info("Invoking on complete for cleanup");
            runtimeService.oncomplete();
        }
    }

    private void loadService() {
        try {
            runtimeService = (HydrographRuntimeService) GeneralCommandLineUtilities
                    .loadAndInitClass(propertiesLoader.getRuntimeServiceClassName());
        } catch (IllegalAccessException | InstantiationException e) {
            LOG.error("Error in instantiating runtime service class.", e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            LOG.error("Error in loading runtime service class.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to kill the job
     */
    public void kill() {
        runtimeService.kill();
    }

}
