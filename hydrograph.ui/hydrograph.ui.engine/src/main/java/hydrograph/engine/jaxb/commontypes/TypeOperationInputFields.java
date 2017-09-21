
package hydrograph.engine.jaxb.commontypes;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.executiontracking.TypeExecutiontrackingOperationInputFields;
import hydrograph.engine.jaxb.filter.TypeFilterOperationInputFields;
import hydrograph.engine.jaxb.partitionbyexpression.TypePbeInputFields;
import hydrograph.engine.jaxb.transform.TypeTransformOperationInputFields;


/**
 * <p>Java class for type-operation-input-fields complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-operation-input-fields">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/commontypes}type-input-field" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-operation-input-fields", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "field"
})
@XmlSeeAlso({
    TypeTransformOperationInputFields.class,
    hydrograph.engine.jaxb.aggregate.TypeOperationInputFields.class,
    hydrograph.engine.jaxb.groupcombine.TypeOperationInputFields.class,
    TypeFilterOperationInputFields.class,
    TypeExecutiontrackingOperationInputFields.class,
    hydrograph.engine.jaxb.cumulate.TypeOperationInputFields.class,
    hydrograph.engine.jaxb.normalize.TypeOperationInputFields.class,
    TypePbeInputFields.class
})
public class TypeOperationInputFields {

    @XmlElement(required = true)
    protected List<TypeInputField> field;

    /**
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeInputField }
     * 
     * 
     */
    public List<TypeInputField> getField() {
        if (field == null) {
            field = new ArrayList<TypeInputField>();
        }
        return this.field;
    }

}
