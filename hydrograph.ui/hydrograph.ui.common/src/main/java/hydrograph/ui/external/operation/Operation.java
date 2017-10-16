/*******************************************************************************
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
 *******************************************************************************/

package hydrograph.ui.external.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import hydrograph.ui.external.common.InputFields;
import hydrograph.ui.external.common.OperationOutputFields;
import hydrograph.ui.external.common.Properties;


/**
 * <p>Java class for operation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="operation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inputFields" type="{hydrograph/ui/external/common}input-fields" minOccurs="0"/>
 *         &lt;element name="outputFields" type="{hydrograph/ui/external/common}operation-output-fields" minOccurs="0"/>
 *         &lt;element name="properties" type="{hydrograph/ui/external/common}properties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operation", propOrder = {
    "inputFields",
    "outputFields",
    "properties"
})
public class Operation {

    protected InputFields inputFields;
    protected OperationOutputFields outputFields;
    protected Properties properties;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "class")
    protected String clazz;

    /**
     * Gets the value of the inputFields property.
     * 
     * @return
     *     possible object is
     *     {@link InputFields }
     *     
     */
    public InputFields getInputFields() {
        return inputFields;
    }

    /**
     * Sets the value of the inputFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputFields }
     *     
     */
    public void setInputFields(InputFields value) {
        this.inputFields = value;
    }

    /**
     * Gets the value of the outputFields property.
     * 
     * @return
     *     possible object is
     *     {@link OperationOutputFields }
     *     
     */
    public OperationOutputFields getOutputFields() {
        return outputFields;
    }

    /**
     * Sets the value of the outputFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link OperationOutputFields }
     *     
     */
    public void setOutputFields(OperationOutputFields value) {
        this.outputFields = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

}
