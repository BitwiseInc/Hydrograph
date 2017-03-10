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

import hydrograph.engine.transformation.userfunctions.base.FilterBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.ArrayList;
import java.util.Properties;

public class CustomFilter implements FilterBase {

	private static final int DEFAULT_VALUE=100;
	
	int value;
	
	@Override
	public void prepare(Properties props, ArrayList<String> inputFields) {
		String strVal = props.getProperty("value");
		if (strVal!=null && !strVal.trim().equals("")){
			value=  Integer.parseInt(strVal.trim());
		}else{
			value=DEFAULT_VALUE;
		}
		
	}

	@Override
	public boolean isRemove(ReusableRow reusableRow) {
		long input=reusableRow.getInteger(0);
		//long input= (Long)reusableRow.getField(0);
		return input>value;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}
