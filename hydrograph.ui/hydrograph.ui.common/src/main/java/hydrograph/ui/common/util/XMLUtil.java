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

package hydrograph.ui.common.util;

import hydrograph.ui.logging.factory.LogFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *  The Class XMLUtil is used for xml utility operations.
 * @author Bitwise
 *
 */
public class XMLUtil {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(XMLUtil.class);
	
	private final static String INDENT_SPACE = "2";
	private final static String TRANSFORMER_INDENT_AMOUNT_KEY="{http://xml.apache.org/xslt}indent-amount";
	
	/**
	 * 
	 * Convert XML string to {@link Document}
	 * 
	 * @param xmlString
	 * @return {@link Document}
	 */
	public static Document convertStringToDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder;  
        try 
        {  
            builder = factory.newDocumentBuilder();  
            Document doc = builder.parse( new InputSource( new StringReader( xmlString ) ) );            
            
            return doc;
        } catch (ParserConfigurationException| SAXException| IOException e) {  
        	logger.debug("Unable to convert string to Document",e);  
        } 
        return null;
    }
	
	/**
	 * 
	 * Format given XML string
	 * 
	 * @param xmlString
	 * @return String
	 */
	public static String formatXML(String xmlString){
		
		try{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(TRANSFORMER_INDENT_AMOUNT_KEY, INDENT_SPACE);
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			
			Document xmlDoc = convertStringToDocument(xmlString);
			
			if(xmlDoc==null){
				return xmlString;
			}
			
			DOMSource source = new DOMSource(xmlDoc);
			transformer.transform(source, result);
			return result.getWriter().toString();	
		}catch(TransformerException e){
			logger.debug("Unable to format XML string",e);
		}
		
		return null;
	}
		
}
