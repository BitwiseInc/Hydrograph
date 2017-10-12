package hydrograph.ui.propertywindow.widgets.dialogs;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * The GenericExportFileDialog will be used for exporting file (i.e. saving the
 * file) It will display the File Dialog to choose the file location and then
 * save the file Class written as generic way so it will be reused.
 * 
 * <pre>
* 	e.g.
* 	{@code
*	GenericExportFileDialog exportXSDFileDialog = new GenericExportFileDialog(exportButton.getShell());
*	exportXSDFileDialog.setFileName("XMLOut");
*	exportXSDFileDialog.setTitle("Select location for saving XSD file");
*	exportXSDFileDialog.setFilterNames(new String[] { "Schema File (*.xsd)" });
*	exportXSDFileDialog.setFilterExtensions(new String[] { "*.xsd" });
*
*	String filePath = exportXSDFileDialog.open();
*	if (filePath != null) {
*		exportXSDFileDialog.export(filePath, "File Data");
*	}
 * </pre>
 */
public class GenericExportFileDialog {

	/**
	 * Title is name for GenericExportFileDialog window default is "Select
	 * external archive".
	 */
	private String title = "Select external archive";

	/**
	 * FileDialog is represent to (Self) FileDialog window.
	 */
	private FileDialog exportFileDialog;

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
	public GenericExportFileDialog(Shell shell) {
		exportFileDialog = new FileDialog(shell, SWT.SAVE);
		exportFileDialog.setText(title);
		exportFileDialog.setOverwrite(true); // Dialog will prompt to user if
												// fileName used for saving
												// already exists.
		exportFileDialog.setFileName(fileName == null ? defaultFileName : fileName);
		exportFileDialog
				.setFilterExtensions(getFilterExtensions() == null ? defaultFilterExtentions : getFilterExtensions());
		exportFileDialog.setFilterNames(getFilterNames() == null ? defaultFilterNames : getFilterNames());
	}

	/**
	 * Open the file window dialog for selecting the location.
	 * 
	 * @return Fully qualified file path. null if user clicked on cancel button.
	 * 
	 */
	public String open() {

		String fileName = exportFileDialog.open();
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
		return exportFileDialog.getText();
	}

	public void setTitle(String title) {
		exportFileDialog.setText(title);
	}

	public FileDialog getExportFileDialog() {
		return exportFileDialog;
	}

	public String getFileName() {
		return exportFileDialog.getFileName();
	}

	public void setFileName(String fileName) {
		exportFileDialog.setFileName(fileName);
	}

	public String[] getFilterNames() {
		return exportFileDialog.getFilterNames();
	}

	public void setFilterNames(String[] filterNames) {
		exportFileDialog.getFilterNames();
	}

	public String[] getFilterExtensions() {
		return exportFileDialog.getFilterExtensions();
	}

	public void setFilterExtensions(String[] filterExtentions) {
		exportFileDialog.setFilterExtensions(filterExtentions);
	}

	public String getFilterPath() {
		return exportFileDialog.getFilterPath();
	}

	public void setFilterPath(String filterPath) {
		exportFileDialog.setFilterPath(filterPath);
	}

	public File getFile() {
		return file;
	}

}