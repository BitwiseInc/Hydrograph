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
 ******************************************************************************/

package hydrograph.engine.core.component.entity;

import hydrograph.engine.core.component.entity.base.InputOutputEntityBase;
import hydrograph.engine.core.component.entity.elements.FieldFormat;
import hydrograph.engine.core.component.entity.elements.KeyField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class OutputFileExcelEntity.
 *
 * @author Bitwise
 *
 */
public class OutputFileExcelEntity extends InputOutputEntityBase {

    private List<KeyField> keyField;
    private String path;
    private String worksheetName;
    private boolean isColumnAsWorksheetName;
    private String charset;
    private String writeMode;
    private boolean stripLeadingQuote;
    private boolean autoColumnSize;
    private List<FieldFormat> headerFormats = new ArrayList<>();
    private List<FieldFormat> dataFormats = new ArrayList<>();


    public List<FieldFormat> getHeaderFormats() { return headerFormats; }

    public void setHeaderFormats(List<FieldFormat> headerFormats) { this.headerFormats = headerFormats; }

    public List<FieldFormat> getDataFormats() { return dataFormats; }

    public void setDataFormats(List<FieldFormat> dataFormats) { this.dataFormats = dataFormats; }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWorksheetName() {
        return worksheetName;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public boolean isStripLeadingQuote() {
        return stripLeadingQuote;
    }

    public void setStripLeadingQuote(boolean stripLeadingQuote) {
        this.stripLeadingQuote = stripLeadingQuote;
    }

    public boolean isAutoColumnSize() {
        return autoColumnSize;
    }

    public void setAutoColumnSize(boolean autoColumnSize) {
        this.autoColumnSize = autoColumnSize;
    }

    public List<KeyField> getKeyField() {
        return keyField;
    }

    public void setKeyField(List<KeyField> keyField) {
        this.keyField = keyField;
    }


    public boolean isColumnAsWorksheetName() {
        return isColumnAsWorksheetName;
    }

    public void setColumnAsWorksheetName(boolean columnAsWorksheetName) {
        isColumnAsWorksheetName = columnAsWorksheetName;
    }

    /**
     * Returns a string with the values for all the members of this entity
     * object.
     * <p>
     * Use cautiously as this is a very heavy operation.
     *
     * @see hydrograph.engine.core.component.entity.base.AssemblyEntityBase#toString()
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(
                "Output file delimited entity info:\n");
        str.append(super.toString());
        str.append("Path: " + getPath());
        str.append(" | Overwrite: " + getWriteMode());
        str.append(" | stripLeadingQuote: " + isStripLeadingQuote());
        str.append(" | autoColumnSize: " + isAutoColumnSize());

        str.append("\nfields: ");
        if (getFieldsList() != null) {
            str.append(Arrays.toString(getFieldsList().toArray()));
        }
        return str.toString();
    }

}
