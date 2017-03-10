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
package hydrograph.engine.cascading.tuplegenerator;

import cascading.tuple.Tuple;

import java.lang.reflect.Type;

public class RandomTupleGenerator implements ITupleGenerator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private listTuples;
	private GenerateDataEntity generateDataEntity;
	private Type[] fieldDataTypes;

	public RandomTupleGenerator(GenerateDataEntity generateDataEntity) {

		this.generateDataEntity = generateDataEntity;
		this.fieldDataTypes = generateDataEntity.getDataTypes();

	}

	@Override
	public Tuple getTuple() {
		Tuple tuple = new Tuple();
		for (int j = 0; j < generateDataEntity.getInputFields().size(); j++) {
			Type type = fieldDataTypes[j];
			FieldEntity fieldEntity = initializeFieldEntity(generateDataEntity,
					j);
			tuple.add(TypeFactory.getFieldType(type).getFieldValue(fieldEntity));
		}
		return tuple;
	}

	private FieldEntity initializeFieldEntity(
			GenerateDataEntity generateDataEntity, int index) {
		FieldEntity fieldEntity = new FieldEntity();
		fieldEntity
				.setDefaultValue(generateDataEntity.getFieldDefaultValue()[index]);
		fieldEntity.setFieldFormat(generateDataEntity.getFieldFormat()[index]);
		fieldEntity.setFieldLength(generateDataEntity.getFieldLength()[index]);
		fieldEntity.setFieldScale(generateDataEntity.getFieldScale()[index]);
		fieldEntity.setRangeFromValue(generateDataEntity
				.getFieldFromRangeValue()[index]);
		fieldEntity
				.setRangeToValue(generateDataEntity.getFieldToRangeValue()[index]);
		fieldEntity.setType(generateDataEntity.getDataTypes()[index]);
		return fieldEntity;

	}
}
