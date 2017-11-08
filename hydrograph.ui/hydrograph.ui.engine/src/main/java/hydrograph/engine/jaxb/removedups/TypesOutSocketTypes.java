
/*
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
 */
package hydrograph.engine.jaxb.removedups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for types-outSocket-types.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="types-outSocket-types">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="out"/>
 *     &lt;enumeration value="unused"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "types-outSocket-types", namespace = "hydrograph/engine/jaxb/removedups")
@XmlEnum
public enum TypesOutSocketTypes {

    @XmlEnumValue("out")
    OUT("out"),
    @XmlEnumValue("unused")
    UNUSED("unused");
    private final String value;

    TypesOutSocketTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypesOutSocketTypes fromValue(String v) {
        for (TypesOutSocketTypes c: TypesOutSocketTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
