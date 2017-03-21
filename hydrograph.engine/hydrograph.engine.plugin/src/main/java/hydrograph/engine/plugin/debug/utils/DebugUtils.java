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
package hydrograph.engine.plugin.debug.utils;

import hydrograph.engine.core.utilities.GeneralUtilities;
import hydrograph.engine.core.xmlparser.ComponentValidationEventHandler;
import hydrograph.engine.jaxb.debug.Debug;
import hydrograph.engine.jaxb.debug.ViewData;
import hydrograph.engine.plugin.debug.entity.DebugPoint;
import hydrograph.engine.plugin.debug.entity.HydrographDebugInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.util.ArrayList;
import java.util.List;
/**
 * The Class DebugUtils.
 *
 * @author Bitwise
 *
 */
public class DebugUtils {

    public static final String OPTION_BASE_PATH = "basepath";
    public static Logger LOG = LoggerFactory.getLogger(DebugUtils.class);
    private static final String OPTION_DEBUG_XML_PATH = "debugxmlpath";

    /**
     * Constructor marked private to disable instantiation
     */
    private DebugUtils(){

    }

    /**
     * @param listOfViewData
     * @return listOfDebugPoints
     */
    public static List<DebugPoint> extractDebugPoints(
            List<ViewData> listOfViewData) {
        ArrayList<DebugPoint> listOfDebugPoints = new ArrayList<DebugPoint>();
        DebugPoint debugPoint;
        for (ViewData viewData : listOfViewData) {
            debugPoint = new DebugPoint();
            debugPoint.setFromComponentId(viewData.getFromComponentId());
            debugPoint.setOutSocketId(viewData.getOutSocketId());
            listOfDebugPoints.add(debugPoint);
        }
        return listOfDebugPoints;
    }

    /**
     * Fetches the basepath argument's value passed as command line argument
     *
     * @param args the command line arguments
     * @return the basepath argument's value
     */
    public static String getBasePath(String[] args) {
        String[] basePath = null;
        basePath = GeneralUtilities.getArgsOption(args, OPTION_BASE_PATH);

        if (basePath != null) {
            // only the first path
            return basePath[0];
        } else {
            return null;
        }
    }

    /**
     * Fetches the debugxmlpath argument's value passed as command line argument
     *
     * @param args the command line arguments
     * @return the debugxmlpath argument's value
     */
    public static String getDebugXMLPath(String[] args) {
        String[] paths;
        paths = GeneralUtilities.getArgsOption(args, OPTION_DEBUG_XML_PATH);
        if (paths != null) {
            // only the first path
            return paths[0];
        } else {
            return null;
            // if path is not found from command line then check from config
            //return config.getProperty(OPTION_DEBUG_XML_PATH);
        }
    }

    /**
     * Creates the object of type {@link HydrographDebugInfo} from the graph xml of type
     * {@link Document}.
     * <p>
     * The method uses jaxb framework to unmarshall the xml document
     *
     * @param graphDocument the xml document with all the graph contents to unmarshall
     * @return an object of type {@link HydrographDebugInfo}
     * @throws SAXException
     */
    public static HydrographDebugInfo createHydrographDebugInfo(Document graphDocument, String debugXSDLocation) throws SAXException {
        try {
            LOG.trace("Creating DebugJAXB object.");
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(ClassLoader.getSystemResource(debugXSDLocation));
            JAXBContext context = JAXBContext.newInstance(Debug.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(new ComponentValidationEventHandler());
            Debug debug = (Debug) unmarshaller.unmarshal(graphDocument);
            HydrographDebugInfo hydrographDebugInfo = new HydrographDebugInfo(debug);
            LOG.trace("DebugJAXB object created successfully");
            return hydrographDebugInfo;
        } catch (JAXBException e) {
            LOG.error("Error while creating JAXB objects from debug XML.", e);
            throw new RuntimeException("Error while creating JAXB objects from debug XML.", e);
        }
    }
}