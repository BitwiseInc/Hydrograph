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
package hydrograph.engine.bhse.xmlparser.preprocessor;

import hydrograph.engine.core.xmlparser.XmlParsingUtils;
import hydrograph.engine.core.xmlparser.externalschema.ParseExternalElements;
import hydrograph.engine.core.xmlparser.parametersubstitution.ParameterSubstitutor;
import hydrograph.engine.core.xmlparser.parametersubstitution.UserParameters;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.IOException;
/**
 * The Class ParseExternalElementsTest .
 *
 * @author Bitwise
 */
public class ParseExternalElementsTest {

	private static Logger LOG = LoggerFactory
			.getLogger(ParseExternalElementsTest.class);

	@Test
	public void itShouldGetTheExternalFields() throws ParseException {

		String[] args = new String[] { "-xmlpath",
				"testData/XMLFiles/DelimitedInputAndOutput.xml", "-param",
				"PATH=testData/Input/delimitedInputFile","-param","PATH2=testData/XMLFiles/schema2.xml",
				"-param", "input=input'1","-param", "out&put=output"};
		try {
			ParameterSubstitutor parameterSubstitutor = new ParameterSubstitutor(
					new UserParameters(args));

			String xmlContents = XmlParsingUtils.getXMLStringFromPath(args[1]);

			ParseExternalElements parseExternalElements = new ParseExternalElements(
					parameterSubstitutor.substitute(xmlContents), parameterSubstitutor);

			Document xmlDom = parseExternalElements.getXmlDom();

			NodeList nodes = xmlDom.getElementsByTagName("schema");

			Assert.assertEquals(3,nodes.item(0).getChildNodes().getLength());
		} catch (IOException e) {
			LOG.error("", e);
		}
	}
}