package hydrograph.ui.propertywindow.filter.composites;

import java.util.List;

import javax.swing.JToolBar.Separator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wst.common.ui.internal.dnd.ObjectTransfer;

import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.propertywindow.filter.FilterExpressionOperationDialog;

public class InputFieldsComposite extends Composite {
	private Table inputFieldTable;
	private List<String> inputFields;
	private TableViewer inputFieldTableViewer;
	private FilterExpressionOperationDialog dialog;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param dialog
	 * @param style
	 * @param inputFields
	 */
	public InputFieldsComposite(Composite parent, FilterExpressionOperationDialog dialog, int style,
			List<String> inputFields) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		this.inputFields = inputFields;
		this.dialog = dialog;

		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));

		deleteToolItem(toolBar);

		inputFieldTableViewer = new TableViewer(this, SWT.BORDER |SWT.MULTI |SWT.FULL_SELECTION);
		inputFieldTable = inputFieldTableViewer.getTable();
		inputFieldTable.setLinesVisible(true);
		inputFieldTable.setHeaderVisible(true);
		inputFieldTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn inputFieldColumnViewer = new TableViewerColumn(inputFieldTableViewer, SWT.NONE);
		TableColumn inputFieldColumn = inputFieldColumnViewer.getColumn();
		inputFieldColumn.setWidth(100);
		inputFieldColumn.setText("InputFields");

		inputFieldColumnViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return String.valueOf(element);
			}
		});

		inputFieldColumnViewer.setEditingSupport(new InputFieldEditingSupport(inputFieldColumnViewer.getViewer()));
		inputFieldTableViewer.setContentProvider(new InputFieldContentProvider());

		inputFieldTableViewer.setInput(inputFields);
		addDropSuuport(inputFieldTableViewer);
		inputFieldColumn.pack();
		
		inputFieldTable.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				int tableSize=inputFieldTable.getSize().x;
				inputFieldColumn.setWidth(tableSize-4);
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
			}
		});
	}

	private void addDropSuuport(TableViewer tableViewer) {
		DropTarget dropTarget = new DropTarget(inputFieldTable, DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] { ObjectTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetAdapter() {

			public void drop(DropTargetEvent event) {
				Object[] objects = (Object[]) event.data;
				for (Object field : objects) {
					if (!inputFields.contains(String.valueOf(field))) {
						inputFields.add(String.valueOf(field));
					}
				}
				tableViewer.refresh();
				dialog.refreshErrorLogs();
			}

			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT || event.detail == DND.DROP_NONE) {
					event.detail = DND.DROP_COPY;
				}
			}
		});
	}

	private void deleteToolItem(ToolBar toolBar) {
		ToolItem tltmDelete = new ToolItem(toolBar, SWT.NONE);
		tltmDelete.setWidth(5);
		tltmDelete.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		tltmDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem tableItem : inputFieldTable.getSelection()) {
					inputFields.remove(String.valueOf(tableItem.getData()));
				}
				inputFieldTableViewer.refresh();
				dialog.refreshErrorLogs();
				addCusrsorToLastRow();
			}
		});
	}

	private void addCusrsorToLastRow() {
		int index = inputFields.size() - 1;
		if (index > -1) {
			{
				inputFieldTableViewer.editElement(inputFieldTableViewer.getElementAt(index), 0);
			}
		}
	}

	private void addToolItem(ToolBar toolBar) {
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputFields.add("Field");
				dialog.refreshErrorLogs();
			}
		});
	}

	@Override
	protected void checkSubclass() {
		/* Disable the check that prevents subclassing of SWT components */}

	public void refresh() {
		inputFieldTableViewer.refresh();
	}

	class InputFieldContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			return ((List) inputElement).toArray();
		}

	}

	class InputFieldEditingSupport extends EditingSupport {

		private TextCellEditor cellEditor;

		public InputFieldEditingSupport(ColumnViewer viewer) {
			super(viewer);
			cellEditor = new TextCellEditor((Table) viewer.getControl());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return String.valueOf(element);
		}

		@Override
		protected void setValue(Object element, Object value) {
			String currentValue = String.valueOf(element);
			String newValue = String.valueOf(value);
			int index = inputFields.indexOf(currentValue);
			if (index > -1 && StringUtils.isNotBlank(newValue) && !inputFields.contains(newValue)) {
				inputFields.set(index, newValue);
			}
			getViewer().refresh();
		}
	}
}
