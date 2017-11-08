
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
package hydrograph.engine.jaxb.sort;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeSortOrder;


/**
 * <p>Java class for type-seconday-key-fields-attributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-seconday-key-fields-attributes">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-field-name">
 *       &lt;attribute name="order" type="{hydrograph/engine/jaxb/commontypes}type-sort-order" default="asc" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-seconday-key-fields-attributes", namespace = "hydrograph/engine/jaxb/sort")
public class TypeSecondayKeyFieldsAttributes
    extends TypeFieldName
{

    @XmlAttribute(name = "order")
    protected TypeSortOrder order;

    /**
     * Gets the value of the order property.
     * 
     * @return
     *     possible object is
     *     {@link TypeSortOrder }
     *     
     */
    public TypeSortOrder getOrder() {
        if (order == null) {
            return TypeSortOrder.ASC;
        } else {
            return order;
        }
    }

    /**
     * Sets the value of the order property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeSortOrder }
     *     
     */
    public void setOrder(TypeSortOrder value) {
        this.order = value;
    }

}
