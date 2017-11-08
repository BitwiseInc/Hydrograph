
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
import hydrograph.engine.jaxb.ooracle.TypeLoadChoice;
import hydrograph.engine.jaxb.ooracle.TypeOutputOracleBase;


/**
 * <p>Java class for oracle complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="oracle">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ooracle}type-output-oracle-base">
 *       &lt;sequence>
 *         &lt;element name="sid" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="hostName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="port" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type" minOccurs="0"/>
 *         &lt;element name="driverType" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="tableName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="userName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="password" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="chunkSize" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="extraUrlParams" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="loadType" type="{hydrograph/engine/jaxb/ooracle}type-load-choice"/>
 *         &lt;element name="schemaName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "oracle", namespace = "hydrograph/engine/jaxb/outputtypes", propOrder = {
    "sid",
    "hostName",
    "port",
    "driverType",
    "tableName",
    "userName",
    "password",
    "chunkSize",
    "extraUrlParams",
    "loadType",
    "schemaName"
})
public class Oracle
    extends TypeOutputOracleBase
{

    @XmlElement(required = true)
    protected ElementValueStringType sid;
    @XmlElement(required = true)
    protected ElementValueStringType hostName;
    protected ElementValueIntegerType port;
    @XmlElement(required = true)
    protected ElementValueStringType driverType;
    @XmlElement(required = true)
    protected ElementValueStringType tableName;
    @XmlElement(required = true)
    protected ElementValueStringType userName;
    @XmlElement(required = true)
    protected ElementValueStringType password;
    protected ElementValueStringType chunkSize;
    protected ElementValueStringType extraUrlParams;
    @XmlElement(required = true)
    protected TypeLoadChoice loadType;
    protected ElementValueStringType schemaName;

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

}
