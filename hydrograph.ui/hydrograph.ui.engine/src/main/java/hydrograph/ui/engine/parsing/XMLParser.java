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

 
package hydrograph.ui.engine.parsing;


import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.ui.repository.UIComponentRepo;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.xml.sax.SAXException;

public class XMLParser {
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(XMLParser.class);
	
	/**Parses the XML to fetch parameters.
	 * @param inputFile, source XML  
	 * @return true, if XML is successfully parsed.
	 * @throws Exception 
	 */
	public boolean parseXML(File inputFile,UIComponentRepo componentRepo) throws ParserConfigurationException, SAXException, IOException{
      LOGGER.debug("Parsing target XML for separating Parameters");
         SAXParserFactory factory = SAXParserFactory.newInstance();
         SAXParser saxParser;
		try {
			factory.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION,true);
			saxParser = factory.newSAXParser();
			XMLHandler xmlhandler = new XMLHandler(componentRepo);
			saxParser.parse(inputFile, xmlhandler);
			return true;
		} catch (ParserConfigurationException | SAXException | IOException exception) {
			 LOGGER.error("Parsing failed...",exception);
			throw exception; 
		}
   }  
}

