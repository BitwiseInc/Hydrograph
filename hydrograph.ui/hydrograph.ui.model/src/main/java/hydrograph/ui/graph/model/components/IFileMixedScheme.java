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
 * The Class IFileMixedScheme.
 * 
 * @author Bitwise
 */
public class IFileMixedScheme extends InputCategory{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4982973488164196698L;

	/**
	 * Instantiates a new input fixed width.
	 */
	public IFileMixedScheme(){
		super();
	}

	@Override
	public String getConverter() {
		return "hydrograph.ui.engine.converter.impl.InputFileMixedSchemeConverter";
	}
	
	@Override
	public String getGridRowType() {
		return Constants.MIXEDSCHEMA_GRID_ROW;
	}

}
