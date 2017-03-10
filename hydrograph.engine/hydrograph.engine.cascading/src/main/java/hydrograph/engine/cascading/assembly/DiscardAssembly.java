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

import cascading.flow.FlowDef;
import cascading.pipe.Pipe;
import cascading.tap.Tap;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.tap.NullTap;
import hydrograph.engine.core.component.entity.DiscardEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscardAssembly extends BaseComponent<DiscardEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1677007645659116577L;

	private FlowDef flowDef;
	private Pipe inputPipes;
	@SuppressWarnings("rawtypes")
	private Tap nullTap;
	private DiscardEntity discardEntity;
	private static Logger LOG = LoggerFactory.getLogger(DiscardAssembly.class);

	public DiscardAssembly(DiscardEntity assemblyEntityBase, ComponentParameters componentParameters) {
		super(assemblyEntityBase, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(discardEntity.toString());
			}
			LOG.trace("Creating discard assembly for '" + discardEntity.getComponentId());
			flowDef = componentParameters.getFlowDef();
			inputPipes = componentParameters.getInputPipe();

			nullTap = new NullTap();
			Pipe sinkPipe = new Pipe(discardEntity.getComponentId()+"", inputPipes);
			setOutLink("output","NoSocketId",
					discardEntity.getComponentId(), sinkPipe, componentParameters
					.getInputFieldsList().get(0));
			setHadoopProperties(sinkPipe.getStepConfigDef());

			flowDef = flowDef.addTailSink(sinkPipe, nullTap);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void initializeEntity(DiscardEntity assemblyEntityBase) {
		this.discardEntity = assemblyEntityBase;
	}
}
