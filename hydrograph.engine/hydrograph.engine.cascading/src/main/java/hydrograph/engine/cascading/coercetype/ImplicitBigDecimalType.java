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
package hydrograph.engine.cascading.coercetype;

import cascading.CascadingException;
import cascading.tuple.type.CoercibleType;
import cascading.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ImplicitBigDecimalType implements CoercibleType<BigDecimal> {

	private static final long serialVersionUID = 6712110015613181562L;
	private int scale;
	private boolean isImplicit = false;
	private static Logger LOG = LoggerFactory
			.getLogger(ImplicitBigDecimalType.class);

	public ImplicitBigDecimalType(int scale) {
		this.scale = scale;
	}

	public ImplicitBigDecimalType(int scale, boolean isImplicit) {
		this.scale = scale;
		this.isImplicit = isImplicit;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public BigDecimal canonical(Object value) {
		if (value == null)
			return null;

		Class from = value.getClass();

		try {
			if (this.isImplicit) {
				if (from == BigDecimal.class) {
					return ((BigDecimal) value).movePointLeft(scale).setScale(
							scale);
				} else if (from == Double.class) {
					return BigDecimal.valueOf((Double) value)
							.movePointLeft(scale).setScale(scale);
				} else if (from == BigInteger.class) {
					return new BigDecimal((BigInteger) value).movePointLeft(
							scale).setScale(scale);
				} else if (from == Long.class) {
					return BigDecimal.valueOf((Long) value)
							.movePointLeft(scale).setScale(scale);
				} else if (from == String.class) {
					return new BigDecimal(value.toString().trim())
							.movePointLeft(scale).setScale(scale);
				} else if (from == Integer.class) {
					return BigDecimal.valueOf((int) value).movePointLeft(scale)
							.setScale(scale);
				}
			} else {
				if (from == BigDecimal.class) {
					return ((BigDecimal) value).setScale(scale);
				} else if (from == Double.class) {
					return BigDecimal.valueOf((Double) value).setScale(scale);
				} else if (from == BigInteger.class) {
					return new BigDecimal((BigInteger) value).setScale(scale);
				} else if (from == Long.class) {
					return BigDecimal.valueOf((Long) value).setScale(scale);
				} else if (from == String.class) {
					// BigDecimal throws NumberFormatException for blank string
					if (value.toString().trim().equals(""))
						return null;
					return new BigDecimal(value.toString().trim())
							.setScale(scale);
				} else if (from == Integer.class) {
					return BigDecimal.valueOf((int) value).setScale(scale);
				}
			}
		} catch (Exception e) {
			LOG.error("Exception while coercing BigDecimal from '" + value
					+ "', scale: '" + scale + "', class: '" + from + "'", e);
			throw new RuntimeException(e);
		}
		throw new CascadingException("unknown type coercion requested from: "
				+ Util.getTypeName(from));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object coerce(Object value, Type type) {
		if (value == null)
			return null;

		Class from = value.getClass();

		try {
			if (from == BigDecimal.class) {
				return ((BigDecimal) value).setScale(scale);
			} else if (from == Double.class) {
				return BigDecimal.valueOf((Double) value).setScale(scale);
			} else if (from == BigInteger.class) {
				return new BigDecimal((BigInteger) value).setScale(scale);
			} else if (from == Long.class) {
				return BigDecimal.valueOf((Long) value).setScale(scale);
			} else if (from == String.class) {
				return new BigDecimal(value.toString().trim()).setScale(scale);
			} else if (from == Integer.class) {
				return new BigDecimal((int) value).setScale(scale);
			}
		} catch (Exception e) {
			LOG.error("Exception while coercing BigDecimal from '" + value
					+ "', scale: '" + scale + "', type: '" + type + "'", e);
			throw new RuntimeException(e);
		}

		throw new CascadingException("unknown type coercion requested from: "
				+ Util.getTypeName(from));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getCanonicalType() {
		return BigDecimal.class;
	}
}