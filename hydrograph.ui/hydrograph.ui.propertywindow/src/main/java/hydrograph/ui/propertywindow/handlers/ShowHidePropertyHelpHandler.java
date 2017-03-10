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

 
package hydrograph.ui.propertywindow.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

public class ShowHidePropertyHelpHandler extends AbstractHandler implements IHandler,IElementUpdater{
	private UIElement element;
	
	private static ShowHidePropertyHelpHandler INSTANCE;
	
	private boolean ShowHidePropertyHelpChecked;
	
	public ShowHidePropertyHelpHandler(){
		INSTANCE = this;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		if(ShowHidePropertyHelpChecked){
			element.setChecked(false);
			ShowHidePropertyHelpChecked=false;
		}else{
			element.setChecked(true);
			ShowHidePropertyHelpChecked=true;
		}
		
		return null;
	}
	
	@Override
	public void updateElement(UIElement element, Map parameters) {
		this.element = element;
	}
	
	
	public static ShowHidePropertyHelpHandler getInstance(){
		return INSTANCE;
	}
	
	public boolean isShowHidePropertyHelpChecked(){
		return ShowHidePropertyHelpChecked;
	}
}
