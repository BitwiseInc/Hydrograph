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


package hydrograph.ui.validators.utils;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.InternalClassFileEditorInput;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.slf4j.Logger;

import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.logging.factory.LogFactory;



/**
 * utility class for validators.
 * 
 * @author Bitwise
 *
 */
public class ValidatorUtility {

private static final Logger logger = LogFactory.INSTANCE.getLogger(ValidatorUtility.class);
public static final ValidatorUtility INSTANCE = new ValidatorUtility();

/**
 * This method checks if java file is present under source folder or not.
 * @param filePath java file path. 
 * @return true if file is present otherwise false.
 */
public boolean isClassFilePresentOnBuildPath(String filePath)
{
	if(filePath.contains("."))
	{	
	String packageName=filePath.substring(0, filePath.lastIndexOf('.'));
	String JavaFileName=filePath.substring(filePath.lastIndexOf('.')+1);
	
       IJavaProject javaProject=null;
       
	   
	    	 ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
	   		 ISelection selection = selectionService.getSelection();    

	   		        if(selection instanceof IStructuredSelection) 
	   		        {    
	   		            Object element = ((IStructuredSelection)selection).getFirstElement(); 
	   		          if(element instanceof IResource)
	   		          { 	  
	   		         IProject project= ((IResource)element).getProject();
	   		         javaProject = JavaCore.create(project);
	   		          }
	   		          else
	   		          {
	   		        	javaProject=createJavaProjectThroughActiveEditor();
	   		          } 
        	     }
	   		        else if(selection instanceof TextSelection)
	   		        {
	   		     	javaProject=createJavaProjectThroughActiveEditor();
	   		        }
	    
		IPackageFragmentRoot[] ipackageFragmentRootList=null;
		try {
			ipackageFragmentRootList = javaProject.getPackageFragmentRoots();
		} catch (JavaModelException e) {
			logger.error("Unable to get jars which are on build path of project " ,e );
		}
		for(IPackageFragmentRoot tempIpackageFragmentRoot:ipackageFragmentRootList)
		{
			if(!tempIpackageFragmentRoot.getElementName().contains("-sources"))
			{		
			IPackageFragment packageFragment=tempIpackageFragmentRoot.getPackageFragment(packageName);
			if(!packageFragment.exists())
			continue;
			else
			{
				if(packageFragment.getCompilationUnit(JavaFileName+".java").exists()
						||packageFragment.getClassFile(JavaFileName+".class").exists()
						)
				return true;
			}	
			}
		} 
	   }
		return false;
}


private IJavaProject createJavaProjectThroughActiveEditor() {
	
	IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	 	    if(page.getActiveEditor().getEditorInput() instanceof IFileEditorInput)
	 	    {	
	 		IFileEditorInput input = (IFileEditorInput) page.getActiveEditor().getEditorInput();
	 		IFile file = input.getFile();
	 		IProject activeProject = file.getProject();
	 		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProject.getName());
	 		return JavaCore.create(project);
	 	    }
	 	    else if(page.getActiveEditor().getEditorInput() instanceof IClassFileEditorInput)
	 	    {
	  IClassFileEditorInput classFileEditorInput=(InternalClassFileEditorInput)page.getActiveEditor().getEditorInput() ;
	  IClassFile classFile=classFileEditorInput.getClassFile();
	  return classFile.getJavaProject();
	 	    }
	 	    return null;
}

	public void putOutputFieldsInTransformMapping(TransformMapping transformMapping) {
		transformMapping.getOutputFieldList().clear();
		for (MappingSheetRow mappingSheetRow : transformMapping.getMappingSheetRows()) {
			transformMapping.getOutputFieldList().addAll(mappingSheetRow.getOutputList());
		}
		if(!transformMapping.getMapAndPassthroughField().isEmpty()&&
	 			transformMapping.getMapAndPassthroughField().get(0).getFilterProperty()==null)
	 	{
	 		backwardJobComapatabilityCode(transformMapping);	
	 	}else{
	 		for (NameValueProperty nameValueProperty : transformMapping.getMapAndPassthroughField()) {
	 			if(!transformMapping.getOutputFieldList().contains(nameValueProperty.getFilterProperty())) {
	 				transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
	 			}
	 		}
	 	}
	}
	
	private void backwardJobComapatabilityCode(TransformMapping transformMapping)
    {
    		List<NameValueProperty> tempNameValuePropertyList=new ArrayList<>();
    		for(NameValueProperty nameValueProperty:transformMapping.getMapAndPassthroughField())
    		{
    			NameValueProperty newNameValueProperty=new NameValueProperty();
    			newNameValueProperty.setPropertyName(nameValueProperty.getPropertyName());
    			newNameValueProperty.setPropertyValue(nameValueProperty.getPropertyValue());
    			newNameValueProperty.getFilterProperty().setPropertyname(nameValueProperty.getPropertyValue());
    			tempNameValuePropertyList.add(newNameValueProperty);
    			transformMapping.getOutputFieldList().add(newNameValueProperty.getFilterProperty());
    		}	
    		transformMapping.getMapAndPassthroughField().clear();
    		transformMapping.getMapAndPassthroughField().addAll(tempNameValuePropertyList);
    }
}