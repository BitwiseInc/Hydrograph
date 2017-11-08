
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
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.ElementValueIntegerType;
import hydrograph.engine.jaxb.commontypes.StandardCharsets;
import hydrograph.engine.jaxb.commontypes.TypeCommandComponent;


/**
 * <p>Java class for S3FileTransfer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="S3FileTransfer">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-command-component">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;group ref="{hydrograph/engine/jaxb/commandtypes}BasicAuth"/>
 *           &lt;element name="crediationalPropertiesFile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *         &lt;element name="region" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="localPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bucketName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="keyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="folder_name_in_bucket" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "S3FileTransfer", namespace = "hydrograph/engine/jaxb/commandtypes", propOrder = {
    "accessKeyID",
    "secretAccessKey",
    "crediationalPropertiesFile",
    "region",
    "localPath",
    "bucketName",
    "keyName",
    "folderNameInBucket",
    "fileOperation",
    "encoding",
    "timeOut",
    "retryAfterDuration",
    "retryAttempt",
    "failOnError",
    "overwritemode"
})
public class S3FileTransfer
    extends TypeCommandComponent
{

    protected String accessKeyID;
    protected String secretAccessKey;
    protected String crediationalPropertiesFile;
    @XmlElement(required = true)
    protected String region;
    @XmlElement(required = true)
    protected String localPath;
    @XmlElement(required = true)
    protected String bucketName;
    protected String keyName;
    @XmlElement(name = "folder_name_in_bucket")
    protected String folderNameInBucket;
    @XmlElement(name = "FileOperation", required = true)
    protected FileOperationChoice fileOperation;
    protected S3FileTransfer.Encoding encoding;
    protected ElementValueIntegerType timeOut;
    protected ElementValueIntegerType retryAfterDuration;
    protected ElementValueIntegerType retryAttempt;
    protected Boolean failOnError;
    protected String overwritemode;

    /**
     * Gets the value of the accessKeyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessKeyID() {
        return accessKeyID;
    }

    /**
     * Sets the value of the accessKeyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessKeyID(String value) {
        this.accessKeyID = value;
    }

    /**
     * Gets the value of the secretAccessKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    /**
     * Sets the value of the secretAccessKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecretAccessKey(String value) {
        this.secretAccessKey = value;
    }

    /**
     * Gets the value of the crediationalPropertiesFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrediationalPropertiesFile() {
        return crediationalPropertiesFile;
    }

    /**
     * Sets the value of the crediationalPropertiesFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrediationalPropertiesFile(String value) {
        this.crediationalPropertiesFile = value;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegion(String value) {
        this.region = value;
    }

    /**
     * Gets the value of the localPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalPath() {
        return localPath;
    }

    /**
     * Sets the value of the localPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalPath(String value) {
        this.localPath = value;
    }

    /**
     * Gets the value of the bucketName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets the value of the bucketName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBucketName(String value) {
        this.bucketName = value;
    }

    /**
     * Gets the value of the keyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Sets the value of the keyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyName(String value) {
        this.keyName = value;
    }

    /**
     * Gets the value of the folderNameInBucket property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderNameInBucket() {
        return folderNameInBucket;
    }

    /**
     * Sets the value of the folderNameInBucket property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderNameInBucket(String value) {
        this.folderNameInBucket = value;
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
     *     {@link S3FileTransfer.Encoding }
     *     
     */
    public S3FileTransfer.Encoding getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link S3FileTransfer.Encoding }
     *     
     */
    public void setEncoding(S3FileTransfer.Encoding value) {
        this.encoding = value;
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
