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

package hydrograph.ui.external.expression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import hydrograph.ui.external.common.ExpressionOutputFields;
import hydrograph.ui.external.common.InputFields;
import hydrograph.ui.external.common.Properties;


/**
 * <p>Java class for expression complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="expression">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inputFields" type="{hydrograph/ui/external/common}input-fields" minOccurs="0"/>
 *         &lt;element name="outputFields" type="{hydrograph/ui/external/common}expression-output-fields" minOccurs="0"/>
 *         &lt;element name="properties" type="{hydrograph/ui/external/common}properties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expr" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mergeExpr" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="accumulatorInitalValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "expression", propOrder = {
    "inputFields",
    "outputFields",
    "properties"
})
public class Expression {

    protected InputFields inputFields;
    protected ExpressionOutputFields outputFields;
    protected Properties properties;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "expr", required = true)
    protected String expr;
    @XmlAttribute(name = "mergeExpr")
    protected String mergeExpr;
    @XmlAttribute(name = "accumulatorInitalValue")
    protected String accumulatorInitalValue;

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
     *     {@link ExpressionOutputFields }
     *     
     */
    public ExpressionOutputFields getOutputFields() {
        return outputFields;
    }

    /**
     * Sets the value of the outputFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExpressionOutputFields }
     *     
     */
    public void setOutputFields(ExpressionOutputFields value) {
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
     * Gets the value of the expr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpr() {
        return expr;
    }

    /**
     * Sets the value of the expr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpr(String value) {
        this.expr = value;
    }

    /**
     * Gets the value of the mergeExpr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMergeExpr() {
        return mergeExpr;
    }

    /**
     * Sets the value of the mergeExpr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMergeExpr(String value) {
        this.mergeExpr = value;
    }

    /**
     * Gets the value of the accumulatorInitalValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccumulatorInitalValue() {
        return accumulatorInitalValue;
    }

    /**
     * Sets the value of the accumulatorInitalValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccumulatorInitalValue(String value) {
        this.accumulatorInitalValue = value;
    }

}
