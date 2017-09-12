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

import java.util.Map;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * DataFormatCustomCellEditior class creates custom dialog for Excel component
 * @author Bitwise
 *
 */
public class DataFormatCustomCellEditior extends DialogCellEditor{
	
	
	public DataFormatCustomCellEditior(TableViewer viewer) {
		super(viewer.getTable());
	}
	@Override
	protected Control createControl(Composite parent) {
		return super.createControl(parent);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		Map<String,String> dataMap = (Map<String,String>) getValue();
		HeaderAndDataFormattingDialog headerAndDataFormattingDialog = new HeaderAndDataFormattingDialog(Display.getCurrent().getActiveShell(),dataMap);
		headerAndDataFormattingDialog.open();
		return headerAndDataFormattingDialog.getExcelFormatData();
	}
	
	@Override
	protected Button createButton(Composite parent) {
		return super.createButton(parent);
	}
	
	@Override
	protected Control createContents(Composite cell) {
		return super.createContents(cell);
	}

}
