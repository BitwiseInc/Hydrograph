
package hydrograph.engine.jaxb.generatesequence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;


/**
 * <p>Java class for type-operation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-operation">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-transform-operation">
 *       &lt;sequence>
 *         &lt;element name="outputFields" type="{hydrograph/engine/jaxb/generatesequence}type-operation-output-field"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-operation", namespace = "hydrograph/engine/jaxb/generatesequence")
public class TypeOperation
    extends TypeTransformOperation
{


}
