
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for type-lookup-insocket.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="type-lookup-insocket">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="driver"/>
 *     &lt;enumeration value="lookup"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "type-lookup-insocket", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlEnum
public enum TypeLookupInsocket {

    @XmlEnumValue("driver")
    DRIVER("driver"),
    @XmlEnumValue("lookup")
    LOOKUP("lookup");
    private final String value;

    TypeLookupInsocket(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeLookupInsocket fromValue(String v) {
        for (TypeLookupInsocket c: TypeLookupInsocket.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
