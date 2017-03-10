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


import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;


/**
 * The listener interface for receiving DirectoryDialogSelection events. The class that is interested in processing a
 * DirectoryDialogSelection event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>add DirectoryDialogSelectionListener<code> method. When
 * the DirectoryDialogSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see DirectoryDialogSelectionEvent
 */
public class DirectoryDialogSelectionListener implements IELTListener{
	private Logger LOGGER = LogFactory.INSTANCE.getLogger(DirectoryDialogSelectionListener.class);
	private ControlDecoration txtDecorator;
	@Override
	public int getListenerType() {
		return SWT.Selection;
	}

	@Override
	public Listener getListener(final PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helpers,
			final Widget... widgets) {
		final Button button = ((Button) widgets[0]);
		button.getShell();
		if (helpers != null) {
			txtDecorator = (ControlDecoration) helpers.get(HelperType.CONTROL_DECORATION);
		}

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				LOGGER.debug("Launching Directory Dialog for selecting directory path");
				if (event.type == SWT.Selection) {
					String path = null;
					DirectoryDialog filedialog = new DirectoryDialog(button.getShell(), SWT.None);
					filedialog.setFilterPath(((Text) widgets[1]).getText());
					path=filedialog.open();
					if (StringUtils.isNotEmpty(path)) {
						((Text) widgets[1]).setText(path);
						propertyDialogButtonBar.enableApplyButton(true);
						txtDecorator.hide();
					}
				}
			}
		};
		return listener;
	}
	
}
