
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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.aggregate.AggregateBase;
import hydrograph.engine.jaxb.cumulate.CumulateBase;
import hydrograph.engine.jaxb.groupcombine.GroupcombineBase;
import hydrograph.engine.jaxb.join.JoinBase;
import hydrograph.engine.jaxb.lookup.LookupBase;
import hydrograph.engine.jaxb.operationstypes.Executiontracking;
import hydrograph.engine.jaxb.operationstypes.Filter;
import hydrograph.engine.jaxb.operationstypes.GenerateSequence;
import hydrograph.engine.jaxb.operationstypes.Normalize;
import hydrograph.engine.jaxb.operationstypes.Transform;
import hydrograph.engine.jaxb.partitionbyexpression.PartitionByExpressionBase;
import hydrograph.engine.jaxb.subjob.SubjobBase;


/**
 * <p>Java class for type-operations-component complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-operations-component">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-base-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/commontypes}type-base-inSocket" maxOccurs="unbounded"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="operation" type="{hydrograph/engine/jaxb/commontypes}type-transform-operation" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="expression" type="{hydrograph/engine/jaxb/commontypes}type-transform-expression" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="includeExternalOperation" type="{hydrograph/engine/jaxb/commontypes}type-external-schema" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="includeExternalExpression" type="{hydrograph/engine/jaxb/commontypes}type-external-schema" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="outputRecordCount" type="{hydrograph/engine/jaxb/commontypes}type-output-record-count" minOccurs="0"/>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/commontypes}type-operations-out-socket" maxOccurs="unbounded"/>
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
@XmlType(name = "type-operations-component", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "inSocket",
    "operationOrExpressionOrIncludeExternalOperation",
    "outputRecordCount",
    "outSocket",
    "runtimeProperties"
})
@XmlSeeAlso({
    Filter.class,
    Transform.class,
    GenerateSequence.class,
    Normalize.class,
    Executiontracking.class,
    AggregateBase.class,
    GroupcombineBase.class,
    JoinBase.class,
    CumulateBase.class,
    LookupBase.class,
    SubjobBase.class,
    PartitionByExpressionBase.class
})
public abstract class TypeOperationsComponent
    extends TypeBaseComponent
{

    @XmlElement(required = true)
    protected List<TypeBaseInSocket> inSocket;
    @XmlElementRefs({
        @XmlElementRef(name = "includeExternalOperation", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "expression", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "includeExternalExpression", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "operation", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> operationOrExpressionOrIncludeExternalOperation;
    protected TypeOutputRecordCount outputRecordCount;
    @XmlElement(required = true)
    protected List<TypeOperationsOutSocket> outSocket;
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
     * Gets the value of the operationOrExpressionOrIncludeExternalOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operationOrExpressionOrIncludeExternalOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperationOrExpressionOrIncludeExternalOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TypeExternalSchema }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeTransformExpression }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeExternalSchema }{@code >}
     * {@link JAXBElement }{@code <}{@link TypeTransformOperation }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getOperationOrExpressionOrIncludeExternalOperation() {
        if (operationOrExpressionOrIncludeExternalOperation == null) {
            operationOrExpressionOrIncludeExternalOperation = new ArrayList<JAXBElement<?>>();
        }
        return this.operationOrExpressionOrIncludeExternalOperation;
    }

    /**
     * Gets the value of the outputRecordCount property.
     * 
     * @return
     *     possible object is
     *     {@link TypeOutputRecordCount }
     *     
     */
    public TypeOutputRecordCount getOutputRecordCount() {
        return outputRecordCount;
    }

    /**
     * Sets the value of the outputRecordCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOutputRecordCount }
     *     
     */
    public void setOutputRecordCount(TypeOutputRecordCount value) {
        this.outputRecordCount = value;
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
     * {@link TypeOperationsOutSocket }
     * 
     * 
     */
    public List<TypeOperationsOutSocket> getOutSocket() {
        if (outSocket == null) {
            outSocket = new ArrayList<TypeOperationsOutSocket>();
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
