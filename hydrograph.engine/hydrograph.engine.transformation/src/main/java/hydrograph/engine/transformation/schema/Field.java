package hydrograph.engine.transformation.schema;
/**
 * The Class Field.
 *
 * @author Bitwise
 */
public class Field {

    String fieldName;
    DataType fieldType;
    String fieldFormat;
    int fieldPrecision;
    int fieldScale;

    public String getFieldName() {
        return fieldName;
    }

    public DataType getFieldType() {
        return fieldType;
    }

    public String getFieldFormat() {
        return fieldFormat;
    }

    public int getFieldPrecision() {
        return fieldPrecision;
    }

    public int getFieldScale() {
        return fieldScale;
    }

    private Field(Builder builder) {
        this.fieldName = builder.fieldName;
        this.fieldType = builder.fieldType;
        this.fieldFormat = builder.fieldFormat;
        this.fieldPrecision = builder.fieldPrecision;
        this.fieldScale = builder.fieldScale;
    }

    public static class Builder {

        String fieldName;
        DataType fieldType;
        String fieldFormat = "";
        int    fieldPrecision;
        int    fieldScale;

        public Builder(String fieldName, DataType fieldType){
            this.fieldName = fieldName;
            this.fieldType = fieldType;
        }

        public Builder addFormat(String fieldFormat){
            this.fieldFormat = fieldFormat;
            return this;
        }

        public Builder addPrecision(int fieldPrecision){
            this.fieldPrecision = fieldPrecision;
            return this;
        }

        public Builder addScale(int fieldScale){
            this.fieldScale = fieldScale;
            return this;
        }

        public Field build(){
            return new Field(this);
        }
    }
}
