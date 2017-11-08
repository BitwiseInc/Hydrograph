
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
package hydrograph.engine.jaxb.straightpulltypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.KeepValue;
import hydrograph.engine.jaxb.removedups.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.removedups.TypeRemovedupsBase;
import hydrograph.engine.jaxb.removedups.TypeSecondaryKeyFields;


/**
 * <p>Java class for removeDups complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="removeDups">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/removedups}type-removedups-base">
 *       &lt;sequence>
 *         &lt;element name="keep" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}keep_value" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="primaryKeys" type="{hydrograph/engine/jaxb/removedups}type-primary-key-fields"/>
 *         &lt;element name="secondaryKeys" type="{hydrograph/engine/jaxb/removedups}type-secondary-key-fields" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "removeDups", namespace = "hydrograph/engine/jaxb/straightpulltypes", propOrder = {
    "keep",
    "primaryKeys",
    "secondaryKeys"
})
public class RemoveDups
    extends TypeRemovedupsBase
{

    protected RemoveDups.Keep keep;
    @XmlElement(required = true)
    protected TypePrimaryKeyFields primaryKeys;
    protected TypeSecondaryKeyFields secondaryKeys;

    /**
     * Gets the value of the keep property.
     * 
     * @return
     *     possible object is
     *     {@link RemoveDups.Keep }
     *     
     */
    public RemoveDups.Keep getKeep() {
        return keep;
    }

    /**
     * Sets the value of the keep property.
     * 
     * @param value
     *     allowed object is
     *     {@link RemoveDups.Keep }
     *     
     */
    public void setKeep(RemoveDups.Keep value) {
        this.keep = value;
    }

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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}keep_value" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Keep {

        @XmlAttribute(name = "value", required = true)
        protected KeepValue value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link KeepValue }
         *     
         */
        public KeepValue getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link KeepValue }
         *     
         */
        public void setValue(KeepValue value) {
            this.value = value;
        }

    }

}
