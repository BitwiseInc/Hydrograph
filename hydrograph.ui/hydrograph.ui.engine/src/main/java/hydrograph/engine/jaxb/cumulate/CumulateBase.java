
package hydrograph.engine.jaxb.cumulate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.operationstypes.Cumulate;


/**
 * <p>Java class for cumulate-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cumulate-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operations-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/cumulate}type-in-socket"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="operation" type="{hydrograph/engine/jaxb/cumulate}type-operation" maxOccurs="unbounded"/>
 *           &lt;element name="expression" type="{hydrograph/engine/jaxb/cumulate}type-transform-expression" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/cumulate}type-out-socket"/>
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
@XmlType(name = "cumulate-base", namespace = "hydrograph/engine/jaxb/cumulate")
@XmlSeeAlso({
    Cumulate.class
})
public class CumulateBase
    extends TypeOperationsComponent
{


}
