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
package hydrograph.ui.dataviewer.filter;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import com.google.gson.Gson;

import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.Fields;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.dataviewer.adapters.DataViewerAdapter;
import hydrograph.ui.dataviewer.constants.AdapterConstants;
import hydrograph.ui.dataviewer.constants.Messages;
import hydrograph.ui.dataviewer.utilities.DataViewerUtility;
import hydrograph.ui.dataviewer.utilities.ViewDataSchemaHelper;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class FilterHelper.
 * Helper class for Filter Window
 * @author Bitwise
 *
 */
public class FilterHelper {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(FilterHelper.class);

	public static final FilterHelper INSTANCE = new FilterHelper();
	private DataViewerAdapter dataViewerAdapter;
	private DebugDataViewer debugDataViewer;
	private FilterConditionsDialog filterConditionsDialog;
	private String SCHEMA_FILE_EXTENTION=".xml";
	private String localCondition = "";
	
	/**
	 * Sets the local condition.
	 * 
	 * @param localCondition
	 *            the new local condition
	 */
	public void setLocalCondition(String localCondition) {
		this.localCondition = localCondition;
	}

	/**
	 * Sets the remote condition.
	 * 
	 * @param remoteCondition
	 *            the new remote condition
	 */
	public void setRemoteCondition(String remoteCondition) {
		this.remoteCondition = remoteCondition;
	}

	private String remoteCondition;
	
	private FilterHelper() {
	}
	
	/**
	 * Gets the type based operator map.
	 * 
	 * @return the type based operator map
	 */
	public Map<String, String[]> getTypeBasedOperatorMap(){
		Map<String, String[]> typeBasedConditionalOperators = new HashMap<String, String[]>();
		String[] NUMERIC_CONDITIONS = new String[]{FilterConstants.GREATER_THAN, FilterConstants.FIELD_GREATER_THAN,
				FilterConstants.LESS_THAN, FilterConstants.FIELD_LESS_THAN,
				FilterConstants.LESS_THAN_EQUALS, FilterConstants.FIELD_LESS_THAN_EQUALS,
				FilterConstants.GREATER_THAN_EQUALS,FilterConstants.FIELD_GREATER_THAN_EQUALS,
				FilterConstants.NOT_EQUALS,FilterConstants.FIELD_NOT_EQUALS,
				FilterConstants.EQUALS,FilterConstants.FIELD_EQUALS,
				FilterConstants.IN, FilterConstants.NOT_IN,
				FilterConstants.BETWEEN,
				FilterConstants.BETWEEN_FIELD};

		typeBasedConditionalOperators.put(FilterConstants.TYPE_STRING, new String[]{FilterConstants.NOT_EQUALS, FilterConstants.EQUALS, 
				FilterConstants.LIKE, FilterConstants.NOT_LIKE, FilterConstants.IN, FilterConstants.NOT_IN}); 
		typeBasedConditionalOperators.put(FilterConstants.TYPE_BOOLEAN, new String[]{FilterConstants.NOT_EQUALS, FilterConstants.EQUALS});
		
		typeBasedConditionalOperators.put(FilterConstants.TYPE_INTEGER, NUMERIC_CONDITIONS); 
		typeBasedConditionalOperators.put(FilterConstants.TYPE_DATE, NUMERIC_CONDITIONS); 
		typeBasedConditionalOperators.put(FilterConstants.TYPE_BIGDECIMAL, NUMERIC_CONDITIONS);
		typeBasedConditionalOperators.put(FilterConstants.TYPE_LONG, NUMERIC_CONDITIONS);
		typeBasedConditionalOperators.put(FilterConstants.TYPE_SHORT, NUMERIC_CONDITIONS);
		typeBasedConditionalOperators.put(FilterConstants.TYPE_FLOAT, NUMERIC_CONDITIONS);
		typeBasedConditionalOperators.put(FilterConstants.TYPE_DOUBLE, NUMERIC_CONDITIONS);
		return typeBasedConditionalOperators;
	}
	
	/**
	 * Gets the text box value 1 listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param saveButton
	 *            the save button
	 * @param displayButton
	 *            the display button
	 * @return the text box value 1 listener
	 */
	public  Listener getTextBoxValue1Listener(final List<Condition> conditionsList, 
			final Map<String, String> fieldsAndTypes, final String[] fieldNames, final Button saveButton, final Button displayButton) {
		Listener listener = new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				Text text = (Text)event.widget;
				int index = (int) text.getData(FilterConstants.ROW_INDEX);
				Condition filterConditions = conditionsList.get(index);
				filterConditions.setValue1(text.getText());
				validateText(text, filterConditions.getFieldName(), fieldsAndTypes, filterConditions.getConditionalOperator());
				toggleSaveDisplayButton(conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
			}
		};
		return listener;
	}
	
	/**
	 * Gets the text box value 2 listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param saveButton
	 *            the save button
	 * @param displayButton
	 *            the display button
	 * @return the text box value 2 listener
	 */
	public  Listener getTextBoxValue2Listener(final List<Condition> conditionsList, 
			final Map<String, String> fieldsAndTypes, final String[] fieldNames, final Button saveButton, final Button displayButton) {
		Listener listener = new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				Text text = (Text)event.widget;
				int index = (int) text.getData(FilterConstants.ROW_INDEX);
				Condition filterConditions = conditionsList.get(index);
				filterConditions.setValue2(text.getText());
				validateText(text, filterConditions.getFieldName(), fieldsAndTypes,filterConditions.getConditionalOperator());
				toggleSaveDisplayButton(conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
			}
		};
		return listener;
	}
	
	/**
	 * Gets the local condition.
	 * 
	 * @return the local condition
	 */
	public String getLocalCondition() {
		return localCondition;
	}

	/**
	 * Gets the remote condition.
	 * 
	 * @return the remote condition
	 */
	public String getRemoteCondition() {
		return remoteCondition;
	}
	
	private void toggleSaveDisplayButton(final List<Condition> conditionsList, final Map<String, String> fieldsAndTypes,
			final String[] fieldNames, final Button saveButton, final Button displayButton) {
		if(FilterValidator.INSTANCE.isAllFilterConditionsValid(conditionsList, fieldsAndTypes, fieldNames,debugDataViewer)){
			saveButton.setEnabled(true);
			displayButton.setEnabled(true);
		}
		else{
			saveButton.setEnabled(false);
			displayButton.setEnabled(false);
		}
	}
	
	/**
	 * Gets the field name selection listener.
	 * 
	 * @param tableViewer
	 *            the table viewer
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param saveButton
	 *            the save button
	 * @param displayButton
	 *            the display button
	 * @return the field name selection listener
	 */
	public SelectionListener getFieldNameSelectionListener(final TableViewer tableViewer, final List<Condition> conditionsList,
			final Map<String, String> fieldsAndTypes, final String[] fieldNames, final Button saveButton, final Button displayButton) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				CCombo source = (CCombo) e.getSource();
				int index = (int) source.getData(FilterConstants.ROW_INDEX);
				Condition filterConditions = conditionsList.get(index);
				String fieldName = source.getText();
				filterConditions.setFieldName(fieldName);
				
				if(StringUtils.isNotBlank(fieldName)){
					String fieldType = fieldsAndTypes.get(fieldName);
					TableItem item = tableViewer.getTable().getItem(index);
					CCombo conditionalCombo = (CCombo) item.getData(FilterConditionsDialog.CONDITIONAL_OPERATORS);
					String[] items = FilterHelper.INSTANCE.getTypeBasedOperatorMap().get(fieldType);
					//if the current item is not in the item list, reset it
					if(!Arrays.asList(items).contains(conditionalCombo.getText())){
						conditionalCombo.setText("");
					}
					conditionalCombo.setItems(items);
					new AutoCompleteField(conditionalCombo, new CComboContentAdapter(), conditionalCombo.getItems());
				}
				validateCombo(source);
				toggleSaveDisplayButton(conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
		return listener;
	}
	
	/**
	 * Gets the field name modify listener.
	 * 
	 * @param tableViewer
	 *            the table viewer
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param saveButton
	 *            the save button
	 * @param displayButton
	 *            the display button
	 * @return the field name modify listener
	 */
	public ModifyListener getFieldNameModifyListener(final TableViewer tableViewer, final List<Condition> conditionsList,
			final Map<String, String> fieldsAndTypes, final String[] fieldNames, final Button saveButton, final Button displayButton) {
		ModifyListener listener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				CCombo source = (CCombo) e.getSource();
				int index = (int) source.getData(FilterConstants.ROW_INDEX);
				Condition filterConditions = conditionsList.get(index);
				String fieldName = source.getText();
				filterConditions.setFieldName(fieldName);
				
				if(StringUtils.isNotBlank(fieldName)){
					String fieldType = fieldsAndTypes.get(fieldName);
					TableItem item = tableViewer.getTable().getItem(index);
					CCombo conditionalCombo = (CCombo) item.getData(FilterConditionsDialog.CONDITIONAL_OPERATORS);
					if(conditionalCombo != null && StringUtils.isNotBlank(fieldType)){
						conditionalCombo.setText(filterConditions.getConditionalOperator());
						conditionalCombo.setItems(FilterHelper.INSTANCE.getTypeBasedOperatorMap().get(fieldType));
						new AutoCompleteField(conditionalCombo, new CComboContentAdapter(), conditionalCombo.getItems());
					}
				}
				validateCombo(source);
				toggleSaveDisplayButton(conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
			}
		};
		return listener;
	}
	
	/**
	 * Gets the conditional operator selection listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param saveButton
	 *            the save button
	 * @param displayButton
	 *            the display button
	 * @return the conditional operator selection listener
	 */
	public SelectionListener getConditionalOperatorSelectionListener(final List<Condition> conditionsList, 
			final Map<String, String> fieldsAndTypes, final String[] fieldNames, final Button saveButton, final Button displayButton) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				CCombo source = (CCombo) e.getSource();
				TableItem tableItem = getTableItem(source);
				Text text2 = (Text) tableItem.getData(FilterConstants.VALUE2TEXTBOX);
				Text text1 = (Text) tableItem.getData(FilterConstants.VALUE1TEXTBOX);
				String selectedValue = source.getItem(source.getSelectionIndex());
				showToolTip(text1, selectedValue);
				enableAndDisableValue2TextBox(selectedValue, text2);
				processConditionalOperator(source, conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
			}
		
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
		return listener;
	}
	
	private void showToolTip(Text text1, String selectedValue) {
		if (StringUtils.equalsIgnoreCase(selectedValue, FilterConstants.IN)
				|| StringUtils.equalsIgnoreCase(selectedValue, FilterConstants.NOT_IN)) {
			text1.setToolTipText(Messages.COMMA_SEPERATED_VALUE);
		} else {
			text1.setToolTipText("");
		}
	}
	
	private TableItem getTableItem(CCombo source) {
		TableEditor tableEditor = (TableEditor) source.getData(FilterConstants.CONDITIONAL_EDITOR);
		TableItem tableItem = tableEditor.getItem();
		return tableItem;
	}
	
	/**
	 * Gets the conditional operator modify listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param saveButton
	 *            the save button
	 * @param displayButton
	 *            the display button
	 * @return the conditional operator modify listener
	 */
	public ModifyListener getConditionalOperatorModifyListener(final List<Condition> conditionsList, 
			final Map<String, String> fieldsAndTypes, final String[] fieldNames, final Button saveButton, final Button displayButton) {
		ModifyListener listener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				CCombo source = (CCombo) e.getSource();
				TableItem tableItem = getTableItem(source);
				Condition condition = (Condition) tableItem.getData();
				if (tableItem.getData(FilterConstants.VALUE2TEXTBOX) != null) {
					Text text = (Text) tableItem.getData(FilterConstants.VALUE2TEXTBOX);
					enableAndDisableValue2TextBox(condition.getConditionalOperator(), text);
				}
				processConditionalOperator(source, conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
			}
		};
		return listener;
	}
	
	private void enableAndDisableValue2TextBox(String condition, Text text) {
		if(StringUtils.equalsIgnoreCase(condition,FilterConstants.BETWEEN)
				|| StringUtils.equalsIgnoreCase(condition,FilterConstants.BETWEEN_FIELD)){
			text.setVisible(true);
		} else {
			text.setVisible(false);
		}
	}
	
	/**
	 * Returns focus-listener object
	 * @return
	 */
	public FocusListener getConditionalOperatorFocusListener() {
		FocusListener focusListener = new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (e.getSource() instanceof CCombo) {
					CCombo source = (CCombo) e.getSource();
					for (String option : source.getItems()) {
						if (StringUtils.equalsIgnoreCase(source.getText(), option)) {
							source.setText(option);
							break;
						}
					}
				}
			}

			@Override
			public void focusGained(FocusEvent e) { /* do nothing. */
			}
		};
		return focusListener;
	}
	
	private void processConditionalOperator(CCombo source, List<Condition> conditionsList, Map<String, String> fieldsAndTypes,
			String[] fieldNames, Button saveButton, Button displayButton){
		int index = (int) source.getData(FilterConstants.ROW_INDEX);
		Condition filterConditions = conditionsList.get(index);
		filterConditions.setConditionalOperator(source.getText());
		validateCombo(source);
		toggleSaveDisplayButton(conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
	}
	
	/**
	 * Gets the relational op selection listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param saveButton
	 *            the save button
	 * @param displayButton
	 *            the display button
	 * @return the relational op selection listener
	 */
	public SelectionListener getRelationalOpSelectionListener(final List<Condition> conditionsList,  
			final Map<String, String> fieldsAndTypes, final String[] fieldNames, final Button saveButton, final Button displayButton) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				CCombo source = (CCombo) e.getSource();
				processRelationalOperator(source, conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
		return listener;
	}
	
	/**
	 * Gets the relational op modify listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param saveButton
	 *            the save button
	 * @param displayButton
	 *            the display button
	 * @return the relational op modify listener
	 */
	public ModifyListener getRelationalOpModifyListener(final List<Condition> conditionsList,  
			final Map<String, String> fieldsAndTypes, final String[] fieldNames, final Button saveButton, final Button displayButton) {
		ModifyListener listener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				CCombo source = (CCombo) e.getSource();
				processRelationalOperator(source, conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
			}
			
		};
		return listener;
	}
	
	private void processRelationalOperator(CCombo source, List<Condition> conditionsList, Map<String, String> fieldsAndTypes,
			String[] fieldNames, Button saveButton, Button displayButton){
		int index = (int) source.getData(FilterConstants.ROW_INDEX);
		Condition filterConditions = conditionsList.get(index);
		filterConditions.setRelationalOperator(source.getText());
		if(index != 0){
			validateCombo(source);
		}
		toggleSaveDisplayButton(conditionsList, fieldsAndTypes, fieldNames, saveButton, displayButton);
	}
	
	/**
	 * Adds the button listener.
	 * 
	 * @param tableViewer
	 *            the table viewer
	 * @param conditionsList
	 *            the conditions list
	 * @param dummyList
	 *            the dummy list
	 * @param groupSelectionMap
	 *            the group selection map
	 * @return the selection listener
	 */
	public SelectionListener addButtonListener(final TableViewer tableViewer, final List<Condition> conditionsList, 
			final List<Condition> dummyList, final TreeMap<Integer, List<List<Integer>>> groupSelectionMap) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.getSource();
				int index = (int) button.getData(FilterConstants.ROW_INDEX);
				conditionsList.add(index, new Condition());
				dummyList.clear();
				dummyList.addAll(cloneList(conditionsList));
				FilterHelper.INSTANCE.refreshGroupSelections(tableViewer, index, "ADD", groupSelectionMap);
				tableViewer.refresh();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
		return listener;
	}
	
	
	/**
	 * Gets the save button listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param groupSelectionMap
	 *            the group selection map
	 * @param dataset
	 *            the dataset
	 * @param originalFilterConditions
	 *            the original filter conditions
	 * @param retainRemoteFilter
	 *            the retain remote filter
	 * @param retainLocalFilter
	 *            the retain local filter
	 * @return the save button listener
	 */
	public SelectionListener getSaveButtonListener(final List<Condition> conditionsList, final Map<String, String> fieldsAndTypes,
			final Map<Integer,List<List<Integer>>> groupSelectionMap, final String dataset,final FilterConditions originalFilterConditions,
			final RetainFilter retainRemoteFilter,final RetainFilter retainLocalFilter) {
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
			
				StringBuffer buffer = getCondition(conditionsList, fieldsAndTypes, groupSelectionMap,false);
				
				logger.debug("Query String : " + buffer);
				if(dataset.equalsIgnoreCase(Messages.DOWNLOADED)){	
					localCondition=buffer.toString();
					showLocalFilteredData(StringUtils.trim(buffer.toString()));
					debugDataViewer.setLocalCondition(localCondition);
				}
				else{
					if(!retainLocalFilter.getRetainFilter()){
						if(!debugDataViewer.getLocalCondition().equals("")){
							MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
							messageBox.setText("Warning");
							messageBox.setMessage(Messages.NOT_RETAINED);
							int response = messageBox.open();
							if (response != SWT.OK) {
								return;
							}
						}
					}
					else{
						if(!debugDataViewer.getLocalCondition().equals("")){
							MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
							messageBox.setText("Warning");
							messageBox.setMessage(Messages.RETAINED);
							int response = messageBox.open();
							if (response != SWT.OK) {
								return;
							}
						}else{
							retainLocalFilter.setRetainFilter(false);
						}
					}
					if (!retainLocalFilter.getRetainFilter()) {
						originalFilterConditions.setLocalCondition("");
						originalFilterConditions.getLocalConditions().clear();
						filterConditionsDialog.getLocalConditionsList().clear();
						debugDataViewer.setLocalCondition("");
						localCondition = "";
					}
					remoteCondition=buffer.toString();
					showRemoteFilteredData(StringUtils.trim(buffer.toString()));
					debugDataViewer.setRemoteCondition(remoteCondition);
				}
				filterConditionsDialog.close();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
		return listener;
	}
	
	
	private void showRemoteFilteredData(String buffer) {
			debugDataViewer.downloadDebugFiles(true,true);
			debugDataViewer.setOverWritten(true);
	}

	private void showLocalFilteredData(String buffer) {
		try {
			dataViewerAdapter.setFilterCondition(buffer);
			dataViewerAdapter.resetOffset();
			dataViewerAdapter.initializeTableData();
			debugDataViewer.clearJumpToText();
			debugDataViewer.submitRecordCountJob();
			debugDataViewer.getDataViewLoader().updateDataViewLists();
			debugDataViewer.getDataViewLoader().reloadloadViews();
			
			enableAndDisableNextButtonOfDataViewer();
			
		} catch (SQLException|IOException exception) {
			logger.error("Error occuring while showing local filtered data",exception);
		}
	}

	private void enableAndDisableNextButtonOfDataViewer() {
			int pageSize = debugDataViewer.getViewDataPreferences().getPageSize();
			if (dataViewerAdapter.getRowCount() < pageSize) {
				debugDataViewer.getStatusManager().enableNextPageButton(false);
			} else {
				debugDataViewer.getStatusManager().enableNextPageButton(true);
			}
	}

	/**
	 * Creates the json object for remote filter.
	 * 
	 * @param buffer
	 *            the buffer
	 * @return the string
	 */
	public String createJsonObjectForRemoteFilter(String buffer) {
		Gson gson=new Gson();
		RemoteFilterJson remoteFilterJson = new RemoteFilterJson(buffer,
				DataViewerUtility.INSTANCE.getSchema(debugDataViewer.getDebugFileLocation() + 
						debugDataViewer.getDebugFileName()+SCHEMA_FILE_EXTENTION), 
						debugDataViewer.getViewDataPreferences().getFileSize(), debugDataViewer.getJobDetails());
		
		String filterJson=gson.toJson(remoteFilterJson);
		return filterJson;
	}
	

	/**
	 * Sets the data viewer adapter.
	 * 
	 * @param dataViewerAdapter
	 *            the data viewer adapter
	 * @param filterConditionsDialog
	 *            the filter conditions dialog
	 */
	public void setDataViewerAdapter(DataViewerAdapter dataViewerAdapter, FilterConditionsDialog filterConditionsDialog) {
		this.dataViewerAdapter=dataViewerAdapter;
		this.filterConditionsDialog=filterConditionsDialog;
	}

	/**
	 * Sets the debug data viewer.
	 * 
	 * @param debugDataViewer
	 *            the new debug data viewer
	 */
	public void setDebugDataViewer(DebugDataViewer debugDataViewer) {
		this.debugDataViewer=debugDataViewer;
	}
	
	/**
	 * Gets the condition value.
	 * 
	 * @param fieldName
	 *            the field name
	 * @param value
	 *            the value
	 * @param conditional
	 *            the conditional
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param isDisplayPressed
	 *            the is display pressed
	 * @return the condition value
	 */
	protected String getConditionValue(String fieldName, String value, String conditional, Map<String, String> fieldsAndTypes, boolean isDisplayPressed) {
		String trimmedCondition = StringUtils.trim(conditional);
		String dataType = fieldsAndTypes.get(fieldName);
		if((FilterConstants.TYPE_STRING.equalsIgnoreCase(dataType) || FilterConstants.TYPE_DATE.equalsIgnoreCase(dataType) 
				|| FilterConstants.TYPE_BOOLEAN.equalsIgnoreCase(dataType)) && !conditional.endsWith("(Field)")){
			if(FilterConstants.IN.equalsIgnoreCase(trimmedCondition) || FilterConstants.NOT_IN.equalsIgnoreCase(trimmedCondition)){
				if(StringUtils.isNotBlank(value) && value.contains(FilterConstants.DELIM_COMMA)){
					StringTokenizer tokenizer = new StringTokenizer(value, FilterConstants.DELIM_COMMA);
					StringBuffer temp = new StringBuffer();
					int numberOfTokens = tokenizer.countTokens();
					temp.append(FilterConstants.OPEN_BRACKET); 
					for (int index = 0; index < numberOfTokens; index++) {
						if (FilterConstants.TYPE_DATE.equalsIgnoreCase(dataType) && !isDisplayPressed) {
							try {
								Fields dataViewerFileSchema = getSchema();
								SimpleDateFormat formatter = getDateFormatter(fieldName, dataViewerFileSchema);
								Date parsedDate = getParsedDate(tokenizer.nextToken(), formatter);
								temp.append(FilterConstants.SINGLE_QOUTE).append(formatter.format(parsedDate))
										.append(FilterConstants.SINGLE_QOUTE);
							} catch (ParseException parseException) {
								logger.error("Error while parsing date value", parseException);
							}
						}
						else
						{
							temp.append(FilterConstants.SINGLE_QOUTE).append(tokenizer.nextToken())
							.append(FilterConstants.SINGLE_QOUTE);
						}
						if(index < numberOfTokens - 1){
							temp.append(FilterConstants.DELIM_COMMA);
						}
					}
					temp.append(FilterConstants.CLOSE_BRACKET);
					return temp.toString();
				}
				else{
					return FilterConstants.OPEN_BRACKET + FilterConstants.SINGLE_QOUTE 
							+ value + FilterConstants.SINGLE_QOUTE + FilterConstants.CLOSE_BRACKET;
				}
			}
			else{
				if (!isDisplayPressed) {
					if (FilterConstants.TYPE_DATE.equalsIgnoreCase(dataType)) {
						try {
							Fields dataViewerFileSchema = getSchema();
							SimpleDateFormat formatter = getDateFormatter(fieldName, dataViewerFileSchema);
							Date parsedDate = getParsedDate(value, formatter);
							return FilterConstants.SINGLE_QOUTE + formatter.format(parsedDate)
									+ FilterConstants.SINGLE_QOUTE;
						} catch (ParseException parseException) {
							logger.error("Error while parsing date value",parseException);
						}
					} else {
						return FilterConstants.SINGLE_QOUTE + value + FilterConstants.SINGLE_QOUTE;
					}
					return null;
				} else {
					return FilterConstants.SINGLE_QOUTE + value + FilterConstants.SINGLE_QOUTE;
				}
				
			}
		}
		else{
			if(FilterConstants.IN.equalsIgnoreCase(trimmedCondition) || FilterConstants.NOT_IN.equalsIgnoreCase(trimmedCondition)){
				return FilterConstants.OPEN_BRACKET + value + FilterConstants.CLOSE_BRACKET;
			}
			else{
				return value;
			}
		}
	}

	private Date getParsedDate(String nextToken, SimpleDateFormat formatter) throws ParseException {
		Date parsedDate = formatter.parse(nextToken);
		formatter.applyPattern(AdapterConstants.DATE_FORMAT);
		return parsedDate;
	}

	private SimpleDateFormat getDateFormatter(String fieldName, Fields dataViewerFileSchema) {
		SimpleDateFormat formatter = null;
		for (Field field : dataViewerFileSchema.getField()) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				formatter = new SimpleDateFormat(field.getFormat());
			}
		}
		return formatter;
	}

	private Fields getSchema() {
		String debugFileName = debugDataViewer.getDebugFileName();
		String debugFileLocation = debugDataViewer.getDebugFileLocation();
		Fields dataViewerFileSchema = ViewDataSchemaHelper.INSTANCE
				.getFieldsFromSchema(debugFileLocation + debugFileName
						+ AdapterConstants.SCHEMA_FILE_EXTENTION);
		return dataViewerFileSchema;
	}

	private boolean validateCombo(CCombo combo){
		if((Arrays.asList(combo.getItems())).contains(combo.getText())){
			combo.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			return true;
		}else {
			combo.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 244, 113));
			return false;
		}
	}
	
	private boolean validateText(Text text, String fieldName, Map<String, String> fieldsAndTypes, String conditionalOperator) {
		String type = FilterValidator.INSTANCE.getType(fieldName, fieldsAndTypes);
		if((StringUtils.isNotBlank(text.getText()) && FilterValidator.INSTANCE.validateDataBasedOnTypes(type, text.getText(), conditionalOperator,debugDataViewer,fieldName)) ||
				FilterValidator.INSTANCE.validateField(fieldsAndTypes,text.getText(),fieldName)){
			text.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			return true;
		}else {
			text.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 244, 113));
			return false;
		}
	}

	/**
	 * Clone list.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @return the list
	 */
	public List<Condition> cloneList(List<Condition> conditionsList) {
		List<Condition> tempList = new ArrayList<>();
		for (Condition condition : conditionsList) {
			Condition newCondition = new Condition();
			tempList.add(newCondition.copy(condition));
		}
		return tempList;
	}

	/**
	 * Gets the remote display button listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param groupSelectionMap
	 *            the group selection map
	 * @param styledTextRemote
	 *            the styled text remote
	 * @return the remote display button listener
	 */
	public SelectionListener getRemoteDisplayButtonListener(final List<Condition> conditionsList, final Map<String, String> fieldsAndTypes,
			final Map<Integer,List<List<Integer>>> groupSelectionMap,final StyledText styledTextRemote) {
		SelectionListener listner = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				styledTextRemote.setText(getCondition(conditionsList,fieldsAndTypes, groupSelectionMap,true).toString());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		return listner;
	}

	/**
	 * Gets the local display button listener.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param groupSelectionMap
	 *            the group selection map
	 * @param styledTextLocal
	 *            the styled text local
	 * @return the local display button listener
	 */
	public SelectionListener getLocalDisplayButtonListener(final List<Condition> conditionsList, final Map<String, String> fieldsAndTypes,
			final Map<Integer,List<List<Integer>>> groupSelectionMap,final StyledText styledTextLocal) {
		SelectionListener listner = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				styledTextLocal.setText(getCondition(conditionsList,fieldsAndTypes, groupSelectionMap,true).toString());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		return listner;
	}
	
	/**
	 * Gets the condition.
	 * 
	 * @param conditionsList
	 *            the conditions list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param groupSelectionMap
	 *            the group selection map
	 * @param isDisplayPressed
	 *            the is display pressed
	 * @return the condition
	 */
	public StringBuffer getCondition(final List<Condition> conditionsList, final Map<String, String> fieldsAndTypes,
			final Map<Integer, List<List<Integer>>> groupSelectionMap,boolean isDisplayPressed) {
		
		//put number of elements in the list
		//1 2 3 4 5
		List<String> actualStringList = new LinkedList<>();
		for (int conditionIndex = 0; conditionIndex < conditionsList.size(); conditionIndex++) {
			actualStringList.add(conditionIndex, String.valueOf((conditionIndex)));
		}
		logger.trace(actualStringList.toString());
		//start adding brackets for grouping
		Set<Integer> treeSet  = (Set<Integer>) groupSelectionMap.keySet();
		if(treeSet.size() > 0){
			for (Integer position : treeSet) {
			List<List<Integer>> groupsInColumn = groupSelectionMap.get(position);
				for (int groupIndex = 0; groupIndex < groupsInColumn.size(); groupIndex++) {
					List<Integer> group = groupsInColumn.get(groupIndex);
					//add opening bracket before first element in the group
					if(!group.isEmpty()){
					Integer firstItem = group.get(0);
					Integer firstItemIndex = actualStringList.indexOf(String.valueOf(firstItem));
					actualStringList.add(firstItemIndex, FilterConstants.OPEN_BRACKET);
					//add closing bracket after last element in the group							
					Integer lastItem = group.get(group.size()-1);
					Integer lastItemIndex = actualStringList.indexOf(String.valueOf(lastItem));
					actualStringList.add(lastItemIndex + 1, FilterConstants.CLOSE_BRACKET);
					}
				}
			}
		}
		
		//start adding relational operators
		int indexOfRelational = 1;
		//start from 2nd index
		for (int item = 1; item < conditionsList.size(); item++) {
			int indexOfItem = actualStringList.indexOf(String.valueOf(item));
			while(true){
				if((actualStringList.get(indexOfItem-1)).matches(FilterConstants.REGEX_DIGIT) 
						||(actualStringList.get(indexOfItem-1)).equalsIgnoreCase(FilterConstants.CLOSE_BRACKET)){
					actualStringList.add(indexOfItem, conditionsList.get(indexOfRelational).getRelationalOperator());
					break;
				}else{
					indexOfItem = indexOfItem - 1;
				}
			}
			indexOfRelational += 1;
			logger.trace(actualStringList.toString());
		}
		
		StringBuffer buffer = new StringBuffer();
		for(int item = 0; item < conditionsList.size(); item++){
			StringBuffer conditionString = new StringBuffer();
			
			Condition condition = conditionsList.get(item);
			if(StringUtils.equalsIgnoreCase(condition.getConditionalOperator(), FilterConstants.BETWEEN)
					|| StringUtils.equalsIgnoreCase(condition.getConditionalOperator(),FilterConstants.BETWEEN_FIELD)){
				conditionString
						.append(condition.getFieldName())
						.append(FilterConstants.SINGLE_SPACE)
						.append(condition.getConditionalOperator())
						.append(FilterConstants.SINGLE_SPACE)
						.append(getConditionValue(condition.getFieldName(), condition.getValue1(),
								condition.getConditionalOperator(), fieldsAndTypes,isDisplayPressed))
						.append(FilterConstants.SINGLE_SPACE)
						.append(FilterConstants.AND)
						.append(FilterConstants.SINGLE_SPACE)
						.append(getConditionValue(condition.getFieldName(), condition.getValue2(),
								condition.getConditionalOperator(), fieldsAndTypes,isDisplayPressed));
			} else {
				conditionString
						.append(condition.getFieldName())
						.append(FilterConstants.SINGLE_SPACE)
						.append(condition.getConditionalOperator())
						.append(FilterConstants.SINGLE_SPACE)
						.append(getConditionValue(condition.getFieldName(), condition.getValue1(),
								condition.getConditionalOperator(), fieldsAndTypes,isDisplayPressed));
			}
			int index = actualStringList.indexOf(String.valueOf(item));
			actualStringList.set(index, conditionString.toString());
		}
		
		for (String item : actualStringList) {
			buffer.append(item + FilterConstants.SINGLE_SPACE);
		}
		
		Pattern p = Pattern.compile("\\(Field\\)");
		Matcher m = p.matcher(buffer);
		StringBuffer temp = new StringBuffer();
		while(m.find()){
			m.appendReplacement(temp, "");
		}
		m.appendTail(temp);
		buffer = new StringBuffer(temp);
		return buffer;
	}
	
	
	/**
	 * Gets the retain button listener.
	 * 
	 * @param retainFilter
	 *            the retain filter
	 * @return the retain button listener
	 */
	public SelectionListener getRetainButtonListener(final RetainFilter retainFilter) {
		SelectionListener listner = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button)e.getSource();
				retainFilter.setRetainFilter(button.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		return listner;
	}
   
	/**
	 * Gets the adds-at-the-end listener.
	 * 
	 * @param tableViewer
	 *            the table viewer
	 * @param conditionList
	 *            the condition list
	 * @param dummyList
	 *            the dummy list
	 * @return the adds the at end listener
	 */
	public SelectionAdapter getAddAtEndListener(final TableViewer tableViewer, final List<Condition> conditionList, 
			final List<Condition> dummyList) {
	    return new SelectionAdapter() {
	          @Override
	          public void widgetSelected(SelectionEvent e) {
	                conditionList.add(conditionList.size(), new Condition());
	                dummyList.clear();
					dummyList.addAll(cloneList(conditionList));
	                tableViewer.refresh();
	          }
	    };
	}
	
	/**
	 * Check button listener.
	 * 
	 * @param tableViewer
	 *            the table viewer
	 * @param conditionsList
	 *            the conditions list
	 * @param btnAddGrp
	 *            the btn add grp
	 * @return the selection listener
	 */
	public SelectionListener checkButtonListener(final TableViewer tableViewer,
			final List<Condition> conditionsList, final Button btnAddGrp) {
		SelectionListener listener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int count=0;
				boolean isEnabled=false;
				List<Integer> selectionPattern= new ArrayList<>();
				TableItem[] items = tableViewer.getTable().getItems();
				
				   for (TableItem tableItem : items) {
					   Button button = (Button) tableItem.getData(FilterConditionsDialog.GROUP_CHECKBOX);
					   if(button.getSelection()){
					     count++;
					     selectionPattern.add(tableViewer.getTable().indexOf(tableItem));				     
					   }
				   }
				if(count>=2){
					if(validateCheckSequence(selectionPattern)){
						isEnabled=true;
					}						
				}
				btnAddGrp.setEnabled(isEnabled);	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		};
		return listener;
	}
	
	
	
	private boolean validateCheckSequence(List<Integer> selectionPattern){
		boolean retval=true;
		for(int i=0;i<selectionPattern.size()-1;i++){
			if((selectionPattern.get(i+1)-selectionPattern.get(i))>1){
				retval=false;
				break;
			}
		}
		return retval;
	}
	
	/**
	 * Validate user group selection.
	 * 
	 * @param groupSelectionMap
	 *            the group selection map
	 * @param selectionList
	 *            the selection list
	 * @return true, if successful
	 */
	public boolean validateUserGroupSelection(Map<Integer,List<List<Integer>>> groupSelectionMap,List<Integer> selectionList){
		boolean retValue=true;
		for (int key : groupSelectionMap.keySet()) {
			List<List<Integer>> groups = groupSelectionMap.get(key);
			for (List<Integer> grp : groups) {
				if (selectionList.size() == grp.size()) {
					if (!ListUtils.isEqualList(selectionList, grp) && ListUtils.intersection(selectionList, grp).size()==0) {
						retValue = true;
					}else if(ListUtils.isEqualList(selectionList, grp)){
						if (createErrorDialog(Messages.GROUP_CLAUSE_ALREADY_EXISTS).open() == SWT.OK) {
							retValue=false;
							break;
						}
					}else if(ListUtils.intersection(selectionList, grp).size() > 0){
						if (createErrorDialog(Messages.CANNOT_CREATE_GROUP_CLAUSE).open() == SWT.OK) {
							retValue=false;
							break;
						}
					}
				}else {
					if (ListUtils.isEqualList(ListUtils.intersection(selectionList, grp), grp)) {
						retValue = true;
					} else if(ListUtils.isEqualList(ListUtils.intersection(grp,selectionList),selectionList)){
					    	retValue=true;
				   }else if (ListUtils.intersection(selectionList, grp).size() == 0) {
						retValue = true;
					}else{
						if (createErrorDialog(Messages.CANNOT_CREATE_GROUP_CLAUSE).open() == SWT.OK) {
							retValue=false;
							break;
						}
					}
				}
			}
			if(!retValue){
				break; 
			}
		}
		return retValue;
	}
	
	
	/**
	 * Checks if column is modifiable.
	 * 
	 * @param groupSelectionMap
	 *            the group selection map
	 * @param selectionList
	 *            the selection list
	 * @return true, if is column modifiable
	 */
	public boolean isColumnModifiable(TreeMap<Integer,List<List<Integer>>> groupSelectionMap,List<Integer> selectionList){
		boolean retValue = false;
		for(int i=groupSelectionMap.lastKey();i>=0;i--){
			retValue=true;
			List<List<Integer>> groups = new ArrayList<>(groupSelectionMap.get(i));
			for (List<Integer> grp : groups) {
				if (ListUtils.intersection(selectionList, grp).size()>0) {
					retValue=false;
				}
		    }
		
			if(retValue){
				groupSelectionMap.get(i).add(selectionList);
				break;
			}
		}
		return retValue;
	}
	
	/**
	 * Creates the error dialog.
	 * 
	 * @param errorMessage
	 *            the error message
	 * @return the message box
	 */
	public MessageBox createErrorDialog(String errorMessage) {
		MessageBox messageBox = new MessageBox(new Shell(), SWT.ERROR | SWT.OK);
		messageBox.setMessage(errorMessage);
		messageBox.setText("Error");
		return messageBox;
	}
	
	/**
	 * Gets the color.
	 * 
	 * @param colorIndex
	 *            the color index
	 * @return the color
	 */
	public Color getColor(int colorIndex){
		Map<Integer,Color> colorMap = new HashMap<>();
		colorMap.put(0, CustomColorRegistry.INSTANCE.getColorFromRegistry(255,196,196)); // Light yellow
		colorMap.put(1, CustomColorRegistry.INSTANCE.getColorFromRegistry(176,255,176)); //Light green
		colorMap.put(2, CustomColorRegistry.INSTANCE.getColorFromRegistry(149,255,255)); //Light blue
		colorMap.put(3, CustomColorRegistry.INSTANCE.getColorFromRegistry(254,194,224)); //Light Pink
		colorMap.put(4, CustomColorRegistry.INSTANCE.getColorFromRegistry(147,194,147)); 
		colorMap.put(5, CustomColorRegistry.INSTANCE.getColorFromRegistry(255,81,168)); 
	
	   return colorMap.get(colorIndex);
	}
	
	/**
	 * Refresh group selections.
	 * 
	 * @param tableViewer
	 *            the table viewer
	 * @param indexOfRow
	 *            the index of row
	 * @param addOrDeleteRow
	 *            the add or delete row
	 * @param groupSelectionMap
	 *            the group selection map
	 * @return true, if successful
	 */
	public boolean refreshGroupSelections(TableViewer tableViewer, int indexOfRow,String addOrDeleteRow,
		TreeMap<Integer,List<List<Integer>>> groupSelectionMap){
		boolean isRemoveColumn = false;
		for(int key:groupSelectionMap.keySet()){
			List<List<Integer>> groups = groupSelectionMap.get(key);
			boolean isNewIndexAddedorRemoved=false;
			for (List<Integer> grp : groups) {
				List<Integer> tempGrp= new ArrayList<>(grp);
				if("ADD".equalsIgnoreCase(addOrDeleteRow)){
					for (int i = 0; i < grp.size(); i++) {
						if(grp.get(i)>=indexOfRow){
							grp.set(i, grp.get(i) + 1);
						}
					}
					if(tempGrp.contains(indexOfRow)){
						if(!isNewIndexAddedorRemoved && tempGrp.get(0)!=indexOfRow){//other than starting index then add row.
							grp.add(tempGrp.indexOf(indexOfRow),indexOfRow);
							isNewIndexAddedorRemoved=true;
						}
					}
				}else if("DEL".equalsIgnoreCase(addOrDeleteRow)){
					if(tempGrp.contains(indexOfRow)){
						if(!isNewIndexAddedorRemoved){//other than starting index then add row.
							grp.remove(grp.indexOf(indexOfRow));
							if(grp.size()==1){
								grp.clear();
							}
							if(rearrangeGroupsAfterDeleteRow(groupSelectionMap, grp)&& groupSelectionMap.lastKey()!=key){
								grp.clear();
							}
							List tempList=new ArrayList<>(); 
							for (List lst : groupSelectionMap.get(key)) {
								tempList.addAll(lst);
							}
							if(tempList.isEmpty()){
								isRemoveColumn=true;
							}
						}
						isNewIndexAddedorRemoved=true;
					}
					for (int i = 0; i < grp.size(); i++) {
						if(grp.get(i)>=indexOfRow){
							grp.set(i, grp.get(i) -1);
						}
					}
				}
				tempGrp.clear();	
			}
		}
		return isRemoveColumn;
	}
			
	
	/**
	 * Rearrange groups.
	 * 
	 * @param groupSelectionMap
	 *            the group selection map
	 * @param selectionList
	 *            the selection list
	 */
	public void rearrangeGroups(TreeMap<Integer, List<List<Integer>>> groupSelectionMap,List<Integer> selectionList) {
		List<Integer> tempList = new ArrayList<>();
		int lastKey=groupSelectionMap.lastKey();
		for (int i = lastKey; i >= 0; i--) {
			List<List<Integer>> groups =groupSelectionMap.get(i);
			for (int j=0; j<=groups.size()-1;j++) {
				if (selectionList.size()< groups.get(j).size()&& 
						ListUtils.intersection(selectionList, groups.get(j)).size() > 0) {
					tempList.addAll(groups.get(j));
					groups.get(j).clear();
					groups.set(j,new ArrayList<Integer>(selectionList));					
					selectionList.clear();
					selectionList.addAll(tempList);
				}
				tempList.clear();
			}
		}
	}
	
	
	/**
	 * Rearrange groups after delete row.
	 * 
	 * @param groupSelectionMap
	 *            the group selection map
	 * @param selectionList
	 *            the selection list
	 * @return true, if successful
	 */
	public boolean rearrangeGroupsAfterDeleteRow(TreeMap<Integer, List<List<Integer>>> groupSelectionMap,
			List<Integer> selectionList) {
		boolean retValue = false;
		int lastKey = groupSelectionMap.lastKey();
		int count = 0;
		for (int i = lastKey; i >= 0; i--) {
			List<List<Integer>> groups = groupSelectionMap.get(i);
			for (int j = 0; j <= groups.size() - 1; j++) {
				if (selectionList.size() == groups.get(j).size() && ListUtils.isEqualList(selectionList, groups.get(j))) {
					count++;
					if (count >= 2) {
						retValue = true;
					}
				}
			}
		}
		return retValue;
	}

	/**
	 * Dispose all columns.
	 * 
	 * @param tableViewer
	 *            the table viewer
	 */
	public void disposeAllColumns(TableViewer tableViewer){
		TableColumn[] columns = tableViewer.getTable().getColumns();
		TableItem[] items = tableViewer.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].dispose();
		}
		for (TableColumn tc : columns) {
			tc.dispose();
		}
	}	
	 
	
	/**
	 * Rearrange group columns.
	 * 
	 * @param groupSelectionMap
	 *            the group selection map
	 */
	 public void rearrangeGroupColumns(TreeMap<Integer, List<List<Integer>>> groupSelectionMap){
		 Map<Integer, List<List<Integer>>> tempMap = new TreeMap<Integer, List<List<Integer>>>(groupSelectionMap);
		 for(int key:tempMap.keySet()){
			  List<List<Integer>> groups = tempMap.get(key);
			  List<Integer> tempList=new ArrayList<>();
			  for (List<Integer> grp : groups) {
				  	tempList.addAll(grp);
			  }
			  if(tempList.isEmpty()){
				  for(int i=key ;i<tempMap.size()-1;i++){
					  groupSelectionMap.put(i, tempMap.get(i+1));
				  }
				  groupSelectionMap.remove(groupSelectionMap.lastKey());
			  }
		 }
	 }

 
}

