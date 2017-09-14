
package hydrograph.engine.jaxb.join;

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
@XmlType(name = "types-outSocket-types", namespace = "hydrograph/engine/jaxb/join")
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
