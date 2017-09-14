
package hydrograph.engine.jaxb.commandtypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for file-operation-choice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="file-operation-choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="upload" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="download" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "file-operation-choice", namespace = "hydrograph/engine/jaxb/commandtypes", propOrder = {
    "upload",
    "download"
})
public class FileOperationChoice {

    protected Object upload;
    protected Object download;

    /**
     * Gets the value of the upload property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getUpload() {
        return upload;
    }

    /**
     * Sets the value of the upload property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setUpload(Object value) {
        this.upload = value;
    }

    /**
     * Gets the value of the download property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDownload() {
        return download;
    }

    /**
     * Sets the value of the download property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDownload(Object value) {
        this.download = value;
    }

}
