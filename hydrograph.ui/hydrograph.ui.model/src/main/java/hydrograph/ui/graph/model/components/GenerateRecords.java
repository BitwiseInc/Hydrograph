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

 
package hydrograph.ui.graph.model.components;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.model.categories.InputCategory;

/**
 * The Class GenerateRecords.
 * 
 * @author Bitwise
 */
public class GenerateRecords extends InputCategory {

	private static final long serialVersionUID = 7890131953714494139L;

	/**
	 * Instantiates a new GenerateRecords component.
	 */
	public GenerateRecords() {
	super();
	}
	
	public String getConverter()
	{
		return "hydrograph.ui.engine.converter.impl.GenerateRecordsConverter";
		
	}
	
	@Override
	public String getGridRowType() {
		return Constants.GENERATE_RECORD_GRID_ROW;
	}
}
