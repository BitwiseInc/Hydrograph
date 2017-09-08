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


package hydrograph.ui.propertywindow.widgets.customwidgets.operational;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.OperationClassConfig;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.OutputRecordCountUtility;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;


/**
 * The Class ELTOperationClassWidget.
 * 
 * @author Bitwise
 */
public class TransformWidget extends AbstractWidget {

	private static final String EDIT = "Edit";
	private String propertyName;
	private LinkedHashMap<String, Object> property = new LinkedHashMap<>();
	private TransformMapping transformMapping;
	private List<AbstractWidget> widgets;
	private List<FilterProperties> outputList;
	private boolean verifySchemaVidate = true;
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
	public TransformWidget(ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);
		this.transformMapping = (TransformMapping) componentConfigrationProperty.getPropertyValue();
		if (transformMapping == null) {
			transformMapping = new TransformMapping();
		}
		outputList=new ArrayList<>();
		this.propertyName = componentConfigrationProperty.getPropertyName();
 
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void attachToPropertySubGroup(final AbstractELTContainerWidget container) {

		final ELTDefaultSubgroupComposite transformComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		transformComposite.createContainerWidget();
		OperationClassConfig operationClassConfig = (OperationClassConfig) widgetConfig;
		ELTDefaultLable defaultLable1 = new ELTDefaultLable(operationClassConfig.getComponentDisplayName()+" \n ");
		transformComposite.attachWidget(defaultLable1);

		setPropertyHelpWidget((Control) defaultLable1.getSWTWidgetControl());
			
		ELTDefaultButton eltDefaultButton = new ELTDefaultButton(EDIT).grabExcessHorizontalSpace(false);
		if(OSValidator.isMac()){
			eltDefaultButton.buttonWidth(120);
		}
		transformComposite.attachWidget(eltDefaultButton);
		((Button) eltDefaultButton.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
                
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
					OutputRecordCountUtility.INSTANCE.propagateOuputFieldsToSchemaTabFromTransformWidget(transformMapping,
							getSchemaForInternalPropagation(),getComponent(),outputList);	
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
				if(transformDialog.isYesButtonPressed()){
					propertyDialog.pressOK();	
				}
			}
		});
		Utils.INSTANCE.loadProperties();
		SchemaSyncUtility.INSTANCE.unionFilter(transformMapping.getOutputFieldList(), outputList);
		OutputRecordCountUtility.INSTANCE.propagateOuputFieldsToSchemaTabFromTransformWidget(transformMapping,
				getSchemaForInternalPropagation(),getComponent(),outputList);	 
	}
  
	@Override
	public boolean canClosePropertyDialog() {
		return validateTransformSchemaOnOkClick(getSchemaForInternalPropagation(), getComponent());
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		property.put(propertyName, transformMapping);
		return property;
	}

	/**
	 * @param schema
	 * @param component
	 * @param mapping
	 */
	private boolean validateTransformSchemaOnOkClick(Schema schema, Component component){
		if(schema != null && component != null && component != null){
			List<GridRow> gridRows = null;
			List<GridRow> currentSchemaGridRowList = null;
			List<GridRow> temp = new LinkedList<>();
			Schema currentCompSchema = (Schema)component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
			if(currentCompSchema!=null){
				currentSchemaGridRowList = currentCompSchema.getGridRow();
			}
					
			List<Link> links = component.getInputLinks();
			for(Link link : links){
				Schema inputLinkSchema = (Schema) link.getSource().getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
				if(inputLinkSchema!=null){
					gridRows = inputLinkSchema.getGridRow();
				}
			}
			
			if(gridRows != null && currentCompSchema!=null){
				for(int index=0;index <= currentSchemaGridRowList.size() - 1;index++){
					for(GridRow gridRow : gridRows){
						if(StringUtils.equals(gridRow.getFieldName(), currentSchemaGridRowList.get(index).getFieldName())){
							temp.add(currentSchemaGridRowList.get(index));
						}
					}
				}
			}
			return compareSchemaFields(gridRows, temp);
		}
		return false;
	}
	
	@Override
	public boolean verifySchemaFile() {
		return verifySchemaVidate;
	}
	
	/** 
	 * @param inputLinkSchema
	 * @param currentCompSchema
	 */
	private boolean compareSchemaFields(List<GridRow> inputLinkSchema, List<GridRow> currentCompSchema){
		for(int index = 0; index < currentCompSchema.size() - 1; index++){
			for(GridRow gridRow : inputLinkSchema){
				if(StringUtils.equals(gridRow.getFieldName(), currentCompSchema.get(index).getFieldName())){
					if(!StringUtils.equals(gridRow.getDataTypeValue(), currentCompSchema.get(index).getDataTypeValue())){
						MessageDialog dialog = new MessageDialog(new Shell(),
								"Warning", null,"Output Schema is updated,Do you want to continue with changes?", MessageDialog.CONFIRM,
								new String[] {"Yes", "No"}, 0);
						int dialogResult =dialog.open();
						if(dialogResult == 0){
							return true;
						}else{
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(transformMapping);
	}

	@Override
	public void addModifyListener(Property property,  ArrayList<AbstractWidget> widgetList) {
		widgets=widgetList;
	}
}