package hydrograph.ui.propertywindow.filter.viewer;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.expression.editor.util.FieldDataTypeMap;
import hydrograph.ui.propertywindow.filter.FilterExpressionOperationDialog;
import hydrograph.ui.propertywindow.widgets.utility.FilterOperationExpressionValidation;

public class ErrorLogTableViewer extends TableViewer {

	private static final String ERROR_LOGS_COLUMN_HEADER = "Error Logs";
	private Table errorLogTable;
	private Set<String> errors=new LinkedHashSet<String>();
	private FilterLogicDataStructure dataStructure;
	private FilterExpressionOperationDialog dialog;
	private TableColumn errorLogColumn;
	
	public ErrorLogTableViewer(Composite parent, FilterExpressionOperationDialog dialog, int style, FilterLogicDataStructure dataStructure ) {
		super(parent, style);
		this.dataStructure=dataStructure;
		this.dialog=dialog;
		errorLogTable = getTable();
		errorLogTable.setLinesVisible(true);
		errorLogTable.setHeaderVisible(true);
		errorLogTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn errorLogColumnViewer = new TableViewerColumn(this, SWT.NONE);
		errorLogColumn = errorLogColumnViewer.getColumn();
		errorLogColumn.setWidth(100);
		errorLogColumn.setText(ERROR_LOGS_COLUMN_HEADER);
		
		errorLogColumnViewer.setLabelProvider(new ColumnLabelProvider() {
			
			@Override
			public String getText(Object element) {
				return String.valueOf(element);
			}
			
			@Override
			public Color getForeground(Object element) {
				return new Color(null, 255,0,0);
			}
		});
		
		setContentProvider(new ErrorLogsContentProvider());
		setInput(errors);
		refreshErrors();
		
		errorLogTable.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				int tableSize=errorLogTable.getSize().x;
				errorLogColumn.setWidth(tableSize-4);
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
			}
		});
	}

	public void refreshErrors() {
		if (dataStructure != null) {
			errors.clear();
			if(dataStructure.isOperation()){
				FilterOperationExpressionValidation.INSTANCE.validateOperationClassData(dataStructure, errors, this);
			}else{
				FilterOperationExpressionValidation.INSTANCE.validationExpressionData(dataStructure,
						FieldDataTypeMap.INSTANCE.createFieldDataTypeMap(
								dataStructure.getExpressionEditorData().getInputFields(), dialog.getSchemaFields()),
						errors, this);
			}
		}
		refresh();
	}
	
	class ErrorLogsContentProvider implements IStructuredContentProvider {
	
		@Override
		public Object[] getElements(Object inputElement) {
			return ((Set)inputElement).toArray();
		}
		
	}
	
}
