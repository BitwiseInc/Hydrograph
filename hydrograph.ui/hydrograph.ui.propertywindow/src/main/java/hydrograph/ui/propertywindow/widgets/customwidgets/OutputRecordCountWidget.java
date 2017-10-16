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


package hydrograph.ui.propertywindow.widgets.customwidgets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.InputField;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.expression.editor.launcher.LaunchExpressionEditor;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.expression.editor.util.FieldDataTypeMap;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.OperationClassConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.TransformDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.OutputRecordCountUtility;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * 
 * output record count widget for normalize component 
 * 
 * @author Bitwise
 *
 */
public class OutputRecordCountWidget extends AbstractWidget{

	
	private Button expressionRadioButton,operationRadioButton,editButton;
	private Text outputRecordCoundText;
	private LinkedHashMap<String, Object> property = new LinkedHashMap<>();
	private String propertyName;
	private List<FilterProperties> outputList;
	private TransformMapping transformMapping;
	private List<AbstractWidget> widgets;
	private ControlDecoration expressionValidateDecorator;
	  
	
	private void createTransformEditButtonAndLabel(Composite compositeForNormalizeEditButtton, int data_key) 
	{
		OperationClassConfig operationClassConfig = (OperationClassConfig) widgetConfig;
		ELTDefaultLable	label_normalize = new ELTDefaultLable(operationClassConfig.getComponentDisplayName()+" \n ");
		label_normalize.attachWidget(compositeForNormalizeEditButtton);
		expressionRadioButton.setData(String.valueOf(data_key), label_normalize.getSWTWidgetControl());
		
		ELTDefaultButton eltDefaultButton = new ELTDefaultButton(Constants.EDIT).grabExcessHorizontalSpace(false);
		if(OSValidator.isMac())
		{
			eltDefaultButton.buttonWidth(120);
		}
		eltDefaultButton.attachWidget(compositeForNormalizeEditButtton);
		addSelectionListenerToEditButton(eltDefaultButton);
		SchemaSyncUtility.INSTANCE.unionFilter(transformMapping.getOutputFieldList(), outputList);
		OutputRecordCountUtility.INSTANCE.propagateOuputFieldsToSchemaTabFromTransformWidget(transformMapping,
				getSchemaForInternalPropagation(),getComponent(),outputList);	 
	}

	private void addSelectionListenerToEditButton(ELTDefaultButton eltDefaultButton) {
		((Button) eltDefaultButton.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) 
			{
                TransformMapping oldATMappings = (TransformMapping) transformMapping.clone();
				TransformDialog transformDialog=new TransformDialog(Display.getCurrent().getActiveShell(),getComponent(),widgetConfig,transformMapping);
				int returncode=transformDialog.open();
				outputList.clear();
                outputList = transformDialog.getFinalSortedList();
				if(transformDialog.isCancelPressed()||returncode==1)
				{
					transformMapping=oldATMappings;
				}
			 	
				if(transformDialog.isOkPressed())
               	{
					OutputRecordCountUtility.INSTANCE.propagateOuputFieldsToSchemaTabFromTransformWidget(
							transformMapping,getSchemaForInternalPropagation(),getComponent(),outputList
							);
					SchemaSyncUtility.INSTANCE.autoSyncSchema(getSchemaForInternalPropagation(), getComponent(), widgets);
					showHideErrorSymbol(widgets);
               	}	

				if(!oldATMappings.equals(transformDialog.getATMapping()) && returncode==0)
				{
					propertyDialogButtonBar.enableApplyButton(true);
					
				}
				if(transformDialog.isNoButtonPressed())
				{
					propertyDialog.pressCancel();
				}	
				if(transformDialog.isYesButtonPressed())
				{
					propertyDialog.pressOK();	
				}
             }
		});
	}

	private Composite createcompositeForNormalizeEditButtton(ELTDefaultSubgroupComposite eltDefaultSubgroupComposite) 
	{
		Composite composite=new Composite(eltDefaultSubgroupComposite.getContainerControl(),SWT.NONE);
		setLayoutAndDataToComposite(composite,3);
		return composite;
	}

	private Composite createLabelTextBoxAndButtonComposite(ELTDefaultSubgroupComposite eltDefaultSubgroupComposite) 
	{
		Composite composite=new Composite(eltDefaultSubgroupComposite.getContainerControl(), SWT.NONE);
		setLayoutAndDataToComposite(composite,3);
		return composite;
		
	}

	private void setLayoutAndDataToComposite(Composite composite,int noOfColumn) 
	{
		GridLayout gridLayout=new GridLayout(noOfColumn,false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 1;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing=10;
		composite.setLayout(gridLayout);
		GridData radioButtonCompositeGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		if(OSValidator.isMac()){
			radioButtonCompositeGridData.horizontalIndent = -3;
		}
		composite.setLayoutData(radioButtonCompositeGridData);
	}

	private void createLabelTextBoxAndButton(Composite textBobLabelButtonComposite, int data_key)
	{
		AbstractELTWidget outputRecordCountLabel1=new ELTDefaultLable(Constants.OUTPUT_RECORD_COUNT);
		outputRecordCountLabel1.attachWidget(textBobLabelButtonComposite);
		Composite composite=new Composite(textBobLabelButtonComposite, SWT.NONE);
		setLayoutAndDataToComposite(composite,1);
		ELTDefaultTextBox outputRecordCountTextBox=new ELTDefaultTextBox();
		outputRecordCountTextBox.attachWidget(composite);
		outputRecordCoundText=(Text)outputRecordCountTextBox.getSWTWidgetControl();
		expressionValidateDecorator = WidgetUtility.addDecorator(outputRecordCoundText,"Invalid Expression");
		expressionValidateDecorator.setMarginWidth(3);
		addModifyListenerToRecordCountTextBox();
		if(transformMapping.getExpressionEditorData()!=null)
		{
			outputRecordCoundText.setText(transformMapping.getExpressionEditorData().getExpression());
			showHideDecorator();	
		}	
		AbstractELTWidget outputRecordCountButton=new ELTDefaultButton(Constants.EDIT);
		outputRecordCountButton.attachWidget(textBobLabelButtonComposite);
		editButton=(Button)outputRecordCountButton.getSWTWidgetControl();
	    intializeEnableOrDisableStateOfWidgetsBasedOnExpressionOrOperationSelected();
	    addListenerOnEditButtonToOpenExpressionEditor();
	    
		expressionRadioButton.setData(String.valueOf(data_key), outputRecordCountLabel1.getSWTWidgetControl());
	}

	private void addModifyListenerToRecordCountTextBox() 
	{
		outputRecordCoundText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
			 Text textBox=(Text)e.widget;
			 transformMapping.getExpressionEditorData().setExpression(textBox.getText());
            propertyDialogButtonBar.enableApplyButton(true);				
			}
		});
		
	}

	private void addListenerOnEditButtonToOpenExpressionEditor() 
	{
			editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				intializeExpresionEditorData();
				LaunchExpressionEditor launchExpressionEditor=new LaunchExpressionEditor();
				launchExpressionEditor.launchExpressionEditor(transformMapping.getExpressionEditorData(), 
						OutputRecordCountUtility.INSTANCE.getInputSchema(getComponent()), getComponent().getComponentName());
				outputRecordCoundText.setText(transformMapping.getExpressionEditorData().getExpression());
				if(transformMapping.getExpressionEditorData().isValid())
				expressionValidateDecorator.hide();
				else
				expressionValidateDecorator.show();	
			}
		});
    		
	}
	
	private List<FixedWidthGridRow> getInputSchema(Component component) {
		
		List<FixedWidthGridRow> fixedWidthGridRows = new ArrayList<>();
		Map<String,Schema> schemaMap=(TreeMap<String,Schema>)component.getProperties().get(Constants.PREVIOUS_COMPONENT_OLD_SCHEMA);
		for (Link link : component.getTargetConnections()) {
			if(schemaMap!=null)
			{
			Schema schema=schemaMap.get(link.getTargetTerminal());
			List<GridRow> gridRowList=null;
			if(schema!=null)
			gridRowList=schemaMap.get(link.getTargetTerminal()).getGridRow();
			fixedWidthGridRows.addAll(SchemaSyncUtility.INSTANCE.convertGridRowsSchemaToFixedSchemaGridRows(gridRowList));
			}
		}
		return fixedWidthGridRows;
	}
	
	
	private void intializeExpresionEditorData() 
	{
		ExpressionEditorData expressionEditorData=transformMapping.getExpressionEditorData();
		List<String> inputFieldNames = new ArrayList<>();
		for(InputField inputField:transformMapping.getInputFields())
		{
			inputFieldNames.add(inputField.getFieldName());
		}
		expressionEditorData.getfieldsUsedInExpression().clear();
		expressionEditorData.getfieldsUsedInExpression().addAll(inputFieldNames);
		expressionEditorData.getSelectedInputFieldsForExpression().clear();
		expressionEditorData.setComponentName(getComponent().getComponentName());
		expressionEditorData.getSelectedInputFieldsForExpression().
		putAll(FieldDataTypeMap.INSTANCE.createFieldDataTypeMap(inputFieldNames, OutputRecordCountUtility.INSTANCE
				.getInputSchema(getComponent())));
	}
	private void intializeEnableOrDisableStateOfWidgetsBasedOnExpressionOrOperationSelected() 
	{
		if(!transformMapping.isExpression())
		{
        	editButton.setEnabled(false);
        	outputRecordCoundText.setEnabled(false);
		}	
	}

	private void attachRadioButtonToComposite(Composite radioButtonsComposite) 
	{
      expressionRadioButton=new Button(radioButtonsComposite, SWT.RADIO);
      if(OSValidator.isMac())
      {
		expressionRadioButton.setText(Messages.MAC_EXPRESSION_EDITIOR_LABEL);
	  }
      else 
      {
	    expressionRadioButton.setText(Messages.WINDOWS_EXPRESSION_EDITIOR_LABEL);
	  }
      
      operationRadioButton = new Button(radioButtonsComposite, SWT.RADIO);
	  operationRadioButton.setText(Messages.OPERATION_CALSS_LABEL);
	  
	  addSelectionListenerToExpressionRadioButton(expressionRadioButton);
	  addSelectionListenerToOperationRadioButton(operationRadioButton);
	  if(transformMapping.isExpression())
	  {	  
	  expressionRadioButton.setSelection(true);
	  }
	  else
	  {	  
	  operationRadioButton.setSelection(true);	  
	  }
	}

	private void addSelectionListenerToExpressionRadioButton(Button expressionRadioButton) 
	{
		expressionRadioButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				outputRecordCoundText.setEnabled(true);
				editButton.setEnabled(true);
				transformMapping.setExpression(true);
				propertyDialogButtonBar.enableApplyButton(true);
				if(!transformMapping.getExpressionEditorData().isValid())
				{
					expressionValidateDecorator.show();
				}
				OutputRecordCountUtility.INSTANCE.removeOperationFieldFromOutputList(transformMapping);
				OutputRecordCountUtility.INSTANCE.addExpressionOutputFieldToOuterListOfMappingWindow(transformMapping);
				setSchemaUpdated(false);
				outputList.clear();
				SchemaSyncUtility.INSTANCE.unionFilter(transformMapping.getOutputFieldList(), 
						outputList);
				OutputRecordCountUtility.INSTANCE.propagateOuputFieldsToSchemaTabFromTransformWidget(
						transformMapping,getSchemaForInternalPropagation(),getComponent(),outputList
						);
	 		}
		});
	}
	
	private void addSelectionListenerToOperationRadioButton(Button operationRadioButton) 
	{
		operationRadioButton.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseUp(MouseEvent e) {
				outputRecordCoundText.setEnabled(false);
				editButton.setEnabled(false);
				transformMapping.setExpression(false);
				propertyDialogButtonBar.enableApplyButton(true);
				expressionValidateDecorator.hide();
				OutputRecordCountUtility.INSTANCE.removeExpressionFieldFromOutputList(transformMapping);
				OutputRecordCountUtility.INSTANCE.addOperationOutputFieldToOuterListOfMappingWindow(transformMapping);
				setSchemaUpdated(false);
				outputList.clear();
				SchemaSyncUtility.INSTANCE.unionFilter(transformMapping.getOutputFieldList(), 
						outputList);
				OutputRecordCountUtility.INSTANCE.propagateOuputFieldsToSchemaTabFromTransformWidget(
						transformMapping,getSchemaForInternalPropagation(),getComponent(),outputList
						);
			}
        });
	}
	
	private Composite createRadioButtonComposite(ELTDefaultSubgroupComposite eltDefaultSubgroupComposite) {
		Composite radioButtonComposite=new Composite(eltDefaultSubgroupComposite.getContainerControl(), SWT.NONE);
		setLayoutAndDataToComposite(radioButtonComposite,2);
		return radioButtonComposite;
	}

	public OutputRecordCountWidget(ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) 
	{
		super(componentConfigrationProperty, componentMiscellaneousProperties,
				propertyDialogButtonBar);
		transformMapping=(TransformMapping)componentConfigrationProperty.getPropertyValue();
		if(transformMapping==null)
		{
		transformMapping=new TransformMapping();
		transformMapping.setExpression(true);
		ExpressionEditorData expressionEditorData=new ExpressionEditorData("", "");
		transformMapping.setExpressionEditorData(expressionEditorData);
		}
		propertyName=componentConfigrationProperty.getPropertyName();
		outputList=new ArrayList<>();
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) 
	{
		int data_key = 0;
		ELTDefaultSubgroupComposite eltDefaultSubgroupComposite=new ELTDefaultSubgroupComposite(container.getContainerControl());
        eltDefaultSubgroupComposite.createContainerWidget();
		eltDefaultSubgroupComposite.numberOfBasicWidgets(1);
		Composite radioButtonsComposite= createRadioButtonComposite(eltDefaultSubgroupComposite);
		attachRadioButtonToComposite(radioButtonsComposite);
		// for adding help tooltip on widget's labels
		expressionRadioButton.setData(String.valueOf(data_key++), expressionRadioButton);
		expressionRadioButton.setData(String.valueOf(data_key++), operationRadioButton);

		Composite labelTexBoxAndButtonComposite=createLabelTextBoxAndButtonComposite(eltDefaultSubgroupComposite);
		createLabelTextBoxAndButton(labelTexBoxAndButtonComposite, data_key++);
		Composite compositeForNormalizeEditButtton=createcompositeForNormalizeEditButtton(eltDefaultSubgroupComposite);
		createTransformEditButtonAndLabel(compositeForNormalizeEditButtton, data_key);
		
		

		setPropertyHelpWidget(expressionRadioButton);
	}
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		if(transformMapping.isExpression()){
			transformMapping.getMappingSheetRows().forEach(mappingSheet->{
				if(!mappingSheet.isExpression() && mappingSheet.isActive() ){
					mappingSheet.getInputFields().clear();
					mappingSheet.getOutputList().clear();
					mappingSheet.getExternalOperation().clear();
					mappingSheet.setParameter(false);
					mappingSheet.setOperationClassFullPath("");
					mappingSheet.setOperationClassPath("");
				}
				}
			);
		}else transformMapping.getMappingSheetRows().forEach(mappingSheet->{
			   if((mappingSheet.isExpression() && mappingSheet.isActive())){
					mappingSheet.getInputFields().clear();
					mappingSheet.getOutputList().clear();
					mappingSheet.getExternalExpresion().clear();
					mappingSheet.setParameter(false);
					if(mappingSheet.getExpressionEditorData()!=null)mappingSheet.getExpressionEditorData().clear();
				   }
			   }
				);
		property.put(propertyName, transformMapping);
		String expression=transformMapping.getExpressionEditorData().getExpression();
		ExpressionEditorUtil.validateExpression(expression,  transformMapping.getExpressionEditorData()
				.getCombinedFieldDatatypeMap()
				, transformMapping.getExpressionEditorData());
		showHideDecorator();	
        return property;
	}

	private void showHideDecorator() {
		if(transformMapping.isExpression()&&!transformMapping.getExpressionEditorData().isValid())
			expressionValidateDecorator.show();
			else 
			expressionValidateDecorator.hide();
	}

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(transformMapping);
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		widgets=widgetList;
		outputRecordCoundText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				showHideErrorSymbol(widgetList);
			}
		});
	}
	
	
}