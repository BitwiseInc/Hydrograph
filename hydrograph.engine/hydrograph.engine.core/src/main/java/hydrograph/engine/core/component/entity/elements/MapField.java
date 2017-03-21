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
/**
 * 
 */
package hydrograph.engine.core.component.entity.elements;

import java.io.Serializable;

/**
 * The Class MapField.
 *
 * @author Bitwise
 *
 */
public class MapField implements Serializable{

	private String sourceName;
	private String targetName;
	private String inSocketId;

	/**
	 * @param sourceName
	 * @param name
	 * @param inSocketId
	 */
	public MapField(String sourceName, String name, String inSocketId) {

		this.sourceName = sourceName;
		this.targetName = name;
		this.inSocketId = inSocketId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public String getName() {
		return targetName;
	}

	public String getInSocketId() {
		return inSocketId;
	}

	@Override
	public String toString() {
		return "In socket id: " + inSocketId + " | source name: " + sourceName
				+ " | target name: " + targetName;
	}
}
