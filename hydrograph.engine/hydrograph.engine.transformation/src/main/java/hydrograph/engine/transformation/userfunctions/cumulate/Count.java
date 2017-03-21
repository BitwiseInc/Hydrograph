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
package hydrograph.engine.transformation.userfunctions.cumulate;

import hydrograph.engine.transformation.userfunctions.base.CumulateTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.ArrayList;
import java.util.Properties;
/**
 * The Class Count.
 *
 * @author Bitwise
 *
 */
public class Count implements CumulateTransformBase {

	private Long count;
	private Long zero = Long.valueOf(0);

	@Override
	public void prepare(Properties props, ArrayList<String> inputFields,
			ArrayList<String> outputFields, ArrayList<String> keyFields) {
		count = zero;

	}

	@Override
	public void cumulate(ReusableRow input, ReusableRow output) {
		if (input.getField(0) != null)
			count = count + 1;

		output.setField(0, count);
	}

	@Override
	public void onCompleteGroup() {
		count = zero;

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}
