
package hydrograph.engine.jaxb.igr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeBaseRecord;


/**
 * <p>Java class for type-generate-record-record complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-generate-record-record">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-base-record">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/igr}type-generate-record-field"/>
 *         &lt;element name="record" type="{hydrograph/engine/jaxb/igr}type-generate-record-record"/>
 *         &lt;element name="includeExternalSchema" type="{hydrograph/engine/jaxb/commontypes}type-external-schema" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-generate-record-record", namespace = "hydrograph/engine/jaxb/igr")
public class TypeGenerateRecordRecord
    extends TypeBaseRecord
{


}
