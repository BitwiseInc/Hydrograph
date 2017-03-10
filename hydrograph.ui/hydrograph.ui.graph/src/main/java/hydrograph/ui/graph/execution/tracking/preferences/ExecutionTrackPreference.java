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
package hydrograph.ui.graph.execution.tracking.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.graph.Activator;
import hydrograph.ui.graph.execution.tracking.utils.TrackingDisplayUtils;



/**
 * @author Bitwise
 * 
 *Create preference page of execution tracking and save prefernces
 *
 */
public class ExecutionTrackPreference extends PreferencePage implements IWorkbenchPreferencePage {
	private ExecutionTrackingPreferanceComposite executionTrackingPreferanceComposite;

	public ExecutionTrackPreference() {
		setPreferenceStore(PlatformUI.getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setDefault(ExecutionPreferenceConstants.EXECUTION_TRACKING, true);
		preferenceStore.setDefault(ExecutionPreferenceConstants.TRACKING_LOG_PATH,
				TrackingDisplayUtils.INSTANCE.getInstallationPath());
		
		setPreferenceStore(preferenceStore);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 * 
	 * set prefernces to defaults
	 */
	@Override
	protected void performDefaults() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		ExecutionTrackingPreferencesDataStructure defalultPreferences = new ExecutionTrackingPreferencesDataStructure();
		defalultPreferences.setEnableTrackingCheckBox(true);
		defalultPreferences.setOverrideRemoteHostButton(false);
		defalultPreferences.setTrackingLogPathText(preferenceStore.getDefaultString(ExecutionPreferenceConstants.TRACKING_LOG_PATH));
		executionTrackingPreferanceComposite.setDefaults(defalultPreferences);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		ExecutionTrackingPreferencesDataStructure preference = new ExecutionTrackingPreferencesDataStructure();
		preference.setEnableTrackingCheckBox(
				getPreferenceStore().getBoolean(ExecutionPreferenceConstants.EXECUTION_TRACKING));
		preference.setTrackingLogPathText(getPreferenceStore().getString(ExecutionPreferenceConstants.TRACKING_LOG_PATH));

		executionTrackingPreferanceComposite = new ExecutionTrackingPreferanceComposite(parent, SWT.NONE, this,
				preference);
		executionTrackingPreferanceComposite.setLayout(new GridLayout(1, false));
		executionTrackingPreferanceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 * Save prefernece on ok button
	 */
	@Override
	public boolean performOk() {
		ExecutionTrackingPreferencesDataStructure preference = executionTrackingPreferanceComposite.getPreferences();
		getPreferenceStore().setValue(ExecutionPreferenceConstants.EXECUTION_TRACKING,preference.isEnableTrackingCheckBox());
		getPreferenceStore().setValue(ExecutionPreferenceConstants.EXECUTION_TRACKING,preference.isEnableTrackingCheckBox());
		getPreferenceStore().setValue(ExecutionPreferenceConstants.TRACKING_LOG_PATH,
				preference.getTrackingLogPathText());
		return super.performOk();
	}

}