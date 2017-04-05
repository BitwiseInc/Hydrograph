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
package hydrograph.engine.core.utilities;

import hydrograph.engine.jaxb.commontypes.*;

import java.util.ArrayList;
import java.util.List;
/**
 * The Class SocketUtilities.
 *
 * @author Bitwise
 */
public class SocketUtilities {

	public static List<? extends TypeBaseInSocket> getInSocketList(
			TypeBaseComponent component) {
		// No need to check InputEntityBase
		List<? extends TypeBaseInSocket> inSocketList = new ArrayList<TypeBaseInSocket>();
		if (component instanceof TypeOutputComponent)
			inSocketList = ((TypeOutputComponent) component).getInSocket();
		else if (component instanceof TypeStraightPullComponent)
			inSocketList = ((TypeStraightPullComponent) component)
					.getInSocket();
		else if (component instanceof TypeOperationsComponent)
			inSocketList = ((TypeOperationsComponent) component).getInSocket();
		return inSocketList;
	}

	public static TypeBaseComponent updateComponentInSocket(
			TypeBaseComponent oldComponent, String fromCompId, String fromSocketId, String newfromCompId, String newfromSocketId) {
		// No need to check InputEntityBase
		List<? extends TypeBaseInSocket> inSocketList = new ArrayList<TypeBaseInSocket>();
		if (oldComponent instanceof TypeOutputComponent) {
			for (int i = 0; i < ((TypeOutputComponent) oldComponent).getInSocket()
					.size(); i++) {
				if (((TypeOutputComponent) oldComponent).getInSocket().get(i)
						.getFromComponentId().equals(fromCompId)
						&& ((TypeOutputComponent) oldComponent).getInSocket()
								.get(i).getFromSocketId()
								.equals(fromSocketId)) {
					((TypeOutputComponent) oldComponent).getInSocket().get(i).setFromComponentId(newfromCompId);
					((TypeOutputComponent) oldComponent).getInSocket().get(i).setFromSocketId(newfromSocketId);
				}
			}
		} else if (oldComponent instanceof TypeStraightPullComponent){
			for (int i = 0; i < ((TypeStraightPullComponent) oldComponent).getInSocket()
					.size(); i++) {
				if (((TypeStraightPullComponent) oldComponent).getInSocket().get(i)
						.getFromComponentId().equals(fromCompId)
						&& ((TypeStraightPullComponent) oldComponent).getInSocket()
								.get(i).getFromSocketId()
								.equals(fromSocketId)) {
					((TypeStraightPullComponent) oldComponent).getInSocket().get(i).setFromComponentId(newfromCompId);
					((TypeStraightPullComponent) oldComponent).getInSocket().get(i).setFromSocketId(newfromSocketId);
				}
			}
		}
		else if (oldComponent instanceof TypeOperationsComponent){
			for (int i = 0; i < ((TypeOperationsComponent) oldComponent).getInSocket()
					.size(); i++) {
				if (((TypeOperationsComponent) oldComponent).getInSocket().get(i)
						.getFromComponentId().equals(fromCompId)
						&& ((TypeOperationsComponent) oldComponent).getInSocket()
								.get(i).getFromSocketId()
								.equals(fromSocketId)) {
					((TypeOperationsComponent) oldComponent).getInSocket().get(i).setFromComponentId(newfromCompId);
					((TypeOperationsComponent) oldComponent).getInSocket().get(i).setFromSocketId(newfromSocketId);
				}
			}
		}
		return oldComponent;
	}

	public static TypeBaseInSocket getInSocket(TypeBaseComponent component,
			String socketId) {
		// No need to check InputEntityBase
		List<? extends TypeBaseInSocket> inSocketList = new ArrayList<TypeBaseInSocket>();
		if (component instanceof TypeOutputComponent)
			inSocketList = ((TypeOutputComponent) component).getInSocket();
		else if (component instanceof TypeStraightPullComponent)
			inSocketList = ((TypeStraightPullComponent) component)
					.getInSocket();
		else if (component instanceof TypeOperationsComponent)
			inSocketList = ((TypeOperationsComponent) component).getInSocket();

		for (TypeBaseInSocket typeBaseInSocket : inSocketList) {
			if (typeBaseInSocket.getId().equals(socketId)) {
				return typeBaseInSocket;
			}
		}
		return null;
	}

	public static List<? extends TypeBaseOutSocket> getOutSocketList(
			TypeBaseComponent component) {
		// No need to check TypeBaseOutPutEntity
		List<? extends TypeBaseOutSocket> outSocketList = new ArrayList<TypeBaseOutSocket>();
		if (component instanceof TypeInputComponent)
			outSocketList = ((TypeInputComponent) component).getOutSocket();
		else if (component instanceof TypeStraightPullComponent)
			outSocketList = ((TypeStraightPullComponent) component)
					.getOutSocket();
		else if (component instanceof TypeOperationsComponent)
			outSocketList = ((TypeOperationsComponent) component)
					.getOutSocket();
		return outSocketList;
	}

	public static TypeBaseOutSocket getOutSocket(TypeBaseComponent component,
			String socketId) {
		// No need to check TypeBaseOutPutEntity
		List<? extends TypeBaseOutSocket> outSocketList = new ArrayList<TypeBaseOutSocket>();
		if (component instanceof TypeInputComponent)
			outSocketList = ((TypeInputComponent) component).getOutSocket();
		else if (component instanceof TypeStraightPullComponent)
			outSocketList = ((TypeStraightPullComponent) component)
					.getOutSocket();
		else if (component instanceof TypeOperationsComponent)
			outSocketList = ((TypeOperationsComponent) component)
					.getOutSocket();
		for (TypeBaseOutSocket typeBaseOutSocket : outSocketList) {
			if (typeBaseOutSocket.getId().equals(socketId))
				return typeBaseOutSocket;
		}
		return null;
	}

	public static void getOutSocketFields(TypeBaseOutSocket typeBaseOutSocket) {

		if (typeBaseOutSocket instanceof TypeOperationsOutSocket) {
			TypeOperationsOutSocket outSocketList = (TypeOperationsOutSocket) typeBaseOutSocket;
			List<Object> x = outSocketList
					.getPassThroughFieldOrOperationFieldOrExpressionField();
		}
	}
}