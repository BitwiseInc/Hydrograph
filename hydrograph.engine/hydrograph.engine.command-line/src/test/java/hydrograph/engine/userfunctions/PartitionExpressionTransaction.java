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
package hydrograph.engine.userfunctions;

import hydrograph.engine.transformation.userfunctions.base.CustomPartitionExpression;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.io.Serializable;
import java.util.Properties;

/**
 * The Class PartitionExpressionTransaction.
 *
 * @author Bitwise
 *
 */
public class PartitionExpressionTransaction implements CustomPartitionExpression, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void prepare(Properties props) {
		
	}
	
	@Override
	public String getPartition(ReusableRow keys, int numOfPartitions) {
		if (keys.getField(0) == null) {
			return "out_credit";
		}
		if (keys.getString(0).trim().equalsIgnoreCase("credit")) {
			return "out_credit";
		} else if (keys.getString(0).trim().equalsIgnoreCase("debit")) {
			return "out_debit";
		} else {
			return "out_mix";
		}

	}

}
