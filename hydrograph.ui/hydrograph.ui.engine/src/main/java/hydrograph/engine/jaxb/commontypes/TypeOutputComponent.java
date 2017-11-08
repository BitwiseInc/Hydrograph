
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
import hydrograph.engine.jaxb.ofexcel.TypeOutputFileExcelBase;
import hydrograph.engine.jaxb.ofmixedscheme.TypeMixedBase;
import hydrograph.engine.jaxb.ofsubjob.TypeOutputFileDelimitedSubjob;
import hydrograph.engine.jaxb.ofxml.TypeOutputFileXmlBase;
import hydrograph.engine.jaxb.ohivetextfile.TypeOutputHiveTextFileDelimitedBase;
import hydrograph.engine.jaxb.ojdbcupdate.TypeOutputJdbcupdateBase;
import hydrograph.engine.jaxb.omysql.TypeOutputMysqlBase;
import hydrograph.engine.jaxb.ooracle.TypeOutputOracleBase;
import hydrograph.engine.jaxb.oredshift.TypeOutputRedshiftBase;
import hydrograph.engine.jaxb.osparkredshift.TypeOutputSparkredshiftBase;
import hydrograph.engine.jaxb.oteradata.TypeOutputTeradataBase;
import hydrograph.engine.jaxb.otffw.TypeFixedWidthBase;
import hydrograph.engine.jaxb.otfs.TypeOutputFileSequenceBase;
import hydrograph.engine.jaxb.outputtypes.Discard;


/**
 * <p>Java class for type-output-component complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-output-component">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-base-component">
 *       &lt;sequence>
 *         &lt;element name="inSocket" type="{hydrograph/engine/jaxb/commontypes}type-output-inSocket" maxOccurs="unbounded"/>
 *         &lt;element name="overWrite" type="{hydrograph/engine/jaxb/commontypes}type-true-false" minOccurs="0"/>
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
@XmlType(name = "type-output-component", namespace = "hydrograph/engine/jaxb/commontypes", propOrder = {
    "inSocket",
    "overWrite",
    "runtimeProperties"
})
@XmlSeeAlso({
    Discard.class,
    TypeFixedWidthBase.class,
    hydrograph.engine.jaxb.otfd.TypeOutputFileDelimitedBase.class,
    TypeOutputFileSequenceBase.class,
    hydrograph.engine.jaxb.ofparquet.TypeOutputFileDelimitedBase.class,
    hydrograph.engine.jaxb.ohiveparquet.TypeOutputFileDelimitedBase.class,
    TypeOutputHiveTextFileDelimitedBase.class,
    TypeMixedBase.class,
    TypeOutputFileDelimitedSubjob.class,
    hydrograph.engine.jaxb.ofsubjob.TypeOutputFileDelimitedBase.class,
    TypeOutputMysqlBase.class,
    TypeOutputRedshiftBase.class,
    TypeOutputSparkredshiftBase.class,
    TypeOutputOracleBase.class,
    TypeOutputTeradataBase.class,
    TypeOutputFileExcelBase.class,
    TypeOutputFileXmlBase.class,
    TypeOutputJdbcupdateBase.class
})
public abstract class TypeOutputComponent
    extends TypeBaseComponent
{

    @XmlElement(required = true)
    protected List<TypeOutputInSocket> inSocket;
    protected TypeTrueFalse overWrite;
    protected TypeProperties runtimeProperties;

    /**
     * Gets the value of the inSocket property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the inSocket property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInSocket().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeOutputInSocket }
     * 
     * 
     */
    public List<TypeOutputInSocket> getInSocket() {
        if (inSocket == null) {
            inSocket = new ArrayList<TypeOutputInSocket>();
        }
        return this.inSocket;
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
