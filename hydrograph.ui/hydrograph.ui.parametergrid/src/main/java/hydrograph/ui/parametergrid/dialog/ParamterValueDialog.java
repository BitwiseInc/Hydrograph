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

package hydrograph.ui.parametergrid.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * The dialog box to take multi-line parameter value from user
 * 
 * @author Bitwise
 *
 */
public class ParamterValueDialog extends Dialog {
	private Text text;
	private String inputString;
	private String initialValue;
	private final String DIALOG_NOTE = "Note: Values entered in this text area will be saved without new line characters";

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */@Deprecated
	public ParamterValueDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.TITLE |  SWT.RESIZE );
	}
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ParamterValueDialog(Shell parentShell,String initalValue) {
		super(parentShell);
		setShellStyle(SWT.TITLE |  SWT.RESIZE );
		this.initialValue = initalValue;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		getShell().setText("Parameter value dialog");
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		text = new Text(container, SWT.BORDER | SWT.MULTI | SWT.VERTICAL | SWT.HORIZONTAL);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label lblNoteVaulesEntered = new Label(container, SWT.NONE);
		lblNoteVaulesEntered.setText(DIALOG_NOTE);
		
		if(StringUtils.isNotBlank(this.initialValue))
			text.setText(this.initialValue);
		
		getShell().setMinimumSize(getInitialSize());
		return container;
	}

	/**
	 * 
	 * Get parameter value
	 * 
	 * @return String
	 */
	public String getParamterValue(){
		return inputString;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		// create a layout with spacing and margins appropriate for the font
		// size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 0; // this is incremented by createButton
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN );
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN - 4);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING -2);
		
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
				| GridData.VERTICAL_ALIGN_CENTER);
		
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		
		// Add the buttons to the button bar.
		createButtonsForButtonBar(composite);
		return composite;
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(474, 279);
	}
	
	@Override
	protected void okPressed() {
		inputString = text.getText();
		super.okPressed();
	}
}
