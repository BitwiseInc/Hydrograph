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

 
package hydrograph.ui.propertywindow.runconfig;

import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;


public class EmptyTextListener implements ModifyListener {

	private String fieldName;
	private ControlDecoration errorDecorator;

	public EmptyTextListener(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public void modifyText(ModifyEvent event) {
		Text textBox = (Text)event.getSource();
		Button btnRemoteMode = (Button)textBox.getData(RunConfigDialog.SELECTION_BUTTON_KEY);
		String txt= textBox.getText();

		if (StringUtils.isBlank(txt)) {
			if(errorDecorator==null){
			errorDecorator = WidgetUtility.addDecorator(textBox,Messages.bind(Messages.EMPTY_FIELD, fieldName));
			}
			if(btnRemoteMode!=null){
				if(btnRemoteMode.getSelection()){
					errorDecorator.show();
				}else
					errorDecorator.hide();
			}else{
				errorDecorator.show();
			}
			errorDecorator.setMarginWidth(3);

		} else {
			if(errorDecorator!=null)
				errorDecorator.hide();

		}

	}
	
	public ControlDecoration getErrorDecoration(){
		return errorDecorator;
	}

}
