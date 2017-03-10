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
import hydrograph.ui.graph.model.categories.SubjobCategory;

import org.apache.commons.lang.StringUtils;


/**
 * Return sub graph component converter.
 * 
 * @author Bitwise
 * 
 */
public class SubjobComponent extends SubjobCategory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2406782326279531800L;

	@Override
	public String getConverter() {
		String type = (String) this.getProperties().get(Constants.TYPE);
		if (StringUtils.isNotBlank(type)) {
			if (type.equalsIgnoreCase(Constants.INPUT))
				return "hydrograph.ui.engine.converter.impl.InputSubJobConverter";
			if (type.equalsIgnoreCase(Constants.OUTPUT))
				return "hydrograph.ui.engine.converter.impl.OutputSubJobConverter";
			if (type.equalsIgnoreCase(Constants.OPERATION))
				return "hydrograph.ui.engine.converter.impl.OperationSubJobConverter";
		
		}
		return "hydrograph.ui.engine.converter.impl.CommandSubjobConverter";

	}

}
