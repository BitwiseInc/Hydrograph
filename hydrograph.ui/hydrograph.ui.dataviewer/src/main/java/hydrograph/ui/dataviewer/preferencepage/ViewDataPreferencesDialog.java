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

package hydrograph.ui.dataviewer.preferencepage;



import hydrograph.ui.common.util.ConvertHexValues;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.dataviewer.Activator;
import hydrograph.ui.dataviewer.constants.Messages;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

/**
 * This class is responsible for making dialog of view data preferences
 * 
 * @author Bitwise
 *
 */
public class ViewDataPreferencesDialog extends Dialog {
	private Label warningImageLabel;
	private Label warningLabel;
	private Text delimiterTextBox;
	private Text quoteCharactorTextBox;
	private Button includeHeardersCheckBox;
	private Text fileSizeTextBox;
	private Text pageSizeTextBox;
	private ViewDataPreferencesVO viewDataPreferencesVO;
	private ControlDecoration pageSizeIntegerDecorator;
	private ControlDecoration fileSizeIntegerDecorator;
	private ControlDecoration fileSizeEmptyDecorator;
	private ControlDecoration pageSizeEmptyDecorator;
	private ControlDecoration delimiterDuplicateDecorator;
	private ControlDecoration delimiterSingleCharactorDecorator;
	private ControlDecoration quoteSingleCharactorDecorator;
	private ControlDecoration quoteCharactorDuplicateDecorator;
	private ControlDecoration delimiterEmptyDecorator;
	private ControlDecoration quoteCharactorEmptyDecorator;
	private ControlDecoration fileSizeZeroDecorator;
	private ControlDecoration pageSizeZeroDecorator;
	private String REGEX = "[\\d]*";
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ViewDataPreferencesDialog(Shell parentShell) {
		super(parentShell);

	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("View Data Preferences");
		container.setLayout(new GridLayout(2, false));
		Composite composite = new Composite(container, SWT.BORDER);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
		gd_composite.widthHint = 619;
		composite.setLayoutData(gd_composite);
		Label delimiterLabel = new Label(composite, SWT.NONE);
		delimiterLabel.setBounds(22, 22, 55, 15);
		delimiterLabel.setText("Delimiter");
		setCursorTooltip(delimiterLabel,Messages.DELIMITER_HELP_TEXT);
		
		Label quoteCharactorLabel = new Label(composite, SWT.NONE);
		quoteCharactorLabel.setBounds(22, 61, 94, 15);
		quoteCharactorLabel.setText("Quote Character");
		setCursorTooltip(quoteCharactorLabel,Messages.QUOTE_CHARACTOR_HELP_TEXT);
		
		delimiterTextBox = new Text(composite, SWT.BORDER);
		delimiterTextBox.setBounds(132, 19, 86, 21);
		
		quoteCharactorTextBox = new Text(composite, SWT.BORDER);
		quoteCharactorTextBox.setBounds(132, 58, 86, 21);
		
		includeHeardersCheckBox = new Button(composite, SWT.CHECK);
		includeHeardersCheckBox.setBounds(578, 21, 109, 16);
		includeHeardersCheckBox.setText("Include Headers");
		setCursorTooltip(includeHeardersCheckBox,Messages.INCLUDE_HEADER_HELP_TEXT);
		
		Label fileSizeLabel = new Label(composite, SWT.NONE);
		fileSizeLabel.setBounds(301, 22, 76, 15);
		fileSizeLabel.setText("File Size (MB)");
		setCursorTooltip(fileSizeLabel,Messages.FILE_SIZE_HELP_TEXT);
		
		Label pageSizeLabel = new Label(composite, SWT.NONE);
		pageSizeLabel.setBounds(301, 61, 55, 15);
		pageSizeLabel.setText("Page Size");
		setCursorTooltip(pageSizeLabel,Messages.PAGE_SIZE_HELP_TEXT);
		fileSizeTextBox = new Text(composite, SWT.BORDER);
		fileSizeTextBox.setBounds(414, 19, 76, 21);

		pageSizeTextBox = new Text(composite, SWT.BORDER);
		pageSizeTextBox.setBounds(414, 58, 76, 21);
		delimiterTextBox.setText(viewDataPreferencesVO.getDelimiter());
		quoteCharactorTextBox.setText(viewDataPreferencesVO.getQuoteCharactor());
		includeHeardersCheckBox.setSelection(viewDataPreferencesVO.getIncludeHeaders());
		fileSizeTextBox.setText(Integer.toString(viewDataPreferencesVO.getFileSize()));
		pageSizeTextBox.setText(Integer.toString(viewDataPreferencesVO.getPageSize()));
		fileSizeIntegerDecorator = addDecorator(fileSizeTextBox, Messages.NUMERIC_VALUE_ACCPECTED);
		pageSizeIntegerDecorator = addDecorator(pageSizeTextBox, Messages.NUMERIC_VALUE_ACCPECTED);
		fileSizeEmptyDecorator = addDecorator(fileSizeTextBox, Messages.FILE_SIZE_BLANK);
		pageSizeEmptyDecorator = addDecorator(pageSizeTextBox, Messages.PAGE_SIZE_BLANK);
		delimiterDuplicateDecorator = addDecorator(delimiterTextBox, Messages.DUPLICATE_ERROR_MESSAGE);
		delimiterSingleCharactorDecorator = addDecorator(delimiterTextBox, Messages.SINGLE_CHARACTOR_ERROR_MESSAGE);
		quoteSingleCharactorDecorator = addDecorator(quoteCharactorTextBox, Messages.SINGLE_CHARACTOR_ERROR_MESSAGE);
		quoteCharactorDuplicateDecorator = addDecorator(quoteCharactorTextBox, Messages.DUPLICATE_ERROR_MESSAGE);
		delimiterEmptyDecorator = addDecorator(delimiterTextBox, Messages.DELIMITER_BLANK);
		quoteCharactorEmptyDecorator = addDecorator(quoteCharactorTextBox, Messages.QUOTE_CHARACTOR_BLANK);
		fileSizeZeroDecorator=addDecorator(fileSizeTextBox,Messages.NUMERIC_VALUE_ACCPECTED);
		pageSizeZeroDecorator=addDecorator(pageSizeTextBox,Messages.NUMERIC_VALUE_ACCPECTED);
		pageSizeIntegerDecorator.hide();
		fileSizeIntegerDecorator.hide();
		fileSizeEmptyDecorator.hide();
		pageSizeEmptyDecorator.hide();
		delimiterDuplicateDecorator.hide();
		delimiterSingleCharactorDecorator.hide();
		quoteSingleCharactorDecorator.hide();
		quoteCharactorDuplicateDecorator.hide();
		delimiterEmptyDecorator.hide();
		quoteCharactorEmptyDecorator.hide();
		fileSizeZeroDecorator.hide();
		pageSizeZeroDecorator.hide();

		delimiterTextBox.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				validateForSingleAndDuplicateCharacter(e, quoteCharactorTextBox.getText(),
						delimiterSingleCharactorDecorator, delimiterDuplicateDecorator,delimiterEmptyDecorator);

			}
		});

		quoteCharactorTextBox.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {
				validateForSingleAndDuplicateCharacter(e, delimiterTextBox.getText(), quoteSingleCharactorDecorator,
						quoteCharactorDuplicateDecorator,quoteCharactorEmptyDecorator);

			}

		});
		delimiterTextBox.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				validateDelimiterAndQuoteCharactorProperty(delimiterTextBox.getText(), quoteCharactorTextBox.getText(),
						delimiterSingleCharactorDecorator, delimiterDuplicateDecorator);
			}

			@Override
			public void focusGained(FocusEvent e) {

				enableAndDisableOkButtonIfAnyDecoratorIsVisible();

			}
		});

		quoteCharactorTextBox.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				validateDelimiterAndQuoteCharactorProperty(delimiterTextBox.getText(), quoteCharactorTextBox.getText(),
						delimiterSingleCharactorDecorator, delimiterDuplicateDecorator);
			}

			@Override
			public void focusGained(FocusEvent e) {

				enableAndDisableOkButtonIfAnyDecoratorIsVisible();

			}
		});
		attachListnersToSizeTextBox();
		return container;
	}

	private void setCursorTooltip(Control label, String helpText) {
		label.setToolTipText(helpText);
		label.setCursor(new Cursor(label.getDisplay(), SWT.CURSOR_HELP));
	}
	
	
	private void validateForSingleAndDuplicateCharacter(VerifyEvent e, String textBoxValue,
			ControlDecoration singleCharactorDecorator, ControlDecoration duplicateDecorator, ControlDecoration emptyDecorator) {
		singleCharactorDecorator.hide();
		duplicateDecorator.hide();
		emptyDecorator.hide();
		String value = ((Text) e.widget).getText();
		String currentValue = (value.substring(0, e.start) + e.text + value.substring(e.end));
		if (StringUtils.isNotEmpty(currentValue)) {
			validateDelimiterAndQuoteCharactorProperty(currentValue, textBoxValue, singleCharactorDecorator,
					duplicateDecorator);
		} else {
			getButton(0).setEnabled(false);
			emptyDecorator.show();
			warningLabel.setText(Messages.WARNING_MESSAGE);
			warningLabel.setVisible(true);
			warningImageLabel.setVisible(true);
		}
	}
	
	



	private void attachListnersToSizeTextBox() {
		fileSizeTextBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				isTextBoxEmpty(e, fileSizeEmptyDecorator,fileSizeZeroDecorator);
			}
		});

		fileSizeTextBox.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent event) {
				validateTextBoxValue(event, fileSizeIntegerDecorator);
			}
		});

		fileSizeTextBox.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				fileSizeIntegerDecorator.hide();
				disableOkButtonWhenTextBoxIsEmpty(fileSizeTextBox);
			}

			@Override
			public void focusGained(FocusEvent e) {
				enableAndDisableOkButtonIfAnyDecoratorIsVisible();
			}
		});
		

		pageSizeTextBox.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				pageSizeIntegerDecorator.hide();
				disableOkButtonWhenTextBoxIsEmpty(pageSizeTextBox);
			}

			@Override
			public void focusGained(FocusEvent e) {
				enableAndDisableOkButtonIfAnyDecoratorIsVisible();
			}
		});
		
		pageSizeTextBox.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent event) {
				validateTextBoxValue(event, pageSizeIntegerDecorator);
			}
		});
		pageSizeTextBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String pageSizeValue = ((Text) e.widget).getText();
				if (!pageSizeValue.isEmpty()) {
					if (Integer.parseInt(pageSizeValue) > 5000) {
						warningLabel.setText(Messages.MEMORY_OVERFLOW_EXCEPTION);
						warningImageLabel.setVisible(true);
						warningLabel.setVisible(true);
					} else {
						warningImageLabel.setVisible(false);
						warningLabel.setVisible(false);
					}
				}
				isTextBoxEmpty(e, pageSizeEmptyDecorator,pageSizeZeroDecorator);
			}
		});
	}
	
	private void disableOkButtonWhenTextBoxIsEmpty(Text textBox) {
		if (textBox.getText().isEmpty()) {
			getButton(0).setEnabled(false);
		} else {
			getButton(0).setEnabled(true);
		}
	}

	
	
	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		warningImageLabel = new Label(composite, SWT.NONE);
		warningImageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		
		warningImageLabel.setImage(ImagePathConstant.WARNING_ICON.getImageFromRegistry());

		warningLabel = new Label(composite, SWT.NONE);
		warningLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
		warningLabel.setText(Messages.WARNING_MESSAGE);

		warningLabel.setVisible(false);
		warningImageLabel.setVisible(false);
		if (!delimiterTextBox.getText().equalsIgnoreCase(",")
				|| !quoteCharactorTextBox.getText().equalsIgnoreCase("\"")) {
			warningImageLabel.setVisible(true);
			warningLabel.setVisible(true);
		}
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(731, 180);
	}

	@Override
	protected void okPressed() {
		viewDataPreferencesVO.setDelimiter(delimiterTextBox.getText());
		viewDataPreferencesVO.setQuoteCharactor(quoteCharactorTextBox.getText());
		viewDataPreferencesVO.setIncludeHeaders(includeHeardersCheckBox.getSelection());
		viewDataPreferencesVO.setFileSize(Integer.parseInt(fileSizeTextBox.getText()));
		viewDataPreferencesVO.setPageSize(Integer.parseInt(pageSizeTextBox.getText()));
		super.okPressed();
	}




	private boolean validateDelimiterAndQuoteCharactorProperty(String textBoxValue, String textBoxValue2,
			ControlDecoration singleCharactorDecorator, ControlDecoration duplicateDecorator) {
		if (StringUtils.length(ConvertHexValues.parseHex(textBoxValue)) == 1) {
			enableAndDisableOkButtonIfAnyDecoratorIsVisible();
			if (!(textBoxValue.equalsIgnoreCase(",") || textBoxValue.equalsIgnoreCase("\""))
					&& !textBoxValue.equalsIgnoreCase(textBoxValue2)) {
				warningLabel.setText(Messages.WARNING_MESSAGE);
				warningLabel.setVisible(true);
				warningImageLabel.setVisible(true);
				hideDelimiterAndQuoteCharactorDecorator();
				if (StringUtils.length(ConvertHexValues.parseHex(textBoxValue2)) > 1) {
					getButton(0).setEnabled(false);
				}
				else
				{
					getButton(0).setEnabled(true);
					enableAndDisableOkButtonIfAnyDecoratorIsVisible();
				}
				return false;
			} else {
				if (textBoxValue.equalsIgnoreCase(textBoxValue2)) {
					duplicateDecorator.show();
					getButton(0).setEnabled(false);
					return false;
				} else {
					showWarningMessage(textBoxValue, textBoxValue2);
					duplicateDecorator.hide();
					enableAndDisableOkButtonIfAnyDecoratorIsVisible();
					return true;
				}
			}
		} else {
			if (!textBoxValue.isEmpty()) {
				singleCharactorDecorator.show();
				getButton(0).setEnabled(false);
			}
			return false;
		}
	}

	private void showWarningMessage(String textBoxValue, String textBoxValue2) {
		if ((textBoxValue.equalsIgnoreCase(",") || textBoxValue.equalsIgnoreCase("\""))
				&& (textBoxValue2.equalsIgnoreCase("\"") || textBoxValue2.equalsIgnoreCase(","))) {
			warningLabel.setVisible(false);
			warningImageLabel.setVisible(false);
		} else {
			warningLabel.setVisible(true);
			warningImageLabel.setVisible(true);
		}
	}

	private void hideDelimiterAndQuoteCharactorDecorator() {
		if (delimiterDuplicateDecorator.isVisible()) {
			delimiterDuplicateDecorator.hide();
		}
		if (quoteCharactorDuplicateDecorator.isVisible()) {
			quoteCharactorDuplicateDecorator.hide();
		}
	}



	private void enableAndDisableOkButtonIfAnyDecoratorIsVisible() {
		if (quoteCharactorDuplicateDecorator.isVisible() || quoteSingleCharactorDecorator.isVisible()
				|| fileSizeEmptyDecorator.isVisible() || pageSizeEmptyDecorator.isVisible()
				|| delimiterDuplicateDecorator.isVisible() || delimiterSingleCharactorDecorator.isVisible()
				|| delimiterEmptyDecorator.isVisible()||quoteCharactorEmptyDecorator.isVisible()
				|| fileSizeZeroDecorator.isVisible() || pageSizeZeroDecorator.isVisible()) {
			getButton(0).setEnabled(false);
		} else {
			getButton(0).setEnabled(true);
		}
	}
	
	public void setViewDataPreferences(ViewDataPreferencesVO viewDataPreferencesVO) {
		this.viewDataPreferencesVO = viewDataPreferencesVO;
	}

	public ViewDataPreferencesVO getViewDataPreferences() {
		return viewDataPreferencesVO;
	}

	private ControlDecoration addDecorator(Control control, String message) {
		ControlDecoration txtDecorator = new ControlDecoration(control, SWT.LEFT);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_ERROR);
		Image img = fieldDecoration.getImage();
		txtDecorator.setImage(img);
		txtDecorator.setDescriptionText(message);
		txtDecorator.setMarginWidth(3);
		return txtDecorator;
	}
	
	private void validateTextBoxValue(VerifyEvent event, ControlDecoration integerDecorator) {
		integerDecorator.hide();
		String string = event.text;
		Matcher matchs = Pattern.compile(REGEX).matcher(string);
		if (!matchs.matches()) {
			integerDecorator.show();
			event.doit = false;
		} else {
			integerDecorator.hide();
		}
	}

	private void isTextBoxEmpty(ModifyEvent e, ControlDecoration emptyDecorator,ControlDecoration zeroDecorator) {
		emptyDecorator.hide();
		zeroDecorator.hide();
		String fileSize = ((Text) e.widget).getText();
		if (!fileSize.isEmpty()) {
			emptyDecorator.hide();
			if (Integer.parseInt(fileSize) == 0) {
				zeroDecorator.show();
				getButton(0).setEnabled(false);
			} else {
				zeroDecorator.hide();
				getButton(0).setEnabled(true);
			}
			enableAndDisableOkButtonIfAnyDecoratorIsVisible();
		} else {
			getButton(0).setEnabled(false);
			emptyDecorator.show();
		}
	}

}
