
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
import hydrograph.engine.jaxb.ojdbcupdate.TypeOutputJdbcupdateBase;
import hydrograph.engine.jaxb.ojdbcupdate.TypeUpdateKeys;


/**
 * <p>Java class for jdbcUpdate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="jdbcUpdate">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ojdbcupdate}type-output-jdbcupdate-base">
 *       &lt;sequence>
 *         &lt;element name="url" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="jdbcDriverClass" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="tableName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type"/>
 *         &lt;element name="batchSize" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type" minOccurs="0"/>
 *         &lt;element name="userName" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="password" type="{hydrograph/engine/jaxb/commontypes}element-value-string-type" minOccurs="0"/>
 *         &lt;element name="update" type="{hydrograph/engine/jaxb/ojdbcupdate}type-update-keys"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "jdbcUpdate", namespace = "hydrograph/engine/jaxb/outputtypes", propOrder = {
    "url",
    "jdbcDriverClass",
    "tableName",
    "batchSize",
    "userName",
    "password",
    "update"
})
public class JdbcUpdate
    extends TypeOutputJdbcupdateBase
{

    @XmlElement(required = true)
    protected ElementValueStringType url;
    @XmlElement(required = true)
    protected ElementValueStringType jdbcDriverClass;
    @XmlElement(required = true)
    protected ElementValueStringType tableName;
    protected ElementValueIntegerType batchSize;
    protected ElementValueStringType userName;
    protected ElementValueStringType password;
    @XmlElement(required = true)
    protected TypeUpdateKeys update;

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setUrl(ElementValueStringType value) {
        this.url = value;
    }

    /**
     * Gets the value of the jdbcDriverClass property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueStringType }
     *     
     */
    public ElementValueStringType getJdbcDriverClass() {
        return jdbcDriverClass;
    }

    /**
     * Sets the value of the jdbcDriverClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueStringType }
     *     
     */
    public void setJdbcDriverClass(ElementValueStringType value) {
        this.jdbcDriverClass = value;
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
     * Gets the value of the batchSize property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public ElementValueIntegerType getBatchSize() {
        return batchSize;
    }

    /**
     * Sets the value of the batchSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public void setBatchSize(ElementValueIntegerType value) {
        this.batchSize = value;
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
     * Gets the value of the update property.
     * 
     * @return
     *     possible object is
     *     {@link TypeUpdateKeys }
     *     
     */
    public TypeUpdateKeys getUpdate() {
        return update;
    }

    /**
     * Sets the value of the update property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeUpdateKeys }
     *     
     */
    public void setUpdate(TypeUpdateKeys value) {
        this.update = value;
    }

}
