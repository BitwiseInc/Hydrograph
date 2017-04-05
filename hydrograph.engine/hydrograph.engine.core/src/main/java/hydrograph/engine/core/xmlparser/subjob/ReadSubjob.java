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
package hydrograph.engine.core.xmlparser.subjob;

import hydrograph.engine.core.utilities.XmlUtilities;
import hydrograph.engine.core.xmlparser.XmlParsingUtils;
import hydrograph.engine.core.xmlparser.parametersubstitution.ParameterSubstitutor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
/**
 * The Class ReadSubjob.
 *
 * @author Bitwise
 */
public class ReadSubjob {

	private static final String ID = "id";
	private static final String PATH = "path";
	private static final String URI = "uri";
	private static final String INPUTS = "inputs";
	private static final String OUTPUTS = "outputs";
	private static final String OPERATIONS = "operations";
	private static final String SUBJOB = "subjob";
	private static final String EMPTY = "";
	private static final String TYPE = "xsi:type";
	private static final Object SUBJOBPARAMETER = "subjobParameter";
	private static final String COMMANDS = "commands";
	private static final String BATCH = "batch";

	private Document xmlDocument = null;
	private String xmlContent;
	private ParameterSubstitutor parameterSubstitutor;
	private String subjobPath;

	public ReadSubjob(String xmlContent) {
		this.xmlContent = xmlContent;
	}

	public Document getSubjobDom() throws FileNotFoundException {
		return getSubjobDocument(xmlContent);
	}

	private Document getSubjobDocument(String xmlContents)
			throws FileNotFoundException {
		xmlDocument = XmlUtilities.getXMLDocument(removeComments(xmlContents));
		Document subjobDocument = getSubjob(xmlDocument);
		return subjobDocument;
	}

	private String removeComments(String xmlContents) {
		return xmlContents.replaceAll("<!--[\\s\\S]*?-->", "");
	}

	private Document getSubjob(Document xmlDocument)
			throws FileNotFoundException {
		Node parentNode = xmlDocument.getFirstChild();
		String subjobComponentId, subjobComponentBatch;
		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
			if (isSubjobPresent(parentNode.getChildNodes().item(i))) {
				Node subjobNode = parentNode.getChildNodes().item(i);
				subjobComponentId = subjobNode.getAttributes().getNamedItem(ID)
						.getNodeValue();

				subjobComponentBatch = subjobNode.getAttributes().getNamedItem(
						BATCH) == null ? "0" : subjobNode.getAttributes()
						.getNamedItem(BATCH).getNodeValue();

				String subjobXml = EMPTY;
				subjobXml = readSubjob(subjobNode);
				if (!subjobXml.isEmpty()) {
					Document subjobXmlDocument = XmlUtilities
							.getXMLDocument(removeComments(subjobXml));
					subjobXmlDocument = getSubjob(subjobXmlDocument);
					xmlDocument = parseSubjob(xmlDocument, subjobXmlDocument,
							subjobComponentId, subjobComponentBatch);
				} else {
					throw new RuntimeException(
							"Subjob XML contents empty. Subjob ID: '"
									+ subjobComponentId + "'. Subjob path : " + subjobPath );
				}
			}
		}
		return xmlDocument;
	}

	private Document parseSubjob(Document parentXmlDocument,
			Document subjobXmlDocument, String subjobComponentId,
			String subjobComponentBatch) {
		ParseSubjob subjobParser = new ParseSubjob(parentXmlDocument,
				subjobXmlDocument, subjobComponentId, subjobComponentBatch);
		return subjobParser.expandSubjob();

	}

	private boolean isSubjobPresent(Node parentNode) {
		return parentNode.getNodeName().matches(
				INPUTS + "|" + OPERATIONS + "|" + OUTPUTS + "|" + COMMANDS)
				&& parentNode.getAttributes().getNamedItem(TYPE).getNodeValue()
						.split(":")[1].equals(SUBJOB);
	}

	private String readSubjob(Node subjobNode) throws FileNotFoundException {
		for (int j = 0; j < subjobNode.getChildNodes().getLength(); j++) {
			if (subjobNode.getChildNodes().item(j).getNodeName().equals(PATH)) {
				 subjobPath = subjobNode.getChildNodes().item(j)
						.getAttributes().getNamedItem(URI).getNodeValue();
				if (isSubjobExists(subjobPath)) {
					parameterSubstitutor = new ParameterSubstitutor(
							getSubjobParameter(subjobNode));
					String subjobXml = parameterSubstitutor
							.substitute(XmlParsingUtils
									.getXMLStringFromPath(subjobPath));
					return subjobXml;
				} else {
					throw new FileNotFoundException(
							"Subjob file doesn't exist: " + subjobPath);
				}
			}
		}
		return EMPTY;
	}

	private boolean isSubjobExists(String subjobPath) {
		if (new File(subjobPath).exists()) {
			return true;
		} else {
			return false;
		}
	}

	private SubjobParameters getSubjobParameter(Node node) {
		HashMap<String, String> parameter = new HashMap<String, String>();
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			if (node.getChildNodes().item(i).getNodeName()
					.equals(SUBJOBPARAMETER)) {
				parameter = fillSubjobParameter(node.getChildNodes().item(i));

			}
		}
		return new SubjobParameters(parameter);
	}

	private HashMap<String, String> fillSubjobParameter(Node parameterNode) {
		HashMap<String, String> parameter = new HashMap<String, String>();
		int parameterNodeLen = parameterNode.getChildNodes().getLength();
		for (int j = 0; j < parameterNodeLen; j++) {
			if (parameterNode.getChildNodes().item(j).getNodeName()
					.equals("property")) {
				String name = parameterNode.getChildNodes().item(j)
						.getAttributes().getNamedItem("name").getNodeValue();
				String value = parameterNode.getChildNodes().item(j)
						.getAttributes().getNamedItem("value").getNodeValue();

				parameter.put(name, value);
			}
		}
		return parameter;
	}
}
