
package hydrograph.engine.jaxb.itffw;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeInputComponent;
import hydrograph.engine.jaxb.inputtypes.TextFileFixedWidth;


/**
 * <p>Java class for type-fixed-width-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-fixed-width-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-input-component">
 *       &lt;sequence>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/itffw}type-input-fixedwidth-out-socket"/>
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
@XmlType(name = "type-fixed-width-base", namespace = "hydrograph/engine/jaxb/itffw")
@XmlSeeAlso({
    TextFileFixedWidth.class
})
public class TypeFixedWidthBase
    extends TypeInputComponent
{


}
