
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
package hydrograph.engine.jaxb.commontypes;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import hydrograph.engine.jaxb.executiontracking.TypeExecutiontrackingOperation;
import hydrograph.engine.jaxb.filter.TypeFilterOperation;
import hydrograph.engine.jaxb.partitionbyexpression.TypePbeOperation;


/**
 * <p>Java class for type-transform-operation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-transform-operation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="inputFields" type="{hydrograph/engine/jaxb/commontypes}type-operation-input-fields" minOccurs="0"/>
 *         &lt;element name="outputFields" type="{hydrograph/engine/jaxb/commontypes}type-operation-output-fields" minOccurs="0"/>
 *         &lt;element name="properties" type="{hydrograph/engine/jaxb/commontypes}type-properties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-transform-operation", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "inputFields",
    "outputFields",
    "properties"
})
@XmlSeeAlso({
    hydrograph.engine.jaxb.transform.TypeTransformOperation.class,
    hydrograph.engine.jaxb.aggregate.TypeOperation.class,
    hydrograph.engine.jaxb.groupcombine.TypeOperation.class,
    TypeFilterOperation.class,
    TypeExecutiontrackingOperation.class,
    hydrograph.engine.jaxb.generatesequence.TypeOperation.class,
    hydrograph.engine.jaxb.cumulate.TypeOperation.class,
    hydrograph.engine.jaxb.normalize.TypeOperation.class,
    TypePbeOperation.class
})
public class TypeTransformOperation {

    protected TypeOperationInputFields inputFields;
    protected TypeOperationOutputFields outputFields;
    protected TypeProperties properties;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "class")
    protected String clazz;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the inputFields property.
     * 
     * @return
     *     possible object is
     *     {@link TypeOperationInputFields }
     *     
     */
    public TypeOperationInputFields getInputFields() {
        return inputFields;
    }

    /**
     * Sets the value of the inputFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOperationInputFields }
     *     
     */
    public void setInputFields(TypeOperationInputFields value) {
        this.inputFields = value;
    }

    /**
     * Gets the value of the outputFields property.
     * 
     * @return
     *     possible object is
     *     {@link TypeOperationOutputFields }
     *     
     */
    public TypeOperationOutputFields getOutputFields() {
        return outputFields;
    }

    /**
     * Sets the value of the outputFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOperationOutputFields }
     *     
     */
    public void setOutputFields(TypeOperationOutputFields value) {
        this.outputFields = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link TypeProperties }
     *     
     */
    public TypeProperties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeProperties }
     *     
     */
    public void setProperties(TypeProperties value) {
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

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
