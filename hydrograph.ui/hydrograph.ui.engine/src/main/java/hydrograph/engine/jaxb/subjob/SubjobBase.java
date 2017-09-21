
package hydrograph.engine.jaxb.subjob;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.operationstypes.Subjob;


/**
 * <p>Java class for subjob-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subjob-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operations-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/subjob}type-in-socket" maxOccurs="unbounded"/>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/subjob}type-out-socket" maxOccurs="unbounded"/>
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
@XmlType(name = "subjob-base", namespace = "hydrograph/engine/jaxb/subjob")
@XmlSeeAlso({
    Subjob.class
})
public class SubjobBase
    extends TypeOperationsComponent
{


}
