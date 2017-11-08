
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
import hydrograph.engine.jaxb.clone.TypeCloneInSocket;
import hydrograph.engine.jaxb.executiontracking.TypeExecutiontrackingInSocket;
import hydrograph.engine.jaxb.filter.TypeFilterInSocket;
import hydrograph.engine.jaxb.limit.TypeLimitInSocket;
import hydrograph.engine.jaxb.partitionbyexpression.TypePbeInSocket;
import hydrograph.engine.jaxb.transform.TypeTransformInSocket;


/**
 * <p>Java class for type-base-inSocket complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-base-inSocket">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{hydrograph/engine/jaxb/commontypes}grp-attr-base-inSocket"/>
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-base-inSocket", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlSeeAlso({
    TypeBaseInSocketFixedIn0 .class,
    TypeOutputInSocket.class,
    TypeLimitInSocket.class,
    TypeCloneInSocket.class,
    TypeTransformInSocket.class,
    hydrograph.engine.jaxb.aggregate.TypeInSocket.class,
    hydrograph.engine.jaxb.groupcombine.TypeInSocket.class,
    hydrograph.engine.jaxb.join.TypeInSocket.class,
    TypeFilterInSocket.class,
    TypeExecutiontrackingInSocket.class,
    hydrograph.engine.jaxb.cumulate.TypeInSocket.class,
    hydrograph.engine.jaxb.lookup.TypeInSocket.class,
    hydrograph.engine.jaxb.normalize.TypeInSocket.class,
    hydrograph.engine.jaxb.subjob.TypeInSocket.class,
    TypePbeInSocket.class
})
public class TypeBaseInSocket {

    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "fromComponentId", required = true)
    protected String fromComponentId;
    @XmlAttribute(name = "fromSocketId", required = true)
    protected String fromSocketId;
    @XmlAttribute(name = "fromSocketType")
    protected String fromSocketType;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

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
     * Gets the value of the fromSocketId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromSocketId() {
        return fromSocketId;
    }

    /**
     * Sets the value of the fromSocketId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromSocketId(String value) {
        this.fromSocketId = value;
    }

    /**
     * Gets the value of the fromSocketType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromSocketType() {
        return fromSocketType;
    }

    /**
     * Sets the value of the fromSocketType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromSocketType(String value) {
        this.fromSocketType = value;
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
