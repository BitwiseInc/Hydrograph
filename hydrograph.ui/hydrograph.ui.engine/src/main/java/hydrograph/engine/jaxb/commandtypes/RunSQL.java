
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
import hydrograph.engine.jaxb.commontypes.TypeCommandComponent;


/**
 * <p>Java class for runSQL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="runSQL">
 *   &lt;complexContent>
 *     &lt;extension base="{hydrograph/engine/jaxb/commontypes}type-command-component">
 *       &lt;sequence>
 *         &lt;element name="database_Connection_Name">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="databaseConnectionName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="server_Name">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="ip_address" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="port_Number">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="portNumber" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="database_Name">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="databaseName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="db_User_Name">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="userName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="db_Password">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="queryCommand">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="query" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlType(name = "runSQL", namespace = "hydrograph/engine/jaxb/commandtypes", propOrder = {
    "databaseConnectionName",
    "serverName",
    "portNumber",
    "databaseName",
    "dbUserName",
    "dbPassword",
    "queryCommand"
})
public class RunSQL
    extends TypeCommandComponent
{

    @XmlElement(name = "database_Connection_Name", required = true)
    protected RunSQL.DatabaseConnectionName databaseConnectionName;
    @XmlElement(name = "server_Name", required = true)
    protected RunSQL.ServerName serverName;
    @XmlElement(name = "port_Number", required = true)
    protected RunSQL.PortNumber portNumber;
    @XmlElement(name = "database_Name", required = true)
    protected RunSQL.DatabaseName databaseName;
    @XmlElement(name = "db_User_Name", required = true)
    protected RunSQL.DbUserName dbUserName;
    @XmlElement(name = "db_Password", required = true)
    protected RunSQL.DbPassword dbPassword;
    @XmlElement(required = true)
    protected RunSQL.QueryCommand queryCommand;

    /**
     * Gets the value of the databaseConnectionName property.
     * 
     * @return
     *     possible object is
     *     {@link RunSQL.DatabaseConnectionName }
     *     
     */
    public RunSQL.DatabaseConnectionName getDatabaseConnectionName() {
        return databaseConnectionName;
    }

    /**
     * Sets the value of the databaseConnectionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunSQL.DatabaseConnectionName }
     *     
     */
    public void setDatabaseConnectionName(RunSQL.DatabaseConnectionName value) {
        this.databaseConnectionName = value;
    }

    /**
     * Gets the value of the serverName property.
     * 
     * @return
     *     possible object is
     *     {@link RunSQL.ServerName }
     *     
     */
    public RunSQL.ServerName getServerName() {
        return serverName;
    }

    /**
     * Sets the value of the serverName property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunSQL.ServerName }
     *     
     */
    public void setServerName(RunSQL.ServerName value) {
        this.serverName = value;
    }

    /**
     * Gets the value of the portNumber property.
     * 
     * @return
     *     possible object is
     *     {@link RunSQL.PortNumber }
     *     
     */
    public RunSQL.PortNumber getPortNumber() {
        return portNumber;
    }

    /**
     * Sets the value of the portNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunSQL.PortNumber }
     *     
     */
    public void setPortNumber(RunSQL.PortNumber value) {
        this.portNumber = value;
    }

    /**
     * Gets the value of the databaseName property.
     * 
     * @return
     *     possible object is
     *     {@link RunSQL.DatabaseName }
     *     
     */
    public RunSQL.DatabaseName getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets the value of the databaseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunSQL.DatabaseName }
     *     
     */
    public void setDatabaseName(RunSQL.DatabaseName value) {
        this.databaseName = value;
    }

    /**
     * Gets the value of the dbUserName property.
     * 
     * @return
     *     possible object is
     *     {@link RunSQL.DbUserName }
     *     
     */
    public RunSQL.DbUserName getDbUserName() {
        return dbUserName;
    }

    /**
     * Sets the value of the dbUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunSQL.DbUserName }
     *     
     */
    public void setDbUserName(RunSQL.DbUserName value) {
        this.dbUserName = value;
    }

    /**
     * Gets the value of the dbPassword property.
     * 
     * @return
     *     possible object is
     *     {@link RunSQL.DbPassword }
     *     
     */
    public RunSQL.DbPassword getDbPassword() {
        return dbPassword;
    }

    /**
     * Sets the value of the dbPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunSQL.DbPassword }
     *     
     */
    public void setDbPassword(RunSQL.DbPassword value) {
        this.dbPassword = value;
    }

    /**
     * Gets the value of the queryCommand property.
     * 
     * @return
     *     possible object is
     *     {@link RunSQL.QueryCommand }
     *     
     */
    public RunSQL.QueryCommand getQueryCommand() {
        return queryCommand;
    }

    /**
     * Sets the value of the queryCommand property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunSQL.QueryCommand }
     *     
     */
    public void setQueryCommand(RunSQL.QueryCommand value) {
        this.queryCommand = value;
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
     *       &lt;attribute name="databaseConnectionName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DatabaseConnectionName {

        @XmlAttribute(name = "databaseConnectionName", required = true)
        protected String databaseConnectionName;

        /**
         * Gets the value of the databaseConnectionName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDatabaseConnectionName() {
            return databaseConnectionName;
        }

        /**
         * Sets the value of the databaseConnectionName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDatabaseConnectionName(String value) {
            this.databaseConnectionName = value;
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
     *       &lt;attribute name="databaseName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DatabaseName {

        @XmlAttribute(name = "databaseName", required = true)
        protected String databaseName;

        /**
         * Gets the value of the databaseName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDatabaseName() {
            return databaseName;
        }

        /**
         * Sets the value of the databaseName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDatabaseName(String value) {
            this.databaseName = value;
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
     *       &lt;attribute name="password" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DbPassword {

        @XmlAttribute(name = "password", required = true)
        protected String password;

        /**
         * Gets the value of the password property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPassword() {
            return password;
        }

        /**
         * Sets the value of the password property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPassword(String value) {
            this.password = value;
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
     *       &lt;attribute name="userName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DbUserName {

        @XmlAttribute(name = "userName", required = true)
        protected String userName;

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
     *       &lt;attribute name="portNumber" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PortNumber {

        @XmlAttribute(name = "portNumber")
        protected String portNumber;

        /**
         * Gets the value of the portNumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPortNumber() {
            return portNumber;
        }

        /**
         * Sets the value of the portNumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPortNumber(String value) {
            this.portNumber = value;
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
     *       &lt;attribute name="query" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class QueryCommand {

        @XmlAttribute(name = "query", required = true)
        protected String query;

        /**
         * Gets the value of the query property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getQuery() {
            return query;
        }

        /**
         * Sets the value of the query property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setQuery(String value) {
            this.query = value;
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
     *       &lt;attribute name="ip_address" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ServerName {

        @XmlAttribute(name = "ip_address", required = true)
        protected String ipAddress;

        /**
         * Gets the value of the ipAddress property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIpAddress() {
            return ipAddress;
        }

        /**
         * Sets the value of the ipAddress property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIpAddress(String value) {
            this.ipAddress = value;
        }

    }

}
