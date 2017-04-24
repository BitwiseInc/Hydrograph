/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package hydrograph.ui.propertywindow.runconfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.swt.customwidget.HydroGroup;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.communication.messages.Message;
import hydrograph.ui.communication.messages.MessageType;
import hydrograph.ui.communication.utilities.SCPUtility;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;

/**
 * 
 * Run configuration dialog allows user to change run configurations before
 * running the job.
 * 
 * @author bitwise
 *
 */
public class RunConfigDialog extends Dialog {
	
	private static final String TRUE = "true";

	private static final Logger logger = LogFactory.INSTANCE.getLogger(RunConfigDialog.class);
	
	private Text txtBasePath;
	private Text txtEdgeNode;
	private Text txtUserName;
	private Text txtPassword;
	private Text txtKeyFile;
	private Text txtRunUtility;
	private Text txtProjectPath;
	
	private Button browseButton;
	private Button radioPassword;
	private Button radioKeyFile;
	private Button chkbtnSavePassword;
	
	private HydroGroup runModeGroup;
	private HydroGroup serverDetailsGroup;
	private HydroGroup remotePathConfigGroup;
	private Composite groupHolderComposite;
	private Composite remoteRunDetailsHolder;

	private Button viewDataCheckBox;
	private Button btnLocalMode;
	private Button btnRemoteMode;

	private Properties buildProps;

	private final String LOCAL_MODE = "local";
	private final String REMOTE_MODE = "remote";
	private final String HOST = "host";
	private final String USER_NAME = "userName";
	private final String KEY_FILE = "KeyFile";

	private final String RUN_UTILITY = "runUtility";
	private final String REMOTE_DIRECTORY = "remoteDirectory";
	private final String BASE_PATH = "basePath";
	private final String VIEW_DATA_CHECK = "viewDataCheck";
	private final String USE_PASSWORD_AUTHENTICATION = "usePasswordAuthentication";
	public static final String SELECTION_BUTTON_KEY = "REMOTE_BUTTON_KEY";
	
	private static final String SECURE_STORAGE_HYDROGRAPH_CREDENTIALS_RUNCONFIG_DIALOG_NODE = "Run Dialog";
		
	private String KeyFile;
	private String password;
	private String userId;
	private String edgeNodeText;
	private String basePath;
	private boolean remoteMode;
	private String host;
	private String username;
	private boolean isDebug;
	private boolean runGraph;
	private boolean usePassword;
	
	EmptyTextListener textPasswordListener;
	EmptyTextListener keyFileListener;
	
	private static String LOCAL_HOST = "localhost";

	private Composite container;
	
	private Button okButton;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public RunConfigDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		this.runGraph = false;
		buildProps = new Properties();
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(Messages.RUN_CONFIGURATION_SETTINGS);

		groupHolderComposite = new Composite(container, SWT.BORDER);
		groupHolderComposite.setLayout(new GridLayout(1, false));
		groupHolderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		runModeGroup = new HydroGroup(groupHolderComposite, SWT.NONE);
		runModeGroup.setHydroGroupText("Run Mode");
		runModeGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		runModeGroup.getHydroGroupClientArea().setLayout(new GridLayout(1, false));

		Composite composite_3 = new Composite(runModeGroup.getHydroGroupClientArea(), SWT.NONE);
		composite_3.setLayout(new GridLayout(1, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite composite_1 = new Composite(composite_3, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite composite_4 = new Composite(composite_1, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		composite_4.setLayout(new GridLayout(2, false));

		btnLocalMode = new Button(composite_4, SWT.RADIO);
		btnLocalMode.setText(Messages.LOCAL_MODE);
		btnLocalMode.addSelectionListener(runModeSelectionListener);

		btnRemoteMode = new Button(composite_4, SWT.RADIO);
		btnRemoteMode.setText(Messages.REMOTE_MODE);
		btnRemoteMode.addSelectionListener(runModeSelectionListener);

		Composite composite_5 = new Composite(composite_1, SWT.NONE);
		composite_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite_5.setLayout(new GridLayout(1, false));

		viewDataCheckBox = new Button(composite_5, SWT.CHECK);
		viewDataCheckBox.setText(Messages.VIEW_DATA);
		viewDataCheckBox.addSelectionListener(viewDataSelectionListener);

		Composite composite_2 = new Composite(composite_3, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.horizontalSpacing = 15;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblDebugFileLocation = new Label(composite_2, SWT.NONE);
		lblDebugFileLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDebugFileLocation.setText(Messages.BASE_PATH);

		txtBasePath = new Text(composite_2, SWT.BORDER);
		txtBasePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtBasePath.setEnabled(false);
		txtBasePath.setData(SELECTION_BUTTON_KEY, viewDataCheckBox);
		EmptyTextListener basePathListener = new EmptyTextListener(Messages.BASE_PATH);
		txtBasePath.addModifyListener(basePathListener);

		remoteRunDetailsHolder = new Composite(groupHolderComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		remoteRunDetailsHolder.setLayout(gl_composite);
		remoteRunDetailsHolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		serverDetailsGroup = new HydroGroup(remoteRunDetailsHolder, SWT.NONE);
		serverDetailsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		serverDetailsGroup.setHydroGroupText("Server Details");
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 15;
		serverDetailsGroup.getHydroGroupClientArea().setLayout(gridLayout);

		new Label(serverDetailsGroup.getHydroGroupClientArea(), SWT.NONE);	
		
		Composite radioComposite = new Composite(serverDetailsGroup.getHydroGroupClientArea(), SWT.NONE);
		radioComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		radioComposite.setLayout(new GridLayout(2, false));
		
		radioPassword = new Button(radioComposite, SWT.RADIO);
		radioPassword.setText(Messages.LABEL_PWD);
		radioPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		radioPassword.addSelectionListener(radioPasswordSelectionListener);
		
		radioKeyFile = new Button(radioComposite, SWT.RADIO);
		radioKeyFile.setText(Messages.KEY_FILE);
		radioKeyFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		radioKeyFile.addSelectionListener(radioKeyFileSelectionListener);
	
		Label lblEdgeNode = new Label(serverDetailsGroup.getHydroGroupClientArea(), SWT.NONE);
		lblEdgeNode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEdgeNode.setText(Messages.EDGE_NODE);
		
		txtEdgeNode = new Text(serverDetailsGroup.getHydroGroupClientArea(), SWT.BORDER);
		txtEdgeNode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtEdgeNode.setData(SELECTION_BUTTON_KEY, btnRemoteMode);
		EmptyTextListener textEdgeNodeListener = new EmptyTextListener(Messages.EDGE_NODE);
		txtEdgeNode.addModifyListener(textEdgeNodeListener);
		addFocusListener(txtEdgeNode);
		
		Label lblUser = new Label(serverDetailsGroup.getHydroGroupClientArea(), SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser.setText(Messages.USER);

		txtUserName = new Text(serverDetailsGroup.getHydroGroupClientArea(), SWT.BORDER);
		txtUserName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtUserName.setData(SELECTION_BUTTON_KEY, btnRemoteMode);
		EmptyTextListener textUserListener = new EmptyTextListener(Messages.HOST);
		txtUserName.addModifyListener(textUserListener);
		addFocusListener(txtUserName);

		Label lblPassword = new Label(serverDetailsGroup.getHydroGroupClientArea(), SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText(Messages.LABEL_PWD);

		txtPassword = new Text(serverDetailsGroup.getHydroGroupClientArea(), SWT.PASSWORD | SWT.BORDER);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtPassword.setData(SELECTION_BUTTON_KEY, btnRemoteMode);
		
		new Label(serverDetailsGroup.getHydroGroupClientArea(), SWT.NONE);		
		chkbtnSavePassword = new Button(serverDetailsGroup.getHydroGroupClientArea(), SWT.CHECK);
		chkbtnSavePassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		chkbtnSavePassword.setText(Messages.SAVE_PASSWORD);
		
		textPasswordListener = new EmptyTextListener(Messages.LABEL_PWD);
		txtPassword.addModifyListener(textPasswordListener);
		
		Label lblKeyFile = new Label(serverDetailsGroup.getHydroGroupClientArea(), SWT.NONE);
		lblKeyFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblKeyFile.setText(Messages.KEY_FILE);

		Composite keyFileComposite = new Composite(serverDetailsGroup.getHydroGroupClientArea(), SWT.NONE);
		keyFileComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		keyFileComposite.setLayout(new GridLayout(2, false));
		
		txtKeyFile = new Text(keyFileComposite, SWT.BORDER);
		txtKeyFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtKeyFile.setData(SELECTION_BUTTON_KEY, btnRemoteMode);
		
		browseButton = new Button(keyFileComposite, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		browseButton.setText("...");
		browseButton.addSelectionListener(browseListener);
		radioPassword.setSelection(true);
		
		keyFileListener = new EmptyTextListener(Messages.KEY_FILE);
		txtKeyFile.addModifyListener(keyFileListener);
		
		remotePathConfigGroup = new HydroGroup(remoteRunDetailsHolder, SWT.NONE);
		remotePathConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		remotePathConfigGroup.setHydroGroupText("Remote Path Configurations");
		GridLayout gridLayout_1 = new GridLayout(2, false);
		gridLayout_1.horizontalSpacing = 15;
		remotePathConfigGroup.getHydroGroupClientArea().setLayout(gridLayout_1);

		Label lblRunUtility = new Label(remotePathConfigGroup.getHydroGroupClientArea(), SWT.NONE);
		lblRunUtility.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRunUtility.setText(Messages.RUN_UTILITY);

		txtRunUtility = new Text(remotePathConfigGroup.getHydroGroupClientArea(), SWT.BORDER);
		txtRunUtility.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		EmptyTextListener runUtilityListener = new EmptyTextListener(Messages.RUN_UTILITY);
		txtRunUtility.addModifyListener(runUtilityListener);
		
		Label lblProjectPath = new Label(remotePathConfigGroup.getHydroGroupClientArea(), SWT.NONE);
		lblProjectPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProjectPath.setText(Messages.PROJECT_PATH);

		txtProjectPath = new Text(remotePathConfigGroup.getHydroGroupClientArea(), SWT.BORDER);
		txtProjectPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		EmptyTextListener projectPathListener = new EmptyTextListener(Messages.PROJECT_PATH);
		txtProjectPath.addModifyListener(projectPathListener);
		
		serverDetailsGroup.setVisible(false);
		remotePathConfigGroup.setVisible(false);

		Monitor primary = parent.getMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = parent.getShell().getBounds();
	    
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    parent.getShell().setLocation((bounds.width/2 - rect.width/4), y);
	    
		return container;
	}

	private void addFocusListener(Text txtBox) {
		txtBox.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				populatePasswordField();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// DO Nothing
			}
		});
	}
	
	private ISecurePreferences getHydrographSecureStorageRootNode(){
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		ISecurePreferences hydrographSecureStorageRootNode = preferences.node(Constants.SECURE_STORAGE_HYDROGRAPH_CREDENTIALS_ROOT_NODE);
		return hydrographSecureStorageRootNode;
	}
	
	private ISecurePreferences getHydrographSecureStorageRunDialogNode(ISecurePreferences hydrographSecureStorageRootNode){
		ISecurePreferences hydrographSecureStorageRunDialogNode = hydrographSecureStorageRootNode.node(SECURE_STORAGE_HYDROGRAPH_CREDENTIALS_RUNCONFIG_DIALOG_NODE);
		return hydrographSecureStorageRunDialogNode;
	}
	
	private ISecurePreferences getSecureStorageHostNode(String hostname,boolean createHostNode){
		ISecurePreferences hydrographSecureStorageRootNode = getHydrographSecureStorageRootNode();
		ISecurePreferences hydrographSecureStorageRunDialogNode=getHydrographSecureStorageRunDialogNode(hydrographSecureStorageRootNode);
		
		if(hydrographSecureStorageRunDialogNode.nodeExists(hostname) || createHostNode){
			ISecurePreferences hydrographSecureStorageRunDialogHostNode = hydrographSecureStorageRunDialogNode.node(hostname);
			return hydrographSecureStorageRunDialogHostNode;
		}else{
			return null;
		}
	}
	
	private void populatePasswordField() {
		ISecurePreferences hydrographSecureStorageRunDialogHostNode = getSecureStorageHostNode(txtEdgeNode.getText().toLowerCase(),false);
		try {
			if(hydrographSecureStorageRunDialogHostNode!=null){
				String password=hydrographSecureStorageRunDialogHostNode.get(txtUserName.getText(), "");
				txtPassword.setText(password);
				if(radioPassword.isEnabled() && !txtPassword.isEnabled()){
					textPasswordListener.getErrorDecoration().hide();
				}
				if(!StringUtils.isBlank(password)){
					chkbtnSavePassword.setSelection(true);
				}else{
					chkbtnSavePassword.setSelection(false);
				}
			}else{
				txtPassword.setText("");
			}
			
		} catch (StorageException storageException) {
			logger.debug("Unable to fetch password from eclipse secure storage " , storageException);;
		}
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		applyServerDetailsCrossTextEmptyValidationListener(txtEdgeNode);
		applyServerDetailsCrossTextEmptyValidationListener(txtPassword);
		applyServerDetailsCrossTextEmptyValidationListener(txtUserName);
		applyServerDetailsCrossTextEmptyValidationListener(txtBasePath);
		applyServerDetailsCrossTextEmptyValidationListener(txtKeyFile);
		applyServerDetailsCrossTextEmptyValidationListener(txtRunUtility);
		applyServerDetailsCrossTextEmptyValidationListener(txtProjectPath);
		loadBuildProperties();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(499, 483);
	}

	SelectionListener runModeSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			Button button = ((Button) event.widget);
			if (button.getText().equals(Messages.REMOTE_MODE)) {
				showRemoteRunDetailsHolderComposite();
			} else {
				hideRemoteRunDetailsHolderComposite();
			}
		}
	};

	SelectionListener browseListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		    fd.setText(Messages.OPEN_BUTTON_LABEL);
		    fd.setFilterPath(Messages.C_DRIVE_LOCATION);
		    String[] filterExt = {Messages.PPK_EXT,Messages.PEM_EXT};
		    fd.setFilterExtensions(filterExt);
		    txtKeyFile.setText(fd.open());
		}
	};
	
	SelectionListener radioPasswordSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			txtPassword.setEnabled(true);
			chkbtnSavePassword.setEnabled(true);
			txtKeyFile.setEnabled(false);
			browseButton.setEnabled(false);
			keyFileListener.getErrorDecoration().hide();
			if(StringUtils.isBlank(txtPassword.getText())){
				textPasswordListener.getErrorDecoration().show();
			}
			toggleOkButton();
		}
	};
	
	SelectionListener radioKeyFileSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			txtKeyFile.setEnabled(true);
			browseButton.setEnabled(true);
			txtPassword.setEnabled(false);
			chkbtnSavePassword.setEnabled(false);
			textPasswordListener.getErrorDecoration().hide();
			if(StringUtils.isBlank(txtKeyFile.getText())){
				keyFileListener.getErrorDecoration().show();
			}
			toggleOkButton();
		}
	};
    
	private void showRemoteRunDetailsHolderComposite() {
		Point shellSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		getShell().setSize(shellSize);
		txtEdgeNode.setText(txtEdgeNode.getText());
		txtUserName.setText(txtUserName.getText());	
		txtPassword.setText(txtPassword.getText());
		txtKeyFile.setText(txtKeyFile.getText());
		if(radioKeyFile.isEnabled() && txtKeyFile.isEnabled()){
			textPasswordListener.getErrorDecoration().hide();
		}else{
			keyFileListener.getErrorDecoration().hide();
		}
		serverDetailsGroup.setVisible(true);
		remotePathConfigGroup.setVisible(true);
	}

	private void hideRemoteRunDetailsHolderComposite() {
		Point remoteRunDetailsHolderSize = remoteRunDetailsHolder.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point shellSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point newShellSize = new Point(shellSize.x, shellSize.y - remoteRunDetailsHolderSize.y);
		getShell().setSize(newShellSize);
		txtEdgeNode.setText(txtEdgeNode.getText());
		txtUserName.setText(txtUserName.getText());
		txtPassword.setText(txtPassword.getText());
		txtKeyFile.setText(txtKeyFile.getText());
		serverDetailsGroup.setVisible(false);
		remotePathConfigGroup.setVisible(false);
	}

	SelectionListener viewDataSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			Button button = ((Button) event.widget);
			txtBasePath.setEnabled(button.getSelection());
			txtBasePath.setText(txtBasePath.getText());
		}
	};
	
	private void loadBuildProperties() {
		String buildPropFilePath = buildPropFilePath();
		IPath bldPropPath = new Path(buildPropFilePath);
		IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(bldPropPath);
		try {
			InputStream reader = iFile.getContents();
			buildProps.load(reader);

		} catch (CoreException | IOException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"Exception occurred while loading build properties from file -\n" + e.getMessage());
		}

		Enumeration<?> propertyNames = buildProps.propertyNames();
		populateTextBoxes(propertyNames);

	}

	private String buildPropFilePath() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IFileEditorInput input = (IFileEditorInput) page.getActiveEditor().getEditorInput();

		IFile file = input.getFile();
		IProject activeProject = file.getProject();
		String activeProjectName = activeProject.getName();
		return "/" + activeProjectName + "/build.properties";
	}

	private void populateTextBoxes(Enumeration propertyNames) {
		if (StringUtils.equals(buildProps.getProperty(REMOTE_MODE), TRUE)) {
			btnRemoteMode.setSelection(true);
			btnLocalMode.setSelection(false);
			showRemoteRunDetailsHolderComposite();
		} else {
			btnLocalMode.setSelection(true);
			btnRemoteMode.setSelection(false);
			hideRemoteRunDetailsHolderComposite();
		}
		txtEdgeNode.setText(getBuildProperty(HOST));
		txtUserName.setText(getBuildProperty(USER_NAME));
		txtKeyFile.setText(getBuildProperty(KEY_FILE));
		txtRunUtility.setText(getBuildProperty(RUN_UTILITY));
		txtProjectPath.setText(getBuildProperty(REMOTE_DIRECTORY));
		txtBasePath.setText(getBuildProperty(BASE_PATH));

		if (StringUtils.equals(buildProps.getProperty(VIEW_DATA_CHECK), TRUE)) {
			viewDataCheckBox.setSelection(true);
			txtBasePath.setEnabled(true);
		}
		if(StringUtils.equals(buildProps.getProperty(USE_PASSWORD_AUTHENTICATION), TRUE) ){
			togglePasswordAndKeyFile(true);
			keyFileListener.getErrorDecoration().hide();
			if(StringUtils.isBlank(txtPassword.getText())){
				textPasswordListener.getErrorDecoration().show();
			}
		}
		else{
			togglePasswordAndKeyFile(false);
			
			textPasswordListener.getErrorDecoration().hide();
			if(StringUtils.isBlank(txtKeyFile.getText())){
				keyFileListener.getErrorDecoration().show();
			}
		}
		populatePasswordField();
	}
	
	private void togglePasswordAndKeyFile(Boolean state){
		radioPassword.setSelection(state);
		txtPassword.setEnabled(state);
		chkbtnSavePassword.setEnabled(state);
		radioKeyFile.setSelection(!state);
		txtKeyFile.setEnabled(!state);
		browseButton.setEnabled(!state);
		
	}
	
	private String getBuildProperty(String key) {
		if (buildProps.getProperty(VIEW_DATA_CHECK) == null) {
			return "";
		} else {
			String value = buildProps.getProperty(key);
			return StringUtils.isNotBlank(value) ? value : "";
		}
	}

	/**
	 * 
	 * Returns cluster password
	 * 
	 * @return cluster password
	 */
	public String getClusterPassword() {
		return this.password;
	}

	/**
	 * 
	 * Returns user id
	 * 
	 * @return user id
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * 
	 * Returns Host name / IP
	 * 
	 * @return Host name / IP
	 */
	public String getEdgeNodeIp() {
		return this.edgeNodeText;
	}

	/**
	 * 
	 * Returns base path on remote server. The base path is directory path on
	 * remote server which contains temporary debug files
	 * 
	 * @return
	 */
	public String getBasePath() {
		return this.basePath;
	}

	/**
	 * 
	 * Get host name
	 * 
	 * @return
	 */
	public String getHost() {
		if (remoteMode) {
			return this.host;
		} else {
			return LOCAL_HOST;
		}

	}

	/**
	 * 
	 * Return user name
	 * 
	 * @return
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * 
	 * Returns true if job is running on remote mode
	 * 
	 * @return
	 */
	public boolean isRemoteMode() {
		return remoteMode;
	}

	/**
	 * 
	 * Returns true if job is running in debug mode
	 * 
	 * @return
	 */
	public boolean isDebug() {
		return isDebug;
	}

	public String getKeyFile() {
		return KeyFile;
	}
	
	public boolean getIsUsePassword(){
		return usePassword;
	}
	
	@Override
	protected void okPressed() {
		saveRunConfigurations();
		
		if(validateCredentials() && runGraph){
			super.okPressed();
		}
		
	}

	private boolean validateCredentials() {
		if (remoteMode) {
			return validateHostUsernameAndPassword();
		}else{
			return true;
		}	
	}

	private boolean validateHostUsernameAndPassword() {
		if (isUsernamePasswordOrHostEmpty()) {
			return false;
		} else {
			if(radioPassword.getSelection()){
				return connectAndValidateUserNamePasswordAndHost();
			}
			else{
				return true;
			}
		}
	}

	private boolean connectAndValidateUserNamePasswordAndHost() {
		Message message = SCPUtility.INSTANCE.validateCredentials(host, username, password);
		if (message.getMessageType() != MessageType.SUCCESS) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.CREDENTIAL_VALIDATION_MESSAGEBOX_TITLE,
					message.getMessage());
			return false;
		}else{
			return true;
		}
	}

	private boolean isUsernamePasswordOrHostEmpty() {
		Notification notification = new Notification();
		if (remoteMode) {
			if (StringUtils.isEmpty(txtEdgeNode.getText())){
				notification.addError(Messages.EMPTY_HOST_FIELD_MESSAGE);
			}
				
			if (StringUtils.isEmpty(txtUserName.getText())){
				notification.addError(Messages.EMPTY_USERNAME_FIELD_MESSAGE);
			}
			if(radioPassword.getSelection() && StringUtils.isEmpty(txtPassword.getText())){
				notification.addError(Messages.EMPTY_PASSWORD_FIELD_MESSAGE);
			}
		}
		
		if(notification.hasErrors()){
			MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.EMPTY_FIELDS_MESSAGE_BOX_TITLE,
					notification.errorMessage());	
			return true;
		}else{
			return false;
		}
	}

	private void saveRunConfigurations() {
		remoteMode = btnRemoteMode.getSelection();
		IFile iFile;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {

			buildProps.put(LOCAL_MODE, String.valueOf(btnLocalMode.getSelection()));
			buildProps.put(REMOTE_MODE, String.valueOf(btnRemoteMode.getSelection()));
			buildProps.put(HOST, txtEdgeNode.getText());
			buildProps.put(USER_NAME, txtUserName.getText());
			buildProps.put(KEY_FILE, txtKeyFile.getText());
			buildProps.put(RUN_UTILITY, txtRunUtility.getText());
			buildProps.put(REMOTE_DIRECTORY, txtProjectPath.getText());
			buildProps.put(BASE_PATH, txtBasePath.getText());
			buildProps.put(VIEW_DATA_CHECK, String.valueOf(viewDataCheckBox.getSelection()));
			buildProps.put(USE_PASSWORD_AUTHENTICATION, String.valueOf(radioPassword.getSelection()));
			buildProps.store(out, null);

			String buildPropFilePath = buildPropFilePath();

			IPath bldPropPath = new Path(buildPropFilePath);
			iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(bldPropPath);
			iFile.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, null);

		} catch (IOException | CoreException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"Exception occurred while saving run configuration file -\n" + e.getMessage());
		}
		this.userId = txtUserName.getText();
		this.password = txtPassword.getText();
		this.KeyFile = txtKeyFile.getText();
		this.username = txtUserName.getText();
		this.host = txtEdgeNode.getText();
		this.basePath = txtBasePath.getText();
		this.isDebug = viewDataCheckBox.getSelection();
		this.usePassword = radioPassword.getSelection();
		 savePassword();
		try {
			checkBuildProperties(btnRemoteMode.getSelection());
			this.runGraph = true;
		} catch (IllegalArgumentException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
			this.runGraph = false;
		}

		setPreferences();
	}

	private void savePassword() {

		if (StringUtils.isNotBlank(password) && chkbtnSavePassword.getSelection()
				&& StringUtils.isNotBlank(txtEdgeNode.getText()) && StringUtils.isNotBlank(txtUserName.getText())) {

			savePasswordToSecureStorage(password);
		}else{
			savePasswordToSecureStorage("");
		}
	}

	private void savePasswordToSecureStorage(String password) {
		ISecurePreferences hydrographSecureStorageRunDialogHostNode = getSecureStorageHostNode(
				txtEdgeNode.getText().toLowerCase(), true);

		try {
			hydrographSecureStorageRunDialogHostNode.put(txtUserName.getText(), password, true);
		} catch (StorageException storageException) {
			logger.debug("Failed to save the password in secure storage", storageException);
		}
	}

	private void setPreferences() {
		if (StringUtils.isBlank(PlatformUI.getPreferenceStore().getString(Constants.HOST))){
			PlatformUI.getPreferenceStore().setValue(Constants.HOST, this.host);
		}
	}

	@Override
	protected void cancelPressed() {
		runGraph = false;
		super.cancelPressed();
	}

	private void checkBuildProperties(boolean remote) {
		Notification notification = validate(remote);
		if (notification.hasErrors()) {
			throw new IllegalArgumentException(notification.errorMessage());
		}
	}

	private Notification validate(boolean remote) {
		Notification note = new Notification();
		if (isDebug && StringUtils.isEmpty(txtBasePath.getText())){
			note.addError(Messages.EMPTY_BASE_PATH_FIELD_MESSAGE);
		}

		IPath path = new Path(txtBasePath.getText());
		if (isDebug && !path.isAbsolute()) {
			note.addError(Messages.BASE_PATH_FIELD_VALIDATION_MESSAGE);
		}

		return note;
	}

	public boolean proceedToRunGraph() {
		return runGraph;
	}
	
	private void applyServerDetailsCrossTextEmptyValidationListener(Text text) {
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				toggleOkButton();
			}
		});
	}
	
	private void toggleOkButton() {
		if (okButton == null) {
			return;
		}

		if (btnRemoteMode.getSelection()) {
			if(radioPassword.getSelection()){
				if ( checkAllForBlank() || StringUtils.isEmpty(txtPassword.getText())) {
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}	
			}
			else {
				if (checkAllForBlank() || StringUtils.isEmpty(txtKeyFile.getText())) {
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}	
			}
		} else {
			okButton.setEnabled(true);
		}
		
		if(viewDataCheckBox.getSelection()){
			if (StringUtils.isEmpty(txtBasePath.getText())) {
				okButton.setEnabled(false);
			} 
		}
	}

	private boolean checkAllForBlank() {
		return StringUtils.isEmpty(txtEdgeNode.getText()) || StringUtils.isEmpty(txtUserName.getText()) ||
		StringUtils.isEmpty(txtRunUtility.getText()) || StringUtils.isEmpty(txtProjectPath.getText());
	}
}
