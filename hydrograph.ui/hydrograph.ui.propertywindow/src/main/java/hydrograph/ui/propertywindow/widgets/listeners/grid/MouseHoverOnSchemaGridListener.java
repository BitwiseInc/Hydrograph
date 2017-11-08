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

package hydrograph.ui.propertywindow.widgets.listeners.grid;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.MouseActionListener;

public class MouseHoverOnSchemaGridListener extends MouseActionListener{


private static final Logger logger = LogFactory.INSTANCE.getLogger(MouseHoverOnSchemaGridListener.class);
	
	Table table=null;
	private Shell tip=null;
	private Label label=null;
	
	private static final String JAVA_MATH_BIG_DECIMAL = "java.math.BigDecimal";
	private static final String JAVA_UTIL_DATE = "java.util.Date";
	private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
	private static final String JAVA_LANG_DOUBLE = "java.lang.Double";
	private static final String JAVA_LANG_FLOAT = "java.lang.Float";
	private static final String JAVA_LANG_SHORT = "java.lang.Short";
	private static final String JAVA_LANG_LONG = "java.lang.Long";
	private static final String REGULAR_EXPRESSION_FOR_NUMBER = "\\d+";
	
	@Override
	public int getListenerType(){
		return SWT.MouseHover;
	}
	
	@Override
	public void mouseAction(PropertyDialogButtonBar propertyDialogButtonBar,
			ListenerHelper helpers,Event event,Widget... widgets){
		
		  table=(Table)widgets[0];
	      TableItem item = table.getItem(new Point(event.x, event.y));
	      
	      if (item != null && item.getForeground().getRed()==255){
	    	  
	    	  if (tip != null && !tip.isDisposed()){
        	   tip.dispose();
	    	  }
	    	  
           tip = new Shell(table.getShell(), SWT.ON_TOP | SWT.TOOL);
           tip.setLayout(new FormLayout());
           label = new Label(tip, SWT.NONE);
           label.setForeground(table.getParent().getShell().getDisplay()
               .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
           label.setBackground(table.getParent().getShell().getDisplay()
               .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
           label.setData("_TABLEITEM", item);
           label.setText(setAppropriateToolTipMessage(item,(String)helpers.get(HelperType.COMPONENT_TYPE)));
           label.addListener(SWT.MouseExit,ListenerFactory.Listners.DISPOSE_LISTENER
					.getListener().getListener(propertyDialogButtonBar, helpers, widgets) );
           label.addListener(SWT.MouseDown,ListenerFactory.Listners.DISPOSE_LISTENER
					.getListener().getListener(propertyDialogButtonBar, helpers, widgets));
           Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
           Point pt = table.toDisplay(event.x, event.y);
           tip.setBounds(pt.x, pt.y-20, size.x, size.y);
           tip.setVisible(true);
           table.setData("tip",tip);
           table.setData("label", label);
	      }
	}
	
	
	private String setToolTipForDateFormatIfBlank(GridRow gridRow){
		return Messages.DATE_FORMAT_MUST_NOT_BE_BLANK;
	}
	
	private String setToolTipForBigDecimal(GridRow gridRow, String componentType){
		int precision = 0 , scale = 0;
		
		if(StringUtils.isNotBlank(gridRow.getPrecision()) && StringUtils.isNotBlank(gridRow.getScale())){
			try{
				precision = Integer.parseInt(gridRow.getPrecision());
			}
			catch(NumberFormatException exception){
				logger.debug("Failed to parse the precision ", exception);
				return Messages.PRECISION_MUST_CONTAINS_NUMBER_ONLY_0_9;
			}
			
			try{
				scale = Integer.parseInt(gridRow.getScale());
			}
			catch(NumberFormatException exception){
				logger.debug("Failed to parse the scale ", exception);
				return Messages.SCALE_MUST_CONTAINS_NUMBER_ONLY_0_9;
			}
		}
		
		if(StringUtils.isBlank(gridRow.getPrecision())
				&& (StringUtils.containsIgnoreCase(componentType, "hive")
						||StringUtils.containsIgnoreCase(componentType, "parquet"))){
			return Messages.PRECISION_MUST_NOT_BE_BLANK;
	    }else if(!(gridRow.getPrecision().matches("\\d+")) &&
	    		StringUtils.isNotBlank(gridRow.getPrecision())){
			return Messages.PRECISION_MUST_CONTAINS_NUMBER_ONLY_0_9;
		}else if((StringUtils.isBlank(gridRow.getScale()))){
			 return Messages.SCALE_MUST_NOT_BE_BLANK;
		}else if(!(gridRow.getScale().matches("\\d+")) || scale<0){
			return Messages.SCALE_SHOULD_BE_POSITIVE_INTEGER;
		}else if(StringUtils.equalsIgnoreCase(gridRow.getScaleTypeValue(),"none")){
			return Messages.SCALETYPE_MUST_NOT_BE_NONE;
		}
		else if(precision <= scale){
			return Messages.SCALE_MUST_BE_LESS_THAN_PRECISION;
		}
		return "";
	}
	
	
	private String setToolTipForDateSchemaRange(GenerateRecordSchemaGridRow generateRecordSchemaGridRow){
		
		Date rangeFromDate = null, rangeToDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat(generateRecordSchemaGridRow.getDateFormat());

		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeFrom())){
			try{
				rangeFromDate = formatter.parse(generateRecordSchemaGridRow.getRangeFrom());
			}catch (ParseException e){
				return Messages.RANGE_FROM_DATE_INCORRECT_PATTERN;
			}
		}

		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeTo())){
			try{
				rangeToDate = formatter.parse(generateRecordSchemaGridRow.getRangeTo());
			}catch (ParseException e){
				return Messages.RANGE_TO_DATE_INCORRECT_PATTERN;
			}
		}

		if (rangeFromDate != null && rangeToDate != null && rangeFromDate.after(rangeToDate)){
			return Messages.RANGE_FROM_GREATER_THAN_RANGE_TO;
		}
		
		return "";
	}
	
	
	private String setToolTipForBigDecimalSchemaRange(GenerateRecordSchemaGridRow generateRecordSchemaGridRow){
		BigDecimal rangeFrom = null, rangeTo = null;

		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeFrom())){
			rangeFrom = new BigDecimal(generateRecordSchemaGridRow.getRangeFrom());
		}
		
		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeTo())){
			rangeTo = new BigDecimal(generateRecordSchemaGridRow.getRangeTo());
		}

		if (rangeFrom != null && rangeTo != null){
			if (rangeFrom.compareTo(rangeTo) > 0){
				return Messages.RANGE_FROM_GREATER_THAN_RANGE_TO;
			}
		}

		if (!generateRecordSchemaGridRow.getLength().isEmpty()){
			int fieldLength = 0, scaleLength = 0;
			fieldLength = Integer.parseInt(generateRecordSchemaGridRow.getLength());

			if (!generateRecordSchemaGridRow.getScale().isEmpty()){
				scaleLength = Integer.parseInt(generateRecordSchemaGridRow.getScale());
			}

			if (fieldLength < 0){
				return Messages.FIELD_LENGTH_LESS_THAN_ZERO;
			}else if (scaleLength < 0){
				return Messages.FIELD_SCALE_LESS_THAN_ZERO;
			}else if (scaleLength >= fieldLength){
				return Messages.FIELD_SCALE_NOT_LESS_THAN_FIELD_LENGTH;
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
					minPermissibleRangeValue="0";
					maxPermissibleRangeValue="0";
				}
			
				BigDecimal minRangeValue = new BigDecimal(minPermissibleRangeValue);
				BigDecimal maxRangeValue = new BigDecimal(maxPermissibleRangeValue);

				return setToolTipIfSchemaRangeAndLengthIsInvalid(rangeFrom, fieldLength, rangeTo, minRangeValue, maxRangeValue);
			}
		}
		return "";
	}
		
	
	private String setToolTipForDoubleFloatIntegerLongShortSchemaRange(
			GenerateRecordSchemaGridRow generateRecordSchemaGridRow){
		BigDecimal rangeFrom = null, rangeTo = null;

		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeFrom()) && generateRecordSchemaGridRow.getRangeFrom().matches(REGULAR_EXPRESSION_FOR_NUMBER)){
			rangeFrom = new BigDecimal(generateRecordSchemaGridRow.getRangeFrom());
		}
		
		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeTo()) && generateRecordSchemaGridRow.getRangeTo().matches(REGULAR_EXPRESSION_FOR_NUMBER)){
			rangeTo = new BigDecimal(generateRecordSchemaGridRow.getRangeTo());
		}

		if (rangeFrom != null && rangeTo != null){
			if (rangeFrom.compareTo(rangeTo) > 0){
				return Messages.RANGE_FROM_GREATER_THAN_RANGE_TO;
			}
		}
		
		if (!generateRecordSchemaGridRow.getLength().isEmpty()){
			int fieldLength = Integer.parseInt(generateRecordSchemaGridRow.getLength());

			if (fieldLength < 0){
				return Messages.FIELD_LENGTH_LESS_THAN_ZERO;
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
			
				if(fieldLength == 0){
					minPermissibleRangeValue="0";
					maxPermissibleRangeValue="0";
				}
							
				BigDecimal minRangeValue = new BigDecimal(minPermissibleRangeValue);
				BigDecimal maxRangeValue = new BigDecimal(maxPermissibleRangeValue);
		
				return setToolTipIfSchemaRangeAndLengthIsInvalid(rangeFrom, fieldLength, rangeTo, minRangeValue, maxRangeValue);
			}
		}
		return "";
	}

	
	private String setToolTipIfSchemaRangeAndLengthIsInvalid(BigDecimal rangeFrom, int fieldLength, BigDecimal rangeTo,
			BigDecimal minRangeValue, BigDecimal maxRangeValue){
		
		if (rangeFrom != null && fieldLength > 0 && rangeTo != null){
			if (rangeFrom.compareTo(minRangeValue) < 0){
				return Messages.RANGE_FROM_LESS_THAN_MIN_PERMISSIBLE_VALUE;
			}else if (rangeTo.compareTo(maxRangeValue) > 0){
				return Messages.RANGE_TO_GREATER_THAN_MAX_PERMISSIBLE_VALUE;
			}
		}else if(rangeFrom != null && fieldLength > 0 && rangeTo == null){
			if (rangeFrom.compareTo(maxRangeValue) > 0){
				return Messages.RANGE_FROM_GREATER_THAN_MAX_PERMISSIBLE_VALUE;
			}
		}else if(rangeTo != null && fieldLength > 0 && rangeFrom == null){
			if (rangeTo.compareTo(minRangeValue) < 0){
				return Messages.RANGE_TO_LESS_THAN_MIN_PERMISSIBLE_VALUE;
			}
		}
		return "";
	}

	private String setToolTipForSchemaRange(GenerateRecordSchemaGridRow generateRecordSchemaGridRow){
		
		if (StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_UTIL_DATE)){
			return setToolTipForDateSchemaRange(generateRecordSchemaGridRow);
		}else if (StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL)){
			return setToolTipForBigDecimalSchemaRange(generateRecordSchemaGridRow);
		}else if (StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_DOUBLE)
				|| StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_FLOAT)
				|| StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_INTEGER)
				|| StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_LONG)
				|| StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_LANG_SHORT)){
			return setToolTipForDoubleFloatIntegerLongShortSchemaRange(generateRecordSchemaGridRow);
		}
		return "";
	}
	
	
	private String setToolTipForGenerateRecordGridRow(GenerateRecordSchemaGridRow generateRecordSchemaGridRow, String componentType){
		
		String tooltip = null;
		
		if (StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeFrom())
				|| StringUtils.isNotBlank(generateRecordSchemaGridRow.getRangeTo())){
			tooltip = setToolTipForSchemaRange(generateRecordSchemaGridRow);			
		}
		
		if (tooltip != null){
			return tooltip;
		}else if(StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_UTIL_DATE) 
				&& StringUtils.isBlank(generateRecordSchemaGridRow.getDateFormat())){
			return setToolTipForDateFormatIfBlank(generateRecordSchemaGridRow);
		}else if((StringUtils.equalsIgnoreCase(generateRecordSchemaGridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL))){
			return setToolTipForBigDecimal(generateRecordSchemaGridRow, componentType);			
		}
		
		return "";
	}
	
	
	private String setAppropriateToolTipMessage(TableItem item, String componentType){
		
		GridRow gridRow=(GridRow)item.getData();
		
		if(!gridRow.getFieldName().matches(Constants.REGEX)){
			return Messages.FIELDNAME_NOT_ALPHANUMERIC_ERROR;
		}
		if(gridRow instanceof FixedWidthGridRow){
			
			FixedWidthGridRow fixedWidthGridRow = (FixedWidthGridRow)gridRow;
			
			if(fixedWidthGridRow instanceof GenerateRecordSchemaGridRow){
				GenerateRecordSchemaGridRow generateRecordSchemaGridRow = (GenerateRecordSchemaGridRow) fixedWidthGridRow;
				return setToolTipForGenerateRecordGridRow(generateRecordSchemaGridRow, componentType);
			}else if(fixedWidthGridRow instanceof MixedSchemeGridRow){
				MixedSchemeGridRow mixedSchemeGridRow = (MixedSchemeGridRow) fixedWidthGridRow;
				return setToolTipForMixedSchemeGridRow(mixedSchemeGridRow, componentType);
			}else{
				
				if(StringUtils.isBlank(fixedWidthGridRow.getLength())){
					return Messages.LENGTH_MUST_NOT_BE_BLANK;
				}else if(!(StringUtils.isBlank(fixedWidthGridRow.getLength())) && !(fixedWidthGridRow.getLength().matches(REGULAR_EXPRESSION_FOR_NUMBER))){
					return Messages.LENGTH_MUST_BE_AN_INTEGER_VALUE;
				}
				else if(Integer.parseInt(fixedWidthGridRow.getLength())==0){
					return Messages.LENGTH_SHOULD_NOT_BE_ZERO;
				}
				
				if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_UTIL_DATE) 
						&& StringUtils.isBlank(gridRow.getDateFormat())){
					return setToolTipForDateFormatIfBlank(gridRow);
				}else if((StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL))){
					return setToolTipForBigDecimal(gridRow, componentType);
				}
				
				
			}
		}else if(gridRow instanceof BasicSchemaGridRow){
			if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_UTIL_DATE) 
					&& StringUtils.isBlank(gridRow.getDateFormat())){
				return setToolTipForDateFormatIfBlank(gridRow);
			}else if((StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL))){
				return setToolTipForBigDecimal(gridRow, componentType);			
			}

		}
		else if(gridRow instanceof XPathGridRow){
		    if(StringUtils.isBlank(((XPathGridRow) gridRow).getXPath())){
				return Messages.X_PATH_MUST_NOT_BE_BLANK;
			}
		    else if(StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_UTIL_DATE) 
					&& StringUtils.isBlank(gridRow.getDateFormat())){
				return setToolTipForDateFormatIfBlank(gridRow);
			}else if((StringUtils.equalsIgnoreCase(gridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL))){
				return setToolTipForBigDecimal(gridRow, componentType);			
			}
			else if(item.getData(Constants.ERROR_MESSAGE)!=null && StringUtils.isNotBlank((String)item.getData(Constants.ERROR_MESSAGE))){
				return (String)item.getData(Constants.ERROR_MESSAGE);
			}
        }
		return "";
	}
	

	private String setToolTipForMixedSchemeGridRow(MixedSchemeGridRow mixedSchemeGridRow, String componentType){
		
		if(StringUtils.isBlank(mixedSchemeGridRow.getDelimiter())
				&& StringUtils.isBlank(mixedSchemeGridRow.getLength())){
			return Messages.LENGTH_OR_DELIMITER_MUST_NOT_BE_BLANK;
		}else if(StringUtils.isNotBlank(mixedSchemeGridRow.getLength()) && !(mixedSchemeGridRow.getLength().matches(REGULAR_EXPRESSION_FOR_NUMBER))){
			return Messages.LENGTH_MUST_BE_AN_INTEGER_VALUE;
		}else if(StringUtils.isNotBlank(mixedSchemeGridRow.getDelimiter()) 
				&& StringUtils.isNotBlank(mixedSchemeGridRow.getLength())){
			return Messages.ONLY_SPECIFY_LENGTH_OR_DELIMITER;
		}
		else if(StringUtils.isNotBlank(mixedSchemeGridRow.getLength()) && Integer.parseInt(mixedSchemeGridRow.getLength())==0){
			return Messages.LENGTH_SHOULD_NOT_BE_ZERO;
		}
		
		if(StringUtils.equalsIgnoreCase(mixedSchemeGridRow.getDataTypeValue(), JAVA_UTIL_DATE) 
				&& StringUtils.isBlank(mixedSchemeGridRow.getDateFormat())){
			return setToolTipForDateFormatIfBlank(mixedSchemeGridRow);
		}else if((StringUtils.equalsIgnoreCase(mixedSchemeGridRow.getDataTypeValue(), JAVA_MATH_BIG_DECIMAL))){
			return setToolTipForBigDecimal(mixedSchemeGridRow, componentType);			
		}
		
		return "";
	}
}