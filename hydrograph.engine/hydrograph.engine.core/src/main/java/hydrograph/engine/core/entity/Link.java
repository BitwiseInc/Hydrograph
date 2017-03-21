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
package hydrograph.engine.core.entity;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeBaseOutSocket;

import java.util.List;

/**
 * The Class Link.
 *
 * @author Bitwise
 *
 */
public class Link {

	private List<? extends TypeBaseInSocket> inSocket;
	private List<? extends TypeBaseOutSocket> outSocket;

	public Link(List<? extends TypeBaseInSocket> inSocketList,
			List<? extends TypeBaseOutSocket> outSocketList) {
		this.inSocket = inSocketList;
		this.outSocket = outSocketList;
	}

	public List<? extends TypeBaseInSocket> getInSocket() {
		return inSocket;
	}

	public List<? extends TypeBaseOutSocket> getOutSocket() {
		return outSocket;
	}
}
