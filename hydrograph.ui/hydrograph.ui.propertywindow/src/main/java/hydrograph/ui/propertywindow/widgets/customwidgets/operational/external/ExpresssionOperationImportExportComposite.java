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
package hydrograph.ui.propertywindow.widgets.customwidgets.operational.external;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.utility.FilterOperationClassUtility;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;
/**
 * @author Bitwise
 * Provides import export functionality for Expression, operations and fields.
 */
public abstract class ExpresssionOperationImportExportComposite  extends Composite{
	private Text txtExternalPath;
	private Label labelExperssionType;
	private Composite radioButtonCompsite;
	private Button btnRadioInternal;
	private Button btnRadioExternal;
	private Label lblExternal;
	private Button btnBrowse;
	private Button btnImport;
	private Button btnExport;
	private ControlDecoration decorator;
	private ExternalWidgetData externalWidgetData;
	private Cursor cursor;

	public ExpresssionOperationImportExportComposite(Composite parent, int style,ImportExportType type, ExternalWidgetData externalWidgetData) {
		super(parent, style);
		this.externalWidgetData = externalWidgetData;
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setLayout(new GridLayout(1, false));
		Composite exprOperImportExportComposite = new Composite(this, SWT.NONE);
		exprOperImportExportComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		exprOperImportExportComposite.setLayout(new GridLayout(2, false));
		
		labelExperssionType = new Label(exprOperImportExportComposite, SWT.NONE);
		labelExperssionType.setText( type.getValue() +" Type");
		
		radioButtonCompsite = new Composite(exprOperImportExportComposite, SWT.NONE);
		radioButtonCompsite.setLayout(new GridLayout(2, false));
		radioButtonCompsite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnRadioInternal = new Button(radioButtonCompsite, SWT.RADIO);
		btnRadioInternal.setText("Internal "+type.getValue());
		
		btnRadioExternal = new Button(radioButtonCompsite, SWT.RADIO);
		btnRadioExternal.setText("External "+type.getValue());
		
		lblExternal = new Label(exprOperImportExportComposite, SWT.NONE);
		lblExternal.setText("External Path");
		
		Composite browseButtonComposite = new Composite(exprOperImportExportComposite, SWT.NONE);
		browseButtonComposite.setLayout(new GridLayout(2, false));
		browseButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		txtExternalPath = new Text(browseButtonComposite, SWT.BORDER);
		txtExternalPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		decorator = WidgetUtility.addDecorator(txtExternalPath, "Field should not be blank.");
		decorator.hide();
		decorator.setMarginWidth(3);
		btnBrowse = new Button(browseButtonComposite, SWT.NONE);
	    GridData gd_btnNewButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.heightHint = 25;
		
		if(OSValidator.isMac()){
			gd_btnNewButton.widthHint = 43;
			gd_btnNewButton.horizontalIndent=-5;
			
		}else{
			gd_btnNewButton.widthHint = 30;
		}
		btnBrowse.setLayoutData(gd_btnNewButton);
		btnBrowse.setText("...");
		new Label(exprOperImportExportComposite, SWT.NONE);
		
		Composite importExportButtonComposite = new Composite(exprOperImportExportComposite, SWT.NONE);
		importExportButtonComposite.setLayout(new GridLayout(2, false));
		importExportButtonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		btnImport = new Button(importExportButtonComposite, SWT.NONE);
		btnImport.setText("Import "+type.getValue());
		btnExport = new Button(importExportButtonComposite, SWT.NONE);
		btnExport.setText("Export "+type.getValue());
		btnExport.setEnabled(false);
		btnImport.setEnabled(false);
		addListners();
		updateWidgetValueWithPreviousChoices();
		addSelectinListnerToExportImport();
		
		addModifyListnerToExternalPathTextBox(externalWidgetData);
	}

	


	private void addModifyListnerToExternalPathTextBox(ExternalWidgetData externalWidgetData) {
		txtExternalPath.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				onPathModify(externalWidgetData);
			}
		});
	}

	protected void onPathModify(ExternalWidgetData externalWidgetData) {
		externalWidgetData.setFilePath(txtExternalPath.getText());
	}


	private void addSelectinListnerToExportImport() {
		btnImport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importButtonSelection((Button)e.widget);
			}
		});
		btnExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportButtonSelection((Button)e.widget);
			}
		});
	}

	protected abstract void exportButtonSelection(Button widget) ;
	protected abstract void importButtonSelection(Button widget) ;
	protected abstract void interalRadioButtonSelection(Button widget);
	protected abstract void externalRadioButtonSelection(Button widget);
	

	private void updateWidgetValueWithPreviousChoices(){
		if(!externalWidgetData.isExternal()){
			btnRadioInternal.setSelection(true);
			toggleInternalExternal(false);
		}else{
			btnRadioExternal.setSelection(true);
			txtExternalPath.setText(externalWidgetData.getFilePath());
			toggleInternalExternal(true);
		}
	}
	/**
	 * 
	 * Attach listeners to widgets.
	 */
	private void addListners() {
		btnRadioInternal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleInternalExternal(false);
				decorator.hide();
				interalRadioButtonSelection((Button)e.widget);
			}
		});
		btnRadioExternal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleInternalExternal(true);
				if (StringUtils.isBlank(txtExternalPath.getText())) {
					 btnExport.setEnabled(false);
					 btnImport.setEnabled(false);
					 decorator.show();
					 externalRadioButtonSelection((Button)e.widget);
				 } else {
					 decorator.hide();
				 }
			}
		});
		txtExternalPath.addFocusListener(new FocusListener() {
			 @Override
			 public void focusLost(FocusEvent e) {
				 if (txtExternalPath.getText().isEmpty()) {
					 decorator.show();
					 txtExternalPath.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 250, 250, 250));
				 } else {
					 decorator.hide();
				 }
			 }
			 @Override
			 public void focusGained(FocusEvent e) {
				 decorator.hide();
				 txtExternalPath.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			 }
		 });
		txtExternalPath.addModifyListener(new ModifyListener() {
			 @Override
			 public void modifyText(ModifyEvent e) {
				 btnExport.setEnabled(StringUtils.isNotBlank(((Text)e.widget).getText()));
				 btnImport.setEnabled(StringUtils.isNotBlank(((Text)e.widget).getText()));
				 externalWidgetData.setFilePath(txtExternalPath.getText());
				 Utils.INSTANCE.addMouseMoveListener(txtExternalPath, cursor);
				 if(StringUtils.isBlank(txtExternalPath.getText())){
					 decorator.show();
				 }else{
					 decorator.hide();
				 }
			 }
		 });
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FilterOperationClassUtility.INSTANCE.browseFile("xml",txtExternalPath);
			}
		});
	}

	/**
	 * @return Import button
	 * 	 The functionality for import button will be different for all 3 category 
	 *   (expression, operation and fields) hence need to be implemented by underlying class.
	 */
	public Button getImportButton(){
		return btnImport;
	}
	
	/**
	 * @return Import button
	 * 	 The functionality for import button will be different for all 3 category 
	 *   (expression, operation and fields) hence need to be implemented by underlying class.
	 */
	public Button getExportButton(){
		return btnExport;
	}
	
	public String getFilePath(){
		return txtExternalPath.getText();
	}
	
	/**
	 * @return
	 * Calculates and returns the file path. Also supports parameterized values.
	 */
	public File getFile(){
		return ELTSchemaGridWidget.getPath(txtExternalPath, Constants.XML_EXTENSION, true, Constants.XML_EXTENSION);
	 }
	
	/**
	 * @param isEnabled
	 * Enable and disable widgets based on internal or external radio button selection
	 * 
	 */
	private void toggleInternalExternal(boolean isEnabled) {
		txtExternalPath.setEnabled(isEnabled);
		btnBrowse.setEnabled(isEnabled);
		if (StringUtils.isNotBlank(txtExternalPath.getText())) {
			btnImport.setEnabled(isEnabled);
			btnExport.setEnabled(isEnabled);
		}
		// if isEnabled is false means internal radio button is checked
		if(!isEnabled){
			externalWidgetData.setExternal(false);
		}else{
			externalWidgetData.setExternal(true);
			externalWidgetData.setFilePath(txtExternalPath.getText());
		}
	}
}