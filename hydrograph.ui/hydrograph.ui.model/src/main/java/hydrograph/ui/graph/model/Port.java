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

/**
 * The Class Port.
 * 
 * @author Bitwise
 */
public class Port extends Model implements Cloneable{
	
	private static final long serialVersionUID = 302760655288792415L;
	private String terminal;
	private int numberOfPortsOfThisType;
	private PortTypeEnum portType;
	private int sequence;
	private Component parent;
	private String labelOfPort;
	private boolean isWatched;
	private boolean allowMultipleLinks;
	private boolean linkMandatory;
	private PortAlignmentEnum portAlignment;
	
	/**
	 * Instantiates a new port.
	 * * 
	 * @param nameOfPort
	 *            the name of port
	 * @param labelOfPort
	 *            the label of port
	 * @param terminal
	 *            the terminal
	 * @param component
	 *            the component
	 * @param noPortsOfThisType
	 *            the no ports of this type
	 * @param type
	 *            the port type
	 * @param seq
	 *            the sequence
	 * @param alwMulLinks 
	 * 			  If multiple links are allowed
	 *  @param lnkMan
	 *  		If link is mandatory    
	 *  @param portAlignmentEnum
	 *  	Port alignment       
	 */
		public Port(String labelOfPort,String terminal, Component component, int noPortsOfThisType, PortTypeEnum type, int seq,
				boolean alwMulLinks, boolean lnkMan, PortAlignmentEnum portAlignmentEnum){
		this.terminal = terminal;
		this.numberOfPortsOfThisType = noPortsOfThisType;
		this.portType = type;
		this.sequence = seq;
		this.parent =component;
		this.labelOfPort=labelOfPort;
		this.allowMultipleLinks = alwMulLinks;
		this.linkMandatory = lnkMan;
		this.portAlignment = portAlignmentEnum;
	}
	
	public Port() {
	}

	public boolean isAllowMultipleLinks() {
		return allowMultipleLinks;
	}

	public boolean isLinkMandatory() {
		return linkMandatory;
	}

	public String getLabelOfPort() {
		return labelOfPort;
	}
	
	public void setLabelOfPort(String label) {
		this.labelOfPort=label;
	}

	public Component getParent() {
		return parent;
	}
	
	public void setParent(Component parent) {
		this.parent = parent;
	}

	public String getTerminal() {
		return terminal;
	}

	public int getNumberOfPortsOfThisType() {
		return numberOfPortsOfThisType;
	}

	public String getPortType() {
		return portType.value();
	}

	public void setPortType(PortTypeEnum portType) {
		this.portType = portType;
	}

	public int getSequence() {
		return sequence;
	}
	
	public boolean isWatched() {
		return isWatched;
	}

	public void setWatched(boolean isWatched) {
		this.isWatched = isWatched;
	}

	/*public String getNameOfPort() {
		return nameOfPort;
	}*/
	public PortAlignmentEnum getPortAlignment() {
		return portAlignment;
	}

	public void setNumberOfPortsOfThisType(int newPortCount){
		this.numberOfPortsOfThisType = newPortCount;
	}
	
	@Override
	public String toString() {
				
		 return "\n******************************************"+
				"\nTerminal: "+terminal+
				"\nnumberOfPortsOfThisType: "+this.numberOfPortsOfThisType+
				"\nportType: "+this.portType+
				"\nsequence: "+this.sequence+
				"\nparent: "+this.parent+
				"\nlabelOfPort: "+this.labelOfPort+
				"\nMultiple links allowed: "+this.allowMultipleLinks+
				"\nLink mandatory: "+this.linkMandatory+
				"\nPort Alignment: "+this.portAlignment.value()+
				"\n******************************************\n";
		 
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Port) {
			Port p = (Port) o;
			
			if ( p.getTerminal().equals(this.getTerminal()) &&
					p.getSequence() == this.getSequence() &&
					p.getPortType().equals(this.getPortType()) &&
					p.getParent().equals( this.getParent()) &&
					p.getLabelOfPort().equals(this.getLabelOfPort()) &&
					p.isAllowMultipleLinks() == this.isAllowMultipleLinks() &&
					p.isLinkMandatory() == this.isLinkMandatory()
				)
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 17;
	 
		result = 31 * result + sequence;
		result = 31 * result + terminal.hashCode();
		result = 31 * result + portType.hashCode();
		result = 31 * result + parent.hashCode();
		result = 31 * result + labelOfPort.hashCode();
	 
		return result;
		
		
	}
	
	@Override
	protected Port clone() throws CloneNotSupportedException {
		Port clonedPort=new Port();

		clonedPort.terminal = terminal;
		clonedPort.numberOfPortsOfThisType = numberOfPortsOfThisType;
		clonedPort.portType = portType;
		clonedPort.sequence = sequence;
		clonedPort.labelOfPort = labelOfPort;
		clonedPort.allowMultipleLinks = allowMultipleLinks;
		clonedPort.linkMandatory = linkMandatory;
		clonedPort.portAlignment=portAlignment;
		clonedPort.isWatched=isWatched;
		return clonedPort;
	}
	
}
