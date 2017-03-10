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
import cascading.tuple.TupleEntry;
import hydrograph.engine.cascading.utilities.TupleHelper;

public class CustomFunctionContext {

	private TupleEntry outputTupleEntry;
	private Object handlerContext;

	public CustomFunctionContext(Fields fields) {
		this.setOutputTupleEntry(TupleHelper.initializeTupleEntry(fields));
	}

	public TupleEntry getOutputTupleEntry() {
		return outputTupleEntry;
	}

	private void setOutputTupleEntry(TupleEntry tupleEntry) {
		this.outputTupleEntry = tupleEntry;
	}

	public void setHandlerContext(Object handlerContext) {
		this.handlerContext = handlerContext;

	}

	public Object getHandlerContext() {
		return handlerContext;
	}

}
