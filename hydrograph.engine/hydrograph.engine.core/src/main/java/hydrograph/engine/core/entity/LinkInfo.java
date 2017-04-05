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
/**
 * The Class LinkInfo.
 *
 * @author Bitwise
 *
 */
public class LinkInfo {

	private String componentId;
	private String inSocketId;
	private TypeBaseInSocket inSocket;
	private String sourceComponentId;
	private String outSocketId;
	private TypeBaseOutSocket outSocket;
	
	public LinkInfo(TypeBaseInSocket inSocketList,TypeBaseOutSocket outSocketList){
		this.inSocket=inSocketList;
		this.outSocket=outSocketList;
	}
	
	public LinkInfo(String componentId, TypeBaseInSocket inSocketList,TypeBaseOutSocket outSocketList){
		this.componentId = componentId;
		this.inSocket=inSocketList;
		this.outSocket=outSocketList;
	}

	public LinkInfo(String componentId, TypeBaseInSocket inSocketList, String sourceComponentId, TypeBaseOutSocket outSocketList){
		this.componentId = componentId;
		this.inSocket=inSocketList;
		this.sourceComponentId = sourceComponentId;
		this.outSocket=outSocketList;
	}
	
	public LinkInfo(String componentId, String inSocketId, TypeBaseInSocket inSocketList, String sourceComponentId, String outSocketId, TypeBaseOutSocket outSocketList){
		this.componentId = componentId;
		this.inSocketId = inSocketId;
		this.inSocket=inSocketList;
		this.sourceComponentId = sourceComponentId;
		this.outSocketId = outSocketId;
		this.outSocket=outSocketList;
	}
	
	public String getComponentId() {
		return componentId;
	}
	
	public String getInSocketId() {
		return inSocketId;
	}

	public String getOutSocketId() {
		return outSocketId;
	}

	public String getSourceComponentId() {
		return sourceComponentId;
	}
	
	public TypeBaseInSocket getInSocket() {
		return inSocket;
	}

	public TypeBaseOutSocket getOutSocket() {
		return outSocket;
	}
	
	public String toString(){
		return componentId + "_" + inSocketId + "_" + sourceComponentId + "_" + outSocketId;
	}
	
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public void setInSocketId(String inSocketId) {
		this.inSocketId = inSocketId;
	}

	public void setInSocket(TypeBaseInSocket inSocket) {
		this.inSocket = inSocket;
	}

	public void setSourceComponentId(String sourceComponentId) {
		this.sourceComponentId = sourceComponentId;
	}

	public void setOutSocketId(String outSocketId) {
		this.outSocketId = outSocketId;
	}

	public void setOutSocket(TypeBaseOutSocket outSocket) {
		this.outSocket = outSocket;
	}
	
}
