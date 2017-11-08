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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.SchemaParser;
import com.predic8.schema.Sequence;

import hydrograph.ui.common.component.config.DataTypes;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.logging.factory.LogFactory;


/**
 *  The Class XMLUtil is used for xml utility operations.
 * @author Bitwise
 *
 */
public class XMLUtil {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(XMLUtil.class);
	private static final String INVALID_XSD_FILE = "Invalid XSD file: ";
	private final static String INDENT_SPACE = "2";
	private final static String TRANSFORMER_INDENT_AMOUNT_KEY="{http://xml.apache.org/xslt}indent-amount";
	private static final String ROW_ELEMENT_IS_NOT_PRESENT_IN_THE_GIVEN_XSD_FILE = "Row element is not present in the given XSD file.";
	private static final String ROOT_ELEMENT_DOES_NOT_CONTAINS_CHILD_ELEMENT = "Root element does not contains child element";
	private static final String ROOT_ELEMENT_IS_NOT_PRESENT_IN_THE_GIVEN_XSD_FILE = "Root element is not present in the given XSD file.";
	private static final String ROW_ELEMENT_DOES_NOT_CONTAINS_CHILD_ELEMENT = "Row element does not contains child element";
	
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
        	factory.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION,true);
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
		
		try(Writer writer = new StringWriter()){
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(TRANSFORMER_INDENT_AMOUNT_KEY, INDENT_SPACE);
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(writer);
			
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
		catch (IOException e){
			logger.debug("Unable to format XML string",e);
		}
		
		return null;
	}
	
	
	public static void unformatXMLString(ByteArrayOutputStream arrayOutputStream) {
		byte[] bytes = arrayOutputStream.toByteArray();
		try(InputStream inputStream = new ByteArrayInputStream(bytes);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader reader = new BufferedReader(inputStreamReader);){
			arrayOutputStream.reset();
			String line;
			while ((line = reader.readLine()) != null){
				arrayOutputStream.write((line.trim() + "\n").getBytes());
			}
		} catch (IOException e) {
			logger.warn("Unable to remove formatting while saving UI XML string",e);
		}
	}
	
	private void addDataTypeToGridRow(Node node,XPathGridRow xPathGridRow) {
		String nodeValue=node.getTextContent();
		xPathGridRow.setDataTypeValue(getDataTypeOntheBasisOfValue(nodeValue));
		xPathGridRow.setDataType(SchemaFieldUtil.INSTANCE.getDataTypeByValue(xPathGridRow.getDataTypeValue()));
	}
    
	 private String getDataTypeOntheBasisOfValue(String value){
			if(StringUtils.isNotBlank(value)){
				try{
					Integer.valueOf(value);
					return DataTypes.JAVA_LANG_INTEGER.value();
				}catch(Exception shortException){
					try{
						Double.valueOf(value);
						return DataTypes.JAVA_LANG_DOUBLE.value();
					}catch(Exception IntegerException){
						return DataTypes.JAVA_LANG_STRING.value();
					}
				}
		    }
			return DataTypes.JAVA_LANG_STRING.value();
	 }
	public List<GridRow> getSchemaFromXML(File schemaFile,String loopXPathQuery)
				throws ParserConfigurationException, SAXException, IOException, JAXBException {
			Document document = getDOMObject(schemaFile);
			return intializeGridRowObject(document,loopXPathQuery);
			
	}
	 
	 private List<GridRow> intializeGridRowObject(Document document,String loopXPathQuery) {
			
			org.w3c.dom.NodeList nodeList=document.getDocumentElement().getChildNodes();
			for(int i=0;i<nodeList.getLength();i++){
				Node currentNode = nodeList.item(i);
				
		        if (currentNode.getNodeType() == Node.ELEMENT_NODE){
		        	return iterateChildNode(currentNode,loopXPathQuery);
		        } 
			}
			return null;
	 }
	 
	 private List<GridRow> iterateChildNode(Node currentNode,String loopXPathQuery) {
			List<GridRow> schemaRecords = new ArrayList<GridRow>();
			org.w3c.dom.NodeList nodeList=currentNode.getChildNodes();
			for(int i=0;i<nodeList.getLength();i++){
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE){
					XPathGridRow xPathGridRow=new XPathGridRow();
					String fieldName=node.getNodeName();
					if(fieldName.contains(":")){
						fieldName=fieldName.split(":")[1];
					}
					xPathGridRow.setFieldName(fieldName);
		        	addDataTypeToGridRow(node,xPathGridRow);
		        	String computedXpath="";
		        	xPathGridRow.setDateFormat("");
		        	xPathGridRow.setPrecision("");
		        	xPathGridRow.setScale("");
		        	xPathGridRow.setScaleType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
		        	xPathGridRow.setDescription("");
		        	computeXPath(node,xPathGridRow,computedXpath,schemaRecords,loopXPathQuery);
		        	
		        }
			}
			return schemaRecords;
		}
	 
	 private void computeXPath(Node node,XPathGridRow xPathGridRow,String computedXpath,List<GridRow> schemaRecords,String loopXPathQuery) {
			org.w3c.dom.NodeList nodeList=node.getChildNodes();
			
			if(!hasChild(node)){
				xPathGridRow.setXPath(computedXpath+node.getNodeName());
			
				if(StringUtils.isNotBlank(loopXPathQuery)){
					xPathGridRow.setAbsolutexPath(loopXPathQuery+Path.SEPARATOR+xPathGridRow.getXPath());
				}else{
					xPathGridRow.setAbsolutexPath(xPathGridRow.getXPath());
				}
				schemaRecords.add(xPathGridRow);
			}else{
				
				computedXpath=computedXpath+node.getNodeName()+Path.SEPARATOR;
				for(int i=0;i<nodeList.getLength();i++){
					Node nod = nodeList.item(i);
					
					if(nod.getNodeType()==Node.ELEMENT_NODE){
						XPathGridRow xPathGridRowChild=new XPathGridRow();
						String fieldName=nod.getNodeName();
						if(fieldName.contains(":")){
							fieldName=fieldName.split(":")[1];
						}
						xPathGridRowChild.setFieldName(fieldName);
			        	addDataTypeToGridRow(nod,xPathGridRowChild);
			        	xPathGridRowChild.setDateFormat("");
			        	xPathGridRowChild.setPrecision("");
			        	xPathGridRowChild.setScale("");
			        	xPathGridRowChild.setScaleType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
			        	xPathGridRowChild.setDescription("");
						computeXPath(nod,xPathGridRowChild,computedXpath,schemaRecords,loopXPathQuery);
					}
					
				}
				
			}
		}
	 
	 private boolean hasChild(Node nod) {
			org.w3c.dom.NodeList nodeList=nod.getChildNodes();
			for(int i=0;i<nodeList.getLength();i++){
				Node node = nodeList.item(i);
				if(node.getNodeType()==Node.ELEMENT_NODE)
				return true;	
			}
			return false;
		}
	 
	 private Document getDOMObject(File schemaFile) throws ParserConfigurationException, SAXException, IOException {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			Document document=documentBuilder.parse(schemaFile);
			document.getDocumentElement().getNodeName();
			document.getElementsByTagName(document.getDocumentElement().getNodeName()).getLength();
			return document;
	 }
	 
		public List<GridRow> getSchemaFromXSD(String XSDFile,String loopXPathQuery) throws ParserConfigurationException, SAXException, IOException, JAXBException{
			SchemaParser parser = new SchemaParser();
			try{
				Schema schema=parser.parse(XSDFile);
				Element element = getRowTagElement(schema);
				if(element==null){
					return null;
				}
				return parseElementsOfRowTag(element,loopXPathQuery);
			}
			catch(Exception e){
				createMessageBox(INVALID_XSD_FILE+e.getMessage(), Constants.ERROR, SWT.ERROR,Display.getCurrent().getActiveShell());
				
			}
			return null;
		}
		
		private com.predic8.schema.Element getRowTagElement(Schema schema) {
			List<Element> elementInsideSchema=schema.getElements();
			if(elementInsideSchema==null ||schema.getElements().isEmpty()){
				createMessageBox(ROOT_ELEMENT_IS_NOT_PRESENT_IN_THE_GIVEN_XSD_FILE, Constants.ERROR, SWT.ERROR,Display.getCurrent().getActiveShell());
			    return null; 
			}
			Element rootElement=schema.getElements().get(0);
			
			ComplexType complexType = getComplexTypeOfElement(schema, rootElement);
			if(complexType==null){
				createMessageBox(ROOT_ELEMENT_DOES_NOT_CONTAINS_CHILD_ELEMENT, Constants.ERROR, SWT.ERROR,Display.getCurrent().getActiveShell());
				return null;
			}
			Sequence sequence=complexType.getSequence();
			List<Element> elementsInsideRootTag=sequence.getElements();
			if(elementsInsideRootTag==null || elementsInsideRootTag.isEmpty()){
				createMessageBox(ROW_ELEMENT_IS_NOT_PRESENT_IN_THE_GIVEN_XSD_FILE, Constants.ERROR, SWT.ERROR,Display.getCurrent().getActiveShell());
			    return null;
			}
			return elementsInsideRootTag.get(0);
		}
		
		private ComplexType getComplexTypeOfElement(Schema schema,Element rootElement) {
			if(rootElement.getBuildInTypeName()!=null){
				return null;
			}
			ComplexType complexType;
			if(rootElement.getType()!=null){
				complexType=schema.getComplexType(rootElement.getType().getQualifiedName());
			}else{
				complexType= (ComplexType)rootElement.getEmbeddedType();
			}
			return complexType;
		}
		
		private List<GridRow> parseElementsOfRowTag(com.predic8.schema.Element element,String looXPathQuery) {
			List<GridRow> schemaRecordList=new ArrayList<>();
			ComplexType complexType=getComplexTypeOfElement(element.getSchema(), element);
			if(complexType==null){
				createMessageBox(ROW_ELEMENT_DOES_NOT_CONTAINS_CHILD_ELEMENT, Constants.ERROR, SWT.ERROR,Display.getCurrent().getActiveShell());
				return null;
			}
			Sequence sequence=complexType.getSequence();
			List<com.predic8.schema.Element> elements=sequence.getElements();
			for(int i=0;i<elements.size();i++){
				String computedXpath="";
				Element element2=elements.get(i);
				XPathGridRow xPathGridRowChild=new XPathGridRow();
				xPathGridRowChild.setFieldName(element2.getName());
				xPathGridRowChild.setScale("");
				xPathGridRowChild.setDateFormat("");
				xPathGridRowChild.setScaleType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
				setDataTypeOfXPathGridRow(element2, xPathGridRowChild);
				xPathGridRowChild.setPrecision("");
				xPathGridRowChild.setDescription("");
				computeXPath(element2,xPathGridRowChild,computedXpath,schemaRecordList,looXPathQuery);
			}
			return schemaRecordList;
		}
		
		
		 private void computeXPath(com.predic8.schema.Element element2,XPathGridRow xPathGridRow,String computedXpath,List<GridRow> schemaRecords,
				 String loopXPathQuery) {
				ComplexType complexTypeOfInnerElements=getComplexTypeOfElement(element2.getSchema(),element2);
				if(complexTypeOfInnerElements==null){
					if(computedXpath.contains(element2.getName()+Path.SEPARATOR)){
						computedXpath=computedXpath.replaceAll(element2.getName()+Path.SEPARATOR, "");
						
					}
					xPathGridRow.setXPath(computedXpath+element2.getName());
					if(StringUtils.isNotBlank(loopXPathQuery)){
						xPathGridRow.setAbsolutexPath(loopXPathQuery+Path.SEPARATOR+xPathGridRow.getXPath());
					}else{
						xPathGridRow.setAbsolutexPath(xPathGridRow.getXPath());
					}
					schemaRecords.add(xPathGridRow);
				}
				else{
					computedXpath=computedXpath+element2.getName()+Path.SEPARATOR;
					List<Element> elements=complexTypeOfInnerElements.getSequence().getElements();
					for(int i=0;i<elements.size();i++){
						Element element=elements.get(i);
						XPathGridRow xPathGridRowChild=new XPathGridRow();
						xPathGridRowChild.setFieldName(element.getName());
						xPathGridRowChild.setScale("");
						xPathGridRowChild.setScaleType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
						xPathGridRowChild.setDateFormat("");
						setDataTypeOfXPathGridRow(element, xPathGridRowChild);
						xPathGridRowChild.setPrecision("");
						xPathGridRowChild.setDescription("");
						computeXPath(element,xPathGridRowChild,computedXpath,schemaRecords,loopXPathQuery);
						
					}
					
				}
			}
		
		
		
		private void setDataTypeOfXPathGridRow(com.predic8.schema.Element element2, XPathGridRow gridRow) {
			if(StringUtils.isNotBlank(element2.getBuildInTypeName())){
				gridRow.setDataTypeValue(getWrapperClassName(element2.getBuildInTypeName(),gridRow));
				gridRow.setDataType(SchemaFieldUtil.INSTANCE.getDataTypeByValue(gridRow.getDataTypeValue()));
			}
		}
		
		
		private String getWrapperClassName(String value,XPathGridRow gridRow){
			 if(StringUtils.isNotBlank(value)){
					if(value.contains("short") ||value.contains("byte")){
						return DataTypes.JAVA_LANG_SHORT.value();
					}else if(value.contains("int")){
						return DataTypes.JAVA_LANG_INTEGER.value();
					}else if(value.contains("long")){
						return DataTypes.JAVA_LANG_LONG.value();
					}else if(value.contains("float")){
						return DataTypes.JAVA_LANG_FLOAT.value();
					}else if(value.contains("double")){
						return DataTypes.JAVA_LANG_DOUBLE.value();
					}else if(value.contains("bigdecimal") ||value.contains("decimal")){
						gridRow.setScale("1");
						gridRow.setScaleTypeValue("explicit");
						gridRow.setScaleType(2);
						return DataTypes.JAVA_MATH_BIG_DECIMAL.value();
					}else if(value.contains("boolean")){
						return DataTypes.JAVA_LANG_BOOLEAN.value();
					}else if(value.contains("date")){
						gridRow.setDateFormat("dd/mm/yyyy");
						return DataTypes.JAVA_UTIL_DATE.value();
					}
				}
				return DataTypes.JAVA_LANG_STRING.value();
		 }
		
		/**
		 * create SWT MessageBox
		 * 
		 * @param message to be shown 
		 * @param title of widow
		 * @param style to be set on window     
		 * @param shell currentActive shell      
		 */
		
		public static int createMessageBox(String message,String windowTitle,int style,Shell shell) {
			MessageBox messageBox = new MessageBox(shell,style);
			messageBox.setText(windowTitle);
			messageBox.setMessage(message);
			return messageBox.open();
		}
}
