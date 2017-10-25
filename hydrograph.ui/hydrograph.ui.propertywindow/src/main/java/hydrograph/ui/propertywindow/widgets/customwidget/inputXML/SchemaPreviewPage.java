package hydrograph.ui.propertywindow.widgets.customwidget.inputXML;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.customwidgets.mapping.tables.inputtable.TableContentProvider;

public class SchemaPreviewPage extends WizardPage{

	
	private static final String SCHEMA_PREVIEW = "Schema Preview";
	private static final String AVAILABLE_FIELDS = "Available Fields";
	private Composite container;
	private Group grpSchemaPreview;
	private Composite composite;
	private List<GridRow> schemaRecordList;
	private TableViewer availableFieldTableViewer;
	protected SchemaPreviewPage(String pageName) {
		super(pageName);
		setDescription(Messages.PREVIEW_FOR_THE_GENERATED_SCHEMA_FROM_XSD_OR_XML_FILE);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		grpSchemaPreview = new Group(container, SWT.NONE);
		grpSchemaPreview.setText(SCHEMA_PREVIEW);
		grpSchemaPreview.setLayout(new GridLayout(1, false));
		grpSchemaPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		composite = new Composite(grpSchemaPreview, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		availableFieldTableViewer=new TableViewer(composite,SWT.BORDER);
		
		TableViewerColumn availableFieldsColumn = new TableViewerColumn(availableFieldTableViewer, SWT.NONE);
		availableFieldsColumn.setLabelProvider(new AvailableFieldsColumnLabelProvider());
		TableColumn tableColumn = availableFieldsColumn.getColumn();
		tableColumn.setText(AVAILABLE_FIELDS);
		
		Table table=availableFieldTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		addControlListener(table, tableColumn);
    }
	
	private void addControlListener(Table table, TableColumn tableColumn) {
		ControlListener controlistener= new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				for (TableColumn column : table.getColumns()) {
					column.setWidth(table.getSize().x / table.getColumnCount() - (4 - table.getColumnCount()));
				}
			}
		};
		table.addControlListener(controlistener);
	}
	
	public void setPath(String path){
		if(getPreviousPage().isPageComplete()){
			//label.setText(path);
			
		}
	}
	
	public void setInputToTreeViewer(List<GridRow> schemaRecord){
		schemaRecordList=schemaRecord;
		availableFieldTableViewer.setContentProvider(new TableContentProvider());
		availableFieldTableViewer.setLabelProvider(new AvailableFieldsColumnLabelProvider());
		availableFieldTableViewer.setInput(schemaRecord);
		availableFieldTableViewer.refresh();
		
	}

	public List<GridRow> getSchemaRecordList() {
		return schemaRecordList;
	}
	
	class AvailableFieldsColumnLabelProvider extends ColumnLabelProvider {
		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			GridRow record = (GridRow) element;
			return record.getFieldName();
		}
	}
			
}
