
package hydrograph.engine.jaxb.straightpulltypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.sort.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.sort.TypeSecondaryKeyFields;
import hydrograph.engine.jaxb.sort.TypeSortBase;


/**
 * <p>Java class for sort complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sort">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/sort}type-sort-base">
 *       &lt;sequence>
 *         &lt;element name="primaryKeys" type="{hydrograph/engine/jaxb/sort}type-primary-key-fields"/>
 *         &lt;element name="secondaryKeys" type="{hydrograph/engine/jaxb/sort}type-secondary-key-fields" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sort", namespace = "hydrograph/engine/jaxb/straightpulltypes", propOrder = {
    "primaryKeys",
    "secondaryKeys"
})
public class Sort
    extends TypeSortBase
{

    @XmlElement(required = true)
    protected TypePrimaryKeyFields primaryKeys;
    protected TypeSecondaryKeyFields secondaryKeys;

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

    /**
     * Gets the value of the secondaryKeys property.
     * 
     * @return
     *     possible object is
     *     {@link TypeSecondaryKeyFields }
     *     
     */
    public TypeSecondaryKeyFields getSecondaryKeys() {
        return secondaryKeys;
    }

    /**
     * Sets the value of the secondaryKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeSecondaryKeyFields }
     *     
     */
    public void setSecondaryKeys(TypeSecondaryKeyFields value) {
        this.secondaryKeys = value;
    }

}
