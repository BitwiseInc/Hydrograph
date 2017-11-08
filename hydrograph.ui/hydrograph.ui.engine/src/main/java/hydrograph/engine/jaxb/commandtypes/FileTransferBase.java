
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
package hydrograph.engine.jaxb.commandtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.ElementValueIntegerType;
import hydrograph.engine.jaxb.commontypes.StandardCharsets;
import hydrograph.engine.jaxb.commontypes.TypeCommandComponent;


/**
 * <p>Java class for FileTransferBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FileTransferBase">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-command-component">
 *       &lt;sequence>
 *         &lt;element name="host_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="user_Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="input_file_path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="output_file_path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FileOperation" type="{hydrograph/engine/jaxb/commandtypes}file-operation-choice"/>
 *         &lt;element name="encoding" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{hydrograph/engine/jaxb/commontypes}standard-charsets" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="port_No" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type" minOccurs="0"/>
 *         &lt;element name="timeOut" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type" minOccurs="0"/>
 *         &lt;element name="retryAfterDuration" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type" minOccurs="0"/>
 *         &lt;element name="retryAttempt" type="{hydrograph/engine/jaxb/commontypes}element-value-integer-type" minOccurs="0"/>
 *         &lt;element name="failOnError" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="overwritemode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FileTransferBase", namespace = "hydrograph/engine/jaxb/commandtypes", propOrder = {
    "hostName",
    "userName",
    "inputFilePath",
    "outputFilePath",
    "fileOperation",
    "encoding",
    "portNo",
    "timeOut",
    "retryAfterDuration",
    "retryAttempt",
    "failOnError",
    "overwritemode"
})
@XmlSeeAlso({
    FTP.class,
    SFTP.class
})
public abstract class FileTransferBase
    extends TypeCommandComponent
{

    @XmlElement(name = "host_Name", required = true)
    protected String hostName;
    @XmlElement(name = "user_Name", required = true)
    protected String userName;
    @XmlElement(name = "input_file_path", required = true)
    protected String inputFilePath;
    @XmlElement(name = "output_file_path", required = true)
    protected String outputFilePath;
    @XmlElement(name = "FileOperation", required = true)
    protected FileOperationChoice fileOperation;
    protected FileTransferBase.Encoding encoding;
    @XmlElement(name = "port_No")
    protected ElementValueIntegerType portNo;
    protected ElementValueIntegerType timeOut;
    protected ElementValueIntegerType retryAfterDuration;
    protected ElementValueIntegerType retryAttempt;
    protected Boolean failOnError;
    protected String overwritemode;

    /**
     * Gets the value of the hostName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the value of the hostName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostName(String value) {
        this.hostName = value;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * Gets the value of the inputFilePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInputFilePath() {
        return inputFilePath;
    }

    /**
     * Sets the value of the inputFilePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInputFilePath(String value) {
        this.inputFilePath = value;
    }

    /**
     * Gets the value of the outputFilePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutputFilePath() {
        return outputFilePath;
    }

    /**
     * Sets the value of the outputFilePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutputFilePath(String value) {
        this.outputFilePath = value;
    }

    /**
     * Gets the value of the fileOperation property.
     * 
     * @return
     *     possible object is
     *     {@link FileOperationChoice }
     *     
     */
    public FileOperationChoice getFileOperation() {
        return fileOperation;
    }

    /**
     * Sets the value of the fileOperation property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileOperationChoice }
     *     
     */
    public void setFileOperation(FileOperationChoice value) {
        this.fileOperation = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link FileTransferBase.Encoding }
     *     
     */
    public FileTransferBase.Encoding getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileTransferBase.Encoding }
     *     
     */
    public void setEncoding(FileTransferBase.Encoding value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the portNo property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public ElementValueIntegerType getPortNo() {
        return portNo;
    }

    /**
     * Sets the value of the portNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public void setPortNo(ElementValueIntegerType value) {
        this.portNo = value;
    }

    /**
     * Gets the value of the timeOut property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public ElementValueIntegerType getTimeOut() {
        return timeOut;
    }

    /**
     * Sets the value of the timeOut property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public void setTimeOut(ElementValueIntegerType value) {
        this.timeOut = value;
    }

    /**
     * Gets the value of the retryAfterDuration property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public ElementValueIntegerType getRetryAfterDuration() {
        return retryAfterDuration;
    }

    /**
     * Sets the value of the retryAfterDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public void setRetryAfterDuration(ElementValueIntegerType value) {
        this.retryAfterDuration = value;
    }

    /**
     * Gets the value of the retryAttempt property.
     * 
     * @return
     *     possible object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public ElementValueIntegerType getRetryAttempt() {
        return retryAttempt;
    }

    /**
     * Sets the value of the retryAttempt property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementValueIntegerType }
     *     
     */
    public void setRetryAttempt(ElementValueIntegerType value) {
        this.retryAttempt = value;
    }

    /**
     * Gets the value of the failOnError property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFailOnError() {
        return failOnError;
    }

    /**
     * Sets the value of the failOnError property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFailOnError(Boolean value) {
        this.failOnError = value;
    }

    /**
     * Gets the value of the overwritemode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOverwritemode() {
        return overwritemode;
    }

    /**
     * Sets the value of the overwritemode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOverwritemode(String value) {
        this.overwritemode = value;
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
    public static class Encoding {

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

}
