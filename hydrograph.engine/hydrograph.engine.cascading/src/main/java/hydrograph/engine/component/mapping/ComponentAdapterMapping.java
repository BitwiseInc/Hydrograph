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

package hydrograph.engine.component.mapping;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

public enum ComponentAdapterMapping {

	TEXTFILEDELIMITED(ComponentType.TEXTFILEDELIMITED),
	TEXTFILEFIXEDWIDTH(ComponentType.TEXTFILEFIXEDWIDTH),
	TEXTFILEMIXEDSCHEME(ComponentType.TEXTFILEMIXEDSCHEME),
	GENERATERECORD(ComponentType.GENERATERECORD),
	SEQUENCEINPUTFILE(ComponentType.SEQUENCEINPUTFILE),
	SEQUENCEOUTPUTFILE(ComponentType.SEQUENCEOUTPUTFILE),
	REMOVEDUPS(ComponentType.REMOVEDUPS),
	UNIONALL(ComponentType.UNIONALL),
	CLONE(ComponentType.CLONE),
	LIMIT(ComponentType.LIMIT),
	SORT(ComponentType.SORT),
	FILTER(ComponentType.FILTER),
	AGGREGATE(ComponentType.AGGREGATE),
	CUMULATE(ComponentType.CUMULATE),
	TRANSFORM(ComponentType.TRANSFORM),
	JOIN(ComponentType.JOIN),
	LOOKUP(ComponentType.LOOKUP),
	GENERATESEQUENCE(ComponentType.GENERATESEQUENCE),
	NORMALIZE(ComponentType.NORMALIZE),
	PARTITIONBYEXPRESSION(ComponentType.PARTITIONBYEXPRESSION),
	DISCARD(ComponentType.DISCARD),
	RUNPROGRAM(ComponentType.RUNPROGRAM),
	HPLSQL(ComponentType.HPLSQL),AVROFILE(ComponentType.AVROFILE),
	HIVETEXTFILE(ComponentType.HIVETEXTFILE),
	PARQUETHIVEFILE(ComponentType.PARQUETHIVEFILE),
	PARQUETFILE(ComponentType.PARQUETFILE),
	MYSQL(ComponentType.MYSQL),
	REDSHIFT(ComponentType.REDSHIFT),
	ORACLE(ComponentType.ORACLE);

	private final ComponentType componentType;

	ComponentAdapterMapping(ComponentType componentType) {
		this.componentType = componentType;
	}

	private enum ComponentType {

		TEXTFILEDELIMITED {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("TextFileDelimited")) {
					return ADAPTER_PACKAGE + ".InputFileDelimitedAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputFileDelimitedAdapter";
				}

			}
		},
		TEXTFILEFIXEDWIDTH {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("TextFileFixedWidth")) {
					return ADAPTER_PACKAGE + ".InputFileFixedWidthAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputFileFixedWidthAdapter";
				}
			}
		},
		TEXTFILEMIXEDSCHEME{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("TextFileMixedScheme")) {
					return ADAPTER_PACKAGE + ".InputFileMixedSchemeAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputFileMixedSchemeAdapter";
				}
			}
		},
		GENERATERECORD{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
					return ADAPTER_PACKAGE + ".GenerateRecordAdapter";
			}
		},
		SEQUENCEINPUTFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".InputFileSequenceFormatAdapter";
			}
		},
		SEQUENCEOUTPUTFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".OutputFileSequenceFormatAdapter";
			}
		},
		REMOVEDUPS{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".RemoveDupsAdapter";
			}
		},
		UNIONALL{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".UnionAllAdapter";
			}
		},
		CLONE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".CloneAdapter";
			}
		},
		LIMIT{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".LimitAdapter";
			}
		},
		SORT{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".SortAdapter";
			}
		},
		FILTER{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".FilterAdapter";
			}
		},
		AGGREGATE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".AggregateAdapter";
			}
		},
		CUMULATE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".CumulateAdapter";
			}
		},
		TRANSFORM{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".TransformAdapter";
			}
		},
		JOIN{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".JoinAdapter";
			}
		},
		HASHJOIN{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".HashJoinAdapter";
			}
		},
		LOOKUP{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".LookupAdapter";
			}
		},
		GENERATESEQUENCE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".UniqueSequenceAdapter";
			}
		},
		NORMALIZE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".NormalizeAdapter";
			}
		},
		PARTITIONBYEXPRESSION{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".PartitionByExpressionAdapter";
			}
		},
		DISCARD{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".DiscardAdapter";
			}
		},
		RUNPROGRAM{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".RunProgramAdapter";
			}
		},
		HPLSQL{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return ADAPTER_PACKAGE + ".HplSqlComponentGenerator";
			}
		},
		AVROFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("AvroFile")) {
					return ADAPTER_PACKAGE + ".InputFileAvroAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputFileAvroAdapter";
				}
			}
		},
		HIVETEXTFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("HiveTextFile")) {
					return ADAPTER_PACKAGE + ".InputFileHiveTextAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputFileHiveTextAdapter";
				}
			}
		},
		PARQUETHIVEFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("ParquetHiveFile")) {
					return ADAPTER_PACKAGE + ".InputFileHiveParquetAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputFileHiveParquetAdapter";
				}
			}
		},
		PARQUETFILE {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("ParquetFile")) {
					return ADAPTER_PACKAGE + ".InputFileParquetAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputFileParquetAdapter";
				}
			}
		},
		MYSQL {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("Mysql")) {
					return ADAPTER_PACKAGE + ".InputMysqlAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputMysqlAdapter";
				}
			}
		},
		
		REDSHIFT {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("Redshift")) {
					return ADAPTER_PACKAGE + ".InputRedShiftAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputRedShiftAdapter";
				}
			}
		},
		
		
		ORACLE {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("Oracle")) {
					return ADAPTER_PACKAGE + ".InputOracleAdapter";
				} else {
					return ADAPTER_PACKAGE + ".OutputOracleAdapter";
				}
			}
		};

		
		private static final String ADAPTER_PACKAGE = "hydrograph.engine.adapters";

		abstract String getMapping(TypeBaseComponent typeBaseComponent);
	}

	public String getMappingType(TypeBaseComponent typeBaseComponent) {
		return componentType.getMapping(typeBaseComponent);
	}
}
