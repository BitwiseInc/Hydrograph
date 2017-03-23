package hydrograph.engine.transformation.userfunctions.groupcombine;

import hydrograph.engine.transformation.schema.DataType;
import hydrograph.engine.transformation.schema.Field;
import hydrograph.engine.transformation.schema.Schema;
import hydrograph.engine.transformation.userfunctions.base.GroupCombineTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.math.BigDecimal;
import java.math.BigInteger;
/**
 * The Class Sum.
 *
 * @author Bitwise
 *
 */
public class Sum implements GroupCombineTransformBase {

    @Override
    public Schema initBufferSchema(Schema inputSchema, Schema outputSchema) {
        DataType type = inputSchema.getSchema().values().iterator().next().getFieldType();
        String name = inputSchema.getSchema().values().iterator().next().getFieldName();
        Field sum = new Field.Builder(name, type).build();
        Schema schema = new Schema();
        schema.addField(sum);
        return schema;
    }

    @Override
    public void initialize(ReusableRow bufferRow) {
        bufferRow.setField(0, null);
    }

    @Override
    public void update(ReusableRow bufferRow, ReusableRow inputRow) {
        Number inputValue = (Number) inputRow.getField(0);


        if (bufferRow.getField(0) == null)
            bufferRow.setField(0, (Comparable)inputValue);
        else if (inputValue != null) {

            Number  sumValue = (Number) bufferRow.getField(0);

            if (inputValue instanceof Integer)
                sumValue = (Integer) sumValue + (Integer) inputValue;
            else if (inputValue instanceof BigInteger)
                sumValue = ((BigInteger) sumValue).add((BigInteger) inputValue);
            else if (inputValue instanceof Double)
                sumValue = (Double) sumValue + (Double) inputValue;
            else if (inputValue instanceof Float)
                sumValue = (Float) sumValue + (Float) inputValue;
            else if (inputValue instanceof Long)
                sumValue = (Long) sumValue + (Long) inputValue;
            else if (inputValue instanceof Short)
                sumValue = (Short) sumValue + (Short) inputValue;
            else if (inputValue instanceof BigDecimal) {
                BigDecimal value = new BigDecimal(inputValue.toString());
                sumValue = ((BigDecimal) sumValue).add(value);
            } else
                throw new hydrograph.engine.transformation.userfunctions.aggregate.Sum.AggregateSumException("The datatype of input field "
                        + inputRow.getFieldName(0) + " is "
                        + inputValue.getClass().toString()
                        + " which is not recognized by sum");
            bufferRow.setField(0, (Comparable)sumValue);
        }
    }

    @Override
    public void merge(ReusableRow bufferRow1, ReusableRow bufferRow2) {
        Number inputValue = (Number) bufferRow2.getField(0);


        if (bufferRow1.getField(0) == null)
            bufferRow1.setField(0, (Comparable)inputValue);
        else if (inputValue != null) {

            Number  sumValue = (Number) bufferRow1.getField(0);

            if (inputValue instanceof Integer)
                sumValue = (Integer) sumValue + (Integer) inputValue;
            else if (inputValue instanceof BigInteger)
                sumValue = ((BigInteger) sumValue).add((BigInteger) inputValue);
            else if (inputValue instanceof Double)
                sumValue = (Double) sumValue + (Double) inputValue;
            else if (inputValue instanceof Float)
                sumValue = (Float) sumValue + (Float) inputValue;
            else if (inputValue instanceof Long)
                sumValue = (Long) sumValue + (Long) inputValue;
            else if (inputValue instanceof Short)
                sumValue = (Short) sumValue + (Short) inputValue;
            else if (inputValue instanceof BigDecimal) {
                BigDecimal value = new BigDecimal(inputValue.toString());
                sumValue = ((BigDecimal) sumValue).add(value);
            } else
                throw new hydrograph.engine.transformation.userfunctions.aggregate.Sum.AggregateSumException("The datatype of input field "
                        + bufferRow1.getFieldName(0) + " is "
                        + inputValue.getClass().toString()
                        + " which is not recognized by sum");
            bufferRow1.setField(0, (Comparable)sumValue);
        }

    }

    @Override
    public void evaluate(ReusableRow bufferRow, ReusableRow outRow) {
        outRow.setField(0, bufferRow.getField(0));
    }
}
