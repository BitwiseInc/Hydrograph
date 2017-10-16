package hydrograph.ui.propertywindow.filter.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.common.ui.internal.dnd.ObjectTransfer;

import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;

public class AvailableFieldComposite extends Composite {

	private final class availableFieldColumnEditingSupport extends EditingSupport {
		private availableFieldColumnEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setValue(Object element, Object value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected Object getValue(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected boolean canEdit(Object element) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private Table availableFieldsTable;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @param dataStructure 
	 */
	public AvailableFieldComposite(Composite parent, int style, FilterLogicDataStructure dataStructure) {
		super(parent, style);
		this.setLayout(new GridLayout(1, false));
		
		TableViewer availableFieldtableViewer = new TableViewer(this, SWT.BORDER | SWT.MULTI|SWT.FULL_SELECTION);
		availableFieldsTable = availableFieldtableViewer.getTable();
		availableFieldsTable.setLinesVisible(true);
		availableFieldsTable.setHeaderVisible(true);
		availableFieldsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(availableFieldtableViewer, SWT.NONE);
		TableColumn tblclmnAvailableFields = tableViewerColumn.getColumn();
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			
			@Override
			public String getText(Object element) {
				return String.valueOf(element);
			}
		});
		tblclmnAvailableFields.setWidth(100);
		tblclmnAvailableFields.setText("Available Fields");
		
		availableFieldtableViewer.setContentProvider(new AvailableFieldContentProvider());
		availableFieldtableViewer.setInput(dataStructure.getAvailableFields());
		tblclmnAvailableFields.pack();
		addDoubleClickListener(availableFieldtableViewer);
		
		availableFieldsTable.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				int tableSize=availableFieldsTable.getSize().x;
				tblclmnAvailableFields.setWidth(tableSize-4);
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
			}
		});
	}

	private void addDoubleClickListener(TableViewer availableFieldtableViewer) {
		DragSource	dragSource = new DragSource(availableFieldsTable, DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] { ObjectTransfer.getInstance() });
		
		dragSource.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) { // Set the data to be the first selected item's text
				List<Object> list=new ArrayList<Object>();
				for(TableItem tableItem:availableFieldsTable.getSelection()){
					list.add(tableItem.getData());
				}
				event.data=list.toArray(new Object[list.size()]);
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	
class AvailableFieldContentProvider implements IStructuredContentProvider{

	@Override
	public Object[] getElements(Object inputElement) {
		return ((List)inputElement).toArray();
	}
	
}
	
}
