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
import hydrograph.engine.cascading.functions.CustomTuplesPartitioner;
import hydrograph.engine.core.component.entity.PartitionByExpressionEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.transformation.userfunctions.base.CustomPartitionExpression;
import hydrograph.engine.utilities.UserClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;


public class PartitionByExpressionAssembly extends BaseComponent<PartitionByExpressionEntity> {

	private PartitionByExpressionEntity partitionByExpressionEntity;

	private static final long serialVersionUID = 8282067513240963488L;
	private Pipe[] partitionedPipes;
	private Pipe partitionInputPipe;
	private CustomPartitionExpression partition;

	private static Logger LOG = LoggerFactory.getLogger(PartitionByExpressionAssembly.class);

	public PartitionByExpressionAssembly(PartitionByExpressionEntity baseComponentEntity,
			ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	private void initAssembly() {
		String className = partitionByExpressionEntity.getOperation().getOperationClass();
		this.setPartition(UserClassLoader.loadAndInitClass(className, CustomPartitionExpression.class));
		partitionedPipes = new Pipe[(int) partitionByExpressionEntity.getNumPartitions()];
		partitionInputPipe = componentParameters.getInputPipe();
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(partitionByExpressionEntity.toString());
			}
			initAssembly();
			createPartitionedPipes();

			for (int i = 0; i < partitionedPipes.length; i++) {
				setOutLink(partitionByExpressionEntity.getOutSocketList().get(i).getSocketType(),
						partitionByExpressionEntity.getOutSocketList().get(i).getSocketId(),
						partitionByExpressionEntity.getComponentId(), partitionedPipes[i],
						componentParameters.getInputFields());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

	}

	private LinkedList<String> getOutSocketIdList() {
		LinkedList<String> outSocketIdList = new LinkedList<String>();
		for (OutSocket outSocket : partitionByExpressionEntity.getOutSocketList()) {
			outSocketIdList.add(outSocket.getSocketId());
		}
		return outSocketIdList;
	}

	private void createPartitionedPipes() {

		int partitionIndex = 0;
		for (String outSocketId : getOutSocketIdList()) {
			partitionedPipes[partitionIndex] = getNewPipe(outSocketId, partitionInputPipe);
			setHadoopProperties(partitionedPipes[partitionIndex].getStepConfigDef());
			partitionIndex++;
		}
	}

	private Pipe getNewPipe(String outSocketId, Pipe pipe) {
		Pipe tempPipe = new Pipe(partitionByExpressionEntity.getComponentId()+outSocketId, pipe);
		tempPipe = new Each(tempPipe, getNewPartitioner(outSocketId));

		return tempPipe;
	}

	private CustomTuplesPartitioner getNewPartitioner(String outSocketId) {
		return new CustomTuplesPartitioner(componentParameters.getInputFields(),
				new Fields(partitionByExpressionEntity.getOperation().getOperationInputFields()), this.getPartition(),
				(int) partitionByExpressionEntity.getNumPartitions(), outSocketId,
				partitionByExpressionEntity.getOperation().getOperationProperties());
	}

	public CustomPartitionExpression getPartition() {
		return this.partition;
	}

	public void setPartition(CustomPartitionExpression partition) {
		this.partition = partition;
	}

	@Override
	public void initializeEntity(PartitionByExpressionEntity assemblyEntityBase) {
		this.partitionByExpressionEntity=assemblyEntityBase;
	}

}
