
package hydrograph.engine.jaxb.operationstypes;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.join.JoinBase;
import hydrograph.engine.jaxb.join.TypeKeyFields;


/**
 * <p>Java class for join complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="join">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/join}join-base">
 *       &lt;sequence>
 *         &lt;element name="keys" type="{hydrograph/engine/jaxb/join}type-key-fields" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "join", namespace = "hydrograph/engine/jaxb/operationstypes", propOrder = {
    "keys"
})
public class Join
    extends JoinBase
{

    @XmlElement(required = true)
    protected List<TypeKeyFields> keys;

    /**
     * Gets the value of the keys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeKeyFields }
     * 
     * 
     */
    public List<TypeKeyFields> getKeys() {
        if (keys == null) {
            keys = new ArrayList<TypeKeyFields>();
        }
        return this.keys;
    }

}
