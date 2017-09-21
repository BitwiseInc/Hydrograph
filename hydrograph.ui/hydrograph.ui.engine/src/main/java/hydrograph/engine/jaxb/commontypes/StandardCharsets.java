
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for standard-charsets.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="standard-charsets">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UTF-8"/>
 *     &lt;enumeration value="US-ASCII"/>
 *     &lt;enumeration value="ISO-8859-1"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "standard-charsets", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlEnum
public enum StandardCharsets {

    @XmlEnumValue("UTF-8")
    UTF_8("UTF-8"),
    @XmlEnumValue("US-ASCII")
    US_ASCII("US-ASCII"),
    @XmlEnumValue("ISO-8859-1")
    ISO_8859_1("ISO-8859-1");
    private final String value;

    StandardCharsets(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StandardCharsets fromValue(String v) {
        for (StandardCharsets c: StandardCharsets.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
