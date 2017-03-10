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

import cascading.operation.filter.Limit;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.LimitEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitAssembly extends BaseComponent<LimitEntity> {

	/**
	 *
	 */
	private static final long serialVersionUID = 6363160717882023280L;

	private LimitEntity limitEntity;
	private static Logger LOG = LoggerFactory.getLogger(LimitAssembly.class);

	public LimitAssembly(LimitEntity limitEntity,
						 ComponentParameters componentParameters) {
		super(limitEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try{
			if (LOG.isTraceEnabled()) {
				LOG.trace(limitEntity.toString());
			}
			for (OutSocket outSocket : limitEntity.getOutSocketList()) {
				LOG.trace("Creating limit assembly for '"
						+ limitEntity.getComponentId() + "' for socket: '"
						+ outSocket.getSocketId() + "' of type: '"
						+ outSocket.getSocketType() + "'");

				Limit limit = new Limit(limitEntity.getMaxRecord());

				Pipe outPipes = new Pipe(limitEntity.getComponentId()+outSocket.getSocketId(),
						componentParameters.getInputPipe());

				setHadoopProperties(outPipes.getStepConfigDef());

				outPipes = new Each(outPipes, limit);

				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
						limitEntity.getComponentId(), outPipes,
						componentParameters.getInputFields());
			}
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void initializeEntity(LimitEntity assemblyEntityBase) {
		this.limitEntity=assemblyEntityBase;
	}
}
