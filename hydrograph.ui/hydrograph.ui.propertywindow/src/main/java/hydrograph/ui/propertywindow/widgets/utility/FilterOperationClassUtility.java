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

 
package hydrograph.ui.propertywindow.widgets.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.ui.actions.OpenNewClassWizardAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;

import hydrograph.ui.common.component.config.Operations;
import hydrograph.ui.common.component.config.TypeInfo;
import hydrograph.ui.common.datastructures.tooltip.TootlTipErrorMessage;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.OperationClassConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.dialogs.ExternalSchemaFileSelectionDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.interfaces.IOperationClassDialog;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;


/**
 * The Class FilterOperationClassUtility use to create operation class widget,new class wizard and selection dialog window. 
 * 
 * @author Bitwise
 */
public class FilterOperationClassUtility  {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(FilterOperationClassUtility.class);
	public static final FilterOperationClassUtility INSTANCE = new FilterOperationClassUtility();

	private static IJavaProject iJavaProject;
	private Button createBtn;
	private Button browseBtn;
	private Button openBtn;
	private Button btnCheckButton;
	private String componentName;
	private String fileNameTextBoxValue;
	private ELTDefaultButton emptyButton;
	private AbstractELTWidget browseButton;
	private FilterOperationClassUtility(){
		
	}
	
	/**
	 * Creates the new class wizard.
	 * 
	 * @param fileNameTextBox
	 *            the file name
	 * @param widgetConfig 
	 */
	public void createNewClassWizard(Text fileNameTextBox, WidgetConfig widgetConfig) {
		OpenNewClassWizardAction wizard = new OpenNewClassWizardAction();
		wizard.setOpenEditorOnFinish(false);
		final CustomizeNewClassWizardPage page = new CustomizeNewClassWizardPage();
		page.setSuperClass("java.lang.Object", true);
		page.setMethodStubSelection(false, false, true, true);
		List<String> interfaceList = new ArrayList<String>();
		OperationClassConfig operationClassConfig = (OperationClassConfig) widgetConfig;
		Operations operations = XMLConfigUtil.INSTANCE.getComponent(getComponentName()).getOperations();
		TypeInfo typeInfo=operations.getInterface();
		if (operationClassConfig!=null && operationClassConfig.getComponentName().equalsIgnoreCase(typeInfo.getName()))
		{
			interfaceList.add(typeInfo.getClazz());
		}
		page.setSuperInterfaces(interfaceList, true);  
		wizard.setConfiguredWizardPage(page);
		if(OSValidator.isMac()){
			Display.getDefault().timerExec(0, new Runnable() {

				@Override
				public void run() {
					page.getControl().forceFocus();					
				}
			});
		}
		wizard.run();
		if (page.isPageComplete()) {
			if(!page.getPackageText().equalsIgnoreCase("")){
				fileNameTextBox.setText(page.getPackageText()+"."
						+ page.getTypeName());
			}else{
				fileNameTextBox.setText(page.getTypeName());
			}
		}
		fileNameTextBox.setData("path", "/" + page.getPackageFragmentRootText() + "/"
				+ page.getPackageText().replace(".", "/") + "/"
				+ page.getTypeName() + ".java");
	}

	/**
	 * Open browse file dialog according extensions.
	 * 
	 * @param filterExtension
	 *            the filter extension
	 * @param fileName
	 *            the file name
	 */
	public void browseFile(String filterExtension, Text fileName) {
		
		if(Extensions.JAVA.toString().equalsIgnoreCase(filterExtension))
		{
			browseJavaSelectionDialog(filterExtension, fileName);
		}
		if(Extensions.JOB.toString().equalsIgnoreCase(filterExtension))
		{
			browseJobSelectionDialog(filterExtension, fileName);
		}
		if(Extensions.SCHEMA.toString().equalsIgnoreCase(filterExtension))
		{
			browseSchemaSelectionDialog(filterExtension, fileName);
		}
		if(Extensions.XML.toString().equalsIgnoreCase(filterExtension))
		{
			browseXMLSelectionDialog(filterExtension, fileName);
		}
		
	}
	/**
	 * 
	 * Open selection dialog for schema files, File selection restricted to ".schema" extension. 
	 * @param filterExtension
	 * @param fileName
	 */
	private void browseSchemaSelectionDialog(String filterExtension, Text fileName) {
		String externalSchemaTextBoxValue = "";
		ExternalSchemaFileSelectionDialog dialog = new ExternalSchemaFileSelectionDialog("Project",
				"Select Schema File (.schema or.xml)",  new String[]{filterExtension,Extensions.XML.toString()}, this);
		if (dialog.open() == IDialogConstants.OK_ID) {
			String file = fileNameTextBoxValue;
			IResource resource = (IResource) dialog.getFirstResult();
			String path[] = resource.getFullPath().toString().split("/");
			if (file.isEmpty()) {
				for (int i = 1; i < path.length; i++) {
					externalSchemaTextBoxValue = externalSchemaTextBoxValue + path[i] + "/";
				}
			} else {
				for (int i = 1; i < path.length; i++) {
					if (!path[i].endsWith(".schema") && !path[i].endsWith(".xml")) {
						externalSchemaTextBoxValue = externalSchemaTextBoxValue + path[i] + "/";
					}
				}
				externalSchemaTextBoxValue = externalSchemaTextBoxValue + file;
			}
			fileName.setText(externalSchemaTextBoxValue);
		}
	}
	
	/**
	 * @param filterExtension
	 * @param fileName
	 * Open the dialog to browse .xml file for expression, operation or outputfields
	 */
	private void browseXMLSelectionDialog(String filterExtension, Text fileName) {
		String externalSchemaTextBoxValue = "";
		ExternalSchemaFileSelectionDialog dialog = new ExternalSchemaFileSelectionDialog("Project",
				"Select Input File (.xml)",  new String[]{filterExtension,Extensions.XML.toString()}, this);
		if (dialog.open() == IDialogConstants.OK_ID) {
			String file = fileNameTextBoxValue;
			IResource resource = (IResource) dialog.getFirstResult();
			String path[] = resource.getFullPath().toString().split("/");
			if (file.isEmpty()) {
				for (int i = 1; i < path.length; i++) {
					externalSchemaTextBoxValue = externalSchemaTextBoxValue + path[i] + "/";
				}
			} else {
				for (int i = 1; i < path.length; i++) {
					if (!path[i].endsWith(".xml")) {
						externalSchemaTextBoxValue = externalSchemaTextBoxValue + path[i] + "/";
					}
				}
				externalSchemaTextBoxValue = externalSchemaTextBoxValue + file;
			}
			fileName.setText(externalSchemaTextBoxValue);
		}
	}
	/**
	 * Open selection dialog for Java files, File selection restricted to ".java" extension.
	 * @param filterExtension
	 * @param fileName
	 */
	public static void browseJavaSelectionDialog(String filterExtension, Text fileName) {
		ResourceFileSelectionDialog dialog = new ResourceFileSelectionDialog(
				"Project", "Select Java Class (.java)", new String[] { filterExtension });
		if (dialog.open() == IDialogConstants.OK_ID) {
			IResource resource = (IResource) dialog.getFirstResult();
			String filePath = resource.getRawLocation().toOSString();
			java.nio.file.Path path =Paths.get(filePath); 
			String classFile=path.getFileName().toString();
			String name = "";
			try(BufferedReader reader= new BufferedReader(new FileReader(filePath))){ 
				String firstLine= reader.readLine();
				if(firstLine.contains(Constants.PACKAGE)){
					name= firstLine.replaceFirst(Constants.PACKAGE, "").replace(";", "");
					if(!name.equalsIgnoreCase("")){
						name=name+"."+classFile.substring(0, classFile.lastIndexOf('.'));
					}
					
				}else{
					name=classFile.substring(0, classFile.lastIndexOf('.'));
				}
				
			} catch (IOException e) { 
				logger.error("Unable to read file " + filePath,e );
			}
			fileName.setText(name.trim());
			filePath = resource.getRawLocation().toOSString();
			fileName.setData("path", resource.getFullPath().toString());
}
	} 
	/**
	 * Open selection dialog for job files, File selection restricted to ".job" extension.
	 * @param filterExtension
	 * @param fileName
	 */
	private void browseJobSelectionDialog(String filterExtension, Text fileName) {
		ResourceFileSelectionDialog dialog = new ResourceFileSelectionDialog(
				"Project", "Select Sub Job (.job)", new String[] { filterExtension });
		if (dialog.open() == IDialogConstants.OK_ID) {
			IResource resource = (IResource) dialog.getFirstResult();
			String filePath = resource.getFullPath().toString();
			if(isFileExistsOnLocalFileSystem(new Path(filePath), fileName)){
				fileName.setText(filePath.substring(1));
			}
		}
	}
	
	/**
	 * Check if file exist on local file system. 
	 * @param jobFilePath
	 * @param textBox
	 * @return
	 */
	private boolean isFileExistsOnLocalFileSystem(IPath jobFilePath, Text textBox) {
		jobFilePath=jobFilePath.removeFileExtension().addFileExtension(Constants.XML_EXTENSION_FOR_IPATH);
		try {
			if (ResourcesPlugin.getWorkspace().getRoot().getFile(jobFilePath).exists()){
				return true;
			}
			else if (jobFilePath.toFile().exists()){
				return true;
			}
		} catch (Exception exception) {
			logger.error("Error occured while cheking file on local file system", exception);
		}
		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.YES
				| SWT.NO);
		messageBox.setMessage(jobFilePath.lastSegment()+Messages.FILE_DOES_NOT_EXISTS);
		messageBox.setText(jobFilePath.toString() +Messages.NOT_EXISTS);
		int response = messageBox.open();
		if (response == SWT.YES) {
			jobFilePath=jobFilePath.removeFileExtension().addFileExtension(Constants.JOB_EXTENSION_FOR_IPATH);
			textBox.setText(jobFilePath.toString().substring(1));
		}
		else{
			textBox.setText("");
		}
		return false;
	}


	
	
	/**
	 * Open file editor to view java file.
	 * 
	 * @param fileName
	 *            the file name
	 * @return true, if successful
	 */
	@SuppressWarnings("restriction")
	public boolean openFileEditor(Text filePath,String pathFile) {
		String fullQualifiedClassName = filePath.getText();
		try {
			logger.debug("Searching "+fullQualifiedClassName+" in project's source folder");
			PackageFragment packageFragment = null;
			String[] packages = StringUtils.split(fullQualifiedClassName, ".");
			String className = fullQualifiedClassName;
			String packageStructure="";
			IFile javaFile=null;
			if (packages.length > 1) {
				className = packages[packages.length - 1];
				packageStructure= StringUtils.replace(fullQualifiedClassName, "." + className, "");
			}
			IFileEditorInput editorInput = (IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActiveEditor().getEditorInput();
			IProject project = editorInput.getFile().getProject();
			IJavaProject iJavaProject = JavaCore.create(project);
			IPackageFragmentRoot packageFragmentRoot = iJavaProject
					.getPackageFragmentRoot(project.getFolder(Constants.ProjectSupport_SRC));
			if(packageFragmentRoot!= null){
			for (IJavaElement iJavaElement : packageFragmentRoot.getChildren()) {
				if (iJavaElement instanceof PackageFragment
						&& StringUtils.equals(iJavaElement.getElementName(), packageStructure)) {
					packageFragment = (PackageFragment) iJavaElement;
					break;
				}
			}
			}
			if (packageFragment != null) {
				for (IJavaElement element : packageFragment.getChildren()) {
					if (element instanceof CompilationUnit
							&& StringUtils.equalsIgnoreCase(element.getElementName(), className + Constants.JAVA_EXTENSION)) {
						javaFile = (IFile) element.getCorrespondingResource();
						break;
					}
				}
			}
			if (javaFile !=null && javaFile.exists()) {
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(javaFile.getLocationURI());
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IDE.openEditorOnFileStore(page, fileStore);
				return true;
			}
		} catch (Exception e) {
			logger.error("Fail to open file");
			return false;
		}
		return false;

	}
	/**
	 * 
	 * Operation class widget creation 
	 * @param composite
	 * @param eltOperationClassDialogButtonBar
	 * @param combo
	 * @param isParameterCheckbox
	 * @param fileNameTextBox
	 * @param tootlTipErrorMessage
	 * @param widgetConfig
	 * @param eltOperationClassDialog
	 * @param propertyDialogButtonBar
	 * @param opeartionClassDialogButtonBar
	 */
	public void createOperationalClass(
			Composite composite,
			PropertyDialogButtonBar eltOperationClassDialogButtonBar,
			AbstractELTWidget combo, AbstractELTWidget isParameterCheckbox,
			AbstractELTWidget fileNameTextBox,
			TootlTipErrorMessage tootlTipErrorMessage,
			WidgetConfig widgetConfig,
			IOperationClassDialog eltOperationClassDialog,
			PropertyDialogButtonBar propertyDialogButtonBar, PropertyDialogButtonBar opeartionClassDialogButtonBar) {
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(composite);
		eltSuDefaultSubgroupComposite.createContainerWidget();
		eltSuDefaultSubgroupComposite.numberOfBasicWidgets(5);

		AbstractELTWidget eltDefaultLable = new ELTDefaultLable(Messages.OPERATION_CLASS);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);

		eltSuDefaultSubgroupComposite.attachWidget(combo);
		Combo comboOfOperaationClasses = (Combo) combo.getSWTWidgetControl();

		eltSuDefaultSubgroupComposite.attachWidget(fileNameTextBox);
		Text fileName = (Text) fileNameTextBox.getSWTWidgetControl();
		fileName.setSize(10, 100);
        addModifyListenerToFileNameTextBox(fileName);
		GridData layoutData = (GridData)fileName.getLayoutData();
		layoutData.horizontalIndent=6;
		
		if(OSValidator.isMac()){
			browseButton = new ELTDefaultButton(Messages.BROWSE_BUTTON_TEXT).buttonWidth(35);
		}else{
			browseButton = new ELTDefaultButton(Messages.BROWSE_BUTTON_TEXT).buttonWidth(25);
		}
		
		eltSuDefaultSubgroupComposite.attachWidget(browseButton);
		browseBtn=(Button)browseButton.getSWTWidgetControl();


		eltSuDefaultSubgroupComposite.attachWidget(isParameterCheckbox);


		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite2 = new ELTDefaultSubgroupComposite(composite);
		eltSuDefaultSubgroupComposite2.createContainerWidget();
		eltSuDefaultSubgroupComposite2.numberOfBasicWidgets(3);

		if(OSValidator.isMac()){
			 emptyButton = new ELTDefaultButton("").buttonWidth(92);
		}
		else{
			emptyButton = new ELTDefaultButton("").buttonWidth(89);
		}
		eltSuDefaultSubgroupComposite2.attachWidget(emptyButton);
		emptyButton.visible(false);

		// Create new button, that use to create operational class
		AbstractELTWidget createButton = new ELTDefaultButton(Messages.CREATE_NEW_OPEARTION_CLASS_LABEL);
		eltSuDefaultSubgroupComposite2.attachWidget(createButton); 
		createBtn=(Button)createButton.getSWTWidgetControl();

		// Edit new button, that use to edit operational class
		AbstractELTWidget openButton = new ELTDefaultButton(Messages.OPEN_BUTTON_LABEL);
		eltSuDefaultSubgroupComposite2.attachWidget(openButton); 
		openBtn=(Button)openButton.getSWTWidgetControl();

		btnCheckButton=(Button) isParameterCheckbox.getSWTWidgetControl();

		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.TOOLTIP_ERROR_MESSAGE, tootlTipErrorMessage);
		helper.put(HelperType.WIDGET_CONFIG, widgetConfig);
		helper.put(HelperType.OPERATION_CLASS_DIALOG_OK_CONTROL, eltOperationClassDialog);
		helper.put(HelperType.OPERATION_CLASS_DIALOG_APPLY_BUTTON, opeartionClassDialogButtonBar);
		helper.put(HelperType.PROPERTY_DIALOG_BUTTON_BAR, propertyDialogButtonBar);
		helper.put(HelperType.FILE_EXTENSION,"java");
		setIJavaProject();
		try { 						
			openButton.attachListener(ListenerFactory.Listners.OPEN_FILE_EDITOR.getListener(),eltOperationClassDialogButtonBar, helper,comboOfOperaationClasses,fileName);
			browseButton.attachListener(ListenerFactory.Listners.BROWSE_FILE_LISTNER.getListener(),eltOperationClassDialogButtonBar, helper,fileName);
			createButton.attachListener(ListenerFactory.Listners.CREATE_NEW_CLASS.getListener(),eltOperationClassDialogButtonBar, helper,comboOfOperaationClasses,fileName);
			combo.attachListener(ListenerFactory.Listners.COMBO_CHANGE.getListener(),eltOperationClassDialogButtonBar, helper,comboOfOperaationClasses,fileName,btnCheckButton);
			isParameterCheckbox.attachListener(ListenerFactory.Listners.ENABLE_BUTTON.getListener(),eltOperationClassDialogButtonBar, null,btnCheckButton,browseButton.getSWTWidgetControl(),createButton.getSWTWidgetControl(),openButton.getSWTWidgetControl());
		} catch (Exception e) {
			logger.error("Fail to attach listener "+e); 
		} 
	}

	private void addModifyListenerToFileNameTextBox(Text fileName) {
		fileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Text classNameTextBox=(Text)e.widget;
				openBtn.setEnabled(StringUtils.isNotBlank(classNameTextBox.getText())&&!btnCheckButton.getSelection());
			}
		});
	}

	private static void setIJavaProject() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if ((page.getActiveEditor().getEditorInput().getClass()).isAssignableFrom(FileEditorInput.class)) {
			IFileEditorInput input = (IFileEditorInput) page.getActiveEditor().getEditorInput();
			IFile file = input.getFile();
			IProject activeProject = file.getProject();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProject.getName());
			iJavaProject = JavaCore.create(project);
		}
	}

	public static IJavaProject getIJavaProject() {
		return iJavaProject;
	}

	/**
	 * Enable and disable new class creation button and open editor button base on isParam checkbox.
	 * @param value
	 * @param checkboxValue
	 */
	public void enableAndDisableButtons(boolean value,boolean checkboxValue) {
		if (checkboxValue==false) {
			createBtn.setEnabled(value);
			browseBtn.setEnabled(value);
			btnCheckButton.setEnabled(!value);
		}
		if (checkboxValue==true) {
			btnCheckButton.setEnabled(value);
			openBtn.setEnabled(!value);
			createBtn.setEnabled(!value);
			browseBtn.setEnabled(!value);
		} 
	}
	
	public  void setComponentName(String name) {
		componentName = name;
	}

	public  String getComponentName() {
		return componentName;
	}

	public boolean isCheckBoxSelected() {
		return btnCheckButton.getSelection();
	}
	
	/**
	 * Set Operation 
	 * @param operationName
	 * @param textBox
	 */
	public void setOperationClassNameInTextBox(String operationName, Text textBox) {
		String operationClassName = null;
		Operations operations = XMLConfigUtil.INSTANCE.getComponent(getComponentName())
				.getOperations();
		List<TypeInfo> typeInfos = operations.getStdOperation();
		for (int i = 0; i < typeInfos.size(); i++) {
			if (typeInfos.get(i).getName().equalsIgnoreCase(operationName)) {
				operationClassName = typeInfos.get(i).getClazz();
				break;
			}
		}
		textBox.setText(operationClassName);;
	}

	public String getFileNameTextBoxValue() {
		return fileNameTextBoxValue;
	}

	public void setFileNameTextBoxValue(String fileNameTextBoxValue) {
		this.fileNameTextBoxValue = fileNameTextBoxValue;
	}

	/**
	 * @return the openBtn
	 */
	public Button getOpenBtn() {
		return openBtn;
	}
	
}
