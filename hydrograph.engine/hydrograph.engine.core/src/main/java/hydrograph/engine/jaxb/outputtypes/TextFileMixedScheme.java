
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
package hydrograph.engine.jaxb.outputtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.BooleanValueType;
import hydrograph.engine.jaxb.commontypes.StandardCharsets;
import hydrograph.engine.jaxb.ofmixedscheme.TypeMixedBase;


/**
 * <p>Java class for textFileMixedScheme complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="textFileMixedScheme">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ofmixedscheme}type-mixed-base">
 *       &lt;sequence>
 *         &lt;element name="path">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="safe" type="{hydrograph/engine/jaxb/commontypes}boolean-value-type" minOccurs="0"/>
 *         &lt;element name="strict" type="{hydrograph/engine/jaxb/commontypes}boolean-value-type" minOccurs="0"/>
 *         &lt;element name="charset" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}standard-charsets" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="quote" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlType(name = "textFileMixedScheme", namespace = "hydrograph/engine/jaxb/outputtypes", propOrder = {
    "path",
    "safe",
    "strict",
    "charset",
    "quote"
})
public class TextFileMixedScheme
    extends TypeMixedBase
{

    @XmlElement(required = true)
    protected TextFileMixedScheme.Path path;
    protected BooleanValueType safe;
    protected BooleanValueType strict;
    protected TextFileMixedScheme.Charset charset;
    protected TextFileMixedScheme.Quote quote;

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link TextFileMixedScheme.Path }
     *     
     */
    public TextFileMixedScheme.Path getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextFileMixedScheme.Path }
     *     
     */
    public void setPath(TextFileMixedScheme.Path value) {
        this.path = value;
    }

    /**
     * Gets the value of the safe property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanValueType }
     *     
     */
    public BooleanValueType getSafe() {
        return safe;
    }

    /**
     * Sets the value of the safe property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanValueType }
     *     
     */
    public void setSafe(BooleanValueType value) {
        this.safe = value;
    }

    /**
     * Gets the value of the strict property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanValueType }
     *     
     */
    public BooleanValueType getStrict() {
        return strict;
    }

    /**
     * Sets the value of the strict property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanValueType }
     *     
     */
    public void setStrict(BooleanValueType value) {
        this.strict = value;
    }

    /**
     * Gets the value of the charset property.
     * 
     * @return
     *     possible object is
     *     {@link TextFileMixedScheme.Charset }
     *     
     */
    public TextFileMixedScheme.Charset getCharset() {
        return charset;
    }

    /**
     * Sets the value of the charset property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextFileMixedScheme.Charset }
     *     
     */
    public void setCharset(TextFileMixedScheme.Charset value) {
        this.charset = value;
    }

    /**
     * Gets the value of the quote property.
     * 
     * @return
     *     possible object is
     *     {@link TextFileMixedScheme.Quote }
     *     
     */
    public TextFileMixedScheme.Quote getQuote() {
        return quote;
    }

    /**
     * Sets the value of the quote property.
     * 
     * @param value
     *     allowed object is
     *     {@link TextFileMixedScheme.Quote }
     *     
     */
    public void setQuote(TextFileMixedScheme.Quote value) {
        this.quote = value;
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
     *       &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}standard-charsets" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Charset {

        @XmlAttribute(name = "value", required = true)
        protected StandardCharsets value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link StandardCharsets }
         *     
         */
        public StandardCharsets getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link StandardCharsets }
         *     
         */
        public void setValue(StandardCharsets value) {
            this.value = value;
        }

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
     *       &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Path {

        @XmlAttribute(name = "uri", required = true)
        protected String uri;

        /**
         * Gets the value of the uri property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUri() {
            return uri;
        }

        /**
         * Sets the value of the uri property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUri(String value) {
            this.uri = value;
        }

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
     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Quote {

        @XmlAttribute(name = "value")
        protected String value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

}
