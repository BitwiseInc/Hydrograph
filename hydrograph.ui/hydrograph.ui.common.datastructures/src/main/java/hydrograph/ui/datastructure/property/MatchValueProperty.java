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

 
package hydrograph.ui.datastructure.property;

import hydrograph.ui.common.cloneableinterface.IDataStructure;

/**
 * The Class MatchValueProperty
 * Stores Match property for the Lookup component.
 * 
 * @author Bitwise
 *
 */
public class MatchValueProperty implements IDataStructure{

	private Boolean radioButtonSelected;
	private String matchValue;

	public MatchValueProperty() {
		radioButtonSelected = Boolean.FALSE;
	}
	public String getMatchValue() {
		return matchValue;
	}

	public void setMatchValue(String matchValue) {
		this.matchValue = matchValue;
	}

	public Boolean isRadioButtonSelected() {
		return radioButtonSelected;
	}
	 
	public void setRadioButtonSelected(Boolean radioButtonSelected) {
		this.radioButtonSelected = radioButtonSelected;
	}
	
	@Override
	public MatchValueProperty clone() {
		MatchValueProperty matchValueProperty = new MatchValueProperty();
		matchValueProperty.setMatchValue(getMatchValue());
		
		return matchValueProperty;
	}
	@Override
	public String toString() {
		return "MatchValue [matchValue=" + matchValue + "]";
	}
	
	
}
