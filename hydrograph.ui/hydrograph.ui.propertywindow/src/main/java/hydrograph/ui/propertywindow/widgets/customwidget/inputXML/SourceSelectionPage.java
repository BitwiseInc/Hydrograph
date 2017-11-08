package hydrograph.ui.propertywindow.widgets.customwidget.inputXML;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.XMLUtil;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class SourceSelectionPage extends WizardPage{
	
	private String xmlPath;
	private String looXPathQuery;
	
	private static final Logger lOGGEER = LogFactory.INSTANCE.getLogger(SourceSelectionPage.class);
	protected SourceSelectionPage(String pageName,String xmlPath,String looXPathQuery) {
		super(pageName);
		this.xmlPath =xmlPath;
		this.looXPathQuery=looXPathQuery;
		setTitle(Messages.SELECT_XML_SOURCE_FOR_SCHEMA_IMPORT);
	}

    private Composite container;
    private Text filePathTextBox;
	Composite filePathComposite;
	private ControlDecoration errorDecorator;
	Button xmlRadioButton;
	Button xsdRadioButton;
	

	@Override
	public IWizardPage getNextPage() {
		if(xmlRadioButton.getSelection()){
			if(StringUtils.isNotBlank(xmlPath)){
				File xmlFile=new File(xmlPath);
				if(xmlFile.exists() && StringUtils.endsWith(xmlPath, Constants.XML_EXTENSION)){
					SchemaPreviewPage page = (SchemaPreviewPage)super.getNextPage();
					page.setPath(xmlPath);	
					setErrorMessage(null);
					List<GridRow> schemaRecordList=generateSchemaRecordFromXML(xmlFile);
					if(schemaRecordList==null){
						page.setPageComplete(false);
						return null;
					}
					page.setInputToTreeViewer(schemaRecordList);
					return page;
				}else{
					setErrorMessage(Messages.INVALID_PATH);
				}
			}else{
				setErrorMessage(Messages.PLEASE_PROVIDE_XML_PATH_FIRST);
				return null;
			}
		}else if(xsdRadioButton.getSelection()){
			File file = new File(filePathTextBox.getText());
			if(file.exists() && StringUtils.endsWith(filePathTextBox.getText(), ".xsd")){
				SchemaPreviewPage page = (SchemaPreviewPage)super.getNextPage();
				page.setPath(filePathTextBox.getText());
				List<GridRow> schemaRecordList=generateSchemaRecordFromXSD(filePathTextBox.getText());
				if(schemaRecordList==null){
					page.setPageComplete(false);
					return null;
					}
				page.setInputToTreeViewer(schemaRecordList);
				return page;
			}else{
				setErrorMessage(Messages.INVALID_PATH);
			}
		}
		return null;
	}

	private List<GridRow> generateSchemaRecordFromXML(File xmlFile) {
		try {
			XMLUtil xmlUtil=new XMLUtil();
			return xmlUtil.getSchemaFromXML(xmlFile,looXPathQuery);
			
		} catch (ParserConfigurationException | SAXException | IOException | JAXBException e) {
			lOGGEER.error("unable to parse the XML file",e.getMessage());
			WidgetUtility.createMessageBox(Messages.INVALID_XML_FILE+e.getMessage(),Constants.ERROR,SWT.ERROR, getShell());
		}
		return null;
	}
	
	
	private List<GridRow> generateSchemaRecordFromXSD(String  XSDFile) {
		try {
			XMLUtil xmlUtil=new XMLUtil();
			return xmlUtil.getSchemaFromXSD(XSDFile,looXPathQuery);
			
		} catch (ParserConfigurationException | SAXException | IOException | JAXBException e) {
			lOGGEER.error("unable to parse the XML file",e.getMessage());
			WidgetUtility.createMessageBox(Messages.INVALID_XML_FILE+e.getMessage(),Constants.ERROR,SWT.ERROR, getShell());
		}
		return null;
	}
	
	@Override
    public void createControl(Composite parent) {
		
        container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		container.setLayout(new GridLayout(1, false));
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		addRadioButton(composite);
		addFilePathComposite(composite);
				
        setControl(container);
        setPageComplete(true);

    }
	
	protected void addRadioButton(Composite container) {
		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		xmlRadioButton = new Button(composite_1, SWT.RADIO);
		xmlRadioButton.setText("XML File");
		xmlRadioButton.setEnabled(true);
		xmlRadioButton.setSelection(true);
		
		xsdRadioButton = new Button(composite_1, SWT.RADIO);
		xsdRadioButton.setText("XSD File");
		
		addSelectionListenerOnXMLRadioBtn();
		addSelectionListenerOnXSDRadioBtn();
		
		
	}

	protected void addFilePathComposite(Composite container) {
		filePathComposite = new Composite(container, SWT.NONE);
		filePathComposite.setLayout(new GridLayout(4, false));
		filePathComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		Label filePathLabel = new Label(filePathComposite, SWT.NONE);
		filePathLabel.setText("XSD File Path");
		new Label(filePathComposite, SWT.NONE);
		
		filePathTextBox = new Text(filePathComposite, SWT.BORDER);
		filePathTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		errorDecorator = WidgetUtility.addDecorator(filePathTextBox, Messages.bind(Messages.EMPTY_FIELD, "XSD File Path"));
		errorDecorator.setMarginWidth(2);
		filePathTextBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
		
		
		filePathTextBox.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if(filePathTextBox.getText().isEmpty()){
					errorDecorator.show();
					filePathTextBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
					setPageComplete(false);
				}
				else{
					errorDecorator.hide();
					 setPageComplete(true);
					
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
		
		filePathTextBox.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if(filePathTextBox.getText().isEmpty()){
					errorDecorator.show();
					filePathTextBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
				}else{
					errorDecorator.hide();
					filePathTextBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
					 setPageComplete(true);
				}
				
				
			}
		});
		
		Button browseBtn = new Button(filePathComposite, SWT.NONE);
		browseBtn.setText(" ... ");
		browseBtn.addSelectionListener(browseListener);
		
		
		
		if(xsdRadioButton.getSelection()){
			filePathComposite.setVisible(true);
		}else{
			filePathComposite.setVisible(false);
		}
		
	}
	
	
	SelectionListener browseListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		    fd.setText(Messages.OPEN_BUTTON_LABEL);
		    fd.setFilterPath(Messages.C_DRIVE_LOCATION);
		    String[] filterExt = {"*.xsd"};
		    fd.setFilterExtensions(filterExt);
		    filePathTextBox.setText(fd.open());
		}
	};
	
	public void addSelectionListenerOnXSDRadioBtn(){
		xsdRadioButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(xsdRadioButton.getSelection()){
					setErrorMessage(null);
					filePathComposite.setVisible(true);
					if(StringUtils.isNotBlank(filePathTextBox.getText())){
						setPageComplete(true);
					}else{
						setPageComplete(false);
					}
					
				}else{
					filePathComposite.setVisible(false);
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
			
		
	}
	
	public void addSelectionListenerOnXMLRadioBtn(){
		xsdRadioButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(xmlRadioButton.getSelection()){
					setErrorMessage(null);
					setPageComplete(true);
				}else{
					setPageComplete(false);
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}
	
	 @Override
		public boolean canFlipToNextPage() {
		 return true;
	    }

    
}
