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
package hydrograph.ui.dataviewer.preferencepage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.swt.customwidget.HydroGroup;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.PreferenceConstants;
import hydrograph.ui.dataviewer.constants.Messages;

/**
 * @author Bitwise
 * ServicesPreference Class creates preference page for 
 * defining common parameters for view data and execution
 * tracking service.
 *
 */
public class ServicesPreference extends PreferencePage implements IWorkbenchPreferencePage {
	
	private Composite grpServiceDetailsCmposite;
	private IntegerFieldEditor localPortNo;
	private IntegerFieldEditor remotePortNo;
	private List<FieldEditor> editorList;
	private BooleanFieldEditor useRemoteConfigBooleanFieldEditor;
	private StringFieldEditor remoteHostFieldEditor;
	private Button useRemoteConfigbutton;
	private static final String DEFAULT_LOCAL_PORT = "8004";
	private static final String DEFAULT_REMOTE_PORT = "8004";
	private static final boolean DEFAULT_USE_REMOTE_CONFIGURATION_CHECK = false;
	
	public ServicesPreference() {
		super();
		setPreferenceStore(PlatformUI.getWorkbench().getPreferenceStore());
	}
	
	
	
	/* 
	 * Set default values 
	 */
	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore preferenceStore =PlatformUI.getPreferenceStore();
		preferenceStore.setDefault(PreferenceConstants.LOCAL_PORT_NO, DEFAULT_LOCAL_PORT);
		preferenceStore.setDefault(PreferenceConstants.REMOTE_PORT_NO, DEFAULT_REMOTE_PORT);
		preferenceStore.setDefault(PreferenceConstants.USE_REMOTE_CONFIGURATION, DEFAULT_USE_REMOTE_CONFIGURATION_CHECK);
		setPreferenceStore(preferenceStore);
	}

	/* Creates contents of the preference page
	 * 
	 */
	@Override
	protected Control createContents(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.None);
		parentComposite.setToolTipText("Export Data");
		GridData parentCompositeData = new GridData(SWT.FILL, SWT.BEGINNING, true, true, 3, 3);
		parentCompositeData.grabExcessHorizontalSpace = true;
		
		parentCompositeData.grabExcessVerticalSpace = true;
		parentComposite.setLayout(new GridLayout(1, false));
		parentComposite.setLayoutData(parentCompositeData);
		
		HydroGroup grpServiceDetails = new HydroGroup(parentComposite, SWT.NONE);
		grpServiceDetails.setHydroGroupText("Port and Remote Host Details");
		
		GridLayout gl_grpServiceDetails = new GridLayout(1, false);
		GridData gd_grpServiceDetailsData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		grpServiceDetails.setLayout(new GridLayout(1,false));
		grpServiceDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpServiceDetails.getHydroGroupClientArea().setLayout(gl_grpServiceDetails);
		grpServiceDetails.getHydroGroupClientArea().setLayoutData(gd_grpServiceDetailsData);
		
		grpServiceDetailsCmposite = new Composite(grpServiceDetails.getHydroGroupClientArea(), SWT.NONE);
		GridData serviceGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		grpServiceDetailsCmposite.setLayout(new GridLayout(1,false));
		grpServiceDetailsCmposite.setLayoutData(serviceGridData);

		localPortNo = new IntegerFieldEditor(PreferenceConstants.LOCAL_PORT_NO, Messages.LOCAL_PORT_NO_LABEL, grpServiceDetailsCmposite, 5);
		localPortNo.getTextControl(grpServiceDetailsCmposite).addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				String value = ((Text)event.getSource()).getText();
				validatePortField(value,localPortNo,Messages.PORTNO_FIELD_VALIDATION);
			}
		});
		localPortNo.getTextControl(grpServiceDetailsCmposite).addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) { }
			@Override
			public void focusGained(FocusEvent event) {
				String value = ((Text)event.getSource()).getText();
				validatePortField(value,localPortNo,Messages.PORTNO_FIELD_VALIDATION);
			}
		});
		localPortNo.setPreferenceStore(getPreferenceStore());
		localPortNo.load();
		
		remotePortNo = new IntegerFieldEditor(PreferenceConstants.REMOTE_PORT_NO, Messages.REMOTE_PORT_NO_LABEL, grpServiceDetailsCmposite, 5);
		remotePortNo.getTextControl(grpServiceDetailsCmposite).addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				String value = ((Text)event.getSource()).getText();
				validatePortField(value,remotePortNo,Messages.PORTNO_FIELD_VALIDATION);
			}
		});
		remotePortNo.getTextControl(grpServiceDetailsCmposite).addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) { }
			@Override
			public void focusGained(FocusEvent event) {
				String value = ((Text)event.getSource()).getText();
				validatePortField(value,remotePortNo,Messages.PORTNO_FIELD_VALIDATION);
			}
		});
		remotePortNo.setPreferenceStore(getPreferenceStore());
		remotePortNo.load();
		
		new Label(grpServiceDetailsCmposite, SWT.None).setText(Messages.OVERRIDE_REMOTE_HOST_LABEL);
		Composite headerRemoteComposite = new Composite(grpServiceDetailsCmposite, SWT.None);
		useRemoteConfigBooleanFieldEditor = new BooleanFieldEditor(PreferenceConstants.USE_REMOTE_CONFIGURATION, "", SWT.DEFAULT, headerRemoteComposite);
		useRemoteConfigbutton = (Button) useRemoteConfigBooleanFieldEditor.getDescriptionControl(headerRemoteComposite);
		getPreferenceStore().setDefault(PreferenceConstants.USE_REMOTE_CONFIGURATION, false);
		useRemoteConfigBooleanFieldEditor.setPreferenceStore(getPreferenceStore());
		useRemoteConfigBooleanFieldEditor.load();
		
		addListenerToRemoteConfigBooleanEditor(headerRemoteComposite);
		
		
		remoteHostFieldEditor = new StringFieldEditor(PreferenceConstants.REMOTE_HOST, Messages.REMOTE_HOST_NAME_LABEL, grpServiceDetailsCmposite);
		remoteHostFieldEditor.getTextControl(grpServiceDetailsCmposite).addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validateRemoteHost();
			}
		});
		remoteHostFieldEditor.setPreferenceStore(getPreferenceStore());
		remoteHostFieldEditor.load();
		remoteHostFieldEditor.setErrorMessage(null);
		
		addFields(localPortNo);
		addFields(remotePortNo);
		addFields(remoteHostFieldEditor);
		
		return null;
	}
	
	/* 
	 * Saves the preferences on ok button click
	 */
	@Override
	public boolean performOk() { 
		localPortNo.getStringValue();
		localPortNo.store();
		remotePortNo.store();
		remoteHostFieldEditor.store();
		useRemoteConfigBooleanFieldEditor.store();
		return super.performOk();
	}

	/* 
	 * Apply the preferences 
	 */
	@Override
	protected void performApply() {
		localPortNo.store();
		remotePortNo.store();
		useRemoteConfigBooleanFieldEditor.store();
		remoteHostFieldEditor.store();
	}
	/* 
	 * Set defaults values
	 */
	@Override
	protected void performDefaults() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		localPortNo.setStringValue(preferenceStore.getDefaultString(PreferenceConstants.LOCAL_PORT_NO));
		remotePortNo.setStringValue(preferenceStore.getDefaultString(PreferenceConstants.REMOTE_PORT_NO));
		useRemoteConfigBooleanFieldEditor.loadDefault();
		super.performDefaults();
}
	
	/**
	 * @param value
	 * @param editor
	 * @param message
	 * Validates port values
	 */
	private void validatePortField(String value, IntegerFieldEditor editor, String message){
		if(StringUtils.isBlank(value) || !value.matches(Constants.PORT_VALIDATION_REGEX)){
			showErrorMessage(editor, message,false);
		}else{
			showErrorMessage(editor, null,true);
			checkState();
		}
	}
	/**
	 * @param editor
	 * @param message
	 * @param validState
	 * Display error message
	 */
	
	private void showErrorMessage(IntegerFieldEditor editor, String message,boolean validState) {
		setErrorMessage(message);
		editor.setErrorMessage(message);
		setValid(validState);
	}

	
	/**
	 * Check if all fields are valid
	 * 
	 */
	private void checkState() {
		if(editorList != null){
			int size = editorList.size();
			for(int i=0; i<size; i++){
				FieldEditor fieldEditor = editorList.get(i);
				if(StringUtils.isNotBlank(((StringFieldEditor)fieldEditor).getErrorMessage())){
					setErrorMessage(((StringFieldEditor)fieldEditor).getErrorMessage());
					setValid(false);
					break;
				}else{
					setValid(true);
				}
			}
		}
	}
	

	/**
	 * Add fields to list
	 */
	private void addFields(FieldEditor editor){
		if (editorList == null) {
			editorList = new ArrayList<>();
		}
		editorList.add(editor);
	}
	
	/**
	 * Listner on check box to validate remote host 
	 */
	private void addListenerToRemoteConfigBooleanEditor(Composite headerRemoteComposite) {
		useRemoteConfigbutton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validateRemoteHost();
			}
		});
	}
	/**
	 * check if remote host name is valid
	 */
	private void validateRemoteHost() {
		if(useRemoteConfigbutton.getSelection() && StringUtils.isEmpty(remoteHostFieldEditor.getStringValue())){
			remoteHostFieldEditor.setErrorMessage(Messages.BLANK_REMOTE_HOST_NAME_ERROR);
			checkState();
		}else{
			setErrorMessage(null);
			remoteHostFieldEditor.setErrorMessage("");
			setValid(true);
			checkState();;
		}
	}
	
	/* 
	 * Returns preference store
	 */
	@Override
	public IPreferenceStore getPreferenceStore() {
		return PlatformUI.getPreferenceStore();
	}
}
