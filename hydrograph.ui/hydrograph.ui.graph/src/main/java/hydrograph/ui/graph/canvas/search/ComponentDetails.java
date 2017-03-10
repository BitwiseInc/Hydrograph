/********************************************************************************
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
 ******************************************************************************/
package hydrograph.ui.graph.canvas.search;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * ComponentDetails class This is a value object class storing the description
 * of Components.
 * @author Bitwise
 */
public class ComponentDetails {

	private String name;
	private String categoryAndPalletteName;
	private String description;
	private ImageDescriptor descriptor;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(ImageDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public String getCategoryAndPalletteName() {
		return categoryAndPalletteName;
	}

	public void setCategoryAndPalletteName(String categoryAndPalletteName) {
		this.categoryAndPalletteName = categoryAndPalletteName;
	}

}
