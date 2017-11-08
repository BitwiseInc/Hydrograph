package hydrograph.ui.propertywindow.widgets.customwidget.inputXML;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class GenerateSchemaWizard extends Wizard implements INewWizard{
	private static final String SUCCESS = "Success";
	private static final String SCHEMA_PREVIEW_PAGE = "Schema Preview Page";
	private static final String SOURCE_SELECTION_PAGE = "source selection page";
	private static final String SCHEMA_IMPORTED_SUCCESSFULLY = "Schema imported successfully.";
	private static final String GENERATE_SCHEMA = "Generate Schema";
	private SourceSelectionPage sourceSelectionPage;
	private SchemaPreviewPage schemaPreviewPage;
	private String path;
	private Component component;
	private String xmlPath ;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private ELTSchemaGridWidget eltSchemaGridWidget;
	private String loopXpathQuery;
	
	public GenerateSchemaWizard(Component component,ELTSchemaGridWidget eltSchemaGridWidget,
			PropertyDialogButtonBar propertyDialogButtonBar,String loopXpathQuery,String xmlPath ){
		this.component = component;
		this.xmlPath=xmlPath;
		setWindowTitle(GENERATE_SCHEMA);
		this.eltSchemaGridWidget=eltSchemaGridWidget;
		this.propertyDialogButtonBar=propertyDialogButtonBar;
		this.loopXpathQuery=loopXpathQuery;
		}
	
	@Override
	public boolean performFinish() {
		List<GridRow> schemaRecordList=schemaPreviewPage.getSchemaRecordList();
		if(schemaRecordList==null || schemaRecordList.isEmpty()){
			return false;
		}
		Schema schema=new Schema();
		schema.getGridRow().addAll(schemaPreviewPage.getSchemaRecordList());
		eltSchemaGridWidget.refresh(schema);
		WidgetUtility.createMessageBox(SCHEMA_IMPORTED_SUCCESSFULLY,SUCCESS,SWT.ICON_WORKING, getShell());
		propertyDialogButtonBar.enableApplyButton(true);
		return true;
	}

	public void addPages() {
		sourceSelectionPage = new SourceSelectionPage(SOURCE_SELECTION_PAGE, xmlPath,loopXpathQuery);
		schemaPreviewPage = new SchemaPreviewPage(SCHEMA_PREVIEW_PAGE);
		addPage(sourceSelectionPage);	
		addPage(schemaPreviewPage);
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public String getPath(){
		return this.path;
	}
	@SuppressWarnings("unused")
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	
	
	public String getXMLPath(){
		return xmlPath;
	}
	
	@Override
	public boolean canFinish() {
       List<IWizardPage> pages=Arrays.asList(getPages());
       for (int i = 0; i < pages.size(); i++) {
          if(getContainer().getCurrentPage()==pages.get(i)){
        	  if(pages.get(i)==getStartingPage())
        		return false;  
        	  if (!pages.get(i).isPageComplete()) {
        		  return false;
        	  }
          }
          }
        return true;
    }
	
}
