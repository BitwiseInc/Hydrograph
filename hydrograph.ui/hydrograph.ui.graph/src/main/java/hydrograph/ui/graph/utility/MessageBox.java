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

package hydrograph.ui.graph.utility;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * Message box UI
 * 
 * @author Bitwise
 *
 */
public class MessageBox {
	
	public static MessageBox INSTANCE = new MessageBox(); 
	
	public static String ERROR="Error";
	public static String WARNING="Warning";
	public static String INFO="Information";
	private MessageBox() {
	}
	
	private int getMessageBoxIcon(String messageBoxType){
		if(StringUtils.equals(ERROR, messageBoxType)){
			return SWT.ICON_ERROR;
		}else if(StringUtils.equals(WARNING, messageBoxType)){
			return SWT.ICON_WARNING;
		}else{
			return SWT.ICON_INFORMATION;
		}
	}
	
	/**
	 * 
	 * Show message box with ok button
	 * 
	 * @param messageBoxTitle - Message box title
	 * @param message - Message to be displayed 
	 */
	public void showMessage(String messageBoxTitle, String message) {
		int shellStyle= SWT.APPLICATION_MODAL | SWT.OK | getMessageBoxIcon(messageBoxTitle);
		org.eclipse.swt.widgets.MessageBox messageBox = new org.eclipse.swt.widgets.MessageBox(Display.getDefault().getActiveShell(),shellStyle);
		messageBox.setText(messageBoxTitle);
		messageBox.setMessage(message);
		messageBox.open();
	}
}
