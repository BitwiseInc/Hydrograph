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
package hydrograph.ui.expression.editor.jar.util;

import hydrograph.ui.common.util.ConfigFileReader;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.PathConstant;
import hydrograph.ui.expression.editor.message.CustomMessageBox;
import hydrograph.ui.expression.editor.repo.ClassRepo;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.Workbench;
import org.slf4j.Logger;

public class BuildExpressionEditorDataSturcture {

	private static final String JAR_PACKAGE_FRAGMENT = "JarPackageFragment";
	public static final BuildExpressionEditorDataSturcture INSTANCE = new BuildExpressionEditorDataSturcture();
	private Logger LOGGER = LogFactory.INSTANCE.getLogger(BuildExpressionEditorDataSturcture.class);

	public void createClassRepo(String jarFileName, String Package) {
		ClassRepo.INSTANCE.flusRepo();
		try {
			IPackageFragmentRoot iPackageFragmentRoot = getIPackageFragment(jarFileName);
			if (iPackageFragmentRoot != null) {
				IPackageFragment fragment = iPackageFragmentRoot.getPackageFragment(Package);
				if (fragment != null) {
					for (IClassFile element : fragment.getClassFiles()) {
						ClassRepo.INSTANCE.addClass(element,"","",false);
					}
				} else {
					new CustomMessageBox(SWT.ERROR, Package + " Package not found in jar "
							+ iPackageFragmentRoot.getElementName(), "ERROR").open();
				}
			}
		} catch (JavaModelException e) {
			LOGGER.error("Error occurred while loading class from jar {}", jarFileName, e);
		}
		loadClassesFromSettingsFolder();
	}

	@SuppressWarnings("restriction")
	public boolean loadUserDefinedClassesInClassRepo(String jarFileName, String packageName) {
		IPackageFragmentRoot iPackageFragmentRoot = getIPackageFragment(jarFileName);
		try {
			if (iPackageFragmentRoot != null) {
				IPackageFragment fragment = iPackageFragmentRoot.getPackageFragment(packageName);
				if (fragment != null && StringUtils.equals(fragment.getClass().getSimpleName(), JAR_PACKAGE_FRAGMENT)) {
					for (IClassFile element : fragment.getClassFiles()) {
						if( isValidCLassName(element.getElementName())) 
						ClassRepo.INSTANCE.addClass(element, jarFileName, packageName, true);
					}
					return true;
				} else {
					for (IJavaElement element : fragment.getChildren()) {
						if (element instanceof ICompilationUnit) {
							for (IJavaElement javaElement : ((ICompilationUnit) element).getChildren()) {
								if (javaElement instanceof SourceType && isValidCLassName(javaElement.getElementName())) {
									ClassRepo.INSTANCE.addClass((SourceType) javaElement, jarFileName, packageName,true);
								}
							}
						}
					}
					return true;
				}
			} else {
				LOGGER.warn("Package not found in jar " + iPackageFragmentRoot.getElementName(), "ERROR");
			}
		} catch (JavaModelException e) {
			LOGGER.error("Error occurred while loading class from jar", e);
		}
		return false;
	}

	private boolean isValidCLassName(String className) {
		className = StringUtils.removeEndIgnoreCase(className, Constants.CLASS_EXTENSION);
		Matcher matchs = Pattern.compile(Constants.ALPHANUMERIC_REGEX).matcher(className);
		if(!matchs.matches())
			return false;
		return true;
	}
	
	@SuppressWarnings("restriction")
	public PackageFragmentRoot getSrcPackageFragment(IJavaProject iJavaProject) throws JavaModelException {
		for(IPackageFragmentRoot iPackageFragmentRoot:iJavaProject.getAllPackageFragmentRoots()){
			if(iPackageFragmentRoot instanceof PackageFragmentRoot && iPackageFragmentRoot.getKind()==iPackageFragmentRoot.K_SOURCE){
				if(StringUtils.startsWith(iPackageFragmentRoot.toString(),hydrograph.ui.common.util.Constants.ProjectSupport_SRC)){
					return (PackageFragmentRoot) iPackageFragmentRoot;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns packages of given jar file
	 * 
	 * @param jarFileName
	 * @return IPackageFragmentRoot
	 * 			packages of given jar file
	 */
	public IPackageFragmentRoot getIPackageFragment(String jarFileName) {
		IProject iProject = getCurrentProject();
		IJavaProject javaProject = JavaCore.create(iProject);
		try {
			if(StringUtils.equals(jarFileName, hydrograph.ui.common.util.Constants.ProjectSupport_SRC)){
				return getSrcPackageFragment(javaProject);
			}
			
			IPackageFragmentRoot[] fragmentRoot = javaProject.getAllPackageFragmentRoots();
			for (IPackageFragmentRoot iPackageFragmentRoot : fragmentRoot) {
				if (StringUtils.contains(iPackageFragmentRoot.getElementName(), jarFileName))
					return iPackageFragmentRoot;
			}
		} catch (JavaModelException javaModelException) {
			LOGGER.error("Error occurred while loading engines-transform jar", javaModelException);
		}
		finally{
			if(javaProject!=null){
				try {
					javaProject.close();
				} catch (JavaModelException modelException) {
					LOGGER.warn("JavaModelException occurred while closing java-project"+modelException);
				}
			}
		}
		if(StringUtils.equals(jarFileName, ConfigFileReader.INSTANCE.getConfigurationValueFromCommon(Constants.KEY_TRANSFORMATION_JAR)))
			new CustomMessageBox(SWT.ERROR, "Error occurred while loading " + jarFileName + " file", "ERROR").open();
		return null;
	}

	public IProject getCurrentProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		IProject project = null;
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IProject) {
				project = ((IResource) element).getProject();
			} else if (element instanceof IFolder) {
				project = ((IFolder) element).getProject();
			} else
				project = getProjectFromActiveGraph();
		}
		return project;
	}

	private IProject getProjectFromActiveGraph() {
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()!=null)
		{	
		IEditorInput editorInput=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		if(editorInput instanceof IFileEditorInput){
			return ((IFileEditorInput)editorInput).getFile().getProject();
		}else if(editorInput instanceof FileStoreEditorInput){
			new CustomMessageBox(SWT.ERROR ,Messages.ERROR_WHILE_LOADING_CONFIGURATIONS_FOR_EXTERNAL_JOBS,Messages.TITLE_FOR_PROBLEM_IN_LOADING_EXPRESSION_EDITOR).open();
		}
		}
		return null;
	}

	public void refreshRepo(){
		createClassRepo(ConfigFileReader.INSTANCE.getConfigurationValueFromCommon(Constants.KEY_TRANSFORMATION_JAR), Constants.PACKAGE_NAME);
		loadClassesFromSettingsFolder();
	}
	
	private void loadClassesFromSettingsFolder() {
		Properties properties = new Properties();
		IFolder folder=getCurrentProject().getFolder(PathConstant.PROJECT_RESOURCES_FOLDER);
		IFile file = folder.getFile(PathConstant.EXPRESSION_EDITOR_EXTERNAL_JARS_PROPERTIES_FILES);
		try {
			LOGGER.debug("Loading property file");
			if (file.getLocation().toFile().exists()) {
				FileInputStream inStream = new FileInputStream(file.getLocation().toString());
				properties.load(inStream);
				
				for(Object key:properties.keySet()){
					String packageName=StringUtils.remove((String)key,Constants.DOT+Constants.ASTERISK);
					if(StringUtils.isNotBlank(properties.getProperty((String)key)) && StringUtils.isNotBlank(packageName)){
						loadUserDefinedClassesInClassRepo(properties.getProperty((String)key),packageName);
					}
				}
			}
		} catch (IOException |RuntimeException exception) {
			LOGGER.error("Exception occurred while loading jar files from projects setting folder",exception);
		}
	}

}
