
package hydrograph.engine.jaxb.ifxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeInputComponent;
import hydrograph.engine.jaxb.inputtypes.XmlFile;


/**
 * <p>Java class for type-input-file-xml-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-input-file-xml-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-input-component">
 *       &lt;sequence>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/ifxml}type-input-xml-out-socket"/>
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
@XmlType(name = "type-input-file-xml-base", namespace = "hydrograph/engine/jaxb/ifxml")
@XmlSeeAlso({
    XmlFile.class
})
public class TypeInputFileXmlBase
    extends TypeInputComponent
{


}
