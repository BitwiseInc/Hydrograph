
/*****************************************************************************************
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
 ****************************************************************************************/

package hydrograph.engine.jaxb.oteradata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeKeyFields;


/**
 * <p>Java class for type-update-keys complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-update-keys">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="updateByKeys" type="{hydrograph/engine/jaxb/commontypes}type-key-fields"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-update-keys", namespace = "hydrograph/engine/jaxb/oteradata", propOrder = {
    "updateByKeys"
})
public class TypeUpdateKeys {

    @XmlElement(required = true)
    protected TypeKeyFields updateByKeys;

    /**
     * Gets the value of the updateByKeys property.
     * 
     * @return
     *     possible object is
     *     {@link TypeKeyFields }
     *     
     */
    public TypeKeyFields getUpdateByKeys() {
        return updateByKeys;
    }

    /**
     * Sets the value of the updateByKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeKeyFields }
     *     
     */
    public void setUpdateByKeys(TypeKeyFields value) {
        this.updateByKeys = value;
    }

}
