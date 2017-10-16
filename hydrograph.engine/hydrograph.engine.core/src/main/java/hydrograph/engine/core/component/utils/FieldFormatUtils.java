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

package hydrograph.engine.core.component.utils;

import hydrograph.engine.core.component.entity.elements.FieldFormat;
import hydrograph.engine.core.component.entity.elements.KeyField;
import hydrograph.engine.jaxb.ofexcel.SortKeyFields;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class FieldFormatUtils.
 *
 * @author Bitwise
 */
public class FieldFormatUtils {
    public static List<FieldFormat> getFormatList(List<hydrograph.engine.jaxb.ofexcel.FieldFormat.Field> list) {
        List<FieldFormat> fieldFormats = new ArrayList<>();
        list.forEach(element -> {
            FieldFormat f = new FieldFormat();
            f.setName(element.getName());
            if (element.getCopyOfFiled() != null)
                f.setProperty(FieldFormatUtils.setPropertyFromCopyOfFiled(list, element.getCopyOfFiled().getFieldName()));
            else
                f.setProperty(FieldFormatUtils.setProperties(element.getProperty()));
            fieldFormats.add(f);
        });
        return fieldFormats;
    }

    private static List<FieldFormat.Property> setPropertyFromCopyOfFiled(List<hydrograph.engine.jaxb.ofexcel.FieldFormat.Field> list, String filedName) {

        return setProperties(getPropertyObject(list, filedName));
    }

    private static List<hydrograph.engine.jaxb.ofexcel.FieldFormat.Field.Property> getPropertyObject(List<hydrograph.engine.jaxb.ofexcel.FieldFormat.Field> list, String filedName) {
        final List<hydrograph.engine.jaxb.ofexcel.FieldFormat.Field.Property>[] properties = new List[]{new ArrayList<>()};
        list.forEach(element -> {
            if (element.getName().equals(filedName)) {
                if (element.getProperty() != null) {
                    properties[0] = element.getProperty();
                } else if (element.getCopyOfFiled() != null) {
                    properties[0] = getPropertyObject(list, filedName);
                }
            }
        });
        return properties[0];
    }

    private static List<FieldFormat.Property> setProperties(List<hydrograph.engine.jaxb.ofexcel.FieldFormat.Field.Property> property) {
        List<FieldFormat.Property> propList = new ArrayList<>();
        property.forEach(props -> {
            FieldFormat.Property p = new FieldFormat.Property();
            p.setName(props.getName());
            p.setType(props.getType());
            p.setValue(props.getValue());
            propList.add(p);
        });
        return propList;
    }

    public static List<KeyField> getKeyField(List<SortKeyFields.Field> KeyFields) {
        List<KeyField> keyFieldList = new ArrayList<>();
        KeyFields.forEach(e-> {
            KeyField keyField=new KeyField();
            keyField.setName(e.getName());
            keyField.setSortOrder(e.getOrder().value());
            keyFieldList.add(keyField);
        });
        return keyFieldList;
    }
}
