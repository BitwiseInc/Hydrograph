
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.ofmixedscheme.TypeOutputMixedInSocket;
import hydrograph.engine.jaxb.ofxml.TypeOutputXmlInSocket;
import hydrograph.engine.jaxb.ohivetextfile.TypeOutputHiveTextFileDelimitedInSocket;
import hydrograph.engine.jaxb.ojdbcupdate.TypeOutputJdbcupdateOutSocket;
import hydrograph.engine.jaxb.omysql.TypeOutputMysqlOutSocket;
import hydrograph.engine.jaxb.ooracle.TypeOutputOracleInSocket;
import hydrograph.engine.jaxb.oredshift.TypeOutputRedshiftInSocket;
import hydrograph.engine.jaxb.osparkredshift.TypeOutputSparkredshiftInSocket;
import hydrograph.engine.jaxb.otdiscard.TypeOutputInSocketIno;
import hydrograph.engine.jaxb.oteradata.TypeOutputTeradataOutSocket;
import hydrograph.engine.jaxb.otffw.TypeOutputFixedwidthInSocket;
import hydrograph.engine.jaxb.otfs.TypeOutputSequenceInSocket;


/**
 * <p>Java class for type-output-inSocket complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-output-inSocket">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-base-inSocket">
 *       &lt;sequence>
 *         &lt;element name="schema" type="{hydrograph/engine/jaxb/commontypes}type-base-record" minOccurs="0"/>
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
@XmlType(name = "type-output-inSocket", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "schema"
})
@XmlSeeAlso({
    TypeOutputFixedwidthInSocket.class,
    hydrograph.engine.jaxb.otfd.TypeOutputDelimitedInSocket.class,
    TypeOutputSequenceInSocket.class,
    TypeOutputInSocketIno.class,
    hydrograph.engine.jaxb.ofparquet.TypeOutputDelimitedInSocket.class,
    hydrograph.engine.jaxb.ohiveparquet.TypeOutputDelimitedInSocket.class,
    TypeOutputHiveTextFileDelimitedInSocket.class,
    TypeOutputMixedInSocket.class,
    hydrograph.engine.jaxb.ofsubjob.TypeOutputDelimitedInSocket.class,
    TypeOutputMysqlOutSocket.class,
    TypeOutputRedshiftInSocket.class,
    TypeOutputSparkredshiftInSocket.class,
    TypeOutputOracleInSocket.class,
    TypeOutputTeradataOutSocket.class,
    TypeOutputXmlInSocket.class,
    TypeOutputJdbcupdateOutSocket.class
})
public class TypeOutputInSocket
    extends TypeBaseInSocket
{

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
