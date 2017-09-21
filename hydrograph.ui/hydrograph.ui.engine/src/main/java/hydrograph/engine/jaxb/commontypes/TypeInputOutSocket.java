
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.exceltype.TypeExcelOutSocket;
import hydrograph.engine.jaxb.ifmixedscheme.TypeInputMixedOutSocket;
import hydrograph.engine.jaxb.ifxml.TypeInputXmlOutSocket;
import hydrograph.engine.jaxb.igr.TypeGenerateRecordOutSocket;
import hydrograph.engine.jaxb.ihivetextfile.TypeInputHiveTextDelimitedOutSocket;
import hydrograph.engine.jaxb.imysql.TypeInputMysqlOutSocket;
import hydrograph.engine.jaxb.ioracle.TypeInputOracleOutSocket;
import hydrograph.engine.jaxb.iteradata.TypeInputTeradataOutSocket;
import hydrograph.engine.jaxb.itffw.TypeInputFixedwidthOutSocket;
import hydrograph.engine.jaxb.itfs.TypeInputSequenceOutSocket;


/**
 * <p>Java class for type-input-outSocket complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-input-outSocket">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-base-outSocket">
 *       &lt;sequence>
 *         &lt;element name="schema" type="{hydrograph/engine/jaxb/commontypes}type-base-record"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-input-outSocket", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "schema"
})
@XmlSeeAlso({
    TypeInputFixedwidthOutSocket.class,
    hydrograph.engine.jaxb.itfd.TypeInputDelimitedOutSocket.class,
    TypeInputSequenceOutSocket.class,
    TypeGenerateRecordOutSocket.class,
    hydrograph.engine.jaxb.ifparquet.TypeInputDelimitedOutSocket.class,
    hydrograph.engine.jaxb.ihiveparquet.TypeInputDelimitedOutSocket.class,
    TypeInputHiveTextDelimitedOutSocket.class,
    TypeInputMixedOutSocket.class,
    hydrograph.engine.jaxb.ifsubjob.TypeInputDelimitedOutSocket.class,
    TypeInputMysqlOutSocket.class,
    hydrograph.engine.jaxb.iredshift.TypeInputRedshiftOutSocket.class,
    hydrograph.engine.jaxb.isparkredshift.TypeInputRedshiftOutSocket.class,
    TypeInputOracleOutSocket.class,
    TypeInputTeradataOutSocket.class,
    TypeInputXmlOutSocket.class,
    TypeExcelOutSocket.class
})
public class TypeInputOutSocket
    extends TypeBaseOutSocket
{

    @XmlElement(required = true)
    protected TypeBaseRecord schema;

    /**
     * Gets the value of the schema property.
     * 
     * @return
     *     possible object is
     *     {@link TypeBaseRecord }
     *     
     */
    public TypeBaseRecord getSchema() {
        return schema;
    }

    /**
     * Sets the value of the schema property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeBaseRecord }
     *     
     */
    public void setSchema(TypeBaseRecord value) {
        this.schema = value;
    }

}
