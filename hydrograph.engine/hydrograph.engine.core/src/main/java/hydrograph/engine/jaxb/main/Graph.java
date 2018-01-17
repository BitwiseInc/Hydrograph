
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
package hydrograph.engine.jaxb.main;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeCommandComponent;
import hydrograph.engine.jaxb.commontypes.TypeInputComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOutputComponent;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="runtimeProperties" type="{hydrograph/engine/jaxb/commontypes}type-properties" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="inputs" type="{hydrograph/engine/jaxb/commontypes}type-input-component"/>
 *           &lt;element name="outputs" type="{hydrograph/engine/jaxb/commontypes}type-output-component"/>
 *           &lt;element name="straightPulls" type="{hydrograph/engine/jaxb/commontypes}type-straight-pull-component"/>
 *           &lt;element name="operations" type="{hydrograph/engine/jaxb/commontypes}type-operations-component"/>
 *           &lt;element name="commands" type="{hydrograph/engine/jaxb/commontypes}type-command-component"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="uniqueJobId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "runtimeProperties",
    "inputsOrOutputsOrStraightPulls"
})
@XmlRootElement(name = "graph", namespace = "hydrograph/engine/jaxb/main")
public class Graph {

    protected TypeProperties runtimeProperties;
    @XmlElements({
        @XmlElement(name = "inputs", type = TypeInputComponent.class),
        @XmlElement(name = "outputs", type = TypeOutputComponent.class),
        @XmlElement(name = "straightPulls", type = TypeStraightPullComponent.class),
        @XmlElement(name = "operations", type = TypeOperationsComponent.class),
        @XmlElement(name = "commands", type = TypeCommandComponent.class)
    })
    protected List<TypeBaseComponent> inputsOrOutputsOrStraightPulls;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "uniqueJobId")
    protected String uniqueJobId;

    /**
     * Gets the value of the runtimeProperties property.
     * 
     * @return
     *     possible object is
     *     {@link TypeProperties }
     *     
     */
    public TypeProperties getRuntimeProperties() {
        return runtimeProperties;
    }

    /**
     * Sets the value of the runtimeProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeProperties }
     *     
     */
    public void setRuntimeProperties(TypeProperties value) {
        this.runtimeProperties = value;
    }

    /**
     * Gets the value of the inputsOrOutputsOrStraightPulls property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inputsOrOutputsOrStraightPulls property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInputsOrOutputsOrStraightPulls().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeInputComponent }
     * {@link TypeOutputComponent }
     * {@link TypeStraightPullComponent }
     * {@link TypeOperationsComponent }
     * {@link TypeCommandComponent }
     * 
     * 
     */
    public List<TypeBaseComponent> getInputsOrOutputsOrStraightPulls() {
        if (inputsOrOutputsOrStraightPulls == null) {
            inputsOrOutputsOrStraightPulls = new ArrayList<TypeBaseComponent>();
        }
        return this.inputsOrOutputsOrStraightPulls;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the uniqueJobId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueJobId() {
        return uniqueJobId;
    }

    /**
     * Sets the value of the uniqueJobId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueJobId(String value) {
        this.uniqueJobId = value;
    }

}
