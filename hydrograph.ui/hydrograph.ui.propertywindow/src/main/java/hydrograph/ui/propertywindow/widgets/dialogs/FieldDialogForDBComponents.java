package hydrograph.ui.propertywindow.widgets.dialogs;


import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Shell;

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
}

	