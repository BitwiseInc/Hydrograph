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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.joinproperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.JoinConfigProperty;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialogs.FieldDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTSWTWidgets;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;


public class ELTJoinConfigGrid extends Dialog {

	private int inputPortValue;
	private List<String> ITEMS = Arrays.asList(Constants.TRUE, Constants.FALSE);
	private List<JoinConfigProperty> tempraryConfigPropertyList;
	private List<JoinConfigProperty> joinConfigPropertyList;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private ELTSWTWidgets eltswtWidgets = new ELTSWTWidgets();
	private Label editLableAsButton;
	private Map<String, List<String>> propagatedFiledNames;
	private Component component;
    private List<List<FilterProperties>> sourceFieldList;
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param propertyDialogButtonBar
	 * @param validationStatus
	 */
	public ELTJoinConfigGrid(Shell parentShell, PropertyDialogButtonBar propertyDialogButtonBar,
			List<JoinConfigProperty> configProperty,Component component) {
		super(parentShell);

		this.joinConfigPropertyList = configProperty;
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		copyAll(configProperty);
		this.component=component;
		
	}

	private List<JoinConfigProperty> copyAll(List<JoinConfigProperty> configProperty) {
		tempraryConfigPropertyList = new ArrayList<>();
		for (JoinConfigProperty joinConfigProperty : configProperty) {
			tempraryConfigPropertyList.add(new JoinConfigProperty(joinConfigProperty.getPortIndex(), joinConfigProperty
					.getJoinKey(), joinConfigProperty.getRecordRequired()));
		}
		return tempraryConfigPropertyList;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Join Configuration");
		container.setLayout(new GridLayout(1, false));

		Composite composite_2 = new Composite(container, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_2.heightHint = 16;
		gd_composite_2.widthHint = 400;
		composite_2.setLayoutData(gd_composite_2);


		Composite composite = new Composite(container, SWT.BORDER);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.heightHint = 212;
		gd_composite.widthHint = 546;
		composite.setLayoutData(gd_composite);

		ScrolledComposite scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(0, 0, 546, 212);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite_1 = new Composite(scrolledComposite, SWT.NONE);

		eltswtWidgets.textBoxWidget(composite_1, SWT.BORDER, new int[] { 0, 2, 142, 23 }, "PortIndex", false);
		eltswtWidgets.textBoxWidget(composite_1, SWT.BORDER, new int[] { 144, 2, 190, 23 }, "Join Key(s)", false);
		eltswtWidgets.textBoxWidget(composite_1, SWT.BORDER, new int[] { 337, 2, 205, 23 }, "Record Required", false);

		scrolledComposite.setContent(composite_1);
		
		String count=(String)component.getProperties().get(Constants.INPUT_PORT_COUNT_PROPERTY);
		inputPortValue=Integer.valueOf(count);
		
		if (tempraryConfigPropertyList != null && tempraryConfigPropertyList.isEmpty()) {
			for (int i = 0; i < inputPortValue; i++) {
				tempraryConfigPropertyList.add(new JoinConfigProperty());
			}
		}

		if (inputPortValue > tempraryConfigPropertyList.size()) {
			for (int i = tempraryConfigPropertyList.size(); i <= inputPortValue; i++) {
				tempraryConfigPropertyList.add(new JoinConfigProperty());
			}
		}

		for (int i = 0, j = 0; i < inputPortValue; i++, j++) {
			final JoinConfigProperty joinConfigProperty = tempraryConfigPropertyList.get(i);

			Text portIndex = eltswtWidgets.textBoxWidget(composite_1, SWT.BORDER, new int[] { 0, 28 + j, 142, 23 },
					"in" + i, false);
			joinConfigProperty.setPortIndex("in" + i);

			final Text keyText = eltswtWidgets.textBoxWidget(composite_1, SWT.BORDER | SWT.READ_ONLY, new int[] { 144,
					28 + j, 170, 23 }, "", false);
			keyText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));


			Combo joinTypeCombo = eltswtWidgets.comboWidget(composite_1, SWT.BORDER,
					new int[] { 337, 28 + j, 205, 23 }, (String[]) ITEMS.toArray(), 0);
			joinTypeCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String comboText = ((Combo) e.widget).getText();
					joinConfigProperty.setRecordRequired(ITEMS.indexOf(comboText));
					propertyDialogButtonBar.enableApplyButton(true);
				}
			});
			j = j + 26;
			if (tempraryConfigPropertyList != null && !tempraryConfigPropertyList.isEmpty()) {
				populate(i, portIndex, keyText, joinTypeCombo);
			}

			editLableAsButton = new Label(composite_1, SWT.None);
			editLableAsButton.setBounds(317, 5 + j, 20, 20);
			editLableAsButton.setImage(ImagePathConstant.EDIT_BUTTON.getImageFromRegistry());
			editLableAsButton.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseUp(MouseEvent e) {
					
					keyText.setText(launchDialogToSelectFields(keyText.getText(), joinConfigProperty.getPortIndex()));
					keyText.setToolTipText(keyText.getText());
					joinConfigProperty.setJoinKey(keyText.getText());
				}

			});
			keyText.setToolTipText(keyText.getText());
		}

		scrolledComposite.setMinSize(composite_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return container;
	}

	public void populate(int i, Text portIndex, Text keyText, Combo joinTypeCombo) {
		portIndex.setText(tempraryConfigPropertyList.get(i).getPortIndex());
		keyText.setText(tempraryConfigPropertyList.get(i).getJoinKey());
		joinTypeCombo.select(tempraryConfigPropertyList.get(i).getRecordRequired());
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(565, 320);
	}
	
	private String launchDialogToSelectFields(String availableValues, String socketId) {
		String teminalNumber=socketId.substring("in".length()); //to get a port number removing 'in' from socketId
		FieldDialog fieldDialog = new FieldDialog(new Shell(), propertyDialogButtonBar);
		fieldDialog.setPropertyFromCommaSepratedString(availableValues);
		if(!sourceFieldList.isEmpty()&& Integer.parseInt(teminalNumber) < sourceFieldList.size())
		{	
		fieldDialog.setSourceFieldsFromPropagatedSchema(SchemaSyncUtility.INSTANCE.
				converterFilterPropertyListToStringList(sourceFieldList.get(Integer.parseInt(teminalNumber))));
		}
		fieldDialog.setComponentName(Constants.JOIN_KEYS_WINDOW_TITLE);
		fieldDialog.open();
		return fieldDialog.getResultAsCommaSeprated();
	}

	public void setPropagatedFieldProperty(Map<String, List<String>> propagatedFiledNames) {
		this.propagatedFiledNames = propagatedFiledNames;
	}

	public void setSourceFieldList(List<List<FilterProperties>> sourceFieldList) {
		this.sourceFieldList = sourceFieldList;
	}

	@Override
	protected void okPressed() {
		joinConfigPropertyList.clear();
		
		List<JoinConfigProperty> tempraryConfigPropList=new ArrayList<JoinConfigProperty>();
		for(int i=0;i<inputPortValue;i++)
		{
			tempraryConfigPropList.add(tempraryConfigPropertyList.get(i));
		}
		
		joinConfigPropertyList.addAll(tempraryConfigPropList);
		super.okPressed();
	}

}
