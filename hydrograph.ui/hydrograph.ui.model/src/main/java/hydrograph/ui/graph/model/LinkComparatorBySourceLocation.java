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

import hydrograph.ui.common.util.Constants;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;


/**
 * The Class LinkComparatorBySourceLocation.
 * <p>
 * This class is used to sort the links on basis of link's source terminals
 * 
 * @author Bitwise
 */
public class LinkComparatorBySourceLocation implements Comparator<Link> {

	@Override
	public int compare(Link firstLink, Link secondLink) {
		int firstLinkSourceYLocation = firstLink.getSource().getLocation().y;
		int secondLinkSourceYLocation = secondLink.getSource().getLocation().y;

		if (firstLinkSourceYLocation == secondLinkSourceYLocation) {
			if (firstLink.getSource().equals(secondLink.getSource())) {
				return compareTargetLinks(firstLink, secondLink);
			} else
				return 0;
		} else if (firstLinkSourceYLocation > secondLinkSourceYLocation)
			return 1;
		else
			return -1;
	}

	private int compareTargetLinks(Link firstLink, Link secondLink) {
		int firstLinkTargetYLocation = firstLink.getTarget().getLocation().y;
		int secondLinkTargetYLocation = secondLink.getTarget().getLocation().y;
		
		if (firstLinkTargetYLocation == secondLinkTargetYLocation) {
			if(firstLink.getTarget().equals(secondLink.getTarget()))
				return compareLinksOnBasisOfTargetTerminals(firstLink, secondLink);
			return 0;
		} else if (firstLinkTargetYLocation > secondLinkTargetYLocation)
			return 1;
		else
			return -1;
	}
	
	private int compareLinksOnBasisOfTargetTerminals(Link firstLink, Link secondLink) {
		int firstLinkTargetTerminalIndex =Integer.valueOf(firstLink.getTargetTerminal().split(Constants.INPUT_SOCKET_TYPE)[1]);
		int secondLinkTargetTerminalIndex = Integer.valueOf(secondLink.getTargetTerminal().split(Constants.INPUT_SOCKET_TYPE)[1]);
		
		if (firstLinkTargetTerminalIndex == secondLinkTargetTerminalIndex) {
			return compareLinksOnBasisOfSourceTerminals(firstLink, secondLink);
		} else if (firstLinkTargetTerminalIndex > secondLinkTargetTerminalIndex)
			return 1;
		else
			return -1;
	}
	
	private int compareLinksOnBasisOfOutSourceTerminals(Link firstLink, Link secondLink) {
		int firstLinkSourceTerminalIndex =Integer.valueOf(firstLink.getSourceTerminal().split(Constants.OUTPUT_SOCKET_TYPE)[1]);
		int secondLinkSourceTerminalIndex = Integer.valueOf(secondLink.getSourceTerminal().split(Constants.OUTPUT_SOCKET_TYPE)[1]);
		
		if (firstLinkSourceTerminalIndex == secondLinkSourceTerminalIndex) {
			return 0;
		} else if (firstLinkSourceTerminalIndex > secondLinkSourceTerminalIndex)
			return 1;
		else
			return -1;
	}
	
	private int compareLinksOnBasisOfUnusedSourceTerminals(Link firstLink, Link secondLink) {
		int firstLinkSourceTerminalIndex =Integer.valueOf(firstLink.getSourceTerminal().split(Constants.UNUSED_SOCKET_TYPE)[1]);
		int secondLinkSourceTerminalIndex = Integer.valueOf(secondLink.getSourceTerminal().split(Constants.UNUSED_SOCKET_TYPE)[1]);
		
		if (firstLinkSourceTerminalIndex == secondLinkSourceTerminalIndex) {
			return 0;
		} else if (firstLinkSourceTerminalIndex > secondLinkSourceTerminalIndex)
			return 1;
		else
			return -1;
	}
	
	private int compareLinksOnBasisOfSourceTerminals(Link firstLink, Link secondLink) {
		if (StringUtils.equals(firstLink.getSource().getPort(firstLink.getSourceTerminal()).getPortType(),Constants.UNUSED_SOCKET_TYPE)
				&& StringUtils.equals(secondLink.getSource().getPort(secondLink.getSourceTerminal()).getPortType(),	Constants.UNUSED_SOCKET_TYPE))
			return compareLinksOnBasisOfUnusedSourceTerminals(firstLink,secondLink);
		
		else if (StringUtils.equals(firstLink.getSource().getPort(firstLink.getSourceTerminal()).getPortType(),Constants.OUTPUT_SOCKET_TYPE)
				&& StringUtils.equals(secondLink.getSource().getPort(secondLink.getSourceTerminal()).getPortType(),	Constants.OUTPUT_SOCKET_TYPE))
			return compareLinksOnBasisOfOutSourceTerminals(firstLink,secondLink);
		
		else if (StringUtils.equals(firstLink.getSource().getPort(firstLink.getSourceTerminal()).getPortType(),Constants.UNUSED_SOCKET_TYPE)
				&& StringUtils.equals(secondLink.getSource().getPort(secondLink.getSourceTerminal()).getPortType(),Constants.OUTPUT_SOCKET_TYPE))
			return 1;
		
		else if (StringUtils.equals(firstLink.getSource().getPort(firstLink.getSourceTerminal()).getPortType(),Constants.OUTPUT_SOCKET_TYPE)
				&& StringUtils.equals(secondLink.getSource().getPort(secondLink.getSourceTerminal()).getPortType(),Constants.UNUSED_SOCKET_TYPE))
			return -1;
		
		return 0;
	}
	
}
