
/*******************************************************************************
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
 *******************************************************************************/

package hydrograph.engine.jaxb.imysql;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import hydrograph.engine.jaxb.commontypes.TypeInputComponent;
import hydrograph.engine.jaxb.inputtypes.Mysql;


/**
 * <p>Java class for type-input-mysql-base complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="type-input-mysql-base">
 *   &lt;complexContent>
 *     &lt;restriction base="{hydrograph/engine/jaxb/commontypes}type-input-component">
 *       &lt;sequence>
 *         &lt;element name="outSocket" type="{hydrograph/engine/jaxb/imysql}type-input-mysql-out-socket"/>
 *         &lt;element name="runtimeProperties" type="{hydrograph/engine/jaxb/commontypes}type-properties" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "type-input-mysql-base", namespace = "hydrograph/engine/jaxb/imysql")
@XmlSeeAlso({
    Mysql.class
})
public class TypeInputMysqlBase
    extends TypeInputComponent
{


}
