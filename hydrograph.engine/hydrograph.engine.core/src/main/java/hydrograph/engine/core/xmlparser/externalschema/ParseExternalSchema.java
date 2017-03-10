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
package hydrograph.engine.core.xmlparser.externalschema;

import hydrograph.engine.core.xmlparser.XmlParsingUtils;
import hydrograph.engine.core.xmlparser.parametersubstitution.ParameterSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ParseExternalSchema {

	private static Logger LOG = LoggerFactory
			.getLogger(ParseExternalSchema.class);

	private final String URI = "uri";
	private final String EXTERNAL_SCHEMA = "includeExternalSchema";
	private final String FIELD = "field";

	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory
			.newInstance();
	private DocumentBuilder documentBuilder;
	private Document xmlDocument = null;
	private String xmlContent;

	private ParameterSubstitutor parameterSubstitutor = null;

	public ParseExternalSchema(String xmlContent, ParameterSubstitutor parameterSubstitutor) {
		this.parameterSubstitutor = parameterSubstitutor;
		this.xmlContent = xmlContent;
	}

	public Document getXmlDom() throws FileNotFoundException {
		return getExternalSchemaDocument(xmlContent);
	}

	private Document getExternalSchemaDocument(String xmlContents) throws FileNotFoundException {
		LOG.info("Parsing external schemas");
		builderFactory.setValidating(false);
		builderFactory.setNamespaceAware(true);
		try {
			documentBuilder = builderFactory.newDocumentBuilder();
			try {
				xmlDocument = documentBuilder.parse(new InputSource(
						new StringReader(xmlContents)));
			} catch (SAXException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		NodeList externalNodes = xmlDocument
				.getElementsByTagName(EXTERNAL_SCHEMA);

		int externalNodesLen = externalNodes.getLength();
		for (int k = 0; k < externalNodesLen; k++) {
			Node parent = externalNodes.item(0).getParentNode();

			List<Node> nodes = createNodes(parent);
			int len = parent.getChildNodes().getLength();

			removeChileNodes(parent, len);

			addChildNodes(xmlDocument, parent, nodes);
		}
		return xmlDocument;

	}

	private void addChildNodes(Document xmlDocument, Node parent,
			List<Node> nodes) {
		Node copiedNode;
		for (int i = 0; i < nodes.size(); i++) {
			copiedNode = xmlDocument.importNode(nodes.get(i), true);
			parent.appendChild(copiedNode);
		}
	}

	private void removeChileNodes(Node parent, int len) {
		for (int i = 0; i < len; i++) {
			parent.removeChild(parent.getChildNodes().item(0));
		}
	}

	private List<Node> createNodes(Node parent) throws FileNotFoundException {
		List<Node> nodeList = new ArrayList<Node>();
		Document xmlDocument2 = null;
		try {
			documentBuilder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			if (parent.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (parent.getChildNodes().item(i).getNodeName().equals(FIELD))
					nodeList.add(parent.getChildNodes().item(i));
				else if(parent.getChildNodes().item(i).getNodeName().equals(EXTERNAL_SCHEMA)){
					String path = parent.getChildNodes().item(i)
							.getAttributes().getNamedItem(URI).getNodeValue();
					if (!new File(path).exists()) {
							throw new FileNotFoundException(
									"External schema file doesn't exist: "
											+ path);
					}
					try {
						String xml = parameterSubstitutor.substitute(XmlParsingUtils.getXMLStringFromPath(path));
						xmlDocument2 = documentBuilder.parse(new InputSource(
								new StringReader(xml)));
					} catch (SAXException e) {
						throw new RuntimeException(e);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					nodeList.addAll(createNodes(xmlDocument2.getElementsByTagName("fields").item(0)));
				}
			}
		}
		return nodeList;
	}
}
