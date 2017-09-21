
package hydrograph.engine.jaxb.operationstypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.groupcombine.GroupcombineBase;
import hydrograph.engine.jaxb.groupcombine.TypePrimaryKeyFields;


/**
 * <p>Java class for groupcombine complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="groupcombine">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/groupcombine}groupcombine-base">
 *       &lt;sequence>
 *         &lt;element name="primaryKeys" type="{hydrograph/engine/jaxb/groupcombine}type-primary-key-fields"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "groupcombine", namespace = "hydrograph/engine/jaxb/operationstypes", propOrder = {
    "primaryKeys"
})
public class Groupcombine
    extends GroupcombineBase
{

    @XmlElement(required = true)
    protected TypePrimaryKeyFields primaryKeys;

    /**
     * Gets the value of the primaryKeys property.
     * 
     * @return
     *     possible object is
     *     {@link TypePrimaryKeyFields }
     *     
     */
    public TypePrimaryKeyFields getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * Sets the value of the primaryKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypePrimaryKeyFields }
     *     
     */
    public void setPrimaryKeys(TypePrimaryKeyFields value) {
        this.primaryKeys = value;
    }

}
