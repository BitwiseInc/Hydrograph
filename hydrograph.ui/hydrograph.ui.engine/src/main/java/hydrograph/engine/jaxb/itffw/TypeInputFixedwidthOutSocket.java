
package hydrograph.engine.jaxb.itffw;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;


/**
 * <p>Java class for type-input-fixedwidth-out-socket complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-input-fixedwidth-out-socket">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-input-outSocket">
 *       &lt;sequence>
 *         &lt;element name="schema" type="{hydrograph/engine/jaxb/itffw}type-fixedwidth-record"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="out0" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" fixed="out" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-input-fixedwidth-out-socket", namespace = "hydrograph/engine/jaxb/itffw")
public class TypeInputFixedwidthOutSocket
    extends TypeInputOutSocket
{


}
