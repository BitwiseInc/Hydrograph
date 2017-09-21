
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for field-data-types.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="field-data-types">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="java.lang.String"/>
 *     &lt;enumeration value="java.lang.Integer"/>
 *     &lt;enumeration value="java.lang.Object"/>
 *     &lt;enumeration value="java.lang.Long"/>
 *     &lt;enumeration value="java.lang.Double"/>
 *     &lt;enumeration value="java.lang.Float"/>
 *     &lt;enumeration value="java.lang.Short"/>
 *     &lt;enumeration value="java.lang.Boolean"/>
 *     &lt;enumeration value="java.util.Date"/>
 *     &lt;enumeration value="java.math.BigDecimal"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "field-data-types", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlEnum
public enum FieldDataTypes {

    @XmlEnumValue("java.lang.String")
    JAVA_LANG_STRING("java.lang.String"),
    @XmlEnumValue("java.lang.Integer")
    JAVA_LANG_INTEGER("java.lang.Integer"),
    @XmlEnumValue("java.lang.Object")
    JAVA_LANG_OBJECT("java.lang.Object"),
    @XmlEnumValue("java.lang.Long")
    JAVA_LANG_LONG("java.lang.Long"),
    @XmlEnumValue("java.lang.Double")
    JAVA_LANG_DOUBLE("java.lang.Double"),
    @XmlEnumValue("java.lang.Float")
    JAVA_LANG_FLOAT("java.lang.Float"),
    @XmlEnumValue("java.lang.Short")
    JAVA_LANG_SHORT("java.lang.Short"),
    @XmlEnumValue("java.lang.Boolean")
    JAVA_LANG_BOOLEAN("java.lang.Boolean"),
    @XmlEnumValue("java.util.Date")
    JAVA_UTIL_DATE("java.util.Date"),
    @XmlEnumValue("java.math.BigDecimal")
    JAVA_MATH_BIG_DECIMAL("java.math.BigDecimal");
    private final String value;

    FieldDataTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FieldDataTypes fromValue(String v) {
        for (FieldDataTypes c: FieldDataTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
