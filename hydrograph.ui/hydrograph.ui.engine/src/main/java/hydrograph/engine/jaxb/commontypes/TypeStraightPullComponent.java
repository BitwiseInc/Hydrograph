
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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.limit.TypeLimitBase;
import hydrograph.engine.jaxb.removedups.TypeRemovedupsBase;
import hydrograph.engine.jaxb.sort.TypeSortBase;
import hydrograph.engine.jaxb.straightpulltypes.Clone;
import hydrograph.engine.jaxb.straightpulltypes.Dummy;
import hydrograph.engine.jaxb.straightpulltypes.UnionAll;


/**
 * <p>Java class for type-straight-pull-component complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-straight-pull-component">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-base-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/commontypes}type-base-inSocket" maxOccurs="unbounded"/>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/commontypes}type-straight-pull-out-socket" maxOccurs="unbounded"/>
 *         &lt;element name="runtimeProperties" type="{hydrograph/engine/jaxb/commontypes}type-properties" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-straight-pull-component", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "inSocket",
    "outSocket",
    "runtimeProperties"
})
@XmlSeeAlso({
    Dummy.class,
    UnionAll.class,
    Clone.class,
    TypeLimitBase.class,
    TypeRemovedupsBase.class,
    TypeSortBase.class
})
public abstract class TypeStraightPullComponent
    extends TypeBaseComponent
{

    @XmlElement(required = true)
    protected List<TypeBaseInSocket> inSocket;
    @XmlElement(required = true)
    protected List<TypeStraightPullOutSocket> outSocket;
    protected TypeProperties runtimeProperties;

    /**
     * Gets the value of the inSocket property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inSocket property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInSocket().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeBaseInSocket }
     * 
     * 
     */
    public List<TypeBaseInSocket> getInSocket() {
        if (inSocket == null) {
            inSocket = new ArrayList<TypeBaseInSocket>();
        }
        return this.inSocket;
    }

    /**
     * Gets the value of the outSocket property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outSocket property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutSocket().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeStraightPullOutSocket }
     * 
     * 
     */
    public List<TypeStraightPullOutSocket> getOutSocket() {
        if (outSocket == null) {
            outSocket = new ArrayList<TypeStraightPullOutSocket>();
        }
        return this.outSocket;
    }

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

}
