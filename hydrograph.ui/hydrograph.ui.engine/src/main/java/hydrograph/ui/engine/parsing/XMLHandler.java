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

import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.repository.InSocketDetail;
import hydrograph.ui.engine.ui.repository.ParameterData;
import hydrograph.ui.engine.ui.repository.UIComponentRepo;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * The class XMLHandler
 * 
 * @author Bitwise
 * 
 */

public class XMLHandler extends DefaultHandler {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(XMLParser.class);
	private String currentComponent;
	private UIComponentRepo componentRepo;
	private static final String ID = "id";
	private static final String VALUE = "value";
	private static final String INSOCKET_TAG = "inSocket";
	private static final String REGEX = "[\\@]{1}[\\{]{1}[\\w]*[\\}]{1}";
	private static final String NAME = "name";

	public	XMLHandler(UIComponentRepo componentRepo){
		this.componentRepo=componentRepo;
	}
	
	/**
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws SAXException
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		List<ParameterData> tempParammeterList;
		Matcher matcher = null;
		if (isComponent(qName)) {
			currentComponent = attributes.getValue(ID);
			List<ParameterData> emptyList = new ArrayList<>();
			componentRepo.getParammeterFactory().put(currentComponent, emptyList);
		}
		if (qName.equalsIgnoreCase(INSOCKET_TAG)) {
			storeInSocket(attributes);
		}

		if (attributes.getValue(VALUE) != null) {
			matcher = Pattern.compile(REGEX).matcher(attributes.getValue(VALUE));
			if (matcher.matches()) {
				tempParammeterList = componentRepo.getParammeterFactory().get(currentComponent);
				if (tempParammeterList != null) {
					if(StringUtils.equals(qName, PropertyNameConstants.PROPERTY_TAG.value())){
						tempParammeterList.add(new ParameterData(qName, attributes.getValue(NAME)+"="+attributes.getValue(VALUE)));
					}else
					tempParammeterList.add(new ParameterData(qName, attributes.getValue(VALUE)));
				}
			}
		}
	}

	private void storeInSocket(Attributes attributes) {
		InSocketDetail inSocketdetail=new InSocketDetail();
		inSocketdetail.setComponentId(currentComponent);
		inSocketdetail.setFromComponentId(attributes.getValue("fromComponentId"));
		inSocketdetail.setFromSocketId(attributes.getValue("fromSocketId"));
		inSocketdetail.setInSocketType(attributes.getValue("type"));
		if(componentRepo.getInsocketMap().get(currentComponent)!=null && componentRepo.getInsocketMap().get(currentComponent).size()!=0)
			componentRepo.getInsocketMap().get(currentComponent).add(inSocketdetail);
		else{
			List <InSocketDetail> inSocketList=new ArrayList<>();
			inSocketList.add(inSocketdetail);
			componentRepo.getInsocketMap().put(currentComponent, inSocketList);			
		}
		LOGGER.debug(inSocketdetail.toString());
	}

	/**
	 * Checks whether Qname is a component or not.
	 * 
	 * @param qName
	 * @return true, if Qname is a component.
	 */
	private boolean isComponent(String qName) {

		for (ComponentTypes componentType : ComponentTypes.values()) {
			if (componentType.value().equals(qName))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

	}
}