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

 
package hydrograph.ui.propertywindow.widgets.listeners;

import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;


// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 18, 2015
 * 
 */

public interface IELTListener {
	
	/**
	 *  This method returns the listener type. 
	 *  e.g  SWT.Modify
	 *  
	 *  @return int
	 * 			
	 */
	public int getListenerType();
	
	/**
	 * Gets the listener.
	 * 
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 * @param helpers
	 *            the helpers
	 * @param widgets
	 *            the widgets
	 * @return the listener
	 */
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar,ListenerHelper helpers,Widget... widgets);
}
