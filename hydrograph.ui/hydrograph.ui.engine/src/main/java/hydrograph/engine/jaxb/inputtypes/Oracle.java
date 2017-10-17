
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
import hydrograph.engine.jaxb.ioracle.TypeInputOracleBase;
import hydrograph.engine.jaxb.ioracle.TypePartitionsChoice;


/**
 * <p>Java class for oracle complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="oracle">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ioracle}type-input-oracle-base">
 *       &lt;sequence>
 *         &lt;element name="sid" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="hostName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="port" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type" minOccurs="0"/>
 *         &lt;element name="driverType" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="numPartitions" type="{hydrograph/engine/jaxb/ioracle}type-partitions-choice" minOccurs="0"/>
 *         &lt;element name="fetchSize" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="extraUrlParams" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="userName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="password" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="schemaName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="tableName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *           &lt;sequence>
 *             &lt;element name="selectQuery" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *             &lt;element name="countQuery" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "oracle", namespace = "hydrograph/engine/jaxb/inputtypes", propOrder = {
    "sid",
    "hostName",
    "port",
    "driverType",
    "numPartitions",
    "fetchSize",
    "extraUrlParams",
    "userName",
    "password",
    "schemaName",
    "tableName",
    "selectQuery",
    "countQuery"
})
public class Oracle
    extends TypeInputOracleBase
{

    @XmlElement(required = true)
    protected ElementValueStringType sid;
    @XmlElement(required = true)
    protected ElementValueStringType hostName;
    protected ElementValueIntegerType port;
    @XmlElement(required = true)
    protected ElementValueStringType driverType;
    protected TypePartitionsChoice numPartitions;
    protected ElementValueStringType fetchSize;
    protected ElementValueStringType extraUrlParams;
    @XmlElement(required = true)
    protected ElementValueStringType userName;
    @XmlElement(required = true)
    protected ElementValueStringType password;
    protected ElementValueStringType schemaName;
    protected ElementValueStringType tableName;
    protected ElementValueStringType selectQuery;
    protected ElementValueStringType countQuery;

    /**
     * Gets the value of the sid property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getSid() {
        return sid;
    }

    /**
     * Sets the value of the sid property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setSid(ElementValueStringType value) {
        this.sid = value;
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
     * Gets the value of the driverType property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getDriverType() {
        return driverType;
    }

    /**
     * Sets the value of the driverType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setDriverType(ElementValueStringType value) {
        this.driverType = value;
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
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setUserName(ElementValueStringType value) {
        this.userName = value;
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

    /**
     * Gets the value of the schemaName property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getSchemaName() {
        return schemaName;
    }

    /**
     * Sets the value of the schemaName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setSchemaName(ElementValueStringType value) {
        this.schemaName = value;
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

}
