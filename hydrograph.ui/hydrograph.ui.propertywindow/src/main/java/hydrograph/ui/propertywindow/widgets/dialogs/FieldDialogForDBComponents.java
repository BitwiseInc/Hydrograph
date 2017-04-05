package hydrograph.ui.propertywindow.widgets.dialogs;


import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;

public class FieldDialogForDBComponents extends FieldDialog{
	
	private int i = 0; 
	public FieldDialogForDBComponents(Shell parentShell, PropertyDialogButtonBar propertyDialogButtonBar) {
		super(parentShell, propertyDialogButtonBar);
	}
	
	@Override
	public void dropAction(DropTargetEvent event) {
		if(i==0){
			super.dropAction(event);
			i++;
		}
		if(i>1){
			return;
		}
	}
	
	
	@Override
	public void addFieldOnDoubleClick() {
		if(i==0){
			super.addFieldOnDoubleClick();
			i++;
		}
		if(i>1){
			return;
		}
	}
	
	@Override
	public void deleteRow() {
		super.deleteRow();
		i = 0;
		addButton.setEnabled(true);
	}
	
	@Override
	public String formatDataToTransfer(TableItem[] selectedTableItems) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(sourceTable.getSelection()[0].getText() + "#");
		
		return buffer.toString();
	}
	
	@Override
	public void addNewProperty(TableViewer tv, String fieldName) {
		if(propertyList.size() < 1){
			super.addNewProperty(tv, fieldName);
			addButton.setEnabled(false);
		}
	}
}

	