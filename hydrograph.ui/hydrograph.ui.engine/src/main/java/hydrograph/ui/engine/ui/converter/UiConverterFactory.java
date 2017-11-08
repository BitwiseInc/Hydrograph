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
 * limitations under the License.
 *******************************************************************************/

 
package hydrograph.ui.engine.ui.converter;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.inputtypes.GenerateRecord;
import hydrograph.engine.jaxb.inputtypes.HiveTextFile;
import hydrograph.engine.jaxb.inputtypes.Mysql;
import hydrograph.engine.jaxb.inputtypes.Oracle;
import hydrograph.engine.jaxb.inputtypes.ParquetHiveFile;
import hydrograph.engine.jaxb.inputtypes.Redshift;
import hydrograph.engine.jaxb.inputtypes.Teradata;
import hydrograph.engine.jaxb.inputtypes.TextFileDelimited;
import hydrograph.engine.jaxb.inputtypes.TextFileFixedWidth;
import hydrograph.engine.jaxb.operationstypes.Aggregate;
import hydrograph.engine.jaxb.operationstypes.Cumulate;
import hydrograph.engine.jaxb.operationstypes.Filter;
import hydrograph.engine.jaxb.operationstypes.GenerateSequence;
import hydrograph.engine.jaxb.operationstypes.Groupcombine;
import hydrograph.engine.jaxb.operationstypes.Join;
import hydrograph.engine.jaxb.operationstypes.Lookup;
import hydrograph.engine.jaxb.operationstypes.Normalize;
import hydrograph.engine.jaxb.operationstypes.PartitionByExpression;
import hydrograph.engine.jaxb.operationstypes.Transform;
import hydrograph.engine.jaxb.outputtypes.Discard;
import hydrograph.engine.jaxb.straightpulltypes.Clone;
import hydrograph.engine.jaxb.straightpulltypes.Dummy;
import hydrograph.engine.jaxb.straightpulltypes.Limit;
import hydrograph.engine.jaxb.straightpulltypes.RemoveDups;
import hydrograph.engine.jaxb.straightpulltypes.Sort;
import hydrograph.engine.jaxb.straightpulltypes.UnionAll;
import hydrograph.ui.engine.ui.converter.impl.AggregateUiConverter;
import hydrograph.ui.engine.ui.converter.impl.CloneUiConverter;
import hydrograph.ui.engine.ui.converter.impl.CommandSubjobUiConverter;
import hydrograph.ui.engine.ui.converter.impl.CumulateUiConverter;
import hydrograph.ui.engine.ui.converter.impl.DiscardUiConverter;
import hydrograph.ui.engine.ui.converter.impl.FTPConverterUi;
import hydrograph.ui.engine.ui.converter.impl.FilterUiConverter;
import hydrograph.ui.engine.ui.converter.impl.GenerateRecordsUiConverter;
import hydrograph.ui.engine.ui.converter.impl.GroupCombineUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputComponentSubjobUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputFileDelimitedUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputFixedWidthUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputHiveParquetUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputHiveTextFileUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputMixedSchemeUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputMysqlUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputOracleUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputParquetUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputRedshiftUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputSparkRedshiftUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputSubjobUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputTeradataUiConverter;
import hydrograph.ui.engine.ui.converter.impl.InputXmlUiConverter;
import hydrograph.ui.engine.ui.converter.impl.JoinComponentUiConverter;
import hydrograph.ui.engine.ui.converter.impl.LimitUiConverter;
import hydrograph.ui.engine.ui.converter.impl.LookupUiConverter;
import hydrograph.ui.engine.ui.converter.impl.NormalizeUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OperationSubJobUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputComponentSubjobUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputDBUpdateUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputFileDelimitedUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputFileExcelUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputFixedWidthUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputHiveParquetUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputHiveTextFileUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputMixedSchemeUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputMysqlUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputOracleUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputParquetUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputRedshiftUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputSparkRedshiftUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputSubjobUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputTeradataUiConverter;
import hydrograph.ui.engine.ui.converter.impl.OutputXMLUiConverter;
import hydrograph.ui.engine.ui.converter.impl.PartitionByExpressionUiConverter;
import hydrograph.ui.engine.ui.converter.impl.RemoveDupsUiConverter;
import hydrograph.ui.engine.ui.converter.impl.RunProgramUiConverter;
import hydrograph.ui.engine.ui.converter.impl.RunSQLUiConverter;
import hydrograph.ui.engine.ui.converter.impl.S3FileTransferUiConverter;
import hydrograph.ui.engine.ui.converter.impl.SFTPUiConverter;
import hydrograph.ui.engine.ui.converter.impl.SortUiConverter;
import hydrograph.ui.engine.ui.converter.impl.TransformComponentUiConverter;
import hydrograph.ui.engine.ui.converter.impl.UnionAllUiConverter;
import hydrograph.ui.engine.ui.converter.impl.UniqueSequenceUiConverter;
import hydrograph.ui.engine.ui.converter.impl.UnknownUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * The class UiConverterFactory Factory class for creating Converter instances for particular component
 * 
 * @author Bitwise
 * 
 */

public class UiConverterFactory {
	public static final UiConverterFactory INSTANCE = new UiConverterFactory();
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(UiConverterFactory.class);

	private UiConverterFactory() {

	}

	/**
	 * Instantiate specific ui-converter.
	 * 
	 * @param typeBaseComponent
	 * 
	 * @param container
	 * 
	 * @return UiConverter, specific ui-converter.
	 */
	public UiConverter getUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		LOGGER.debug("Getting Ui-Converter for component:{}", typeBaseComponent.getClass());
		if ((hydrograph.engine.jaxb.outputtypes.TextFileDelimited.class)
				.isAssignableFrom(typeBaseComponent.getClass())) {
			return new OutputFileDelimitedUiConverter(typeBaseComponent, container);
		}
		if ((TextFileDelimited.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new InputFileDelimitedUiConverter(typeBaseComponent, container);
		}
		if ((hydrograph.engine.jaxb.outputtypes.TextFileFixedWidth.class).isAssignableFrom(typeBaseComponent
				.getClass())) {
			return new OutputFixedWidthUiConverter(typeBaseComponent, container);
		}
		if ((TextFileFixedWidth.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new InputFixedWidthUiConverter(typeBaseComponent, container);
		}
		if ((Clone.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new CloneUiConverter(typeBaseComponent, container);
		}
		if ((UnionAll.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new UnionAllUiConverter(typeBaseComponent, container);
		}
		if ((RemoveDups.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new RemoveDupsUiConverter(typeBaseComponent, container);
		}
		if ((Filter.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new FilterUiConverter(typeBaseComponent, container);
		}
		if ((Aggregate.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new AggregateUiConverter(typeBaseComponent, container);
		}
		if ((Groupcombine.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new GroupCombineUiConverter(typeBaseComponent, container);
		}
		if ((Transform.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new TransformComponentUiConverter(typeBaseComponent, container);
		}
		if ((Cumulate.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new CumulateUiConverter(typeBaseComponent, container);
		}
		if ((Join.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new JoinComponentUiConverter(typeBaseComponent, container);
		}
		if ((Lookup.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new LookupUiConverter(typeBaseComponent, container);
		}
		if ((GenerateRecord.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new GenerateRecordsUiConverter(typeBaseComponent, container);
		}
		if ((GenerateSequence.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new UniqueSequenceUiConverter(typeBaseComponent, container);
		}
		if((Limit.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new LimitUiConverter(typeBaseComponent,container);
		}
		if((Sort.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new SortUiConverter(typeBaseComponent,container);
 		}
		if((Discard.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new DiscardUiConverter(typeBaseComponent,container);
 		}
		if((hydrograph.engine.jaxb.outputtypes.ParquetHiveFile.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputHiveParquetUiConverter(typeBaseComponent,container);
		}
		if((ParquetHiveFile.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputHiveParquetUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.outputtypes.HiveTextFile.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputHiveTextFileUiConverter(typeBaseComponent,container);
		}
		if((HiveTextFile.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputHiveTextFileUiConverter(typeBaseComponent, container);
		}
		if((Oracle.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputOracleUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.outputtypes.Oracle.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputOracleUiConverter(typeBaseComponent, container);
		}
		if((Mysql.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputMysqlUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.outputtypes.Mysql.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputMysqlUiConverter(typeBaseComponent, container);
		}
		if((Teradata.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputTeradataUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.outputtypes.Teradata.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputTeradataUiConverter(typeBaseComponent, container);
		}
		if((Normalize.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new NormalizeUiConverter(typeBaseComponent, container);
		}
		if((Redshift.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputRedshiftUiConverter(typeBaseComponent,container);
 		}
		if((hydrograph.engine.jaxb.outputtypes.Redshift.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputRedshiftUiConverter(typeBaseComponent,container);
 		}

		if((hydrograph.engine.jaxb.inputtypes.ParquetFile.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new InputParquetUiConverter(typeBaseComponent, container);
		}	
		if((hydrograph.engine.jaxb.outputtypes.ParquetFile.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new OutputParquetUiConverter(typeBaseComponent, container);
		}

		if((hydrograph.engine.jaxb.inputtypes.TextFileMixedScheme.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputMixedSchemeUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.outputtypes.TextFileMixedScheme.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputMixedSchemeUiConverter(typeBaseComponent, container);
		}
		if ((hydrograph.engine.jaxb.operationstypes.Subjob.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new OperationSubJobUiConverter(typeBaseComponent, container);
		}
		if ((hydrograph.engine.jaxb.inputtypes.Subjob.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new InputSubjobUiConverter(typeBaseComponent, container);
		}
		if ((hydrograph.engine.jaxb.outputtypes.Subjob.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new OutputSubjobUiConverter(typeBaseComponent, container);
		}
		if ((hydrograph.engine.jaxb.inputtypes.SubjobInput.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new InputComponentSubjobUiConverter(typeBaseComponent, container);
		}
		if ((hydrograph.engine.jaxb.outputtypes.SubjobOutput.class).isAssignableFrom(typeBaseComponent.getClass())) {
			return new OutputComponentSubjobUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.commandtypes.Subjob.class).isAssignableFrom(typeBaseComponent.getClass()))
		{
			return new CommandSubjobUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.commandtypes.RunProgram.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new RunProgramUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.inputtypes.XmlFile.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputXmlUiConverter(typeBaseComponent, container);
		}
		if((PartitionByExpression.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new PartitionByExpressionUiConverter(typeBaseComponent,container);
		}
		if((Dummy.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new UnknownUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.commandtypes.RunSQL.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new RunSQLUiConverter(typeBaseComponent, container);
		}
		
		if((hydrograph.engine.jaxb.outputtypes.JdbcUpdate.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputDBUpdateUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.outputtypes.Sparkredshift.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputSparkRedshiftUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.inputtypes.Sparkredshift.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new InputSparkRedshiftUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.commandtypes.FTP.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new FTPConverterUi(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.commandtypes.SFTP.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new SFTPUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.commandtypes.S3FileTransfer.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new S3FileTransferUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.outputtypes.ExcelFile.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputFileExcelUiConverter(typeBaseComponent, container);
		}
		if((hydrograph.engine.jaxb.outputtypes.XmlFile.class).isAssignableFrom(typeBaseComponent.getClass())){
			return new OutputXMLUiConverter(typeBaseComponent, container);
		}
		return new UnknownUiConverter(typeBaseComponent,container);
	}
}
