
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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.executiontracking.TypeExecutiontrackingOutSocket;
import hydrograph.engine.jaxb.filter.TypeFilterOutSocket;
import hydrograph.engine.jaxb.partitionbyexpression.TypePbeOutSocket;
import hydrograph.engine.jaxb.transform.TypeTransformOutSocket;


/**
 * <p>Java class for type-operations-out-socket complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-operations-out-socket">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-base-outSocket">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="copyOfInsocket" type="{hydrograph/engine/jaxb/commontypes}type-outSocket-as-inSocket"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="passThroughField" type="{hydrograph/engine/jaxb/commontypes}type-input-field"/>
 *           &lt;element name="operationField" type="{hydrograph/engine/jaxb/commontypes}type-operation-field"/>
 *           &lt;element name="expressionField" type="{hydrograph/engine/jaxb/commontypes}type-expression-field"/>
 *           &lt;element name="mapField" type="{hydrograph/engine/jaxb/commontypes}type-map-field"/>
 *           &lt;element name="includeExternalMapping" type="{hydrograph/engine/jaxb/commontypes}type-external-schema"/>
 *         &lt;/choice>
 *       &lt;/choice>
 *       &lt;anyAttribute/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-operations-out-socket", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "copyOfInsocket",
    "passThroughFieldOrOperationFieldOrExpressionField"
})
@XmlSeeAlso({
    TypeTransformOutSocket.class,
    hydrograph.engine.jaxb.aggregate.TypeOutSocket.class,
    hydrograph.engine.jaxb.groupcombine.TypeOutSocket.class,
    hydrograph.engine.jaxb.join.TypeOutSocket.class,
    TypeFilterOutSocket.class,
    TypeExecutiontrackingOutSocket.class,
    hydrograph.engine.jaxb.generatesequence.TypeOutSocket.class,
    hydrograph.engine.jaxb.cumulate.TypeOutSocket.class,
    hydrograph.engine.jaxb.lookup.TypeOutSocket.class,
    hydrograph.engine.jaxb.normalize.TypeOutSocket.class,
    hydrograph.engine.jaxb.subjob.TypeOutSocket.class,
    TypePbeOutSocket.class
})
public class TypeOperationsOutSocket
    extends TypeBaseOutSocket
{

    protected TypeOutSocketAsInSocket copyOfInsocket;
    @XmlElements({
        @XmlElement(name = "passThroughField", type = TypeInputField.class),
        @XmlElement(name = "operationField", type = TypeOperationField.class),
        @XmlElement(name = "expressionField", type = TypeExpressionField.class),
        @XmlElement(name = "mapField", type = TypeMapField.class),
        @XmlElement(name = "includeExternalMapping", type = TypeExternalSchema.class)
    })
    protected List<Object> passThroughFieldOrOperationFieldOrExpressionField;

    /**
     * Gets the value of the copyOfInsocket property.
     * 
     * @return
     *     possible object is
     *     {@link TypeOutSocketAsInSocket }
     *     
     */
    public TypeOutSocketAsInSocket getCopyOfInsocket() {
        return copyOfInsocket;
    }

    /**
     * Sets the value of the copyOfInsocket property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOutSocketAsInSocket }
     *     
     */
    public void setCopyOfInsocket(TypeOutSocketAsInSocket value) {
        this.copyOfInsocket = value;
    }

    /**
     * Gets the value of the passThroughFieldOrOperationFieldOrExpressionField property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the passThroughFieldOrOperationFieldOrExpressionField property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPassThroughFieldOrOperationFieldOrExpressionField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeInputField }
     * {@link TypeOperationField }
     * {@link TypeExpressionField }
     * {@link TypeMapField }
     * {@link TypeExternalSchema }
     * 
     * 
     */
    public List<Object> getPassThroughFieldOrOperationFieldOrExpressionField() {
        if (passThroughFieldOrOperationFieldOrExpressionField == null) {
            passThroughFieldOrOperationFieldOrExpressionField = new ArrayList<Object>();
        }
        return this.passThroughFieldOrOperationFieldOrExpressionField;
    }

}
