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

 
package hydrograph.ui.propertywindow.messagebox;

import hydrograph.ui.propertywindow.messages.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 24, 2015
 * 
 */

public class ConfirmCancelMessageBox {
	private Composite container;
	private MessageBox messageBox;
	
	private ConfirmCancelMessageBox(){
		
	}
	
	/**
	 * Instantiates a new confirm cancel message box.
	 * 
	 * @param container
	 *            the container
	 */
	public ConfirmCancelMessageBox(Composite container){
		this.container =container;
		createMessageBox();
	}
	
	private void createMessageBox(){
		Shell shell=container.getShell();
		int style = SWT.APPLICATION_MODAL | SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION;
		messageBox = new MessageBox(shell,style);
		messageBox.setText("Confirm"); //$NON-NLS-1$
		messageBox.setMessage(Messages.MessageBeforeClosingWindow);
	}
	
	public MessageBox getMessageBox(){
		return messageBox;
	}
}
