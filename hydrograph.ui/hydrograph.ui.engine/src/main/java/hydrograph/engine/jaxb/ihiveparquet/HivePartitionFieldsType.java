
package hydrograph.engine.jaxb.ihiveparquet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hive_partition_fields_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hive_partition_fields_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/ihiveparquet}partition_field_basic_type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hive_partition_fields_type", namespace = "hydrograph/engine/jaxb/ihiveparquet", propOrder = {
    "field"
})
public class HivePartitionFieldsType {

    @XmlElement(required = true)
    protected PartitionFieldBasicType field;

    /**
     * Gets the value of the field property.
     * 
     * @return
     *     possible object is
     *     {@link PartitionFieldBasicType }
     *     
     */
    public PartitionFieldBasicType getField() {
        return field;
    }

    /**
     * Sets the value of the field property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartitionFieldBasicType }
     *     
     */
    public void setField(PartitionFieldBasicType value) {
        this.field = value;
    }

}
