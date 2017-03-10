/********************************************************************************
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
 ******************************************************************************/

package hydrograph.ui.graph.execution.tracking.preferences;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.Activator;

/**
 * 
 * This class adds Job Run preference in preference window.
 * 
 * @author Bitwise
 *
 */
public class JobRunPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String DEFUALT_LOG_LEVEL = "Info";
	public static final String SAVE_JOB_BEFORE_RUN_PREFRENCE = "save_job__before_run_prefrence";
	public static final String LOG_LEVEL_PREFERENCE="log_level_preference";
	
	private JobRunPreferenceComposite jobRunPreferenceComposite;

	public JobRunPreference() {
		super();
		setPreferenceStore(PlatformUI.getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		getPreferenceStore().setDefault(SAVE_JOB_BEFORE_RUN_PREFRENCE, MessageDialogWithToggle.PROMPT);
		getPreferenceStore().setDefault(LOG_LEVEL_PREFERENCE,DEFUALT_LOG_LEVEL);
		String value = Activator.getDefault().getPreferenceStore()
				.getString(SAVE_JOB_BEFORE_RUN_PREFRENCE);
		if (StringUtils.equals(MessageDialogWithToggle.ALWAYS, value)) {
			getPreferenceStore().setValue(SAVE_JOB_BEFORE_RUN_PREFRENCE, value);
		} else {
			getPreferenceStore().setValue(SAVE_JOB_BEFORE_RUN_PREFRENCE, MessageDialogWithToggle.PROMPT);
		}
		
		
		String log_level_value= Activator.getDefault().getPreferenceStore().getString(JobRunPreference.LOG_LEVEL_PREFERENCE);
		
		if (StringUtils.isNotBlank(log_level_value)) {
			getPreferenceStore().setValue(LOG_LEVEL_PREFERENCE, log_level_value);
		} else {
			getPreferenceStore().setValue(LOG_LEVEL_PREFERENCE, DEFUALT_LOG_LEVEL);
		}
		
		String consoleBufferValue = Activator.getDefault().getPreferenceStore().getString(Constants.CONSOLE_BUFFER_SIZE_PREFERANCE_NAME);
		if(StringUtils.isNotBlank(consoleBufferValue)){
			getPreferenceStore().setValue(Constants.CONSOLE_BUFFER_SIZE_PREFERANCE_NAME, consoleBufferValue);
		}else{
			getPreferenceStore().setValue(Constants.CONSOLE_BUFFER_SIZE_PREFERANCE_NAME, Constants.DEFUALT_CONSOLE_BUFFER_SIZE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createContents(org
	 * .eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		getPreferenceStore().getString(SAVE_JOB_BEFORE_RUN_PREFRENCE);
		jobRunPreferenceComposite = new JobRunPreferenceComposite(parent, SWT.NONE,
				getPreferenceStore().getString(SAVE_JOB_BEFORE_RUN_PREFRENCE),
				getPreferenceStore().getString(LOG_LEVEL_PREFERENCE),
				getPreferenceStore().getString(Constants.CONSOLE_BUFFER_SIZE_PREFERANCE_NAME),this);
		jobRunPreferenceComposite.setLayout(new GridLayout(1, false));
		jobRunPreferenceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean returnCode = super.performOk();
		String selection = MessageDialogWithToggle.PROMPT;
		if (jobRunPreferenceComposite.getAlwaysButtonSelection()) {
			selection = MessageDialogWithToggle.ALWAYS;
		}
		Activator.getDefault().getPreferenceStore().setValue(SAVE_JOB_BEFORE_RUN_PREFRENCE, selection);
		Activator.getDefault().getPreferenceStore().setValue(LOG_LEVEL_PREFERENCE,jobRunPreferenceComposite.getLoglevel());
		Activator.getDefault().getPreferenceStore().setValue(Constants.CONSOLE_BUFFER_SIZE_PREFERANCE_NAME, jobRunPreferenceComposite.getConsoleBufferSize());
		return returnCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {
	}

	@Override
	protected void performDefaults() {
		jobRunPreferenceComposite.storeDefaults();
	}
}