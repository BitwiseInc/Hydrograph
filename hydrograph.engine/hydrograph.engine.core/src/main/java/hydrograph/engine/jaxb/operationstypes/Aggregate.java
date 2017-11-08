
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
package hydrograph.engine.jaxb.operationstypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.aggregate.AggregateBase;
import hydrograph.engine.jaxb.aggregate.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.aggregate.TypeSecondaryKeyFields;


/**
 * <p>Java class for aggregate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="aggregate">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/aggregate}aggregate-base">
 *       &lt;sequence>
 *         &lt;element name="primaryKeys" type="{hydrograph/engine/jaxb/aggregate}type-primary-key-fields"/>
 *         &lt;element name="secondaryKeys" type="{hydrograph/engine/jaxb/aggregate}type-secondary-key-fields" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "aggregate", namespace = "hydrograph/engine/jaxb/operationstypes", propOrder = {
    "primaryKeys",
    "secondaryKeys"
})
public class Aggregate
    extends AggregateBase
{

    @XmlElement(required = true)
    protected TypePrimaryKeyFields primaryKeys;
    protected TypeSecondaryKeyFields secondaryKeys;

    /**
     * Gets the value of the primaryKeys property.
     * 
     * @return
     *     possible object is
     *     {@link TypePrimaryKeyFields }
     *     
     */
    public TypePrimaryKeyFields getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * Sets the value of the primaryKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypePrimaryKeyFields }
     *     
     */
    public void setPrimaryKeys(TypePrimaryKeyFields value) {
        this.primaryKeys = value;
    }

    /**
     * Gets the value of the secondaryKeys property.
     * 
     * @return
     *     possible object is
     *     {@link TypeSecondaryKeyFields }
     *     
     */
    public TypeSecondaryKeyFields getSecondaryKeys() {
        return secondaryKeys;
    }

    /**
     * Sets the value of the secondaryKeys property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeSecondaryKeyFields }
     *     
     */
    public void setSecondaryKeys(TypeSecondaryKeyFields value) {
        this.secondaryKeys = value;
    }

}
