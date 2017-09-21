
package hydrograph.engine.jaxb.outputtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.ofsubjob.TypeOutputFileDelimitedSubjob;


/**
 * <p>Java class for subjobOutput complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subjobOutput">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ofsubjob}type-output-file-delimited-subjob">
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
@XmlType(name = "subjobOutput", namespace = "hydrograph/engine/jaxb/outputtypes")
public class SubjobOutput
    extends TypeOutputFileDelimitedSubjob
{


}
