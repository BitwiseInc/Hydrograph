package hydrograph.engine.transformation.userfunctions.groupcombine;

import hydrograph.engine.transformation.schema.DataType;
import hydrograph.engine.transformation.schema.Field;
import hydrograph.engine.transformation.schema.Schema;
import hydrograph.engine.transformation.userfunctions.base.GroupCombineTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;
/**
 * The Class Min.
 *
 * @author Bitwise
 *
 */
public class Min implements GroupCombineTransformBase {

    @Override
    public Schema initBufferSchema(Schema inputSchema, Schema outputSchema) {
        DataType type = inputSchema.getSchema().values().iterator().next().getFieldType();
        Field max = new Field.Builder("min", type).build();
        Schema schema = new Schema();
        schema.addField(max);
        return schema;
    }

    @Override
    public void initialize(ReusableRow bufferRow) {
        bufferRow.setField(0, null);
    }

    @Override
    public void update(ReusableRow bufferRow, ReusableRow inputRow) {
        if (bufferRow.getField(0)==null){
            bufferRow.setField(0,inputRow.getField(0));
        }else{
            if (bufferRow.getField(0).compareTo(inputRow.getField(0)) >0){
                bufferRow.setField(0,inputRow.getField(0));
            }
        }
    }

    @Override
    public void merge(ReusableRow bufferRow1, ReusableRow bufferRow2) {
        if (bufferRow1.getField(0)==null){
            bufferRow1.setField(0,bufferRow2.getField(0));
        }else if (bufferRow2.getField(0)==null) {
        }else{
            if (bufferRow1.getField(0).compareTo(bufferRow2.getField(0)) >0){
                bufferRow1.setField(0,bufferRow2.getField(0));
            }
        }
    }

    @Override
    public void evaluate(ReusableRow bufferRow, ReusableRow outRow) {
        outRow.setField(0, bufferRow.getField(0));
    }}
