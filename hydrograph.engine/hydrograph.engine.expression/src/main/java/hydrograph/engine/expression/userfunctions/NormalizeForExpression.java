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
import hydrograph.engine.expression.utils.ExpressionWrapper;
import hydrograph.engine.transformation.userfunctions.base.NormalizeTransformBase;
import hydrograph.engine.transformation.userfunctions.base.OutputDispatcher;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 * The Class NormalizeForExpression.
 *
 * @author Bitwise
 */
public class NormalizeForExpression implements NormalizeTransformBase {

	private ExpressionWrapper expressionWrapper;
	private ValidationAPI validationAPIForCountExpre;
	private Object[] tuples;
	private List<String[]> validationAPIFieldNames;
	private List<String[]> outFieldsList;

	public void setValidationAPI(ExpressionWrapper expressionWrapper){
		this.expressionWrapper = expressionWrapper;
	}

	public NormalizeForExpression() {
	}

	public void callPrepare(String[] inputFieldNames,String[] inputFieldTypes){
		try {
			validationAPIForCountExpre = new ValidationAPI(expressionWrapper.getCountExpression(),"");
			validationAPIForCountExpre.init(inputFieldNames,inputFieldTypes);
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception in Normalize Expression: "
							+ validationAPIForCountExpre.getExpr() + ",", e);
		}
	}

	public void initialize(List<String[]> inputFieldNames, List<String[]> inputFieldTypes, List<String[]> outFieldsList){
		List<String[]> listInputFieldNames = new ArrayList<String[]>();
		List<String[]> listInputFieldTypes = new ArrayList<String[]>();
		int i = 0, counter = 0;
		try {
			for(ValidationAPI validationAPI : expressionWrapper.getValidationAPIList()){
				String[] fNames = new String[inputFieldNames.get(i).length + 1];
				String[] fTypes = new String[inputFieldNames.get(i).length + 1];
				for(counter = 0 ; counter < inputFieldNames.get(i).length ; counter++ ){
					fNames[counter] = inputFieldNames.get(i)[counter];
					fTypes[counter] = inputFieldTypes.get(i)[counter];
				}
				fNames[counter] = "_index";
				fTypes[counter] = "Integer";
				listInputFieldNames.add(fNames);
				listInputFieldTypes.add(fTypes);
				validationAPI.init(fNames,fTypes);
				i++;
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception in Normalize Expression: "
							+ validationAPIForCountExpre.getExpr() + ",", e);
		}
		validationAPIFieldNames = listInputFieldNames;
		this.outFieldsList = outFieldsList;
	}

	@Override
	public void prepare(Properties props) {

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void Normalize(ReusableRow inputRow, ReusableRow outputRow,
						  OutputDispatcher outputDispatcher) throws RuntimeException{
		try {
			int inputRowLength = inputRow.getFieldNames().size();
			Object[] tuple = new Object[inputRowLength];
			for(int ctr = 0; ctr < inputRowLength; ctr++){
				tuple[ctr] = inputRow.getField(ctr);
			}
			int exprCount = (int) validationAPIForCountExpre.exec(tuple);
			int i=0,counter=0;
			Integer j= 0;
			for (j = 0; j < exprCount; j++) {
				try {
					for(counter = 0; counter < expressionWrapper.getValidationAPIList().size() ; counter++) {
						String[] fieldNames = validationAPIFieldNames.get(counter);
						tuples = new Object[fieldNames.length];
						for (i = 0; i < fieldNames.length - 1; i++) {
							tuples[i] = inputRow.getField(fieldNames[i]);
						}
						tuples[i] = j;
						Object obj = expressionWrapper.getValidationAPIList().get(counter).exec(tuples);
						outputRow.setField(outFieldsList.get(counter)[0],
								(Comparable) obj);
					}
					outputDispatcher.sendOutput();
				} catch (Exception e) {
					throw new RuntimeException(
							"Exception in normalize expression:[\""
									+ expressionWrapper.getValidationAPIList().get(counter)
									+ "\"]. Error being : "+e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception in normalize expression:[\""
					+ validationAPIForCountExpre.getExpr() + "\"].", e);
		}

	}

	@Override
	public void cleanup() {

	}

}
