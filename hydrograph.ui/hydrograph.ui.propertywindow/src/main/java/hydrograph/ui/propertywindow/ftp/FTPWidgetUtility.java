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
package hydrograph.ui.propertywindow.ftp;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ELTModifyListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.ModifyAlphaNumbericTextListener;

/**
 * The Class FTPWidgetUtility
 * @author bitwise
 *
 */
public class FTPWidgetUtility {
	private FTPWidgetUtility instance;

	public FTPWidgetUtility() {
		instance=this;
	}
	
	/**
	 * @return instance of SequencelRun
	 */
	public FTPWidgetUtility getInstance() {
		return instance;
	}
	
	/**
	 *  Create Label Widget
	 * @param control
	 * @param widgetName
	 * @return
	 */
	public Widget createLabel(Composite control, String widgetName){
		Label label = new Label(control, SWT.NONE);
		GridData label_gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		label.setLayoutData(label_gridData);
		label.setText(widgetName);
		return label;
	}
	
	/**
	 * Create Text Widget
	 * @param control
	 * @param widgetName
	 * @return
	 */
	public Widget createText(Composite control, String widgetName, int style){
		Text text = new Text(control, style);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.horizontalIndent = 10;
		text.setLayoutData(gd_text);
		text.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
		return text;
	}
	
	/**
	 * Create Combo Widget
	 * @param control
	 * @param widgetName
	 * @return
	 */
	public Widget CreateCombo(Composite control, String[] widgetName){
		Combo combo = new Combo(control, SWT.READ_ONLY);
		combo.setItems(widgetName);
		combo.select(0);
		GridData gd_partitionKeyButton = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_partitionKeyButton.horizontalIndent = 10;
		combo.setLayoutData(gd_partitionKeyButton);
		
		return combo;
	}
	
	/**
	 * Validate text
	 * @param text
	 */
	public void validateWidgetText(Text text, PropertyDialogButtonBar propertyDialogButtonBar, Cursor cursor,
			ControlDecoration controlDecoration) {
		controlDecoration.setMarginWidth(2);
		ModifyAlphaNumbericTextListener alphaNumbericTextListener = new ModifyAlphaNumbericTextListener();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, controlDecoration);
		text.addListener(SWT.Modify, alphaNumbericTextListener.getListener(propertyDialogButtonBar, helper, text));
	}
	
	/**
	 * @param text
	 * @param propertyDialogButtonBar
	 * @param cursor
	 */
	public void validateEmptyWidgetText(Text text, PropertyDialogButtonBar propertyDialogButtonBar, Cursor cursor, 
			ControlDecoration controlDecoration){
		controlDecoration.setMarginWidth(2);
		ELTModifyListener eltModifyListener = new ELTModifyListener();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, controlDecoration);
		text.addListener(SWT.Modify, eltModifyListener.getListener(propertyDialogButtonBar, helper, text));
	}
	
	/**
	 * 
	 */
	public void removeModifyListener(Text text, PropertyDialogButtonBar propertyDialogButtonBar, Cursor cursor, 
			ControlDecoration controlDecoration){
		controlDecoration.hide();
		ELTModifyListener eltModifyListener = new ELTModifyListener();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, controlDecoration);
		text.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
		text.removeListener(SWT.Modify, eltModifyListener.getListener(propertyDialogButtonBar, helper, text));
	}
	
}
