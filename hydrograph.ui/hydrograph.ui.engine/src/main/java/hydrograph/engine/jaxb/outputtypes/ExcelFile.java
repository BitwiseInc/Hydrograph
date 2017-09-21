
package hydrograph.engine.jaxb.outputtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.StandardCharsets;
import hydrograph.engine.jaxb.commontypes.TypeTrueFalse;
import hydrograph.engine.jaxb.exceltype.TypeFileExcelBase;


/**
 * <p>Java class for excelFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="excelFile">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/exceltype}type-file-excel-base">
 *       &lt;sequence>
 *         &lt;element name="sourcePath">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="targetPath">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="delimiter">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="overWrite" type="{hydrograph/engine/jaxb/commontypes}type-true-false"/>
 *         &lt;element name="templateExcelPath">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="uri" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="stripLeadingQuote" type="{hydrograph/engine/jaxb/commontypes}type-true-false"/>
 *         &lt;element name="autoColumnSize" type="{hydrograph/engine/jaxb/commontypes}type-true-false"/>
 *         &lt;element name="abortOnError" type="{hydrograph/engine/jaxb/commontypes}type-true-false"/>
 *         &lt;element name="charset" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}standard-charsets" />
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
@XmlType(name = "excelFile", namespace = "hydrograph/engine/jaxb/outputtypes", propOrder = {
    "sourcePath",
    "targetPath",
    "delimiter",
    "overWrite",
    "templateExcelPath",
    "stripLeadingQuote",
    "autoColumnSize",
    "abortOnError",
    "charset"
})
public class ExcelFile
    extends TypeFileExcelBase
{

    @XmlElement(required = true)
    protected ExcelFile.SourcePath sourcePath;
    @XmlElement(required = true)
    protected ExcelFile.TargetPath targetPath;
    @XmlElement(required = true)
    protected ExcelFile.Delimiter delimiter;
    @XmlElement(required = true)
    protected TypeTrueFalse overWrite;
    @XmlElement(required = true)
    protected ExcelFile.TemplateExcelPath templateExcelPath;
    @XmlElement(required = true)
    protected TypeTrueFalse stripLeadingQuote;
    @XmlElement(required = true)
    protected TypeTrueFalse autoColumnSize;
    @XmlElement(required = true)
    protected TypeTrueFalse abortOnError;
    protected ExcelFile.Charset charset;

    /**
     * Gets the value of the sourcePath property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.SourcePath }
     *     
     */
    public ExcelFile.SourcePath getSourcePath() {
        return sourcePath;
    }

    /**
     * Sets the value of the sourcePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.SourcePath }
     *     
     */
    public void setSourcePath(ExcelFile.SourcePath value) {
        this.sourcePath = value;
    }

    /**
     * Gets the value of the targetPath property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.TargetPath }
     *     
     */
    public ExcelFile.TargetPath getTargetPath() {
        return targetPath;
    }

    /**
     * Sets the value of the targetPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.TargetPath }
     *     
     */
    public void setTargetPath(ExcelFile.TargetPath value) {
        this.targetPath = value;
    }

    /**
     * Gets the value of the delimiter property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.Delimiter }
     *     
     */
    public ExcelFile.Delimiter getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the value of the delimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.Delimiter }
     *     
     */
    public void setDelimiter(ExcelFile.Delimiter value) {
        this.delimiter = value;
    }

    /**
     * Gets the value of the overWrite property.
     * 
     * @return
     *     possible object is
     *     {@link TypeTrueFalse }
     *     
     */
    public TypeTrueFalse getOverWrite() {
        return overWrite;
    }

    /**
     * Sets the value of the overWrite property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeTrueFalse }
     *     
     */
    public void setOverWrite(TypeTrueFalse value) {
        this.overWrite = value;
    }

    /**
     * Gets the value of the templateExcelPath property.
     * 
     * @return
     *     possible object is
     *     {@link ExcelFile.TemplateExcelPath }
     *     
     */
    public ExcelFile.TemplateExcelPath getTemplateExcelPath() {
        return templateExcelPath;
    }

    /**
     * Sets the value of the templateExcelPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcelFile.TemplateExcelPath }
     *     
     */
    public void setTemplateExcelPath(ExcelFile.TemplateExcelPath value) {
        this.templateExcelPath = value;
    }

    /**
     * Gets the value of the stripLeadingQuote property.
     * 
     * @return
     *     possible object is
     *     {@link TypeTrueFalse }
     *     
     */
    public TypeTrueFalse getStripLeadingQuote() {
        return stripLeadingQuote;
    }

    /**
     * Sets the value of the stripLeadingQuote property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeTrueFalse }
     *     
     */
    public void setStripLeadingQuote(TypeTrueFalse value) {
        this.stripLeadingQuote = value;
    }

    /**
     * Gets the value of the autoColumnSize property.
     * 
     * @return
     *     possible object is
     *     {@link TypeTrueFalse }
     *     
     */
    public TypeTrueFalse getAutoColumnSize() {
        return autoColumnSize;
    }

    /**
     * Sets the value of the autoColumnSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeTrueFalse }
     *     
     */
    public void setAutoColumnSize(TypeTrueFalse value) {
        this.autoColumnSize = value;
    }

    /**
     * Gets the value of the abortOnError property.
     * 
     * @return
     *     possible object is
     *     {@link TypeTrueFalse }
     *     
     */
    public TypeTrueFalse getAbortOnError() {
        return abortOnError;
    }

    /**
     * Sets the value of the abortOnError property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeTrueFalse }
     *     
     */
    public void setAbortOnError(TypeTrueFalse value) {
        this.abortOnError = value;
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
     *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Delimiter {

        @XmlAttribute(name = "value", required = true)
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
    public static class SourcePath {

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
    public static class TargetPath {

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
     *       &lt;attribute name="uri" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TemplateExcelPath {

        @XmlAttribute(name = "uri")
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

}
