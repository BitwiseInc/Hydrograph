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

package hydrograph.ui.propertywindow.widgets.listeners.grid;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ICellEditorValidator;


/**
 * @author Bitwise
 *
 */
public class ELTCellEditorIsEmptyValidator implements ICellEditorValidator{

	private ControlDecoration fieldEmptyDecorator;
	
	
	public ELTCellEditorIsEmptyValidator(ControlDecoration fieldEmptyDecorator) {
		super();
		this.fieldEmptyDecorator = fieldEmptyDecorator;
	}
	
	
	@Override
	public String isValid(Object value) {
		String selectedGrid=(String) value;
		if(StringUtils.isBlank(selectedGrid)){
			fieldEmptyDecorator.show();
			return "Field should not be empty";
		}else{
			fieldEmptyDecorator.hide();
		}
		
		return null;
	}

}
