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

import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.SortEntity;
import hydrograph.engine.core.component.entity.elements.KeyField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;


/**
 * @author Prabodh
 */
public class SortAssembly extends BaseComponent<SortEntity> {
	
	private static final long serialVersionUID = 3468787324609517976L;

	private SortEntity sortEntity;
	private static Logger LOG = LoggerFactory.getLogger(SortAssembly.class);

	public SortAssembly(SortEntity baseComponentEntity, ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(sortEntity.toString());
			}
			for (OutSocket outSocket : sortEntity.getOutSocketList()) {
				LOG.trace("Creating sort assembly for '" + sortEntity.getComponentId() + "' for socket: '"
						+ outSocket.getSocketId() + "' of type: '" + outSocket.getSocketType() + "'");
				
				Fields keyFields = getFieldsFromKeyFields(sortEntity.getKeyFields());
				Fields secondaryKeyFields = getFieldsFromKeyFields(sortEntity.getSecondaryKeyFields());

				if (keyFields != null && LOG.isDebugEnabled()) {
					LOG.debug("Key fields for sort component: '" + sortEntity.getComponentId() + "':  "
							+ keyFields.toString());
				}
				if (secondaryKeyFields != null && LOG.isDebugEnabled()) {
					LOG.debug("Secondary key fields for sort component: '" + sortEntity.getComponentId() + "':  "
							+ secondaryKeyFields.toString());
				}
				Pipe sortOutput = new GroupBy(sortEntity.getComponentId()+outSocket.getSocketId(), componentParameters.getInputPipe(),
						keyFields, secondaryKeyFields);

				setHadoopProperties(sortOutput.getStepConfigDef());

				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(), sortEntity.getComponentId(), sortOutput,
						componentParameters.getInputFields());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
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
			return null;
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

	@Override
	public void initializeEntity(SortEntity assemblyEntityBase) {
		this.sortEntity=assemblyEntityBase;
	}
}