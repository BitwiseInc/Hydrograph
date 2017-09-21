
package hydrograph.engine.jaxb.straightpulltypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;


/**
 * <p>Java class for unionAll complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="unionAll">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-straight-pull-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/commontypes}type-base-inSocket" maxOccurs="unbounded" minOccurs="2"/>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/commontypes}type-straight-pull-out-socket"/>
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
@XmlType(name = "unionAll", namespace = "hydrograph/engine/jaxb/straightpulltypes")
public class UnionAll
    extends TypeStraightPullComponent
{


}
