
package hydrograph.engine.jaxb.partitionbyexpression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.operationstypes.PartitionByExpression;


/**
 * <p>Java class for partitionByExpression-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="partitionByExpression-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operations-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/partitionByExpression}type-pbe-in-socket"/>
 *         &lt;choice>
 *           &lt;element name="operation" type="{hydrograph/engine/jaxb/partitionByExpression}type-pbe-operation"/>
 *           &lt;element name="expression" type="{hydrograph/engine/jaxb/commontypes}type-transform-expression"/>
 *         &lt;/choice>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/partitionByExpression}type-pbe-out-socket" maxOccurs="unbounded" minOccurs="2"/>
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
@XmlType(name = "partitionByExpression-base", namespace = "hydrograph/engine/jaxb/partitionByExpression")
@XmlSeeAlso({
    PartitionByExpression.class
})
public class PartitionByExpressionBase
    extends TypeOperationsComponent
{


}
