package hydrograph.engine.transformation.schema;

import java.util.LinkedHashMap;

public class Schema {

    LinkedHashMap<String, Field> fields = new LinkedHashMap<String, Field>();

    public LinkedHashMap<String, Field> getSchema() {
        return fields;
    }

    public void addField( Field field) {
        fields.put(field.getFieldName(), field);
    }

}
