
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
package hydrograph.engine.jaxb.oteradata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for type-load-choice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-load-choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="newTable" type="{hydrograph/engine/jaxb/oteradata}type-primary-keys"/>
 *         &lt;element name="truncateLoad" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="Insert" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="update" type="{hydrograph/engine/jaxb/oteradata}type-update-keys"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-load-choice", namespace = "hydrograph/engine/jaxb/oteradata", propOrder = {
    "newTable",
    "truncateLoad",
    "insert",
    "update"
})
public class TypeLoadChoice {

    protected TypePrimaryKeys newTable;
    protected Object truncateLoad;
    @XmlElement(name = "Insert")
    protected Object insert;
    protected TypeUpdateKeys update;

    /**
     * Gets the value of the newTable property.
     * 
     * @return
     *     possible object is
     *     {@link TypePrimaryKeys }
     *     
     */
    public TypePrimaryKeys getNewTable() {
        return newTable;
    }

    /**
     * Sets the value of the newTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypePrimaryKeys }
     *     
     */
    public void setNewTable(TypePrimaryKeys value) {
        this.newTable = value;
    }

    /**
     * Gets the value of the truncateLoad property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getTruncateLoad() {
        return truncateLoad;
    }

    /**
     * Sets the value of the truncateLoad property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setTruncateLoad(Object value) {
        this.truncateLoad = value;
    }

    /**
     * Gets the value of the insert property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getInsert() {
        return insert;
    }

    /**
     * Sets the value of the insert property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setInsert(Object value) {
        this.insert = value;
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
