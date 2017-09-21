
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for type-expression-output-fields complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-expression-output-fields">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/commontypes}type-base-field"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-expression-output-fields", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "field"
})
public class TypeExpressionOutputFields {

    @XmlElement(required = true)
    protected TypeBaseField field;

    /**
     * Gets the value of the field property.
     * 
     * @return
     *     possible object is
     *     {@link TypeBaseField }
     *     
     */
    public TypeBaseField getField() {
        return field;
    }

    /**
     * Sets the value of the field property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeBaseField }
     *     
     */
    public void setField(TypeBaseField value) {
        this.field = value;
    }

}
