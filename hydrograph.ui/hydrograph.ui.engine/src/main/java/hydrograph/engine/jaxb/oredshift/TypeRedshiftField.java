
package hydrograph.engine.jaxb.oredshift;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;


/**
 * <p>Java class for type-redshift-field complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-redshift-field">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-base-field">
 *       &lt;attGroup ref="{hydrograph/engine/jaxb/commontypes}grp-attr-base-field"/>
 *       &lt;attribute name="colDef" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-redshift-field", namespace = "hydrograph/engine/jaxb/oredshift")
public class TypeRedshiftField
    extends TypeBaseField
{


}
