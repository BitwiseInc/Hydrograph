
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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.igr.TypeGenerateRecordRecord;
import hydrograph.engine.jaxb.ojdbcupdate.TypeJdbcupdateRecord;
import hydrograph.engine.jaxb.omysql.TypeMysqlRecord;
import hydrograph.engine.jaxb.ooracle.TypeOracleRecord;
import hydrograph.engine.jaxb.oredshift.TypeRedshiftRecord;
import hydrograph.engine.jaxb.osparkredshift.TypeSparkredshiftRecord;
import hydrograph.engine.jaxb.oteradata.TypeTeradataRecord;


/**
 * <p>Java class for type-base-record complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-base-record">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="field" type="{hydrograph/engine/jaxb/commontypes}type-base-field"/>
 *         &lt;element name="record" type="{hydrograph/engine/jaxb/commontypes}type-base-record"/>
 *         &lt;element name="includeExternalSchema" type="{hydrograph/engine/jaxb/commontypes}type-external-schema" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-base-record", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "fieldOrRecordOrIncludeExternalSchema"
})
@XmlSeeAlso({
    hydrograph.engine.jaxb.itffw.TypeFixedwidthRecord.class,
    TypeGenerateRecordRecord.class,
    hydrograph.engine.jaxb.ifmixedscheme.TypeMixedRecord.class,
    hydrograph.engine.jaxb.ifxml.TypeXmlRecord.class,
    hydrograph.engine.jaxb.otffw.TypeFixedwidthRecord.class,
    hydrograph.engine.jaxb.ofmixedscheme.TypeMixedRecord.class,
    TypeMysqlRecord.class,
    TypeRedshiftRecord.class,
    TypeSparkredshiftRecord.class,
    TypeOracleRecord.class,
    TypeTeradataRecord.class,
    hydrograph.engine.jaxb.ofxml.TypeXmlRecord.class,
    TypeJdbcupdateRecord.class
})
public class TypeBaseRecord {

    @XmlElements({
        @XmlElement(name = "field", type = TypeBaseField.class),
        @XmlElement(name = "record", type = TypeBaseRecord.class),
        @XmlElement(name = "includeExternalSchema", type = TypeExternalSchema.class)
    })
    protected List<Object> fieldOrRecordOrIncludeExternalSchema;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the fieldOrRecordOrIncludeExternalSchema property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fieldOrRecordOrIncludeExternalSchema property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFieldOrRecordOrIncludeExternalSchema().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeBaseField }
     * {@link TypeBaseRecord }
     * {@link TypeExternalSchema }
     * 
     * 
     */
    public List<Object> getFieldOrRecordOrIncludeExternalSchema() {
        if (fieldOrRecordOrIncludeExternalSchema == null) {
            fieldOrRecordOrIncludeExternalSchema = new ArrayList<Object>();
        }
        return this.fieldOrRecordOrIncludeExternalSchema;
    }

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

}
