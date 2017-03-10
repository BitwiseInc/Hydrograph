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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.mapping.tables.inputtable;

import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TableItem;

import hydrograph.ui.datastructure.property.mapping.InputField;


public class InputFieldColumnLabelProvider extends ColumnLabelProvider{
	
	@Override
	public String getText(Object element) {
		return ((InputField)element).getFieldName();
	}			
		
	@Override
	  public String getToolTipText(Object element) {
		if(((InputField)element).getErrorObject().isHasError()){
			return ((InputField)element).getErrorObject().getErrorMessage();
		}
		return null;
	  }

	  @Override
	  public Point getToolTipShift(Object object) {
	    return new Point(5, 5);
	  }

	  @Override
	  public int getToolTipDisplayDelayTime(Object object) {
	    return 100; // msec
	  }

	  @Override
	  public int getToolTipTimeDisplayed(Object object) {
	    return 5000; // msec
	  }

	  @Override
	public void update(ViewerCell cell) {
		  TableItem item = (TableItem) cell.getItem();
		  
		  Pattern pattern = Pattern.compile("^[a-zA-Z0-9 _]*$");
			
			if (!pattern.matcher(((InputField)cell.getElement()).getFieldName()).matches()) {
				  item.setBackground(cell.getControl().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				  item.setForeground(cell.getControl().getDisplay().getSystemColor(SWT.COLOR_RED));
				  ((InputField)cell.getElement()).getErrorObject().setErrorMessage("Input field should match regular expression- \"^[a-zA-Z0-9_]*$\" ");
				  ((InputField)cell.getElement()).getErrorObject().setHasError(true);
		    }else{
		    	
		    	if(((InputField)cell.getElement()).getFieldName().trim().equalsIgnoreCase("")){
		    		item.setBackground(cell.getControl().getDisplay().getSystemColor(SWT.COLOR_RED));
		    		((InputField)cell.getElement()).getErrorObject().setErrorMessage("Input field should not be empty");
					  ((InputField)cell.getElement()).getErrorObject().setHasError(true);
		    	}else{
					  ((InputField)cell.getElement()).getErrorObject().setHasError(false);
		    	}
		    	
		    	
		    }
		super.update(cell);
	}

	
}
