
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for match_value.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="match_value">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="first"/>
 *     &lt;enumeration value="last"/>
 *     &lt;enumeration value="all"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "match_value", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlEnum
public enum MatchValue {

    @XmlEnumValue("first")
    FIRST("first"),
    @XmlEnumValue("last")
    LAST("last"),
    @XmlEnumValue("all")
    ALL("all");
    private final String value;

    MatchValue(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MatchValue fromValue(String v) {
        for (MatchValue c: MatchValue.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
