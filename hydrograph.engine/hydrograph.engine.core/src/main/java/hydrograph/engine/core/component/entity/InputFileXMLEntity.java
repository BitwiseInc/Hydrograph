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
package hydrograph.engine.core.component.entity;

import hydrograph.engine.core.component.entity.base.InputOutputEntityBase;

import java.util.Arrays;
/**
 * The Class InputFileXMLEntity.
 *
 * @author Bitwise
 *
 */
public class InputFileXMLEntity extends InputOutputEntityBase {

    private String charset = "UTF-8";
    private String path;
    private String absoluteXPath;
    private String rootTag;
    private String rowTag;
    private boolean safe = false;
    private boolean strict = true;

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAbsoluteXPath() {
        return absoluteXPath;
    }

    public void setAbsoluteXPath(String absoluteXPath) {
        this.absoluteXPath = absoluteXPath;
    }

    public String getRootTag() {
        return rootTag;
    }

    public void setRootTag(String rootTag) {
        this.rootTag = rootTag;
    }

    public String getRowTag() {
        return rowTag;
    }

    public void setRowTag(String rowTag) {
        this.rowTag = rowTag;
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
                "Input file delimited entity info:\n");
        str.append(super.toString());
        str.append("Path: " + path);
        str.append(" | safe: " + safe);
        str.append(" | strict: " + strict);
        str.append(" | charset: " + charset);
        str.append(" | rowTag: " + rowTag);
        str.append(" | rootTag: " + rootTag);
        str.append(" | absoluteXPath: " + absoluteXPath);
        str.append("\nfields: ");
        if (getFieldsList() != null) {
            str.append(Arrays.toString(getFieldsList().toArray()));
        }

        str.append("\nout socket(s): ");
        if (getOutSocketList() != null) {
            str.append(Arrays.toString(getOutSocketList().toArray()));
        }
        return str.toString();
    }
}