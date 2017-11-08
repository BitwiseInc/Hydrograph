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

package hydrograph.ui.propertywindow.widgets.utility;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;

public class SchemaRowValidation{

	public static final String FIELD_IS_DUPLICATE = "Field is duplicate";
	private static final Logger logger = LogFactory.INSTANCE.getLogger(SchemaRowValidation.class);
	private static final String NONE = "none";
	private static final String REGULAR_EXPRESSION_FOR_NUMBER = "\\d+";
	private static final String PARQUET = "parquet";
	private static final String HIVE = "hive";
	private static final String JAVA_MATH_BIG_DECIMAL = "java.math.BigDecimal";
	private static final String JAVA_UTIL_DATE = "java.util.Date";
	private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
	private static final String JAVA_LANG_DOUBLE = "java.lang.Double";
	private static final String JAVA_LANG_FLOAT = "java.lang.Float";
	private static final String JAVA_LANG_SHORT = "java.lang.Short";
	private static final String JAVA_LANG_LONG = "java.lang.Long";
	private boolean invalidDateFormat = false;
	private boolean invalidLength = false;
	private boolean isRowInvalid;
	private Table table;
	
	public static final SchemaRowValidation INSTANCE = new SchemaRowValidation();
	
	
	private SchemaRowValidation(){
		
	}
	
	/**
	 * @param gridRow
	 * @param item
	 * @param table
	 * @param componentType
	 * 
	 * This method highlight invalid data row with red color.
	 * 
	 */
	public void highlightInvalidRowWithRedColor(GridRow gridRow,TableItem item,Table table,String componentType ){ 
		this.table=table;
		if(item==null){
			for(TableItem tableItem:table.getItems()){		
		     setRedColorOnTableRowBasedOnInvalidData((GridRow)tableItem.getData(), componentType, tableItem);	
			}
		}else{
			 setRedColorOnTableRowBasedOnInvalidData(gridRow, componentType, item);	
			 gridRow=null;
		}
	}
	
	
	private void setRedColorOnTableRowBasedOnInvalidData(GridRow gridRow,
			String componentType, TableItem tableItem){
		if(!gridRow.getFieldName().matches(Constants.REGEX)){
			setRedColor(tableItem);
		}
		else if(gridRow instanceof FixedWidthGridRow){
			if(gridRow instanceof GenerateRecordSchemaGridRow){
				executeIfObjectIsGenerateRecordRow(gridRow, componentType, tableItem);
			}else{
				executeIfObjectIsFixedWidthRow(gridRow, componentType, tableItem);
			}
		}
		else if(gridRow instanceof GridRow){
			validationCheckForBigDecimalAndDateDatatype(gridRow, componentType, tableItem);
			if(!isRowInvalid)
			{	
				if(gridRow instanceof XPathGridRow){
					validationCheckForXpathGridRow(gridRow,tableItem);
				}
			}
		}
	}
	
	
	private void validationCheckForXpathGridRow(GridRow gridRow,TableItem tableItem) {
		XPathGridRow xPathGridRow=(XPathGridRow)gridRow;
		String xPath=xPathGridRow.getXPath();
		if(StringUtils.isBlank(xPath)){
			setRedColor(tableItem);
		}else{ 
			checkIfXPathIsDuplicate();
		}
	}

	private void checkIfXPathIsDuplicate( ) {
		Text loopXpathQueryTextBox=(Text)table.getData();
		String loopXPathQuery=loopXpathQueryTextBox.getText();
		Set<Path> setToCheckDuplicates= new HashSet<Path>();
		Set<String> uniqueName=new HashSet<>();
		for(TableItem tableItem:table.getItems()){
			Path xPathColumn=makeXPathAbsoluteIfNot(tableItem.getText(2), loopXPathQuery);
			if(!uniqueName.add(tableItem.getText(0))){
				tableItem.setData(Constants.ERROR_MESSAGE,FIELD_IS_DUPLICATE);
				setRedColor(tableItem);
			}
			else if(!setToCheckDuplicates.add(xPathColumn)){
				tableItem.setData(Constants.ERROR_MESSAGE,Messages.X_PATH_IS_DUPLICATE);
				setRedColor(tableItem);
			}
			else{
				tableItem.setData(Constants.ERROR_MESSAGE,"");
				setBlackColor(tableItem);
			}
		}	
	}
	

	private Path makeXPathAbsoluteIfNot(String xPath,String loopXPathQuery){
		if(StringUtils.isNotBlank(loopXPathQuery) && !xPath.startsWith(loopXPathQuery)){
			xPath=loopXPathQuery+Path.SEPARATOR+xPath;
		}
		return new Path(xPath);
	}

	private void validationCheckForBigDecimalAndDateDatatype(GridRow gridRow, String componentType, TableItem tableItem){
		if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL)){
			validationCheckForBigDecimalDatatype(gridRow, componentType, tableItem);
		}else if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(),JAVA_UTIL_DATE)){
			validationCheckForDateDatatype(gridRow, tableItem);	
		}
		else{
			setBlackColor(tableItem);
		}
	}


	private void executeIfObjectIsFixedWidthRow(GridRow gridRow,
			String componentType, TableItem tableItem){
		
		if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL)){
			if(validationCheckForBigDecimalDatatype(gridRow, componentType, tableItem))
				return;
		}else if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(),JAVA_UTIL_DATE)){
			validationCheckForDateDatatype(gridRow, tableItem);
			return;
		}
		
		FixedWidthGridRow fixedWidthGridRow=(FixedWidthGridRow)gridRow;
		if(fixedWidthGridRow instanceof MixedSchemeGridRow){
			if((StringUtils.isBlank(fixedWidthGridRow.getDelimiter()) && StringUtils.isBlank(fixedWidthGridRow.getLength()))
			   ||(StringUtils.isNotBlank(fixedWidthGridRow.getDelimiter()) && StringUtils.isNotBlank(fixedWidthGridRow.getLength()))
			   ||(StringUtils.isNotBlank(fixedWidthGridRow.getLength()) && (fixedWidthGridRow.getLength().equals("0") ))
			   ||(StringUtils.isNotBlank(fixedWidthGridRow.getLength())&& !(fixedWidthGridRow.getLength().matches(REGULAR_EXPRESSION_FOR_NUMBER))) 
			   ){
				setRedColor(tableItem);
				invalidLength = true;
			}else{
				setBlackColor(tableItem);
				invalidLength = false;
			}
		}else{
			if(StringUtils.isBlank(fixedWidthGridRow.getLength())||!(fixedWidthGridRow.getLength().matches(REGULAR_EXPRESSION_FOR_NUMBER))|| (fixedWidthGridRow.getLength().equals("0"))){
				setRedColor(tableItem);
				invalidLength = true;
			}else{
				setBlackColor(tableItem);
				invalidLength = false;
			}
		}
	}
	
	
	private boolean validateSchemaRangeForDate(GenerateRecordSchemaGridRow generateRecordSchemaGridRow){
		
		Date rangeFromDate = null, rangeToDate = null;
		
		SimpleDateFormat formatter = new SimpleDateFormat(generateRecordSchemaGridRow.getDateFormat());

		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeFrom())){
			try{
				rangeFromDate = formatter.parse(generateRecordSchemaGridRow.getRangeFrom());
			}catch (ParseException e){
				return true;
			}
		}

		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeTo())){
			try{
				rangeToDate = formatter.parse(generateRecordSchemaGridRow.getRangeTo());
			}catch (ParseException e){
				return true;
			}
		}

		if (rangeFromDate != null && rangeToDate != null && rangeFromDate.after(rangeToDate)){
			return true;
		}
		
		return false;
		
	}
	
	
	private boolean validateSchemaRangeForBigDecimal(GenerateRecordSchemaGridRow generateRecordSchemaGridRow){
		
		BigDecimal rangeFrom = null, rangeTo = null;
		
		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeFrom()) && generateRecordSchemaGridRow.getRangeFrom().matches(REGULAR_EXPRESSION_FOR_NUMBER)){
			rangeFrom = new BigDecimal(generateRecordSchemaGridRow.getRangeFrom());
		}
		
		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeTo()) && generateRecordSchemaGridRow.getRangeTo().matches(REGULAR_EXPRESSION_FOR_NUMBER)){
			rangeTo = new BigDecimal(generateRecordSchemaGridRow.getRangeTo());
		}
		
		if (rangeFrom != null && rangeTo != null){
			if (rangeFrom.compareTo(rangeTo) > 0){
				return true;
			}
		}

		if (!generateRecordSchemaGridRow.getLength().isEmpty()){
			int fieldLength = 0, scaleLength = 0;
			fieldLength = Integer.parseInt(generateRecordSchemaGridRow.getLength());
		
			if (!generateRecordSchemaGridRow.getScale().isEmpty()){
				scaleLength = Integer.parseInt(generateRecordSchemaGridRow.getScale());
			}

			if (fieldLength < 0){
				return true;
			}else if (scaleLength < 0){
				return true;
			}else if (scaleLength >= fieldLength){
				return true;
			}else{

				String minPermissibleRangeValue = "", maxPermissibleRangeValue = "";

				for (int i = 1; i <= fieldLength; i++){
					maxPermissibleRangeValue = maxPermissibleRangeValue.concat("9");
					if (minPermissibleRangeValue.trim().length() == 0){
						minPermissibleRangeValue = minPermissibleRangeValue.concat("-");
					}else{
						minPermissibleRangeValue = minPermissibleRangeValue.concat("9");
					}
				}
			
				if(minPermissibleRangeValue.equals("-")){
					minPermissibleRangeValue = "0";
				}
				
				if (scaleLength != 0){
					int decimalPosition = fieldLength - scaleLength;

					if (decimalPosition == 1){
						minPermissibleRangeValue = "0";
						maxPermissibleRangeValue = maxPermissibleRangeValue.replaceFirst("9", ".");
					}else{
						minPermissibleRangeValue = minPermissibleRangeValue.substring(0, decimalPosition - 1)
							+ "." + minPermissibleRangeValue.substring(decimalPosition);
					}
				
					maxPermissibleRangeValue = maxPermissibleRangeValue.substring(0, decimalPosition - 1)
						+ "." + maxPermissibleRangeValue.substring(decimalPosition);
				}
				
				if(fieldLength == 0){
					minPermissibleRangeValue = "0";
					maxPermissibleRangeValue = "0";
				}
			
				BigDecimal minRangeValue = new BigDecimal(minPermissibleRangeValue);
				BigDecimal maxRangeValue = new BigDecimal(maxPermissibleRangeValue);

				if(checkIfSchemaRangeAndLengethIsInvalid(rangeFrom, fieldLength, rangeTo, minRangeValue, maxRangeValue)){
					return true;
				}			
			}
		}
		return false;
	}
	
	private boolean checkIfSchemaRangeAndLengethIsInvalid(BigDecimal rangeFrom, int fieldLength, BigDecimal rangeTo, 
			BigDecimal minRangeValue, BigDecimal maxRangeValue) {
		if (rangeFrom != null && fieldLength > 0 && rangeTo != null){
			if (rangeFrom.compareTo(minRangeValue) < 0){
				return true;
			}else if(rangeTo.compareTo(maxRangeValue) > 0){
				return true;
			}
		}else if(rangeFrom != null && fieldLength > 0 && rangeTo == null){
			if (rangeFrom.compareTo(maxRangeValue) > 0){
				return true;
			}
		}else if(rangeFrom == null && fieldLength > 0 && rangeTo != null){
			if (rangeTo.compareTo(minRangeValue) < 0){
				return true;
			}
		}
		return false;		
	}


	private boolean validateSchemaRangeForDoubleFloatIntegerLongShort(GenerateRecordSchemaGridRow generateRecordSchemaGridRow) {
		BigDecimal rangeFrom = null, rangeTo = null;

		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeFrom())){
			rangeFrom = new BigDecimal(generateRecordSchemaGridRow.getRangeFrom());
		}
		
		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeTo())){
			rangeTo = new BigDecimal(generateRecordSchemaGridRow.getRangeTo());
		}

		if (rangeFrom != null && rangeTo != null){
			if (rangeFrom.compareTo(rangeTo) > 0){
				return true;
			}
		}
		
		if (!generateRecordSchemaGridRow.getLength().isEmpty()){
			int fieldLength = Integer.parseInt(generateRecordSchemaGridRow.getLength());
		
			if (fieldLength < 0){
				return true;
			}else{

				String minPermissibleRangeValue = "", maxPermissibleRangeValue = "";

				for (int i = 1; i <= fieldLength; i++){
					maxPermissibleRangeValue = maxPermissibleRangeValue.concat("9");
					if (minPermissibleRangeValue.trim().length() == 0){
						minPermissibleRangeValue = minPermissibleRangeValue.concat("-");
					}else{
						minPermissibleRangeValue = minPermissibleRangeValue.concat("9");
					}
				}
				
				if(fieldLength == 0){
					minPermissibleRangeValue = "0";
					maxPermissibleRangeValue = "0";
				}
			
				if(minPermissibleRangeValue.equals("-")){
					minPermissibleRangeValue = "0";
				}
			
				BigDecimal minRangeValue = new BigDecimal(minPermissibleRangeValue);
				BigDecimal maxRangeValue = new BigDecimal(maxPermissibleRangeValue);
		
			
				if(checkIfSchemaRangeAndLengethIsInvalid(rangeFrom, fieldLength, rangeTo, minRangeValue, maxRangeValue)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	private void executeIfObjectIsGenerateRecordRow(GridRow gridRow, String componentType, TableItem tableItem){

		GenerateRecordSchemaGridRow generateRecordSchemaGridRow = (GenerateRecordSchemaGridRow) gridRow;
		boolean isRedColor = false;
		
		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeFrom())
				|| StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeTo())){
			if (StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_UTIL_DATE)){				
				isRedColor = validateSchemaRangeForDate(generateRecordSchemaGridRow);
			}else if (StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL)){				
				isRedColor = validateSchemaRangeForBigDecimal(generateRecordSchemaGridRow);
			}else if (StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_DOUBLE)
					|| StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_FLOAT)
					|| StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_INTEGER)
					|| StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_LONG)
					|| StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_SHORT)){				
				isRedColor = validateSchemaRangeForDoubleFloatIntegerLongShort(generateRecordSchemaGridRow);
			}
		}

		if (isRedColor){
			setRedColor(tableItem);
		}else{
			if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL)){
				validationCheckForBigDecimalDatatype(gridRow, componentType, tableItem);
			}else if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(),JAVA_UTIL_DATE)){
				validationCheckForDateDatatype(gridRow, tableItem);	
			}else{
				setBlackColor(tableItem);
			}
		}
	}


	private void setBlackColor(TableItem tableItem){
		tableItem.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 0, 0));
		isRowInvalid=false;
	}

	
	private void setRedColor(TableItem tableItem){
		tableItem.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
		isRowInvalid=true;
	}
	
	
	private void validationCheckForDateDatatype(GridRow gridRow, TableItem tableItem){
		if((StringUtils.isBlank(gridRow.getDateFormat()))){
			setRedColor(tableItem);
			invalidDateFormat = true;
		}else{
			setBlackColor(tableItem);
			invalidDateFormat = false;
		}
	}
	
	
	private boolean validationCheckForBigDecimalDatatype(GridRow gridRow,
			String componentType, TableItem tableItem){
		
		int precision = 0 , scale = 0 ;
		
		if(StringUtils.isNotBlank(gridRow.getPrecision()) && StringUtils.isNotBlank(gridRow.getScale())){
			try{
				precision = Integer.parseInt(gridRow.getPrecision());
			}
			catch(NumberFormatException exception){
				logger.debug("Failed to parse the precision ", exception);
				return false;
			}
			
			try{
				scale = Integer.parseInt(gridRow.getScale());
			}
			catch(NumberFormatException exception){
				logger.debug("Failed to parse the scale ", exception);
				return false;
			}
		}
		
		if(StringUtils.containsIgnoreCase(componentType, HIVE)||StringUtils.containsIgnoreCase(componentType, PARQUET)){
			if(StringUtils.isBlank(gridRow.getPrecision())|| StringUtils.isBlank(gridRow.getScale()) ||
					StringUtils.equalsIgnoreCase(gridRow.getScaleTypeValue(), NONE)||
					!(gridRow.getScale().matches(REGULAR_EXPRESSION_FOR_NUMBER))||!(gridRow.getPrecision().matches(REGULAR_EXPRESSION_FOR_NUMBER))
					|| precision <= scale){
				setRedColor(tableItem);
				return true;
			}else{
				setBlackColor(tableItem);
				return false;
			}	
		}else if(StringUtils.isBlank(gridRow.getScale()) ||
				StringUtils.equalsIgnoreCase(gridRow.getScaleTypeValue(), NONE)||
				!(gridRow.getScale().matches(REGULAR_EXPRESSION_FOR_NUMBER))||(!(gridRow.getPrecision().matches(REGULAR_EXPRESSION_FOR_NUMBER))&&
				 StringUtils.isNotBlank(gridRow.getPrecision()))){
			setRedColor(tableItem);
			return true;
		}else{
			setBlackColor(tableItem);
			return false;
		}
	}
}
