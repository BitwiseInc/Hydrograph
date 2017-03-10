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
package hydrograph.engine.cascading.joiners;

import cascading.pipe.joiner.Joiner;
import cascading.pipe.joiner.JoinerClosure;
import cascading.pipe.joiner.LeftJoin;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.Tuples;
import cascading.tuple.util.TupleViews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;

public class HashJoinJoiner implements Joiner {
	/**
	 * 
	 */

	Fields fieldDeclaration;

	Option chosenOption;
	Joiner allJoiner;

	private static final long serialVersionUID = 2662174573324013434L;

	/** Field LOG */
	private static final Logger LOG = LoggerFactory
			.getLogger(HashJoinJoiner.class);

	public HashJoinJoiner() {
		this.chosenOption = Option.first;
	}

	public HashJoinJoiner(Option option) {
		this.chosenOption = option;
		
		//cascading left joiner will perform all option (Cartesian product)
		if (chosenOption == Option.all) {
			allJoiner = new LeftJoin();
		}
	}

	public Iterator<Tuple> getIterator(JoinerClosure closure) {

		if (chosenOption == Option.all) {
			return allJoiner.getIterator(closure);
		} else {
			//only for first and last option
			return new JoinIterator(closure, chosenOption);
		}
	}

	public int numJoins() {
		return -1;
	}

	public static enum Option {
		first, last, all
	}

	public static class JoinIterator implements Iterator<Tuple> {
		final JoinerClosure closure;
		Iterator[] iterators;
		Tuple[] tupleValues;
		final int MAIN_INPUT_INDEX = 0;
		final Option chosenOption;

		TupleBuilder resultBuilder;
		Tuple result = new Tuple(); // will be replaced

		public JoinIterator(JoinerClosure closure, Option option) {
			this.closure = closure;
			this.chosenOption = option;

			LOG.debug("cogrouped size: {}", closure.size());

			init();
		}

		private void setupForFirst() {
			// set the very first tuples from all the iterators except main
			for (int i = 0; i < iterators.length; i++) {
				if (i == MAIN_INPUT_INDEX) {
					continue;// very imp to skip this
				}

				// if there is nothing in an iterator then set it to all
				// nulls(empty tuple)
				tupleValues[i] = iterators[i].hasNext() ? (Tuple) iterators[i]
						.next() : Tuple
						.size(closure.getValueFields()[i].size());
			}
		}

		private void setupForLast() {
			// set the very first tuples from all the iterators except main
			for (int i = 0; i < iterators.length; i++) {
				if (i == MAIN_INPUT_INDEX) {
					continue;// very imp to skip this
				}

				Tuple lastTuple = getLastTuple(iterators[i]);
				// if there is nothing in an iterator then set it to all
				// nulls(empty tuple)
				tupleValues[i] = lastTuple == null ? Tuple.size(closure
						.getValueFields()[i].size()) : lastTuple;
			}
		}

		private Tuple getLastTuple(Iterator<Tuple> iterator) {
			Tuple lastTuple = null;

			while (iterator.hasNext()) {
				lastTuple = iterator.next();
			}

			return lastTuple;
		}

		protected void init() {
			iterators = new Iterator[closure.size()];

			tupleValues = new Tuple[iterators.length];

			for (int i = 0; i < closure.size(); i++)
				iterators[i] = getIterator(i);

			if (chosenOption == Option.first) {
				setupForFirst();
			} else {
				setupForLast();
			}

			boolean isUnknown = false;

			for (Fields fields : closure.getValueFields())
				isUnknown |= fields.isUnknown();

			if (isUnknown)
				resultBuilder = new TupleBuilder() {
					Tuple result = new Tuple(); // is re-used

					@Override
					public Tuple makeResult(Tuple[] tuples) {
						result.clear();

						// flatten the results into one Tuple
						for (Tuple lastValue : tuples)
							result.addAll(lastValue);

						return result;
					}
				};
			else
				resultBuilder = new TupleBuilder() {
					Tuple result;

					{
						// handle self join.
						Fields[] fields = closure.getValueFields();

						if (closure.isSelfJoin()) {
							fields = new Fields[closure.size()];

							Arrays.fill(fields, closure.getValueFields()[0]);
						}

						result = TupleViews.createComposite(fields);
					}

					@Override
					public Tuple makeResult(Tuple[] tuples) {
						return TupleViews.reset(result, tuples);
					}
				};
		}

		protected Iterator getIterator(int i) {
			return closure.getIterator(i);
		}

		public final boolean hasNext() {
			return iterators[MAIN_INPUT_INDEX].hasNext();
		}

		public Tuple next() {

			tupleValues[MAIN_INPUT_INDEX] = (Tuple) iterators[MAIN_INPUT_INDEX]
					.next();

			return makeResult(tupleValues);
		}

		private Tuple makeResult(Tuple[] lastValues) {
			Tuples.asModifiable(result);

			result = resultBuilder.makeResult(lastValues);

			if (LOG.isTraceEnabled())
				LOG.trace("tuple: {}", result.print());

			return result;
		}

		public void remove() {
			// unsupported
		}
	}

	static interface TupleBuilder {
		Tuple makeResult(Tuple[] tuples);
	}
}
