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

package hydrograph.ui.expression.editor.buttons;

import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.evaluate.EvaluateDialog;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;

public class EvaluateExpressionToolButton extends Button {

	private static final String ITEM_TEXT = "Evaluate";
	private StyledText expressionEditor;


	public EvaluateExpressionToolButton(Composite composite, int style,StyledText expressionEditor) {
		super(composite, style);
		setText(ITEM_TEXT);
		setToolTipText(Messages.EVALUATE_BUTTON_TOOLTIP);
		this.expressionEditor=expressionEditor;
		addSelectionListener();
	}


	private void addSelectionListener() {
		this.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				EvaluateDialog dialog = new EvaluateDialog(Display.getCurrent().getActiveShell(), expressionEditor);
				setEnabled(false);
				dialog.open();
				if (!e.widget.isDisposed())
					setEnabled(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {/*Do-Nothing*/}
		});
	}


	protected void checkSubclass () {
		// Allow subclassing
	}
}
