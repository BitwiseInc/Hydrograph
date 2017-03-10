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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.swt.customwidget.HydroGroup;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.graph.Messages;

/**
 * @author Bitwise This class created the preference page for job run.
 *
 */
public class JobRunPreferenceComposite extends Composite {

	private static final String REGEX_POSITIVE_INTEGER_ONLY = "[\\d]*";
	private static final String HASH_REGEX = "#";
	private Button btnRadioButtonAlways;
	private Button btnRadioButtonPrompt;
    private CCombo ccLogLevels;
    private Text textWidget;
    private JobRunPreference jobRunPreference;
    
	JobRunPreferenceComposite(Composite parent, int none, String selection,String logLevel, String bufferSize, JobRunPreference jobRunPreference) {
		super(parent, none);
		setLayout(new GridLayout(1, false));

		createSaveJobPromtGroup(selection);
		
		createLogLevelGroup(logLevel);
		
		createConsoleBufferWidget(bufferSize);
		
		this.jobRunPreference = jobRunPreference;
	}


	/**
	 * @param selection
	 */
	private void createSaveJobPromtGroup(String selection) {
		HydroGroup hydroGroup = new HydroGroup(this, SWT.NONE);
		hydroGroup.setHydroGroupText(Messages.SAVE_JOBS_BEFORE_LAUNCHING_MESSAGE);
		hydroGroup.setLayout(new GridLayout(1, false));
		hydroGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		hydroGroup.getHydroGroupClientArea().setLayout(new GridLayout(2, false));
		hydroGroup.getHydroGroupClientArea().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		btnRadioButtonAlways = new Button(hydroGroup.getHydroGroupClientArea(), SWT.RADIO);
		btnRadioButtonAlways.setText(StringUtils.capitalize((MessageDialogWithToggle.ALWAYS)));

		btnRadioButtonPrompt = new Button(hydroGroup.getHydroGroupClientArea(), SWT.RADIO);
		btnRadioButtonPrompt.setText(StringUtils.capitalize(MessageDialogWithToggle.PROMPT));

		if (StringUtils.equals(selection, MessageDialogWithToggle.ALWAYS)) {
			btnRadioButtonAlways.setSelection(true);
		} else {
			btnRadioButtonPrompt.setSelection(true);
		}
	}

	/**
	 * @param logLevel 
	 * 
	 */
	private void createLogLevelGroup(String logLevel) {
		
		HydroGroup hydroGroup = new HydroGroup(this, SWT.NONE);
		
		hydroGroup.setHydroGroupText(Messages.LOG_LEVEL_PREF_MESSAGE);
		hydroGroup.setLayout(new GridLayout(1, false));
		hydroGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		hydroGroup.getHydroGroupClientArea().setLayout(new GridLayout(2, false));
		
		Label label = new Label(hydroGroup.getHydroGroupClientArea(), SWT.NONE);
		
		label.setText(Messages.LOG_LEVEL_CONSOLE_PREF_MESSAGE);
		
		ccLogLevels=new CCombo(hydroGroup.getHydroGroupClientArea(), SWT.BORDER);
		GridData gd_ccLogLevels = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_ccLogLevels.widthHint = 300;
		if(OSValidator.isMac()){
			gd_ccLogLevels.heightHint=20;
		}
		ccLogLevels.setLayoutData(gd_ccLogLevels);
		
		ccLogLevels.setItems(Messages.COMBO_LOG_LEVELS.split(HASH_REGEX));
		
		ccLogLevels.setText(logLevel);
		
	}
	
	/**
	 * Create console buffer widget
	 * @param bufferSize
	 */
	private void createConsoleBufferWidget(String bufferSize){
		HydroGroup hydroGroup = new HydroGroup(this, SWT.NONE);
		
		hydroGroup.setHydroGroupText(Messages.HYDROGRAPH_CONSOLE_PREFERANCE_PAGE_GROUP_NAME);
		hydroGroup.setLayout(new GridLayout(1, false));
		hydroGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		hydroGroup.getHydroGroupClientArea().setLayout(new GridLayout(2, false));
		
		Label label = new Label(hydroGroup.getHydroGroupClientArea(), SWT.NONE);
		
		label.setText(Messages.PREFERANCE_CONSOLE_BUFFER_SIZE);
		
		textWidget = new Text(hydroGroup.getHydroGroupClientArea(), SWT.BORDER);
		textWidget.setText(bufferSize);
		textWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		textWidget.setTextLimit(6);
		
		attachConsoleBufferValidator();
		
		Composite purgeComposite = new Composite(hydroGroup.getHydroGroupClientArea(), SWT.NONE);
		purgeComposite.setLayout(new GridLayout(2, false));
		purgeComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		
		Label lblNote = new Label(purgeComposite, SWT.TOP | SWT.WRAP);
		lblNote.setText(Messages.PREFERANCE_PAGE_NOTE);
		FontData fontData = lblNote.getFont().getFontData()[0];
		Font font = new Font(purgeComposite.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblNote.setFont(font);
		Label lblmsg = new Label(purgeComposite, SWT.TOP | SWT.WRAP);
		lblmsg.setText(Messages.UI_PERFORMANCE_NOTE_IN_CASE_OF_CHANGE_IN_BUFFER_SIZE);
		
	}

	private void attachConsoleBufferValidator() {
		textWidget.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				
				String text = textWidget.getText();
				setPreferanceError(null);
				if(StringUtils.isBlank(text)){
					setPreferanceError(Messages.PREFERANCE_ERROR_EMPTY_CONSOLE_BUFFER_FIELD);
					return;
				}
				
				if(!isValidNumber(text)){
					setPreferanceError(Messages.PREFERANCE_ERROR_INVALID_CONSOLE_BUFFER_INPUT);
					return;
				}
				
				int value=Integer.parseInt(text);
				if(!isValidConsoleBufferSize(value)){
					setPreferanceError(Messages.PREFERANCE_ERROR_INVALID_CONSOLE_BUFFER_INPUT);
					return;
				}else{
					setPreferanceError(null);
				}
			}
		});
	}
	
	protected boolean isValidConsoleBufferSize(int value) {
		if(value<1000 || value>40000){
			return false;
		}else{
			return true;
		}
	}


	private boolean isValidNumber(String text) {
		Matcher matchs=Pattern.compile(REGEX_POSITIVE_INTEGER_ONLY).matcher(text);
		if(!matchs.matches()){
			return false;
		}else{
			return true;
		}
	}


	private void setPreferanceError(String errorMessage) {
		if(!StringUtils.isBlank(errorMessage)){
			jobRunPreference.setErrorMessage(errorMessage);
			jobRunPreference.setValid(false);
		}else{
			jobRunPreference.setErrorMessage(null);
			jobRunPreference.setValid(true);
		}
		
	}
	
	/**
	 * @return selection of radio button
	 */
	public boolean getAlwaysButtonSelection() {
		return btnRadioButtonAlways.getSelection();
	}

	/**
	 * Set defaults values of job run preference
	 */
	public void storeDefaults() {
		btnRadioButtonPrompt.setSelection(true);
		btnRadioButtonAlways.setSelection(false);
		ccLogLevels.select(3);
		textWidget.setText(Constants.DEFUALT_CONSOLE_BUFFER_SIZE);
	}
	
	/**
	 * @return buffer size
	 */
	public String getConsoleBufferSize(){
		return textWidget.getText();
	}
	
	/**
	 * 
	 * @return selected log level 
	 */
	public String getLoglevel(){
		return ccLogLevels.getText();
	}
}