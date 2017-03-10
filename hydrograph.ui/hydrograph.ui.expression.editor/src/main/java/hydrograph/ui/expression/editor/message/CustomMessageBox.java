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
package hydrograph.ui.expression.editor.message;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class CustomMessageBox  {

	private int messageType;
	private String message;
	private String title;

	public CustomMessageBox(int messageType,String message,String title) {
		this.messageType=messageType;
		this.message=message;
		this.title=title;
	}
	
	public void open(){
		if(messageType==SWT.ERROR){
			MessageDialog.setDefaultOrientation(SWT.NONE);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
		}
		if(messageType==SWT.ICON_INFORMATION){
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), title, message);
		}
		if(messageType==SWT.ICON_WARNING){
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), title, message);
		}
	}
	
}
