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

package hydrograph.ui.graph.editor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.CopyParticipant;

import hydrograph.ui.graph.Messages;

/**
 * JobCopyParticipant to prepare the information required for paste action of project explorer.
 * 
 * @author Bitwise
 *
 */
public class JobCopyParticipant extends CopyParticipant {

	private IFile modifiedResource;
	private static List<IFile> copiedFileList;
	private static String copyToPath;
	private static List<IFile> previousJobFiles;
	


	@Override
	protected boolean initialize(Object element) {
		if (element instanceof IFile) {
			this.modifiedResource = (IFile) element;
			if (modifiedResource == null && StringUtils.isEmpty(modifiedResource.toString())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		
		copyToPath=getArguments().getDestination().toString();
		IWorkspaceRoot workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workSpaceRoot.getProject(copyToPath.split("/")[1]);
		IFolder jobFolder = project.getFolder(copyToPath.substring(copyToPath.indexOf('/', 2)));
		previousJobFiles=new ArrayList<>();
		for (IResource iResource : jobFolder.members()) {
			if (!(iResource instanceof IFolder)) {
				IFile iFile = (IFile) iResource;
				 if (iFile.getFileExtension().equalsIgnoreCase(Messages.JOB_EXT)) {
					 previousJobFiles.add(iFile);
				 }
			}
		}
		copiedFileList.add(modifiedResource);
		return null;
	}

	
	@Override
	public Change createPreChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return super.createPreChange(pm);
	}


	public static String getCopyToPath() {
		return copyToPath;
	}


	
	public static List<IFile> getCopiedFileList() {
		return copiedFileList;
	}

	public static void setCopiedFileList(List<IFile> copiedFileList) {
		JobCopyParticipant.copiedFileList = copiedFileList;
	}

	public static void cleanUpStaticResourcesAfterPasteOperation()
	{
		copyToPath=null;
		copiedFileList=null;
		previousJobFiles=null;
	}
	
	public static List<IFile> getPreviousJobFiles() {
		return previousJobFiles;
	}


	
}
