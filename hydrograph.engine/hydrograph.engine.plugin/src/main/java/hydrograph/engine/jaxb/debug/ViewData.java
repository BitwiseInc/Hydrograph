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
 * limitations under the License
 *******************************************************************************/

package hydrograph.engine.jaxb.debug;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for viewData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="viewData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="fromComponentId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="outSocketId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="outSocketType" type="{http://www.w3.org/2001/XMLSchema}string" default="out" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "viewData", namespace = "hydrograph/engine/jaxb/debug")
public class ViewData {

    @XmlAttribute(name = "fromComponentId", required = true)
    protected String fromComponentId;
    @XmlAttribute(name = "outSocketId", required = true)
    protected String outSocketId;
    @XmlAttribute(name = "outSocketType")
    protected String outSocketType;

    /**
     * Gets the value of the fromComponentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromComponentId() {
        return fromComponentId;
    }

    /**
     * Sets the value of the fromComponentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromComponentId(String value) {
        this.fromComponentId = value;
    }

    /**
     * Gets the value of the outSocketId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutSocketId() {
        return outSocketId;
    }

    /**
     * Sets the value of the outSocketId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutSocketId(String value) {
        this.outSocketId = value;
    }

    /**
     * Gets the value of the outSocketType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutSocketType() {
        if (outSocketType == null) {
            return "out";
        } else {
            return outSocketType;
        }
    }

    /**
     * Sets the value of the outSocketType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutSocketType(String value) {
        this.outSocketType = value;
    }

}
