
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

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import hydrograph.engine.jaxb.generatesequence.TypeNameField;
import hydrograph.engine.jaxb.igr.TypeGenerateRecordField;
import hydrograph.engine.jaxb.ojdbcupdate.TypeJdbcupdateField;
import hydrograph.engine.jaxb.omysql.TypeMysqlField;
import hydrograph.engine.jaxb.ooracle.TypeOracleField;
import hydrograph.engine.jaxb.oredshift.TypeRedshiftField;
import hydrograph.engine.jaxb.osparkredshift.TypeSparkredshiftField;
import hydrograph.engine.jaxb.oteradata.TypeTeradataField;


/**
 * <p>Java class for type-base-field complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-base-field">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{hydrograph/engine/jaxb/commontypes}grp-attr-base-field"/>
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-base-field", namespace = "hydrograph/engine/jaxb/commontypes")
@XmlSeeAlso({
    hydrograph.engine.jaxb.itffw.TypeFixedwidthField.class,
    TypeGenerateRecordField.class,
    hydrograph.engine.jaxb.ifmixedscheme.TypeMixedField.class,
    hydrograph.engine.jaxb.ifxml.TypeXmlField.class,
    hydrograph.engine.jaxb.otffw.TypeFixedwidthField.class,
    hydrograph.engine.jaxb.ofmixedscheme.TypeMixedField.class,
    TypeMysqlField.class,
    TypeRedshiftField.class,
    TypeSparkredshiftField.class,
    TypeOracleField.class,
    TypeTeradataField.class,
    hydrograph.engine.jaxb.ofxml.TypeXmlField.class,
    TypeJdbcupdateField.class,
    TypeNameField.class
})
public class TypeBaseField {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "type", required = true)
    protected FieldDataTypes type;
    @XmlAttribute(name = "format")
    protected String format;
    @XmlAttribute(name = "precision")
    protected Integer precision;
    @XmlAttribute(name = "scale")
    protected Integer scale;
    @XmlAttribute(name = "scaleType")
    protected ScaleTypeList scaleType;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link FieldDataTypes }
     *     
     */
    public FieldDataTypes getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link FieldDataTypes }
     *     
     */
    public void setType(FieldDataTypes value) {
        this.type = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the precision property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPrecision() {
        return precision;
    }

    /**
     * Sets the value of the precision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrecision(Integer value) {
        this.precision = value;
    }

    /**
     * Gets the value of the scale property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getScale() {
        return scale;
    }

    /**
     * Sets the value of the scale property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setScale(Integer value) {
        this.scale = value;
    }

    /**
     * Gets the value of the scaleType property.
     * 
     * @return
     *     possible object is
     *     {@link ScaleTypeList }
     *     
     */
    public ScaleTypeList getScaleType() {
        return scaleType;
    }

    /**
     * Sets the value of the scaleType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScaleTypeList }
     *     
     */
    public void setScaleType(ScaleTypeList value) {
        this.scaleType = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
