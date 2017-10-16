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

/**
 * The Class ParseExternalElements.
 *
 * @author Bitwise
 */
public class ParseExternalElements {

	private static Logger LOG = LoggerFactory
			.getLogger(ParseExternalElements.class);

	private final String URI = "uri";
	private final String INCLUDE_EXTERNAL_SCHEMA = "includeExternalSchema";
	private final String FIELDS = "fields";
	private final String FIELD = "field";
	private final String RECORD = "record";
	private final String INCLUDE_EXTERNAL_OPERATION = "includeExternalOperation";
	private final String EXTERNAL_OPERATION = "externalOperations";
	private final String OPERATION = "operation";
	private final String OPERATIONFIELD = "operationField";
	private final String INCLUDE_EXTERNAL_EXPRESSION = "includeExternalExpression";
	private final String EXTERNAL_EXPRESSION = "externalExpressions";
	private final String EXPRESSION = "expression";
	private final String EXPRESSIONFIELD = "expressionField";
	private final String INCLUDE_EXTERNAL_MAPPING = "includeExternalMapping";
	private final String EXTERNAL_MAPPING = "externalMappings";
	private final String PASSTHROUGHFIELD = "passThroughField";
	private final String MAPFIELD = "mapField";
	private final String COPYOFINSOCKET = "copyOfInsocket";

	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory
			.newInstance();
	private DocumentBuilder documentBuilder;
	private Document xmlDocument = null;
	private String xmlContent;

	private ParameterSubstitutor parameterSubstitutor = null;

	public ParseExternalElements(String xmlContent, ParameterSubstitutor parameterSubstitutor) {
		this.parameterSubstitutor = parameterSubstitutor;
		this.xmlContent = xmlContent;
	}

	public Document getXmlDom() throws FileNotFoundException {
		Document externalSchemaDocument = getExternalSchemaDocument(xmlContent);
		Document externalOperationsDocument = getExternalOperationsDocument(externalSchemaDocument);
		Document externalExpressionsDocument = getExternalExpressionsDocument(externalOperationsDocument);
		return getExternalMappingsDocument(externalExpressionsDocument);
	}

	private Document getExternalMappingsDocument(Document externalMappingsDocument) throws FileNotFoundException {
		NodeList externalNodes = externalMappingsDocument
				.getElementsByTagName(INCLUDE_EXTERNAL_MAPPING);

		int externalNodesLen = externalNodes.getLength();
		if (externalNodesLen > 0) {
			for (int k = 0; k < externalNodesLen; k++) {
				Node parent = externalNodes.item(0).getParentNode();

				List<Node> nodes = createMappingNodes(parent);
				int len = parent.getChildNodes().getLength();

				removeChildNodes(parent, len);

				addChildNodes(xmlDocument, parent, nodes);
			}
		}
		return xmlDocument;
	}

	private List<Node> createMappingNodes(Node parent) throws FileNotFoundException {
		List<Node> nodeList = new ArrayList<Node>();
		Document xmlDocument2 = null;
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			if (parent.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (parent.getChildNodes().item(i).getNodeName().equals(PASSTHROUGHFIELD)
						|| parent.getChildNodes().item(i).getNodeName().equals(MAPFIELD)
						|| parent.getChildNodes().item(i).getNodeName().equals(EXPRESSIONFIELD)
						|| parent.getChildNodes().item(i).getNodeName().equals(OPERATIONFIELD)
						|| parent.getChildNodes().item(i).getNodeName().equals(COPYOFINSOCKET) )
					nodeList.add(parent.getChildNodes().item(i));
				else if (parent.getChildNodes().item(i).getNodeName().equals(INCLUDE_EXTERNAL_MAPPING)) {
					String path = parent.getChildNodes().item(i)
							.getAttributes().getNamedItem(URI).getNodeValue();
					if (!new File(path).exists()) {
						throw new FileNotFoundException(
								"External mappings file doesn't exist: "
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
					nodeList.addAll(createMappingNodes(xmlDocument2.getElementsByTagName(EXTERNAL_MAPPING).item(0)));
				}
			}
		}
		return nodeList;
	}

	private Document getExternalExpressionsDocument(Document externalOperationsDocument) throws FileNotFoundException {
		NodeList externalNodes = externalOperationsDocument
				.getElementsByTagName(INCLUDE_EXTERNAL_EXPRESSION);

		int externalNodesLen = externalNodes.getLength();
		if (externalNodesLen > 0) {
			for (int k = 0; k < externalNodesLen; k++) {
				Node parent = externalNodes.item(0).getParentNode();

				List<Node> nodes = createExpressionNodes(parent);

				removeExpressionNodes(parent);

				insertExpressionNodesBeforeOutSocket(xmlDocument, parent, nodes);
			}
		}
		return xmlDocument;
	}

	private void insertExpressionNodesBeforeOutSocket(Document xmlDocument, Node parent, List<Node> nodes) {
		Node copiedNode;
		Node outSocket = getOutSocket(parent);
		for (int i = 0; i < nodes.size(); i++) {
			copiedNode = xmlDocument.importNode(nodes.get(i), true);
			parent.insertBefore(copiedNode, outSocket);
		}
	}

	private void removeExpressionNodes(Node parent) {
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			if (parent.getChildNodes().item(i).getNodeName().equals(INCLUDE_EXTERNAL_EXPRESSION) || parent.getChildNodes().item(i).getNodeName().equals(EXPRESSION)) {
				parent.removeChild(parent.getChildNodes().item(i));
			}
		}
	}

	private List<Node> createExpressionNodes(Node parent) throws FileNotFoundException {
		List<Node> nodeList = new ArrayList<Node>();
		Document xmlDocument2 = null;
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			if (parent.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (parent.getChildNodes().item(i).getNodeName().equals(EXPRESSION))
					nodeList.add(parent.getChildNodes().item(i));
				else if (parent.getChildNodes().item(i).getNodeName().equals(INCLUDE_EXTERNAL_EXPRESSION)) {
					String path = parent.getChildNodes().item(i)
							.getAttributes().getNamedItem(URI).getNodeValue();
					if (!new File(path).exists()) {
						throw new FileNotFoundException(
								"External expressions file doesn't exist: "
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
					nodeList.addAll(createExpressionNodes(xmlDocument2.getElementsByTagName(EXTERNAL_EXPRESSION).item(0)));
				}
			}
		}
		return nodeList;
	}

	private Document getExternalOperationsDocument(Document externalSchemaDocument) throws FileNotFoundException {
		NodeList externalNodes = externalSchemaDocument
				.getElementsByTagName(INCLUDE_EXTERNAL_OPERATION);

		int externalNodesLen = externalNodes.getLength();
		if (externalNodesLen > 0) {
			for (int k = 0; k < externalNodesLen; k++) {
				Node parent = externalNodes.item(0).getParentNode();

				List<Node> nodes = createOperationNodes(parent);

				removeOperationNodes(parent);

				insertOperationNodesBeforeOutSocket(xmlDocument, parent, nodes);
			}
		}
		return xmlDocument;
	}

	private void insertOperationNodesBeforeOutSocket(Document xmlDocument, Node parent, List<Node> nodes) {
		Node copiedNode;
		Node outSocket = getOutSocket(parent);
		for (int i = 0; i < nodes.size(); i++) {
			copiedNode = xmlDocument.importNode(nodes.get(i), true);
			parent.insertBefore(copiedNode, outSocket);
		}
	}

	private Node getOutSocket(Node parent) {
		Node outSocket = null;
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			if (parent.getChildNodes().item(i).getNodeName().equals("outSocket"))
				return parent.getChildNodes().item(i);
		}
		return outSocket;
	}

	private void removeOperationNodes(Node parent) {
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			if (parent.getChildNodes().item(i).getNodeName().equals(INCLUDE_EXTERNAL_OPERATION) || parent.getChildNodes().item(i).getNodeName().equals(OPERATION)) {
				parent.removeChild(parent.getChildNodes().item(i));
			}
		}
	}

	private List<Node> createOperationNodes(Node parent) throws FileNotFoundException {
		List<Node> nodeList = new ArrayList<Node>();
		Document xmlDocument2 = null;
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			if (parent.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (parent.getChildNodes().item(i).getNodeName().equals(OPERATION))
					nodeList.add(parent.getChildNodes().item(i));
				else if (parent.getChildNodes().item(i).getNodeName().equals(INCLUDE_EXTERNAL_OPERATION)) {
					String path = parent.getChildNodes().item(i)
							.getAttributes().getNamedItem(URI).getNodeValue();
					if (!new File(path).exists()) {
						throw new FileNotFoundException(
								"External operations file doesn't exist: "
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
					nodeList.addAll(createOperationNodes(xmlDocument2.getElementsByTagName(EXTERNAL_OPERATION).item(0)));
				}
			}
		}
		return nodeList;
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
				.getElementsByTagName(INCLUDE_EXTERNAL_SCHEMA);
		int externalNodesLen = externalNodes.getLength();
		if (externalNodesLen > 0) {
			for (int k = 0; k < externalNodesLen; k++) {
				Node parent = externalNodes.item(0).getParentNode();

				List<Node> nodes = createFieldNodes(parent);
				int len = parent.getChildNodes().getLength();

				removeChildNodes(parent, len);

				addChildNodes(xmlDocument, parent, nodes);
			}
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

	private void removeChildNodes(Node parent, int len) {
		for (int i = 0; i < len; i++) {
			parent.removeChild(parent.getChildNodes().item(0));
		}
	}

	private List<Node> createFieldNodes(Node parent) throws FileNotFoundException {
		List<Node> nodeList = new ArrayList<Node>();
		Document xmlDocument2 = null;
        /*try {
            documentBuilder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}*/
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			if (parent.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
				if (parent.getChildNodes().item(i).getNodeName().equals(FIELD) || parent.getChildNodes().item(i).getNodeName().equals(RECORD))
					nodeList.add(parent.getChildNodes().item(i));
				else if (parent.getChildNodes().item(i).getNodeName().equals(INCLUDE_EXTERNAL_SCHEMA)) {
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
					nodeList.addAll(createFieldNodes(xmlDocument2.getElementsByTagName(FIELDS).item(0)));
				}
			}
		}
		return nodeList;
	}
}