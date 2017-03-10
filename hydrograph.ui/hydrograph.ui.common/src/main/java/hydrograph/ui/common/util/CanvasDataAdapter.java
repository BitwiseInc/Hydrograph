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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class CanvasDataAdapter used for storing canvas data in XML format.
 * 
 * @author Bitwise
 */
public class CanvasDataAdapter {
	private List<String> parameterList;
	private String canvasData;
	
	/**
	 * Instantiates a new canvas data adapter.
	 * 
	 * @param canvasData
	 *            the canvas data
	 */
	public CanvasDataAdapter(String canvasData){
		this.canvasData = canvasData;
		//parameterList = new ArrayList<>();
		parameterList = new LinkedList<>();
	}
	
	/**
	 * Fetch data.
	 */
	public void fetchData(){
		Pattern parameterPattren = Pattern.compile("@\\{(.*?)\\}");
		Matcher matcher = parameterPattren.matcher(canvasData);
		while (matcher.find()) {
		    String parameter = matcher.group(1);
		    parameterList.add(parameter);
		}
	}
	
	/**
	 * Gets the parameter list.
	 * 
	 * @return the parameter list
	 */
	public List<String> getParameterList(){
		fetchData();
		/*IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
        IPath path = project.getFullPath();*/
		return parameterList;		
	}
	
}
