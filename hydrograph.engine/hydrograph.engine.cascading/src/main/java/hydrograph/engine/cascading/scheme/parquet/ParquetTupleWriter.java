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
package hydrograph.engine.cascading.scheme.parquet;

import cascading.tuple.TupleEntry;
import jodd.typeconverter.Convert;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parquet.hadoop.api.WriteSupport;
import parquet.io.api.Binary;
import parquet.io.api.RecordConsumer;
import parquet.schema.MessageType;
import parquet.schema.MessageTypeParser;
import parquet.schema.Type;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class ParquetTupleWriter extends WriteSupport<TupleEntry> {

	private static final long MILLIS_PER_DAY = TimeUnit.DAYS.toMillis(1);
	private static final Logger LOG = LoggerFactory
			.getLogger(ParquetTupleWriter.class);

	private static final ThreadLocal<TimeZone> LOCAL_TIMEZONE = new ThreadLocal<TimeZone>() {
		@Override
		protected TimeZone initialValue() {
			return Calendar.getInstance().getTimeZone();
		}
	};

	private RecordConsumer recordConsumer;
	private MessageType rootSchema;
	public static final String PARQUET_CASCADING_SCHEMA = "parquet.cascading.schema";

	@Override
	public WriteContext init(Configuration configuration) {
		String schema = configuration.get(PARQUET_CASCADING_SCHEMA);
		rootSchema = MessageTypeParser.parseMessageType(schema);
		return new WriteContext(rootSchema, new HashMap<String, String>());
	}

	@Override
	public void prepareForWrite(RecordConsumer recordConsumer) {
		this.recordConsumer = recordConsumer;
	}

	@Override
	public void write(TupleEntry record) {
		recordConsumer.startMessage();
		final List<Type> fields = rootSchema.getFields();

		for (int i = 0; i < fields.size(); i++) {
			Type field = fields.get(i);

			if (record == null || record.getObject(field.getName()) == null) {
				continue;
			}
			recordConsumer.startField(field.getName(), i);
			if (field.isPrimitive()) {
				writePrimitive(record, field);
			} else {
				throw new UnsupportedOperationException(
						"Complex type not implemented");
			}
			recordConsumer.endField(field.getName(), i);
		}
		recordConsumer.endMessage();
	}

	private void writePrimitive(TupleEntry record, Type field) {
		String type;
		if (field.asPrimitiveType().getPrimitiveTypeName().name().toUpperCase()
				.equals("INT32")
				&& field.getOriginalType() != null
				&& field.getOriginalType().name().toUpperCase().equals("DATE"))
			type = "DATE";
		else
			type = field.asPrimitiveType().getPrimitiveTypeName().name();

		switch (type) {
		case "BINARY":
			recordConsumer.addBinary(Binary.fromString(record.getString(field
					.getName())));
			break;
		case "BOOLEAN":
			recordConsumer.addBoolean(record.getBoolean(field.getName()));
			break;
		case "INT32":
			recordConsumer.addInteger(record.getInteger(field.getName()));
			break;
		case "INT64":
			recordConsumer.addLong(record.getLong(field.getName()));
			break;
		case "DOUBLE":
			recordConsumer.addDouble(record.getDouble(field.getName()));
			break;
		case "FLOAT":
			recordConsumer.addFloat(record.getFloat(field.getName()));
			break;
		case "FIXED_LEN_BYTE_ARRAY":
			BigDecimal bg = (BigDecimal) record.getObject(field.getName());
			recordConsumer.addBinary(decimalToBinary(bg, field));
			break;
		case "INT96":
			throw new UnsupportedOperationException(
					"Int96 type not implemented");
		case "DATE":
			recordConsumer.addInteger(millisToDays(record.getLong(field
					.getName())));
			break;
		default:
			throw new UnsupportedOperationException(field.getName()
					+ " type not implemented");
		}
	}

	public static int millisToDays(long millisLocal) {
		long millisUtc = millisLocal
				+ LOCAL_TIMEZONE.get().getOffset(millisLocal);
		int days;
		if (millisUtc >= 0L) {
			days = (int) (millisUtc / MILLIS_PER_DAY);
		} else {
			days = (int) ((millisUtc - 86399999) / MILLIS_PER_DAY);
		}
		return days;
	}

	private Binary decimalToBinary(final BigDecimal hiveDecimal, Type field) {
		int prec = field.asPrimitiveType().getDecimalMetadata().getPrecision();
		int scale = field.asPrimitiveType().getDecimalMetadata().getScale();
		byte[] decimalBytes = hiveDecimal.setScale(scale).unscaledValue()
				.toByteArray();

		// Estimated number of bytes needed.
		int precToBytes = ParquetHiveSerDe.PRECISION_TO_BYTE_COUNT[prec - 1];
		if (precToBytes == decimalBytes.length) {
			// No padding needed.
			return Binary.fromByteArray(decimalBytes);
		}

		byte[] tgt = new byte[precToBytes];
		if (hiveDecimal.signum() == -1) {
			// For negative number, initializing bits to 1
			for (int i = 0; i < precToBytes; i++) {
				tgt[i] |= 0xFF;
			}
		}

		System.arraycopy(decimalBytes, 0, tgt, precToBytes
				- decimalBytes.length, decimalBytes.length); // Padding leading
		LOG.debug(Convert.toString(tgt.length));
		LOG.debug(hiveDecimal.toString());// zeroes/ones.
		return Binary.fromByteArray(tgt);
	}

}
