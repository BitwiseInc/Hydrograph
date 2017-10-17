
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
package hydrograph.engine.jaxb.removedups;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;


/**
 * <p>Java class for type-primary-key-fields complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-primary-key-fields">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/commontypes}type-field-name" maxOccurs="unbounded"/>
 *         &lt;element name="none" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-primary-key-fields", namespace = "hydrograph/engine/jaxb/removedups", propOrder = {
    "field",
    "none"
})
public class TypePrimaryKeyFields {

    protected List<TypeFieldName> field;
    protected Object none;

    /**
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeFieldName }
     * 
     * 
     */
    public List<TypeFieldName> getField() {
        if (field == null) {
            field = new ArrayList<TypeFieldName>();
        }
        return this.field;
    }

    /**
     * Gets the value of the none property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getNone() {
        return none;
    }

    /**
     * Sets the value of the none property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setNone(Object value) {
        this.none = value;
    }

}
