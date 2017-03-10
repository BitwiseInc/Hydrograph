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

import cascading.pipe.Checkpoint;
import cascading.pipe.CoGroup;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.pipe.assembly.Rename;
import cascading.pipe.assembly.Retain;
import cascading.pipe.joiner.Joiner;
import cascading.pipe.joiner.MixedJoin;
import cascading.pipe.joiner.OuterJoin;
import cascading.tuple.Fields;
import com.google.common.primitives.Booleans;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.JoinHelper;
import hydrograph.engine.cascading.filters.BlockAllFilter;
import hydrograph.engine.cascading.filters.JoinGetUnmatchedRecordsFilter;
import hydrograph.engine.cascading.filters.JoinOutLinkFilter;
import hydrograph.engine.cascading.filters.JoinUnusedLinkFilter;
import hydrograph.engine.core.component.entity.JoinEntity;
import hydrograph.engine.core.component.entity.elements.JoinKeyFields;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.utilities.ComponentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * Join Component for joining two or more files.
 * 
 * @author ganesha
 * 
 */

public class JoinAssembly extends BaseComponent<JoinEntity> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory
			.getLogger(JoinAssembly.class);

	private final String RECORD_PRESENT_INDICATOR = "flag";

	private Fields[] uniqInputFields;
	private Fields[] uniqKeyFields;
	private Pipe[] inputLinks;
	private Joiner joiner;
	private JoinHelper joinHelper;
	private JoinEntity joinEntity;
	private Boolean isUnusedSocketPresent;
	private boolean[] joinTypes;

	public JoinAssembly(JoinEntity joinEntity, ComponentParameters compParams) {
		super(joinEntity, compParams);
	}

	@Override
	protected void createAssembly() {
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace(joinEntity.toString());
			}
			OutSocket outSocket = getSocketForType(
					joinEntity.getOutSocketList(), "out").get(0);
			List<OutSocket> unusedSocket = getSocketForType(
					joinEntity.getOutSocketList(), "unused");
			isUnusedSocketPresent = unusedSocket.size() > 0;
			prepare();

			if (isUnusedSocketPresent) {
				inputLinks = addRecordPresentIndicatorField(inputLinks,
						RECORD_PRESENT_INDICATOR, 1);
			}

			LOG.trace("Creating join assembly for '"
					+ joinEntity.getComponentId() + "' for socket: '"
					+ outSocket.getSocketId() + "' of type: '"
					+ outSocket.getSocketType() + "'");
			Pipe join = new CoGroup(joinEntity.getComponentId()+outSocket.getSocketId(),inputLinks, uniqKeyFields,
					getJoinOutputFields(outSocket), joiner);
			setHadoopProperties(join.getStepConfigDef());

			// add checkpoint if unused port in use
			Pipe joinResult;
			if (isUnusedSocketPresent) {
				joinResult = new Checkpoint( join);
			} else {
				joinResult = join;
			}

			setOutLink(joinResult, outSocket);
			if (isUnusedSocketPresent) {
				setUnusedLinks(joinResult, unusedSocket);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * prepares the joiner for Join and initializes the input pipes, input
	 * fields & key fields
	 */
	private void prepare() {
		joinHelper = new JoinHelper(componentParameters);
		uniqInputFields = new Fields[componentParameters.getInputFieldsList()
				.size()];
		uniqKeyFields = new Fields[joinEntity.getAllKeyFieldSize()];
		inputLinks = new Pipe[componentParameters.getInputPipes().size()];
		joinTypes = new boolean[joinEntity.getAllKeyFieldSize()];

		Fields inputFields;
		Fields keyFields = new Fields();

		for (int i = 0; i < componentParameters.getinSocketId().size(); i++) {

			inputFields = componentParameters.getInputFieldsList().get(i);

			int[] inputFieldsPos = inputFields.getPos();
			for (JoinKeyFields joinKeyFields : joinEntity.getKeyFields()) {
				if (joinKeyFields.getInSocketId().equalsIgnoreCase(
						componentParameters.getinSocketId().get(i))) {
					keyFields = new Fields(joinKeyFields.getFields());
					joinTypes[i] = joinKeyFields.isRecordRequired();
				}
			}

			// rename fields. prefix with file index
			int[] keyFieldsPos = keyFields.getPos();
			for (int j : inputFieldsPos) {
				inputFields = inputFields.rename(new Fields(inputFields.get(j)
						.toString()), new Fields(i + "."
						+ inputFields.get(j).toString()));
			}

			uniqInputFields[i] = inputFields;

			// rename key fields. prefix with file index
			for (int j : keyFieldsPos) {
				keyFields = keyFields.rename(new Fields(keyFields.get(j)
						.toString()), new Fields(i + "."
						+ keyFields.get(j).toString()));
			}

			uniqKeyFields[i] = keyFields;
			Pipe inputLink = componentParameters.getInputPipes().get(i);
			inputLink = new Rename(inputLink, componentParameters
					.getInputFieldsList().get(i), inputFields);
			inputLink = new Pipe("link_" + i, inputLink);

			// retain only mapped fields and key fields
			// to be done
			inputLinks[i] = inputLink;
		}

		if (isUnusedSocketPresent) {
			joiner = new OuterJoin();
		} else {
			joiner = new MixedJoin(joinTypes);
		}

	}

	/**
	 * Fetches the List of {@link OutSocket} for specific socket type from all
	 * Sockets
	 * 
	 * @param outSocketList
	 * @param socketType
	 * @return List of {@link OutSocket}
	 */
	private List<OutSocket> getSocketForType(List<OutSocket> outSocketList,
											 String socketType) {
		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		List<OutSocket> unusedOutSockets = new ArrayList<OutSocket>();
		for (int i = 0; i < outSocketList.size(); i++) {
			if (joinEntity.getOutSocketList().get(i).getSocketType()
					.equalsIgnoreCase(socketType)) {

				if (socketType.equalsIgnoreCase("unused")) {
					unusedOutSockets.add(joinEntity.getOutSocketList().get(i));
				} else {
					outSockets.add(joinEntity.getOutSocketList().get(i));
				}

			}
		}

		if (unusedOutSockets.size() > 0) {
			int i = 0;
			for (int j = 0; j < componentParameters.getinSocketId().size(); j++) {
				for (OutSocket outSocket : unusedOutSockets) {
					if (outSocket.getCopyOfInSocketId().equalsIgnoreCase(
							componentParameters.getinSocketId().get(j))) {
						outSockets.add(i, outSocket);
						i++;
					}
				}
			}
		}
		return outSockets;
	}

	/**
	 * Add field flag with value 1 for each row in input file to specify record
	 * is present.
	 * 
	 * @param <T>
	 * 
	 * @param inputPipes
	 * @param fieldName
	 * @param constantValue
	 * @return an array of {@link Pipe}
	 */
	private <T> Pipe[] addRecordPresentIndicatorField(Pipe[] inputPipes,
			String fieldName, T constantValue) {
		Pipe[] pipes = new Pipe[inputPipes.length];
		for (int fileNum = 0; fileNum < inputPipes.length; fileNum++) {
			Pipe tempFile = ComponentHelper.addConstantField(
					inputPipes[fileNum], RECORD_PRESENT_INDICATOR + fileNum,
					constantValue);
			pipes[fileNum] = tempFile;
		}
		return pipes;
	}

	/**
	 * Append fields of all input files
	 * 
	 * @param outSocket
	 * 
	 * @return appended input Fields
	 */
	private Fields getJoinOutputFields(OutSocket outSocket) {
		Fields joinOutputFields = new Fields();
		int i = 0;
		for (Fields fields : uniqInputFields) {
			if (isUnusedSocketPresent)
				fields = fields
						.append(new Fields(RECORD_PRESENT_INDICATOR + i));

			joinOutputFields = joinOutputFields.append(fields);
			i++;
		}

		return joinOutputFields;
	}

	/**
	 * get record present flags fields of files on which outer join is applied
	 * 
	 * @return appended record present flags Fields of files on which outer join
	 *         is specified
	 */
	private Fields getOuterJoinFilesFlagFields() {
		Fields outerJoinFilesFlagFields = new Fields();

		for (int i = 0; i < uniqKeyFields.length; i++) {

			if (uniqKeyFields[i] == null) {
				continue;
			}

			if (getAllJoinTypes()[i]) {
				outerJoinFilesFlagFields = outerJoinFilesFlagFields
						.append(new Fields(RECORD_PRESENT_INDICATOR + i));
			}

		}

		return outerJoinFilesFlagFields;
	}

	/**
	 * get the join type as inputsocket sequence.
	 * 
	 * @return joinTypes
	 */
	private boolean[] getAllJoinTypes() {
		boolean[] joinTypes = new boolean[joinEntity.getKeyFields().size()];
		for (int i = 0; i < componentParameters.getinSocketId().size(); i++) {
			for (JoinKeyFields keyFields2 : joinEntity.getKeyFields()) {
				if (keyFields2.getInSocketId().equalsIgnoreCase(
						componentParameters.getinSocketId().get(i))) {
					joinTypes[i] = keyFields2.isRecordRequired();
				}

			}
		}

		return joinTypes;
	}

	/**
	 * Filter actual join result if FULL OUTER JOIN is performed. Apply output
	 * scheme and set OUT Port
	 * 
	 * @param joinResult
	 * @param outSocket
	 */
	private void setOutLink(Pipe joinResult, OutSocket outSocket) {
		Pipe joinFiltered;
		// OUT Link : if full outer join is specified no need to apply filter
		if (!Booleans.contains(getAllJoinTypes(), true)) {
			setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
					joinEntity.getComponentId(),
					applyOutPutSchema(joinResult, outSocket),
					joinHelper.getMapTargetFields(outSocket));

		} else if (isUnusedSocketPresent) {

			// Apply filter for join result as full outer join is performed for
			// getting results for UNUSED Links out of joined file

			joinFiltered = new Each(joinResult, getOuterJoinFilesFlagFields(),
					new JoinOutLinkFilter());

			setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
					joinEntity.getComponentId(),
					applyOutPutSchema(joinFiltered, outSocket),
					joinHelper.getMapTargetFields(outSocket));

		} else {

			setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
					joinEntity.getComponentId(),
					applyOutPutSchema(joinResult, outSocket),
					joinHelper.getMapTargetFields(outSocket));

		}
	}

	/**
	 * Filter unmatched records from join result if FULL OUTER JOIN is
	 * performed. Set UNUSED PORT respective to each file where unmatched
	 * records are required.
	 * 
	 * @param joinResult
	 */
	private void setUnusedLinks(Pipe joinResult, List<OutSocket> unusedOutSocket) {
		Pipe unUsedLink;
		Pipe allUnMatched;
		Pipe unMatched;

		// Records will be available at UNUSED PORT only if FULL OUTER join is
		// not applied
		if (Booleans.contains(getAllJoinTypes(), true)) {
			// get unUsed records from join result
			allUnMatched = new Each(joinResult, getOuterJoinFilesFlagFields(),
					new JoinGetUnmatchedRecordsFilter());
		} else {
			allUnMatched = joinResult;
		}

		for (int i = 0; i < componentParameters.getinSocketId().size(); i++) {
			for (OutSocket unusedSocket : unusedOutSocket) {
				if (unusedSocket.getCopyOfInSocketId().equalsIgnoreCase(
						componentParameters.getinSocketId().get(i))) {
					// renaming pipes to avoid pipe name conflict with OUT
					// PORT at tail

					LOG.trace("Creating join assembly for '"
							+ joinEntity.getComponentId() + "' for socket: '"
							+ unusedSocket.getSocketId() + "' of type: '"
							+ unusedSocket.getSocketType() + "'");

					unMatched = new Pipe(ComponentHelper.getComponentName("join",joinEntity.getComponentId() ,unusedSocket.getSocketId()), allUnMatched);

					if (Booleans.contains(getAllJoinTypes(), true)) {
						unMatched = new Each(unMatched, new Fields(
								RECORD_PRESENT_INDICATOR + i),
								new JoinUnusedLinkFilter());
					} else {
						unMatched = new Each(unMatched, new Fields(
								RECORD_PRESENT_INDICATOR + i),
								new BlockAllFilter());
					}

					unMatched = new Retain(unMatched, uniqInputFields[i]);

					unMatched = new Rename(unMatched, uniqInputFields[i],
							componentParameters.getCopyOfInSocket(unusedSocket
									.getCopyOfInSocketId()));

					unUsedLink = unMatched;

					// must register all assembly tails
					setOutLink("unused", unusedSocket.getSocketId(),
							joinEntity.getComponentId(), unUsedLink,
							componentParameters.getCopyOfInSocket(unusedSocket
									.getCopyOfInSocketId()));// uniqInputFields[i]);
				}
			}
		}
	}

	/**
	 * Apply output scheme to join result
	 * 
	 * @param joinResult
	 * @param outSocket
	 * @return Pipe array with output scheme
	 */
	private Pipe applyOutPutSchema(Pipe joinResult, OutSocket outSocket) {
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

		for (int i = 0; i < componentParameters.getinSocketId().size(); i++) {
			sourceFields = joinHelper.getMapSourceFields(componentParameters
					.getinSocketId().get(i), outSocket/*, i*/);

			if (sourceFields == null)
				continue;

			int[] sourceFieldsPos = sourceFields.getPos();

			// rename fields. prefix with file index
			for (int j : sourceFieldsPos) {
				sourceFields = sourceFields.rename(
						new Fields(sourceFields.get(j).toString()), new Fields(
								i + "." + sourceFields.get(j).toString()));
			}
			combinedSourceFields = combinedSourceFields.append(sourceFields);
		}
		return combinedSourceFields;
	}

	@Override
	public void initializeEntity(JoinEntity assemblyEntityBase) {
		this.joinEntity=assemblyEntityBase;
	}

}