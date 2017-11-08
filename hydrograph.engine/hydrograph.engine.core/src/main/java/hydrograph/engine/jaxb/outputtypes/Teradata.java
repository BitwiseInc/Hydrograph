
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
package hydrograph.engine.jaxb.outputtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.ElementValueIntegerType;
import hydrograph.engine.jaxb.commontypes.ElementValueStringType;
import hydrograph.engine.jaxb.oteradata.TypeLoadChoice;
import hydrograph.engine.jaxb.oteradata.TypeOutputTeradataBase;


/**
 * <p>Java class for teradata complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="teradata">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/oteradata}type-output-teradata-base">
 *       &lt;sequence>
 *         &lt;element name="databaseName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="hostName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="port" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type" minOccurs="0"/>
 *         &lt;element name="jdbcDriver" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="tableName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="username" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="password" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="chunkSize" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="extraUrlParams" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="loadUtilityType" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="loadType" type="{hydrograph/engine/jaxb/oteradata}type-load-choice"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "teradata", namespace = "hydrograph/engine/jaxb/outputtypes", propOrder = {
    "databaseName",
    "hostName",
    "port",
    "jdbcDriver",
    "tableName",
    "username",
    "password",
    "chunkSize",
    "extraUrlParams",
    "loadUtilityType",
    "loadType"
})
public class Teradata
    extends TypeOutputTeradataBase
{

    @XmlElement(required = true)
    protected ElementValueStringType databaseName;
    @XmlElement(required = true)
    protected ElementValueStringType hostName;
    protected ElementValueIntegerType port;
    @XmlElement(required = true)
    protected ElementValueStringType jdbcDriver;
    @XmlElement(required = true)
    protected ElementValueStringType tableName;
    @XmlElement(required = true)
    protected ElementValueStringType username;
    @XmlElement(required = true)
    protected ElementValueStringType password;
    protected ElementValueStringType chunkSize;
    protected ElementValueStringType extraUrlParams;
    @XmlElement(required = true)
    protected ElementValueStringType loadUtilityType;
    @XmlElement(required = true)
    protected TypeLoadChoice loadType;

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

    /**
     * Gets the value of the chunkSize property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getChunkSize() {
        return chunkSize;
    }

    /**
     * Sets the value of the chunkSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setChunkSize(ElementValueStringType value) {
        this.chunkSize = value;
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
     * Gets the value of the loadUtilityType property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getLoadUtilityType() {
        return loadUtilityType;
    }

    /**
     * Sets the value of the loadUtilityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setLoadUtilityType(ElementValueStringType value) {
        this.loadUtilityType = value;
    }

    /**
     * Gets the value of the loadType property.
     * 
     * @return
     *     possible object is
     *     {@link TypeLoadChoice }
     *     
     */
    public TypeLoadChoice getLoadType() {
        return loadType;
    }

    /**
     * Sets the value of the loadType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeLoadChoice }
     *     
     */
    public void setLoadType(TypeLoadChoice value) {
        this.loadType = value;
    }

}
