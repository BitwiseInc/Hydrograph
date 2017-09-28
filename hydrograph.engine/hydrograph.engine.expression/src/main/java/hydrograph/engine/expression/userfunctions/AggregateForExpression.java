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

import hydrograph.engine.expression.utils.ExpressionWrapper;
import hydrograph.engine.transformation.userfunctions.base.AggregateTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

/**
 * The Class AggregateForExpression .
 *
 * @author Bitwise
 */
@SuppressWarnings("rawtypes")
public class AggregateForExpression implements AggregateTransformBase {

    private ExpressionWrapper expressionWrapper;
    private Object accumulatorValue;
    private String fieldType;

    public AggregateForExpression() {
    }

    public void setValidationAPI(ExpressionWrapper expressionWrapper) {
        this.expressionWrapper = expressionWrapper;
    }

    public void init(String fieldType) {
        this.fieldType = fieldType.split("\\.")[fieldType.split("\\.").length - 1];
        expressionWrapper.getValidationAPI().init(expressionWrapper.getIntialValueExpression());
        accumulatorValue = valueOf(this.fieldType, expressionWrapper.getValidationAPI().exec(new Object[]{}));
    }

    public void callPrepare(String[] inputFieldNames, String[] inputFieldTypes) {

        String fieldNames[] = new String[inputFieldNames.length + 1];
        String fieldTypes[] = new String[inputFieldTypes.length + 1];
        for (int i = 0; i < inputFieldNames.length; i++) {
            fieldNames[i] = inputFieldNames[i];
            fieldTypes[i] = inputFieldTypes[i];
        }
        fieldNames[inputFieldNames.length] = "_accumulator";
        fieldTypes[inputFieldNames.length] = fieldType;
        expressionWrapper.getValidationAPI().init(fieldNames, fieldTypes);
    }

    @Override
    public void prepare(Properties props, ArrayList<String> inputFields,
                        ArrayList<String> outputFields, ArrayList<String> keyFields) {

    }

    @Override
    public void aggregate(ReusableRow input) {
        Object tuples[] = new Object[input.getFieldNames().size() + 1];
        int i = 0;
        for (; i < input.getFieldNames().size(); i++) {
            tuples[i] = input.getField(i);
        }
        tuples[i] = accumulatorValue;
        try {
            if (accumulatorValue instanceof java.lang.Short)
                accumulatorValue = Short.parseShort(expressionWrapper.getValidationAPI().exec(tuples).toString());
            else
                accumulatorValue = expressionWrapper.getValidationAPI().exec(tuples);
        } catch (Exception e) {
            throw new RuntimeException("Exception in aggregate expression: "
                    + expressionWrapper.getValidationAPI().getExpr() + ".\nRow being processed: "
                    + input.toString(), e);
        }
    }

    @Override
    public void onCompleteGroup(ReusableRow output) {
        output.setField(0, (Comparable) accumulatorValue);

        try {
            accumulatorValue = valueOf(fieldType, expressionWrapper.getValidationAPI().execute(expressionWrapper.getIntialValueExpression()));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception in aggregate initial value expression: "
                            + expressionWrapper.getIntialValueExpression() + ".", e);
        }
    }

    @Override
    public void cleanup() {

    }

    private Object valueOf(String className, Object value) {
        String stringValue = value.toString();
        switch (className) {
            case "Short":
                return Short.valueOf(stringValue);
            case "Integer":
                return Integer.valueOf(stringValue);
            case "Long":
                return Long.valueOf(stringValue);
            case "Float":
                return Float.valueOf(stringValue);
            case "Double":
                return Double.valueOf(stringValue);
            case "BigDecimal":
                return BigDecimal.valueOf((Double) value);
            case "Boolean":
                return Boolean.valueOf(stringValue);
            default:
                return stringValue;
        }
    }
}
