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
package hydrograph.engine.utilities;

import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.integration.FlowContext;
import hydrograph.engine.cascading.integration.RuntimeContext;
import hydrograph.engine.core.helper.LinkGenerator;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import org.apache.hadoop.mapred.JobConf;

import java.util.List;

public class ComponentParameterBuilder {

	private ComponentParameters componentParameters;

	public ComponentParameterBuilder(Builder builder) {
		this.componentParameters = builder.componentParameters;
	}

	public ComponentParameters getComponentParameter() {
		return componentParameters;
	}

	public static class Builder {

		private ComponentParameters componentParameters;
		private String componentId;
		private FlowContext flowContext;
		private RuntimeContext runtimeContext;

		public Builder(String componentId,
				ComponentParameters componentParameters,
				FlowContext flowContext, RuntimeContext runtimeContext) {
			this.componentParameters = componentParameters;
			this.componentId = componentId;
			this.flowContext = flowContext;
			this.runtimeContext = runtimeContext;
		}

		public Builder setInputFields() {
			LinkGenerator linkGenerator = new LinkGenerator(runtimeContext
					.getHydrographJob().getJAXBObject());
			List<? extends TypeBaseInSocket> inSocketList = linkGenerator
					.getLink().get(componentId).getInSocket();

			BaseComponent assembly;

			for (TypeBaseInSocket fromSocket : inSocketList) {
				assembly = flowContext.getAssemblies().get(
						fromSocket.getFromComponentId());
				componentParameters.addInputFields(assembly.getOutFields(
						fromSocket.getFromSocketType() != null ? fromSocket
								.getFromSocketType() : "out", fromSocket
								.getFromSocketId(), fromSocket
								.getFromComponentId()));
				componentParameters
						.addCopyOfInSocket(
								fromSocket.getId(),
								assembly.getOutFields(
										fromSocket.getFromSocketType() != null ? fromSocket
												.getFromSocketType() : "out",
										fromSocket.getFromSocketId(),
										fromSocket.getFromComponentId()));
			}
			return this;
		}
		
		public Builder setJobConf() {
			JobConf jobconf = runtimeContext.getJobConf();
			componentParameters.addTempPath(jobconf);
			return this;
		}

		public Builder setInputPipes() {
			LinkGenerator linkGenerator = new LinkGenerator(runtimeContext
					.getHydrographJob().getJAXBObject());
			List<? extends TypeBaseInSocket> inSocketList = linkGenerator
					.getLink().get(componentId).getInSocket();

			BaseComponent assembly;

			for (TypeBaseInSocket fromSocket : inSocketList) {
				assembly = flowContext.getAssemblies().get(
						fromSocket.getFromComponentId());
				componentParameters.addInputPipe(assembly.getOutLink(
						fromSocket.getFromSocketType() != null ? fromSocket
								.getFromSocketType() : "out", fromSocket
								.getFromSocketId(), fromSocket
								.getFromComponentId()));
				componentParameters.addinSocketId(fromSocket.getId());
				componentParameters.addinSocketType(fromSocket.getType());
			}
			return this;
		}

		public Builder setSchemaFields() {

			LinkGenerator linkGenerator = new LinkGenerator(runtimeContext.getHydrographJob().getJAXBObject());
			List<? extends TypeBaseInSocket> inSocketList = linkGenerator.getLink().get(componentId).getInSocket();

			for (TypeBaseInSocket fromSocket : inSocketList) {
				componentParameters.addSchemaFields(runtimeContext.getSchemaFieldHandler().getSchemaFieldMap()
						.get(fromSocket.getFromComponentId() + "_" + fromSocket.getFromSocketId()));
			}
			return this;
		}

		public Builder setFlowdef() {
			componentParameters.setFlowDef(flowContext.getFlowDef());
			return this;
		}
		
		public Builder setUDFPath() {
			componentParameters.setUDFPath(runtimeContext.getUDFPath());
			return this;
		}

		public ComponentParameters build() {
			return new ComponentParameterBuilder(this).getComponentParameter();
		}
	}
}
