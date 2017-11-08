
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.ElementValueIntegerType;
import hydrograph.engine.jaxb.commontypes.ElementValueStringType;
import hydrograph.engine.jaxb.iteradata.TypeInputTeradataBase;
import hydrograph.engine.jaxb.iteradata.TypePartitionsChoice;


/**
 * <p>Java class for teradata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="teradata">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/iteradata}type-input-teradata-base">
 *       &lt;sequence>
 *         &lt;element name="databaseName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="hostName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="port" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type"/>
 *         &lt;element name="jdbcDriver" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="numPartitions" type="{hydrograph/engine/jaxb/iteradata}type-partitions-choice" minOccurs="0"/>
 *         &lt;element name="fetchSize" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="extraUrlParams" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="tableName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *           &lt;sequence>
 *             &lt;element name="selectQuery" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *             &lt;element name="countQuery" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element name="exportOptions" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="username" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="password" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "teradata", namespace = "hydrograph/engine/jaxb/inputtypes", propOrder = {
    "databaseName",
    "hostName",
    "port",
    "jdbcDriver",
    "numPartitions",
    "fetchSize",
    "extraUrlParams",
    "tableName",
    "selectQuery",
    "countQuery",
    "exportOptions",
    "username",
    "password"
})
public class Teradata
    extends TypeInputTeradataBase
{

    @XmlElement(required = true)
    protected ElementValueStringType databaseName;
    @XmlElement(required = true)
    protected ElementValueStringType hostName;
    @XmlElement(required = true)
    protected ElementValueIntegerType port;
    @XmlElement(required = true)
    protected ElementValueStringType jdbcDriver;
    protected TypePartitionsChoice numPartitions;
    protected ElementValueStringType fetchSize;
    protected ElementValueStringType extraUrlParams;
    protected ElementValueStringType tableName;
    protected ElementValueStringType selectQuery;
    protected ElementValueStringType countQuery;
    @XmlElement(required = true)
    protected ElementValueStringType exportOptions;
    @XmlElement(required = true)
    protected ElementValueStringType username;
    @XmlElement(required = true)
    protected ElementValueStringType password;

    /**
     * Gets the value of the databaseName property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets the value of the databaseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setDatabaseName(ElementValueStringType value) {
        this.databaseName = value;
    }

    /**
     * Gets the value of the hostName property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getHostName() {
        return hostName;
    }

    /**
     * Sets the value of the hostName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setHostName(ElementValueStringType value) {
        this.hostName = value;
    }

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public ElementValueIntegerType getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public void setPort(ElementValueIntegerType value) {
        this.port = value;
    }

    /**
     * Gets the value of the jdbcDriver property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * Sets the value of the jdbcDriver property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setJdbcDriver(ElementValueStringType value) {
        this.jdbcDriver = value;
    }

    /**
     * Gets the value of the numPartitions property.
     * 
     * @return
     *     possible object is
     *     {@link TypePartitionsChoice }
     *     
     */
    public TypePartitionsChoice getNumPartitions() {
        return numPartitions;
    }

    /**
     * Sets the value of the numPartitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypePartitionsChoice }
     *     
     */
    public void setNumPartitions(TypePartitionsChoice value) {
        this.numPartitions = value;
    }

    /**
     * Gets the value of the fetchSize property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getFetchSize() {
        return fetchSize;
    }

    /**
     * Sets the value of the fetchSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setFetchSize(ElementValueStringType value) {
        this.fetchSize = value;
    }

    /**
     * Gets the value of the extraUrlParams property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getExtraUrlParams() {
        return extraUrlParams;
    }

    /**
     * Sets the value of the extraUrlParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setExtraUrlParams(ElementValueStringType value) {
        this.extraUrlParams = value;
    }

    /**
     * Gets the value of the tableName property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getTableName() {
        return tableName;
    }

    /**
     * Sets the value of the tableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setTableName(ElementValueStringType value) {
        this.tableName = value;
    }

    /**
     * Gets the value of the selectQuery property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getSelectQuery() {
        return selectQuery;
    }

    /**
     * Sets the value of the selectQuery property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setSelectQuery(ElementValueStringType value) {
        this.selectQuery = value;
    }

    /**
     * Gets the value of the countQuery property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getCountQuery() {
        return countQuery;
    }

    /**
     * Sets the value of the countQuery property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setCountQuery(ElementValueStringType value) {
        this.countQuery = value;
    }

    /**
     * Gets the value of the exportOptions property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getExportOptions() {
        return exportOptions;
    }

    /**
     * Sets the value of the exportOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setExportOptions(ElementValueStringType value) {
        this.exportOptions = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setUsername(ElementValueStringType value) {
        this.username = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setPassword(ElementValueStringType value) {
        this.password = value;
    }

}
