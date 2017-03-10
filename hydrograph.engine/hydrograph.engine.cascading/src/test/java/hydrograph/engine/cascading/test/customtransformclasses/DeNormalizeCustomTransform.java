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

import hydrograph.engine.transformation.userfunctions.base.AggregateTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.ArrayList;
import java.util.Properties;

public class DeNormalizeCustomTransform implements AggregateTransformBase {

	ArrayList<ArrayList<String>> listOfVectorValues = new ArrayList<ArrayList<String>>();
	Properties userProperties;
	int arrayListIndex;

	@Override
	public void prepare(Properties props, ArrayList<String> inputFields,
			ArrayList<String> outputFields, ArrayList<String> keyFields) {

		this.userProperties = props;
		arrayListIndex = -1;
	}

	@Override
	public void aggregate(ReusableRow inputRow) {
		
		listOfVectorValues.add(new ArrayList<String>());
		
		arrayListIndex++;
		listOfVectorValues.get(arrayListIndex).add(inputRow.getString("name"));
		listOfVectorValues.get(arrayListIndex).add(inputRow.getString("date"));
	}

	@Override
	public void onCompleteGroup(ReusableRow outputRow) {

		for (int i = 0; i < listOfVectorValues.size(); i++) {
			outputRow.setField("name"+i, listOfVectorValues.get(i).get(0));
			outputRow.setField("date"+i, listOfVectorValues.get(i).get(1));
		}

		listOfVectorValues.clear();
		arrayListIndex = -1;
	}

	@Override
	public void cleanup() {


	}

}
