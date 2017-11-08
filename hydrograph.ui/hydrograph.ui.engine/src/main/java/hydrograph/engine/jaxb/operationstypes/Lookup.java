
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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.MatchValue;
import hydrograph.engine.jaxb.lookup.LookupBase;
import hydrograph.engine.jaxb.lookup.TypeKeyFields;


/**
 * <p>Java class for lookup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lookup">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/lookup}lookup-base">
 *       &lt;sequence>
 *         &lt;element name="keys" type="{hydrograph/engine/jaxb/lookup}type-key-fields" maxOccurs="unbounded" minOccurs="2"/>
 *         &lt;element name="match">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{hydrograph/engine/jaxb/commontypes}match_value" default="first" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lookup", namespace = "hydrograph/engine/jaxb/operationstypes", propOrder = {
    "keys",
    "match"
})
public class Lookup
    extends LookupBase
{

    @XmlElement(required = true)
    protected List<TypeKeyFields> keys;
    @XmlElement(required = true)
    protected Lookup.Match match;

    /**
     * Gets the value of the keys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeKeyFields }
     * 
     * 
     */
    public List<TypeKeyFields> getKeys() {
        if (keys == null) {
            keys = new ArrayList<TypeKeyFields>();
        }
        return this.keys;
    }

    /**
     * Gets the value of the match property.
     * 
     * @return
     *     possible object is
     *     {@link Lookup.Match }
     *     
     */
    public Lookup.Match getMatch() {
        return match;
    }

    /**
     * Sets the value of the match property.
     * 
     * @param value
     *     allowed object is
     *     {@link Lookup.Match }
     *     
     */
    public void setMatch(Lookup.Match value) {
        this.match = value;
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
     *       &lt;attribute name="value" type="{hydrograph/engine/jaxb/commontypes}match_value" default="first" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Match {

        @XmlAttribute(name = "value")
        protected MatchValue value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link MatchValue }
         *     
         */
        public MatchValue getValue() {
            if (value == null) {
                return MatchValue.FIRST;
            } else {
                return value;
            }
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link MatchValue }
         *     
         */
        public void setValue(MatchValue value) {
            this.value = value;
        }

    }

}
