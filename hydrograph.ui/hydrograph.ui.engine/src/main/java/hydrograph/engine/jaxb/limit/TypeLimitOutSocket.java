
package hydrograph.engine.jaxb.limit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;


/**
 * <p>Java class for type-limit-out-socket complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-limit-out-socket">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-straight-pull-out-socket">
 *       &lt;sequence>
 *         &lt;element name="copyOfInsocket" type="{hydrograph/engine/jaxb/limit}type-outSocket-as-inSocket-in0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="out0" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" fixed="out" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-limit-out-socket", namespace = "hydrograph/engine/jaxb/limit")
public class TypeLimitOutSocket
    extends TypeStraightPullOutSocket
{


}
