
package hydrograph.engine.jaxb.cumulate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for type-operation-input-fields complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-operation-input-fields">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operation-input-fields">
 *       &lt;sequence>
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/cumulate}type-operation-input-field" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-operation-input-fields", namespace = "hydrograph/engine/jaxb/cumulate")
public class TypeOperationInputFields
    extends hydrograph.engine.jaxb.commontypes.TypeOperationInputFields
{


}
