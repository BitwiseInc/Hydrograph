
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.sort.TypePrimaryKeyFieldsAttributes;


/**
 * <p>Java class for type-field-name complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-field-name">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-field-name", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlSeeAlso({
    hydrograph.engine.jaxb.removedups.TypeSecondayKeyFieldsAttributes.class,
    hydrograph.engine.jaxb.sort.TypeSecondayKeyFieldsAttributes.class,
    TypePrimaryKeyFieldsAttributes.class,
    hydrograph.engine.jaxb.aggregate.TypeSecondayKeyFieldsAttributes.class,
    hydrograph.engine.jaxb.cumulate.TypeSecondayKeyFieldsAttributes.class
})
public class TypeFieldName {

    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
