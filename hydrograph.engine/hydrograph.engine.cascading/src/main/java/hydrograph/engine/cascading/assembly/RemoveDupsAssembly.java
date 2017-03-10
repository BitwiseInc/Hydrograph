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
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.pipe.assembly.Retain;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.handlers.RemoveDupsHandler;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.filters.RemoveDupsOutLinkFilter;
import hydrograph.engine.cascading.filters.RemoveDupsUnusedLinkFilter;
import hydrograph.engine.core.component.entity.RemoveDupsEntity;
import hydrograph.engine.core.component.entity.elements.KeyField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class RemoveDupsAssembly extends BaseComponent<RemoveDupsEntity> {

	private static final long serialVersionUID = 8050470302089972525L;
	private RemoveDupsEntity removeDupsEntity;
	private GroupBy sortDedupPipe;
	private Pipe everyPipe;
	private static Logger LOG = LoggerFactory.getLogger(RemoveDupsAssembly.class);

	public RemoveDupsAssembly(RemoveDupsEntity entity, ComponentParameters componentParameters) {
		super(entity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(removeDupsEntity.toString());
			}
			OutSocket unusedSocket = null;
			for (OutSocket outSocket : removeDupsEntity.getOutSocketList()) {
				if (outSocket.getSocketType().equalsIgnoreCase("out")) {
					LOG.trace("Creating remove dups assembly for '" + removeDupsEntity.getComponentId()
							+ "' for socket: '" + outSocket.getSocketId() + "' of type: '" + outSocket.getSocketType()
							+ "'");
					createAssemblyForOutSocket(outSocket);
				} else if (outSocket.getSocketType().equalsIgnoreCase("unused")) {
					unusedSocket = outSocket;
				}
			}
			if (unusedSocket != null) {
				LOG.trace("Creating remove dups assembly for '" + removeDupsEntity.getComponentId() + "' for socket: '"
						+ unusedSocket.getSocketId() + "' of type: '" + unusedSocket.getSocketType() + "'");
				createAssemblyForUnusedSocket(unusedSocket);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

	}

	private void createAssemblyForUnusedSocket(OutSocket outSocket) {
		if (everyPipe == null) {
			throw new NullOutSocketException(removeDupsEntity.getComponentId());
		} else {
			Pipe filterUnusedPipe = createFilterPipe("unused",outSocket.getSocketId(), everyPipe);
			setOutLink(outSocket.getSocketType(), outSocket.getSocketId(), removeDupsEntity.getComponentId(),
					filterUnusedPipe, componentParameters.getInputFields());
		}
	}

	private void createAssemblyForOutSocket(OutSocket outSocket) {

		Fields keyFields = getFieldsFromKeyFields(removeDupsEntity.getKeyFields());
		Fields secondaryKeyFields = getFieldsFromKeyFields(removeDupsEntity.getSecondaryKeyFields());

		sortDedupPipe = new GroupBy(componentParameters.getInputPipe(), keyFields, secondaryKeyFields);

		everyPipe = createEveryPipe("out", outSocket.getSocketId(),sortDedupPipe);
		Pipe filterOutPipe = createFilterPipe("out", outSocket.getSocketId(),everyPipe);

		setOutLink(outSocket.getSocketType(), outSocket.getSocketId(), removeDupsEntity.getComponentId(), filterOutPipe,
				componentParameters.getInputFields());
	}

	private Pipe createFilterPipe(String linkType, String outSocketId, Pipe everyPipe) {

		Pipe filterPipe = new Pipe(removeDupsEntity.getComponentId() + "_RemoveDupsFilter_" + outSocketId, everyPipe);
		setHadoopProperties(everyPipe.getStepConfigDef());

		if (linkType.equals("unused")) {
			filterPipe = new Each(filterPipe, new Fields("keep"), new RemoveDupsUnusedLinkFilter());
		} else {
			filterPipe = new Each(filterPipe, new Fields("keep"), new RemoveDupsOutLinkFilter());
		}
		filterPipe = new Retain(filterPipe, componentParameters.getInputFields());
		return filterPipe;
	}

	private Pipe createEveryPipe(String linkType, String outSocketId, Pipe groupByPipe) {
		groupByPipe = new Pipe(removeDupsEntity.getComponentId() + "_" + outSocketId, groupByPipe);

		RemoveDupsHandler handler = new RemoveDupsHandler(linkType, removeDupsEntity.getKeep(),
				componentParameters.getInputFields());

		setHadoopProperties(groupByPipe.getStepConfigDef());

		return new Every(groupByPipe, handler.getInputFields(), handler, Fields.RESULTS);
	}

	/**
	 * Creates an object of type {@link Fields} from array of {@link KeyField}
	 * 
	 * @param keyFields
	 *            an array of {@link KeyField} containing the field name and
	 *            sort order
	 * @return an object of type {@link Fields}
	 */
	private Fields getFieldsFromKeyFields(KeyField[] keyFields) {
		if (keyFields == null) {
			return Fields.NONE;
		}

		String[] fieldNames = new String[keyFields.length];
		int i = 0;
		for (KeyField eachField : keyFields) {
			fieldNames[i] = eachField.getName();
			i++;
		}

		Fields fields = new Fields(fieldNames);
		i = 0;
		for (KeyField eachField : keyFields) {
			if (eachField.getSortOrder().equalsIgnoreCase("desc")) {
				fields.setComparator(eachField.getName(), Collections.reverseOrder());
			}
			i++;
		}
		return fields;
	}

	private class NullOutSocketException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NullOutSocketException(String componentId) {
			super("Out socket cannot be null for Remove Dups component '" + componentId + "'");
			Logger LOG = LoggerFactory.getLogger(NullOutSocketException.class);
			LOG.error(this.getMessage(), this);
		}
	}

	@Override
	public void initializeEntity(RemoveDupsEntity assemblyEntityBase) {
		this.removeDupsEntity=assemblyEntityBase;
	}
}
