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
package hydrograph.engine.cascading.tuplegenerator;

import java.math.BigDecimal;

@SuppressWarnings("rawtypes")
public enum FieldTypeEnum implements FieldType {
	STRING(new FieldType() {
		@SuppressWarnings("deprecation")
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			String value;

			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultString(fieldEntity
						.getDefaultValue());
			} else if (fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getRandomString(fieldEntity
						.getFieldLength());
			} else {
				value = DataGenerator.getRandomString();
			}
			return value;
		}
	}),

	INTEGER(new FieldType() {
		@SuppressWarnings("deprecation")
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			int value;
			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultInteger(Integer
						.parseInt(fieldEntity.getDefaultValue()));
			} else if (fieldEntity.getRangeFromValue() != null
					&& fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getIntegerBetween(
						Integer.parseInt(fieldEntity.getRangeFromValue()),
						Integer.parseInt(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getRangeFromValue() != null && fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getIntegerBetween(
						Integer.parseInt(fieldEntity.getRangeFromValue()),
						(int) (Math.pow(10, fieldEntity.getFieldLength()) - 1));
			} else if (fieldEntity.getRangeFromValue() != null) {
				value = DataGenerator.getFromInteger(Integer.parseInt(fieldEntity
						.getRangeFromValue()));
			} else if (fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getToInteger(Integer
						.parseInt(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getRandomInteger(fieldEntity
						.getFieldLength());
			} else {
				value = DataGenerator.getRandomInteger();
			}
			return value;
		}
	}),

	BIGDECIMAL(new FieldType() {
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			BigDecimal value;

			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultBigDecimal(new BigDecimal(
						fieldEntity.getDefaultValue()), fieldEntity
						.getFieldScale());
			} else if (fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getRandomBigDecimal(
						fieldEntity.getFieldScale(),
						fieldEntity.getFieldLength());
			} else if (fieldEntity.getRangeFromValue() != null
					&& fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getBigDecimalBetween(new BigDecimal(
						fieldEntity.getRangeFromValue()), new BigDecimal(
						fieldEntity.getRangeToValue()), fieldEntity
						.getFieldScale());
			} else if (fieldEntity.getRangeFromValue() != null) {
				value = DataGenerator.getFromBigDecimal(new BigDecimal(
						fieldEntity.getRangeFromValue()), fieldEntity
						.getFieldScale());
			} else if (fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getToBigDecimal(new BigDecimal(
						fieldEntity.getRangeToValue()), fieldEntity
						.getFieldScale());
			} else {
				value = DataGenerator.getRandomBigDecimal(fieldEntity
						.getFieldScale());
			}
			return value;
		}
	}),

	DATE(new FieldType() {
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			long value;

			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultLongDate(
						fieldEntity.getFieldFormat(),
						fieldEntity.getDefaultValue());
			} else if (fieldEntity.getRangeFromValue() != null
					&& fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getLongDateBetween(
						fieldEntity.getFieldFormat(),
						fieldEntity.getRangeFromValue(),
						fieldEntity.getRangeToValue());
			} else if (fieldEntity.getRangeFromValue() != null) {
				value = DataGenerator.getFromLongDate(
						fieldEntity.getFieldFormat(),
						fieldEntity.getRangeFromValue());
			} else if (fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getToLongDate(
						fieldEntity.getFieldFormat(),
						fieldEntity.getRangeToValue());
			} else {
				value = DataGenerator.getRandomLongDate(fieldEntity
						.getFieldFormat());
			}
			return value;
		}
	}),
	
	//TODO: UPDATE THE FOLLOWING FIELD TYPES
	
	FLOAT(new FieldType() {
		@SuppressWarnings("deprecation")
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			float value;
			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultFloat(Float
						.parseFloat(fieldEntity.getDefaultValue()));
			} else if (fieldEntity.getRangeFromValue() != null
					&& fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getFloatBetween(
						Float.parseFloat(fieldEntity.getRangeFromValue()),
						Float.parseFloat(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getRangeFromValue() != null && fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getFloatBetween(
						Float.parseFloat(fieldEntity.getRangeFromValue()),
						(float) (Math.pow(10, fieldEntity.getFieldLength()) - 1));
			} else if (fieldEntity.getRangeFromValue() != null) {
				value = DataGenerator.getFromFloat(Float.parseFloat(fieldEntity
						.getRangeFromValue()));
			} else if (fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getToFloat(Float
						.parseFloat(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getRandomFloat(fieldEntity
						.getFieldLength());
			} else {
				value = DataGenerator.getRandomFloat();
			}
			return value;
		}
	}),

	SHORT(new FieldType() {
		@SuppressWarnings("deprecation")
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			short value;
			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultShort(Short
						.parseShort(fieldEntity.getDefaultValue()));
			} else if (fieldEntity.getRangeFromValue() != null
					&& fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getShortBetween(
						Short.parseShort(fieldEntity.getRangeFromValue()),
						Short.parseShort(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getRangeFromValue() != null && fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getShortBetween(
						Short.parseShort(fieldEntity.getRangeFromValue()),
						(short) (Math.pow(10, fieldEntity.getFieldLength()) - 1));
			} else if (fieldEntity.getRangeFromValue() != null) {
				value = DataGenerator.getFromShort(Short.parseShort(fieldEntity
						.getRangeFromValue()));
			} else if (fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getToShort(Short
						.parseShort(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getRandomShort(fieldEntity
						.getFieldLength());
			} else {
				value = DataGenerator.getRandomShort();
			}
			return value;
		}
	}),

	LONG(new FieldType() {
		@SuppressWarnings("deprecation")
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			long value;
			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultLong(Long
						.parseLong(fieldEntity.getDefaultValue()));
			} else if (fieldEntity.getRangeFromValue() != null
					&& fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getLongBetween(
						Long.parseLong(fieldEntity.getRangeFromValue()),
						Long.parseLong(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getRangeFromValue() != null && fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getLongBetween(
						Long.parseLong(fieldEntity.getRangeFromValue()),
						(long) (Math.pow(10, fieldEntity.getFieldLength()) - 1));
			} else if (fieldEntity.getRangeFromValue() != null) {
				value = DataGenerator.getFromLong(Long.parseLong(fieldEntity
						.getRangeFromValue()));
			} else if (fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getToLong(Long
						.parseLong(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getRandomLong(fieldEntity
						.getFieldLength());
			} else {
				value = DataGenerator.getRandomLong();
			}
			return value;
		}
	}),
	
	DOUBLE(new FieldType() {
		@SuppressWarnings("deprecation")
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			double value;
			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultDouble(Double
						.parseDouble(fieldEntity.getDefaultValue()));
			} else if (fieldEntity.getRangeFromValue() != null
					&& fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getDoubleBetween(
						Double.parseDouble(fieldEntity.getRangeFromValue()),
						Double.parseDouble(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getRangeFromValue() != null && fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getDoubleBetween(
						Double.parseDouble(fieldEntity.getRangeFromValue()),
						(double) (Math.pow(10, fieldEntity.getFieldLength()) - 1));
			} else if (fieldEntity.getRangeFromValue() != null) {
				value = DataGenerator.getFromDouble(Double.parseDouble(fieldEntity
						.getRangeFromValue()));
			} else if (fieldEntity.getRangeToValue() != null) {
				value = DataGenerator.getToDouble(Double
						.parseDouble(fieldEntity.getRangeToValue()));
			} else if (fieldEntity.getFieldLength() != 0) {
				value = DataGenerator.getRandomDouble(fieldEntity
						.getFieldLength());
			} else {
				value = DataGenerator.getRandomDouble();
			}
			return value;
		}
	}),

	BOOLEAN(new FieldType() {
		@SuppressWarnings("deprecation")
		@Override
		public Object getFieldValue(FieldEntity fieldEntity) {
			boolean value;
			if (fieldEntity.getDefaultValue() != null) {
				value = DataGenerator.getDefaultBoolean(Boolean
						.parseBoolean(fieldEntity.getDefaultValue()));
			} else {
				value = DataGenerator.getRandomBoolean();
			}
			return value;
		}
	});

	private FieldType fieldType;

	private FieldTypeEnum(FieldType ft) {
		fieldType = ft;
	}

	@Override
	public Object getFieldValue(FieldEntity fieldEntity) {
		return fieldType.getFieldValue(fieldEntity);
	}
}
