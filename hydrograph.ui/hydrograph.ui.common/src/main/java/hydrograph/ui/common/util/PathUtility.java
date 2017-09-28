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

package hydrograph.ui.common.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;

import hydrograph.ui.common.Activator;
import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.logging.factory.LogFactory;

/**
 *  Utility class to check path is absolute.
 *  @author Bitwise
 */
public class PathUtility {
	
	private static Logger logger = LogFactory.INSTANCE.getLogger(PathUtility.class);
	private static final String NO_SCHEMA_NAME = "The file name is not given";
	private static final String COULD_NOT_LOCATE_THE_EXTERNAL_SCHEMA_FILE_PATH = "Could not locate the external file path";
	
	
	/** The Constant INSTANCE. */
	public static final PathUtility INSTANCE = new PathUtility();
	
	/**
	 * Instantiates a new path utility.
	 */
	private PathUtility(){
		
	}
	
	/**
	 * Checks if path is absolute.
	 *
	 * @param filePath
	 * @return true, if is absolute
	 */
	public boolean isAbsolute(String filePath){
		IPath path = new Path(filePath);
		return path.isAbsolute();
	}
	
	
	public File getPath(String extSchemaPathText, String defaultExtension, boolean showErrorMessage, String... fileExtensions){
		 File schemaFile=null;
		 String schemaPath =null;
		 String finalParamPath=null;
		 IEditorPart activeEditor= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		 if(activeEditor==null){
			return new File(getFilePath(defaultExtension, schemaPath, fileExtensions));
		 }
		 IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();

		 if(input instanceof IFileEditorInput){
			 
			 if(ParameterUtil.containsParameter(extSchemaPathText, '/')){
				 String paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText);
				 finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText, paramValue);
					while(ParameterUtil.containsParameter(finalParamPath, '/')){
						paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText);
				    	finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText, paramValue);
			    		}
					
					if(finalParamPath.endsWith("/")){
						if(showErrorMessage){
						 createMessageBox(NO_SCHEMA_NAME,"Error",SWT.ICON_ERROR|SWT.OK);
						 logger.error(NO_SCHEMA_NAME);
						}
						 return null;
					}
					else {
						schemaPath = getFilePath(defaultExtension, finalParamPath, fileExtensions);
					}
				  
			 }
		else {
			if (extSchemaPathText.endsWith("/")) {
				if(showErrorMessage){
					createMessageBox(NO_SCHEMA_NAME, "Error",
						SWT.ICON_ERROR | SWT.OK);
					logger.error(NO_SCHEMA_NAME);
				}
				return null;
			}else{
				if(!checkEndsWith(extSchemaPathText, fileExtensions))
				{
					extSchemaPathText=extSchemaPathText.concat(defaultExtension);
				}
				schemaPath = extSchemaPathText;
			}
		}
			 if(!StringUtils.isEmpty(schemaPath) && !ParameterUtil.containsParameter(schemaPath, Path.SEPARATOR) &&!new File(schemaPath).isAbsolute()){
				 IWorkspace workspace = ResourcesPlugin.getWorkspace();
				 IPath relativePath=null;
				 try{
					 relativePath=workspace.getRoot().getFile(new Path(schemaPath)).getLocation();
				 }
				 catch(IllegalArgumentException e)
				 {
					 if(showErrorMessage){
					 createMessageBox(COULD_NOT_LOCATE_THE_EXTERNAL_SCHEMA_FILE_PATH,"Error",SWT.ICON_ERROR|SWT.OK);
					 logger.error(COULD_NOT_LOCATE_THE_EXTERNAL_SCHEMA_FILE_PATH,e);
					 }
					 return null;
				 }	
				 if(relativePath!=null){
						 schemaFile = new File(getFilePath(defaultExtension, relativePath.toOSString(),fileExtensions));
				 }
				 else{
					 schemaFile = new File(getFilePath(defaultExtension, schemaPath,fileExtensions));
				 }
			 }
			 else
			 {	
				 schemaFile = new File(getFilePath(defaultExtension, schemaPath,fileExtensions));
			 }
		 }
		 else{
			 if(ParameterUtil.containsParameter(extSchemaPathText, '/')){
				 String paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText);
				 finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText, paramValue);
					while(ParameterUtil.containsParameter(finalParamPath, '/')){
						paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText);
				    	finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText, paramValue);
			    		}
				  schemaPath = finalParamPath;
			 }
			 else{
				  schemaPath = extSchemaPathText;
			 }
			 if(!new File(schemaPath).isAbsolute()){
				if(showErrorMessage){
				 Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
						 "Existing job is not saved. In order to use relative path save the job", null);
				 StatusManager.getManager().handle(status, StatusManager.BLOCK);
				 }
				 return schemaFile;
			 }
			 else {
				 schemaFile=new File(getFilePath(defaultExtension, schemaPath, fileExtensions));
			 }
		 }
		 return schemaFile;
	 }

	/**
	 * create SWT MessageBox
	 * 
	 * @param message to be shown 
	 * @param title of widow
	 * @param style to be set on window           
	 */
	
	private int createMessageBox(String message,String windowTitle,int style) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell,style);
		messageBox.setText(windowTitle);
		messageBox.setMessage(message);
		return messageBox.open();
	}
	
private static String getFilePath(String defaultExtension, String finalParamPath, String[] fileExtensions) {
	if (!checkEndsWith(finalParamPath, fileExtensions)) {
		return finalParamPath.concat(defaultExtension);
	} else {
		return finalParamPath;
	}
}

private static boolean checkEndsWith(String finalParamPath, String[] fileExtensions) {
	for(String extension:fileExtensions){
		if(StringUtils.endsWithIgnoreCase(finalParamPath, extension)){
			return true;
		}
	}
	return false;
}

}