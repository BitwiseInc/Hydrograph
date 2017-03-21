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

import hydrograph.engine.core.component.entity.InputFileXMLEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.inputtypes.XmlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class InputFileXMLEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileXMLEntityGenerator extends InputComponentGeneratorBase {

    private InputFileXMLEntity inputFileXMLEntity;
    private XmlFile jaxbXMLFile;
    private static Logger LOG = LoggerFactory.getLogger(InputFileXMLEntityGenerator.class);

    public InputFileXMLEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        jaxbXMLFile = (XmlFile) baseComponent;
    }

    @Override
    public void createEntity() {
        inputFileXMLEntity = new InputFileXMLEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file XML entity for component: " + jaxbXMLFile.getId());
        inputFileXMLEntity.setComponentId(jaxbXMLFile.getId());
        inputFileXMLEntity.setBatch(jaxbXMLFile.getBatch());
        inputFileXMLEntity.setComponentName(jaxbXMLFile.getName());
        inputFileXMLEntity.setPath(jaxbXMLFile.getPath().getUri());
        inputFileXMLEntity.setRootTag(jaxbXMLFile.getRootTag().getValue());
        inputFileXMLEntity.setRowTag(jaxbXMLFile.getRowTag().getValue());
        inputFileXMLEntity.setAbsoluteXPath(jaxbXMLFile.getAbsoluteXPath().getValue());
        inputFileXMLEntity.setSafe(jaxbXMLFile.getSafe() != null ? jaxbXMLFile.getSafe().isValue() : false);
        inputFileXMLEntity
                .setStrict(jaxbXMLFile.getStrict() != null ? jaxbXMLFile.getStrict().isValue() : true);
        inputFileXMLEntity.setCharset(
                jaxbXMLFile.getCharset() != null ? jaxbXMLFile.getCharset().getValue().value() : "UTF-8");
        inputFileXMLEntity.setFieldsList(InputEntityUtils.extractInputFields(
                jaxbXMLFile.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        inputFileXMLEntity.setRuntimeProperties(
                InputEntityUtils.extractRuntimeProperties(jaxbXMLFile.getRuntimeProperties()));
        inputFileXMLEntity.setOutSocketList(InputEntityUtils.extractOutSocket(jaxbXMLFile.getOutSocket()));
    }

    @Override
    public InputFileXMLEntity getEntity() {
        return inputFileXMLEntity;
    }
}