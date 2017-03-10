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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.joinlookupproperty;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.propertywindow.widgets.utility.DragDropOperation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.TableViewer;


public class DragDropLookupImp implements DragDropOperation{

	private List listOfFields;
	private boolean isSingleColumn;
	private TableViewer tableViewer;
	
	public DragDropLookupImp(List listOfFields, boolean isSingleColumn,TableViewer tableViewer){
		super();
		this.listOfFields = listOfFields;
		this.isSingleColumn = isSingleColumn;
		this.tableViewer = tableViewer;
	}
	
	@Override
	public void saveResult(String result) {

			LookupMapProperty property = new LookupMapProperty();
			String[] data=result.split(Pattern.quote("."));
			if(!data[1].isEmpty()){
				Matcher match = Pattern.compile(Constants.REGEX).matcher(data[1]);
				if(match.matches()){
				property.setSource_Field(result);
				property.setOutput_Field(data[1]);
				
				if(!listOfFields.contains(property))
					listOfFields.add(property);
				}
			}
			
		tableViewer.refresh();
	}


}
