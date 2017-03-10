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

package hydrograph.engine.cascading.assembly.utils;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

public enum ComponentAssemblyMapping {

	TEXTFILEDELIMITED(AssemblyType.TEXTFILEDELIMITED),
	TEXTFILEFIXEDWIDTH(AssemblyType.TEXTFILEFIXEDWIDTH),
	TEXTFILEMIXEDSCHEME(AssemblyType.TEXTFILEMIXEDSCHEME),
	GENERATERECORD(AssemblyType.GENERATERECORD),
	SEQUENCEINPUTFILE(AssemblyType.SEQUENCEINPUTFILE),
	SEQUENCEOUTPUTFILE(AssemblyType.SEQUENCEOUTPUTFILE),
	REMOVEDUPS(AssemblyType.REMOVEDUPS),
	UNIONALL(AssemblyType.UNIONALL),
	CLONE(AssemblyType.CLONE),
	LIMIT(AssemblyType.LIMIT),
	SORT(AssemblyType.SORT),
	FILTER(AssemblyType.FILTER),
	AGGREGATE(AssemblyType.AGGREGATE),
	CUMULATE(AssemblyType.CUMULATE),
	TRANSFORM(AssemblyType.TRANSFORM),
	JOIN(AssemblyType.JOIN),
	LOOKUP(AssemblyType.LOOKUP),
	GENERATESEQUENCE(AssemblyType.GENERATESEQUENCE),
	NORMALIZE(AssemblyType.NORMALIZE),
	PARTITIONBYEXPRESSION(AssemblyType.PARTITIONBYEXPRESSION),
	DISCARD(AssemblyType.DISCARD),
	RUNPROGRAM(AssemblyType.RUNPROGRAM),
	HPLSQL(AssemblyType.HPLSQL),AVROFILE(AssemblyType.AVROFILE),
	HIVETEXTFILE(AssemblyType.HIVETEXTFILE),
	PARQUETHIVEFILE(AssemblyType.PARQUETHIVEFILE),
	PARQUETFILE(AssemblyType.PARQUETFILE);

	private final AssemblyType assemblyType;

	ComponentAssemblyMapping(AssemblyType assemblyType) {
		this.assemblyType = assemblyType;
	}

	private enum AssemblyType {

		TEXTFILEDELIMITED {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("TextFileDelimited")) {
					return GENERATOR + ".InputFileDelimitedAssemblyGenerator";
				} else {
					return GENERATOR + ".OutputFileDelimitedAssemblyGenerator";
				}

			}
		},
		TEXTFILEFIXEDWIDTH {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("TextFileFixedWidth")) {
					return GENERATOR + ".InputFileFixedWidthAssemblyGenerator";
				} else {
					return GENERATOR + ".OutputFileFixedWidthAssemblyGenerator";
				}
			}
		},
		TEXTFILEMIXEDSCHEME{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("TextFileMixedScheme")) {
					return GENERATOR + ".InputFileMixedSchemeAssemblyGenerator";
				} else {
					return GENERATOR + ".OutputFileMixedSchemeAssemblyGenerator";
				}
			}
		},
		GENERATERECORD{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
					return GENERATOR + ".GenerateRecordAssemblyGenerator";
			}
		},
		SEQUENCEINPUTFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".InputFileSequenceAssemblyGenerator";
			}
		},
		SEQUENCEOUTPUTFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".OutputFileSequenceAssemblyGenerator";
			}
		},
		REMOVEDUPS{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".RemoveDupsAssemblyGenerator";
			}
		},
		UNIONALL{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".UnionAllAssemblyGenerator";
			}
		},
		CLONE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".CloneAssemblyGenerator";
			}
		},
		LIMIT{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".LimitAssemblyGenerator";
			}
		},
		SORT{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".SortAssemblyGenerator";
			}
		},
		FILTER{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".FilterAssemblyGenerator";
			}
		},
		AGGREGATE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".AggregateAssemblyGenerator";
			}
		},
		CUMULATE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".CumulateAssemblyGenerator";
			}
		},
		TRANSFORM{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".TransformAssemblyGenerator";
			}
		},
		JOIN{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".JoinAssemblyGenerator";
			}
		},
		HASHJOIN{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".HashJoinAssemblyGenerator";
			}
		},
		LOOKUP{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".LookupAssemblyGenerator";
			}
		},
		GENERATESEQUENCE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".UniqueSequenceAssemblyGenerator";
			}
		},
		NORMALIZE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".NormalizeAssemblyGenerator";
			}
		},
		PARTITIONBYEXPRESSION{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".PartitionByExpressionAssemblyGenerator";
			}
		},
		DISCARD{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".DiscardAssemblyGenerator";
			}
		},
		RUNPROGRAM{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".RunProgramComponentGenerator";
			}
		},
		HPLSQL{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				return GENERATOR + ".HplSqlComponentGenerator";
			}
		},
		AVROFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("AvroFile")) {
					return GENERATOR + ".InputFileAvroAssemblyGenerator";
				} else {
					return GENERATOR + ".OutputFileAvroAssemblyGenerator";
				}
			}
		},
		HIVETEXTFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("HiveTextFile")) {
					return GENERATOR + ".InputFileHiveTextAssemblyGenerator";
				} else {
					return GENERATOR + ".OutputFileHiveTextAssemblyGenerator";
				}
			}
		},
		PARQUETHIVEFILE{
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("ParquetHiveFile")) {
					return GENERATOR + ".InputFileHiveParquetAssemblyGenerator";
				} else {
					return GENERATOR + ".OutputFileHiveParquetAssemblyGenerator";
				}
			}
		},
		PARQUETFILE {
			@Override
			public String getMapping(TypeBaseComponent typeBaseComponent) {
				if (typeBaseComponent.getClass().getCanonicalName().contains("inputtypes")
						&& typeBaseComponent.getClass().getSimpleName().equals("ParquetFile")) {
					return GENERATOR + ".InputFileParquetAssemblyGenerator";
				} else {
					return GENERATOR + ".OutputFileParquetAssemblyGenerator";
				}
			}
		};

		private static final String GENERATOR = "hydrograph.engine.cascading.assembly.generator";

		abstract String getMapping(TypeBaseComponent typeBaseComponent);
	}

	public String getMappingType(TypeBaseComponent typeBaseComponent) {
		return assemblyType.getMapping(typeBaseComponent);
	}
}
