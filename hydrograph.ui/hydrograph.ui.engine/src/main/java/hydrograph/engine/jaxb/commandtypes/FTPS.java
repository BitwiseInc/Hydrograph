
/*
 *  Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */

package hydrograph.engine.jaxb.commandtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FTPS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FTPS">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commandtypes}FileTransferBase">
 *       &lt;sequence>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="encryptionMethod">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="SSL"/>
 *               &lt;enumeration value="TLS"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
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
@XmlType(name = "FTPS", namespace = "hydrograph/engine/jaxb/commandtypes", propOrder = {
    "password",
    "encryptionMethod"
})
public class FTPS
    extends FileTransferBase
{

    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String encryptionMethod;

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the encryptionMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncryptionMethod() {
        return encryptionMethod;
    }

    /**
     * Sets the value of the encryptionMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncryptionMethod(String value) {
        this.encryptionMethod = value;
    }

}
