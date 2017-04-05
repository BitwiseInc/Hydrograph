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
package hydrograph.engine.core.xmlparser;

import hydrograph.engine.core.utilities.CommandLineOptionsProcessor;
import hydrograph.engine.core.utilities.FileEncodingEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Scanner;
/**
 * The Class XmlParsingUtils.
 *
 * @author Bitwise
 */
public class XmlParsingUtils {

	public static final String DEFAULT_ENCODING = "UTF-8";
	private static CommandLineOptionsProcessor optionsProcessor = new CommandLineOptionsProcessor();

	private XmlParsingUtils() {
	}

	public static String getXMLStringFromPath(String path) {
		File xmlFile = new File(path);
		Scanner scan;
		try {
			scan = new Scanner(xmlFile, getXMLEncoding(path));
		} catch (FileNotFoundException e) {
			throw new HydrographXMLInputServiceException(e);
		}

		scan.useDelimiter("\\Z");
		String entireContent = scan.next();
		scan.close();

		return entireContent;
	}

	public static String getXMLEncoding(String path) {
		File xmlFile = new File(path);

		Scanner scan;
		try {
			scan = new Scanner(xmlFile);
		} catch (FileNotFoundException e) {

			throw new HydrographXMLInputServiceException(e);
		}

		String XMLprolog = (scan.findWithinHorizon("<\\?.*\\?>", 200) + "").replaceAll(" ", "");

		String encoding;
		if (XMLprolog.matches(".*encoding=\".*")) {
			String rightpart = XMLprolog.split("encoding=\"")[1];
			encoding = rightpart.split("\"")[0];
		} else {
			encoding = DEFAULT_ENCODING;
		}

		scan.close();

		try {
			FileEncodingEnum.valueOf(encoding.replaceAll("-", "_"));
		} catch (IllegalArgumentException e) {
			throw new HydrographXMLInputServiceException("Unrecognized encoding \"" + encoding + "\" in XML file.");
		}

		return encoding;
	}

	public static String getXMLPath(String[] args, Properties config) {

		String path = optionsProcessor.getXMLPath(args);

		if (path != null) {
			return path;
		}

		// if path is not found from command line then check from config
		path = config.getProperty(CommandLineOptionsProcessor.OPTION_XML_PATH);

		// if not found in config also then raise an error, otherwise return
		// from config
		if (path == null) {
			throw new HydrographXMLInputServiceException(
					"No XML file path, either through command line or XMLFilePath input property is provided");
		}
		return path;

	}

	public static String getUDFPath(String[] args) {

		String path = optionsProcessor.getUDFPath(args);

		if (path != null) {
			return path;
		}

		return path;

	}

	public static String getJobId(String[] args) {

		String jobId = optionsProcessor.getJobId(args);

		if (jobId != null) {
			return jobId;
		}

		/*if (jobId == null) {
			// optional param
		}*/
		return jobId;

	}

	public static class HydrographXMLInputServiceException extends RuntimeException {
		private static final long serialVersionUID = -7709930763943833311L;

		public HydrographXMLInputServiceException(String msg) {
			super(msg);
		}

		public HydrographXMLInputServiceException(Throwable e) {
			super(e);
		}
	}
}