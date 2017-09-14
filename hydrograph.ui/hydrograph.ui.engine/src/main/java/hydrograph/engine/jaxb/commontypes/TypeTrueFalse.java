
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for type-true-false complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-true-false">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="value" type="{hydrograph/engine/jaxb/commontypes}true_false" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-true-false", namespace = "hydrograph/engine/jaxb/commontypes")
public class TypeTrueFalse {

    @XmlAttribute(name = "value")
    protected TrueFalse value;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link TrueFalse }
     *     
     */
    public TrueFalse getValue() {
        if (value == null) {
            return TrueFalse.TRUE;
        } else {
            return value;
        }
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrueFalse }
     *     
     */
    public void setValue(TrueFalse value) {
        this.value = value;
    }

}
