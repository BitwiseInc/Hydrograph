
package hydrograph.engine.jaxb.ihiveparquet;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hive_partition_filter_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hive_partition_filter_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="partitionColumn" type="{hydrograph/engine/jaxb/ihiveparquet}partition_column"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hive_partition_filter_type", namespace = "hydrograph/engine/jaxb/ihiveparquet", propOrder = {
    "partitionColumn"
})
public class HivePartitionFilterType {

    @XmlElement(required = true)
    protected List<PartitionColumn> partitionColumn;

    /**
     * Gets the value of the partitionColumn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partitionColumn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartitionColumn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartitionColumn }
     * 
     * 
     */
    public List<PartitionColumn> getPartitionColumn() {
        if (partitionColumn == null) {
            partitionColumn = new ArrayList<PartitionColumn>();
        }
        return this.partitionColumn;
    }

}
