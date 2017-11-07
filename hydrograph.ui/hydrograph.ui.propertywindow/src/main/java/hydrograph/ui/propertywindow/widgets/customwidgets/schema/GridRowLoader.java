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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.FieldDataTypes;
import hydrograph.ui.common.schema.Fields;
import hydrograph.ui.common.schema.Schema;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ExternalSchemaUtil;
import hydrograph.ui.common.util.SchemaFieldUtil;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.grid.ELTGridDetails;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;


/**
 * The Class GridRowLoader.
 * 
 * @author Bitwise
 */

public class GridRowLoader {

	public final static String SCHEMA_CONFIG_XSD_PATH = Platform.getInstallLocation().getURL().getPath() + Messages.SCHEMA_CONFIG_XSD_PATH;
	private static Logger logger = LogFactory.INSTANCE.getLogger(GridRowLoader.class);
	
	private String gridRowType;
	private File schemaFile;
	private Fields fields;

	public GridRowLoader(String gridRowType, File schemaFile){
		this.gridRowType = gridRowType;
		this.schemaFile = schemaFile;
	}

	public static void showMessageBox(String message,String header,int SWT_Type)
	{
		MessageBox box=new MessageBox(Display.getCurrent().getActiveShell(), SWT_Type);
		box.setMessage(message);
		box.setText(header);
		box.open();
	}
	
	/**
	 * The method import schema rows from schema file into schema grid.
	 * 
	 */
	public List<GridRow> importGridRowsFromXML(ListenerHelper helper,Table table){
		
		List<GridRow> schemaGridRowListToImport = null;
		
		ELTGridDetails gridDetails = (ELTGridDetails)helper.get(HelperType.SCHEMA_GRID);
		List<GridRow> grids = gridDetails.getGrids();
		grids.clear();
		try(InputStream xml = new FileInputStream(schemaFile);
				InputStream	xsd = new FileInputStream(SCHEMA_CONFIG_XSD_PATH);) {
			if(StringUtils.isNotBlank(schemaFile.getPath())){
				
				if(!schemaFile.getName().contains("."))	{
					logger.error(Messages.IMPORT_XML_IMPROPER_EXTENSION);
					throw new Exception(Messages.IMPORT_XML_IMPROPER_EXTENSION);
					}
				
				if(!(schemaFile.getPath().endsWith(".schema")) && !(schemaFile.getPath().endsWith(".xml"))){
					logger.error(Messages.IMPORT_XML_INCORRECT_FILE);
					throw new Exception(Messages.IMPORT_XML_INCORRECT_FILE);
					}
				
				
				
				
				if(validateXML(xml, xsd)){
					
					DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					builderFactory.setExpandEntityReferences(false);
					builderFactory.setNamespaceAware(true);
					builderFactory.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION,true);
					
					DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
					Document document=documentBuilder.parse(schemaFile);
					
					JAXBContext jaxbContext = JAXBContext.newInstance(Schema.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					
					Schema schema= (Schema) jaxbUnmarshaller.unmarshal(document);
					fields = schema.getFields();
					List<Field> fieldsList = fields.getField();
					GridRow gridRow = null;
					schemaGridRowListToImport = new ArrayList<GridRow>();

					if(Messages.GENERIC_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							addRowToList(gridDetails, grids, getBasicSchemaGridRow(field), schemaGridRowListToImport);
						}	
					}else if(Messages.FIXEDWIDTH_GRID_ROW.equals(gridRowType)){

						for (Field field : fieldsList) {
							addRowToList(gridDetails, grids, getFixedWidthGridRow(field), schemaGridRowListToImport);
						}
					}else if(Messages.GENERATE_RECORD_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							addRowToList(gridDetails, grids, getGenerateRecordGridRow(field), schemaGridRowListToImport);
						}	
					}else if(Messages.XPATH_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							Text loopXPathData=null;
							if(table.getData()!=null && Text.class.isAssignableFrom(table.getData().getClass())){
								loopXPathData=(Text)table.getData();
							}
							XPathGridRow xPathGridRow = new XPathGridRow();
							populateCommonFields(xPathGridRow, field);
							xPathGridRow.setXPath(field.getAbsoluteOrRelativeXpath());
							if(loopXPathData!=null && StringUtils.isNotBlank(loopXPathData.getText())){
								xPathGridRow.setAbsolutexPath(loopXPathData.getText()+Path.SEPARATOR+xPathGridRow.getXPath());
							}else{
								xPathGridRow.setAbsolutexPath(xPathGridRow.getXPath());
							}
							addRowToList(gridDetails, grids, xPathGridRow, schemaGridRowListToImport);
						}	
					}else if(Messages.MIXEDSCHEME_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							addRowToList(gridDetails, grids, getMixedSchemeGridRow(field), schemaGridRowListToImport);
						}	
					}
				}
			}else{
				logger.error(Messages.EXPORT_XML_EMPTY_FILENAME);
				throw new Exception(Messages.EXPORT_XML_EMPTY_FILENAME);
			}
		} catch (JAXBException e1) {
			grids.clear();
			showMessageBox(Messages.IMPORT_XML_FORMAT_ERROR + " -\n"+e1.getMessage(),"Error",SWT.ERROR);
			logger.error(Messages.IMPORT_XML_FORMAT_ERROR);
			return null;
		}catch (DuplicateFieldException e1) {
			grids.clear();
			showMessageBox(e1.getMessage(),"Error", SWT.ERROR);
			logger.error(e1.getMessage());
			return null;
		}
		catch (Exception e) {
			grids.clear();
			showMessageBox(Messages.IMPORT_XML_ERROR+" -\n"+e.getMessage(),"Error",SWT.ERROR);
			logger.error(Messages.IMPORT_XML_ERROR);
			return null;
		}
		
		return schemaGridRowListToImport;
	}


	/**
	 * For importing engine-XML, this method import schema rows from schema file into schema grid.
	 * 
	 */
	public List<GridRow> importGridRowsFromXML(){

		List<GridRow> schemaGridRowListToImport = new ArrayList<GridRow>();
		if(StringUtils.isNotBlank(schemaFile.getPath())){
			try(InputStream xml = new FileInputStream(schemaFile);
					InputStream xsd = new FileInputStream(SCHEMA_CONFIG_XSD_PATH)) {
				
				if(validateXML(xml, xsd)){
					
					DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					builderFactory.setExpandEntityReferences(false);
					builderFactory.setNamespaceAware(true);
					builderFactory.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION,true);
					
					DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
					Document document=documentBuilder.parse(schemaFile);
					
					JAXBContext jaxbContext = JAXBContext.newInstance(Schema.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					
					Schema schema= (Schema) jaxbUnmarshaller.unmarshal(document);
					fields = schema.getFields();
					List<Field> fieldsList = fields.getField();
					GridRow gridRow = null;
					schemaGridRowListToImport = new ArrayList<GridRow>();

					if(Messages.GENERIC_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							gridRow = getBasicSchemaGridRow(field);
							schemaGridRowListToImport.add(gridRow);
						}	
					}else if(Messages.FIXEDWIDTH_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							schemaGridRowListToImport.add(getFixedWidthGridRow(field));
						}
					}else if(Messages.GENERATE_RECORD_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							gridRow = getGenerateRecordGridRow(field);
							schemaGridRowListToImport.add(gridRow);
						}
					}else if(Messages.MIXEDSCHEME_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							gridRow = getMixedSchemeGridRow(field);
							schemaGridRowListToImport.add(gridRow);
						}
					}
					else if(Messages.XPATH_GRID_ROW.equals(gridRowType)){
						for (Field field : fieldsList) {
							gridRow = new XPathGridRow();
							populateCommonFields(gridRow, field);
							((XPathGridRow)gridRow).setXPath(field.getAbsoluteOrRelativeXpath());
							schemaGridRowListToImport.add(gridRow);
						}
					}
				}
			} catch (JAXBException e1) {
				logger.warn(Messages.IMPORT_XML_FORMAT_ERROR);
				return schemaGridRowListToImport;
			}
			catch (IOException ioException) {
				logger.warn(Messages.IMPORT_XML_ERROR);
				return schemaGridRowListToImport;
			} catch (ParserConfigurationException | SAXException exception) {
				logger.warn("Doctype is not allowed in schema files",exception);
				return schemaGridRowListToImport;
			} 
		}else{
			logger.warn(Messages.EXPORT_XML_EMPTY_FILENAME);
		}
		return schemaGridRowListToImport;
	}

	/**
	 * The method exports schema rows from schema grid into schema file.
	 * 
	 */
	public void exportXMLfromGridRows(List<GridRow> schemaGridRowList){
		Schema schema = new Schema();
		fields= new Fields();

		try {
			if(StringUtils.isNotBlank(schemaFile.getPath())){
				
				if(!schemaFile.getName().contains("."))	{
					logger.error(Messages.EXPORT_XML_IMPROPER_EXTENSION);
					throw new Exception(Messages.EXPORT_XML_IMPROPER_EXTENSION);
					}
				
				if(!(schemaFile.getPath().endsWith(".schema")) && !(schemaFile.getPath().endsWith(".xml"))){
					logger.error(Messages.EXPORT_XML_INCORRECT_FILE);
					throw new Exception(Messages.EXPORT_XML_INCORRECT_FILE);
					}
				
			
				
				exportFile(schemaGridRowList, schema);
				showMessageBox(Messages.EXPORTED_SCHEMA,"Information",SWT.ICON_INFORMATION);
			}else{
				logger.error(Messages.EXPORT_XML_EMPTY_FILENAME);
				throw new Exception(Messages.EXPORT_XML_EMPTY_FILENAME);
			}
		} catch (JAXBException e) {
			showMessageBox(Messages.EXPORT_XML_ERROR+" -\n" +
					((e.getCause() != null)? e.getLinkedException().getMessage():e.getMessage()),"Error",SWT.ERROR);
			logger.error(Messages.EXPORT_XML_ERROR);
		}catch (Exception e) {
			showMessageBox(Messages.EXPORT_XML_ERROR+" -\n"+e.getMessage(),"Error",SWT.ERROR);
			logger.error(Messages.EXPORT_XML_ERROR);
		}

	}
	
	/**
	 * The method exports schema rows from schema grid into schema file.
	 * 
	 */
	public void exportXMLfromGridRowsWithoutMessage(List<GridRow> schemaGridRowList){
		Schema schema = new Schema();
		fields= new Fields();

		try {
			if(StringUtils.isNotBlank(schemaFile.getPath())){
				exportFile(schemaGridRowList, schema);
			}else{
				logger.error(Messages.EXPORT_XML_EMPTY_FILENAME);
				throw new Exception(Messages.EXPORT_XML_EMPTY_FILENAME);
			}
		} catch (JAXBException e) {
			showMessageBox(Messages.EXPORT_XML_ERROR+" -\n" + ((e.getCause() != null)? e.getLinkedException().getMessage():e.getMessage()),"Error",SWT.ERROR);
			logger.error(Messages.EXPORT_XML_ERROR);
		}catch (Exception e) {
			showMessageBox(Messages.EXPORT_XML_ERROR+" -\n" +e.getMessage(),"Error",SWT.ERROR);
			logger.error(Messages.EXPORT_XML_ERROR);
		}

	}



	private void exportFile(List<GridRow> schemaGridRowList, Schema schema)	throws JAXBException, PropertyException {
		JAXBContext jaxbContext;
		jaxbContext = JAXBContext.newInstance(Schema.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		for (GridRow gridRow : schemaGridRowList) {
			Field field = ExternalSchemaUtil.INSTANCE.convertGridRowToJaxbSchemaField(gridRow);
			fields.getField().add(field);
		}
		schema.setFields(fields);
		jaxbMarshaller.marshal(schema, schemaFile);
	}

	

	private GridRow getBasicSchemaGridRow(Field field) {
		GridRow gridRow = new BasicSchemaGridRow();
		populateCommonFields(gridRow, field);
		return gridRow;
	}
	
	
	private class DuplicateFieldException extends Exception{
		private static final long serialVersionUID = -6430674437086982389L;

		public DuplicateFieldException(String message)
		{
			super(message);
		}
	}
	

	private GridRow getGenerateRecordGridRow(Field field) {
		GridRow gridRow = new GenerateRecordSchemaGridRow();
		populateCommonFields(gridRow, field);

		if(field.getLength()!=null){
			((GenerateRecordSchemaGridRow) gridRow).setLength(String.valueOf(field.getLength()));
		}else{
			((GenerateRecordSchemaGridRow) gridRow).setLength("");
		}
		if(field.getDefault()!=null){
			((GenerateRecordSchemaGridRow) gridRow).setDefaultValue((String.valueOf(field.getDefault())));
		}else{
			((GenerateRecordSchemaGridRow) gridRow).setDefaultValue((String.valueOf("")));
		}
		if(field.getRangeFrom()!=null){
			((GenerateRecordSchemaGridRow) gridRow).setRangeFrom(String.valueOf(field.getRangeFrom()));
		}else{
			((GenerateRecordSchemaGridRow) gridRow).setRangeFrom("");
		}
		if(field.getRangeFrom()!=null){
			((GenerateRecordSchemaGridRow) gridRow).setRangeTo(String.valueOf(field.getRangeTo()));
		}else{
			((GenerateRecordSchemaGridRow) gridRow).setRangeTo("");
		}
		return gridRow;
	}



	private GridRow getFixedWidthGridRow(Field field) {
		GridRow gridRow = new FixedWidthGridRow();
		populateCommonFields(gridRow, field);
		
		if(field.getLength()!=null){
			((FixedWidthGridRow) gridRow).setLength(String.valueOf(field.getLength()));
		}else{
			((FixedWidthGridRow) gridRow).setLength("");
		}
		return gridRow;
	}
	
	private GridRow getMixedSchemeGridRow(Field field) {
		GridRow gridRow = new MixedSchemeGridRow();
		populateCommonFields(gridRow, field);
		
		if(field.getLength()!=null){
			((MixedSchemeGridRow) gridRow).setLength(String.valueOf(field.getLength()));
		}else{
			((MixedSchemeGridRow) gridRow).setLength("");
		}
		if(field.getDelimiter()!=null){
			((MixedSchemeGridRow)gridRow).setDelimiter(field.getDelimiter());
		}else{
			((MixedSchemeGridRow) gridRow).setDelimiter("");
		}
		return gridRow;
	}

	private boolean validateXML(InputStream xml, InputStream xsd){
		try
		{
			SchemaFactory factory = 
					SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			javax.xml.validation.Schema schema = factory.newSchema(new StreamSource(xsd));
			Validator validator = schema.newValidator();
			
			validator.validate(new StreamSource(xml));
			return true;
		}
		catch( SAXException| IOException ex)
		{
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", Messages.IMPORT_XML_FORMAT_ERROR + "-\n" + ex.getMessage());
			logger.error(Messages.IMPORT_XML_FORMAT_ERROR);
			return false;
		}
	}

	private void addRowToList(ELTGridDetails gridDetails, List<GridRow> grids, GridRow gridRow, List<GridRow> schemaGridRowListToImport) throws Exception {
		if(!grids.contains(gridRow)){
			grids.add(gridRow); 
			gridDetails.setGrids(grids);
			schemaGridRowListToImport.add(gridRow);
		}
		else{
			logger.error(Messages.IMPORT_XML_DUPLICATE_FIELD_ERROR);
			throw new DuplicateFieldException(Messages.IMPORT_XML_DUPLICATE_FIELD_ERROR);
		}
		
	}


	private void populateCommonFields(GridRow gridRow, Field field) {
		gridRow.setFieldName(field.getName());
		gridRow.setDataType(GridWidgetCommonBuilder.getDataTypeByValue(field.getType().value()));
		gridRow.setDataTypeValue(GridWidgetCommonBuilder.getDataTypeValue()[GridWidgetCommonBuilder.getDataTypeByValue(field.getType().value())]);
		
		if(field.getFormat()!=null){
			gridRow.setDateFormat(field.getFormat());
		}else{
			gridRow.setDateFormat("");
		}
		if(FieldDataTypes.JAVA_MATH_BIG_DECIMAL.equals(field.getType())){
			populateBigDecimal(gridRow, field);
		}else{
			gridRow.setPrecision("");
			gridRow.setScale("");
			gridRow.setScaleType(GridWidgetCommonBuilder.getScaleTypeByValue(Messages.SCALE_TYPE_NONE));
			gridRow.setScaleTypeValue(GridWidgetCommonBuilder.getScaleTypeValue()[Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX)]);
		}
		
		if(field.getDescription()!=null)
			gridRow.setDescription(field.getDescription());
		else
			gridRow.setDescription("");
		
	}



	private void populateBigDecimal(GridRow gridRow, Field field) {
		if(field.getPrecision()!=null)
			gridRow.setPrecision(String.valueOf(field.getPrecision()));
		else
			gridRow.setPrecision("");

		if(field.getScale()!=null)
			gridRow.setScale(String.valueOf(field.getScale()));
		else
			gridRow.setScale("");

		if(field.getScaleType()!=null){
			gridRow.setScaleType(SchemaFieldUtil.INSTANCE.getScaleTypeByValue(field.getScaleType().value()));	
			gridRow.setScaleTypeValue(GridWidgetCommonBuilder.getScaleTypeValue()[SchemaFieldUtil.INSTANCE.getScaleTypeByValue(field.getScaleType().value())]);
		}else{
			gridRow.setScaleType(GridWidgetCommonBuilder.getScaleTypeByValue(Messages.SCALE_TYPE_NONE));
			gridRow.setScaleTypeValue(GridWidgetCommonBuilder.getScaleTypeValue()[Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX)]);
		}
	}

}