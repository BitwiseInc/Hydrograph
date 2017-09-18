package hydrograph.ui.propertywindow.ftp;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultCombo;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class ProtocolWidget extends AbstractWidget{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ProtocolWidget.class);
	private String propertyName;
	private Cursor cursor;
	private FTPProtocolDetails ftpProtocolDetails;
	private ArrayList<AbstractWidget> widgets;
	private LinkedHashMap<String, Object> tempPropertyMap;
	private Combo combo;
	private Text hostText;
	private Text portText;
	private ControlDecoration hostDecorator;
	private ControlDecoration portDecorator;
	
	public ProtocolWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		this.propertyName = componentConfigProp.getPropertyName();
		if(componentConfigProp.getPropertyValue() != null && !componentConfigProp.getPropertyValue().equals("")){
			this.ftpProtocolDetails = (FTPProtocolDetails) componentConfigProp.getPropertyValue();
		}
		if (ftpProtocolDetails == null && componentConfigProp.getPropertyValue().equals("")) {
			ftpProtocolDetails = new FTPProtocolDetails("", "", "");
		}
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				subGroup.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();
		eltSuDefaultSubgroupComposite.numberOfBasicWidgets(2);
		
		ELTDefaultLable protocolLbl = new ELTDefaultLable("Protocol");
		eltSuDefaultSubgroupComposite.attachWidget(protocolLbl);
		setPropertyHelpWidget((Control) protocolLbl.getSWTWidgetControl());
		
		AbstractELTWidget comboWidget = new ELTDefaultCombo().defaultText(new String[]{"FTP", "SFTP", "AWS S3 HTTPS"})
				.grabExcessHorizontalSpace(false);
		eltSuDefaultSubgroupComposite.attachWidget(comboWidget);
		combo=(Combo)comboWidget.getSWTWidgetControl();
		GridData data = (GridData) combo.getLayoutData();
		data.horizontalIndent = 5;
		data.verticalIndent = 5;
		combo.select(0);
		
		ELTDefaultLable hostLbl = new ELTDefaultLable("Host");
		eltSuDefaultSubgroupComposite.attachWidget(hostLbl);
		setPropertyHelpWidget((Control) hostLbl.getSWTWidgetControl());
		
		AbstractELTWidget hostwidget = createWidgetTextbox("Host", eltSuDefaultSubgroupComposite, "");
		hostText = (Text) hostwidget.getSWTWidgetControl();
		Utils.INSTANCE.addMouseMoveListener(hostText, cursor);
		attachListeners(hostwidget);
		hostDecorator = WidgetUtility.addDecorator((Text) hostwidget.getSWTWidgetControl(),
				Messages.bind(Messages.EMPTY_FIELD, "Host"));
		hostDecorator.setMarginWidth(3);
		ListenerHelper hostHelper = new ListenerHelper();
		hostHelper.put(HelperType.CONTROL_DECORATION, hostDecorator);
		
		ELTDefaultLable portLbl = new ELTDefaultLable("Port");
		eltSuDefaultSubgroupComposite.attachWidget(portLbl);
		setPropertyHelpWidget((Control) portLbl.getSWTWidgetControl());
		
		AbstractELTWidget portwidget = createWidgetTextbox("Port", eltSuDefaultSubgroupComposite, "");
		portText = (Text) portwidget.getSWTWidgetControl();
		Utils.INSTANCE.addMouseMoveListener(portText, cursor);
		attachListeners(portwidget);
		
		portDecorator = WidgetUtility.addDecorator((Text) portwidget.getSWTWidgetControl(),
				Messages.bind(Messages.EMPTY_FIELD, "Port"));
		portDecorator.setMarginWidth(3);
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, portDecorator);
		try {
			hostwidget.attachListener(Listners.MODIFY.getListener(), propertyDialogButtonBar, hostHelper, hostwidget.getSWTWidgetControl());
			portwidget.attachListener(Listners.MODIFY_NUMERIC_AND_PARAMETER.getListener(), propertyDialogButtonBar, helper, 
					portwidget.getSWTWidgetControl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//`(portwidget);
		
		selectionListener();
		addModifyListener(hostText);
		addModifyListener(portText);
		
		populateWidgets();
	}
	
	private void selectionListener(){
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(combo.getSelectionIndex() == 2){
					hostText.setText("");
					portText.setText("");
					hostDecorator.hide();
					portDecorator.hide();
					hostText.setEnabled(false);
					portText.setEnabled(false);
					hostText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
					portText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
				}else if(combo.getSelectionIndex() == 1){
					hostText.setText("");
					portText.setText("");
					hostText.setEnabled(true);
					portText.setEnabled(true);
				}else{
					hostText.setText("");
					portText.setText("");
					hostDecorator.show();
					portDecorator.show();
					hostText.setEnabled(true);
					portText.setEnabled(true);
					hostText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
					portText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
				}
				showHideErrorSymbol(widgets);
				propertyDialogButtonBar.enableApplyButton(true);
			}
		});
	}
	
	
	private void addModifyListener(Text text){
		if(text != null && !text.isDisposed()){
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Utils.INSTANCE.addMouseMoveListener(text, cursor);	
				}
			});
		}
	}
	
	private void populateWidgets(){
		if(ftpProtocolDetails != null){
			if(ftpProtocolDetails.getProtocol() ==null || ftpProtocolDetails.getProtocol().isEmpty()){
				combo.select(0);
			}else{
				combo.setText(ftpProtocolDetails.getProtocol());
			}
			if(StringUtils.equalsIgnoreCase(ftpProtocolDetails.getProtocol(), "AWS S3 HTTPS")){
				hostText.setText("");
				hostDecorator.hide();
				hostText.setEnabled(false);
				hostText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
				portText.setText("");
				portDecorator.hide();
				portText.setEnabled(false);
				portText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			}else{
				if(StringUtils.isNotBlank(ftpProtocolDetails.getHost())){
					hostText.setText(ftpProtocolDetails.getHost());
				}
				portText.setText(ftpProtocolDetails.getPort());
			}
		}
	}
	
	private void attachListeners(AbstractELTWidget textBoxWidget) {
		try {
			textBoxWidget.attachListener(Listners.EVENT_CHANGE.getListener(), propertyDialogButtonBar, null,
					textBoxWidget.getSWTWidgetControl());
		} catch (Exception exception) {
			logger.error("Failed in attaching listeners to {}, {}", exception);
		}
	}
	
	/**
	 * Create TextBoxes on Stack layout composite
	 * @param labelName
	 * @param compositeWithStack
	 * @return
	 */
	private AbstractELTWidget createWidgetTextbox(String labelName, ELTDefaultSubgroupComposite compositeWithStack, String text) {

		AbstractELTWidget textboxWgt = new ELTDefaultTextBox()
				.grabExcessHorizontalSpace(true).defaultText(text);
		compositeWithStack.attachWidget(textboxWgt);
		Text textbox = ((Text) textboxWgt.getSWTWidgetControl());
		
		GridData data = (GridData) textbox.getLayoutData();
		data.horizontalIndent = 5;
		data.verticalIndent = 5;
		data.widthHint = 260;
		return textboxWgt;
	}
	
	private ModifyListener attachTextModifyListner(final ArrayList<AbstractWidget> widgetList) {
		return new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				Utils.INSTANCE.addMouseMoveListener(hostText, cursor);
				Utils.INSTANCE.addMouseMoveListener(portText, cursor);
				showHideErrorSymbol(widgetList);
			}
		};
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		tempPropertyMap = new LinkedHashMap<>();
		FTPProtocolDetails ftpProtocolDetails = new FTPProtocolDetails(combo.getText(), hostText.getText(), portText.getText());
		tempPropertyMap.put(this.propertyName, ftpProtocolDetails);
		showHideErrorSymbol(widgets);
		return tempPropertyMap;
	}

	@Override
	public boolean isWidgetValid() {
		FTPProtocolDetails ftpProtocolDetails = new FTPProtocolDetails(combo.getText(), hostText.getText(), portText.getText());
		return validateAgainstValidationRule(ftpProtocolDetails);
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		widgets = widgetList;
		hostText.addModifyListener(attachTextModifyListner(widgetList));
		portText.addModifyListener(attachTextModifyListner(widgetList));
	}

}
