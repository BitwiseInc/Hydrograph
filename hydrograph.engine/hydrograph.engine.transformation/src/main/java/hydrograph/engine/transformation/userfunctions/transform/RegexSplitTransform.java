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
package hydrograph.engine.transformation.userfunctions.transform;

import hydrograph.engine.transformation.userfunctions.base.ReusableRow;
import hydrograph.engine.transformation.userfunctions.base.TransformBase;
import hydrograph.engine.transformation.userfunctions.helpers.RegexSplitter;

import java.util.ArrayList;
import java.util.Properties;
/**
 * The Class RegexSplitTransform.
 *
 * @author Bitwise
 *
 */
public class RegexSplitTransform implements TransformBase {

	private RegexSplitter regexSplitter;
	private int maxOutputfields;

	@Override
	public void prepare(Properties props, ArrayList<String> inputFields,
			ArrayList<String> outputFields) {
		if ( props != null ){
			String regex = props.getProperty("regex");
			if (regex == null) {
				throw new RegexNotAvailableException(
						"Property regex is not available for splitting");
			}
			regexSplitter = new RegexSplitter(regex);
			
			maxOutputfields = outputFields.size();
		} else {
			throw new RuntimeException(
					"property 'regex' is missing in the operation RegexSplitTransform.");
		}

	}

	@Override
	public void transform(ReusableRow inputRow, ReusableRow outputRow) {
		outputRow.reset();
		String[] splits = regexSplitter.split(inputRow.getString(0));
		if (splits == null) {
			outputRow.setField(0, null);
			return;
		}

		for (int counter = 0; counter < splits.length; counter++) {
			if (counter >= maxOutputfields) {
				break;
			}
			outputRow.setField(counter, splits[counter]);
		}

	}

	@Override
	public void cleanup() {
		regexSplitter = null;

	}

	private class RegexNotAvailableException extends RuntimeException {

		private static final long serialVersionUID = 2027127198378451848L;

		public RegexNotAvailableException(String msg) {
			super(msg);
		}
	}

}

