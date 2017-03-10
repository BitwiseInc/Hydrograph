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
 * limitations under the License.
 *******************************************************************************/
package hydrograph.engine.spark.components.utils;

import hydrograph.engine.core.component.entity.base.InputOutputEntityBase;
import hydrograph.engine.core.component.entity.elements.SchemaField;

import java.lang.reflect.Type;

/**
 * The Class InputOutputFieldsAndTypesCreator.
 *
 * @author Bitwise
 *
 */
public class InputOutputFieldsAndTypesCreator<T extends InputOutputEntityBase> {

    private String[] fieldNames;
    private String[] fieldDataTypes;
    private int[] fieldLength;
    private int[] fieldScale;
    private int[] fieldPrecision;
    private String[] fieldFormat;
    private String[] fieldScaleType;
    private String[] fieldDefaultValue;
    private String[] fieldToRangeValue;
    private String[] fieldFromRangeValue;
    private String[] fieldLengthOrDelimiter;
    private Type[] typefieldLengthDelimiter;
    private String[] colDef;

    private InputOutputEntityBase inputOutputEntityBase;

    public InputOutputFieldsAndTypesCreator(T entity) {
        inputOutputEntityBase = entity;
        initialize();
    }

    private void initialize() {
        int sizeOfFieldList = inputOutputEntityBase.getFieldsList().size();
        fieldNames = new String[sizeOfFieldList];
        fieldDataTypes = new String[sizeOfFieldList];
        fieldFormat = new String[sizeOfFieldList];
        fieldScale = new int[sizeOfFieldList];
        fieldScaleType = new String[sizeOfFieldList];
        fieldLength = new int[sizeOfFieldList];
        fieldPrecision = new int[sizeOfFieldList];
        fieldDefaultValue = new String[sizeOfFieldList];
        fieldFromRangeValue = new String[sizeOfFieldList];
        fieldToRangeValue = new String[sizeOfFieldList];
        fieldLengthOrDelimiter = new String[sizeOfFieldList];
        colDef = new String[sizeOfFieldList];
        typefieldLengthDelimiter = new Type[sizeOfFieldList];

        for (int i = 0; i < sizeOfFieldList; i++) {
            SchemaField schemaField = inputOutputEntityBase.getFieldsList().get(i);
            fieldNames[i] = schemaField.getFieldName();
            fieldDataTypes[i] = schemaField.getFieldDataType();
            fieldFormat[i] = schemaField.getFieldFormat();
            fieldScale[i] = schemaField.getFieldScale();
            fieldScaleType[i] = schemaField.getFieldScaleType();
            fieldLength[i] = schemaField.getFieldLength();
            fieldPrecision[i] = schemaField.getFieldPrecision();
            fieldDefaultValue[i] = schemaField.getFieldDefaultValue();
            fieldFromRangeValue[i] = schemaField.getFieldFromRangeValue();
            fieldToRangeValue[i] = schemaField.getFieldToRangeValue();
            fieldLengthOrDelimiter[i] = schemaField.getFieldLengthDelimiter();
            typefieldLengthDelimiter[i] = schemaField
                    .getTypeFieldLengthDelimiter();
            colDef[i] = schemaField.getColDef();
        }
    }

    /**
     * This method returns field names
     *
     * @return String[]
     */
    public String[] getFieldNames() {
        return fieldNames;
    }

    /**
     * This method returns field datatypes
     *
     * @return String[]
     */
    public String[] getFieldDataTypes() {
        return fieldDataTypes != null ? fieldDataTypes.clone() : null;
    }

    /**
     * This method returns field format values i.e. if field is of date datatype
     * then there will be date format defined for that field
     *
     * @return String[]
     */
    public String[] getFieldFormat() {
        return fieldFormat != null ? fieldFormat.clone() : null;
    }

    /**
     * This method returns field scale values i.e. if field is of BigDecimal
     * datatype then there will be scale defined for that field
     *
     * @return String[]
     */
    public int[] getFieldScale() {
        return fieldScale != null ? fieldScale.clone() : null;
    }

    /**
     * This method returns field scale type values i.e. if field is of
     * BigDecimal datatype then there will be scale defined for that field
     *
     * @return String[]
     */
    public String[] getFieldScaleType() {
        return fieldScaleType != null ? fieldScaleType.clone() : null;
    }

    /**
     * This method returns field length values.
     *
     * @return int[]
     */
    public int[] getFieldLength() {
        return fieldLength != null ? fieldLength.clone() : null;
    }

    /**
     * This method returns field length values or delimiter used for all fields.
     *
     * @return String[]
     */
    public String[] getFieldLengthOrDelimiter() {
        return fieldLengthOrDelimiter != null ? fieldLengthOrDelimiter.clone() : null;
    }

    /**
     * This method returns type whether it is field length value or delimiter
     * value used for all fields.
     *
     * @return Type[]
     */
    public Type[] getTypeFieldLengthDelimiter() {
        return typefieldLengthDelimiter != null ? typefieldLengthDelimiter.clone() : null;
    }

    /**
     * This method returns field precision values i.e. if field is of BigDecimal
     * datatype then there will be precision defined for that field.
     *
     * @return String[]
     */
    public int[] getFieldPrecision() {
        return fieldPrecision != null ? fieldPrecision.clone() : null;
    }

    /**
     * This method returns field default values.
     *
     * @return String[]
     */
    public String[] getFieldDefaultValue() {
        return fieldDefaultValue != null ? fieldDefaultValue.clone() : null;
    }

    /**
     * This method returns field from-range values.
     *
     * @return String[]
     */
    public String[] getFieldFromRangeValue() {
        return fieldFromRangeValue != null ? fieldFromRangeValue.clone() : null;
    }

    /**
     * This method returns field to-range values.
     *
     * @return String[]
     */
    public String[] getFieldToRangeValue() {
        return fieldToRangeValue != null ? fieldToRangeValue.clone() : null;
    }

    /**
     * This method returns field colDef values.
     *
     * @return String[]
     */
    public String[] getColDef() {
        return colDef;
    }


    /**
     * This method maps the java datatype with hive or parquet specific
     * datatype.
     *
     * @return hive or parquet specific datatype in String[].
     */
    /*public String[] hiveParquetDataTypeMapping(List<SchemaField> fieldList) {
        String[] types = new String[fieldList.size()];
        int i = 0;
        for (SchemaField eachSchemaField : fieldList) {
            HiveParquetDatatypeMapping hiveParquetDataType = HiveParquetDatatypeMapping
                    .valueOf(getTypeNameFromDataType(
                            eachSchemaField.getFieldDataType()).toUpperCase());
            types[i++] = hiveParquetDataType.getMappingType(eachSchemaField,
                    inputOutputEntityBase);
        }
        return types;
    }

    private String getTypeNameFromDataType(String javaDataType) {
        try {
            return Class.forName(javaDataType).getSimpleName();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unsupported data type : " + javaDataType,e);
        }
    }*/
}
