package hydrograph.engine.transformation.schema;

import java.util.LinkedHashMap;
/**
 * The Class Schema.
 *
 * @author Bitwise
 */
public class Schema {

    LinkedHashMap<String, Field> fields = new LinkedHashMap<String, Field>();

    public LinkedHashMap<String, Field> getSchema() {
        return fields;
    }

    public void addField( Field field) {
        fields.put(field.getFieldName(), field);
    }

}
