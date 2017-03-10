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


import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;


/**
 * The listener interface for receiving ELTFileDialogSelection events. The class that is interested in processing a
 * ELTFileDialogSelection event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTFileDialogSelectionListener<code> method. When
 * the ELTFileDialogSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTFileDialogSelectionEvent
 */
public class ELTFileDialogSelectionListener implements IELTListener{

	Shell shell;
	private ControlDecoration txtDecorator;
	private Component currentComponent;
	private String[] filterFileExtensions;
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
			currentComponent = (Component) helpers.get(HelperType.CURRENT_COMPONENT);
			filterFileExtensions=(String[]) helpers.get(HelperType.FILE_EXTENSION);
			
		}

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.Selection) {
					File file = null;
					String path = null;
					FileDialog filedialog = new FileDialog(button.getShell(), SWT.None);
					filedialog.setFilterExtensions(filterFileExtensions);
					filedialog.setFileName(((Text) widgets[1]).getText());
						path = filedialog.open();
						if(path!=null){
						file = new File(path);
						if (StringUtils.isNotEmpty(path)) {
							((Text) widgets[1]).setText(file.getAbsolutePath());
							propertyDialogButtonBar.enableApplyButton(true);
							txtDecorator.hide();
						}
					}
				}
			}
		};
		return listener;
	}
}
