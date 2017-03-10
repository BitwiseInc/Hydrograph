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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.utility.ResourceChangeUtil;
import hydrograph.ui.project.structure.CustomMessages;


/**The Class Rename Job Participant
 * @author Bitwise
 *
 */
public class RenameJobParticipant extends RenameParticipant {
	private static final String DEBUG = "_debug";
	private IFile modifiedResource;
	
	
	@Override
	protected boolean initialize(Object element) {
		this.modifiedResource = (IFile) element;
		if (modifiedResource.getParent() != null && modifiedResource.getParent().getParent() instanceof IProject) {
			if (StringUtils.equalsIgnoreCase(Messages.PROPERTIES_EXT, modifiedResource.getFileExtension())) {
				if (!StringUtils.equalsIgnoreCase(modifiedResource.getParent().getName(), Messages.PARAM)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "Job File Renaming Participant";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		String newName = getArguments().getNewName();
		String newExt = newName.substring(newName.lastIndexOf(".")+1);
		
		if("job".equals(modifiedResource.getFileExtension())) {
			if(!("job".equals(newExt))) {
				return RefactoringStatus.createFatalErrorStatus("Changing extension of job file is not allowed");
			}
		}
		else if("job".equals(newExt)) {
			return RefactoringStatus.createFatalErrorStatus("Changing extension to .job not allowed." +
					"Please create a new job file.");
		}
		else if(CustomMessages.ProjectSupport_JOBS.equals(modifiedResource.getFullPath().segment(1))
				&& !newExt.matches("job|xml")) {
			return RefactoringStatus.createFatalErrorStatus("Only .job and .xml files can be stored in this folder" );
		}
		else if(modifiedResource.getFileExtension().matches("xml|properties")
				|| (newExt.matches("xml|properties"))){
			if(ResourceChangeUtil.isGeneratedFile(modifiedResource.getName()
											,modifiedResource.getProject())) {
				return RefactoringStatus.createFatalErrorStatus(
						".xml or .properties file cannot be renamed. " +
						"Rename the .job file to rename the xml and properties file");
				
			}
			else if(ResourceChangeUtil.isGeneratedFile(newName,modifiedResource.getProject())) {
				return RefactoringStatus.createFatalErrorStatus("Generated file with same name exists.Please choose a different name");
			}
			else if(("properties".equals(modifiedResource.getFileExtension()) 
					|| "properties".equals(newExt))
					&& !modifiedResource.getFullPath().segment(1).equals(CustomMessages.ProjectSupport_PARAM)) {
				return RefactoringStatus.createFatalErrorStatus("properties file can only be saved in param folder.");
			}
		}
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		final HashMap<IFile,RenameResourceChange> changes= new HashMap<IFile,RenameResourceChange>();
		final String newName = ResourceChangeUtil.removeExtension(getArguments().getNewName());
		
		if (modifiedResource.getParent() != null) {
			if (!StringUtils.equalsIgnoreCase(modifiedResource.getParent().getName(),CustomMessages.ProjectSupport_JOBS)) {
				List<IResource> memberList = new ArrayList<IResource>(modifiedResource.getProject()
						.getFolder(modifiedResource.getParent().getName()).members().length);
				ResourceChangeUtil.addMembersToList(memberList,modifiedResource.getProject().getFolder(modifiedResource.getParent().getName()));
				final String fileName = ResourceChangeUtil.removeExtension(modifiedResource.getName());
				for (IResource resource : memberList) {
					if (Pattern.matches(fileName + Constants.EXTENSION, resource.getName())) {
						if((StringUtils.equalsIgnoreCase(Messages.XML_EXT,resource.getFileExtension())
								||StringUtils.equalsIgnoreCase(Messages.JOB_EXT,resource.getFileExtension()))
								&&!(StringUtils.equalsIgnoreCase(modifiedResource.getName(),resource.getName())))
						{
							getRenameChanges(changes, newName, resource);
						}
					}
				}
			}
			else if(StringUtils.equalsIgnoreCase(modifiedResource.getParent().getName(),CustomMessages.ProjectSupport_JOBS)||
					StringUtils.equalsIgnoreCase(modifiedResource.getParent().getName(),CustomMessages.ProjectSupport_PARAM))
			{
				List<IResource> memberList = new ArrayList<IResource>(modifiedResource.getProject()
						.getFolder(CustomMessages.ProjectSupport_PARAM).members().length
						+ modifiedResource.getProject().getFolder(CustomMessages.ProjectSupport_JOBS).members().length);
				ResourceChangeUtil.addMembersToList(memberList,modifiedResource.getProject().getFolder(CustomMessages.ProjectSupport_JOBS));
				ResourceChangeUtil.addMembersToList(memberList,modifiedResource.getProject().getFolder(CustomMessages.ProjectSupport_PARAM));
				final String fileName = ResourceChangeUtil.removeExtension(modifiedResource.getName());
				for (IResource resource : memberList) {
					if (Pattern.matches(fileName + Constants.EXTENSION, resource.getName())) {
						if ((StringUtils.equalsIgnoreCase(Messages.XML_EXT, resource.getFileExtension())
								|| StringUtils.equalsIgnoreCase(Messages.PROPERTIES_EXT, resource.getFileExtension())
								|| StringUtils.equalsIgnoreCase(Messages.JOB_EXT, resource.getFileExtension()))
								&& !(StringUtils.equalsIgnoreCase(modifiedResource.getName(),resource.getName()))) {
							getRenameChanges(changes, newName, resource);
						};
					}
				}
			}
		}
				
		if (changes.isEmpty()) {
	        return null;
		}
		
		CompositeChange result= new CompositeChange("Rename Job Related Files"); 
	    for (Iterator<RenameResourceChange> iter= changes.values().iterator(); iter.hasNext();) {
	        result.add((Change) iter.next());
	    }
		return result;
	}

	
	private void getRenameChanges(final HashMap<IFile, RenameResourceChange> changes, String newName,
			IResource resource) {
		RenameResourceChange change = (RenameResourceChange) changes.get((IFile) resource);
		
		if (change == null) {
			String fileName = ResourceChangeUtil.removeExtension(resource.getName());
			if(fileName.endsWith(DEBUG)){
				newName = newName.concat(DEBUG);
			}
			change = new RenameResourceChange(resource.getFullPath(),
					newName + "." + resource.getFileExtension());
			changes.put((IFile) resource, change);
		}
	}

}
