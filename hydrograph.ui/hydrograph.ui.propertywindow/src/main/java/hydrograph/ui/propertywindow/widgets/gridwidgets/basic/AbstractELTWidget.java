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

 
package hydrograph.ui.propertywindow.widgets.gridwidgets.basic;

import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.IELTListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 18, 2015
 * 
 */

public abstract class AbstractELTWidget {
	protected Widget widget;
	protected Object jfaceWidgets;

	/**
	 * Attach widget.
	 * 
	 * @param container
	 *            the container
	 */
	public abstract void attachWidget(Composite container);
	
	/**
	 * Attach listener.
	 * 
	 * @param ELTListener
	 *            the ELT listener
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 * @param helpers
	 *            the helpers
	 * @param widgets
	 *            the widgets
	 * @throws Exception
	 *             the exception
	 */
	public void attachListener(IELTListener ELTListener,PropertyDialogButtonBar propertyDialogButtonBar,ListenerHelper helpers, Widget... widgets) throws Exception{
		if(widget != null)
			widget.addListener(ELTListener.getListenerType(), ELTListener.getListener(propertyDialogButtonBar, helpers, widgets));
		else
			throw new Exception("IELTWidget.widget object has set in sub class ");
	}
	
	public Widget getSWTWidgetControl(){
		return widget;
	}
	
	public Object getJfaceWidgetControl(){
		return jfaceWidgets;
	}
}
