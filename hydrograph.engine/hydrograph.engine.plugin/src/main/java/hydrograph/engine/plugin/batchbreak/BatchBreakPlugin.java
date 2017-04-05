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
package hydrograph.engine.plugin.batchbreak;



import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.core.entity.LinkInfo;
import hydrograph.engine.core.flowmanipulation.FlowManipulationContext;
import hydrograph.engine.core.flowmanipulation.ManipulatorListener;
import hydrograph.engine.core.utilities.SocketUtilities;
import hydrograph.engine.jaxb.commontypes.*;
import hydrograph.engine.jaxb.inputtypes.SequenceInputFile;
import hydrograph.engine.jaxb.inputtypes.SequenceInputFile.Path;
import hydrograph.engine.jaxb.outputtypes.SequenceOutputFile;
import org.apache.hadoop.conf.Configuration;

import java.util.*;

/**
 * The Class BatchBreakPlugin.
 *
 * @author Bitwise
 *
 */
public class BatchBreakPlugin implements ManipulatorListener {

    private Map<String, Set<SchemaField>> schemaFieldsMap;
    private List<TypeBaseComponent> jaxbGraph;
    private List<LinkInfo> batchChangeOriginalLinks;
    private List<LinkInfo> batchChangeLinks;
    private static final String DEFAULT_OUT_SOCKET = "out0";
    private static final String DEFAULT_IN_SOCKET = "in0";
    private List<String> tempPathList;

    @Override
    public List<TypeBaseComponent> execute(FlowManipulationContext manipulationContext) {
        tempPathList = new ArrayList<String>();
        jaxbGraph = manipulationContext.getJaxbMainGraph();
        schemaFieldsMap = manipulationContext.getSchemaFieldMap();
        this.batchChangeOriginalLinks = new ArrayList<LinkInfo>();
        this.batchChangeLinks = new ArrayList<LinkInfo>();
        updateBatch();
        populateBatchChangeComponents();
        updateLinksAndComponents(jaxbGraph);
        manipulationContext.setTmpPath(tempPathList);
        return jaxbGraph;
    }

    /**
     * Update subbatch of all the components in the job. Batch attribute should
     * be present on all the components. It throws RuntimeException if batch of
     * source component is greater than batch of target component.
     */
    private void updateBatch() {
        HashSet<String> componentsTraversed = new HashSet<String>();
        for (TypeBaseComponent jaxbComponent : jaxbGraph) {
            updateBatchOfInputFlow(jaxbComponent, componentsTraversed);
        }
    }

    private void updateBatchOfInputFlow(TypeBaseComponent jaxbComponent, HashSet<String> componentsTraversed) {

        if (!componentsTraversed.contains(jaxbComponent.getId())) {
            List<? extends TypeBaseInSocket> inSocketList = SocketUtilities.getInSocketList(jaxbComponent);

            for (TypeBaseInSocket typeBaseInSocket : inSocketList) {
                String batch = jaxbComponent.getBatch();
                int batchLevel = batch.split("\\.").length;

                TypeBaseComponent sourceComponent = getComponent(typeBaseInSocket.getFromComponentId());

                updateBatchOfInputFlow(sourceComponent, componentsTraversed);
                for (int j = 0; j < batchLevel; j++) {
                    if (Integer.parseInt(batch.split("\\.")[j]) < Integer
                            .parseInt(sourceComponent.getBatch().split("\\.")[j])) {
                        // if main batch is less than source component main
                        // batch then throw error
                        // else update the subbatch of this component with the
                        // subbatch of source component
                        if (j > 0) {
                            jaxbComponent.setBatch(sourceComponent.getBatch());
                        } else {
                            throw new RuntimeException(
                                    "Batch of source component cannot be greater than target component. Source component '"
                                            + sourceComponent.getId() + "' has batch " + sourceComponent.getBatch()
                                            + " and target component '" + jaxbComponent.getId() + "' has batch "
                                            + jaxbComponent.getBatch());
                        }
                    }
                }

            }
            componentsTraversed.add(jaxbComponent.getId());
        }
    }

    public TypeBaseComponent getComponentFromComponentId(String componentId) {
        for (TypeBaseComponent component : jaxbGraph) {
            if (component.getId().equals(componentId)) {
                return component;
            }
        }
        throw new GraphTraversalException("Component not present for the component id: " + componentId);
    }

    private String getTempPath(String prefix) {
        String name = prefix + "_" + UUID.randomUUID().toString();
        name = name.replaceAll("\\s+|\\*|\\+|/+", "_");

        Configuration conf = new Configuration();
        String tempDir = conf.get("hadoop.tmp.dir");

        /*String tempDir = conf.get("cascading.tmp.dir");
        if(tempDir == null) {
            tempDir = conf.get("hadoop.tmp.dir");
        }*/

        String tempPath = (new org.apache.hadoop.fs.Path(tempDir, name)).toString();
        tempPathList.add(tempPath);
        return tempPath;
    }

    private void updateLinksAndComponents(List<TypeBaseComponent> jaxbGraph2) {
        int counter = 0;

        for (LinkInfo link2 : batchChangeLinks) {
            TypeBaseComponent targetComponent = this.getComponentFromComponentId(link2.getComponentId());
            String tempPath = getTempPath(targetComponent.getId());
            TypeBaseInSocket inSocket = link2.getInSocket();
            Set<SchemaField> schemaFields = schemaFieldsMap
                    .get(link2.getInSocket().getFromComponentId() + "_" + link2.getInSocket().getFromSocketId());
            String sequenceInputComponentId = targetComponent.getId() + "_" + counter + "_batch_"
                    + targetComponent.getBatch();

            SequenceInputFile jaxbSequenceInputFile = new SequenceInputFile();
            jaxbSequenceInputFile.setId(sequenceInputComponentId);
            jaxbSequenceInputFile.setBatch(targetComponent.getBatch());
            Path inPath = new Path();
            inPath.setUri(tempPath);
            jaxbSequenceInputFile.setPath(inPath);
            TypeInputOutSocket outputSocket = new TypeInputOutSocket();
            outputSocket.setId(DEFAULT_OUT_SOCKET);
            TypeBaseRecord record = new TypeBaseRecord();
            for (SchemaField field : schemaFields) {

                TypeBaseField typeBaseField = new TypeBaseField();
                typeBaseField.setName(field.getFieldName());
                typeBaseField.setType(FieldDataTypes.fromValue(field.getFieldDataType()));
                setFieldScale(field, typeBaseField);
                setFieldPrecision(field, typeBaseField);
                // setFieldScaleType(schemaField, typeBaseField);
                setFieldFormat(field, typeBaseField);
                record.getFieldOrRecordOrIncludeExternalSchema().add(typeBaseField);
            }
            outputSocket.setSchema(record);
            jaxbSequenceInputFile.getOutSocket().add(outputSocket);

            jaxbGraph2.add(jaxbSequenceInputFile);

            inSocket.setFromComponentId(sequenceInputComponentId);
            inSocket.setFromSocketId(DEFAULT_OUT_SOCKET);

            // SocketUtilities.replaceInSocket(targetComponent,
            // inSocket.getId(), newInSocket);
            counter++;

            TypeBaseComponent sourceComponent = this.getComponentFromComponentId(link2.getSourceComponentId());

            TypeBaseOutSocket outSocket = link2.getOutSocket();

            SequenceOutputFile jaxbSequenceOutputFile = new SequenceOutputFile();

            String sequenceOutputComponentId = sourceComponent.getId() + "_" + counter + "_batch_"
                    + sourceComponent.getBatch();
            jaxbSequenceOutputFile.setId(sequenceOutputComponentId);

            jaxbSequenceOutputFile.setBatch(sourceComponent.getBatch());
            hydrograph.engine.jaxb.outputtypes.SequenceOutputFile.Path outPath = new hydrograph.engine.jaxb.outputtypes.SequenceOutputFile.Path();
            outPath.setUri(tempPath);
            jaxbSequenceOutputFile.setPath(outPath);
            TypeOutputInSocket outputInSocket = new TypeOutputInSocket();

            outputInSocket.setFromComponentId(sourceComponent.getId());
            outputInSocket.setFromSocketId(outSocket.getId());
            outputInSocket.setId(DEFAULT_IN_SOCKET);

            TypeBaseRecord record2 = new TypeBaseRecord();
            for (SchemaField field : schemaFields) {

                TypeBaseField typeBaseField = new TypeBaseField();
                typeBaseField.setName(field.getFieldName());
                typeBaseField.setType(FieldDataTypes.fromValue(field.getFieldDataType()));
                setFieldScale(field, typeBaseField);
                setFieldPrecision(field, typeBaseField);
                // setFieldScaleType(schemaField, typeBaseField);
                setFieldFormat(field, typeBaseField);
                record2.getFieldOrRecordOrIncludeExternalSchema().add(typeBaseField);
            }
            outputInSocket.setSchema(record2);
            jaxbSequenceOutputFile.getInSocket().add(outputInSocket);

            jaxbGraph2.add(jaxbSequenceOutputFile);

            counter++;

            addInSocketToOriginalLinks(sourceComponent.getId(), outSocket.getId(), inSocket);
            addOutSocketToOriginalLinks(targetComponent.getId(), inSocket.getId(), outputSocket);

        }
    }

    public static void setFieldScale(SchemaField schemaField, TypeBaseField typeBaseField) {
        typeBaseField.setScale(schemaField.getFieldScale());
    }

    public static void setFieldPrecision(SchemaField schemaField, TypeBaseField typeBaseField) {
        typeBaseField.setPrecision(schemaField.getFieldPrecision());
    }

    public static void setFieldFormat(SchemaField schemaField, TypeBaseField typeBaseField) {
        if (schemaField.getFieldFormat() != null) {
//            if (schemaField.getFieldDataType().toLowerCase().contains("date")) {
//                typeBaseField.setFormat("yyyy-MM-dd HH:mm:ss");
//            } else {
            typeBaseField.setFormat(schemaField.getFieldFormat());
//            }
        }
    }

    private void addInSocketToOriginalLinks(String sourceComponentId, String outSocketId, TypeBaseInSocket inSocket) {
        for (LinkInfo link2 : batchChangeOriginalLinks) {
            if (link2.getOutSocketId().equals(outSocketId) && link2.getSourceComponentId().equals(sourceComponentId)) {
                link2.setInSocket(inSocket);
            }
        }

    }

    private void addOutSocketToOriginalLinks(String targetComponentId, String inSocketPortId,
                                             TypeInputOutSocket outputSocket) {
        for (LinkInfo link2 : batchChangeOriginalLinks) {
            if (link2.getInSocketId().equals(inSocketPortId) && link2.getComponentId().equals(targetComponentId)) {
                link2.setOutSocket(outputSocket);
            }
        }

    }

    private void populateBatchChangeComponents() {

        for (TypeBaseComponent component : jaxbGraph) {
            List<? extends TypeBaseInSocket> inSocketList = SocketUtilities.getInSocketList(component);

            String batch = component.getBatch();

            for (TypeBaseInSocket inSocket : inSocketList) {
                // get the dependent component
                TypeBaseComponent sourceComponent = getComponent(inSocket.getFromComponentId());
                int result = compareBatch(sourceComponent.getBatch(),batch);
                if (result > 0) {
                    throw new GraphTraversalException(
                            "Batch of source component cannot be greator then target component. Source component "
                                    + sourceComponent.getId() + " has batch " + sourceComponent.getBatch()
                                    + " and target component " + component.getId() + " has batch " + batch);
                }

                TypeBaseOutSocket outSocket = SocketUtilities.getOutSocket(sourceComponent, inSocket.getFromSocketId());

                LinkInfo link = new LinkInfo(component.getId(), inSocket.getId(), inSocket, sourceComponent.getId(),
                        outSocket.getId(), outSocket);

                if (result < 0) {
                    batchChangeLinks.add(link);
                }
                batchChangeOriginalLinks.add(link);
            }
        }
    }

    private int compareBatch(String sourceComponentBatch,String targetComponentBatch){
        int result=0;
        if(sourceComponentBatch.contains(".")){

            for(int i = 0; i< sourceComponentBatch.split("\\.").length; i++) {
                result=compareBatch(sourceComponentBatch.split("\\.")[i], targetComponentBatch.split("\\.")[i]);
                if(result!=0)
                    break;
            }
            return result;
        }
        else{
            return Integer.compare(Integer.parseInt(sourceComponentBatch),Integer.parseInt(targetComponentBatch));
        }

    }

    private TypeBaseComponent getComponent(String componentID) {
        for (TypeBaseComponent component : jaxbGraph) {
            if (component.getId().equals(componentID))
                return component;
        }
        throw new GraphTraversalException("Component not found with id '" + componentID + "'");
    }

    private class GraphTraversalException extends RuntimeException {

        private static final long serialVersionUID = -2396594973435552339L;

        public GraphTraversalException(String msg) {
            super(msg);
        }

        public GraphTraversalException(Throwable e) {
            super(e);
        }

        public GraphTraversalException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}