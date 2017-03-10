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

package hydrograph.ui.propertywindow.widgets.utility;

import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;

import org.eclipse.swt.widgets.Button;


/**
 * This class is used for providing layout to the Schema Tab Buttons
 *  
 * @author  Bitwise
 *
 */
public class SchemaButtonsSyncUtility {
	
	
	
	public static SchemaButtonsSyncUtility INSTANCE = new SchemaButtonsSyncUtility();

	public void buttonSize(Button button , Integer macButtonWidth , Integer macButtonHeight , Integer windowButtonWidth , Integer windowButtonHeight )
	{
			if(OSValidator.isMac())
			{
				button.setSize(macButtonWidth, macButtonHeight);
			} else {
				button.setSize(windowButtonWidth, windowButtonHeight);
			}
	}
	
	
	public void buttonSize(ELTDefaultButton button , Integer macButtonWidth , Integer macButtonHeight , Integer windowButtonWidth , Integer windowButtonHeight )
	{
			if(OSValidator.isMac())
			{
				button.buttonWidth(macButtonWidth);
				button.buttonHeight(macButtonHeight);
			} else {
				button.buttonWidth(windowButtonWidth);
				button.buttonHeight(windowButtonHeight);
			}
	}
}
