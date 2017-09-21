
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for keep_value.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="keep_value">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="first"/>
 *     &lt;enumeration value="last"/>
 *     &lt;enumeration value="unique"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "keep_value", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlEnum
public enum KeepValue {

    @XmlEnumValue("first")
    FIRST("first"),
    @XmlEnumValue("last")
    LAST("last"),
    @XmlEnumValue("unique")
    UNIQUE("unique");
    private final String value;

    KeepValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KeepValue fromValue(String v) {
        for (KeepValue c: KeepValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
