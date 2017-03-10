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

 
package hydrograph.ui.menus.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.refactoring.reorg.PasteAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;

import hydrograph.ui.engine.util.ConverterUtil;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.editor.JobCopyParticipant;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.utils.GenerateUniqueJobIdUtil;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.logging.factory.LogFactory;



/**
 * The Class PasteHandler.
 * <p>
 * Handler to Paste component on canvas and Project Explorer 
 * 
 * @author Bitwise
 */
public class PasteHandler extends AbstractHandler implements IHandler {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(PasteHandler.class);
	private static final String ERROR = "Error"; 
	private static final String JOB_EXTENSION=".job";
	private static final String PROPERTIES_EXTENSION=".properties";
	private static final String XML="xml";
	private static final String JOB="job";
	private static final String PARAMETER_FOLDER_NAME="param";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<IFile> jobFiles = new ArrayList<>();
		List<IFile> pastedFileList = new ArrayList<>();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if(part instanceof CommonNavigator){
			PasteAction action = new PasteAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite());
			action.run();
			IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = workSpaceRoot.getProject(JobCopyParticipant.getCopyToPath().split("/")[1]);
			IFolder jobFolder = project.getFolder(
					JobCopyParticipant.getCopyToPath().substring(JobCopyParticipant.getCopyToPath().indexOf('/', 2)));
			IFolder paramFolder = project.getFolder(PARAMETER_FOLDER_NAME);
			try {
				createCurrentJobFileList(jobFolder, jobFiles);
				pastedFileList=getPastedFileList(jobFiles);
				generateUniqueJobIdForPastedFiles(pastedFileList);
				createXmlFilesForPastedJobFiles(pastedFileList);
				List<String> copiedPropertiesList = getCopiedPropertiesList();
				createPropertiesFilesForPastedFiles(paramFolder, pastedFileList, copiedPropertiesList);
				JobCopyParticipant.cleanUpStaticResourcesAfterPasteOperation();

			} catch (CoreException  coreException) {
				logger.warn("Error while copy paste jobFiles",coreException.getMessage() );
			}
			
		}
		
		else if(part instanceof ELTGraphicalEditor){
			IEditorPart editor = HandlerUtil.getActiveEditor(event);
			((ELTGraphicalEditor)editor).pasteSelection();
		}
		
		return null;
	}

	private void generateUniqueJobIdForPastedFiles(List<IFile> pastedFileList) {
		for (IFile file : pastedFileList) {
			try(ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					InputStream inputStream=file.getContents()) {
				Container container = (Container) CanvasUtils.INSTANCE.fromXMLToObject(inputStream);
				container.setUniqueJobId(GenerateUniqueJobIdUtil.INSTANCE.generateUniqueJobId());
			CanvasUtils.INSTANCE.fromObjectToXML(container,outStream);
				file.setContents(new ByteArrayInputStream(outStream.toByteArray()), true, false, null);
				

			} catch (CoreException | NoSuchAlgorithmException | IOException exception) {
				logger.warn("Exception while generating unique job id for pasted files.");

			} 
		}
		
	}
	private void createPropertiesFilesForPastedFiles(IFolder paramFolder, List<IFile> pastedFileList,
			List<String> copiedPropertiesList){
		for (int i = 0; i < copiedPropertiesList.size(); i++) {
			try(InputStream inputStream = paramFolder.getFile(copiedPropertiesList.get(i)).getContents()) {
				IFile file = paramFolder
						.getFile(pastedFileList.get(i).getName().replace(JOB_EXTENSION,PROPERTIES_EXTENSION));
				if (!file.exists()) {
					file.create(inputStream, true, null);
				} else {
					int userInput=showErrorMessage(file, file.getName() + " already exists.Do you want to replace it?");
					if (userInput == SWT.YES) {
						file.setContents(inputStream, true,false, null);
					}
				}
			} catch (CoreException | IOException coreException) {
				logger.error("Error while creating properties files for pasted files ::{}", coreException.getMessage());
				
			} 
		}
	}
	

	private List<String> getCopiedPropertiesList() {
		List<String> copiedPropertiesList = new ArrayList<>();
		List<IFile> copiedFileList = JobCopyParticipant.getCopiedFileList();
		for (IFile iFile : copiedFileList) {
			copiedPropertiesList.add((iFile.getName().replace(JOB_EXTENSION,PROPERTIES_EXTENSION)));
		}
		return copiedPropertiesList;
	}

	private void createXmlFilesForPastedJobFiles(List<IFile> pastedFileList) {
		for (IFile file : pastedFileList) {
			try(InputStream inputStream=file.getContents()) {
				Container container = (Container) CanvasUtils.INSTANCE.fromXMLToObject(inputStream);
				IPath path = file.getFullPath().removeFileExtension().addFileExtension(XML);
				IFile xmlFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if(xmlFile.exists()){
					int userInput = showErrorMessage(xmlFile,xmlFile.getName()+" already exists.Do you want to replace it?");
					if (userInput == SWT.YES) {
						ConverterUtil.INSTANCE.convertToXML(container, true, xmlFile, null);
					} 
				}
				else {
					ConverterUtil.INSTANCE.convertToXML(container, true, xmlFile, null);
				}

			} catch (CoreException | InstantiationException | IllegalAccessException | InvocationTargetException
					| NoSuchMethodException | IOException exception) {
				logger.error("Error while generating xml files for pasted job files", exception);

			} 
		}
	}
	private int showErrorMessage(IFile xmlFile,String message) {
		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(),
				SWT.ERROR | SWT.YES | SWT.NO);
		messageBox.setText(ERROR);
		messageBox.setMessage(message);
		int returnCode = messageBox.open();
		return returnCode;
	}



	private void createCurrentJobFileList(IFolder jobFolder, List<IFile> jobFiles)
			throws CoreException {
		for (IResource iResource : jobFolder.members()) {
			if (!(iResource instanceof IFolder)) {
				IFile iFile = (IFile) iResource;
				if (iFile.getFileExtension().equalsIgnoreCase(JOB)) {
					jobFiles.add(iFile);
				} 
			}
		}
		
	}
	private List<IFile> getPastedFileList(List<IFile> jobFiles) {
		List<IFile> newJobFilesList = ListUtils.subtract(jobFiles, JobCopyParticipant.getPreviousJobFiles());
		jobFiles.clear();
		jobFiles.addAll(newJobFilesList);
		return jobFiles;
	}


	

}
