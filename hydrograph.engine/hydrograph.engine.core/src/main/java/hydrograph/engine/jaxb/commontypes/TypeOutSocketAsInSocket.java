
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


/**
 * <p>Java class for type-outSocket-as-inSocket complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-outSocket-as-inSocket">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="inSocketId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-outSocket-as-inSocket", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlSeeAlso({
    hydrograph.engine.jaxb.limit.TypeOutSocketAsInSocketIn0 .class,
    hydrograph.engine.jaxb.clone.TypeOutSocketAsInSocketIn0 .class,
    hydrograph.engine.jaxb.removedups.TypeOutSocketAsInSocketIn0 .class,
    hydrograph.engine.jaxb.sort.TypeOutSocketAsInSocketIn0 .class,
    hydrograph.engine.jaxb.transform.TypeOutSocketAsInSocketIn0 .class,
    hydrograph.engine.jaxb.aggregate.TypeOutSocketAsInSocketIn0 .class,
    hydrograph.engine.jaxb.groupcombine.TypeOutSocketAsInSocketIn0 .class,
    hydrograph.engine.jaxb.filter.TypeOutSocketAsInSocketIn0 .class,
    hydrograph.engine.jaxb.executiontracking.TypeOutSocketAsInSocketIn0 .class
})
public class TypeOutSocketAsInSocket {

    @XmlAttribute(name = "inSocketId", required = true)
    protected String inSocketId;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the inSocketId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInSocketId() {
        return inSocketId;
    }

    /**
     * Sets the value of the inSocketId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInSocketId(String value) {
        this.inSocketId = value;
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
