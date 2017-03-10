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

import cascading.pipe.HashJoin;
import cascading.pipe.Pipe;
import cascading.pipe.assembly.Rename;
import cascading.pipe.assembly.Retain;
import cascading.pipe.joiner.Joiner;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.JoinHelper;
import hydrograph.engine.cascading.joiners.HashJoinJoiner;
import hydrograph.engine.cascading.joiners.HashJoinJoiner.Option;
import hydrograph.engine.core.component.entity.LookupEntity;
import hydrograph.engine.core.component.entity.elements.JoinKeyFields;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HashJoin Component for joining two or more files.
 * 
 * @author ganesha
 * 
 */

public class LookupAssembly extends BaseComponent<LookupEntity> {

	private static final long serialVersionUID = 1L;
	private LookupEntity lookupEntity;
	private static Logger LOG = LoggerFactory.getLogger(LookupAssembly.class);

	private Fields[] uniqInputFields;
	private Fields[] uniqKeyFields;
	private Pipe[] inputPipes;
	private Joiner joiner;
	private JoinHelper joinHelper;

	public LookupAssembly(LookupEntity lookupEntity,
			ComponentParameters componentParameters) {
		super(lookupEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(lookupEntity.toString());
			}
			for (OutSocket outSocket : lookupEntity.getOutSocketList()) {
				LOG.trace("Creating hash join assembly for '"
						+ lookupEntity.getComponentId() + "' for socket: '"
						+ outSocket.getSocketId() + "' of type: '"
						+ outSocket.getSocketType() + "'");

				prepare(outSocket);

				Pipe join = new HashJoin(lookupEntity.getComponentId()+outSocket.getSocketId(), inputPipes,
						uniqKeyFields, getJoinOutputFields(), joiner);

				setHadoopProperties(join.getStepConfigDef());
				setOutLink(join, outSocket);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	private Fields getJoinOutputFields() {
		Fields joinOutputFields = new Fields();
		for (Fields fields : uniqInputFields) {
			joinOutputFields = joinOutputFields.append(fields);
		}
		return joinOutputFields;
	}

	private void setOutLink(Pipe joinResult, OutSocket outSocket) {
		setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
				lookupEntity.getComponentId(),
				applyOutputSchema(joinResult, outSocket),
				joinHelper.getMapTargetFields(outSocket));
	}

	private Pipe applyOutputSchema(Pipe joinResult, OutSocket outSocket) {
		Pipe outPort;

		// Add output file scheme to join result
		outPort = new Rename(joinResult,
				getAllSourceFieldsWithFileIndexPrefix(outSocket),
				joinHelper.getMapTargetFields(outSocket));

		outPort = new Retain(outPort, joinHelper.getMapTargetFields(outSocket));

		return outPort;
	}

	private Fields getAllSourceFieldsWithFileIndexPrefix(OutSocket outSocket) {
		Fields sourceFields;

		Fields combinedSourceFields = new Fields();

		int k = 1;
		for (int i = 0; i < componentParameters.getInputPipes().size(); i++) {
			int index;
			String inSocketType = componentParameters.getinSocketType().get(i);

			if (!inSocketType.equals("driver"))
				index = k;
			else
				index = 0;

			sourceFields = joinHelper.getMapSourceFields(componentParameters
					.getinSocketId().get(i), outSocket);
			if (sourceFields == null)
				continue;

			// rename fields. prefix with file index
			int[] sourceFieldsPos = sourceFields.getPos();
			for (int j : sourceFieldsPos) {
				sourceFields = sourceFields.rename(
						new Fields(sourceFields.get(j).toString()), new Fields(
								index + "." + sourceFields.get(j).toString()));
			}
			combinedSourceFields = combinedSourceFields.append(sourceFields);

			if (!inSocketType.equals("driver"))
				k++;
		}

		return combinedSourceFields;
	}

	private Option getMatchOption(String match) {
		Option option = null;

		if (match.equals("first"))
			option = Option.first;
		else if (match.equals("last"))
			option = Option.last;
		else if (match.equals("all"))
			option = Option.all;

		return option;
	}

	/**
	 * prepares the {@link HashJoinJoiner} for Join and initializes the input
	 * pipes, input fields & key fields
	 */
	private void prepare(OutSocket outSocket) {

		joinHelper = new JoinHelper(componentParameters);
		uniqInputFields = new Fields[componentParameters.getInputFieldsList()
				.size()];
		uniqKeyFields = new Fields[lookupEntity.getAllKeyFieldSize()];
		inputPipes = new Pipe[componentParameters.getInputPipes().size()];

		Fields inputFields;
		Fields keyFields = new Fields();

		joiner = new HashJoinJoiner(getMatchOption(lookupEntity.getMatch()));

		int k = 1;
		for (int i = 0; i < componentParameters.getInputPipes().size(); i++) {
			int index;

			inputFields = componentParameters.getInputFieldsList().get(i);

			String inputSocketType = componentParameters.getinSocketType().get(
					i);

			if (!inputSocketType.equals("driver"))
				index = k;
			else
				index = 0;

			int[] inputFieldsPos = inputFields.getPos();

			for (JoinKeyFields keyFieldsEntity : lookupEntity.getKeyFields()) {
				if (keyFieldsEntity.getInSocketId().equalsIgnoreCase(
						componentParameters.getinSocketId().get(i))) {
					keyFields = new Fields(keyFieldsEntity.getFields());
				}
			}

			// rename fields. prefix with file index
			int[] keyFieldsPos = keyFields.getPos();
			for (int j : inputFieldsPos) {
				inputFields = inputFields
						.rename(new Fields(inputFields.get(j).toString()),
								new Fields(index + "."
										+ inputFields.get(j).toString()));
			}

			uniqInputFields[index] = inputFields;

			// rename key fields. prefix with file index
			for (int j : keyFieldsPos) {
				keyFields = keyFields.rename(new Fields(keyFields.get(j)
						.toString()), new Fields(index + "."
						+ keyFields.get(j).toString()));
			}

			uniqKeyFields[index] = keyFields;

			Pipe inputLink = componentParameters.getInputPipes().get(i);
			inputLink = new Rename(inputLink, componentParameters
					.getInputFieldsList().get(i), inputFields);
			inputLink = new Pipe("link_" + index, inputLink);

			// retain only mapped fields and key fields
			// to be done
			inputPipes[index] = inputLink;

			if (!inputSocketType.equals("driver"))
				k++;
		}
	}

	@Override
	public void initializeEntity(LookupEntity assemblyEntityBase) {
		this.lookupEntity=assemblyEntityBase;
	}

}
