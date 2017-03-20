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

package hydrograph.ui.expression.editor.composites;

import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.PathConstant;
import hydrograph.ui.expression.editor.jar.util.BuildExpressionEditorDataSturcture;
import hydrograph.ui.expression.editor.message.CustomMessageBox;
import hydrograph.ui.expression.editor.pages.AddExternalJarPage;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;

public class CategoriesDialogSourceComposite extends Composite {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(CategoriesDialogSourceComposite.class);
	private List sourcePackageList;
	private Combo comboJarList;
	private List targetList;
	private CategoriesDialogTargetComposite targetComposite;
	private AddExternalJarPage addCategoreisDialog;
	protected String[] filters = new String[] { "*.jar" };
	private Button deleteButton;
	private Label addAllSelectedPackagesLabel;
	private Label addSelectedPackagesLabel;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param addCategoreisDialog
	 * @param style
	 */
	public CategoriesDialogSourceComposite(Composite parent, AddExternalJarPage addCategoreisDialog, int style) {
		super(parent, style);
		this.addCategoreisDialog = addCategoreisDialog;
		setLayout(new GridLayout(1, false));

		Composite headerComposite = new Composite(this, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 0, 0);
		gd_composite.heightHint = 34;
		headerComposite.setLayoutData(gd_composite);
		headerComposite.setLayout(new GridLayout(3, false));

		comboJarList = new Combo(headerComposite, SWT.READ_ONLY);
		comboJarList.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 0, 1));
		loadComboJarListFromBuildPath(comboJarList,null);
		addListnersToCombo();
		
		createBrowseButton(headerComposite);
		createDelButton(headerComposite);

		sourcePackageList = new List(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		GridData gd_packageList = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_packageList.heightHint = 254;
		sourcePackageList.setLayoutData(gd_packageList);
		addDragSupport(sourcePackageList, comboJarList);
		addListenerToSourcePackageList(sourcePackageList);
	}

	private void addListenerToSourcePackageList(final List sourcePackageList) {
		sourcePackageList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(sourcePackageList.getSelectionCount()>0){
					addSelectedPackagesLabel.setEnabled(true);
				}else{
					addSelectedPackagesLabel.setEnabled(false);
				}
			}
		});
	}

	private void createBrowseButton(Composite headerComposite) {
		Button browseButton = new Button(headerComposite, SWT.NONE);
		browseButton.setText("Browse");
		browseButton.setToolTipText(Messages.EXTERNAL_JAR_DIALOG_BROWSE_BUTTON_TOOLTIP);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IFolder libsFolder = BuildExpressionEditorDataSturcture.INSTANCE.getCurrentProject().getFolder(
						PathConstant.PROJECT_LIB_FOLDER);
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OK);
				dialog.setFilterExtensions(filters);
				String path = dialog.open();
				if (path != null) {
					File file = new File(path);
					if (file.isFile()) {
						IFile newFile = libsFolder.getFile(file.getName());
						if (!newFile.exists()) {
							try(InputStream fis = new FileInputStream(file.getAbsoluteFile())) {
								newFile.create(fis, true, null);
								addFileToBuildPath(newFile);
								loadComboJarListFromBuildPath(comboJarList,newFile.getName());
								
							} catch (CoreException |IOException e1) {
								LOGGER.error(
										"Exception occurred while copying jar file from local-file-system to project",
										e1);
								new CustomMessageBox(SWT.ERROR, Messages.JAR_FILE_COPY_ERROR, Messages.ERROR_TITLE)
										.open();
							}
						} else {
							new CustomMessageBox(SWT.ERROR, Messages.DUPLICATE_JAR_FILE_COPY_ERROR,
									Messages.ERROR_TITLE).open();
						}
					}
				}
			}

			private void addFileToBuildPath(IFile jarFile) throws CoreException {
				LOGGER.error("Adding jar file " + jarFile.getName() + " to build Path");
				IJavaProject javaProject = JavaCore.create(BuildExpressionEditorDataSturcture.INSTANCE
						.getCurrentProject());
				IClasspathEntry[] oldClasspathEntry = javaProject.getRawClasspath();
				IClasspathEntry[] newClasspathEntry = new IClasspathEntry[oldClasspathEntry.length + 1];
				for (int index = 0; index < oldClasspathEntry.length; index++) {
					if (oldClasspathEntry[index].getPath().equals(jarFile.getFullPath()))
						return;
					newClasspathEntry[index] = javaProject.getRawClasspath()[index];
				}
				newClasspathEntry[oldClasspathEntry.length] = JavaCore.newLibraryEntry(jarFile.getFullPath(), null,
						null);
				javaProject.setRawClasspath(newClasspathEntry, new NullProgressMonitor());
				javaProject.close();
			}
		});
	}

	private void createDelButton(Composite headerComposite) {
		deleteButton = new Button(headerComposite, SWT.NONE);
		deleteButton.setBounds(0, 0, 75, 25);
		deleteButton.setToolTipText(Messages.EXTERNAL_JAR_DIALOG_DELETE_BUTTON_TOOLTIP);
		try {
			deleteButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		} catch (Exception exception) {
			LOGGER.error("Exception occurred while attaching image to button", exception);
			deleteButton.setText("Delete");
		}

		deleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comboJarList.getSelectionIndex() > -1) {
					String jarName = comboJarList.getItem(comboJarList.getSelectionIndex());
					if (userIsSure(jarName)) {
						try {
							removeJarFromBuildPath(jarName);
							comboJarList.remove(jarName);
							sourcePackageList.removeAll();
							refresh(jarName);
							enableOrDisableAddLabelsOnComboSelection();
						} catch (CoreException e1) {
							LOGGER.error("Exception occurred while removing jar file" + jarName + "from build Path");
						}
					}
				}
			}

			private boolean userIsSure(String jarName) {
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION
						| SWT.YES | SWT.NO);
				messageBox.setMessage("Do you really want to remove " + jarName + " file?\nCannot be undone.");
				messageBox.setText("Remove Resource");
				int response = messageBox.open();
				if (response == SWT.YES)
					return true;
				return false;
			}

			private void refresh(String jarName) {
				boolean isAnyItemRemovedFromTargetList = false;
				String[] items = targetComposite.getTargetList().getItems();
				targetComposite.getTargetList().removeAll();
				for (String item : items) {
					String jarFileName = StringUtils.trim(StringUtils.substringAfter(item, Constants.DASH));
					if (!StringUtils.equalsIgnoreCase(jarFileName, jarName)) {
						targetComposite.getTargetList().add(item);
					} else
						isAnyItemRemovedFromTargetList = true;
				}
				if (isAnyItemRemovedFromTargetList) {
					addCategoreisDialog.createPropertyFileForSavingData();
				}
			}

			private void removeJarFromBuildPath(String jarName) throws CoreException {
				LOGGER.debug("Removing jar file" + jarName + "from build Path");
				IJavaProject javaProject = JavaCore.create(BuildExpressionEditorDataSturcture.INSTANCE
						.getCurrentProject());
				IFile jarFile = javaProject.getProject().getFolder(PathConstant.PROJECT_LIB_FOLDER).getFile(jarName);
				IClasspathEntry[] oldClasspathEntry = javaProject.getRawClasspath();
				IClasspathEntry[] newClasspathEntry = new IClasspathEntry[oldClasspathEntry.length - 1];
				if (jarFile.exists()) {
					int index = 0;
					for (IClasspathEntry classpathEntry : oldClasspathEntry) {
						if (classpathEntry.getPath().equals(jarFile.getFullPath())) {
							continue;
						}
						newClasspathEntry[index] = classpathEntry;
						index++;
					}
					javaProject.setRawClasspath(newClasspathEntry, new NullProgressMonitor());
					jarFile.delete(true, new NullProgressMonitor());
				}
				javaProject.close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {/* Do-Nothing */
			}
		});
	}

	private void addDragSupport(final List sourcePackageList, final Combo comboJarList) {
		DragSource dragSource = ExpressionEditorUtil.INSTANCE.getDragSource(sourcePackageList);
		dragSource.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				event.data = formatDataToTransfer(sourcePackageList.getSelection());
			}

			private Object formatDataToTransfer(String[] selection) {
				StringBuffer buffer = new StringBuffer();
				for (String field : selection) {
					buffer.append(field + Constants.DOT + Constants.ASTERISK + SWT.SPACE + Constants.DASH + SWT.SPACE
							+ comboJarList.getItem(comboJarList.getSelectionIndex())
							+ Constants.FIELD_SEPRATOR_FOR_DRAG_DROP);
				}
				return buffer.toString();
			}
		});
	}

	private void addListnersToCombo() {
		comboJarList.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				populatePackageNameFromSelectedJar();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /*Do-nothing*/ }
		});
	}

	/**
	 * Populate packages for selected jar file
	 * 
	 */
	public void populatePackageNameFromSelectedJar() {
		sourcePackageList.removeAll();
		IPackageFragmentRoot iPackageFragmentRoot = (IPackageFragmentRoot) comboJarList.getData(String
				.valueOf(comboJarList.getSelectionIndex()));
		if (iPackageFragmentRoot != null) {
			try {
				for (IJavaElement iJavaElement : iPackageFragmentRoot.getChildren()) {
						if (iJavaElement instanceof IPackageFragment) {
							IPackageFragment packageFragment = (IPackageFragment) iJavaElement;
							if (packageFragment.containsJavaResources()) {
								if(!isAlreadyPresentInTargetList(packageFragment.getElementName()))
								sourcePackageList.add(packageFragment.getElementName());
							}
						}
				}
			} catch (JavaModelException javaModelException) {
				LOGGER.warn("Error occurred while fetching packages from "
						+ iPackageFragmentRoot.getElementName());
			}
		}
		if(comboJarList.getSelectionIndex() ==0){
			deleteButton.setEnabled(false);
		}else{
			deleteButton.setEnabled(true);
		}
		enableOrDisableAddLabelsOnComboSelection();
	}
	
	private boolean isAlreadyPresentInTargetList(String packageName) {
		if(packageName!=null){
			for(String selectedPackage:targetList.getItems()){
				if(StringUtils.equals(ExpressionEditorUtil.INSTANCE.getPackageNameFromFormattedText(selectedPackage), packageName)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Enables or Disables add-buttons as per the source package list.
	 */
	public void enableOrDisableAddLabelsOnComboSelection() {
		addSelectedPackagesLabel.setEnabled(false);
		if(sourcePackageList.getItemCount()>0){
			addAllSelectedPackagesLabel.setEnabled(true);
		}else{
			addAllSelectedPackagesLabel.setEnabled(false);
		}
	}

	@SuppressWarnings("restriction")
	private void loadComboJarListFromBuildPath(Combo comboJarList, String newJarName) {
		comboJarList.removeAll();
		IProject iProject = BuildExpressionEditorDataSturcture.INSTANCE.getCurrentProject();
		IJavaProject iJavaProject=null;
		try {
			iJavaProject = JavaCore.create(iProject);
			PackageFragmentRoot srcfragmentRoot= BuildExpressionEditorDataSturcture.INSTANCE.getSrcPackageFragment(iJavaProject);
			comboJarList.add(hydrograph.ui.common.util.Constants.ProjectSupport_SRC);
			comboJarList.setData(String.valueOf(comboJarList.getItemCount() - 1), srcfragmentRoot);
			
			for (IPackageFragmentRoot iPackageFragmentRoot : iJavaProject.getAllPackageFragmentRoots()) {
				if (isJarPresentInLibFolder(iPackageFragmentRoot.getPath())
						&& iPackageFragmentRoot.getKind() != IPackageFragmentRoot.K_SOURCE) {
					comboJarList.add(iPackageFragmentRoot.getElementName());
					comboJarList.setData(String.valueOf(comboJarList.getItemCount() - 1), iPackageFragmentRoot);
				}
			}
			selectAndLoadJarData(newJarName);
		} catch (JavaModelException javaModelException) {
			LOGGER.error("Error occurred while loading engines-transform jar", javaModelException);
		}
		finally{
			if(iJavaProject!=null){
				try {
					iJavaProject.close();
				} catch (JavaModelException e) {
					LOGGER.warn("JavaModelException occurred while closing java-project"+e);
				}
			}
		}

	}

	private void selectAndLoadJarData(String newJarName) {
		if(StringUtils.isNotBlank(newJarName) && comboJarList.indexOf(newJarName)!=-1){
			comboJarList.select(comboJarList.indexOf(newJarName));
			populatePackageNameFromSelectedJar();
		}
	}


	private boolean isJarPresentInLibFolder(IPath path) {
		String currentProjectName = BuildExpressionEditorDataSturcture.INSTANCE.getCurrentProject().getName();
		if (StringUtils.equals(currentProjectName, path.segment(0))
				&& StringUtils.equals(PathConstant.PROJECT_LIB_FOLDER, path.segment(1)))
			return true;
		return false;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setTargetList(List targetList) {
		this.targetList = targetList;
	}

	public void setTargetComposite(CategoriesDialogTargetComposite categoriesDialogTargetComposite) {
		this.targetComposite = categoriesDialogTargetComposite;
	}

	public void setAddAllLabel(Label addAllSelectedPackagesLabel) {
		this.addAllSelectedPackagesLabel=addAllSelectedPackagesLabel;
	}

	public void setAddLabel(Label addSelectedPackagesLabel) {
		this.addSelectedPackagesLabel=addSelectedPackagesLabel;
	}
	
	public Combo getComboJarList() {
		return comboJarList;
	}
	
	public List getSourcePackageList() {
		return sourcePackageList;
	}

}


