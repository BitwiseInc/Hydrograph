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
package hydrograph.engine.core.xmlparser;

import hydrograph.engine.core.core.HydrographInputService;
import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.props.PropertiesLoader;
import hydrograph.engine.core.utilities.XmlUtilities;
import hydrograph.engine.core.xmlparser.externalschema.ParseExternalElements;
import hydrograph.engine.core.xmlparser.parametersubstitution.ParameterSubstitutor;
import hydrograph.engine.core.xmlparser.parametersubstitution.UserParameters;
import hydrograph.engine.core.xmlparser.subjob.ReadSubjob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
/**
 * The Class HydrographXMLInputService.
 *
 * @author Bitwise
 */
public class HydrographXMLInputService implements HydrographInputService {

	HydrographJobGenerator hydrographJobGenerator;
	Properties config;
	private static Logger LOG = LoggerFactory
			.getLogger(HydrographXMLInputService.class);

	public HydrographXMLInputService() {
		hydrographJobGenerator = new HydrographJobGenerator();
	}

	public HydrographJob parseParameters(String[] args) throws JAXBException {
		return parseHydrographJob(PropertiesLoader.getInstance()
				.getRuntimeServiceProperties(), args);
	}

	@Override
	public HydrographJob parseHydrographJob(Properties config, String[] args)
			throws JAXBException {
		HydrographJob hydrographJob = null;
		this.config = config;
		String path = XmlParsingUtils.getXMLPath(args, config);
		LOG.info("Parsing for graph file: " + path + " started");
		ParameterSubstitutor parameterSubstitutor = new ParameterSubstitutor(
				getUserParameters(args));

		try {
			ParseExternalElements parseExternalElements = new ParseExternalElements(
					checkSubjobAndExpandXml(parameterSubstitutor,
							XmlParsingUtils.getXMLStringFromPath(path)),
					parameterSubstitutor);
			hydrographJob = hydrographJobGenerator.createHydrographJob(
					parseExternalElements.getXmlDom(),
					config.getProperty("xsdLocation"));

		} catch (FileNotFoundException e) {
			LOG.error("Error while merging subjob and mainjob.", e);
			throw new RuntimeException(
					"Error while merging subjob and mainjob.", e);
		} catch (SAXException e) {
			LOG.error("Error while parsing XSD.", e);
			throw new RuntimeException("Error while parsing XSD.", e);
		}
		LOG.info("Graph: '" + hydrographJob.getJAXBObject().getName()
				+ "' parsed successfully");
		return hydrographJob;
	}

	private String checkSubjobAndExpandXml(
			ParameterSubstitutor parameterSubstitutor, String xmlContents)
			throws FileNotFoundException {

		LOG.info("Expanding subjobs");
		ReadSubjob subjobParser = new ReadSubjob(
				parameterSubstitutor.substitute(xmlContents));

		Document expandedXmlDocument = subjobParser.getSubjobDom();
		return XmlUtilities.getXMLStringFromDocument(expandedXmlDocument);
	}

	private UserParameters getUserParameters(String[] args) {
		try {
			return new UserParameters(args);
		} catch (IOException e) {
			throw new HydrographXMLInputServiceException(e);
		}
	}

	public class HydrographXMLInputServiceException extends RuntimeException {
		private static final long serialVersionUID = -7709930763943833311L;

		public HydrographXMLInputServiceException(Throwable e) {
			super(e);
		}
	}

}