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


public class UniqueSequenceNumberContext {

	private long counter;
	private int noOfSlices;
	private int currentSliceNo;

	public UniqueSequenceNumberContext() {
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}
	
	public int getNoOfSlices() {
		return noOfSlices;
	}

	public void setNumberOfSlices(int noOfSlices) {
		this.noOfSlices = noOfSlices;
	}
	
	public int getCurrentSliceNo() {
		return currentSliceNo;
	}

	public void setCurrentSliceNo(int currentSliceNo) {
		this.currentSliceNo = currentSliceNo;
	}
	

}
