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
package hydrograph.ui.propertywindow.ftp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

/**
 * The Class FTPOperationConfigUtility
 * @author Bitwise
 *
 */
public class FTPOperationConfigUtility {
	private FTPOperationConfigUtility INSTANCE;
	
	public FTPOperationConfigUtility() {
		INSTANCE=this;
	}
	
	/**
	 * @return instance of FTPOperationConfigUtility
	 */
	public FTPOperationConfigUtility getInstance() {
		return INSTANCE;
	}
	
	
	/**
	 * @param control
	 * @return
	 */
	public Control addLocalRemoteRemoveFiles(Composite control){
		Composite composite = new Composite(control, SWT.BORDER);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		ftpWidgetUtility.createLabel(composite, "Local Path");
		Text localPathTxt = (Text) ftpWidgetUtility.createText(composite, "", SWT.BORDER);
		Button localPathBrwsBtn = new Button(composite, SWT.NONE);
		localPathBrwsBtn.setText("...");
		selectionListener(localPathBrwsBtn, localPathTxt);
		
		ftpWidgetUtility.createLabel(composite, "File Name");
		ftpWidgetUtility.createText(composite, "", SWT.BORDER);
		
		
		return composite;
	}
	
	private void selectionListener(Button button, Text txt){
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog filedialog = new FileDialog(button.getShell(), SWT.None);
				txt.setText(filedialog.open());
			}
		});
	}

}
