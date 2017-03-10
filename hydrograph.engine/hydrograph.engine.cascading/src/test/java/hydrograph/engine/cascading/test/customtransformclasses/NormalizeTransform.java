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
package hydrograph.engine.cascading.test.customtransformclasses;

import hydrograph.engine.transformation.userfunctions.base.NormalizeTransformBase;
import hydrograph.engine.transformation.userfunctions.base.OutputDispatcher;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.Properties;

public class NormalizeTransform implements NormalizeTransformBase {

	private Properties prop;

	@Override
	public void prepare(Properties props) {
		this.prop = props;
	}

	@Override
	public void Normalize(ReusableRow inputRow, ReusableRow outputRow,
			OutputDispatcher dispatcher) {

		for (int i = 1; i <= Integer.parseInt(prop.getProperty("VectorSize")); i++) {
			if (inputRow.getInteger("foo") == 1) {
				outputRow.setField("string", inputRow.getField("s" + i));
				dispatcher.sendOutput();
			}
		}
	}

	@Override
	public void cleanup() {

	}

}
