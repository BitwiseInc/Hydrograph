
package hydrograph.engine.jaxb.oteradata;

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
@XmlType(name = "database-type-value", namespace = "hydrograph/engine/jaxb/oteradata")
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
