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

 
package hydrograph.ui.project.structure.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.project.structure.Activator;
import hydrograph.ui.project.structure.CustomMessages;
import hydrograph.ui.project.structure.natures.ProjectNature;

/**
 * Class to create the Custom Project Structure.
 *
 */
public class ProjectStructureCreator {

	private static final String ORG_ECLIPSE_M2E_CORE_MAVEN2_NATURE = "org.eclipse.m2e.core.maven2Nature";
	private static final String POM_XML = "pom.xml";
	private static final String MAVEN = "maven";
	private static final String TEMPLATE_PROJECT_NAME = "templateProjectName";
	private static final String PROPERTIES = "properties";
	private static final String BUILD = "build";

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ProjectStructureCreator.class);

	public static final String [] paths = {CustomMessages.ProjectSupport_Settings,CustomMessages.ProjectSupport_JOBS,CustomMessages.ProjectSupport_RESOURCES,Constants.ProjectSupport_SRC,  
		CustomMessages.ProjectSupport_SCRIPTS,CustomMessages.ProjectSupport_PARAM,CustomMessages.ProjectSupport_GLOBAL_PARAM,CustomMessages.ProjectSupport_SCHEMA,CustomMessages.ProjectSupport_EXTERNALTRANSFORMFILES,CustomMessages.ProjectSupport_LIB};

	public static final ProjectStructureCreator INSTANCE = new ProjectStructureCreator();

	private ProjectStructureCreator(){
	}

	/**
	 * Creates the custom project structure at the specified location and name
	 * The Project will have below natures:<br>
	 * <b>Java</b> and Custom nature as per <b>ETL</b> project
	 * @param projectName Project name
	 * @param location Where should the project be saved
	 * @return
	 */
	public IProject createProject(String projectName, URI location){
		if (StringUtils.isNotBlank(projectName) && projectName.contains(" ")){
			MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setText("Error");
			messageBox.setMessage("The Project Name has spaces");
			if(messageBox.open()==SWT.OK)
			{
				return null;
			}
		}
		else if(StringUtils.isBlank(projectName)){
			throw new InvalidProjectNameException();
		}
		IProject project = null;
		try {
			project = createBaseProject(projectName, location);
			if(project!=null)
			{
				addNature(project);
				addToProjectStructure(project, paths);
				IJavaProject javaProject = JavaCore.create(project);

				IFolder binFolder = project.getFolder(CustomMessages.ProjectSupport_BIN);
				javaProject.setOutputLocation(binFolder.getFullPath(), null);

				List<IClasspathEntry> entries = addJavaLibrariesInClassPath();

				IFolder libFolder = project.getFolder(CustomMessages.ProjectSupport_LIB);

				//add libs to project class path 
				String installLocation = Platform.getInstallLocation().getURL().getPath();

				//copyExternalLibAndAddToClassPath(installLocation + CustomMessages.ProjectSupport_LIB, libFolder, entries);

				copyBuildFile(installLocation + CustomMessages.ProjectSupport_CONFIG_FOLDER + "/" + 
						CustomMessages.ProjectSupport_GRADLE + "/" + BUILD, project);
				
				copyBuildFile(installLocation + CustomMessages.ProjectSupport_CONFIG_FOLDER + "/" + 
						CustomMessages.ProjectSupport_GRADLE + "/" + PROPERTIES, project);
				
				copyBuildFile(installLocation + CustomMessages.ProjectSupport_CONFIG_FOLDER + "/" + 
						MAVEN, project);
				
				updateMavenFile(POM_XML, project);
				
				javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);

				//set source folder entry in classpath
				javaProject.setRawClasspath(setSourceFolderInClassPath(project, javaProject), null);
			}
		} catch (CoreException e) {
			logger.debug("Failed to create Project with parameters as projectName : {} location : {}, exception : {}", 
					new Object[]{projectName, location, e});
			project = null;
		}

		return project;
	} 

	private void copyBuildFile(String source, IProject project) throws CoreException {
		File sourceFileLocation = new File(source);
		File[] listFiles = sourceFileLocation.listFiles();
		if(listFiles != null){
			for(File sourceFile : listFiles){
				IFile destinationFile = project.getFile(sourceFile.getName());
				try(InputStream fileInputStream = new FileInputStream(sourceFile)) {
					if(!destinationFile.exists()){ //used while importing a project
						destinationFile.create(fileInputStream, true, null);
					}
				} catch (IOException | CoreException exception) {
					logger.debug("Copy build file operation failed");
					throw new CoreException(new MultiStatus(Activator.PLUGIN_ID, 100, "Copy build file operation failed", exception));
				}
			}
		}
	}

	private void updateMavenFile(String source, IProject project) throws CoreException {
		try{
			IFile destinationFile = project.getFile(source);
			java.nio.file.Path path = Paths.get(destinationFile.getLocationURI());
			Charset charset = StandardCharsets.UTF_8;
			String content = new String(Files.readAllBytes(path), charset);
			content = content.replaceAll(TEMPLATE_PROJECT_NAME, project.getName());
			Files.write(path, content.getBytes(charset));
		} catch (IOException exception) {
			logger.debug("Could not change the group and artifact name");
			throw new CoreException(new MultiStatus(Activator.PLUGIN_ID, 100, "Could not change the group and artifact name", exception));
		}
	}
	
	/**
	 * Sets the <b>src</b> folder as the source folder in project
	 * @param project
	 * @param javaProject
	 * @return IClasspathEntry[]
	 * @throws JavaModelException
	 */
	private IClasspathEntry[] setSourceFolderInClassPath(IProject project,	IJavaProject javaProject) throws JavaModelException {
		IFolder sourceFolder = project.getFolder(Constants.ProjectSupport_SRC); //$NON-NLS-1$
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(sourceFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
		return newEntries;
	}

	/**
	 * Adds java libraries to the classpath
	 * @return List &lt; IClasspathEntry &gt;
	 */
	private List<IClasspathEntry> addJavaLibrariesInClassPath() {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		entries.add(JavaCore.newContainerEntry(JavaRuntime.newDefaultJREContainerPath()));
		entries.add(JavaCore.newContainerEntry(new Path("org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER")));
		return entries;
	}

	/**
	 * Creates the specified folder if it does not exists
	 * @param folder
	 * @throws CoreException
	 */
	private void createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			createFolder((IFolder) parent);
		}
		if (!folder.exists()) {
			folder.create(false, true, null);
		}
	}

	/**
	 * Create a folder structure as specified in the parameters
	 * @param newProject 
	 * @param paths contains the names of the folder that need to be created.
	 * @throws CoreException
	 */
	private void addToProjectStructure(IProject newProject, String[] paths) throws CoreException {
		for (String path : paths) {
			IFolder etcFolders = newProject.getFolder(path);
			createFolder(etcFolders);
		}
	}

	private void addNature(IProject project) throws CoreException{
		if(!project.hasNature(ProjectNature.NATURE_ID)){
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 3];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = ProjectNature.NATURE_ID;
			newNatures[prevNatures.length + 1] = JavaCore.NATURE_ID;
			newNatures[prevNatures.length + 2] = ORG_ECLIPSE_M2E_CORE_MAVEN2_NATURE;

			// validate the natures
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IStatus status = workspace.validateNatureSet(newNatures); 
			ICommand javaBuildCommand= description.newCommand();
			javaBuildCommand.setBuilderName("org.eclipse.jdt.core.javabuilder");
			ICommand mavenBuildCommand= description.newCommand();
			mavenBuildCommand.setBuilderName("org.eclipse.m2e.core.maven2Builder");
			ICommand[] iCommand = {javaBuildCommand, mavenBuildCommand};
			description.setBuildSpec(iCommand); 
			// only apply new nature, if the status is ok
			if (status.getCode() == IStatus.OK) {
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
			}
			logger.debug("Project nature added"); 
		}
	}

	/**
	 * Creates the base structure of the project under specified location and name
	 * @param projectName
	 * @param location
	 * @return
	 * @throws CoreException 
	 */
	private IProject createBaseProject(String projectName, URI location) throws CoreException {
		IProject newProject=null;
		
		if(location==null){
				newProject = createTheProjectAtSpecifiedLocation(projectName,location);
		}
		else{
			IPath iPath	 = new Path(location.getPath());
			if (!StringUtils.equals(iPath.lastSegment(), projectName)) {
				iPath = iPath.append(projectName);
			}
			URI newLocation=URI.create(iPath.toFile().toURI().toString());
				newProject=	createTheProjectAtSpecifiedLocation(projectName, newLocation);
		}
		return newProject;
	}
	
	private IProject createTheProjectAtSpecifiedLocation(String projectName,
			URI location) throws CoreException {
		IProject newProject;
		newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		if(!newProject.exists()){
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
			if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
				desc.setLocationURI(null);
			}
			else{
				desc.setLocationURI(location);
			}

			try {
				newProject.create(desc, null);
				if (!newProject.isOpen()) {
					newProject.open(null);
				}
				logger.debug("Project base structure created"); 
			} catch (CoreException exception) {
				logger.debug("Project base structure creation failed");
				throw exception;
			}
		}
		return newProject;
	}

	/*
	 * Copy External jar to project lib directory
	 * @param source path
	 * @param target path
	 * @return list of added files path
	 */
	private void copyExternalLibAndAddToClassPath(String source,IFolder destinationFolder, List<IClasspathEntry> entries) throws CoreException{
		File sourceFileLocation = new File(source);
		File[] listFiles = sourceFileLocation.listFiles();
		if(listFiles != null){
			for(File sourceFile : listFiles){
				IFile destinationFile = destinationFolder.getFile(sourceFile.getName());
				try(InputStream fileInputStream = new FileInputStream(sourceFile)) {
					if(!destinationFile.exists()){ //used while importing a project
						destinationFile.create(fileInputStream, true, null);
					}
					entries.add(JavaCore.newLibraryEntry(new Path(destinationFile.getLocation().toOSString()), null, null));
				} catch (IOException | CoreException exception) {
					logger.debug("Copy external library files operation failed", exception);
					throw new CoreException(new MultiStatus(Activator.PLUGIN_ID, 101, "Copy external library files operation failed", exception));
				}
			}
		}
	}

	/**
	 * The Class InvalidProjectNameException.
	 * 
	 * @author Bitwise
	 */
	public class InvalidProjectNameException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = -6194407190206545086L;
	}
}
