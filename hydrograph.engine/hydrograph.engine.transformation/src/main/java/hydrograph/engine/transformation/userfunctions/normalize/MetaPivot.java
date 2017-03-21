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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.transformation.userfunctions.normalize;

import hydrograph.engine.transformation.userfunctions.base.NormalizeTransformBase;
import hydrograph.engine.transformation.userfunctions.base.OutputDispatcher;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.Properties;
/**
 * The Class MetaPivot.
 *
 * @author Bitwise
 *
 */
public class MetaPivot implements NormalizeTransformBase {

	@Override
	public void prepare(Properties props) {

	}

	@Override
	public void Normalize(ReusableRow inputRow, ReusableRow outputRow,
			OutputDispatcher outputDispatcher) {

		for (String iterable_element : inputRow.getFieldNames()) {
			outputRow.setField(outputRow.getFieldName(0), iterable_element);
			outputRow.setField(outputRow.getFieldName(1), inputRow.getField(iterable_element));
			outputDispatcher.sendOutput();
		}

	}

	@Override
	public void cleanup() {

	}

}
