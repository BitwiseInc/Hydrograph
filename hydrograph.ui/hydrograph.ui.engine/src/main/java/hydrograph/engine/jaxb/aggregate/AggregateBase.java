
package hydrograph.engine.jaxb.aggregate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.operationstypes.Aggregate;


/**
 * <p>Java class for aggregate-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aggregate-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operations-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/aggregate}type-in-socket"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="operation" type="{hydrograph/engine/jaxb/aggregate}type-operation" maxOccurs="unbounded"/>
 *           &lt;element name="expression" type="{hydrograph/engine/jaxb/aggregate}type-transform-expression" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/aggregate}type-out-socket"/>
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
@XmlType(name = "aggregate-base", namespace = "hydrograph/engine/jaxb/aggregate")
@XmlSeeAlso({
    Aggregate.class
})
public class AggregateBase
    extends TypeOperationsComponent
{


}
