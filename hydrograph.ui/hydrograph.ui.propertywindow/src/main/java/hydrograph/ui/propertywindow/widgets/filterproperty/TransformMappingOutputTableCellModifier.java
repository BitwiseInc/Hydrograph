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
package hydrograph.ui.propertywindow.widgets.filterproperty;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.TransformDialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;

public class TransformMappingOutputTableCellModifier implements ICellModifier {
	private Viewer viewer;
	private TransformDialog transformDialog;	
	
	public TransformMappingOutputTableCellModifier(Viewer viewer,TransformDialog transformDialog) {
		this.viewer = viewer;
		this.transformDialog=transformDialog;
	}
	@Override
	public boolean canModify(Object element, String property) {
		FilterProperties filterProperties = (FilterProperties) element;
			if(ParameterUtil.isParameter(filterProperties.getPropertyname()))
			{
				filterProperties.setPropertyname
				(StringUtils.replace(StringUtils.replace(filterProperties.getPropertyname(), Constants.PARAMETER_PREFIX, ""),
						Constants.PARAMETER_SUFFIX,""));
				return true;
			}
			if(StringUtils.isBlank(filterProperties.getPropertyname()))
			return true;	
		return false;
	}

	@Override
	public Object getValue(Object element, String property) {
		FilterProperties filter = (FilterProperties) element;
		return filter.getPropertyname();
	}

	@Override
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item)
		{	
			element = ((Item) element).getData();
		}
		    FilterProperties filterProperties = (FilterProperties) element;
		    int indexOfSelectedField= transformDialog.getATMapping().getOutputFieldList().indexOf(filterProperties);
			filterProperties.setPropertyname((String )value);	
			if(indexOfSelectedField==-1)
			transformDialog.getATMapping().getOutputFieldList().add(filterProperties);
			transformDialog.refreshOutputTable();
            transformDialog.showHideValidationMessage();
		 
		viewer.refresh();

	}

}
