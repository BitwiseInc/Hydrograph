
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for scale-type-list.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="scale-type-list">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="explicit"/>
 *     &lt;enumeration value="implicit"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "scale-type-list", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlEnum
public enum ScaleTypeList {

    @XmlEnumValue("explicit")
    EXPLICIT("explicit"),
    @XmlEnumValue("implicit")
    IMPLICIT("implicit");
    private final String value;

    ScaleTypeList(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScaleTypeList fromValue(String v) {
        for (ScaleTypeList c: ScaleTypeList.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
