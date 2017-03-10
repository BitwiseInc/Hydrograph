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

import cascading.tap.TapException;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.coerce.Coercions;
import cascading.tuple.coerce.StringCoerce;
import cascading.tuple.type.CoercibleType;
import cascading.tuple.type.DateType;

import java.io.IOException;
import java.lang.reflect.Type;

public class FixedWidthHelper {

	@SuppressWarnings("rawtypes")
	public static Object[] splitLine(Fields sourceFields, String line,
			int[] lengths, Type[] types, boolean safe, boolean strict) {

		String[] tokens = new String[lengths.length];
		int counter = 0;
		int parsedLength = 0;
		int availableLength;
		String token;
		for (int tokenLength : lengths) {
			availableLength = line.length() - parsedLength;
			if (tokenLength > availableLength) {
				// if we are going beyond the line limit then we should check
				// strict
				if (strict) {
					String message = "Input line does not have enough length to parse all fields. Input length is "
							+ line.length()
							+ ". Length required to parse "
							+ (counter + 1)
							+ " field is "
							+ (parsedLength + tokenLength)
							+ "\nLine being parsed: " + line;
					// trap actual line data
					throw new TapException(message, new Tuple(line));
				}
				// if its not strict then just parse whatever we can or put null
				if (availableLength > 0) {
					try {
						token = line.substring(parsedLength, parsedLength
								+ tokenLength);
					} catch (Exception ex) {
						String message = "Input line does not have enough length to parse all fields. Input length is "
								+ line.length()
								+ ". Length required to parse "
								+ (counter + 1)
								+ " field is "
								+ (parsedLength + tokenLength)
								+ "\nLine being parsed: " + line;
						// trap actual line data
						throw new TapException(message, new Tuple(line));
					}
					parsedLength = line.length();
				} else {
					token = null;
				}
			} else {
				// if we still have room then just move on
				token = line
						.substring(parsedLength, parsedLength + tokenLength);
				parsedLength = parsedLength + tokenLength;
			}
			tokens[counter] = token;
			counter = counter + 1;
		}

		if (parsedLength != line.length() && strict) {
			String message = "Input line length ("
					+ line.length()
					+ ") is not matching with parsed data length ("
					+ parsedLength
					+ "). If it is ok to have this situation then set strict to false and try again."
					+ "\nLine being parsed: " + line;
			// trap actual line data
			throw new TapException(message, new Tuple(line));
		}

		// assign the field types, if any.
		Type[] fieldDataTypes = sourceFields.getTypes();
		// if field types is not present then check for datatypes provided in
		// scheme
		if (sourceFields.getTypes() == null && types != null) {
			fieldDataTypes = types;
		}
		int fieldSize = sourceFields.size();

		// if both the datatypes are not present then assign string to all.
		if (fieldDataTypes == null) {
			fieldDataTypes = new Type[fieldSize];
			for (int i = 0; i < fieldSize; i++) {
				fieldDataTypes[i] = String.class;
			}
		}
		CoercibleType[] coercions = Coercions.coercibleArray(fieldSize,
				fieldDataTypes);
		return coerceParsedTokens(sourceFields, line, safe, tokens,
				fieldDataTypes, coercions);
	}

	@SuppressWarnings("rawtypes")
	private static Object[] coerceParsedTokens(Fields sourceFields,
			String line, boolean safe, String[] tokens, Type[] fieldDataTypes,
			CoercibleType[] coercions) {
		Object[] coercedTokens = new Object[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			try {
				if (coercions[i] instanceof StringCoerce) {
					coercedTokens[i] = coercions[i].canonical(tokens[i]);
				} else
					coercedTokens[i] = coercions[i].canonical(tokens[i].trim()
							.length() > 0 ? tokens[i].trim() : null);
			} catch (Exception exception) {
				String message = "field " + sourceFields.get(i)
						+ " cannot be coerced from : " + tokens[i] + " to: "
						+ fieldDataTypes[i];
				coercedTokens[i] = null;
				if (!safe) {
					// trap actual line data
					throw new TapException(message, exception, new Tuple(line));
				}
			}
		}
		return coercedTokens;
	}

	private static void appendFiller(Appendable buffer, char filler, int times)
			throws IOException {
		for (int i = 0; i < times; i++) {
			buffer.append(filler);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Appendable createLine(Iterable tuple, Appendable buffer,
			char filler, int[] len, boolean strict, Type[] types,
			Fields sinkFields) throws IOException {
		int count = -1;

		for (Object value : tuple) {
			count++;

			// to apply datatype while writing the file
			if (types[count] instanceof DateType) {
				value = Coercions.coercibleTypeFor(types[count]).coerce(value,
						String.class);
			} else {
				value = Coercions.coercibleTypeFor(types[count]).canonical(
						value);
			}

			// set blank for null
			if (value == null) {
				value = "";
			}

			int lengthDifference = value.toString().length() - len[count];

			if (lengthDifference == 0) {
				buffer.append(value.toString());
			} else if (lengthDifference > 0) {
				if (strict) {
					throw new TapException(
							"Fixed width write error. Field "
									+ sinkFields.get(count)
									+ " has length "
									+ value.toString().length()
									+ " whereas provided is "
									+ len[count]
									+ ". Set strict to false and provide filler to overide such errors if this is expected behaviour."
									+ "\nLine being parsed: " + tuple,
							new Tuple(tuple.toString()));
				}
				buffer.append(value.toString().substring(0, len[count]));
			} else if (lengthDifference < 0) {

				if (isNumeric(value)) {
					appendZero(buffer, lengthDifference * -1);
					buffer.append(value.toString());
				} else {
					buffer.append(value.toString());
					appendFiller(buffer, filler, lengthDifference * -1);
				}
			}
		}
		return buffer;
	}

	private static boolean isNumeric(Object value) {
		return value instanceof Number;
	}

	private static void appendZero(Appendable buffer, int times)
			throws IOException {
		char filler = ' ';
		for (int i = 0; i < times; i++) {
			buffer.append(filler);
		}
	}
}