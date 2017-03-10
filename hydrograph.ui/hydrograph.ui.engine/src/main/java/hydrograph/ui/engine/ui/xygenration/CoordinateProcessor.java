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

import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.exceptions.ComponentNotFoundException;
import hydrograph.ui.engine.ui.repository.UIComponentRepo;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.geometry.Point;
import org.slf4j.Logger;


/**
 * The class CoordinateProcessor
 * 
 * @author Bitwise
 * 
 */

public class CoordinateProcessor {
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(CoordinateProcessor.class);
	private List<Node> nodeList = new ArrayList<>();
	private Map<String, Node> nodeMap = new LinkedHashMap<>();
	private Map<String, Component> tempUiComponentFactory = new LinkedHashMap<>();
	private UIComponentRepo componentRepo;
	private static final int HORIZONTAL_SPACING = 200;
	private static final int VERTICAL_SPACING = 200;

	public CoordinateProcessor(UIComponentRepo componentRepo) {
		this.componentRepo=componentRepo;
	}
	/**
	 * Initiate X-Y coordinate generation process and creates node structure for each component.
	 */
	public void initiateCoordinateGenration() throws ComponentNotFoundException{
		LOGGER.debug("Processing link-data for creating individual nodes");
		Node sourceNode = null;
		Node destinationNode = null;
		tempUiComponentFactory.putAll(componentRepo.getComponentUiFactory());
		for (LinkingData linkingData : componentRepo.getComponentLinkList()) {
			if (nodeMap.get(linkingData.getSourceComponentId()) == null) {
				sourceNode = new Node(linkingData.getSourceComponentId());
				nodeMap.put(linkingData.getSourceComponentId(), sourceNode);
				nodeList.add(sourceNode);
				tempUiComponentFactory.remove(linkingData.getSourceComponentId());
			}
			if (nodeMap.get(linkingData.getTargetComponentId()) == null) {
				destinationNode = new Node(linkingData.getTargetComponentId());
				nodeMap.put(linkingData.getTargetComponentId(), destinationNode);
				nodeList.add(destinationNode);
				tempUiComponentFactory.remove(linkingData.getTargetComponentId());
			}
			nodeMap.get(linkingData.getSourceComponentId()).getDestinationNodes()
					.add(nodeMap.get(linkingData.getTargetComponentId()));
			nodeMap.get(linkingData.getTargetComponentId()).getSourceNodes()
					.add(nodeMap.get(linkingData.getSourceComponentId()));
		}
		genrateUnlinkedComponents();
		caculateXY();
		processNodes();
	}

	private void genrateUnlinkedComponents() {
		LOGGER.debug("Generating nodes of non-connected UI-Components");
		for (Entry<String, Component> entry : tempUiComponentFactory.entrySet()) {
			nodeList.add(new Node(entry.getKey()));
		}

	}

	private void processNodes() {
		LOGGER.debug("Process individual links");
		genrateYCoordinate();
		for (Node node : nodeList) {
			Point point = new Point();
			point.x = node.gethPosition();
			point.y = node.getvPosition();
			componentRepo.getComponentUiFactory().get(node.name).setLocation(point);
		}
	}

	private void genrateYCoordinate() {
		LOGGER.debug("Generating Y coordinates for all UI-Components");
		int height = 0;
		LinkedHashMap<Integer, Integer> ycoordinate = new LinkedHashMap<>();
		LinkedHashMap<Integer, Node> upperNodeDetails = new LinkedHashMap<>();
		for (Node node : nodeList) {
			if (ycoordinate.get(node.gethPosition()) == null) {
				ycoordinate.put(node.gethPosition(), 1);
				upperNodeDetails.put(node.gethPosition(), node);
				node.setvPosition(1);

			} else {
				height = ycoordinate.get(node.gethPosition()) + VERTICAL_SPACING
						+ incrementYPosition(upperNodeDetails.get(node.gethPosition()));
				upperNodeDetails.put(node.gethPosition(), node);
				ycoordinate.put(node.gethPosition(), height);
				node.setvPosition(ycoordinate.get(node.gethPosition()));
			}

		}
	}

	private void caculateXY()throws ComponentNotFoundException {
		LOGGER.debug("Calculating logical Linking and positioning of Nodes");
		int position = 1;
		List<Node> noSource = new ArrayList<>();
		List<Node> noDestination = new ArrayList<>();
		for (Node node : nodeList) {
			if (node.getSourceNodes().isEmpty()) {
				node.sethPosition(position);
				noSource.add(node);

			}
			if (node.getDestinationNodes().isEmpty()) {
				noDestination.add(node);
			}
		}

		for (Node node : noSource) {
			calculate(node, position);
		}
	}

	private void calculate(Node node, int position) throws ComponentNotFoundException{
		LOGGER.debug("Applying X coordinates for UI-Components");
		node.sethPosition(position);
		position = incrementXPosition(node, position);
		if (node.getDestinationNodes().isEmpty()) {
			return;
		}

		for (Node destinationNode : node.getDestinationNodes()) {
			if (destinationNode.gethPosition() == 0 && destinationNode.getvPosition() == 0)
				calculate(destinationNode, position + HORIZONTAL_SPACING);
		}
	}

	private int incrementXPosition(Node node, int position)throws ComponentNotFoundException {
		LOGGER.debug("Applying X cordinate for component:{}" + node.getName());
		if (componentRepo.getComponentUiFactory().get(node.getName()) != null) {
			int width = componentRepo.getComponentUiFactory().get(node.getName()).getSize().width();
			if (width > 100)
				position = position + (width - 100);
			return position;
		} else
			throw new ComponentNotFoundException(node.getName(), null);
	}

	private int incrementYPosition(Node node) {
		int height = componentRepo.getComponentUiFactory().get(node.getName()).getSize().height();
		if (height > 75)
			height = height - 75;
		else
			height = 0;
		return height;
	}

}
