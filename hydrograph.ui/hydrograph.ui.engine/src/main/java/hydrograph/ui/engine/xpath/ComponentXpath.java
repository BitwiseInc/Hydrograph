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
 * limitations under the License.
 *******************************************************************************/

package hydrograph.ui.engine.xpath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.util.ConverterUtil;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The class ComponentXpath
 * <p>
 * Used to create XPath objects for UI components.
 * 
 * @author Bitwise
 * 
 */
public class ComponentXpath {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(ConverterUtil.class);
	public static final ComponentXpath INSTANCE = new ComponentXpath();
	private Map<String, ComponentsAttributeAndValue> xpathMap;
	private Document doc;

	public Map<String, ComponentsAttributeAndValue> getXpathMap() {
		if (xpathMap == null) {
			xpathMap = new HashMap<String, ComponentsAttributeAndValue>();
		}
		return xpathMap;
	}

	/**
	 * Adds the parameters in the Target XML.
	 * 
	 * @param out
	 *            the out
	 * @return the byte array output stream
	 */
	public ByteArrayOutputStream addParameters(ByteArrayOutputStream out) {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
		try {
			XPath xPath = createXPathInstance(inputStream, null);
			LOGGER.debug("GENRATED COMPONENTS XPATH {}", getXpathMap().toString());
			for (Map.Entry<String, ComponentsAttributeAndValue> entry : getXpathMap().entrySet()) {
				NodeList nodeList = (NodeList) xPath.compile(entry.getKey()).evaluate(doc, XPathConstants.NODESET);
				if(entry.getValue().isNewNode())
					addParameterAsNewNode(entry, nodeList,entry.getValue().hasEmptyNode());
				else
					addParameterAsAttribute(entry, nodeList);
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			out.reset();

			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
			getXpathMap().clear();

		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException
				| TransformerException e) {
			LOGGER.error("Exception occurred while parametrizing the XML", e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException ioe) {
				LOGGER.error("Exception occurred while closing input stream", ioe);
			}
		}

		return out;
	}

	private void addParameterAsAttribute(Map.Entry<String, ComponentsAttributeAndValue> entry,NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nNode = nodeList.item(i);

			if (Node.ELEMENT_NODE == nNode.getNodeType()) {
				Element eElement = (Element) nNode;
				eElement.setAttribute(entry.getValue().getAttributeName(), entry.getValue().getAttributeValue());
			}
		}
	}

	private void addParameterAsNewNode(Map.Entry<String, ComponentsAttributeAndValue> entry,NodeList nodeList,boolean removeEmtyNode) {
		Node cloneNode=null;
		Node nNode=null;
		Node parentNode = null;
		try {

			for (int i = 0; i < nodeList.getLength(); i++) {
				parentNode = nodeList.item(i);
				if(parentNode!=null){
				NodeList nNodeLst = (NodeList) nodeList.item(i);
				for (int j = 0; j < nNodeLst.getLength(); j++) {
					nNode = nNodeLst.item(j);
					cloneNode = nNode.cloneNode(false);
					cloneNode.setTextContent(entry.getValue().getNewNodeText());
				}
				if(cloneNode!=null)
				parentNode.appendChild(cloneNode);
			}}
			// Remove empty node
			if (removeEmtyNode)
				for (int i = 0; i < nodeList.getLength(); i++) {
					parentNode = nodeList.item(i);
					NodeList nNodeLst = (NodeList) nodeList.item(i);
					nNode = nNodeLst.item(0);
					Node remove = nNode.getNextSibling();
					parentNode.removeChild(remove);
				}

		} catch (DOMException exception) {
			LOGGER.error("Exception occured", exception);
		}
	}
	
	/**
	 * Creates the Xpath instance.
	 * 
	 * @param inputStream
	 *            the input stream
	 * @param file
	 *            the file
	 * @return the x path
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public XPath createXPathInstance(ByteArrayInputStream inputStream, File file) throws ParserConfigurationException,
			SAXException, IOException {
		LOGGER.debug("Invoking X-Path instance");
		XPath xPath = null;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION, true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		if (inputStream != null)
			doc = dBuilder.parse(inputStream);
		else if (file != null) {
			doc = dBuilder.parse(file);
		}
		doc.getDocumentElement().normalize();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		xPath = XPathFactory.newInstance().newXPath();
		return xPath;
	}

}