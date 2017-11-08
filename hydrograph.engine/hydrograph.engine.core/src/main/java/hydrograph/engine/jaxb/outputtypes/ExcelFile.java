
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
import hydrograph.engine.jaxb.commontypes.StandardWriteMode;
import hydrograph.engine.jaxb.ofexcel.FieldFormat;
import hydrograph.engine.jaxb.ofexcel.SortKeyFields;
import hydrograph.engine.jaxb.ofexcel.TypeOutputFileExcelBase;


/**
 * <p>Java class for excelFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="excelFile">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/ofexcel}type-output-file-excel-base">
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
 *         &lt;element name="worksheetName" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="IsColumn" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="stripLeadingQuote" type="{hydrograph/engine/jaxb/commontypes}boolean-value-type" minOccurs="0"/>
 *         &lt;element name="autoColumnSize" type="{hydrograph/engine/jaxb/commontypes}boolean-value-type" minOccurs="0"/>
 *         &lt;element name="cellFormat" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="header" type="{hydrograph/engine/jaxb/ofexcel}field-format"/>
 *                   &lt;element name="data" type="{hydrograph/engine/jaxb/ofexcel}field-format"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="writeMode" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}standard-write-mode" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="charset" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}standard-charsets" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="sortKeyFields" type="{hydrograph/engine/jaxb/ofexcel}Sort-Key-fields" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "excelFile", namespace = "hydrograph/engine/jaxb/outputtypes", propOrder = {
    "path",
    "worksheetName",
    "stripLeadingQuote",
    "autoColumnSize",
    "cellFormat",
    "writeMode",
    "charset",
    "sortKeyFields"
})
public class ExcelFile
    extends TypeOutputFileExcelBase
{

    @XmlElement(required = true)
    protected ExcelFile.Path path;
    protected ExcelFile.WorksheetName worksheetName;
    protected BooleanValueType stripLeadingQuote;
    protected BooleanValueType autoColumnSize;
    protected ExcelFile.CellFormat cellFormat;
    protected ExcelFile.WriteMode writeMode;
    protected ExcelFile.Charset charset;
    protected SortKeyFields sortKeyFields;

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.Path }
     *     
     */
    public ExcelFile.Path getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.Path }
     *     
     */
    public void setPath(ExcelFile.Path value) {
        this.path = value;
    }

    /**
     * Gets the value of the worksheetName property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.WorksheetName }
     *     
     */
    public ExcelFile.WorksheetName getWorksheetName() {
        return worksheetName;
    }

    /**
     * Sets the value of the worksheetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.WorksheetName }
     *     
     */
    public void setWorksheetName(ExcelFile.WorksheetName value) {
        this.worksheetName = value;
    }

    /**
     * Gets the value of the stripLeadingQuote property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanValueType }
     *     
     */
    public BooleanValueType getStripLeadingQuote() {
        return stripLeadingQuote;
    }

    /**
     * Sets the value of the stripLeadingQuote property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanValueType }
     *     
     */
    public void setStripLeadingQuote(BooleanValueType value) {
        this.stripLeadingQuote = value;
    }

    /**
     * Gets the value of the autoColumnSize property.
     * 
     * @return
     *     possible object is
     *     {@link BooleanValueType }
     *     
     */
    public BooleanValueType getAutoColumnSize() {
        return autoColumnSize;
    }

    /**
     * Sets the value of the autoColumnSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BooleanValueType }
     *     
     */
    public void setAutoColumnSize(BooleanValueType value) {
        this.autoColumnSize = value;
    }

    /**
     * Gets the value of the cellFormat property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.CellFormat }
     *     
     */
    public ExcelFile.CellFormat getCellFormat() {
        return cellFormat;
    }

    /**
     * Sets the value of the cellFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.CellFormat }
     *     
     */
    public void setCellFormat(ExcelFile.CellFormat value) {
        this.cellFormat = value;
    }

    /**
     * Gets the value of the writeMode property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.WriteMode }
     *     
     */
    public ExcelFile.WriteMode getWriteMode() {
        return writeMode;
    }

    /**
     * Sets the value of the writeMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.WriteMode }
     *     
     */
    public void setWriteMode(ExcelFile.WriteMode value) {
        this.writeMode = value;
    }

    /**
     * Gets the value of the charset property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.Charset }
     *     
     */
    public ExcelFile.Charset getCharset() {
        return charset;
    }

    /**
     * Sets the value of the charset property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.Charset }
     *     
     */
    public void setCharset(ExcelFile.Charset value) {
        this.charset = value;
    }

    /**
     * Gets the value of the sortKeyFields property.
     * 
     * @return
     *     possible object is
     *     {@link SortKeyFields }
     *     
     */
    public SortKeyFields getSortKeyFields() {
        return sortKeyFields;
    }

    /**
     * Sets the value of the sortKeyFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link SortKeyFields }
     *     
     */
    public void setSortKeyFields(SortKeyFields value) {
        this.sortKeyFields = value;
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
     *       &lt;sequence>
     *         &lt;element name="header" type="{hydrograph/engine/jaxb/ofexcel}field-format"/>
     *         &lt;element name="data" type="{hydrograph/engine/jaxb/ofexcel}field-format"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "header",
        "data"
    })
    public static class CellFormat {

        @XmlElement(required = true)
        protected FieldFormat header;
        @XmlElement(required = true)
        protected FieldFormat data;

        /**
         * Gets the value of the header property.
         * 
         * @return
         *     possible object is
         *     {@link FieldFormat }
         *     
         */
        public FieldFormat getHeader() {
            return header;
        }

        /**
         * Sets the value of the header property.
         * 
         * @param value
         *     allowed object is
         *     {@link FieldFormat }
         *     
         */
        public void setHeader(FieldFormat value) {
            this.header = value;
        }

        /**
         * Gets the value of the data property.
         * 
         * @return
         *     possible object is
         *     {@link FieldFormat }
         *     
         */
        public FieldFormat getData() {
            return data;
        }

        /**
         * Sets the value of the data property.
         * 
         * @param value
         *     allowed object is
         *     {@link FieldFormat }
         *     
         */
        public void setData(FieldFormat value) {
            this.data = value;
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
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="IsColumn" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class WorksheetName {

        @XmlAttribute(name = "name", required = true)
        protected String name;
        @XmlAttribute(name = "IsColumn", required = true)
        protected boolean isColumn;

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
         * Gets the value of the isColumn property.
         * 
         */
        public boolean isIsColumn() {
            return isColumn;
        }

        /**
         * Sets the value of the isColumn property.
         * 
         */
        public void setIsColumn(boolean value) {
            this.isColumn = value;
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
     *       &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}standard-write-mode" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class WriteMode {

        @XmlAttribute(name = "value", required = true)
        protected StandardWriteMode value;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link StandardWriteMode }
         *     
         */
        public StandardWriteMode getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link StandardWriteMode }
         *     
         */
        public void setValue(StandardWriteMode value) {
            this.value = value;
        }

    }

}
