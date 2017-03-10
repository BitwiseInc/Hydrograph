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

public class RecordFilterContext {

	private Object filterHandler;
	private String counterName;
	private Object filterClass;
	
	private static final String COUNTER_GROUP = "com.hydrograph.customgroup";



	public Object getFilterClass() {
		return filterClass;
	}

	public void setFilterClass(Object filterClass) {
		this.filterClass = filterClass;
	}

	public static String getCounterGroup() {
		return COUNTER_GROUP;
	}
	
	public String getCounterName() {
		return counterName;
	}

	public void setCounterName(String counterName) {
		this.counterName = counterName;
	}

	public Object getHandlerContext() {
		return filterHandler;
	}

	public void setHandlerContext(Object prepare) {
		this.filterHandler = prepare;
	}

}
