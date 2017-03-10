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
 * limitations under the License.
 *******************************************************************************/

package hydrograph.ui.engine.xpath;


/**
 * The Class ComponentsAttributeAndValue.
 * Used to hold component's properties attribute and name
 * 
 * @author Bitwise
 */
public class ComponentsAttributeAndValue {
	
	private boolean isNewNode;
	private String newNodeText;
	public void setNewNodeText(String newNodeText) {
		this.newNodeText = newNodeText;
	}
	private boolean emptyNode;
	private String attributeName;
	private String attributeValue;
	private static final String DEFAULT_ATTRIBUTE_NAME = "value";

	private ComponentsAttributeAndValue() {

	}

	public ComponentsAttributeAndValue(boolean isNewNode,String newNodeText, boolean emptyNode){
		this.isNewNode=isNewNode;
		this.newNodeText=newNodeText;
		this.emptyNode=emptyNode;
	}
	
	public ComponentsAttributeAndValue(String attributeName, String attributeValue) {
		if (attributeName == null)
			this.attributeName = DEFAULT_ATTRIBUTE_NAME;
		else
			this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.isNewNode=false;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public boolean isNewNode() {
		return isNewNode;
	}

	public String getNewNodeText() {
		return newNodeText;
	}

	public boolean hasEmptyNode() {
		return emptyNode;
	}

	
}
