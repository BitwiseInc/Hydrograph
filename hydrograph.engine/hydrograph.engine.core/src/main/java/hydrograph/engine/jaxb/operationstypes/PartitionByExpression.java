
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.partitionbyexpression.PartitionByExpressionBase;


/**
 * <p>Java class for partitionByExpression complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="partitionByExpression">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/partitionByExpression}partitionByExpression-base">
 *       &lt;sequence>
 *         &lt;element name="noOfPartitions">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
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
@XmlType(name = "partitionByExpression", namespace = "hydrograph/engine/jaxb/operationstypes", propOrder = {
    "noOfPartitions"
})
public class PartitionByExpression
    extends PartitionByExpressionBase
{

    @XmlElement(required = true)
    protected PartitionByExpression.NoOfPartitions noOfPartitions;

    /**
     * Gets the value of the noOfPartitions property.
     * 
     * @return
     *     possible object is
     *     {@link PartitionByExpression.NoOfPartitions }
     *     
     */
    public PartitionByExpression.NoOfPartitions getNoOfPartitions() {
        return noOfPartitions;
    }

    /**
     * Sets the value of the noOfPartitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartitionByExpression.NoOfPartitions }
     *     
     */
    public void setNoOfPartitions(PartitionByExpression.NoOfPartitions value) {
        this.noOfPartitions = value;
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
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NoOfPartitions {

        @XmlAttribute(name = "value", required = true)
        protected long value;

        /**
         * Gets the value of the value property.
         * 
         */
        public long getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         */
        public void setValue(long value) {
            this.value = value;
        }

    }

}
