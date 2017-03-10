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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.cascading.scheme.avro;

import org.apache.avro.Schema;


public class AvroSchemaUtils {

	public static enum AvroTableProperties {
		SCHEMA_LITERAL("avro.schema.literal"), SCHEMA_URL("avro.schema.url"), SCHEMA_NAMESPACE(
				"avro.schema.namespace"), SCHEMA_NAME("avro.schema.name"), SCHEMA_DOC(
				"avro.schema.doc"), AVRO_SERDE_SCHEMA("avro.serde.schema"), SCHEMA_RETRIEVER(
				"avro.schema.retriever");

		private final String propName;

		AvroTableProperties(String propName) {
			this.propName = propName;
		}

		public String getPropName() {
			return this.propName;
		}
	}

	@Deprecated
	public static final String SCHEMA_LITERAL = "avro.schema.literal";
	@Deprecated
	public static final String SCHEMA_URL = "avro.schema.url";
	@Deprecated
	public static final String SCHEMA_NAMESPACE = "avro.schema.namespace";
	@Deprecated
	public static final String SCHEMA_NAME = "avro.schema.name";
	@Deprecated
	public static final String SCHEMA_DOC = "avro.schema.doc";
	@Deprecated
	public static final String AVRO_SERDE_SCHEMA = AvroTableProperties.AVRO_SERDE_SCHEMA
			.getPropName();
	@Deprecated
	public static final String SCHEMA_RETRIEVER = AvroTableProperties.SCHEMA_RETRIEVER
			.getPropName();

	public static final String SCHEMA_NONE = "none";
	public static final String EXCEPTION_MESSAGE = "Neither "
			+ AvroTableProperties.SCHEMA_LITERAL.getPropName() + " nor "
			+ AvroTableProperties.SCHEMA_URL.getPropName()
			+ " specified, can't determine table schema";

	public static Schema getSchemaFor(String str) {
		Schema.Parser parser = new Schema.Parser();
		return parser.parse(str);
	}

}