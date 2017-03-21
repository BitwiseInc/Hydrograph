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
package hydrograph.engine.plugin.debug;

import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.core.flowmanipulation.FlowManipulationContext;
import hydrograph.engine.core.flowmanipulation.ManipulatorListener;
import hydrograph.engine.core.utilities.PropertiesHelper;
import hydrograph.engine.core.utilities.XmlUtilities;
import hydrograph.engine.core.xmlparser.XmlParsingUtils;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.plugin.debug.entity.DebugPoint;
import hydrograph.engine.plugin.debug.entity.HydrographDebugInfo;
import hydrograph.engine.plugin.debug.utils.DebugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
/**
 * The Class DebugPlugin.
 *
 * @author Bitwise
 *
 */
public class DebugPlugin implements ManipulatorListener {

    public static Logger LOG = LoggerFactory.getLogger(DebugPlugin.class);
    private static final String PLUGIN_PROPERTIES = "plugin.properties";
    private static final String DEBUG_XSD_LOCATION = "debugXSDLocation";

    @Override
    public List<TypeBaseComponent> execute(
            FlowManipulationContext manipulationContext) {
        List<TypeBaseComponent> typeBaseComponents = manipulationContext
                .getJaxbMainGraph();

        String[] args = manipulationContext.getArgs();

        String debugXMLPath = DebugUtils.getDebugXMLPath(args);
        if (debugXMLPath != null) {
            if (XmlParsingUtils.getJobId(args) == null)
                throw new IllegalArgumentException(
                        "job id argument is required for view data feature.");
            if (DebugUtils.getBasePath(args) == null)
                throw new IllegalArgumentException(
                        "base path argument is required for view data feature.");
            HydrographDebugInfo hydrographDebugInfo;

            Properties properties;
            try {
                properties = PropertiesHelper.getProperties(PLUGIN_PROPERTIES);
            } catch (IOException e) {
                LOG.error("Error reading properties file: '" + PLUGIN_PROPERTIES
                        + "'");
                throw new RuntimeException(e);
            }
            Document debugXmlDoc;
            try {
                LOG.info("Parsing for Debug graph file: " + debugXMLPath + " started");

            /*Debug xml will not require parameter substitution
            ParameterSubstitutor parameterSubstitutor = new ParameterSubstitutor(
                getUserParameters(args));*/

                debugXmlDoc = XmlUtilities.getXMLDocument(XmlParsingUtils.getXMLStringFromPath(debugXMLPath));

                LOG.info("Debug graph parsed successfully");
            } catch (Exception e) {
                LOG.error("Error in parsing debug xml", e);
                throw new RuntimeException(e);
            }
            try {
                hydrographDebugInfo = DebugUtils.createHydrographDebugInfo(debugXmlDoc,
                        properties.getProperty(DEBUG_XSD_LOCATION));
            } catch (SAXException e) {
                LOG.error("Error while parsing debug XSD.", e);
                throw new RuntimeException("Error while parsing debug XSD.", e);
            }
            List<DebugPoint> debugGraphList = DebugUtils.extractDebugPoints(HydrographDebugInfo.DebugChecker
                    .getViewData(hydrographDebugInfo));
            for (DebugPoint debug : debugGraphList) {
                typeBaseComponents = createComponent(debug, typeBaseComponents,
                        manipulationContext);
            }
        }
        return typeBaseComponents;
    }

    private List<TypeBaseComponent> createComponent(DebugPoint debug,
                                                    List<TypeBaseComponent> mainGraphList,
                                                    FlowManipulationContext manipulationContext) {

        for (TypeBaseComponent baseComponent : mainGraphList) {
            if (debug.getFromComponentId().equalsIgnoreCase(
                    baseComponent.getId())) {
                TypeBaseComponent clone = generateReplicateComponent(
                        baseComponent, mainGraphList, debug);
//				TypeBaseComponent limit = generateLimitComponent(baseComponent,
//						mainGraphList, debug.getLimit(), clone);
                generateOutputTextComponent(baseComponent, mainGraphList,
                        debug, manipulationContext.getSchemaFieldMap(), clone,
                        manipulationContext.getJobId(),
                        DebugUtils.getBasePath(manipulationContext.getArgs()));
                return mainGraphList;
            }
        }

        throw new RuntimeException("debug fromComponent_id not matched : " + debug.getFromComponentId());
    }

    private TypeBaseComponent generateOutputTextComponent(
            TypeBaseComponent baseComponent,
            List<TypeBaseComponent> componentList, DebugPoint debug,
            Map<String, Set<SchemaField>> schemaFieldsMap,
            TypeBaseComponent component, String jobId, String basePath) {

        DebugContext debugContext = new DebugContext();
        debugContext.setBasePath(basePath);
        debugContext.setJobId(jobId);
        debugContext.setPreviousComponentId(component.getId());
        debugContext.setFromComponentId(debug.getFromComponentId());
        debugContext.setFromOutSocketId(debug.getOutSocketId());
        debugContext.setBatch(baseComponent.getBatch());
        debugContext.setSchemaFieldsMap(schemaFieldsMap);
        debugContext.setTypeBaseComponents(componentList);
        return ComponentBuilder.TEXT_OUTPUT_COMPONENT.create(debugContext);
    }

    private TypeBaseComponent generateOutputAvroComponent(
            TypeBaseComponent baseComponent,
            List<TypeBaseComponent> componentList, DebugPoint debug,
            Map<String, Set<SchemaField>> schemaFieldsMap,
            TypeBaseComponent component, String jobId, String basePath) {

        DebugContext debugContext = new DebugContext();
        debugContext.setBasePath(basePath);
        debugContext.setJobId(jobId);
        debugContext.setPreviousComponentId(component.getId());
        debugContext.setFromComponentId(debug.getFromComponentId());
        debugContext.setFromOutSocketId(debug.getOutSocketId());
        debugContext.setBatch(baseComponent.getBatch());
        debugContext.setSchemaFieldsMap(schemaFieldsMap);
        debugContext.setTypeBaseComponents(componentList);
        return ComponentBuilder.AVRO_OUTPUT_COMPONENT.create(debugContext);
    }

    private TypeBaseComponent generateReplicateComponent(
            TypeBaseComponent baseComponent,
            List<TypeBaseComponent> componentList, DebugPoint debug) {

        DebugContext debugContext = new DebugContext();
        debugContext.setFromComponentId(debug.getFromComponentId());
        debugContext.setFromOutSocketId(debug.getOutSocketId());
        debugContext.setBatch(baseComponent.getBatch());
        debugContext.setTypeBaseComponents(componentList);
        return ComponentBuilder.REPLICATE_COMPONENT.create(debugContext);
    }

}
