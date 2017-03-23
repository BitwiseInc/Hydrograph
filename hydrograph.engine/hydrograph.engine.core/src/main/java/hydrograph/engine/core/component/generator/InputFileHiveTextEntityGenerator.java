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
package hydrograph.engine.core.component.generator;

import hydrograph.engine.core.component.entity.InputFileHiveTextEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.core.utilities.GeneralUtilities;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.ihivetextfile.HivePartitionFieldsType;
import hydrograph.engine.jaxb.ihivetextfile.HivePartitionFilterType;
import hydrograph.engine.jaxb.ihivetextfile.PartitionColumn;
import hydrograph.engine.jaxb.ihivetextfile.PartitionFieldBasicType;
import hydrograph.engine.jaxb.inputtypes.HiveTextFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * The Class InputFileHiveTextEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileHiveTextEntityGenerator extends
		InputComponentGeneratorBase {

	private HiveTextFile jaxbHiveTextFile;
	private InputFileHiveTextEntity inputHiveFileEntity;
	private static Logger LOG = LoggerFactory
			.getLogger(InputFileHiveTextEntityGenerator.class);

	public InputFileHiveTextEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbHiveTextFile = (HiveTextFile) baseComponent;
	}

	@Override
	public void createEntity() {
		inputHiveFileEntity = new InputFileHiveTextEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing input file parquet entity for component: "
				+ jaxbHiveTextFile.getId());

		inputHiveFileEntity.setComponentId(jaxbHiveTextFile.getId());

		inputHiveFileEntity
				.setFieldsList(InputEntityUtils
						.extractInputFields(jaxbHiveTextFile.getOutSocket()
								.get(0).getSchema()
								.getFieldOrRecordOrIncludeExternalSchema()));
		inputHiveFileEntity.setOutSocketList(InputEntityUtils
				.extractOutSocket(jaxbHiveTextFile.getOutSocket()));
		inputHiveFileEntity.setDelimiter(jaxbHiveTextFile.getDelimiter() != null
				? GeneralUtilities.parseHex(jaxbHiveTextFile.getDelimiter().getValue()) : null);
		inputHiveFileEntity.setDatabaseName(jaxbHiveTextFile.getDatabaseName()
				.getValue());
		inputHiveFileEntity.setTableName(jaxbHiveTextFile.getTableName()
				.getValue());
		inputHiveFileEntity.setExternalTablePathUri(jaxbHiveTextFile
				.getExternalTablePath() != null ? jaxbHiveTextFile
				.getExternalTablePath().getUri() : null);
		inputHiveFileEntity
				.setQuote(jaxbHiveTextFile.getQuote() != null ? jaxbHiveTextFile
						.getQuote().getValue() : "");
		inputHiveFileEntity
				.setSafe(jaxbHiveTextFile.getSafe() != null ? jaxbHiveTextFile
						.getSafe().isValue() : false);
		inputHiveFileEntity
				.setStrict(jaxbHiveTextFile.getStrict() != null ? jaxbHiveTextFile
						.getStrict().isValue() : false);
		inputHiveFileEntity.setRuntimeProperties(InputEntityUtils
				.extractRuntimeProperties(jaxbHiveTextFile
						.getRuntimeProperties()));
		inputHiveFileEntity
				.setPartitionKeys(extractPartitionFields(jaxbHiveTextFile
						.getPartitionKeys()));
		inputHiveFileEntity.setBatch(jaxbHiveTextFile.getBatch());
		inputHiveFileEntity
				.setPartitionFilterList(populatePartitionFilterList(jaxbHiveTextFile
						.getPartitionFilter()));
		inputHiveFileEntity.setListOfPartitionKeyValueMap(populatePartitionKeyValueMap(jaxbHiveTextFile.getPartitionFilter()));

	}

	private ArrayList<HashMap<String, String>> populatePartitionKeyValueMap(HivePartitionFilterType partitionFilter) {
		ArrayList<HashMap<String, String>> partitionKeyValueMap = new ArrayList<>();
		if (partitionFilter != null && partitionFilter.getPartitionColumn() != null) {

			for (PartitionColumn column : partitionFilter.getPartitionColumn()) {
				HashMap<String, String> map = new HashMap<>();
				map.put(column.getName(), column.getValue());
				if (column.getPartitionColumn() != null)
					fillPartitionKeyValueMap(map, column.getPartitionColumn());
				partitionKeyValueMap.add(map);
			}
		}

		return partitionKeyValueMap;
	}

	private void fillPartitionKeyValueMap(HashMap<String, String> partitionKeyValue, PartitionColumn partitionColumn) {
		partitionKeyValue.put(partitionColumn.getName(),partitionColumn.getValue());
		if(partitionColumn.getPartitionColumn()!=null)
			fillPartitionKeyValueMap(partitionKeyValue,partitionColumn.getPartitionColumn());
	}

	private ArrayList<ArrayList<String>> populatePartitionFilterList(HivePartitionFilterType hivePartitionFilterType) {
		ArrayList<ArrayList<String>> listOfPartitionColumn = new ArrayList<ArrayList<String>>();
		if (hivePartitionFilterType != null && hivePartitionFilterType.getPartitionColumn() != null) {
			for (PartitionColumn partitionColumn : hivePartitionFilterType.getPartitionColumn()) {
				ArrayList<String> arrayList = new ArrayList<String>();
				arrayList = fillArrayList(partitionColumn, arrayList);
				listOfPartitionColumn.add(arrayList);
			}
		}
		return listOfPartitionColumn;
	}

	private ArrayList<String> fillArrayList(PartitionColumn partitionColumn,
			ArrayList<String> listOfPartitionColumn) {
		listOfPartitionColumn.add(partitionColumn.getValue());
		if (partitionColumn.getPartitionColumn() != null) {
			listOfPartitionColumn = fillArrayList(partitionColumn.getPartitionColumn(), listOfPartitionColumn);
		}
		return listOfPartitionColumn;
	}

	/**
	 * This method extracts partition keys from {@link HivePartitionFieldsType}
	 * hivePartitionFieldsType which is passed as a parameter.
	 * 
	 * If hivePartitionFieldsType object is null then string array of size of 0
	 * will be returned.
	 * 
	 * @param hivePartitionFieldsType
	 * @return String[]
	 */
	private String[] extractPartitionFields(
			HivePartitionFieldsType hivePartitionFieldsType) {
		String[] partitionKeys;
		List<String> partitionFieldsList = new ArrayList<String>();
		if (hivePartitionFieldsType != null
				&& hivePartitionFieldsType.getField() != null) {
			partitionFieldsList = getPartitionFieldsList(
					hivePartitionFieldsType.getField(), partitionFieldsList);
			partitionKeys = partitionFieldsList
					.toArray(new String[partitionFieldsList.size()]);
			return partitionKeys;
		} else {
			return new String[0];
		}
	}

	private List<String> getPartitionFieldsList(
			PartitionFieldBasicType partitionFieldBasicType,
			List<String> partitionFieldsList) {
		partitionFieldsList.add(partitionFieldBasicType.getName());
		if (partitionFieldBasicType.getField() != null) {
			getPartitionFieldsList(partitionFieldBasicType.getField(),
					partitionFieldsList);
		}
		return partitionFieldsList;
	}



	@Override
	public InputFileHiveTextEntity getEntity() {
		return inputHiveFileEntity;
	}


}