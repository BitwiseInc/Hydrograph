package hydrograph.engine.transformation.userfunctions.groupcombine;

import hydrograph.engine.transformation.schema.DataType;
import hydrograph.engine.transformation.schema.Field;
import hydrograph.engine.transformation.schema.Schema;
import hydrograph.engine.transformation.userfunctions.base.GroupCombineTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;
/**
 * The Class StringAppend.
 *
 * @author Bitwise
 *
 */
public class StringAppend implements GroupCombineTransformBase {

    @Override
    public Schema initBufferSchema(Schema inputSchema, Schema outputSchema) {
        Field append = new Field.Builder("stringAppend", DataType.String).build();
        Schema schema = new Schema();
        schema.addField(append);
        return schema;
    }

    @Override
    public void initialize(ReusableRow bufferRow) {
        bufferRow.setField(0, "");
    }

    @Override
    public void update(ReusableRow bufferRow, ReusableRow inputRow) {
        bufferRow.setField(0,  bufferRow.getString(0) + inputRow.getString(0));
    }

    @Override
    public void merge(ReusableRow bufferRow1, ReusableRow bufferRow2) {
        bufferRow1.setField(0,  bufferRow1.getString(0) + bufferRow2.getString(0));
    }

    @Override
    public void evaluate(ReusableRow bufferRow, ReusableRow outRow) {
        outRow.setField(0, bufferRow.getField(0));
    }
}
