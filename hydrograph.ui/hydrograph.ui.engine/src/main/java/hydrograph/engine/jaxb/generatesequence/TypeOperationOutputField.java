
package hydrograph.engine.jaxb.generatesequence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeOperationOutputFields;


/**
 * <p>Java class for type-operation-output-field complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-operation-output-field">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operation-output-fields">
 *       &lt;sequence>
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/generatesequence}type-name-field"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-operation-output-field", namespace = "hydrograph/engine/jaxb/generatesequence")
public class TypeOperationOutputField
    extends TypeOperationOutputFields
{


}
