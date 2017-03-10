
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

package hydrograph.ui.graph.dialog;
import hydrograph.ui.graph.Activator;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.execution.tracking.preferences.JobRunPreference;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SaveJobFileBeforeRunDialog extends Dialog {

	private String messageText;
	private Button alwaysSaveCheckButton;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SaveJobFileBeforeRunDialog(Shell parentShell,String messageText) {
		super(parentShell);
		this.messageText=messageText;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super .createDialogArea(parent);
		container.getShell().setText(Messages.CONFIRM_TO_SAVE_JOB_BEFORE_RUN_DIALOG_TITLE);
		
		container.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite messageComposite = new Composite(composite, SWT.NONE);
		messageComposite.setLayout(new GridLayout(2, false));
		GridData gd_messageComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_messageComposite.heightHint = 34;
		messageComposite.setLayoutData(gd_messageComposite);
		
		Label iconLabel = new Label(messageComposite, SWT.NONE);
		iconLabel.setImage(getSWTImage());
		
		Label messageLabel = new Label(messageComposite, SWT.WRAP);
		messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		messageLabel.setText(messageText);
		new Label(composite, SWT.NONE);
		
		Composite alwaysSaveComposite = new Composite(composite, SWT.NONE);
		alwaysSaveComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		alwaysSaveComposite.setLayout(new GridLayout(3, false));
		
		Label blankSpaceLabel = new Label(alwaysSaveComposite, SWT.NONE);
		
		
		alwaysSaveCheckButton = new Button(alwaysSaveComposite, SWT.CHECK);
		alwaysSaveCheckButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		alwaysSaveCheckButton.setText("Always save job before running");
		new Label(alwaysSaveComposite, SWT.NONE);
		new Label(composite, SWT.NONE);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.YES_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.NO_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(480, 192);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if(alwaysSaveCheckButton.getSelection()){
			Activator.getDefault().getPreferenceStore().setValue(JobRunPreference.SAVE_JOB_BEFORE_RUN_PREFRENCE, MessageDialogWithToggle.ALWAYS);
		}
		super.okPressed();
	}

	private Image getSWTImage() {
		Shell shell = getShell();
		final Display display;
		if (shell == null || shell.isDisposed()) {
			shell = getParentShell();
		}
		if (shell == null || shell.isDisposed()) {
			display = Display.getCurrent();
			// The dialog should be always instantiated in UI thread.
			// However it was possible to instantiate it in other threads
			// (the code worked in most cases) so the assertion covers
			// only the failing scenario. See bug 107082 for details.
			Assert.isNotNull(display,
					"The dialog should be created in UI thread"); //$NON-NLS-1$
		} else {
			display = shell.getDisplay();
		}

		final Image[] image = new Image[1];
		display.syncExec(new Runnable() {
			public void run() {
				image[0] = display.getSystemImage(SWT.ICON_QUESTION);
			}
		});

		return image[0];

	}
}


