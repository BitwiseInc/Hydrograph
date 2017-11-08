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

import hydrograph.engine.core.component.entity.OutputFileXMLEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TrueFalse;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.XmlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class OutputFileXMLEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputFileXMLEntityGenerator extends OutputComponentGeneratorBase {

    private OutputFileXMLEntity outputFileXMLEntity;
    private XmlFile jaxbOutputFileXML;
    private static Logger LOG = LoggerFactory.getLogger(OutputFileXMLEntityGenerator.class);

    public OutputFileXMLEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        jaxbOutputFileXML = (XmlFile) baseComponent;
    }

    @Override
    public void createEntity() {
        outputFileXMLEntity = new OutputFileXMLEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing output file XML entity for component: " + jaxbOutputFileXML.getId());
        outputFileXMLEntity.setComponentId(jaxbOutputFileXML.getId());
        outputFileXMLEntity.setBatch(jaxbOutputFileXML.getBatch());
        outputFileXMLEntity.setComponentName(jaxbOutputFileXML.getName());
        outputFileXMLEntity.setPath(jaxbOutputFileXML.getPath().getUri());
        outputFileXMLEntity.setRootTag(jaxbOutputFileXML.getRootTag().getValue());
        outputFileXMLEntity.setRowTag(jaxbOutputFileXML.getRowTag().getValue());
        outputFileXMLEntity.setAbsoluteXPath(jaxbOutputFileXML.getAbsoluteXPath().getValue());
        outputFileXMLEntity.setSafe(
                jaxbOutputFileXML.getSafe() != null ? jaxbOutputFileXML.getSafe().isValue() : false);
        outputFileXMLEntity.setCharset(jaxbOutputFileXML.getCharset() != null
                ? jaxbOutputFileXML.getCharset().getValue().value() : "UTF-8");
        outputFileXMLEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
                jaxbOutputFileXML.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        outputFileXMLEntity.setRuntimeProperties(
                OutputEntityUtils.extractRuntimeProperties(jaxbOutputFileXML.getRuntimeProperties()));
        outputFileXMLEntity.setStrict(
                jaxbOutputFileXML.getStrict() != null ? jaxbOutputFileXML.getStrict().isValue() : true);
        if (jaxbOutputFileXML.getOverWrite() != null
                && (TrueFalse.FALSE).equals(jaxbOutputFileXML.getOverWrite().getValue()))
            outputFileXMLEntity.setOverWrite(false);
        else
            outputFileXMLEntity.setOverWrite(true);
    }

    @Override
    public OutputFileXMLEntity getEntity() {
        return outputFileXMLEntity;
    }
}