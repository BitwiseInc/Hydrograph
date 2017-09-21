
package hydrograph.engine.jaxb.inputtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.ifsubjob.TypeInputFileDelimitedSubjob;


/**
 * <p>Java class for subjobInput complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subjobInput">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ifsubjob}type-input-file-delimited-subjob">
 *       &lt;sequence>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subjobInput", namespace = "hydrograph/engine/jaxb/inputtypes")
public class SubjobInput
    extends TypeInputFileDelimitedSubjob
{


}
