
package hydrograph.engine.jaxb.normalize;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for type-transform-expression complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-transform-expression">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-transform-expression">
 *       &lt;sequence>
 *         &lt;element name="inputFields" type="{hydrograph/engine/jaxb/normalize}type-operation-input-fields" minOccurs="0"/>
 *         &lt;element name="outputFields" type="{hydrograph/engine/jaxb/commontypes}type-expression-output-fields" minOccurs="0"/>
 *         &lt;element name="properties" type="{hydrograph/engine/jaxb/commontypes}type-properties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expr" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-transform-expression", namespace = "hydrograph/engine/jaxb/normalize")
public class TypeTransformExpression
    extends hydrograph.engine.jaxb.commontypes.TypeTransformExpression
{


}
