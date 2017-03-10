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

package hydrograph.ui.graph.model;

import java.util.Map;

/**
 * The Class PortDetails.
 * 
 * @author Bitwise
 */

public class PortDetails extends Model implements Cloneable{
	
	private static final long serialVersionUID = 8713585457081109591L;
	private Map<String, Port> ports;
	private PortAlignmentEnum portAlignment;
	private int numberOfPorts;
	private boolean changePortCountDynamically;
	private boolean allowMultipleLinks;
	private boolean linkMandatory;
	
	public PortDetails(Map<String, Port> portsMap,  PortAlignmentEnum portAlignment, int noOfPorts, boolean changePortCount, boolean allowMultLinks, boolean linkMan){
		this.ports = portsMap;
		this.portAlignment = portAlignment;
		this.numberOfPorts = noOfPorts;
		this.changePortCountDynamically = changePortCount;
		this.allowMultipleLinks = allowMultLinks;
		this.linkMandatory = linkMan;
	}

	public boolean isLinkMandatory() {
		return linkMandatory;
	}

	public boolean isAllowMultipleLinks() {
		return allowMultipleLinks;
	}

	public Map<String, Port> getPorts() {
		return ports;
	}

	public void setPorts(Map<String, Port> ports) {
		this.ports = ports;
	}

	public PortAlignmentEnum getPortAlignment() {
		return portAlignment;
	}

	public int getNumberOfPorts() {
		return numberOfPorts;
	}

	public boolean isChangePortCountDynamically() {
		return changePortCountDynamically;
	}
	
	
}
