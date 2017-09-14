
package hydrograph.engine.jaxb.filter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;


/**
 * <p>Java class for type-filter-operation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-filter-operation">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-transform-operation">
 *       &lt;sequence>
 *         &lt;element name="inputFields" type="{hydrograph/engine/jaxb/filter}type-filter-operation-input-fields" minOccurs="0"/>
 *         &lt;element name="properties" type="{hydrograph/engine/jaxb/commontypes}type-properties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="class" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-filter-operation", namespace = "hydrograph/engine/jaxb/filter")
public class TypeFilterOperation
    extends TypeTransformOperation
{


}
