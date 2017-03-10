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

import cascading.pipe.Pipe;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.CloneEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CloneAssembly extends BaseComponent<CloneEntity> {

	private static final long serialVersionUID = 8145806669418685707L;

	CloneEntity cloneEntity;
	private static Logger LOG = LoggerFactory.getLogger(CloneAssembly.class);

	public CloneAssembly(CloneEntity baseComponentEntity, ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(cloneEntity.toString());
			}
			Pipe inputPipes = componentParameters.getInputPipe();
			Pipe clonePipe;
			for (OutSocket outSocket : cloneEntity.getOutSocketList()) {

				LOG.trace("Creating clone assembly for '" + cloneEntity.getComponentId() + "' for socket: '"
						+ outSocket.getSocketId() + "' of type: '" + outSocket.getSocketType() + "'");
				clonePipe = new Pipe(cloneEntity.getComponentId()+outSocket.getSocketId(), inputPipes);
				setHadoopProperties(clonePipe.getStepConfigDef());
				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(), cloneEntity.getComponentId(), clonePipe,
						componentParameters.getInputFields());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void initializeEntity(CloneEntity assemblyEntityBase) {
		this.cloneEntity = assemblyEntityBase;
	}
}