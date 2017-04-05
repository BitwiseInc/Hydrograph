package hydrograph.engine.transformation.userfunctions.groupcombine;

import hydrograph.engine.transformation.schema.DataType;
import hydrograph.engine.transformation.schema.Field;
import hydrograph.engine.transformation.schema.Schema;
import hydrograph.engine.transformation.userfunctions.base.GroupCombineTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;
/**
 * The Class Count.
 *
 * @author Bitwise
 *
 */
public class Count implements GroupCombineTransformBase {

    @Override
    public Schema initBufferSchema(Schema inputSchema, Schema outputSchema) {
        Field count = new Field.Builder("count", DataType.Long).build();
        Schema schema = new Schema();
        schema.addField(count);
        return schema;
    }

    @Override
    public void initialize(ReusableRow bufferRow) {
        bufferRow.setField(0, 0L);
    }

    @Override
    public void update(ReusableRow bufferRow, ReusableRow inputRow) {
        bufferRow.setField(0, ((Long) bufferRow.getField(0)) + 1L);
    }

    @Override
    public void merge(ReusableRow bufferRow1, ReusableRow bufferRow2) {
        bufferRow1.setField(0, ((Long) bufferRow1.getField(0)) + ((Long) bufferRow2.getField(0)));
    }

    @Override
    public void evaluate(ReusableRow bufferRow, ReusableRow outRow) {
        outRow.setField(0, bufferRow.getField(0));
    }
}
