package hydrograph.ui.propertywindow.ftp;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class FTPOperationConfigDialog extends Dialog{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(FTPOperationConfigDialog.class);
	private String windowLabel;
	private Cursor cursor;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private Combo authenticationModeCombo;
	private String[] optionList;
	private Text text1;
	private Text text2;
	private Text text3;
	private Text text4;
	private Combo overwriteCombo;
	private Map<String, FTPAuthOperationDetails> authOperationSelectionMap;
	private ControlDecoration text1ControlDecoration;
	private ControlDecoration text2ControlDecoration;
	private Composite putFileComposite1;
	private Composite putFileComposite2;
	private Label overWriteLabel;
	private String protocol;
	private int COMPOSITE_CONST_HEIGHT=0;
	private ControlDecoration text3ControlDecoration;
	private ControlDecoration text4ControlDecoration;
	
	
	protected FTPOperationConfigDialog(Shell parentShell, String windowTitle,PropertyDialogButtonBar propertyDialogButtonBar,
			Map<String, FTPAuthOperationDetails> initialMap, Cursor cursor, String[] optionList, String protocol) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL | SWT.RESIZE);
		if (StringUtils.isNotBlank(windowTitle))
			windowLabel = windowTitle;
		else
			windowLabel = Messages.ADDITIONAL_PARAMETERS_FOR_DB_WINDOW_LABEL;
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		this.cursor = cursor;
		this.optionList = optionList;
		this.authOperationSelectionMap = initialMap;
		this.protocol = protocol;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(windowLabel);
		
		if(StringUtils.equalsIgnoreCase(protocol, "AWS S3 HTTPS")){
			COMPOSITE_CONST_HEIGHT = 330;
		}else {
			COMPOSITE_CONST_HEIGHT = 276;
		}
		
		Shell shell = container.getShell();
		shell.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                Rectangle rect = shell.getBounds();
                if(rect.width != COMPOSITE_CONST_HEIGHT) {
                    shell.setBounds(rect.x, rect.y, rect.width, COMPOSITE_CONST_HEIGHT);
                }
            }
        });
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		Label operationLabel = (Label) ftpWidgetUtility.createLabel(composite, "Operation");
		authenticationModeCombo = (Combo) ftpWidgetUtility.CreateCombo(composite, optionList);
		
		
		Composite composite2 = new Composite(container, SWT.BORDER);
		composite2.setLayout(new GridLayout(1, false));
		composite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Composite stackComposite = new Composite(composite2, SWT.BORDER);
		StackLayout layout = new StackLayout();
		stackComposite.setLayout(layout);
		stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		stackComposite.getShell().setText("Operation Config");
		createOperationConfigArea(stackComposite, layout);
		
		addModifyListener(text1);
		addModifyListener(text2);
		addModifyListener(text3);
		addModifyListener(text4);
		
		populateWidget();
		
		return container;
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

	private void populateWidget() {
		for(Map.Entry<String, FTPAuthOperationDetails> map : authOperationSelectionMap.entrySet()){
			authenticationModeCombo.setText(map.getKey());
			FTPAuthOperationDetails authOperationDetails = map.getValue();
			text1.setText(authOperationDetails.getField1());
			text2.setText(authOperationDetails.getField2());
			if(StringUtils.equalsIgnoreCase(protocol, "AWS S3 HTTPS")){
				if(authOperationDetails.getField3() != null && authOperationDetails.getField4() != null){
					text3.setText(authOperationDetails.getField3());
					text4.setText(authOperationDetails.getField4());
				}
			}
			
			if(map.getKey().contains("Put Files")){
				overwriteCombo.select(0);
				overWriteLabel.setEnabled(false);
				overwriteCombo.setEnabled(false);
			}
			if(StringUtils.equalsIgnoreCase(authOperationDetails.getField5(), "Overwrite If Exists")){
				overwriteCombo.select(1);
			}else if(StringUtils.equalsIgnoreCase(authOperationDetails.getField5(), "Fail If Exists")){
				overwriteCombo.select(2);
			}else{overwriteCombo.select(0);}
		}
	}

	private void createOperationConfigArea(Composite stackComposite, StackLayout stackLayout){
		if(StringUtils.equalsIgnoreCase(protocol, "AWS S3 HTTPS")){
			Composite composite = (Composite) S3GetPutFile(stackComposite);
			stackLayout.topControl = composite;
		}else{
			Composite composite = (Composite) addOperationGetPutFiles(stackComposite);
			stackLayout.topControl = composite;
		}
		authenticationModeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(authenticationModeCombo.getSelectionIndex() == 1){
					overWriteLabel.setEnabled(false);
					overwriteCombo.setEnabled(false);
					if(StringUtils.equalsIgnoreCase(protocol, "AWS S3 HTTPS")){
						updateWidgetsValue(text1, text2, text3, text4);
					}else{
						updateWidgetsValue(text1, text2);
					}
				}else{
					overWriteLabel.setEnabled(true);
					overwriteCombo.setEnabled(true);
					if(StringUtils.equalsIgnoreCase(protocol, "AWS S3 HTTPS")){
						updateWidgetsValue(text1, text2, text3, text4);
					}else{
						updateWidgetsValue(text1, text2);
					}
					overwriteCombo.select(0);
				}
			}
		});
	}
	
	private void updateWidgetsValue(Widget... widgets){
			for(int i = 0;  i <= widgets.length - 1; i++){
				if(widgets[i] != null && !widgets[i].isDisposed()){
					((Text)widgets[i]).setText("");;
				}
			}
	}
	
	private Control addOperationGetPutFiles(Composite control){
		Composite composite = new Composite(control, SWT.BORDER);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		ftpWidgetUtility.createLabel(composite, "Local Path");
		text1 = (Text) ftpWidgetUtility.createText(composite, "", SWT.BORDER);
		Button localPathBrwsBtn = new Button(composite, SWT.NONE);
		localPathBrwsBtn.setText("...");
		selectionListener(localPathBrwsBtn, text1);
		
		ftpWidgetUtility.createLabel(composite, "Remote Path");
		text2 = (Text) ftpWidgetUtility.createText(composite, "", SWT.BORDER);
		new Button(composite, SWT.NONE).setVisible(false);
		
		overWriteLabel = (Label) ftpWidgetUtility.createLabel(composite, "Write Mode");
		overwriteCombo = (Combo) ftpWidgetUtility.CreateCombo(composite, new String[]{"--Select--", "Overwrite If Exists", "Fail If Exists"});
		
		if(text1ControlDecoration == null){
			text1ControlDecoration = WidgetUtility.addDecorator(text1,Messages.EMPTYFIELDMESSAGE);
		}
		if(text2ControlDecoration == null){
			text2ControlDecoration = WidgetUtility.addDecorator(text2,Messages.EMPTYFIELDMESSAGE);
		}
		
		FTPWidgetUtility widgetUtility = new FTPWidgetUtility();
		widgetUtility.validateEmptyWidgetText(text1, propertyDialogButtonBar, cursor, text1ControlDecoration);
		widgetUtility.validateEmptyWidgetText(text2, propertyDialogButtonBar, cursor, text2ControlDecoration);
		
		return composite;
	}
	
	private Control S3GetPutFile(Composite control){
		Composite composite = new Composite(control, SWT.BORDER);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		ftpWidgetUtility.createLabel(composite, "Local Path");
		text1 = (Text) ftpWidgetUtility.createText(composite, "", SWT.BORDER);
		Button localPathBrwsBtn = new Button(composite, SWT.NONE);
		localPathBrwsBtn.setText("...");
		selectionListener(localPathBrwsBtn, text1);
		
		ftpWidgetUtility.createLabel(composite, "S3 Bucket");
		text2 = (Text) ftpWidgetUtility.createText(composite, "", SWT.BORDER);
		new Button(composite, SWT.NONE).setVisible(false);
		
		ftpWidgetUtility.createLabel(composite, "S3 File Path");
		text3 = (Text) ftpWidgetUtility.createText(composite, "", SWT.BORDER);
		new Button(composite, SWT.NONE).setVisible(false);
		
		ftpWidgetUtility.createLabel(composite, "AWS Region");
		text4 = (Text) ftpWidgetUtility.createText(composite, "", SWT.BORDER);
		new Button(composite, SWT.NONE).setVisible(false);
		
		overWriteLabel = (Label) ftpWidgetUtility.createLabel(composite, "Write Mode");
		overwriteCombo = (Combo) ftpWidgetUtility.CreateCombo(composite, new String[]{"--Select--", "Overwrite If Exists", "Fail If Exists"});
		overWriteLabel.setEnabled(true);
		overwriteCombo.setEnabled(true);
		
		text1ControlDecoration = WidgetUtility.addDecorator(text1, Messages.EMPTYFIELDMESSAGE);
		text2ControlDecoration = WidgetUtility.addDecorator(text2, Messages.EMPTYFIELDMESSAGE);
		text3ControlDecoration = WidgetUtility.addDecorator(text3, Messages.EMPTYFIELDMESSAGE);
		text4ControlDecoration = WidgetUtility.addDecorator(text4, Messages.EMPTYFIELDMESSAGE);
		
		FTPWidgetUtility widgetUtility = new FTPWidgetUtility();
		widgetUtility.validateEmptyWidgetText(text1, propertyDialogButtonBar, cursor, text1ControlDecoration);
		widgetUtility.validateEmptyWidgetText(text2, propertyDialogButtonBar, cursor, text2ControlDecoration);
		widgetUtility.validateEmptyWidgetText(text3, propertyDialogButtonBar, cursor, text3ControlDecoration);
		widgetUtility.validateEmptyWidgetText(text4, propertyDialogButtonBar, cursor, text4ControlDecoration);
		
		return composite;
	}
	
	
	/**
	 * @param button
	 * @param txt
	 */
	private void selectionListener(Button button, Text txt){
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog filedialog = new FileDialog(button.getShell(), SWT.None);
				txt.setText(filedialog.open());
			}
		});
	}
	
	/**
	 * @return param value map
	 */
	public Map<String, FTPAuthOperationDetails> getOperationParamDetails() {
		return authOperationSelectionMap;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(450, 276);
	}
	
	@Override
	protected void okPressed() {
		String isOverwrite = null;
		String text3Value = null;
		String text4Value = null;
		
		authOperationSelectionMap = new LinkedHashMap<String, FTPAuthOperationDetails>();
		if(overwriteCombo.isEnabled()){
				isOverwrite = overwriteCombo.getText();
		}
		
		if(text3 !=null){
			text3Value = text3.getText();
		}
		if(text3 != null){
			text4Value = text4.getText();
		}
		FTPAuthOperationDetails authOperationDetails = new FTPAuthOperationDetails(text1.getText(), 
				text2.getText(), text3Value, text4Value, isOverwrite);
		authOperationSelectionMap.put(authenticationModeCombo.getText(), authOperationDetails);
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}
}
