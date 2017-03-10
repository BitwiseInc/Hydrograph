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



package hydrograph.ui.propertywindow.widgets.listeners.grid;

import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.MouseActionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;

public class DisposeSchemaGridListener extends MouseActionListener{
	public int getListenerType() {
		return SWT.Dispose;
	}
	
	@Override
	public void mouseAction(PropertyDialogButtonBar propertyDialogButtonBar,
			ListenerHelper helpers, Event event, Widget... widgets) {
		   Table table=(Table)event.widget;
		   Shell tip=(Shell) table.getData("tip");
		   Label label=(Label) table.getData("label");
		if(tip!=null) 
		{
		 tip.dispose();
         tip = null;
         label = null;
		}
	}
}
