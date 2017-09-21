
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for type-sort-order.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="type-sort-order">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="asc"/>
 *     &lt;enumeration value="desc"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "type-sort-order", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlEnum
public enum TypeSortOrder {

    @XmlEnumValue("asc")
    ASC("asc"),
    @XmlEnumValue("desc")
    DESC("desc");
    private final String value;

    TypeSortOrder(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeSortOrder fromValue(String v) {
        for (TypeSortOrder c: TypeSortOrder.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
