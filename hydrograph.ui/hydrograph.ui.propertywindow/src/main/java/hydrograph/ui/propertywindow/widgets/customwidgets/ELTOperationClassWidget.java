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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.OperationClassProperty;
import hydrograph.ui.propertywindow.filter.FilterExpressionOperationDialog;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.dialogs.ELTOperationClassDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;


/**
 * The Class ELTOperationClassWidget.
 * 
 * @author Bitwise
 */
public class ELTOperationClassWidget extends AbstractWidget {

	private String propertyName;
	private ArrayList<AbstractWidget> widgets;
	private LinkedHashMap<String, Object> property = new LinkedHashMap<>(); 
	private OperationClassProperty operationClassProperty;
	private ELTOperationClassDialog eltOperationClassDialog;
	private List<NameValueProperty> nameValuePropertyList;
	private Button operationRadioButton;
	private Button expressionRadioButton;
	private boolean isOpenedFirstTime;
	/**
	 * Instantiates a new ELT operation class widget.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTOperationClassWidget(
			ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties,
				propertyDialogButtonBar);
		nameValuePropertyList=new ArrayList<>(); 
		this.operationClassProperty = (OperationClassProperty) componentConfigrationProperty.getPropertyValue();
		if(operationClassProperty == null){
			isOpenedFirstTime=true;
			operationClassProperty = new OperationClassProperty(Messages.CUSTOM, "", false, "",nameValuePropertyList,null,null);
		}
		this.propertyName = componentConfigrationProperty.getPropertyName();
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {

		int data_key = 0;
		final ELTDefaultSubgroupComposite runtimeComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());

		runtimeComposite.createContainerWidget();
		runtimeComposite.numberOfBasicWidgets(3);
		Composite radioButtonComposite = new Composite(runtimeComposite.getContainerControl(),SWT.NONE);
		GridLayout radioButtonCompositeLayout = new GridLayout(1,false);
		radioButtonCompositeLayout.marginLeft = 0;
		radioButtonCompositeLayout.marginRight = 1;
		radioButtonCompositeLayout.marginWidth = 0;
		radioButtonComposite.setLayout(radioButtonCompositeLayout);
		GridData radioButtonCompositeGridData = new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1);
		if(OSValidator.isMac()){
			radioButtonCompositeGridData.horizontalIndent = -3;
		}
		radioButtonComposite.setLayoutData(radioButtonCompositeGridData);
		operationClassProperty.getExpressionEditorData().setComponentName(getComponent().getComponentName());
		expressionRadioButton = new Button(radioButtonComposite, SWT.RADIO);
		if(OSValidator.isMac()){
			expressionRadioButton.setText(Messages.MAC_EXPRESSION_EDITIOR_LABEL);
		}else {
		    expressionRadioButton.setText(Messages.WINDOWS_EXPRESSION_EDITIOR_LABEL);
		}
		setPropertyHelpWidget(expressionRadioButton);
		addSelectionListenerOnExpression();
		ELTDefaultButton eltDefaultButton = new ELTDefaultButton(
				Messages.EDIT_BUTTON_LABEL).grabExcessHorizontalSpace(false);
		if(OSValidator.isMac()){
			eltDefaultButton.buttonWidth(120);
		}
		runtimeComposite.attachWidget(eltDefaultButton);
		
		operationRadioButton = new Button(radioButtonComposite, SWT.RADIO);
		operationRadioButton.setText(Messages.OPERATION_CALSS_LABEL);
		operationRadioButton.setData("0",operationRadioButton);
		operationRadioButton.setData("1",expressionRadioButton);
		setPropertyHelpWidget(operationRadioButton);
		addSelectionListenerOnOperation();
		
		initialize();
		
		setToolTipMessage(Messages.OperationClassBlank);
		((Button)eltDefaultButton.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
//				if(expressionRadioButton.getSelection()){
//					List<FixedWidthGridRow> inputFieldSchema=getInputSchema();
//					operationClassProperty.setExpression(true);
//					operationClassProperty.getExpressionEditorData().getSelectedInputFieldsForExpression().clear();
//					operationClassProperty.getExpressionEditorData().getSelectedInputFieldsForExpression()
//							.putAll(FieldDataTypeMap.INSTANCE.createFieldDataTypeMap(null,inputFieldSchema));
//					LaunchExpressionEditor launchExpressionEditor=new LaunchExpressionEditor();
//					String oldExpression=operationClassProperty.getExpressionEditorData().getExpression();
//					launchExpressionEditor.launchExpressionEditor(operationClassProperty.getExpressionEditorData(),inputFieldSchema,getComponent().getComponentLabel().getLabelContents());
//					if(!StringUtils.equals(operationClassProperty.getExpressionEditorData().getExpression(), oldExpression)){
//						propertyDialogButtonBar.enableApplyButton(true);
//					}
//						
//				}else{
//				OperationClassProperty oldOperationClassProperty=operationClassProperty.clone();
//				eltOperationClassDialog = new ELTOperationClassDialog(
//						runtimeComposite.getContainerControl().getShell(), propertyDialogButtonBar,
//						operationClassProperty, widgetConfig, getComponent().getComponentName());
//				eltOperationClassDialog.open();
//				operationClassProperty.setComboBoxValue(eltOperationClassDialog.getOperationClassProperty().getComboBoxValue());
//				operationClassProperty.setOperationClassPath(eltOperationClassDialog.getOperationClassProperty().getOperationClassPath());
//				operationClassProperty.setOperationClassFullPath(eltOperationClassDialog.getOperationClassProperty().getOperationClassFullPath());
//				operationClassProperty.setParameter(eltOperationClassDialog.getOperationClassProperty().isParameter());
//				if (eltOperationClassDialog.isCancelPressed() && (!(eltOperationClassDialog.isApplyPressed()))) {
//					operationClassProperty.setNameValuePropertyList(oldOperationClassProperty.getNameValuePropertyList());
//				}
//				setToolTipMessage(eltOperationClassDialog.getTootlTipErrorMessage());
//				
//				if(eltOperationClassDialog.isYesPressed()){
//					propertyDialog.pressOK();
//				}
//				
//				if(eltOperationClassDialog.isNoPressed()){
//					propertyDialog.pressCancel();
//				}
//				
//				
//			}
//				showHideErrorSymbol(widgets);
				
				FilterExpressionOperationDialog dialog = new FilterExpressionOperationDialog(
						Display.getCurrent().getActiveShell(),
						new FilterLogicDataStructure(getComponent().getComponentName()), getComponent(),
						propertyDialogButtonBar, widgetConfig, getInputSchema());
				
				dialog.open();
				showHideErrorSymbol(widgets);
			}

			private List<FixedWidthGridRow> getInputSchema() 
			{
				ELTSchemaGridWidget  schemaWidget = null;
				for(AbstractWidget abstractWidget:widgets)
				{
					if(abstractWidget instanceof ELTSchemaGridWidget)
					{
						schemaWidget=(ELTSchemaGridWidget)abstractWidget;
						break;
					}
				}
				if (schemaWidget != null) {
					schemaWidget.refresh();
				}
				List<GridRow> gridRowList=(List<GridRow>)schemaWidget.getTableViewer().getInput();
				return SchemaSyncUtility.INSTANCE.convertGridRowsSchemaToFixedSchemaGridRows(gridRowList);
			}
		});
	
} 
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {		
		property.put(propertyName, operationClassProperty);
		return property;
	}

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(operationClassProperty);
	}

	public void initialize()
	{
		if(operationClassProperty.isExpression() || isOpenedFirstTime)
		{
			expressionRadioButton.setSelection(true);
		}else{
			operationRadioButton.setSelection(true);
		}
	}
	
	public void addSelectionListenerOnOperation(){
		operationRadioButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(operationRadioButton.getSelection()){
					operationClassProperty.setExpression(false);
					enableOpertaionFieldButton((Button)e.widget, true);
				}else
					operationClassProperty.setExpression(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {/*Do-Nothing*/}
		});
		
	}
	
	
	public void addSelectionListenerOnExpression(){
		
		expressionRadioButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(expressionRadioButton.getSelection()){
					operationClassProperty.setExpression(true);
					enableOpertaionFieldButton((Button)e.widget, false);
				}else
					operationClassProperty.setExpression(false);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {/*Do-Nothing*/}
		} );

		expressionRadioButton.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				Button exprRadioButton=expressionRadioButton;
				enableOpertaionFieldButton(exprRadioButton,operationRadioButton.getSelection());
			}
			
			@Override
			public void controlMoved(ControlEvent e)  {/*Do-Nothing*/}
		});
		
	}


	@Override
	public void addModifyListener(Property property,  ArrayList<AbstractWidget> widgetList) {
		widgets=widgetList;
	}

	private void enableOpertaionFieldButton( Button radioButton,boolean enableOperation){
		for(AbstractWidget widget : widgets){
			if(widget instanceof SingleColumnWidget){
				SingleColumnWidget singleColumnWidget = (SingleColumnWidget) widget;
				singleColumnWidget.setEditButtonEnable(enableOperation);
			}
		}
	}
	
}
