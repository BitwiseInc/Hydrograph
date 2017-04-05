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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
/**
 * The Class ComponentValidationEventHandler.
 *
 * @author Bitwise
 */
public class ComponentValidationEventHandler implements ValidationEventHandler {

	public boolean handleEvent(ValidationEvent event) {

		boolean nodeFound = false;
		String id = "";
		String element = "";
		String message1 = "";
		String[] message = event.getMessage().split(":");
		Node node = event.getLocator().getNode();

		while (!nodeFound) {
			// to validate inputs,outputs,operation and straightPulls
			if (node.getLocalName().equals("inputs") || node.getLocalName().equals("outputs")
					|| node.getLocalName().equals("operations") || node.getLocalName().equals("straightPulls")) {

				if (isIdAttributePresent(node.getAttributes())) {
					id = node.getAttributes().getNamedItem("id").getNodeValue().toString();
				} else {
					element = node.getLocalName();
				}
				nodeFound = true;

			} else if (node.getParentNode().getLocalName().equals("inputs")
					|| node.getParentNode().getLocalName().equals("outputs")
					|| node.getParentNode().getLocalName().equals("operations")
					|| node.getParentNode().getLocalName().equals("straightPulls")) {

				id = node.getParentNode().getAttributes().getNamedItem("id").getNodeValue().toString();
				nodeFound = true;
			} else {
				node = node.getParentNode();

			}
		}
		if (element.equals("")) {
			message1 = "For component '" + id + "'";
			
		} else {
			message1 = "For element '" + element + "', id is either not present or it is set to null";
		}
		System.out.println("Message : " + message1);
		System.out.println("Message : " +message[1].trim());

		return false;
	}

	private boolean isIdAttributePresent(NamedNodeMap attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			if (attributes.item(i).getLocalName().equals("id")) {
				if (!attributes.item(i).getNodeValue().equals("")) {
					return true;

				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}

}