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
package hydrograph.engine.spark.recordreader;

import hydrograph.engine.core.constants.Constants;
import hydrograph.engine.spark.helper.DelimitedAndFixedWidthHelper;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * The Class DelimitedAndFixedWidthRecordReader.
 *
 * @author Bitwise
 *
 */
public class DelimitedAndFixedWidthRecordReader implements
		RecordReader<LongWritable, Text> {

	private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;
	String charsetName = "UTF-8";
	String quote;
	StringBuilder stringBuilder;
	long start;
	long end;
	long pos;
	FileSystem fs;
	FSDataInputStream fileIn;
	private String[] lengthsAndDelimiters, lengthsAndDelimitersType;
	final Path file;
	InputStreamReader inputStreamReader;
	char[] singleChar, multipleChars;
	boolean isQuotePresent = false;

	public DelimitedAndFixedWidthRecordReader(JobConf conf, FileSplit split)
			throws IOException {
		lengthsAndDelimiters = DelimitedAndFixedWidthHelper
				.modifyIdentifier(conf.get("lengthsAndDelimiters").split(Constants.LENGTHS_AND_DELIMITERS_SEPARATOR));
		lengthsAndDelimitersType = conf.get("lengthsAndDelimitersType").split(Constants.LENGTHS_AND_DELIMITERS_SEPARATOR);
		quote = conf.get("quote");
		charsetName = conf.get("charsetName");
		start = split.getStart();
		pos = start;
		end = start + split.getLength();
		file = split.getPath();
		fs = file.getFileSystem(conf);
		fileIn = fs.open(split.getPath());
		fileIn.seek(start);
		inputStreamReader = new InputStreamReader(fileIn, charsetName);
		singleChar = new char[1];
		stringBuilder = new StringBuilder();
		isQuotePresent = isQuotePresent(quote);
	}

	private boolean isQuotePresent(String string) {
		if (string != null) {
			if (string.equals(""))
				return false;
			else
				return true;
		}
		return false;
	}

	@Override
	public void close() throws IOException {
		inputStreamReader.close();
		fileIn.close();
	}

	@Override
	public LongWritable createKey() {
		return new LongWritable();
	}

	@Override
	public Text createValue() {
		return new Text("");
	}

	@Override
	public synchronized float getProgress() throws IOException {
		if (pos == end)
			return 0.0f;
		else {
			return Math.min(1.0f, (pos - start) / (float) (end - start));
		}
	}

	@Override
	public synchronized boolean next(LongWritable key, Text value)
			throws IOException, RuntimeException {
		boolean fieldNotFound, isMatchingDelimiterInProgress = false, isSecondLastCharNewline = false, isThirdLastCharNewline = false;
		boolean quoteCharFound = false;
		int fieldLength, delimiterCharCounter;
		stringBuilder.setLength(0);
		if (!isEOFEncountered() && !isSecondLastCharNewline
				&& !isThirdLastCharNewline) {
			for (int i = 0; i < lengthsAndDelimiters.length
					&& !isSecondLastCharNewline && !isThirdLastCharNewline; i++) {
				if (lengthsAndDelimitersType[i].contains("Integer")) {
					fieldLength = Integer.parseInt(lengthsAndDelimiters[i]);
					if (!(pos + fieldLength > end)) {
						multipleChars = new char[fieldLength];
						inputStreamReader.read(multipleChars);
						pos += new String(multipleChars).getBytes(charsetName).length;
						stringBuilder.append(multipleChars);
					} else if ((isSecondLastChar() && isSecondLastCharNewline())
							|| (isThirdLastChar() && isThirdLastCharNewline())) {
						stringBuilder.setLength(0);
						isSecondLastCharNewline = true;
						isThirdLastCharNewline = true;
					} else {
						String message = "The input data is not according to specified schema. Expected data with delimiters or lengths as "
								+ Arrays.toString(lengthsAndDelimiters)
								+ ", got: " + stringBuilder.toString();
						throw new RuntimeException(message);
					}
				} else {
					fieldNotFound = true;
					delimiterCharCounter = 0;
					do {
						if (!isEOFEncountered()) {
							inputStreamReader.read(singleChar);
							pos += new String(singleChar).getBytes(charsetName).length;
							if (isQuotePresent) {
								if (isQuoteChar(singleChar[0]) == true) {
									quoteCharFound = !quoteCharFound;
								}
							}
							if (!quoteCharFound) {
								if (lengthsAndDelimiters[i]
										.charAt(delimiterCharCounter) == singleChar[0]) {
									if (++delimiterCharCounter == lengthsAndDelimiters[i]
											.length()) {
										fieldNotFound = false;
									}
									isMatchingDelimiterInProgress = true;
								} else if (isMatchingDelimiterInProgress) {
									isMatchingDelimiterInProgress = false;
									delimiterCharCounter = 0;
								}
							}
							stringBuilder.append(singleChar);
						} else if ((stringBuilder.toString().length() == 1 || stringBuilder
								.toString().length() == 2)
								&& (stringBuilder.toString().contentEquals(
										"\r\n") || stringBuilder.toString()
										.contentEquals("\n"))) {
							fieldNotFound = false;
							stringBuilder.setLength(0);
							isSecondLastCharNewline = true;
							isThirdLastCharNewline = true;
						} else {
							fieldNotFound = false;
							String message = "The input data is not according to specified schema. Expected data with delimiters or lengths as: "
									+ Arrays.toString(lengthsAndDelimiters)
									+ ", got: " + stringBuilder.toString();
							throw new RuntimeException(message);
						}
					} while (fieldNotFound);
				}
			}
		} else {
			return false;
		}
		if (!isThirdLastCharNewline && !isSecondLastCharNewline) {
			value.set(stringBuilder.toString());
			return true;
		} else {
			return false;
		}

	}

	private boolean isQuoteChar(char c) {
		if (quote.charAt(0) == c) {
			return true;
		}
		return false;
	}

	private boolean isThirdLastCharNewline() throws IOException {
		inputStreamReader.read(singleChar);
		pos += new String(singleChar).getBytes(charsetName).length;
		stringBuilder.append(singleChar);
		return stringBuilder.toString().contentEquals("\r");
	}

	private boolean isThirdLastChar() {
		return pos == end - 2;
	}

	private boolean isSecondLastCharNewline() throws IOException {
		inputStreamReader.read(singleChar);
		pos += new String(singleChar).getBytes(charsetName).length;
		stringBuilder.append(singleChar);
		return stringBuilder.toString().contentEquals("\n");
	}

	private boolean isSecondLastChar() {
		return pos == end - 1;
	}

	@Override
	public synchronized long getPos() throws IOException {
		return pos;
	}

	private boolean isEOFEncountered() {
		return pos >= end;
	}
}