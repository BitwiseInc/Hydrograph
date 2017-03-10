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

 
package hydrograph.ui.engine.ui.xygenration;

import java.util.ArrayList;
import java.util.List;

public class Node {
	String name;
	private List<Node> sourceNodes = new ArrayList<Node>();
	private List<Node> destinationNodes = new ArrayList<Node>();
	private int hPosition = 0;
	private int vPosition = 0;
	
	public Node(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Node> getSourceNodes() {
		return sourceNodes;
	}
	
	public List<Node> getDestinationNodes() {
		return destinationNodes;
	}
	
	public void sethPosition(int hPosition) {
		this.hPosition = hPosition;
	}
	public int gethPosition() {
		return hPosition;
	}
	public int getvPosition() {
		return vPosition;
	}
	
	public void setvPosition(int vPosition) {
		this.vPosition = vPosition;
	}

	@Override
	public String toString() {
		return "Node [name=" + name + ", hPosition=" + hPosition + ", vPosition "+vPosition+", " +"]";//+sourceNodes;
//				"Destination "+getDestinationNodes()+", Source "+getSourceNodes()+"]";
	}
}
