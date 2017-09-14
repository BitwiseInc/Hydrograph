
package hydrograph.engine.jaxb.removedups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;
import hydrograph.engine.jaxb.straightpulltypes.RemoveDups;


/**
 * <p>Java class for type-removedups-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-removedups-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-straight-pull-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/commontypes}type-base-inSocket-fixed-in0"/>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/removedups}type-out-socket" maxOccurs="2"/>
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
@XmlType(name = "type-removedups-base", namespace = "hydrograph/engine/jaxb/removedups")
@XmlSeeAlso({
    RemoveDups.class
})
public class TypeRemovedupsBase
    extends TypeStraightPullComponent
{


}
