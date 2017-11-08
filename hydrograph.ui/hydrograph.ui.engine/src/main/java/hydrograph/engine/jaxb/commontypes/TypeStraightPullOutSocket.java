
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.clone.TypeCloneOutSocket;
import hydrograph.engine.jaxb.limit.TypeLimitOutSocket;


/**
 * <p>Java class for type-straight-pull-out-socket complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-straight-pull-out-socket">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-base-outSocket">
 *       &lt;sequence>
 *         &lt;element name="copyOfInsocket" type="{hydrograph/engine/jaxb/commontypes}type-outSocket-as-inSocket"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-straight-pull-out-socket", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "copyOfInsocket"
})
@XmlSeeAlso({
    hydrograph.engine.jaxb.limit.TypeOutSocket.class,
    TypeLimitOutSocket.class,
    TypeCloneOutSocket.class,
    hydrograph.engine.jaxb.removedups.TypeOutSocket.class,
    hydrograph.engine.jaxb.sort.TypeOutSocket.class
})
public class TypeStraightPullOutSocket
    extends TypeBaseOutSocket
{

    @XmlElement(required = true)
    protected TypeOutSocketAsInSocket copyOfInsocket;

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

}
