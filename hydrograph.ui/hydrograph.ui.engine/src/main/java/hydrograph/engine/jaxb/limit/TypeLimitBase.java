
package hydrograph.engine.jaxb.limit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;
import hydrograph.engine.jaxb.straightpulltypes.Limit;


/**
 * <p>Java class for type-limit-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-limit-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-straight-pull-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/limit}type-limit-in-socket"/>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/limit}type-out-socket"/>
 *         &lt;element name="runtimeProperties" type="{hydrograph/engine/jaxb/commontypes}type-properties" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-limit-base", namespace = "hydrograph/engine/jaxb/limit")
@XmlSeeAlso({
    Limit.class
})
public class TypeLimitBase
    extends TypeStraightPullComponent
{


}
