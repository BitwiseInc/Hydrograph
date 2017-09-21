
package hydrograph.engine.jaxb.operationstypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;


/**
 * <p>Java class for normalize complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="normalize">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-operations-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/normalize}type-in-socket"/>
 *         &lt;choice>
 *           &lt;element name="operation" type="{hydrograph/engine/jaxb/normalize}type-operation"/>
 *           &lt;element name="expression" type="{hydrograph/engine/jaxb/normalize}type-transform-expression" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element name="outputRecordCount" type="{hydrograph/engine/jaxb/commontypes}type-output-record-count" minOccurs="0"/>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/normalize}type-out-socket"/>
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
@XmlType(name = "normalize", namespace = "hydrograph/engine/jaxb/operationstypes")
public class Normalize
    extends TypeOperationsComponent
{


}
