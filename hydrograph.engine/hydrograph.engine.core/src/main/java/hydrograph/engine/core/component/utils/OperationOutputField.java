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
package hydrograph.engine.core.component.utils;

import java.io.Serializable;

/**
 * The Class OperationOutputField.
 *
 * @author Bitwise
 *
 */
public class OperationOutputField  implements Serializable{


    private String fieldName;
    private String dataType;
    private String format;
    private int scale;
    private int precision;


    public OperationOutputField(String fieldName, String dataType, String format, int scale, int precision) {

        this.fieldName = fieldName;
        this.dataType = dataType;
        this.format = format;
        this.scale = scale;
        this.precision = precision;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }


    @Override
    public String toString() {
        return "OperationOutputField{" +
                "fieldName='" + fieldName + '\'' +
                ", dataType='" + dataType + '\'' +
                ", format='" + format + '\'' +
                ", scale='" + scale + '\'' +
                ", precision='" + precision + '\'' +
                '}';
    }
}
