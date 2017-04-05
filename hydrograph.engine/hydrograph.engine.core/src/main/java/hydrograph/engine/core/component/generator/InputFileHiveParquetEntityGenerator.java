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

import hydrograph.engine.core.component.entity.InputFileHiveParquetEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.ihiveparquet.HivePartitionFieldsType;
import hydrograph.engine.jaxb.ihiveparquet.HivePartitionFilterType;
import hydrograph.engine.jaxb.ihiveparquet.PartitionColumn;
import hydrograph.engine.jaxb.ihiveparquet.PartitionFieldBasicType;
import hydrograph.engine.jaxb.inputtypes.ParquetHiveFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * The Class InputFileHiveParquetEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileHiveParquetEntityGenerator extends
		InputComponentGeneratorBase {

	private ParquetHiveFile jaxbInputFileHiveParquetFile;
	private InputFileHiveParquetEntity inputFileHiveParquetEntity;
	private static Logger LOG = LoggerFactory
			.getLogger(InputFileHiveParquetEntityGenerator.class);

	public InputFileHiveParquetEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbInputFileHiveParquetFile = (ParquetHiveFile) baseComponent;
	}

	@Override
	public void createEntity() {
		inputFileHiveParquetEntity = new InputFileHiveParquetEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing input file hive parquet entity for component: "
				+ jaxbInputFileHiveParquetFile.getId());
		inputFileHiveParquetEntity.setComponentId(jaxbInputFileHiveParquetFile
				.getId());
		inputFileHiveParquetEntity.setFieldsList(InputEntityUtils
				.extractInputFields(jaxbInputFileHiveParquetFile.getOutSocket()
						.get(0).getSchema()
						.getFieldOrRecordOrIncludeExternalSchema()));
		inputFileHiveParquetEntity.setRuntimeProperties(InputEntityUtils
				.extractRuntimeProperties(jaxbInputFileHiveParquetFile
						.getRuntimeProperties()));
		inputFileHiveParquetEntity.setOutSocketList(InputEntityUtils
				.extractOutSocket(jaxbInputFileHiveParquetFile.getOutSocket()));
		inputFileHiveParquetEntity.setDatabaseName(jaxbInputFileHiveParquetFile
				.getDatabaseName().getValue());
		inputFileHiveParquetEntity.setTableName(jaxbInputFileHiveParquetFile
				.getTableName().getValue());
		inputFileHiveParquetEntity
				.setPartitionKeys(extractPartitionFields(jaxbInputFileHiveParquetFile
						.getPartitionKeys()));
		inputFileHiveParquetEntity
				.setExternalTablePathUri(jaxbInputFileHiveParquetFile
						.getExternalTablePath() == null ? null
						: jaxbInputFileHiveParquetFile.getExternalTablePath()
								.getUri());
		inputFileHiveParquetEntity
				.setPartitionFilterList(populatePartitionFilterList(jaxbInputFileHiveParquetFile.getPartitionFilter()));
		inputFileHiveParquetEntity.setListOfPartitionKeyValueMap(populatePartitionKeyValueMap(jaxbInputFileHiveParquetFile.getPartitionFilter()));
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
	
	private String createPartitionFilterRegex(
			HivePartitionFilterType hivePartitionFilterType) {
		if (hivePartitionFilterType != null
				&& hivePartitionFilterType.getPartitionColumn() != null) {
			String partitionRegex = "";
			String regex = "";
			int numberOfPartitionKeys = inputFileHiveParquetEntity
					.getPartitionKeys().length;
			for (PartitionColumn partitionColumn : hivePartitionFilterType
					.getPartitionColumn()) {
				if (partitionRegex != "") {
					partitionRegex = partitionRegex + "|";
				}
				regex = "";
				regex = buildRegex(partitionColumn, regex);
				if (!(regex.split("\t").length == numberOfPartitionKeys)) {
					regex = regex + "\t.*";
				} else {
					regex = regex + "\\b";
				}
				partitionRegex = partitionRegex + regex;
			}
			return partitionRegex;
		} else {
			return "";
		}
	}

	private String buildRegex(PartitionColumn partitionColumn,
			String partitionRegex) {
		partitionRegex = partitionRegex + partitionColumn.getValue();
		if (partitionColumn.getPartitionColumn() != null) {
			partitionRegex = partitionRegex + "\t";
			partitionRegex = buildRegex(partitionColumn.getPartitionColumn(),
					partitionRegex);
		}
		return partitionRegex;
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
	public InputFileHiveParquetEntity getEntity() {
		return inputFileHiveParquetEntity;
	}

	
}