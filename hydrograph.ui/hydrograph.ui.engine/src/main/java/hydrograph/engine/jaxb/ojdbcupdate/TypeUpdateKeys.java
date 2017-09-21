
package hydrograph.engine.jaxb.ojdbcupdate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeKeyFields;


/**
 * <p>Java class for type-update-keys complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-update-keys">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="updateByKeys" type="{hydrograph/engine/jaxb/commontypes}type-key-fields"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-update-keys", namespace = "hydrograph/engine/jaxb/ojdbcupdate", propOrder = {
    "updateByKeys"
})
public class TypeUpdateKeys {

    @XmlElement(required = true)
    protected TypeKeyFields updateByKeys;

    /**
     * Gets the value of the updateByKeys property.
     * 
     * @return
     *     possible object is
     *     {@link TypeKeyFields }
     *     
     */
    public TypeKeyFields getUpdateByKeys() {
        return updateByKeys;
    }

    /**
     * Sets the value of the updateByKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeKeyFields }
     *     
     */
    public void setUpdateByKeys(TypeKeyFields value) {
        this.updateByKeys = value;
    }

}
