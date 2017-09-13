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
 ******************************************************************************/

package hydrograph.engine.core.component.generator;

import hydrograph.engine.core.component.entity.OutputFileExcelEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.core.component.utils.FieldFormatUtils;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.ExcelFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class OutputFileExcelEntityGenerator.
 *
 * @author Bitwise
 */
public class OutputFileExcelEntityGenerator extends OutputComponentGeneratorBase {

    private static Logger LOG = LoggerFactory.getLogger(OutputFileExcelEntityGenerator.class);
    private OutputFileExcelEntity outputFileExcelEntity;
    private ExcelFile jaxbOutputFileExcel;

    public OutputFileExcelEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        jaxbOutputFileExcel = (ExcelFile) baseComponent;
    }

    @Override
    public void createEntity() {
        outputFileExcelEntity = new OutputFileExcelEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing output file delimited entity for component: " + jaxbOutputFileExcel.getId());
        outputFileExcelEntity.setComponentId(jaxbOutputFileExcel.getId());
        outputFileExcelEntity.setBatch(jaxbOutputFileExcel.getBatch());
        outputFileExcelEntity.setComponentName(jaxbOutputFileExcel.getName());
        outputFileExcelEntity.setPath(jaxbOutputFileExcel.getPath().getUri());
        outputFileExcelEntity.setFileExtension(jaxbOutputFileExcel.getFileExtension() != null ? jaxbOutputFileExcel.getFileExtension().getValue().value() : "XLSX");
        outputFileExcelEntity.setWorksheetName(jaxbOutputFileExcel.getWorksheetName()!=null ? jaxbOutputFileExcel.getWorksheetName().getName(): "HydrographSheet");
        outputFileExcelEntity.setStripLeadingQuote(jaxbOutputFileExcel.getStripLeadingQuote() !=null ? jaxbOutputFileExcel.getStripLeadingQuote().isValue():true);
        outputFileExcelEntity.setAutoColumnSize(jaxbOutputFileExcel.getAutoColumnSize() != null ? jaxbOutputFileExcel.getAutoColumnSize().isValue() : true);
        outputFileExcelEntity.setAbortOnError(jaxbOutputFileExcel.getAbortOnError()!= null ?jaxbOutputFileExcel.getAbortOnError().isValue(): true);

        outputFileExcelEntity.setWriteMode(jaxbOutputFileExcel.getWriteMode() != null ? jaxbOutputFileExcel.getWriteMode().getValue().value() : "Overwrite");
        outputFileExcelEntity.setCharset(jaxbOutputFileExcel.getCharset() != null ? jaxbOutputFileExcel.getCharset().getValue().value() : "UTF-8");
        outputFileExcelEntity.setFieldsList(OutputEntityUtils.extractOutputFields(jaxbOutputFileExcel.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        outputFileExcelEntity.setRuntimeProperties(OutputEntityUtils.extractRuntimeProperties(jaxbOutputFileExcel.getRuntimeProperties()));

        outputFileExcelEntity.setHeaderFormats(FieldFormatUtils.getHeaderFormatList(jaxbOutputFileExcel.getCellFormat().getHeader().getField()));
        outputFileExcelEntity.setDataFormats(FieldFormatUtils.getHeaderFormatList(jaxbOutputFileExcel.getCellFormat().getData().getField()));

    }

    @Override
    public OutputFileExcelEntity getEntity() {
        return outputFileExcelEntity;
    }
}
