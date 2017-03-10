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
import cascading.pipe.Merge;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.functions.CopyFields;
import hydrograph.engine.core.component.entity.UnionAllEntity;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class UnionAllAssembly extends BaseComponent<UnionAllEntity> {

	private static final long serialVersionUID = -1271944466197184074L;
	private static Logger LOG = LoggerFactory.getLogger(UnionAllAssembly.class);

	private UnionAllEntity unionAllEntity;

	public UnionAllAssembly(UnionAllEntity baseComponentEntity,
			ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(unionAllEntity.toString());
			}
			LOG.trace("Creating union all assembly for '"
					+ unionAllEntity.getComponentId() + "' for socket: '"
					+ unionAllEntity.getOutSocket().getSocketId()
					+ "' of type: '"
					+ unionAllEntity.getOutSocket().getSocketType() + "'");

			ArrayList<Fields> fieldList = componentParameters
					.getInputFieldsList();
			
			validateInputFields();
		
			Pipe[] inputPipes = alignfields(
					componentParameters.getInputPipes(), fieldList);

			Pipe outPipe = new Merge(unionAllEntity.getComponentId()+unionAllEntity.getOutSocket().getSocketId(), inputPipes);

			setHadoopProperties(outPipe.getStepConfigDef());

			setOutLink("out", unionAllEntity.getOutSocket().getSocketId(),
					unionAllEntity.getComponentId(), outPipe, fieldList.get(0));
		} catch (SchemaMismatchException e) {
			LOG.error(e.getMessage(), e);
			throw new SchemaMismatchException(e);
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

	}

	private void validateInputFields() throws SchemaMismatchException {
		ArrayList<Set<SchemaField>> schemaFieldList = componentParameters.getSchemaFieldList();
		Set<SchemaField> refSchema = schemaFieldList.get(0);
		
		for (int i = 1; i < schemaFieldList.size(); i++) {
			if (refSchema.size() != schemaFieldList.get(i).size()) {
				throw new SchemaMismatchException("Component:" + unionAllEntity.getComponentId()
						+ " - Different schema is defined for input sockets. For UnionAll component schema of all input sockets should be same.");
			}
		}

		for (int i = 1; i < schemaFieldList.size(); i++) {
			if (!isEqualSchema(refSchema, schemaFieldList.get(i))){
				throw new SchemaMismatchException("Component:" + unionAllEntity.getComponentId()
						+ " - Different schema is defined for input sockets. For UnionAll component schema of all input sockets should be same.");
			}
		}

	}
	

	private boolean isEqualSchema(Set<SchemaField> refSchema,
			Set<SchemaField> set) {
		HashMap<String, Boolean> fieldCheckMap = new HashMap<String, Boolean>(
				refSchema.size());
		for (SchemaField refSchemaField : refSchema){
			fieldCheckMap.put(refSchemaField.getFieldName(), false);
			for (SchemaField setSchemaField : set){
				if (setSchemaField.getFieldName().equals(
						refSchemaField.getFieldName())
						&& setSchemaField.getFieldDataType().equals(
								refSchemaField.getFieldDataType())) {
					fieldCheckMap.put(refSchemaField.getFieldName(), true);
					break;
				}
			}
		}
		
		if (fieldCheckMap.containsValue(false))
			return false;
		else 
			return true;
	}

	private Pipe[] alignfields(ArrayList<Pipe> arrayList,
			ArrayList<Fields> fieldList) {
		Pipe[] inputPipes = new Pipe[componentParameters.getInputPipes().size()];
		int i = 0;
		for (Pipe eachPipe : arrayList) {
			inputPipes[i] = new Each(eachPipe, fieldList.get(i),
					new CopyFields(fieldList.get(0)), Fields.RESULTS);
			i++;
		}
		return inputPipes;
	}

	@Override
	public void initializeEntity(UnionAllEntity assemblyEntityBase) {
		this.unionAllEntity = assemblyEntityBase;
	}
	
	public class SchemaMismatchException extends RuntimeException{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SchemaMismatchException(String msg) {
			super(msg);
		}
		
		public SchemaMismatchException(Throwable e) {
			super(e);
		}
		
	}

}
