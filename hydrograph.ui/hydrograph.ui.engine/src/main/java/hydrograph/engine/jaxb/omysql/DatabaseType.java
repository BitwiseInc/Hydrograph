
package hydrograph.engine.jaxb.omysql;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for database-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="database-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/omysql}database-type-value" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "database-type", namespace = "hydrograph/engine/jaxb/omysql")
public class DatabaseType {

    @XmlAttribute(name = "value", required = true)
    protected DatabaseTypeValue value;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link DatabaseTypeValue }
     *     
     */
    public DatabaseTypeValue getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatabaseTypeValue }
     *     
     */
    public void setValue(DatabaseTypeValue value) {
        this.value = value;
    }

}
