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
package hydrograph.engine.expression.userfunctions;

import hydrograph.engine.expression.api.ValidationAPI;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;
import hydrograph.engine.transformation.userfunctions.base.TransformBase;

import java.util.ArrayList;
import java.util.Properties;
/**
 * The Class TransformForExpression.
 *
 * @author Bitwise
 */
@SuppressWarnings("rawtypes")
public class TransformForExpression implements TransformBase {

	ValidationAPI validationAPI;
	Object[] tuples;

	public void setValidationAPI(ValidationAPI validationAPI){
		this.validationAPI = validationAPI;
	}

	public TransformForExpression() {

	}

	public void callPrepare(String[] inputFieldNames,String[] inputFieldTypes){
		try {
			validationAPI.init(inputFieldNames,inputFieldTypes);
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception in Transform Expression: "
							+ validationAPI.getExpr() + ",", e);
		}
	}

	@Override
	public void prepare(Properties props, ArrayList<String> inputFields,
			ArrayList<String> outputFields) {

	}

	@Override
	public void transform(ReusableRow inputRow, ReusableRow outputRow) {
		outputRow.reset();
		tuples = new Object[inputRow.getFields().size()];
		for(int i=0;i<inputRow.getFields().size();i++){
			tuples[i] = inputRow.getField(i);
		}
		try {
			Object output = validationAPI.exec(tuples);
			if(output instanceof java.util.Date) {
				outputRow.setDate(0, (Comparable) output);
			}else {
				outputRow.setField(0, (Comparable) output);
			}
		} catch (Exception e) {
			throw new RuntimeException("Tranform Expression: "
					+ validationAPI.getValidExpression()
					+ ". Row being processed:[\"" + inputRow.toString()+"\"]", e);
		}
	}

	@Override
	public void cleanup() {

	}

}
