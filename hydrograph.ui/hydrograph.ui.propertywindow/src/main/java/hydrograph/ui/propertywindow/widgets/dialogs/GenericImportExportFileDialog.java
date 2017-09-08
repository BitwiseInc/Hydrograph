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

package hydrograph.ui.propertywindow.widgets.dialogs;

import java.io.File;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * The GenericImportExportFileDialog will be used for exporting and importing file. 
 * 
 * <pre>
 * 	e.g. Export File Dialog
 * 	{@code
 *	GenericImportExportFileDialog exportFileDialog = new GenericImportExportFileDialog(getShell(), SWT.SAVE);
 *	exportFileDialog.setFileName("XMLOut");
 *	exportFileDialog.setTitle("Select location for saving XSD file");
 *	exportFileDialog.setFilterNames(new String[] { "Schema File (*.xsd)" });
 *	exportFileDialog.setFilterExtensions(new String[] { "*.xsd" });
 * 
 *	String filePath = exportFileDialog.open();
 *	if (filePath != null) {
 *		exportFileDialog.export(filePath, "File Data");
 *	}
 *
 * 	e.g. Import File Dialog
 * 	{@code
 *	GenericImportExportFileDialog importFileDialog = new GenericImportExportFileDialog(getShell(), SWT.OPEN);
 *	importFileDialog.setFileName("XMLOut");
 * 	importFileDialog.setTitle("Select location from where you want to import file");
 *	importFileDialog.setFilterNames(new String[] { "Schema File (*.xsd)" });
 *	importFileDialog.setFilterExtensions(new String[] { "*.xsd" });
 *
 *	String filePath = importFileDialog.open();
 *	if (filePath != null) {
 *		// import file logic
 *	}
 * </pre>
 */
public class GenericImportExportFileDialog {

	/**
	 * Title is name for GenericImportExportFileDialog window default is "Select
	 * external archive".
	 */
	private String title = "Select external archive";

	/**
	 * FileDialog is represent to (Self) FileDialog window.
	 */
	private FileDialog genericImportExportFileDialog;

	/**
	 * FileName is used while saving the file default is "newFile".
	 */
	private String fileName;

	/**
	 * DefaultFileName is used if fileName not specified.
	 */
	private String defaultFileName = "newFile";

	/**
	 * Please see doc of $filterNames instance member this is used for default
	 * purpose.
	 */
	private final String[] defaultFilterNames = new String[] { "All Files" };

	/**
	 * Please see doc of $filterExtentions instance member this is used for
	 * default purpose.
	 */
	private final String[] defaultFilterExtentions = new String[] { "*.*" };

	/**
	 * File represent the file java.io.File instance used while saving the file.
	 */
	private File file;

	/**
	 * GenericExportFileDialog Constructor.
	 * 
	 * @param Shell
	 */
	public GenericImportExportFileDialog(Shell shell, int style) {
		genericImportExportFileDialog = new FileDialog(shell, style);
		genericImportExportFileDialog.setText(title);
		genericImportExportFileDialog.setOverwrite(true); // Dialog will prompt to user if
												// fileName used for saving
												// already exists.
		genericImportExportFileDialog.setFileName(fileName == null ? defaultFileName : fileName);
		genericImportExportFileDialog
				.setFilterExtensions(getFilterExtensions() == null ? defaultFilterExtentions : getFilterExtensions());
		genericImportExportFileDialog.setFilterNames(getFilterNames() == null ? defaultFilterNames : getFilterNames());
	}

	/**
	 * Open the file window dialog for selecting the location.
	 * 
	 * @return Fully qualified file path. null if user clicked on cancel button.
	 * 
	 */
	public String open() {

		String fileName = genericImportExportFileDialog.open();
		if (fileName == null) {
			// User has clicked on cancelled button, so return.
			return null;
		} else {
			// User has selected a file; see if it already exists
			file = new File(fileName);
		}
		return fileName;
	}

	public String getTitle() {
		return genericImportExportFileDialog.getText();
	}

	public void setTitle(String title) {
		genericImportExportFileDialog.setText(title);
	}

	public FileDialog getGenericExportImportFileDialog() {
		return genericImportExportFileDialog;
	}

	public String getFileName() {
		return genericImportExportFileDialog.getFileName();
	}

	public void setFileName(String fileName) {
		genericImportExportFileDialog.setFileName(fileName);
	}

	public String[] getFilterNames() {
		return genericImportExportFileDialog.getFilterNames();
	}

	public void setFilterNames(String[] filterNames) {
		genericImportExportFileDialog.setFilterNames(filterNames);
	}

	public String[] getFilterExtensions() {
		return genericImportExportFileDialog.getFilterExtensions();
	}

	public void setFilterExtensions(String[] filterExtentions) {
		genericImportExportFileDialog.setFilterExtensions(filterExtentions);
	}

	public String getFilterPath() {
		return genericImportExportFileDialog.getFilterPath();
	}

	public void setFilterPath(String filterPath) {
		genericImportExportFileDialog.setFilterPath(filterPath);
	}

	public File getFile() {
		return file;
	}

}
