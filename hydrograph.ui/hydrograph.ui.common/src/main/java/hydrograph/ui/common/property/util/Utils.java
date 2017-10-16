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

package hydrograph.ui.common.property.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.JoinMappingGrid;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;

/**
 * 
 * Utility class
 * 
 * @author Bitwise
 *
 */
public class Utils {
	
	public static final String PARAMETER_NOT_FOUND = "Parameter is not Found!";
	public static final String OPERATION = "operation";
	public static final String LOOKUP_MAP = "hash_join_map";
	public static final String JOIN_MAP = "join_mapping";
	private Properties jobProps;
	private Map<String, String> paramsMap;
	private String finalParamPath;
	
	public static Utils INSTANCE = new Utils();
	
	private Utils(){
		this.jobProps = new Properties();
	}
	
	/**
	 * 
	 * Checks if component schema is sync with mapping of the component
	 * 
	 * @param componentName
	 * @param componentProperties
	 * @return true if schema is in sync with mapping of the component otherwise false
	 */
	public boolean isMappingAndSchemaAreInSync(String componentName,Map<String, Object> componentProperties) {
		List<String> outputFieldList = getOutputFieldList(componentName,componentProperties);
		
		Schema schema = (Schema) componentProperties.get(Constants.SCHEMA_PROPERTY_NAME);
		
		if(schema == null && outputFieldList == null){
			return true;
		}
		
		if(schema == null || outputFieldList == null){
			return false;
		}
		
		List<String> schemaFieldList = getSchemaFieldList(schema.getGridRow());		
		
		if(schemaFieldList.size()!=outputFieldList.size()){
			return false;
		}
		
		for(int index=0;index<schemaFieldList.size();index++){
			if(!StringUtils.equals(schemaFieldList.get(index), outputFieldList.get(index))){
				return false;
			}
		}
		return true;
	}
	
	private List<String> getSchemaFieldList(List<GridRow> schemaGridRowList) {
		List<String> schemaFieldList = new LinkedList<>();
		
		for(GridRow gridRow: schemaGridRowList){
			schemaFieldList.add(gridRow.getFieldName());
		}
		return schemaFieldList;
	}
	
	private List<String> getOutputFieldList(String componentName,Map<String, Object> componentProperties) {
		List<String> outputFieldList=null;
		if(StringUtils.equalsIgnoreCase(componentName,Constants.JOIN)){
			JoinMappingGrid joinMappingGrid = (JoinMappingGrid) componentProperties.get(JOIN_MAP);
			if(joinMappingGrid == null){
				return null;
			}
			outputFieldList = getOutputFieldsFromJoinMapping(joinMappingGrid);
		}else if(StringUtils.equalsIgnoreCase(componentName,Constants.LOOKUP)){
			LookupMappingGrid lookupMappingGrid = (LookupMappingGrid) componentProperties.get(LOOKUP_MAP);
			if(lookupMappingGrid == null){
				return null;
			}
			outputFieldList = getOutputFieldsFromLookupMapping(lookupMappingGrid);
		}else if(StringUtils.equalsIgnoreCase(Constants.TRANSFORM, componentName) ||
				   StringUtils.equalsIgnoreCase(Constants.AGGREGATE, componentName) ||
				   StringUtils.equalsIgnoreCase(Constants.NORMALIZE, componentName) ||
				   StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE, componentName) ||
				   StringUtils.equalsIgnoreCase(Constants.CUMULATE, componentName)){
			TransformMapping transformMapping = (TransformMapping) componentProperties.get(OPERATION);
			if(transformMapping == null){
				return null;
			}
			outputFieldList = getOutputFieldsFromTransformMapping(transformMapping.getOutputFieldList());
		}
		return outputFieldList;
	}
	
	private List<String> getOutputFieldsFromJoinMapping(
			JoinMappingGrid joinMappingGrid) {
		List<String> lookupMapOutputs = new ArrayList<>();
		for (LookupMapProperty l : joinMappingGrid.getLookupMapProperties()) {
			lookupMapOutputs.add(l.getOutput_Field());
		}
		return lookupMapOutputs;
	}
	
	private List<String> getOutputFieldsFromLookupMapping(
			LookupMappingGrid lookupMappingGrid) {
		List<String> lookupMapOutputs = new ArrayList<>();
		for (LookupMapProperty l : lookupMappingGrid.getLookupMapProperties()) {
			lookupMapOutputs.add(l.getOutput_Field());
		}
		return lookupMapOutputs;
	}
	
	private List<String> getOutputFieldsFromTransformMapping(
			List<FilterProperties> outputFieldList) {
		List<String> outputFields = new ArrayList<>();
		for (FilterProperties fileFilterProperty : outputFieldList) {
			if(!ParameterUtil.isParameter(fileFilterProperty.getPropertyname())&& !StringUtils.isEmpty(fileFilterProperty.getPropertyname())){
			outputFields.add(fileFilterProperty.getPropertyname());
			}	
		}
		return outputFields;
	}
	
	/**
	 * 
	 * loading the properties files
	 */
	public void loadProperties() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page.getActiveEditor().getEditorInput() instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) page.getActiveEditor().getEditorInput();
			List<File> paramNameList = null;
			IFile file = input.getFile();
			IProject activeProject = file.getProject();
			final File globalparamFilesPath = new File(activeProject.getLocation().toString() + "/" + "globalparam");
			final File localParamFilePath = new File(activeProject.getLocation().toString() + "/" + Constants.PARAM_FOLDER);
			File[] files = (File[]) ArrayUtils.addAll(listFilesForFolder(globalparamFilesPath),
					getJobsPropertyFile(localParamFilePath,file));
			if (files != null) {
				paramNameList = Arrays.asList(files);
				getParamMap(paramNameList, null);
			}
		}
	}
	
	
	/**
	 * 
	 * loading the properties files without open jon file
	 * @param jobFile job file path
	 */
	public void loadProperties(IFile jobFile) {
		List<File> paramNameList = null;
		IProject activeProject = jobFile.getProject();
		final File globalparamFilesPath = new File(activeProject.getLocation().toString() + "/" + "globalparam");
		final File localParamFilePath = new File(activeProject.getLocation().toString() + "/" + Constants.PARAM_FOLDER);
		File[] files = (File[]) ArrayUtils.addAll(listFilesForFolder(globalparamFilesPath),
				getJobsPropertyFile(localParamFilePath, jobFile));
		if (files != null) {
			paramNameList = Arrays.asList(files);
			getParamMap(paramNameList, jobFile);
		}
	}
	
		
	 	/**
		 * 
		 * checking the parameter in paramsMap
		 * @param value
		 * @return value of Parameter if found in Map otherwise Parameter not found
		 */
	 public String getParamValue(String value){
		 Optional<String> optional = Optional.of(value);
		 if(jobProps != null && !jobProps.isEmpty() && optional.isPresent() && value.contains("@{")){
			String param = "";
			String[] splitString = value.split("/");
			for(String field : splitString){
				if(field.startsWith("@{")){
					field = StringUtils.substringBetween(field, "@{", "}");
					for (Map.Entry<String, String> entry : paramsMap.entrySet()){
						if(StringUtils.equals(entry.getKey(), field)){
							if(entry.getValue().endsWith("/")){
								param = param == null ? entry.getValue() : param.concat(entry.getValue() + "/");
							}
							param = param == null ? entry.getValue() : param.concat(entry.getValue() + "/");
						}
					}
				}else{
					param += field + "/";
				}
			}
			return getResult(param);
		}
			return PARAMETER_NOT_FOUND;
	}	
	 
	public String getParamValueForRunSql(String parameterValue) {
		if ((jobProps != null && !jobProps.isEmpty()) && (StringUtils.isNotBlank(parameterValue))) {
			for (Entry<Object, Object> entry : jobProps.entrySet()) {
					parameterValue = parameterValue.replace("@{" + entry.getKey() + "}", (String) entry.getValue());
			}
			return parameterValue;
		}
		return PARAMETER_NOT_FOUND;
	}
	 
	public String getParamValueForTextBox(String parameterValue) {
		Optional<String> optional = Optional.of(parameterValue);
		if (jobProps != null && !jobProps.isEmpty() && optional.isPresent() && parameterValue.contains("@{")) {
			Enumeration<?> properties = jobProps.propertyNames();
			while (properties.hasMoreElements()) {
				String key = (String) properties.nextElement();
				String value = jobProps.getProperty(key);
				if (parameterValue.contains(key)) {
					parameterValue = parameterValue.replace("@{" + key + "}", value);
				}
			}

			return parameterValue;
		}
		return PARAMETER_NOT_FOUND;
	}
	
	 /**
	  * The function will remove last char of string.
	 * @param value
	 * @return
	 */
	private String getResult(String value){
		StringBuffer buffer = new StringBuffer();
		if(value.endsWith("/")){
			buffer.append(value);
			buffer = buffer.deleteCharAt(value.lastIndexOf("/"));
		}
		return buffer.toString();
	 }
		
	 	/**
		 * 
		 * get the file Path according to the Parameter value
		 * @param extSchemaPath
		 * @param paramValue
		 * @param extSchemaPathText
		 * @return the file Path according to the Parameter value
		 */
	 public String getParamFilePath(String extSchemaPath, String paramValue, Text extSchemaPathText){
			String remainingString = "";
		    if( checkParameterValue(extSchemaPath)){
		    	if(StringUtils.isNotEmpty(paramValue)){
		    		extSchemaPathText.setToolTipText(paramValue+remainingString);
		    	}else{
		    		extSchemaPathText.setToolTipText(PARAMETER_NOT_FOUND);
		    	}
		    }else if(StringUtils.contains(paramValue, PARAMETER_NOT_FOUND)){
		    	extSchemaPathText.setToolTipText(remainingString);
		    }else{
		    	remainingString = extSchemaPath.substring(extSchemaPath.indexOf("}")+1, extSchemaPath.length());
		    	extSchemaPathText.setToolTipText(paramValue+remainingString);
		   		}
		    	
			return paramValue+remainingString;
		}		
		
	 
	 	/**
			 * 
			 * get the file Path according to the Parameter value
			 * @param extSchemaPath
			 * @param paramValue
			 * @param extSchemaPathText
			 * @return the file Path according to the Parameter value
			 */
		 public String getParamFilePath(String extSchemaPath, String paramValue){
				String remainingString = "";
			     if( !checkParameterValue(extSchemaPath) && StringUtils.contains(paramValue, PARAMETER_NOT_FOUND)){
			    	remainingString = extSchemaPath.substring(extSchemaPath.indexOf("}")+1, extSchemaPath.length());
			   		}
				return paramValue+remainingString;
			}		
			
	 /**
		 * 
		 * get the file Path according to the Parameter value
		 * @param extSchemaPath
		 * @param paramValue
		 * @param extSchemaPathText
		 * @return the file Path according to the Parameter value
		 */
	public String getParamFilePath(String paramValue, StyledText extSchemaPathText) {
		extSchemaPathText.setToolTipText(paramValue);
		return paramValue;
	}
	 
	 private boolean checkParameterValue(String value){
		 boolean isParam = false;
		 String[] splitString = value.split("/");
			for(String field : splitString){
				if(ParameterUtil.isParameter(field)){
					isParam = true;
					break;
				}
			}
		return isParam;
	 }
	 	/**
		 * 
		 * Add MouseMoveListner 
		 * @param extSchemaPathText
		 * @param cursor
		 */
	 public void addMouseMoveListener(Text extSchemaPathText , Cursor cursor){
		 if(ParameterUtil.containsParameter(extSchemaPathText.getText(),'/')||ParameterUtil.containsParameter(extSchemaPathText.getText(),'\\')){
				extSchemaPathText.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 0, 255));	
				extSchemaPathText.setCursor(cursor);
				extSchemaPathText.addMouseMoveListener(getMouseListner(extSchemaPathText));
					}
			else{
				extSchemaPathText.removeMouseMoveListener(getMouseListner(extSchemaPathText));
				extSchemaPathText.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 0, 0));
				extSchemaPathText.setCursor(null);
			}
	 }		

	public void addMouseMoveListener(StyledText extSchemaPathText, Cursor cursor) {
		if (extSchemaPathText.getText().contains("@{")) {
			extSchemaPathText.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry(0, 0, 255));
			extSchemaPathText.setCursor(cursor);
			extSchemaPathText.addMouseMoveListener(getMouseListner(extSchemaPathText));
		} else {
			extSchemaPathText.removeMouseMoveListener(getMouseListner(extSchemaPathText));
			extSchemaPathText.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry(0, 0, 0));
			extSchemaPathText.setCursor(null);
		}
	}
	
	 private void getParamMap(List<File> FileNameList, IFile jobFile){
		 
		 	String activeProjectName = null;
			InputStream reader = null;
			String propFilePath = null;
			if (jobFile == null) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IFileEditorInput input = (IFileEditorInput) page.getActiveEditor().getEditorInput();
				IFile file = input.getFile();
				activeProjectName = file.getProject().getName();
			} else {
				activeProjectName = jobFile.getProject().getName();
			}

			for(File propFileName : FileNameList){
			if (!propFileName.isDirectory()) {
				String fileName = propFileName.getName();
				if (StringUtils.contains(propFileName.toString(), "globalparam")) {
					propFilePath = "/" + activeProjectName + "/globalparam" + "/" + fileName;
				} else {
					propFilePath = "/" + activeProjectName + "/param" + "/" + fileName;
				}
			}
			if (propFilePath != null) {
				IPath propPath = new Path(propFilePath);
				IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(propPath);
				try {
					reader = iFile.getContents();
					jobProps.load(reader);

				} catch (CoreException | IOException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
							"Exception occured while loading build properties from file -\n" + e.getMessage());
				}

				finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}
					}
				}
			}
			
				Enumeration<?> e = jobProps.propertyNames();
				paramsMap = new HashMap<String, String>();
			    while (e.hasMoreElements()){
			        String param = (String) e.nextElement();
			        paramsMap.put(param, jobProps.getProperty(param));
			     }
		    }
		}		 
		 
	 private MouseMoveListener getMouseListner(final Text extSchemaPathText){
			final MouseMoveListener listner = new MouseMoveListener() {
				
				@Override
				public void mouseMove(MouseEvent e) {
					String paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText.getText());
				    finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText.getText(), paramValue, extSchemaPathText);
				    while(ParameterUtil.containsParameter(finalParamPath, '/')){
				    	paramValue = Utils.INSTANCE.getParamValue(extSchemaPathText.getToolTipText());
				    	finalParamPath = Utils.INSTANCE.getParamFilePath(extSchemaPathText.getToolTipText(), paramValue, extSchemaPathText);
			    		}
					}
				};
			return listner;
		}		
	 
	private MouseMoveListener getMouseListner(final StyledText extSchemaPathText) {
		final MouseMoveListener listner = new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				String paramValue = Utils.INSTANCE.getParamValueForRunSql(extSchemaPathText.getText());
				finalParamPath = Utils.INSTANCE.getParamFilePath(paramValue, extSchemaPathText);
			}
		};
		return listner;
	}	
	
	
	 private File[]  listFilesForFolder(final File folder) {
			File[] listofFiles = folder.listFiles();
			return listofFiles;
			}
	 
	private File[] getJobsPropertyFile(File folder, IFile jobFile) {
		File[] files = new File[1];
		String paramFileName = jobFile.getFullPath().removeFileExtension().addFileExtension(Constants.PROPERTIES).toFile().getName();
		IFile paramFile= jobFile.getProject().getFolder(Constants.PARAM_FOLDER).getFile(paramFileName);
		files[0]=paramFile.getFullPath().toFile();
		return files;
	}
	 
}
