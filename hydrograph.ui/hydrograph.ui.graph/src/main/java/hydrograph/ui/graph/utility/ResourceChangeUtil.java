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

 
package hydrograph.ui.graph.utility;

import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.project.structure.CustomMessages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;


public class ResourceChangeUtil {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ResourceChangeUtil.class);
	
	private ResourceChangeUtil() {
		
	}
	
	public static void addMembersToList(List<IResource> memberList, IFolder folder) {
		try {
			for(IResource resource: folder.members()) {
				if(resource instanceof IFile) {
					memberList.add(resource);
				}
				else {
					addMembersToList(memberList, (IFolder)resource);
				}
			}
		} catch (CoreException e) {
			logger.debug("Unable to add members to list " , e);
		}
	}
	
	public static boolean isGeneratedFile(String fileName,IProject project) {
		List<IResource> memberList= new ArrayList<>(); 
		String jobFileName = removeExtension(fileName)+".job";
		
		addMembersToList(memberList, (IFolder)project.getFolder(CustomMessages.ProjectSupport_JOBS));
		for(IResource file:memberList) {
			if(jobFileName.equals(file.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public static String removeExtension(String fileName) {
		return fileName.substring(0,fileName.lastIndexOf("."));
	}
}
