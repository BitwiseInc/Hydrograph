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
package hydrograph.engine.cascading.assembly;

import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.functions.UniqueSequenceNumberOperation;
import hydrograph.engine.core.component.entity.UniqueSequenceEntity;
import hydrograph.engine.core.component.entity.elements.Operation;
import hydrograph.engine.core.component.entity.elements.OperationField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniqueSequenceAssembly extends BaseComponent<UniqueSequenceEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7136851169742722787L;
	private UniqueSequenceEntity uniqueSequenceEntity;
	private Fields outputFieldsList;
	private static Logger LOG = LoggerFactory
			.getLogger(UniqueSequenceAssembly.class);

	public UniqueSequenceAssembly(UniqueSequenceEntity uniqueSequenceEntity,
			ComponentParameters componentParameters) {
		super(uniqueSequenceEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(uniqueSequenceEntity.toString());
			}
			for (OutSocket outSocket : uniqueSequenceEntity.getOutSocketList()) {
				LOG.trace("Creating unique sequence assembly for '"
						+ uniqueSequenceEntity.getComponentId()
						+ "' for socket: '" + outSocket.getSocketId()
						+ "' of type: '" + outSocket.getSocketType() + "'");

				initializeOperationFieldsForOutSocket(outSocket);
				UniqueSequenceNumberOperation seqNo = new UniqueSequenceNumberOperation(
						outputFieldsList);

				Pipe uniSeqPipe = new Pipe(uniqueSequenceEntity.getComponentId()+outSocket.getSocketId(),
						componentParameters.getInputPipe());
				setHadoopProperties(uniSeqPipe.getStepConfigDef());
				uniSeqPipe = new Each(uniSeqPipe, Fields.NONE, seqNo,
						Fields.ALL);

				setOutLink(
						outSocket.getSocketType(),
						outSocket.getSocketId(),
						uniqueSequenceEntity.getComponentId(),
						uniSeqPipe,
						componentParameters.getInputFields().append(
								outputFieldsList));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	private void initializeOperationFieldsForOutSocket(OutSocket outSocket) {
		outputFieldsList = new Fields();

		// check if the unique sequence component has operation(s)
		if (uniqueSequenceEntity.isOperationPresent()) {
			for (Operation eachOperation : uniqueSequenceEntity
					.getOperationsList()) {
				if (isOperationIDExistsInOperationFields(
						eachOperation.getOperationId(), outSocket)) {
					outputFieldsList = outputFieldsList.append(new Fields(
							eachOperation.getOperationOutputFields()));
				} else {
					LOG.info("Operation: '" + eachOperation.getOperationId()
							+ "' of unique sequence component '"
							+ uniqueSequenceEntity.getComponentId()
							+ "' not used in out socket");
				}
			}
		}
	}

	private boolean isOperationIDExistsInOperationFields(String operationId,
			OutSocket outSocket) {
		for (OperationField eachOperationField : outSocket
				.getOperationFieldList()) {
			if (eachOperationField.getOperationId().equals(operationId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initializeEntity(UniqueSequenceEntity assemblyEntityBase) {
		this.uniqueSequenceEntity = assemblyEntityBase;
	}
}
