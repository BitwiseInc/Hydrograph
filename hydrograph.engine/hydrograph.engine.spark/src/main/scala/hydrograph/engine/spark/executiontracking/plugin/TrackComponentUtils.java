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
package hydrograph.engine.spark.executiontracking.plugin;

import hydrograph.engine.core.component.entity.elements.InSocket;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.entity.utils.StraightPullEntityUtils;
import hydrograph.engine.core.utilities.SocketUtilities;
import hydrograph.engine.jaxb.commontypes.*;
import hydrograph.engine.jaxb.operationstypes.Executiontracking;
import hydrograph.engine.jaxb.operationstypes.Filter;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.*;
/**
 * The Class TrackComponentUtils.
 *
 * @author Bitwise
 *
 */

class TrackComponentUtils {

	/**
	 * Creates an object of type {@link TypeBaseInSocket} from component's fromComponentId,
	 *   outSocketId and outSocketType
	 * @param fromComponentId
	 * 					fromComponentId to set in {@link TypeBaseInSocket} object
	 * @param outSocketId
	 * 					outSocketId to set in {@link TypeBaseInSocket} object
	 * @param outSocketType
	 * 					outSocketType to set in {@link TypeBaseInSocket} object
	 * @return an object of type {@link TypeBaseInSocket}
	 */
	static TypeBaseInSocket getStraightPullInSocket(String fromComponentId, String outSocketId,
													String outSocketType) {
		TypeBaseInSocket baseInSocket = new TypeBaseInSocket();
		baseInSocket.setFromComponentId(fromComponentId);
		baseInSocket.setFromSocketId(outSocketId);
		baseInSocket.setFromSocketType(outSocketType);
		baseInSocket.setId("in0");
		return baseInSocket;
	}

	/**
	 * Creates an object of type {@link TypeOperationsOutSocket} from id and inSocketId
	 * @param id
	 * 			id to set in in {@link TypeOperationsOutSocket} object
	 * @param inSocketId
	 * 				inSocketId to set in in {@link TypeOperationsOutSocket} object
	 * @return an object of type {@link TypeOperationsOutSocket}
	 */
	static TypeOperationsOutSocket getStraightPullOutSocket(String id, String inSocketId) {
		TypeOperationsOutSocket operationOutSocket = new TypeOperationsOutSocket();
		operationOutSocket.setId(id);
		TypeOutSocketAsInSocket typeOutSocketAsInSocket = new TypeOutSocketAsInSocket();
		typeOutSocketAsInSocket.setInSocketId(inSocketId);
		operationOutSocket.setCopyOfInsocket(typeOutSocketAsInSocket);
		return operationOutSocket;
	}

	/**
	 * Generate UniqueComponentId for generated filter components
	 * @param compId
	 * 				compId to set
	 * @param socketId
	 * 				socketId to set
	 * @param typeBaseComponents
	 * 				- {@link TypeBaseComponent} to set
	 * @return the UniqueComponentId
	 */
	static String generateUniqueComponentId(String compId, String socketId,
											List<TypeBaseComponent> typeBaseComponents) {
		String newComponentID = compId + "_" + socketId;
		for (int i = 0; i < typeBaseComponents.size(); i++) {
			if (newComponentID.equalsIgnoreCase(typeBaseComponents.get(i).getId())) {
				newComponentID += "_" + i;
			}
		}
		return newComponentID;
	}

	/**
	 * Creates an object of type {@link TypeBaseComponent}
	 * @param jaxbGraph
	 * 				List of {@link TypeBaseComponent} object to set
	 * @param compId
	 * 				compId to set
	 * @param socketId
	 * 				socketId to set
	 * @return the {@link TypeBaseComponent} object
	 */
	static TypeBaseComponent getComponent(List<TypeBaseComponent> jaxbGraph, String compId,
										  String socketId) {
		for (TypeBaseComponent component : jaxbGraph) {
			for (TypeBaseInSocket inSocket : SocketUtilities.getInSocketList(component)) {
				if (inSocket.getFromComponentId().equalsIgnoreCase(compId)
						&& inSocket.getFromSocketId().equalsIgnoreCase(socketId)) {
					return component;
				}
			}
		}
		throw new RuntimeException("debug FromComponent id: " + compId + " or Socket id: " + socketId
				+ " are not properly configured");
	}

	static TypeBaseComponent getCurrentComponent(List<TypeBaseComponent> jaxbGraph, String compId,
												 String socketId) {
		for (TypeBaseComponent component : jaxbGraph) {
			if(component.getId().equals(compId)){
				return component;
			}
		}
		throw new RuntimeException("debug FromComponent id: " + compId + " or Socket id: " + socketId
				+ " are not properly configured");
	}

	/**
	 * Creates an object of type {@link Filter}
	 * @param trackContext
	 * 					- {@link TrackContext} to set
	 * @param jaxbObjectList
	 * 					- List of {@link TypeBaseComponent} object to set
	 * @param schemaFieldsMap
	 * 					 - Set of {@link SchemaField} object to set
	 * @return the object of type {@link Filter}
	 */
	static Executiontracking generateFilterAfterEveryComponent(TrackContext trackContext, List<TypeBaseComponent> jaxbObjectList,
															   Map<String, Set<SchemaField>> schemaFieldsMap) {
		Executiontracking executiontracking = new Executiontracking();
		final QName _TypeOperationsComponentOperation_QNAME = new QName("", "operation");
		TypeTransformOperation filterOperation = new TypeTransformOperation();
		JAXBElement<TypeTransformOperation> jaxbFilterOperation = new JAXBElement<TypeTransformOperation>(_TypeOperationsComponentOperation_QNAME, TypeTransformOperation.class, TypeOperationsComponent.class, filterOperation);

		Set<SchemaField> schemaFields = schemaFieldsMap
				.get(trackContext.getFromComponentId() + "_" + trackContext.getFromOutSocketId());

		TypeOperationInputFields typeOperationInputFields = new TypeOperationInputFields();
		TypeInputField typeInputField = new TypeInputField();
		typeInputField.setInSocketId(trackContext.getFromOutSocketId());
		typeInputField.setName(schemaFields.iterator().next().getFieldName());
		typeOperationInputFields.getField().add(typeInputField);

		filterOperation.setInputFields(typeOperationInputFields);
//		filterOperation.setClazz(ExecutionCounter.class.getCanonicalName());
		executiontracking.setId(TrackComponentUtils.generateUniqueComponentId(trackContext.getFromComponentId(),
				"generatedHydrographFilter", jaxbObjectList));
		executiontracking.setBatch(trackContext.getBatch());
		executiontracking.setName(trackContext.getComponentName());
		executiontracking.getInSocket().add(TrackComponentUtils.getStraightPullInSocket(trackContext.getFromComponentId(),
				trackContext.getFromOutSocketId(), trackContext.getFromOutSocketType()));

		executiontracking.getOutSocket().add(TrackComponentUtils.getStraightPullOutSocket("out0", "in0"));
		executiontracking.getOperationOrExpressionOrIncludeExternalOperation().add(jaxbFilterOperation);
		return executiontracking;
	}

	/**
	 * Creates a List of OutSocket object
	 * @param typeBaseComponent
	 * 					- {@link TypeBaseComponent} to set
	 * @return the List of {@link OutSocket}
	 */
	static List<OutSocket> getOutSocketListofComponent(TypeBaseComponent typeBaseComponent) {
		if (typeBaseComponent instanceof TypeInputComponent) {
			TypeInputComponent typeInputComponent = (TypeInputComponent) typeBaseComponent;
			return InputEntityUtils.extractOutSocket(typeInputComponent.getOutSocket());
		} else if (typeBaseComponent instanceof TypeOutputComponent) {
			return Collections.emptyList();
		} else if (typeBaseComponent instanceof TypeStraightPullComponent) {
			TypeStraightPullComponent typeStraightPullComponent = (TypeStraightPullComponent) typeBaseComponent;
			return StraightPullEntityUtils.extractOutSocketList(typeStraightPullComponent.getOutSocket());
		} else if (typeBaseComponent instanceof TypeOperationsComponent) {
			TypeOperationsComponent typeOperationsComponent = (TypeOperationsComponent) typeBaseComponent;
			return OperationEntityUtils.extractOutSocketList(typeOperationsComponent.getOutSocket());
		} else if (typeBaseComponent instanceof TypeCommandComponent) {
			return Collections.emptyList();
		}
		return Collections.emptyList();
	}

	public static List<InSocket> extractInSocketList(List<TypeOutputInSocket> inSocket) {

		if (inSocket == null) {
			throw new NullPointerException("In socket cannot be null");
		}

		List<InSocket> inSocketList = new ArrayList<InSocket>();

		for (TypeBaseInSocket socket : inSocket) {

			InSocket inSock = new InSocket(socket.getFromComponentId(), socket.getFromSocketId(), socket.getId());

			inSock.setInSocketType(socket.getType() != null ? socket.getType() : "");
			inSock.setFromSocketType(socket.getFromSocketType() != null ? socket.getFromSocketType() : "");

			inSocketList.add(inSock);

		}

		return inSocketList;

	}

	public static List<InSocket> extractInSocketListOfComponents(TypeBaseComponent typeBaseComponent) {

		if (typeBaseComponent instanceof TypeStraightPullComponent) {
			TypeStraightPullComponent typeStraightPullComponent = (TypeStraightPullComponent) typeBaseComponent;
			return extractInSocketListOfTypeBaseInSocket(typeStraightPullComponent.getInSocket());
		}
		else if(typeBaseComponent instanceof TypeOperationsComponent) {
			TypeOperationsComponent typeOperationsComponent = (TypeOperationsComponent) typeBaseComponent;
			return extractInSocketListOfTypeBaseInSocket(typeOperationsComponent.getInSocket());
		}

		return Collections.emptyList();
	}

	public static List<InSocket> extractInSocketListOfTypeBaseInSocket(List<TypeBaseInSocket> inSocket) {

		if (inSocket == null) {
			throw new NullPointerException("In socket cannot be null");
		}

		List<InSocket> inSocketList = new ArrayList<InSocket>();

		for (TypeBaseInSocket socket : inSocket) {

			InSocket inSock = new InSocket(socket.getFromComponentId(), socket.getFromSocketId(), socket.getId());

			inSock.setInSocketType(socket.getType() != null ? socket.getType() : "");
			inSock.setFromSocketType(socket.getFromSocketType() != null ? socket.getFromSocketType() : "");

			inSocketList.add(inSock);

		}

		return inSocketList;

	}


}