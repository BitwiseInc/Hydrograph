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
package hydrograph.engine.helper;

import hydrograph.engine.core.utilities.FileEncodingEnum;
import hydrograph.engine.core.xmlparser.HydrographXMLInputService;
import hydrograph.engine.jaxb.main.Graph;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestHelperClass {

	HydrographXMLInputService bis;
	static JAXBContext context;
	static Unmarshaller unmarshaller;
	static Graph graph;
	public static final String DEFAULT_ENCODING = "UTF-8";

	public static String getXMLStringFromPath(String path) {
		File xmlFile = new File(path);
		Scanner scan;
		try {
			scan = new Scanner(xmlFile, getXMLEncoding(path));
		} catch (FileNotFoundException e) {

			throw new RuntimeException(e);
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

			throw new RuntimeException(e);
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

			throw new RuntimeException("Unrecognized encoding \"" + encoding + "\" in XML file.");
		}

		return encoding;
	}
}
