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
package hydrograph.engine.cascading.assembly.handlers;


import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Buffer;
import cascading.operation.BufferCall;
import cascading.operation.OperationCall;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import hydrograph.engine.cascading.assembly.context.RemoveDupsHandlerContext;
import hydrograph.engine.cascading.assembly.infra.AssemblyCreationException;
import hydrograph.engine.core.constants.Keep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class RemoveDupsHandler extends BaseOperation<RemoveDupsHandlerContext>
		implements Buffer<RemoveDupsHandlerContext> {

	private static final long serialVersionUID = 3236218434040424251L;
	private static Logger LOG = LoggerFactory
			.getLogger(RemoveDupsHandler.class);

	private String linkType;
	private Fields inputFields;
	private Keep keep;

	public RemoveDupsHandler(String socketType, Keep keep, Fields inputFields) {
		super(inputFields.size(), inputFields.append(new Fields("keep")));
		this.linkType = socketType;
		this.inputFields = inputFields;
		this.keep = keep;
		LOG.trace("RemoveDupsHandler object created");
	}

	public String getOutputLinkType() {
		return linkType;
	}

	public Fields getInputFields() {
		return inputFields;
	}

	public Fields getOutputFields() {
		return inputFields;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(FlowProcess flowProcess,
			OperationCall<RemoveDupsHandlerContext> call) {

		RemoveDupsKeep keepImplementation;

		if (keep == Keep.first) {
			keepImplementation = new KeepFirst();
		} else if (keep == Keep.last) {
			keepImplementation = new KeepLast();
		} else if (keep == Keep.unique) {
			keepImplementation = new KeepUnique();
		} else {
			throw new AssemblyCreationException(
					"Unable to create RemoveDupsHandler for unknown keep option "
							+ keep);
		}
		RemoveDupsHandlerContext context = new RemoveDupsHandlerContext(
				keepImplementation);
		call.setContext(context);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void operate(FlowProcess arg0,
			BufferCall<RemoveDupsHandlerContext> call) {
		call.getContext().getKeepImplementation().operate(call);
	}

	public interface RemoveDupsKeep {
		public void operate(BufferCall<RemoveDupsHandlerContext> call);
	}

	class KeepFirst implements RemoveDupsKeep {

		public void operate(BufferCall<RemoveDupsHandlerContext> call) {

			Iterator<TupleEntry> records = call.getArgumentsIterator();

			boolean isFirstRecordProcessed = false;
			while (records.hasNext()) {
				Tuple record = records.next().getTupleCopy();
				record.add(new Fields("keep"));
				if (!isFirstRecordProcessed) {
					record.setBoolean(record.size() - 1, true);
					isFirstRecordProcessed = true;
				} else {
					record.setBoolean(record.size() - 1, false);
				}
				call.getOutputCollector().add(record);
			}
		}
	}

	class KeepLast implements RemoveDupsKeep {

		public void operate(BufferCall<RemoveDupsHandlerContext> call) {
			Iterator<TupleEntry> records = call.getArgumentsIterator();

			while (records.hasNext()) {
				Tuple record = records.next().getTupleCopy();
				record.add(new Fields("keep"));
				if (records.hasNext()) {
					record.setBoolean(record.size() - 1, false);
				} else {
					record.setBoolean(record.size() - 1, true);
				}
				call.getOutputCollector().add(record);
			}
		}
	}

	class KeepUnique implements RemoveDupsKeep {

		public void operate(BufferCall<RemoveDupsHandlerContext> call) {

			Iterator<TupleEntry> records = call.getArgumentsIterator();

			if (records.hasNext()) {
				Tuple firstRecord = records.next().getTupleCopy();
				firstRecord.add(new Fields("keep"));
				if (records.hasNext()) {
					// if there are more records, it means we need to reject all
					firstRecord.setBoolean(firstRecord.size() - 1, false);
					call.getOutputCollector().add(firstRecord);
					while (records.hasNext()) {
						firstRecord = records.next().getTupleCopy();
						firstRecord.add(new Fields("keep"));
						firstRecord.setBoolean(firstRecord.size() - 1, false);
						call.getOutputCollector().add(firstRecord);
					}
				} else {
					firstRecord.setBoolean(firstRecord.size() - 1, true);
					call.getOutputCollector().add(firstRecord);
				}
			}
		}
	}
}