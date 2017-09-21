
package hydrograph.engine.jaxb.join;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.operationstypes.Join;


/**
 * <p>Java class for join-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="join-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operations-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/join}type-in-socket" maxOccurs="unbounded" minOccurs="2"/>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/join}type-out-socket" maxOccurs="unbounded"/>
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
@XmlType(name = "join-base", namespace = "hydrograph/engine/jaxb/join")
@XmlSeeAlso({
    Join.class
})
public class JoinBase
    extends TypeOperationsComponent
{


}
