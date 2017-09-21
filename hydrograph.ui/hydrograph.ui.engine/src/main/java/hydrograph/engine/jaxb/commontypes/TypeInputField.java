
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.executiontracking.TypeExecutiontrackingOperationInputField;
import hydrograph.engine.jaxb.filter.TypeFilterOperationInputField;
import hydrograph.engine.jaxb.generatesequence.TypePassthroughInputField;
import hydrograph.engine.jaxb.partitionbyexpression.TypePbeOperationInputField;
import hydrograph.engine.jaxb.transform.TypeTransformOperationInputField;


/**
 * <p>Java class for type-input-field complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-input-field">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="inSocketId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-input-field", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlSeeAlso({
    TypeTransformOperationInputField.class,
    hydrograph.engine.jaxb.aggregate.TypeOperationInputField.class,
    hydrograph.engine.jaxb.groupcombine.TypeOperationInputField.class,
    TypeFilterOperationInputField.class,
    TypeExecutiontrackingOperationInputField.class,
    TypePassthroughInputField.class,
    hydrograph.engine.jaxb.cumulate.TypeOperationInputField.class,
    hydrograph.engine.jaxb.normalize.TypeOperationInputField.class,
    TypePbeOperationInputField.class
})
public class TypeInputField {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "inSocketId", required = true)
    protected String inSocketId;

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

    /**
     * Gets the value of the inSocketId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInSocketId() {
        return inSocketId;
    }

    /**
     * Sets the value of the inSocketId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInSocketId(String value) {
        this.inSocketId = value;
    }

}
