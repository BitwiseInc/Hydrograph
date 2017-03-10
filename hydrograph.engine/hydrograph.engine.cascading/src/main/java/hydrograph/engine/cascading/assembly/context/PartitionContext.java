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
import cascading.tuple.Tuple;
import hydrograph.engine.cascading.functions.patitioner.PartitionLogic;

public class PartitionContext {
	private int currentPipeIndex;
	private Tuple currentRow;
	private Tuple currentKeys;
	private Fields keyFields;
	private int numOfPartitions;
	private int batchSize;
	private int substractor;
	private PartitionLogic partitionLogic;

	public PartitionContext(PartitionLogic partitionLogic) {
		this.setPartitionLogic(partitionLogic);
	}

	public Tuple getCurrentRow() {
		return currentRow;
	}

	public void setCurrentRow(Tuple currentRow) {
		this.currentRow = currentRow;
	}

	public Tuple getCurrentKeys() {
		return currentKeys;
	}

	public void setCurrentKeys(Tuple currentKeys) {
		this.currentKeys = currentKeys;
	}

	public Fields getKeyFields() {
		return keyFields;
	}

	public void setKeyFields(Fields keyFields) {
		this.keyFields = keyFields;
	}

	public int getCurrentPipeIndex() {
		return currentPipeIndex;
	}

	public void setCurrentPipeIndex(int currentPipeIndex) {
		this.currentPipeIndex = currentPipeIndex;
	}

	public boolean isCurrentPipe() {
		return getPartitionLogic().getPartition(getCurrentKeys(), this) == this
				.getCurrentPipeIndex();
	}

	public int getNumOfPartitions() {
		return numOfPartitions;
	}

	public void setNumOfPartitions(int numOfPartitions) {
		this.numOfPartitions = numOfPartitions;
	}

	public PartitionLogic getPartitionLogic() {
		return partitionLogic;
	}

	public void setPartitionLogic(PartitionLogic partitionLogic) {
		this.partitionLogic = partitionLogic;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void incrementSubstractor() {
		this.substractor++;
	}

	public int getSubstractor() {
		return substractor;
	}

	public void setSubstractor(int substractor) {
		this.substractor = substractor;
	}
}
