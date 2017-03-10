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

public class CustomAggregateWithSecondaryKeys implements AggregateTransformBase {

	
	int count = 0, sum = 0;
	@Override
	public void prepare(Properties props, ArrayList<String> inputFields,
			ArrayList<String> outputFields, ArrayList<String> keyFields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aggregate(ReusableRow input) {
		if(count<3){	//get the sum of top 3 records in each group
			sum += input.getInteger("col3");
			count++;
		}
		
	}

	@Override
	public void onCompleteGroup(ReusableRow output) {
		output.setField(0, sum);
		sum = 0;
		count = 0;		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}
