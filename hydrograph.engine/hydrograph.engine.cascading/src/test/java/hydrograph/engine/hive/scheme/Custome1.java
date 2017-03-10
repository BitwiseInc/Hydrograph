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
package hydrograph.engine.hive.scheme;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

public class Custome1 extends BaseOperation implements Function {

	private Fields fields;

	public Custome1(Fields fields) {
		super(fields);
		this.fields = fields;
	}

	public Custome1() {
	}

	@Override
	public void operate(FlowProcess arg0, FunctionCall args) {

		TupleEntry te = args.getArguments();

		String[] d = te.getString("f").split("\t");

		Tuple t1 = new Tuple();
		t1.add(Integer.parseInt(d[0]));
		t1.add(Integer.parseInt(d[1]));
		// t1.add(new String[] { te.getString("f3"), te.getString("f4") });
		Tuple t2 = new Tuple();
		t2.add(t1);

		TupleEntry tupleEntry = new TupleEntry(fields, t2);
		args.getOutputCollector().add(tupleEntry);
	}

}
