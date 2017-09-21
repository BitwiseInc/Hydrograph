
package hydrograph.engine.jaxb.oredshift;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeBaseRecord;


/**
 * <p>Java class for type-redshift-record complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-redshift-record">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-base-record">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/oredshift}type-redshift-field"/>
 *         &lt;element name="record" type="{hydrograph/engine/jaxb/oredshift}type-redshift-record"/>
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
@XmlType(name = "type-redshift-record", namespace = "hydrograph/engine/jaxb/oredshift")
public class TypeRedshiftRecord
    extends TypeBaseRecord
{


}
