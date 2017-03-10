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
package hydrograph.engine.cascading.scheme;

import cascading.scheme.util.DelimitedParser;
import cascading.tap.TapException;
import cascading.tuple.Tuple;
import cascading.tuple.coerce.Coercions;
import cascading.tuple.coerce.StringCoerce;
import cascading.tuple.type.DateType;
import cascading.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created a custom class to trim spaces from numerical and date fields while
 * reading and to apply datatypes while writing data
 * 
 * @author Bhavesh
 *
 */
@SuppressWarnings("rawtypes")
public class HydrographDelimitedParser extends DelimitedParser {

	private static final long serialVersionUID = 4546944494735373827L;
	private static final Logger LOG = LoggerFactory
			.getLogger(HydrographDelimitedParser.class);

	private boolean hasHeader = false;

	public HydrographDelimitedParser(String delimiter, String quote,
			Class[] types) {
		super(delimiter, quote, types);
	}

	public HydrographDelimitedParser(String delimiter, String quote,
			Class[] types, boolean strict, boolean safe) {
		super(delimiter, quote, types, strict, safe);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cascading.scheme.util.DelimitedParser#coerceParsedLine(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	protected Object[] coerceParsedLine(String line, Object[] split) {
		if (types != null) // forced null in ctor
		{
			Object[] result = new Object[split.length];
			for (int i = 0; i < split.length; i++) {
				// Added custom code to apply datatypes
				try {
					if (coercibles[i] instanceof StringCoerce) {
						result[i] = coercibles[i].canonical(split[i]);
					} else {
						result[i] = coercibles[i]
								.canonical(split[i] == null ? null : split[i]
										.toString().trim());
					}
					// End custom code
				} catch (Exception exception) {
					result[i] = null;

					if (!safe) {
						// trap data
						throw new TapException(getSafeMessage(split[i], i),
								exception, new Tuple(line));
					}
					if (LOG.isDebugEnabled())
						LOG.debug(getSafeMessage(split[i], i), exception);
				}
			}
			split = result;
		}
		return split;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cascading.scheme.util.DelimitedParser#joinWithQuote(java.lang.Iterable,
	 * java.lang.Appendable)
	 */
	@Override
	protected Appendable joinWithQuote(Iterable tuple, Appendable buffer)
			throws IOException {
		int count = 0;
		for (Object value : tuple) {

			if (!hasHeader) {
				// to apply datatype while writing the file
				if (!(types[count] instanceof DateType)) {
					value = Coercions.coercibleTypeFor(types[count]).canonical(
							value);
				}
			}

			if (count != 0) {
				buffer.append(delimiter);
			}
			if (value != null) {
				String valueString = value.toString();

				if (valueString.contains(quote)) {
					valueString = valueString.replaceAll(quote, quote + quote);
				}
				if (valueString.contains(delimiter)) {
					valueString = quote + valueString + quote;
				}
				buffer.append(valueString);
			}
			count++;
		}
		hasHeader = false;
		
		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cascading.scheme.util.DelimitedParser#joinNoQuote(java.lang.Iterable,
	 * java.lang.Appendable)
	 */
	@Override
	protected Appendable joinNoQuote(Iterable tuple, Appendable buffer)
			throws IOException {
		int count = 0;

		for (Object value : tuple) {

			if (!hasHeader) {
				// to apply datatype while writing the file
				if (!(types[count] instanceof DateType)) {
					value = Coercions.coercibleTypeFor(types[count]).canonical(
							value);
				}
			}

			if (count != 0)
				buffer.append(delimiter);

			if (value != null)
				buffer.append(value.toString());

			count++;
		}
		hasHeader = false;

		return buffer;
	}

	@Override
	public Appendable joinFirstLine(Iterable iterable, Appendable buffer) {
		hasHeader = true;
		iterable = prepareFields(iterable);

		return joinLine(iterable, buffer);
	}

	private String getSafeMessage(Object object, int i) {
		try {
			return "field " + sourceFields.get(i)
					+ " cannot be coerced from : " + object + " to: "
					+ Util.getTypeName(types[i]);
		} catch (Exception e) {
			return "field pos " + i + " cannot be coerced from: " + object
					+ ", pos has no corresponding field name or coercion type";
		}
	}
}
