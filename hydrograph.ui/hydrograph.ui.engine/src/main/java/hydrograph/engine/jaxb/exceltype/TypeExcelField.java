
package hydrograph.engine.jaxb.exceltype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;


/**
 * <p>Java class for type-excel-field complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-excel-field">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-base-field">
 *       &lt;attGroup ref="{hydrograph/engine/jaxb/commontypes}grp-attr-base-field"/>
 *       &lt;attribute name="headerFormat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dataFormat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-excel-field", namespace = "hydrograph/engine/jaxb/exceltype")
public class TypeExcelField
    extends TypeBaseField
{


}
