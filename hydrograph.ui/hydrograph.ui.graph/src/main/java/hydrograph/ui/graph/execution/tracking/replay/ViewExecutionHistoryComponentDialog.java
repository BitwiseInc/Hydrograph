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
package hydrograph.ui.graph.execution.tracking.replay;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.graph.Messages;

/**
 * The Class ReplayComponentDialog use to create dialog to manage extra and missed components List.
 * 
 * @author Bitwise
 */
public class ViewExecutionHistoryComponentDialog extends Dialog{
	private Text text;
	private List<String> extraComponentList;
	private List<String> missedComponentList;
	
	private int extraCompcount = 1;
	private int missedCompcount = 1;

	public ViewExecutionHistoryComponentDialog(Shell parentShell, List<String> extraComponentList, List<String> missedComponentList) {
		super(parentShell);
		setShellStyle(SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
		this.extraComponentList = extraComponentList;
		this.missedComponentList = missedComponentList;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Component Details");
		container.setLayout(new GridLayout(1, false));
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_scrolledComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_scrolledComposite.heightHint = 289;
		gd_scrolledComposite.widthHint = 571;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		text = new Text(scrolledComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
		StringBuilder stringBuilder = new StringBuilder();
		
		if(extraComponentList != null && extraComponentList.size() > 0){
			stringBuilder.append(Messages.REPLAY_EXTRA_COMPONENTS + "\n");
			extraComponentList.forEach(componentName -> { stringBuilder.append(extraCompcount + ". " + componentName + "\n");
				extraCompcount++;
			});
		}
		
		if(missedComponentList != null && missedComponentList.size() > 0 && !missedComponentList.isEmpty()){
			stringBuilder.append(Messages.REPLAY_MISSING_COMPONENTS + "\n");
			missedComponentList.forEach(componentName -> { stringBuilder.append(missedCompcount + "." + componentName + "\n");
				missedCompcount++;
			});
		}
		
		text.setText(stringBuilder.toString());
		
		scrolledComposite.setContent(text);
		
		
		return super.createDialogArea(parent);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID, "OK", false);
		Button closeButton = createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);		
	}
}
