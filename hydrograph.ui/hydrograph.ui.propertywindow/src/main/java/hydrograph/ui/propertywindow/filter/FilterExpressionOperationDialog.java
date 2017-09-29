package hydrograph.ui.propertywindow.filter;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.common.datastructure.filter.OperationClassData;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.filter.composites.AvailableFieldComposite;
import hydrograph.ui.propertywindow.filter.composites.ExpressionComposite;
import hydrograph.ui.propertywindow.filter.composites.OperationComposite;
import hydrograph.ui.propertywindow.filter.viewer.ErrorLogTableViewer;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.interfaces.IOperationClassDialog;

public class FilterExpressionOperationDialog extends Dialog implements IOperationClassDialog {
	private OperationComposite operationComposite;
	private ExpressionComposite expressionComposite;
	private Composite filterLogicInnerComposite;
	private StackLayout filterLogicInnerCompositeLayout;
	private FilterLogicDataStructure dataStructure;
	private ErrorLogTableViewer errorLogTableViewer;
	private Component component;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private WidgetConfig widgetConfig;
	private List<FixedWidthGridRow> schemaFields;
	private String title;
	private boolean isYesButtonPressed;
	private boolean isNoButtonPressed;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param list 
	 */
	public FilterExpressionOperationDialog(Shell parentShell, FilterLogicDataStructure dataStructure,
			Component component, PropertyDialogButtonBar propertyDialogButtonBar, WidgetConfig widgetConfig, List<FixedWidthGridRow> schemaFields) {
		super(parentShell);
		this.component = component;
		this.dataStructure = dataStructure;
		this.propertyDialogButtonBar=propertyDialogButtonBar;
		this.widgetConfig=widgetConfig;
		this.schemaFields=schemaFields;
		isYesButtonPressed = false;
		isNoButtonPressed = false;
		updateAvailableFields(dataStructure, schemaFields);
	}

	
	
	private void updateAvailableFields(FilterLogicDataStructure dataStructure, List<FixedWidthGridRow> schemaFields) {
		dataStructure.getAvailableFields().clear();
		for (GridRow gridRow : schemaFields) {
			dataStructure.getAvailableFields().add(gridRow.getFieldName());
		}
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		getShell().setText(title);
		
		SashForm mainSashForm = new SashForm(container, SWT.BORDER);
		mainSashForm.setSashWidth(2);
		mainSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createAvailableFieldTable(mainSashForm);
		
		Composite filterLogicMainComposite = new Composite(mainSashForm, SWT.NONE);
		filterLogicMainComposite.setLayout(new GridLayout(1, false));
		
		SashForm filterLogicSashForm = new SashForm(filterLogicMainComposite, SWT.VERTICAL);
		filterLogicSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		filterLogicInnerComposite = new Composite(filterLogicSashForm, SWT.BORDER);
		filterLogicInnerCompositeLayout = new StackLayout();
		filterLogicInnerComposite.setLayout(filterLogicInnerCompositeLayout);
		
		createExpressionOrOperationComposite(filterLogicInnerComposite);
		
		createErrorLogsTable(filterLogicSashForm);
		
		filterLogicSashForm.setWeights(new int[] {3, 1});
		mainSashForm.setWeights(new int[] {1, 2});
		return container;
	}

	private void createErrorLogsTable(SashForm filterLogicSashForm) {
		errorLogTableViewer = new ErrorLogTableViewer(filterLogicSashForm, this , SWT.BORDER, dataStructure);
	}

	private void createAvailableFieldTable(SashForm mainSashForm) {
		new AvailableFieldComposite(mainSashForm, SWT.NONE, dataStructure);
	}
	
	private void createExpressionOrOperationComposite(Composite filterLogicInnerComposite){
		operationComposite = new OperationComposite(this, filterLogicInnerComposite, SWT.NONE);
		expressionComposite = new ExpressionComposite(this, filterLogicInnerComposite, SWT.NONE);
		if(dataStructure.isOperation()){
			filterLogicInnerCompositeLayout.topControl=operationComposite;
		}else{
			filterLogicInnerCompositeLayout.topControl=expressionComposite;
		}
	}
	
	public FilterLogicDataStructure getDataStructure() {
		return dataStructure;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	public void switchToExpression(){
		filterLogicInnerCompositeLayout.topControl=expressionComposite;
		filterLogicInnerComposite.layout();
		operationComposite.resetRadioButton();
	}
	
	public void switchToOperation(){
		filterLogicInnerCompositeLayout.topControl=operationComposite;
		filterLogicInnerComposite.layout();
		expressionComposite.resetRadioButton();
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(900, 590);
	}
	
	public void refreshErrorLogs() {
		if (errorLogTableViewer != null) {
			errorLogTableViewer.refreshErrors();
		}
	}
	
	protected boolean isResizable() {
		return true;
	}

	public WidgetConfig getWidgetConfig() {
		return widgetConfig;
	}

	public PropertyDialogButtonBar getPropertyDialogButtonBar() {
		return propertyDialogButtonBar;
	}

	public Component getComponent() {
		return component;
	}
	
	public List<FixedWidthGridRow> getSchemaFields() {
		return schemaFields;
	}
	
	public void  setTitle(String dialogTitle){
		this.title=dialogTitle;
	}



	@Override
	public void pressOK() {
		isYesButtonPressed = true;
		okPressed();
	}



	@Override
	public void pressCancel() {
	    isNoButtonPressed = true;
		cancelPressed();
		
	}
	
	/**
	 * 
	 * returns true if ok button pressed from code
	 * 
	 * @return boolean
	 */
	public boolean isYesButtonPressed() {
		return isYesButtonPressed;
	}
	
	/**
	 * 
	 * returns true of cancel button pressed from code
	 * 
	 * @return boolean
	 */
	public boolean isNoButtonPressed() {
		return isNoButtonPressed;
	}
}
