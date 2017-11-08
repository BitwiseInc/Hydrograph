
/*
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hydrograph.engine.jaxb.inputtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.BooleanValueType;
import hydrograph.engine.jaxb.ihivetextfile.HivePartitionFieldsType;
import hydrograph.engine.jaxb.ihivetextfile.HivePartitionFilterType;
import hydrograph.engine.jaxb.ihivetextfile.HivePathType;
import hydrograph.engine.jaxb.ihivetextfile.HiveType;
import hydrograph.engine.jaxb.ihivetextfile.TypeInputHiveTextFileDelimitedBase;


/**
 * <p>Java class for hiveTextFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hiveTextFile">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ihivetextfile}type-input-hive-text-file-delimited-base">
 *       &lt;sequence>
 *         &lt;element name="partitionKeys" type="{hydrograph/engine/jaxb/ihivetextfile}hive_partition_fields_type" minOccurs="0"/>
 *         &lt;element name="partitionFilter" type="{hydrograph/engine/jaxb/ihivetextfile}hive_partition_filter_type" minOccurs="0"/>
 *         &lt;element name="delimiter" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="databaseName" type="{hydrograph/engine/jaxb/ihivetextfile}hive_type"/>
 *         &lt;element name="tableName" type="{hydrograph/engine/jaxb/ihivetextfile}hive_type"/>
 *         &lt;element name="externalTablePath" type="{hydrograph/engine/jaxb/ihivetextfile}hive_path_type" minOccurs="0"/>
 *         &lt;element name="quote" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="safe" type="{hydrograph/engine/jaxb/commontypes}boolean-value-type" minOccurs="0"/>
 *         &lt;element name="strict" type="{hydrograph/engine/jaxb/commontypes}boolean-value-type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hiveTextFile", namespace = "hydrograph/engine/jaxb/inputtypes", propOrder = {
    "partitionKeys",
    "partitionFilter",
    "delimiter",
    "databaseName",
    "tableName",
    "externalTablePath",
    "quote",
    "safe",
    "strict"
})
public class HiveTextFile
    extends TypeInputHiveTextFileDelimitedBase
{

    protected HivePartitionFieldsType partitionKeys;
    protected HivePartitionFilterType partitionFilter;
    protected HiveTextFile.Delimiter delimiter;
    @XmlElement(required = true)
    protected HiveType databaseName;
    @XmlElement(required = true)
    protected HiveType tableName;
    protected HivePathType externalTablePath;
    protected HiveTextFile.Quote quote;
    protected BooleanValueType safe;
    protected BooleanValueType strict;

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
     * Gets the value of the delimiter property.
     * 
     * @return
     *     possible object is
     *     {@link HiveTextFile.Delimiter }
     *     
     */
    public HiveTextFile.Delimiter getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the value of the delimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link HiveTextFile.Delimiter }
     *     
     */
    public void setDelimiter(HiveTextFile.Delimiter value) {
        this.delimiter = value;
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

    /**
     * Gets the value of the quote property.
     * 
     * @return
     *     possible object is
     *     {@link HiveTextFile.Quote }
     *     
     */
    public HiveTextFile.Quote getQuote() {
        return quote;
    }

    /**
     * Sets the value of the quote property.
     * 
     * @param value
     *     allowed object is
     *     {@link HiveTextFile.Quote }
     *     
     */
    public void setQuote(HiveTextFile.Quote value) {
        this.quote = value;
    }

    /**
     * Gets the value of the safe property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanValueType }
     *     
     */
    public BooleanValueType getSafe() {
        return safe;
    }

    /**
     * Sets the value of the safe property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanValueType }
     *     
     */
    public void setSafe(BooleanValueType value) {
        this.safe = value;
    }

    /**
     * Gets the value of the strict property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanValueType }
     *     
     */
    public BooleanValueType getStrict() {
        return strict;
    }

    /**
     * Sets the value of the strict property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanValueType }
     *     
     */
    public void setStrict(BooleanValueType value) {
        this.strict = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Delimiter {

        @XmlAttribute(name = "value", required = true)
        protected String value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Quote {

        @XmlAttribute(name = "value")
        protected String value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

}
