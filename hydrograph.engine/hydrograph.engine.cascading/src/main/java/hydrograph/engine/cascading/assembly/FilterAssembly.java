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
import hydrograph.engine.cascading.assembly.handlers.FilterCustomHandler;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.filters.RecordFilter;
import hydrograph.engine.core.component.entity.FilterEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.expression.api.ValidationAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.HashMap;
import java.util.Map;

public class FilterAssembly extends BaseComponent<FilterEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7728233694925104673L;

	private FilterEntity filterEntity;
	private static Logger LOG = LoggerFactory.getLogger(FilterAssembly.class);

	public FilterAssembly(FilterEntity baseComponentEntity, ComponentParameters parameters) {
		super(baseComponentEntity, parameters);
	}
	
	private void setOperationClassInCaseExpression() {
		for (int i = 0; i < filterEntity.getOperationsList().size(); i++) {
			if (filterEntity.getOperationsList().get(i).getOperationClass() == null) {
				filterEntity.getOperationsList().get(i)
						.setOperationClass(
								"hydrograph.engine.expression.userfunctions.FilterForExpression");
			}
		}
	}
	
	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(filterEntity.toString());
			}
			setOperationClassInCaseExpression();
			for (OutSocket outSocket : filterEntity.getOutSocketList()) {
				LOG.trace("Creating filter assembly for '" + filterEntity.getComponentId() + "' for socket: '"
						+ outSocket.getSocketId() + "' of type: '" + outSocket.getSocketType() + "'");
				createAssemblyFor(outSocket.getSocketId(), outSocket.getSocketType());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	private void createAssemblyFor(String socketId, String socketType) {

		boolean isUnused = false;

		if (socketType.equals("unused")) {
			isUnused = true;
		}

		Pipe filterPipe = new Pipe(filterEntity.getComponentId()+ socketId,
				componentParameters.getInputPipe());
		// validate expression
		ValidationAPI validationAPI = null;
		if (filterEntity.getOperation().getOperationClass().equals("hydrograph.engine.expression.userfunctions.FilterForExpression")) {
			validationAPI = new ValidationAPI(filterEntity.getOperation().getExpression(),
					componentParameters.getUDFPath());
			expressionValidate(validationAPI);
		}
		FilterCustomHandler filterCustomHandler = new FilterCustomHandler(
				new Fields(filterEntity.getOperation().getOperationInputFields()),
				filterEntity.getOperation().getOperationClass(), filterEntity.getOperation().getOperationProperties(),
				isUnused, validationAPI);

		RecordFilter selectCustomFilter = new RecordFilter(filterCustomHandler,
				componentParameters.getInputPipe().getName(),validationAPI);

		setHadoopProperties(filterPipe.getStepConfigDef());

		filterPipe = new Each(filterPipe, new Fields(filterEntity.getOperation().getOperationInputFields()),
				selectCustomFilter);

		setOutLink(socketType, socketId, filterEntity.getComponentId(), filterPipe,
				componentParameters.getInputFields());
	}

	private void expressionValidate(ValidationAPI validationAPI) {
			Map<String, Class<?>> schemaMap = new HashMap<String, Class<?>>();
			try {
				for (SchemaField schemaField : componentParameters.getSchemaFields()) {
					schemaMap.put(schemaField.getFieldName(), Class.forName(schemaField.getFieldDataType()));
				}
				DiagnosticCollector<JavaFileObject> diagnostic = validationAPI.filterCompiler(schemaMap);
				if (diagnostic.getDiagnostics().size() > 0) {
					throw new RuntimeException(diagnostic.getDiagnostics().get(0).getMessage(null));
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
	}

	@Override
	public void initializeEntity(FilterEntity assemblyEntityBase) {
		this.filterEntity = assemblyEntityBase;
	}

}
