
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
package hydrograph.engine.jaxb.omysql;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for database-type-value.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="database-type-value">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MySQL"/>
 *     &lt;enumeration value="Teradata"/>
 *     &lt;enumeration value="Oracle"/>
 *     &lt;enumeration value="H2"/>
 *     &lt;enumeration value="Derby"/>
 *     &lt;enumeration value="PostgreSQL"/>
 *     &lt;enumeration value="Redshift "/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "database-type-value", namespace = "hydrograph/engine/jaxb/omysql")
@XmlEnum
public enum DatabaseTypeValue {

    @XmlEnumValue("MySQL")
    MY_SQL("MySQL"),
    @XmlEnumValue("Teradata")
    TERADATA("Teradata"),
    @XmlEnumValue("Oracle")
    ORACLE("Oracle"),
    @XmlEnumValue("H2")
    H_2("H2"),
    @XmlEnumValue("Derby")
    DERBY("Derby"),
    @XmlEnumValue("PostgreSQL")
    POSTGRE_SQL("PostgreSQL"),
    @XmlEnumValue("Redshift ")
    REDSHIFT("Redshift ");
    private final String value;

    DatabaseTypeValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DatabaseTypeValue fromValue(String v) {
        for (DatabaseTypeValue c: DatabaseTypeValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
