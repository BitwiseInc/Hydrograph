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
package hydrograph.ui.propertywindow.widgets.customwidgets.excelcomponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import hydrograph.ui.common.datastructures.messages.Messages;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;

/**
 * HeaderAndDataFormattingDialog allows user to provide excel worksheet data and
 * header format
 * 
 * @author Bitwise
 *
 */
public class HeaderAndDataFormattingDialog extends Dialog {
	private Table excelFormatterTable;
	private Shell shell;
	private Map<String, String> formatMap;
	private CCombo borderStyleCombo;
	private CCombo alignmentCombo;
	private TableEditor cellBackgroundColorEditor;
	private TableEditor borderColorEditor;
	private CCombo verticalAlignmentCombo;
	private String[] excelFormats = Messages.EXCEL_FORMATS.split("\\|");
	private TableEditor fontEditor;
	private FontDialog dialog;
	private CCombo borderRangeCombo;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param formatMap
	 */
	public HeaderAndDataFormattingDialog(Shell parentShell, Map<String, String> formatMap) {
		super(parentShell);
		this.shell = parentShell;
		this.formatMap = formatMap;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(Messages.EXCEL_FORMAT_WINDOW_LABEL);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label parameterFileLabel = new Label(composite, SWT.NONE);
		parameterFileLabel.setText(Messages.PARAMETER_FILES);

		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewer tableViewer = createTableColumns(tableComposite);

		tableViewer.setContentProvider(new HeaderAndDataFormattingContentProvider());
		List<Entry> entries = new ArrayList<>(formatMap.entrySet());
		tableViewer.setInput(entries);

		addWidgetsToTableViewer(excelFormatterTable);

		populateWidgetData();

		return container;
	}

	private void populateWidgetData() {
		if (formatMap != null && !formatMap.isEmpty()) {

			if (formatMap.get(excelFormats[0]) != null) {
				borderStyleCombo.setText(formatMap.get(excelFormats[0]));
			}
			if (formatMap.get(excelFormats[1]) != null) {
				borderRangeCombo.setText(formatMap.get(excelFormats[1]));
			}
			if (formatMap.get(excelFormats[2]) != null) {
				alignmentCombo.setText(formatMap.get(excelFormats[2]));
			}
			if (formatMap.get(excelFormats[3]) != null) {
				verticalAlignmentCombo.setText(formatMap.get(excelFormats[3]));
			}
			if (formatMap.get(excelFormats[4]) != null) {
				fontEditor.getItem().setText(1, formatMap.get(excelFormats[4]));
			}
			if (formatMap.get(excelFormats[5]) != null) {
				cellBackgroundColorEditor.getItem().setText(1, formatMap.get(excelFormats[5]));
			}
			if (formatMap.get(excelFormats[6]) != null) {
				borderColorEditor.getItem().setText(1, formatMap.get(excelFormats[6]));
			}
		}
	}

	private TableViewer createTableColumns(Composite tableComposite) {
		TableViewer tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		excelFormatterTable = tableViewer.getTable();
		excelFormatterTable.setHeaderVisible(true);
		excelFormatterTable.setLinesVisible(true);
		excelFormatterTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn parameterNameColumn = tableViewerColumn.getColumn();
		parameterNameColumn.setWidth(254);
		parameterNameColumn.setText(Messages.PARAMETER_NAME);
		tableViewerColumn.setLabelProvider(new ParameterNameLabelProvider());

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn parameterValueColumn = tableViewerColumn_1.getColumn();
		parameterValueColumn.setWidth(251);
		parameterValueColumn.setText(Messages.PARAMETER_VALUE);
		tableViewerColumn_1.setLabelProvider(new ParameterValueLabelProvider());
		return tableViewer;
	}

	private void addWidgetsToTableViewer(Table excelFormatterTable) {
		TableEditor borderStyleEditor = new TableEditor(excelFormatterTable);
		borderStyleCombo = new CCombo(excelFormatterTable, SWT.NONE);
		borderStyleEditor.grabHorizontal = true;
		borderStyleCombo.setItems(Messages.BORDER_STYLE.split("\\|"));
		borderStyleCombo.select(1);
		borderStyleEditor.setEditor(borderStyleCombo, excelFormatterTable.getItem(0), 1);

		TableEditor borderRangeEditor = new TableEditor(excelFormatterTable);
		borderRangeCombo = new CCombo(excelFormatterTable, SWT.NONE);
		borderRangeEditor.grabHorizontal = true;
		borderRangeCombo.setItems(Messages.BORDER_RANGE.split("\\|"));
		borderRangeCombo.select(4);
		borderRangeEditor.setEditor(borderRangeCombo, excelFormatterTable.getItem(1), 1);

		TableEditor horizontalAlignmentEditor = new TableEditor(excelFormatterTable);
		alignmentCombo = new CCombo(excelFormatterTable, SWT.NONE);
		horizontalAlignmentEditor.grabHorizontal = true;
		alignmentCombo.setItems(Messages.HORIZONTAL_ALIGN.split("\\|"));
		alignmentCombo.select(5);
		horizontalAlignmentEditor.setEditor(alignmentCombo, excelFormatterTable.getItem(2), 1);

		TableEditor verticalAlignmentEditor = new TableEditor(excelFormatterTable);
		verticalAlignmentCombo = new CCombo(excelFormatterTable, SWT.NONE);
		verticalAlignmentEditor.grabHorizontal = true;
		verticalAlignmentCombo.setItems(Messages.VERTICAL_ALIGN.split("\\|"));
		verticalAlignmentCombo.select(2);
		verticalAlignmentEditor.setEditor(verticalAlignmentCombo, excelFormatterTable.getItem(3), 1);

		fontEditor = new TableEditor(excelFormatterTable);
		Button fontButton = new Button(excelFormatterTable, SWT.PUSH);
		fontEditor.grabHorizontal = false;
		fontButton.setText(Messages.FONT);
		fontEditor.minimumWidth = 40;
		if(OSValidator.isMac()){
			fontEditor.minimumWidth = 60;
		}
		fontEditor.horizontalAlignment = SWT.RIGHT;
		fontEditor.setEditor(fontButton, excelFormatterTable.getItem(4), 1);
		addSelectionListeneronToFontButton(fontButton, fontEditor);

		cellBackgroundColorEditor = new TableEditor(excelFormatterTable);
		Button cellBackgroundColorButton = new Button(excelFormatterTable, SWT.PUSH);
		cellBackgroundColorEditor.grabHorizontal = false;
		cellBackgroundColorButton.setText(Messages.COLOR);
		cellBackgroundColorEditor.minimumWidth = 40;
		if(OSValidator.isMac()){
			cellBackgroundColorEditor.minimumWidth = 60;
		}
		cellBackgroundColorEditor.horizontalAlignment = SWT.RIGHT;
		cellBackgroundColorEditor.setEditor(cellBackgroundColorButton, excelFormatterTable.getItem(5), 1);
		addSelectionListeneronButton(cellBackgroundColorButton, cellBackgroundColorEditor);

		borderColorEditor = new TableEditor(excelFormatterTable);
		Button borderColorButton = new Button(excelFormatterTable, SWT.PUSH);
		borderColorButton.setText(Messages.COLOR);
		borderColorEditor.grabHorizontal = false;
		borderColorEditor.minimumWidth = 40;
		if(OSValidator.isMac()){
			borderColorEditor.minimumWidth = 60;
		}
		borderColorEditor.horizontalAlignment = SWT.RIGHT;
		borderColorEditor.setEditor(borderColorButton, excelFormatterTable.getItem(6), 1);
		addSelectionListeneronButton(borderColorButton, borderColorEditor);
	}

	private void addSelectionListeneronToFontButton(Button fontButton, TableEditor fontEditor) {
		fontButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String fontdata = "";
				dialog = new FontDialog(Display.getCurrent().getActiveShell(), SWT.NONE);
				dialog.setText("Select Font");
				String string = formatMap.get(excelFormats[4]);
				fontdata = checkDefaultValue(fontEditor, fontdata, string);
				RGB checkDefaultColor = checkDefaultColor(fontdata, string);
				dialog.setRGB(checkDefaultColor);
				FontData defaultFont = new FontData(fontdata);
				dialog.setFontData(defaultFont);
				FontData newFont = dialog.open();
				RGB rgb = dialog.getRGB();
				String convertRGBToHEX = convertRGBToHEX(rgb);
				if (newFont != null) {
					fontEditor.getItem().setText(1, newFont.toString() + "|" + convertRGBToHEX);

				}
			}

		});
	}
	
	private RGB checkDefaultColor(String fontdata, String string) {
		 Color color = new Color(null, new RGB(0, 0, 0));
		if (StringUtils.equals(string, fontEditor.getItem().getText(1))) {
			int index = string.lastIndexOf("|");
			if (index != -1) {
				String colorValue = string.substring(index + 1);
				 java.awt.Color c = java.awt.Color.decode(colorValue);
			   color = new Color(Display.getCurrent(), c.getRed(), c.getGreen(), c.getBlue());
			  return color.getRGB();
			}
		} else {
			String fontEditorValue = fontEditor.getItem().getText(1);
			int index = fontEditorValue.lastIndexOf("|");
			if (index != -1) {
				String colorValue = fontEditorValue.substring(index + 1);
				 java.awt.Color c = java.awt.Color.decode(colorValue);
				  color = new Color(Display.getCurrent(), c.getRed(), c.getGreen(), c.getBlue());
				  return color.getRGB();
			}
		}
		 return color.getRGB();
	}
	
	private String checkDefaultValue(TableEditor fontEditor, String fontdata, String string) {
		if (StringUtils.equals(string, fontEditor.getItem().getText(1))) {
			int index = string.lastIndexOf("|");
			if (index != -1) {
				String color = string.substring(index + 1);
				fontdata = string.substring(0, index);
			} else {
				fontdata = formatMap.get(excelFormats[4]);
			}
		} else {
			String fontEditorValue = fontEditor.getItem().getText(1);
			int index = fontEditorValue.lastIndexOf("|");
			if (index != -1) {
				String color = fontEditorValue.substring(index + 1);
				fontdata = fontEditorValue.substring(0, index);
			}
		}
		return fontdata;
	}

	private void addSelectionListeneronButton(Button button, TableEditor editor) {
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog dlg = new ColorDialog(Display.getCurrent().getActiveShell());
				dlg.setRGB(new RGB(0, 0, 0));
				RGB rgb = dlg.open();
				if (rgb != null) {
					Color color = new Color(shell.getDisplay(), rgb);
					String colorValue = convertRGBToHEX(rgb);
					editor.getItem().setText(1, colorValue);
					color.dispose();
				}
			}
		});
	}
	
	private String convertRGBToHEX(RGB rgb) {
		String red = Integer.toHexString(rgb.red);
		if (red.equals("0"))
			red = "00";
		String green = Integer.toHexString(rgb.green);
		if (green.equals("0"))
			green = "00";
		String blue = Integer.toHexString(rgb.blue);
		if (blue.equals("0"))
			blue = "00";
		return "#" + red + green + blue;
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
		return new Point(550, 399);
	}

	@Override
	protected void okPressed() {
		formatMap.clear();
		String[] excelFormats = Messages.EXCEL_FORMATS.split("\\|");
		if (StringUtils.isNotBlank(borderStyleCombo.getText())) {
			formatMap.put(excelFormats[0], borderStyleCombo.getText());
		}
		if (StringUtils.isNotBlank(borderRangeCombo.getText())) {
			formatMap.put(excelFormats[1], borderRangeCombo.getText());
		}
		if (StringUtils.isNotBlank(alignmentCombo.getText())) {
			formatMap.put(excelFormats[2], alignmentCombo.getText());
		}
		if (StringUtils.isNotBlank(verticalAlignmentCombo.getText())) {
			formatMap.put(excelFormats[3], verticalAlignmentCombo.getText());
		}
		if (StringUtils.isNotBlank(fontEditor.getItem().getText())) {
			formatMap.put(excelFormats[4], fontEditor.getItem().getText(1));
		}
		if (StringUtils.isNotBlank(cellBackgroundColorEditor.getItem().getText())) {
			formatMap.put(excelFormats[5], cellBackgroundColorEditor.getItem().getText(1));
		}
		if (StringUtils.isNotBlank(borderColorEditor.getItem().getText())) {
			formatMap.put(excelFormats[6], borderColorEditor.getItem().getText(1));
		}
		super.okPressed();
	}

	public Map<String, String> getExcelFormatData() {
		return formatMap;

	}
}
