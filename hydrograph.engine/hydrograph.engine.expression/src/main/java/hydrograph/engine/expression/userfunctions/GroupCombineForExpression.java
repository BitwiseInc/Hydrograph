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
import hydrograph.engine.transformation.schema.DataType;
import hydrograph.engine.transformation.schema.Field;
import hydrograph.engine.transformation.schema.Schema;
import hydrograph.engine.transformation.userfunctions.base.GroupCombineTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.math.BigDecimal;

/**
 * The Class GroupCombineForExpression.
 *
 * @author Bitwise
 */
@SuppressWarnings("rawtypes")
public class GroupCombineForExpression implements GroupCombineTransformBase {

    private ExpressionWrapper expressionWrapperForUpdate;
    private ExpressionWrapper expressionWrapperForMerge;
    private String bufferFieldType;
    private String bufferFieldFormat;
    private int bufferFieldScale;
    private int bufferFieldPrecision;
    private Comparable accumulatorInitialValue;

    public GroupCombineForExpression() {
    }

    public void setValidationAPIForUpateExpression(ExpressionWrapper expressionWrapper) {
        this.expressionWrapperForUpdate = expressionWrapper;
    }

    public void setValidationAPIForMergeExpression(ExpressionWrapper expressionWrapper) {
        this.expressionWrapperForMerge = expressionWrapper;
    }

    public void init(String bufferFieldType, String bufferFieldFormat, int bufferFieldScale, int bufferFieldPrecision) {
        this.bufferFieldType = bufferFieldType.split("\\.")[bufferFieldType.split("\\.").length - 1];
        this.bufferFieldFormat = bufferFieldFormat;
        this.bufferFieldScale = bufferFieldScale;
        this.bufferFieldPrecision = bufferFieldPrecision;
        try {
            expressionWrapperForUpdate.getValidationAPI().init(expressionWrapperForUpdate.getIntialValueExpression());
            accumulatorInitialValue = (Comparable) (valueOf(this.bufferFieldType,expressionWrapperForUpdate.getValidationAPI().exec(new Object[]{})));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception in aggregate initial value expression: "
                            + expressionWrapperForUpdate.getIntialValueExpression() + ".", e);
        }
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

    public void callPrepare(String[] inputFieldNames, String[] inputFieldTypes) {
        String fieldNames[] = new String[inputFieldNames.length + 1];
        String fieldTypes[] = new String[inputFieldTypes.length + 1];
        for (int i = 0; i < inputFieldNames.length; i++) {
            fieldNames[i] = inputFieldNames[i];
            fieldTypes[i] = inputFieldTypes[i];
        }
        fieldNames[inputFieldNames.length] = "_accumulator";
        fieldTypes[inputFieldNames.length] = bufferFieldType;
        expressionWrapperForUpdate.getValidationAPI().init(fieldNames, fieldTypes);
        expressionWrapperForMerge.getValidationAPI().init(new String[]{"_accumulator1", "_accumulator2"}, new String[]{bufferFieldType, bufferFieldType});
    }




    @Override
    public Schema initBufferSchema(Schema inputSchema, Schema outputSchema) {
        Field _accumulator = new Field.Builder("_accumulator", DataType.valueOf(bufferFieldType))
                .addFormat(bufferFieldFormat).addPrecision(bufferFieldPrecision).addScale(bufferFieldScale).build();
        Schema schema = new Schema();
        schema.addField( _accumulator);
        return schema;
    }

    @Override
    public void initialize(ReusableRow bufferRow) {
        bufferRow.setField(0, accumulatorInitialValue);
    }

    @Override
    public void update(ReusableRow bufferRow, ReusableRow inputRow) {
        Comparable tuples[] = new Comparable[inputRow.getFieldNames().size() + 1];
        int i = 0;
        for (; i < inputRow.getFieldNames().size(); i++) {
            tuples[i] = inputRow.getField(i);
        }
        tuples[i] = bufferRow.getField(0);
        try {
            if (bufferFieldType.equals("Short")) {
                bufferRow.setField(0, Short.parseShort(expressionWrapperForUpdate.getValidationAPI().exec(tuples).toString()));
            } else {
                bufferRow.setField(0, (Comparable) expressionWrapperForUpdate.getValidationAPI().exec(tuples));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception in aggregate expression: "
                    + expressionWrapperForUpdate.getValidationAPI().getExpr() + ".\nRow being processed: "
                    + inputRow.toString(), e);
        }
    }

    @Override
    public void merge(ReusableRow bufferRow1, ReusableRow bufferRow2) {
        Comparable tuples[] = new Comparable[2];
        tuples[0] = bufferRow1.getField(0);
        tuples[1] = bufferRow2.getField(0);
        try {
            if (bufferFieldType.equals("Short")) {
                bufferRow1.setField(0, Short.parseShort(expressionWrapperForMerge.getValidationAPI().exec(tuples).toString()));
            } else {
                bufferRow1.setField(0, (Comparable) expressionWrapperForMerge.getValidationAPI().exec(tuples));
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception in merge expression: "
                    + expressionWrapperForMerge.getValidationAPI().getExpr() + ".\nRow being processed: "
                    + bufferRow2.toString(), e);
        }
    }

    @Override
    public void evaluate(ReusableRow bufferRow, ReusableRow outRow) {
        outRow.setField(0, bufferRow.getField(0));
    }
}
