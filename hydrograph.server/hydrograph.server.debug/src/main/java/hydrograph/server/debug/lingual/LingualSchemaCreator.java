/*******************************************************************************
 *  Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/
	package hydrograph.server.debug.lingual;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.bind.catalog.Stereotype;
import cascading.lingual.catalog.Format;
import cascading.lingual.catalog.Protocol;
import cascading.lingual.catalog.SchemaCatalog;
import cascading.lingual.catalog.SchemaDef;
import cascading.lingual.catalog.service.FileCatalogService;
import cascading.lingual.platform.PlatformBroker;
import cascading.lingual.platform.PlatformBrokerFactory;
import cascading.lingual.platform.hadoop2.Hadoop2MR1PlatformBroker;
import cascading.tuple.Fields;

/**
 * This class will create catalog, schema, stereotype and table.
 * 
 * @author Santlal and Amiya
 * 
 */
public class LingualSchemaCreator {

	private String resultSchema = "resultSchema";
	private String lingualSchema = null;
	private final String FORMAT = "csv";
	private final String PROTOCOL = "hdfs";
	private final String PLATFORM = "hadoop2-mr1";

	Protocol defineProtocol = Protocol.getProtocol(PROTOCOL);
	Format defineFormat = Format.getFormat(FORMAT);
	Properties properties = new Properties();
	Logger LOG = LoggerFactory.getLogger(LingualSchemaCreator.class);

	public void createCatalog(String linugalMetaDataPath, String schema, String tableName,
			String stereoTypeName, String identifier, String[] fieldName, Type[] fieldType)
					throws IOException, ClassNotFoundException {

		lingualSchema = schema;

		// Setting platform to Hadoop2-mr1
		PlatformBroker platformbrokers = getPlatformBrokerForHadoop2Mr1Platform();

		properties.setProperty("catalog", linugalMetaDataPath);
		properties.setProperty("resultSchema",resultSchema);
	//	properties.setProperty("resultPath", linugalMetaDataPath);

		platformbrokers.getCatalogManager().getSchemaCatalog().getSchemaDef(resultSchema)
				.setIdentifier(linugalMetaDataPath);


		LOG.info("Hadoop2-mr1 platformbroker instance created");

		// Initializing metadata, installing provider and Commit to catalog
		boolean isMetaDataInitialized = platformbrokers.initializeMetaData();

		LOG.info("MetaData is initialized : " + isMetaDataInitialized);

		commitToCatalogAndInstallProvider(platformbrokers, isMetaDataInitialized);

		// Getting FileCatalogService
		FileCatalogService filecatalogservice = getFileCatalogService(properties, platformbrokers);

		// opening SchemaCatalog using FileCatalogService
		SchemaCatalog schemacatalog = filecatalogservice.openSchemaCatalog();

		// adding Schemadefination
		addSchemaDefition(platformbrokers);

		SchemaDef schemadef = getSchemaDefition(platformbrokers);

		// adding stereotype and table
		addStereotypeAndtable(schemadef, fieldName, fieldType, identifier, stereoTypeName, tableName, platformbrokers);

		platformbrokers.commitCatalog();
	}

	/**
	 * This method will add Stereotype and table it will check if stereotype and
	 * table is already exist if yes then remove it, this will happen only when
	 * some body have stop exection in between so when run next time with same
	 * parameter then it will first reduce previous one and create new one
	 * 
	 * @param schemaDef
	 * @param fieldName
	 * @param fieldType
	 * @param identifier
	 * @param stereoTypeName
	 * @param tableName
	 * @param platformbrokers
	 * 
	 */
	private void addStereotypeAndtable(SchemaDef schemaDef, String fieldName[], Type[] fieldType, String identifier,
			String stereoTypeName, String tableName, PlatformBroker platformbrokers) {

		Stereotype<Protocol, Format> stereotype = new Stereotype<Protocol, Format>(defineProtocol, defineFormat,
				stereoTypeName, new Fields(fieldName).applyTypes(fieldType));
		schemaDef.addStereotype(stereotype);

		LOG.info("Stereotype " + stereoTypeName + " added successfully");
		schemaDef.addTable(tableName, identifier, stereotype, defineProtocol, defineFormat);
		LOG.info("Table " + tableName + " added successfully");

	}

	private SchemaDef getSchemaDefition(PlatformBroker platformbrokers) {
		return platformbrokers.getCatalogManager().getSchemaDef(lingualSchema);
	}

	private void addSchemaDefition(PlatformBroker platformbrokers) {

		SchemaDef sd = platformbrokers.getCatalogManager().getSchemaDef(lingualSchema);
		if (sd == null) {
			platformbrokers.getCatalogManager().addSchemaDef(lingualSchema, defineProtocol, defineFormat);
		}
		LOG.info("Schema added successfully.");

	}

	private FileCatalogService getFileCatalogService(Properties properties2, PlatformBroker platformbrokers) {

		FileCatalogService filecatalogservice = new FileCatalogService();
		filecatalogservice.setProperties(properties);
		filecatalogservice.setPlatformBroker(platformbrokers);

		LOG.info("File catalog service is created. ");

		return filecatalogservice;
	}

	private PlatformBroker getPlatformBrokerForHadoop2Mr1Platform() {

		PlatformBroker platformbroker = PlatformBrokerFactory.makeInstance(Hadoop2MR1PlatformBroker.class);
		properties = platformbroker.getProperties();

		return PlatformBrokerFactory.createPlatformBroker(PLATFORM, properties);
	}

	private void commitToCatalogAndInstallProvider(PlatformBroker platformbrokers, boolean isMetaDataInitialized) {

		if (isMetaDataInitialized) {
			platformbrokers.commitCatalog(); // check whether catalog exist if
												// not create it
		}
	}

	public void cleanUp(String schemaName) {
		removeStereotypeAndTableOfProcessSchema(schemaName);
	}

	private void removeStereotypeAndTableOfProcessSchema(String schemaName) {
		PlatformBroker platformbrokers = getPlatformBrokerForHadoop2Mr1Platform();

		Collection<String> stereotypeNames = platformbrokers.getCatalogManager().getSchemaCatalog()
				.getSchemaDef(schemaName).getStereotypeNames();
		for (String stereotypeName : stereotypeNames)
			platformbrokers.getCatalogManager().getSchemaCatalog().removeStereotype(schemaName, stereotypeName);

		Collection<String> tableNames = platformbrokers.getCatalogManager().getSchemaCatalog()
				.getSchemaDef(schemaName).getChildTableNames();
		for (String tableName : tableNames)
			platformbrokers.getCatalogManager().getSchemaCatalog().removeTableDef(schemaName, tableName);

		LOG.info(schemaName + "'sstereotype and table deleted successfully.");
		platformbrokers.commitCatalog();
	}
}
