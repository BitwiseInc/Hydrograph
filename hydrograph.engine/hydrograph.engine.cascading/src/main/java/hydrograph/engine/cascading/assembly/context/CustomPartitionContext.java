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
package hydrograph.engine.cascading.assembly.context;

import cascading.tuple.Fields;
import hydrograph.engine.transformation.userfunctions.base.CustomPartitionExpression;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.ArrayList;

public class CustomPartitionContext {
	private String  currentPartitionId;

	private ReusableRow currentRow;
	private ReusableRow currentKeys;
	private ArrayList<String> keyFields;
	private int numOfPartitions;
	private CustomPartitionExpression partitionLogic;

	public CustomPartitionContext(
			CustomPartitionExpression customPartitionExpression) {
		this.setPartitionLogic(customPartitionExpression);
		keyFields = new ArrayList<String>();
	}

	public ReusableRow getCurrentRow() {
		return currentRow;
	}

	public void setCurrentRow(ReusableRow currentRow) {
		this.currentRow = currentRow;
	}

	public ReusableRow getCurrentKeys() {
		return currentKeys;
	}

	public void setCurrentKeys(ReusableRow currentKeys) {
		this.currentKeys = currentKeys;
	}

	public ArrayList<String> getKeyFields() {
		return keyFields;
	}

	public void setKeyFields(Fields fields) {
		for (int loop = 0; loop < fields.size(); loop++) {
			this.keyFields.add(fields.get(loop).toString());
		}
	}
	public String getCurrentPartitionId() {
		return currentPartitionId;
	}

	public void setCurrentPartitionId(String currentPartitionId) {
		this.currentPartitionId = currentPartitionId;
	}


	
	public boolean isCurrentPipe() {
		return getPartitionLogic().getPartition(getCurrentKeys(),
				getNumOfPartitions()).equals(this.getCurrentPartitionId());
	}

	public int getNumOfPartitions() {
		return numOfPartitions;
	}

	public void setNumOfPartitions(int numOfPartitions) {
		this.numOfPartitions = numOfPartitions;
	}

	public CustomPartitionExpression getPartitionLogic() {
		return partitionLogic;
	}

	public void setPartitionLogic(CustomPartitionExpression partitionLogic) {
		this.partitionLogic = partitionLogic;
	}

}
