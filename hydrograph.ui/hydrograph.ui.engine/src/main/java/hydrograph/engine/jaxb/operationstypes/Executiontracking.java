
package hydrograph.engine.jaxb.operationstypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;


/**
 * <p>Java class for executiontracking complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="executiontracking">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operations-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/executiontracking}type-executiontracking-in-socket"/>
 *         &lt;choice>
 *           &lt;element name="operation" type="{hydrograph/engine/jaxb/executiontracking}type-executiontracking-operation"/>
 *           &lt;element name="expression" type="{hydrograph/engine/jaxb/commontypes}type-transform-expression"/>
 *         &lt;/choice>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/executiontracking}type-executiontracking-out-socket" maxOccurs="2"/>
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
@XmlType(name = "executiontracking", namespace = "hydrograph/engine/jaxb/operationstypes")
public class Executiontracking
    extends TypeOperationsComponent
{


}
