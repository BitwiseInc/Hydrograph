
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.ifmixedscheme.TypeMixedBase;
import hydrograph.engine.jaxb.ifsubjob.TypeInputFileDelimitedSubjob;
import hydrograph.engine.jaxb.ifxml.TypeInputFileXmlBase;
import hydrograph.engine.jaxb.igr.TypeGenerateRecordBase;
import hydrograph.engine.jaxb.ihivetextfile.TypeInputHiveTextFileDelimitedBase;
import hydrograph.engine.jaxb.imysql.TypeInputMysqlBase;
import hydrograph.engine.jaxb.ioracle.TypeInputOracleBase;
import hydrograph.engine.jaxb.iredshift.TypeInputRedshiftBase;
import hydrograph.engine.jaxb.isparkredshift.TypeInputSparkredshiftBase;
import hydrograph.engine.jaxb.iteradata.TypeInputTeradataBase;
import hydrograph.engine.jaxb.itffw.TypeFixedWidthBase;
import hydrograph.engine.jaxb.itfs.TypeInputFileSequenceBase;


/**
 * <p>Java class for type-input-component complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-input-component">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-base-component">
 *       &lt;sequence>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/commontypes}type-input-outSocket" maxOccurs="unbounded"/>
 *         &lt;element name="runtimeProperties" type="{hydrograph/engine/jaxb/commontypes}type-properties" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-input-component", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "outSocket",
    "runtimeProperties"
})
@XmlSeeAlso({
    TypeFixedWidthBase.class,
    hydrograph.engine.jaxb.itfd.TypeInputFileDelimitedBase.class,
    TypeInputFileSequenceBase.class,
    TypeGenerateRecordBase.class,
    hydrograph.engine.jaxb.ifparquet.TypeInputFileDelimitedBase.class,
    hydrograph.engine.jaxb.ihiveparquet.TypeInputFileDelimitedBase.class,
    TypeInputHiveTextFileDelimitedBase.class,
    TypeMixedBase.class,
    hydrograph.engine.jaxb.ifsubjob.TypeInputFileDelimitedBase.class,
    TypeInputFileDelimitedSubjob.class,
    TypeInputMysqlBase.class,
    TypeInputRedshiftBase.class,
    TypeInputSparkredshiftBase.class,
    TypeInputOracleBase.class,
    TypeInputTeradataBase.class,
    TypeInputFileXmlBase.class
})
public abstract class TypeInputComponent
    extends TypeBaseComponent
{

    @XmlElement(required = true)
    protected List<TypeInputOutSocket> outSocket;
    protected TypeProperties runtimeProperties;

    /**
     * Gets the value of the outSocket property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outSocket property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutSocket().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeInputOutSocket }
     * 
     * 
     */
    public List<TypeInputOutSocket> getOutSocket() {
        if (outSocket == null) {
            outSocket = new ArrayList<TypeInputOutSocket>();
        }
        return this.outSocket;
    }

    /**
     * Gets the value of the runtimeProperties property.
     * 
     * @return
     *     possible object is
     *     {@link TypeProperties }
     *     
     */
    public TypeProperties getRuntimeProperties() {
        return runtimeProperties;
    }

    /**
     * Sets the value of the runtimeProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeProperties }
     *     
     */
    public void setRuntimeProperties(TypeProperties value) {
        this.runtimeProperties = value;
    }

}
