package hydrograph.ui.propertywindow.widgets.customwidget.inputXML;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTFilePathWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.TextBoxWithLabelWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.TextBoxWithLableConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;

public class InputXMLGenerateSchemaWidget extends AbstractWidget{
	
	private Button generateSchemaButton;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private Shell shell;
	private List<AbstractWidget> widgets;
	
	public InputXMLGenerateSchemaWidget(ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);
		this.propertyDialogButtonBar=propertyDialogButtonBar;
		
	}
	
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();

		AbstractELTWidget eltDefaultLable = new ELTDefaultLable(Messages.GENERATE_SCHEMA_LABEL);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);
		setPropertyHelpWidget((Control) eltDefaultLable.getSWTWidgetControl());

		AbstractELTWidget eltDefaultButton = new ELTDefaultButton(Messages.GENERATE_SCHEMA_BUTTON_LABEL);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultButton);
		generateSchemaButton = (Button) eltDefaultButton.getSWTWidgetControl();		
		
		generateSchemaButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String loopXPathData=getTextBoxData();
				String filePathData=getFilePath();
				GenerateSchemaWizard generateSchemaWizard = new GenerateSchemaWizard(getComponent(),getSchemaWidget(),propertyDialogButtonBar,
						loopXPathData,filePathData);
				WizardDialog dialog = new WizardDialog(generateSchemaButton.getShell(), generateSchemaWizard);
				dialog.setMinimumPageSize(500,400); 
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}
    private String getFilePath(){
    	String textBoxData=null;
		for(AbstractWidget widget:widgets){
			if(ELTFilePathWidget.class.isAssignableFrom(widget.getClass())){
				ELTFilePathWidget filePathWidget=(ELTFilePathWidget)widget;
				textBoxData= filePathWidget.getTextBox().getText();
				break;
				
			}
			
		}
		return textBoxData;
    }
	private String getTextBoxData() {
		String textBoxData=null;
		for(AbstractWidget widget:widgets){
			if(TextBoxWithLabelWidget.class.isAssignableFrom(widget.getClass())){
				TextBoxWithLabelWidget textBoxWithLabelWidget=(TextBoxWithLabelWidget)widget;
				TextBoxWithLableConfig textBoxWithLableConfig=textBoxWithLabelWidget.getWidgetConfig();
				if(Messages.LOOP_XPATH_QUERY.equals(textBoxWithLableConfig.getName())){
					textBoxData= textBoxWithLabelWidget.getTextBox().getText();
					break;
				}
			}
			
		}
		return textBoxData;
	}

	private ELTSchemaGridWidget getSchemaWidget() {
		for(AbstractWidget abstractWidget:widgets){
			if(abstractWidget instanceof ELTSchemaGridWidget){
				return (ELTSchemaGridWidget)abstractWidget;
			}
		}
		return null;
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		return null;
	}

	@Override
	public boolean isWidgetValid() {
		return true;
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		this.widgets=widgetList;
	}

}
