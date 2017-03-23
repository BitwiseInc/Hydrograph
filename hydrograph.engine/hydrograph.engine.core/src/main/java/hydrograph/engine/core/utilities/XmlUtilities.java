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
package hydrograph.engine.core.utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
/**
 * The Class XmlUtilities.
 *
 * @author Bitwise
 */
public class XmlUtilities {

	
	public static Document getXMLDocument(String xmlContent) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		Document xmlDocument = null;
		builderFactory.setValidating(false);
		builderFactory.setNamespaceAware(true);
		try {
			DocumentBuilder documentBuilder = builderFactory
					.newDocumentBuilder();
			try {
				xmlDocument = documentBuilder.parse(new InputSource(
						new StringReader(xmlContent)));
			} catch (SAXException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		return xmlDocument;

	}
	
	public static String getXMLStringFromDocument(Document doc){
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		try {
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
		} catch (TransformerException e) {
			throw new RuntimeException("Error while transforming XML document to String : ", e);
		}
		String output = writer.getBuffer().toString();
		return output;
	}
	
	public static NodeList getComponentsWithAttribute(Node xmlDocument, String attribute) throws XPathExpressionException{
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = null;
		NodeList n1 = null;
			expr = xpath.compile("//*[@"+attribute+"]");
			n1 = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
		return n1;
		
	}
}
