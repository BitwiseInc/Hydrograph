
package hydrograph.engine.jaxb.inputtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.ihiveparquet.HivePartitionFieldsType;
import hydrograph.engine.jaxb.ihiveparquet.HivePartitionFilterType;
import hydrograph.engine.jaxb.ihiveparquet.HivePathType;
import hydrograph.engine.jaxb.ihiveparquet.HiveType;
import hydrograph.engine.jaxb.ihiveparquet.TypeInputFileDelimitedBase;


/**
 * <p>Java class for parquetHiveFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="parquetHiveFile">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ihiveparquet}type-input-file-delimited-base">
 *       &lt;sequence>
 *         &lt;element name="partitionKeys" type="{hydrograph/engine/jaxb/ihiveparquet}hive_partition_fields_type" minOccurs="0"/>
 *         &lt;element name="partitionFilter" type="{hydrograph/engine/jaxb/ihiveparquet}hive_partition_filter_type" minOccurs="0"/>
 *         &lt;element name="databaseName" type="{hydrograph/engine/jaxb/ihiveparquet}hive_type"/>
 *         &lt;element name="tableName" type="{hydrograph/engine/jaxb/ihiveparquet}hive_type"/>
 *         &lt;element name="externalTablePath" type="{hydrograph/engine/jaxb/ihiveparquet}hive_path_type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parquetHiveFile", namespace = "hydrograph/engine/jaxb/inputtypes", propOrder = {
    "partitionKeys",
    "partitionFilter",
    "databaseName",
    "tableName",
    "externalTablePath"
})
public class ParquetHiveFile
    extends TypeInputFileDelimitedBase
{

    protected HivePartitionFieldsType partitionKeys;
    protected HivePartitionFilterType partitionFilter;
    @XmlElement(required = true)
    protected HiveType databaseName;
    @XmlElement(required = true)
    protected HiveType tableName;
    protected HivePathType externalTablePath;

    /**
     * Gets the value of the partitionKeys property.
     * 
     * @return
     *     possible object is
     *     {@link HivePartitionFieldsType }
     *     
     */
    public HivePartitionFieldsType getPartitionKeys() {
        return partitionKeys;
    }

    /**
     * Sets the value of the partitionKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link HivePartitionFieldsType }
     *     
     */
    public void setPartitionKeys(HivePartitionFieldsType value) {
        this.partitionKeys = value;
    }

    /**
     * Gets the value of the partitionFilter property.
     * 
     * @return
     *     possible object is
     *     {@link HivePartitionFilterType }
     *     
     */
    public HivePartitionFilterType getPartitionFilter() {
        return partitionFilter;
    }

    /**
     * Sets the value of the partitionFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link HivePartitionFilterType }
     *     
     */
    public void setPartitionFilter(HivePartitionFilterType value) {
        this.partitionFilter = value;
    }

    /**
     * Gets the value of the databaseName property.
     * 
     * @return
     *     possible object is
     *     {@link HiveType }
     *     
     */
    public HiveType getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets the value of the databaseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link HiveType }
     *     
     */
    public void setDatabaseName(HiveType value) {
        this.databaseName = value;
    }

    /**
     * Gets the value of the tableName property.
     * 
     * @return
     *     possible object is
     *     {@link HiveType }
     *     
     */
    public HiveType getTableName() {
        return tableName;
    }

    /**
     * Sets the value of the tableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link HiveType }
     *     
     */
    public void setTableName(HiveType value) {
        this.tableName = value;
    }

    /**
     * Gets the value of the externalTablePath property.
     * 
     * @return
     *     possible object is
     *     {@link HivePathType }
     *     
     */
    public HivePathType getExternalTablePath() {
        return externalTablePath;
    }

    /**
     * Sets the value of the externalTablePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link HivePathType }
     *     
     */
    public void setExternalTablePath(HivePathType value) {
        this.externalTablePath = value;
    }

}
