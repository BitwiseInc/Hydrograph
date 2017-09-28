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
package hydrograph.engine.core.component.entity.utils;

import hydrograph.engine.core.component.entity.elements.*;
import hydrograph.engine.core.component.utils.OperationOutputField;
import hydrograph.engine.jaxb.commontypes.*;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.join.TypeKeyFields;
import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.*;

/**
 * The Class OperationEntityUtils.
 *
 * @author Bitwise
 *
 */
public class OperationEntityUtils implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7977314904706287826L;

    private OperationEntityUtils() {
    }

    /**
     * Extracts the operation list from the {@link Operation} object of type
     * {@link TypeTransformOperation}, passed as a parameter
     *
     * @param jaxbOperationList the list of {@link TypeTransformOperation} objects which
     *                          contain information of operations for the component
     * @return a list of {@link Operation} objects. Each object in the list
     * contains information of operation class, input fields, output
     * fields and properties for one operation
     */
    public static List<Operation> extractOperations(List<JAXBElement<?>> jaxbOperationList) {

        List<Operation> operationList = new ArrayList<Operation>();

        for (JAXBElement typeTransformOperation : jaxbOperationList) {
            Operation operation = new Operation();
            if (typeTransformOperation.getValue() instanceof TypeTransformOperation) {
                operation.setOperationId(((TypeTransformOperation) typeTransformOperation.getValue()).getId());
                operation.setOperationInputFields(extractOperationInputFields(
                        ((TypeTransformOperation) typeTransformOperation.getValue()).getInputFields()));
                operation.setOperationOutputFields(extractOperationOutputFields(
                        ((TypeTransformOperation) typeTransformOperation.getValue()).getOutputFields()));
                operation.setOperationFields(extractOperationFields(
                        ((TypeTransformOperation) typeTransformOperation.getValue()).getOutputFields()));
                operation.setOperationClass(((TypeTransformOperation) typeTransformOperation.getValue()).getClazz());
                operation.setOperationProperties(
                        extractOperationProperties(((TypeTransformOperation) typeTransformOperation.getValue()).getProperties()));
                operation.setExpressionPresent(false);
                operationList.add(operation);
            } else {
                operation.setOperationId(((TypeTransformExpression) typeTransformOperation.getValue()).getId());
                operation.setOperationInputFields(extractOperationInputFields(
                        ((TypeTransformExpression) typeTransformOperation.getValue()).getInputFields()));
                operation.setOperationOutputFields(extractExpressionOutputFields(
                        ((TypeTransformExpression) typeTransformOperation.getValue()).getOutputFields()));
                operation.setOperationFields(extractOperationFields(
                        ((TypeTransformExpression) typeTransformOperation.getValue()).getOutputFields()));
                operation.setExpression(addSemiColonIfNotPresent(((TypeTransformExpression) typeTransformOperation.getValue()).getExpr()));
                if (((TypeTransformExpression) typeTransformOperation.getValue()).getMergeExpr() != null)
                    operation.setMergeExpression(addSemiColonIfNotPresent(((TypeTransformExpression) typeTransformOperation.getValue()).getMergeExpr()));
                operation.setAccumulatorInitialValue(addQuotes(((TypeTransformExpression) typeTransformOperation.getValue()).getAccumulatorInitalValue()));
                operation.setOperationClass(null);
                operation.setExpressionPresent(true);
                operation.setOperationProperties(
                        extractOperationProperties(((TypeTransformExpression) typeTransformOperation.getValue()).getProperties()));
                operationList.add(operation);
            }
        }
        return operationList;
    }

    private static String addQuotes(String accumulatorInitalValue) {
        if (accumulatorInitalValue != null
                && accumulatorInitalValue.matches("^[_a-zA-Z].+")
                && !accumulatorInitalValue.trim().startsWith("\"")
                && !accumulatorInitalValue.endsWith("\"")) {
            accumulatorInitalValue = "\"" + accumulatorInitalValue + "\"";
        }
        return accumulatorInitalValue;
    }

    private static String addSemiColonIfNotPresent(String expr) {
        if (expr.trim().endsWith(";")) {
            return expr.substring(0, expr.length() - 1);
        }
        return expr;
//		return expr;
    }

    /**
     * Extracts the operation list from the {@link Operation} object of type
     * {@link TypeTransformOperation}, passed as a parameter
     *
     * @param jaxbOperationList
     *            the list of {@link TypeTransformOperation} objects which
     *            contain information of operations for the component
     * @return a list of {@link Operation} objects. Each object in the list
     *         contains information of operation class, input fields, output
     *         fields and properties for one operation
     */
//	public static List<Expression> extractExpression(List<Object> jaxbOperationList) {
//
//		List<Expression> operationList = new ArrayList<Expression>();
//
//		for (Object typeTransformOperation : jaxbOperationList) {
//			if (typeTransformOperation instanceof TypeTransformExpression) {
//				Expression operation = new Expression();
//				operation.setOperationId(((TypeTransformExpression) typeTransformOperation).getId());
//				operation.setOperationInputFields(extractOperationInputFields(
//						((TypeTransformExpression) typeTransformOperation).getInputFields()));
//				operation.setOperationOutputFields(extractExpressionOutputFields(
//						((TypeTransformExpression) typeTransformOperation).getOutputFields()));
//				operation.setExpression(((TypeTransformExpression) typeTransformOperation).getExpr());
//				operation.setOperationProperties(
//						extractOperationProperties(((TypeTransformExpression) typeTransformOperation).getProperties()));
//				operationList.add(operation);
//			}
//		}
//		return operationList;
//	}

    /**
     * Extracts the {@link Properties} object from the {@link TypeProperties}
     * object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code typeProperties} parameter
     * is null
     *
     * @param typeProperties the {@link TypeProperties} object which contain information of
     *                       operation properties for the component
     * @return a {@link Properties} object
     */
    private static Properties extractOperationProperties(TypeProperties typeProperties) {
        Properties properties = new Properties();
        if (typeProperties == null) {
            return null;
        } else if (typeProperties.getProperty() == null) {
            return null;
        }

        // Fetch all the properties passed to operation
        for (Property eachProperty : typeProperties.getProperty()) {
            properties.setProperty(eachProperty.getName(), eachProperty.getValue());
        }

        return properties;
    }

    /**
     * Extracts the operation input fields from the
     * {@link TypeOperationInputFields} object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code typeOperationInputFields}
     * parameter is null
     *
     * @param typeOperationInputFields the object of {@link TypeOperationInputFields} which contain
     *                                 information of operation input fields for the component
     * @return a string array containing the input fields for an operation
     */
    private static String[] extractOperationInputFields(TypeOperationInputFields typeOperationInputFields) {
        if (typeOperationInputFields == null) {
            return null;
        } else if (typeOperationInputFields.getField() == null) {
            return null;
        }
        List<TypeInputField> typeInputFieldList = typeOperationInputFields.getField();
        String[] inputFields = new String[typeInputFieldList.size()];
        int i = 0;
        for (TypeInputField typeInputField : typeInputFieldList) {
            inputFields[i++] = typeInputField.getName();
        }
        return inputFields;
    }

    /**
     * Extracts the operation output fields from the
     * {@link TypeOperationOutputFields} object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code typeOperationOutputFields}
     * parameter is null
     *
     * @param typeOperationOutputFields the object of {@link TypeOperationOutputFields} which contain
     *                                  information of operation output fields for the component
     * @return a string array containing the output fields for an operation
     */
    private static String[] extractOperationOutputFields(TypeOperationOutputFields typeOperationOutputFields) {

        if (typeOperationOutputFields == null) {
            return null;
        } else if (typeOperationOutputFields.getField() == null) {
            return null;
        }
        List<TypeBaseField> typeOutputFieldList = typeOperationOutputFields.getField();
        String[] outputFields = new String[typeOutputFieldList.size()];
        int i = 0;
        for (TypeBaseField typeOutputField : typeOutputFieldList) {
            outputFields[i++] = typeOutputField.getName();
        }
        return outputFields;

    }


    /**
     * Extracts the operation output fields from the
     * {@link TypeOperationOutputFields} object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code typeOperationOutputFields}
     * parameter is null
     *
     * @param typeOperationOutputFields the object of {@link TypeOperationOutputFields} which contain
     *                                  information of operation output fields for the component
     * @return a array OperationOutputField containing  the output fields for an operation
     */
    private static OperationOutputField[] extractOperationFields(TypeOperationOutputFields typeOperationOutputFields) {

        if (typeOperationOutputFields == null) {
            return null;
        } else if (typeOperationOutputFields.getField() == null) {
            return null;
        }
        List<TypeBaseField> typeOutputFieldList = typeOperationOutputFields.getField();
        OperationOutputField[] outputFields = new OperationOutputField[typeOutputFieldList.size()];
        int i = 0;
        for (TypeBaseField outputField : typeOutputFieldList) {
            outputFields[i++] = new OperationOutputField(outputField.getName(), outputField.getType().value(), outputField.getFormat() != null ? outputField.getFormat() : "yyyy-MM-dd", outputField.getScale() != null ? outputField.getScale() : 38, outputField.getPrecision() != null ? outputField.getPrecision() : 38);
        }
        return outputFields;

    }

    /**
     * Extracts the operation output fields from the
     * {@link TypeOperationOutputFields} object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code typeOperationOutputFields}
     * parameter is null
     *
     * @param typeOperationOutputFields the object of {@link TypeOperationOutputFields} which contain
     *                                  information of operation output fields for the component
     * @return a array OperationOutputField containing  the output fields for an operation
     */
    private static OperationOutputField[] extractOperationFields(TypeExpressionOutputFields typeOperationOutputFields) {

        if (typeOperationOutputFields == null) {
            return null;
        } else if (typeOperationOutputFields.getField() == null) {
            return null;
        }
        List<TypeBaseField> typeOutputFieldList = new ArrayList<TypeBaseField>();
        typeOutputFieldList.add(typeOperationOutputFields.getField());
        OperationOutputField[] outputFields = new OperationOutputField[typeOutputFieldList.size()];
        int i = 0;
        for (TypeBaseField outputField : typeOutputFieldList) {
            outputFields[i++] = new OperationOutputField(outputField.getName(), outputField.getType().value(), outputField.getFormat() != null ? outputField.getFormat() : "yyyy-MM-dd", outputField.getScale() != null ? outputField.getScale() : 38, outputField.getPrecision() != null ? outputField.getPrecision() : 38);
        }
        return outputFields;

    }

    /**
     * Extracts the operation output fields from the
     * {@link TypeExpressionOutputFields} object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code TypeExpressionOutputFields}
     * parameter is null
     *
     * @param typeOperationOutputFields the object of {@link TypeExpressionOutputFields} which contain
     *                                  information of operation output fields for the component
     * @return a string array containing the output fields for an operation
     */
    private static String[] extractExpressionOutputFields(TypeExpressionOutputFields typeOperationOutputFields) {

        if (typeOperationOutputFields == null) {
            return null;
        } else if (typeOperationOutputFields.getField() == null) {
            return null;
        }
        return new String[]{typeOperationOutputFields.getField().getName()};

    }

    /**
     * Extracts the operation fields of type {@link TypeOperationField} from the
     * list of output socket of type {@link TypeOperationsOutSocket}, passed as
     * a parameter
     * <p>
     * The method returns {@code null} if the {@code operationList} parameter is
     * null
     * <p>
     * <b>Note</b>: This method returns the operation fields from the out
     * socket. If looking for the operation output fields provided in the
     * operation, look for {@link #extractOperations(List)} method
     *
     * @param outSocket the {@link TypeOperationsOutSocket} object which contains the
     *                  operation field information
     * @return a list of string array containing names of all the operation
     * fields for the out sockets. Each array in the list corresponds to
     * the operation fields for one out socket
     * @throws NullPointerException when {@code outSocket} is null
     */
    public static List<String[]> extractOperationFieldsListFromSocket(List<TypeOperationsOutSocket> outSocket)
            throws NullPointerException {

        if (outSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<String[]> operationFieldsList = new ArrayList<String[]>();

        for (TypeOperationsOutSocket socket : outSocket) {
            ArrayList<String> operationFields = new ArrayList<String>();
            for (Object field : socket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
                if (field instanceof TypeOperationField) {
                    TypeOperationField operationField = (TypeOperationField) field;
                    operationFields.add(operationField.getName());
                }
            }
            operationFieldsList.add(operationFields.toArray(new String[operationFields.size()]));
        }

        return operationFieldsList;
    }

    /**
     * Extracts the pass through fields of type {@link TypeInputField} from the
     * list of output socket of type {@link TypeOperationsOutSocket}, passed as
     * a parameter
     *
     * @param outSocket the {@link TypeOperationsOutSocket} object which contains the
     *                  pass through field information
     * @return a list of string array containing names of all the pass through
     * fields for the out sockets. Each array in the list corresponds to
     * the pass through fields for one out socket
     * @throws NullPointerException when {@code outSocket} is null
     */
    public static List<String[]> extractPassThroughFieldsListFromOutSocket(List<TypeOperationsOutSocket> outSocket)
            throws NullPointerException {

        if (outSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<String[]> passThroughFieldsList = new ArrayList<String[]>();

        for (TypeOperationsOutSocket socket : outSocket) {
            ArrayList<String> passThroughFields = new ArrayList<String>();
            for (Object field : socket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
                if (field instanceof TypeInputField) {
                    TypeInputField passThroughField = (TypeInputField) field;
                    passThroughFields.add(passThroughField.getName());
                }
            }
            passThroughFieldsList.add(passThroughFields.toArray(new String[passThroughFields.size()]));
        }

        return passThroughFieldsList;
    }

    /**
     * Extracts the list of map fields of type {@link TypeMapField} from the
     * output socket object of type {@link TypeOperationsOutSocket}, passed as a
     * parameter
     *
     * @param outSocket list of {@link TypeOperationsOutSocket} objects which contain
     *                  the map field information
     * @return list of map {@link Map}<{@link String}, {@link String}>
     * containing all the map field names for the out socket. The map
     * fields are stored as Map&lt;SourceFieldName, TargetFieldName&gt;.
     * Each map in the list corresponds to the map fields for one out
     * socket
     * @throws NullPointerException when {@code outSocket} is null
     */
    public static List<Map<String, String>> extractMapFieldsListFromOutSocket(List<TypeOperationsOutSocket> outSocket)
            throws NullPointerException {

        if (outSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<Map<String, String>> mapFieldsList = new ArrayList<Map<String, String>>();

        for (TypeOperationsOutSocket socket : outSocket) {
            Map<String, String> mapFields = new HashMap<String, String>();
            for (Object field : socket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
                if (field instanceof TypeMapField) {
                    TypeMapField mapField = (TypeMapField) field;
                    mapFields.put(mapField.getSourceName(), mapField.getName());
                }
            }
            mapFieldsList.add(mapFields);
        }

        return mapFieldsList;
    }

    /**
     * Extracts the socket id's for all the sockets from the output socket
     * object of type {@link TypeOperationsOutSocket}, passed as a parameter
     *
     * @param outSocket list of {@link TypeOperationsOutSocket} objects which contain
     *                  the socket id information
     * @return string array containing the socket id's for all the sockets
     * @throws NullPointerException when {@code outSocket} is null
     */
    public static String[] extractSocketIdFromOutSocket(List<TypeOperationsOutSocket> outSocket)
            throws NullPointerException {

        if (outSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<String> socketId = new ArrayList<String>();
        for (TypeOperationsOutSocket socket : outSocket) {
            socketId.add(socket.getId());
        }

        return socketId.toArray(new String[socketId.size()]);
    }

    /**
     * Extracts the socket type for all the sockets from the output socket
     * object of type {@link TypeOperationsOutSocket}, passed as a parameter
     *
     * @param outSocket list of {@link TypeOperationsOutSocket} objects which contain
     *                  the socket type information
     * @return string array containing the socket type for all the sockets
     * @throws NullPointerException when {@code outSocket} is null
     */
    public static String[] extractSocketTypeFromOutSocket(List<TypeOperationsOutSocket> outSocket)
            throws NullPointerException {

        if (outSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<String> socketType = new ArrayList<String>();
        for (TypeOperationsOutSocket socket : outSocket) {
            socketType.add(socket.getType());
        }

        return socketType.toArray(new String[socketType.size()]);
    }

    /**
     * Extracts the {@link Properties} object from the {@link TypeProperties}
     * object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code typeProperties} parameter
     * is null
     *
     * @param typeProperties the {@link TypeProperties} object which contain information of
     *                       runtime properties for the component
     * @return a {@link Properties} object
     */
    public static Properties extractRuntimeProperties(TypeProperties typeProperties) {

        if (typeProperties == null) {
            return null;
        } else if (typeProperties.getProperty() == null) {
            return null;
        }
        Properties properties = new Properties();
        // Fetch all the properties passed to operation
        for (Property eachProperty : typeProperties.getProperty()) {
            properties.setProperty(eachProperty.getName(), eachProperty.getValue());
        }

        return properties;
    }

    /**
     * Extracts the {@link List&lt;PassThroughField&gt;} object from the
     * {@link TypeOperationsOutSocket} object passed as a parameter
     * <p>
     *
     * @param typeOperationsOutSocket the {@link TypeOperationsOutSocket} object which contain
     *                                information of fields like pass-through fields, map fields,
     *                                etc.
     * @return a {@link List&lt;PassThroughField&gt;} object
     * @throws NullPointerException when {@code outSocket} is null
     */
    private static List<PassThroughField> extractPassThroughFieldsListFromOutSockets(
            TypeOperationsOutSocket typeOperationsOutSocket) throws NullPointerException {
        if (typeOperationsOutSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }
        List<PassThroughField> passThroughFieldsList = new ArrayList<PassThroughField>();
        for (Object field : typeOperationsOutSocket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
            if (field instanceof TypeInputField) {
                TypeInputField passThroughField = (TypeInputField) field;
                PassThroughField passThroughFieldsObj = new PassThroughField(passThroughField.getName(),
                        passThroughField.getInSocketId());
                passThroughFieldsList.add(passThroughFieldsObj);
            }
        }
        return passThroughFieldsList;
    }

    /**
     * Extracts the {@link List&lt;OperationField&gt;} object from the
     * {@link TypeOperationsOutSocket} object passed as a parameter
     * <p>
     *
     * @param typeOperationsOutSocket the {@link TypeOperationsOutSocket} object which contain
     *                                information of fields like pass-through fields, map fields,
     *                                etc.
     * @return a {@link List&lt;OperationField&gt;} object
     * @throws NullPointerException when {@code outSocket} is null
     */
    private static List<OperationField> extractOperationFieldFromOutSockets(
            TypeOperationsOutSocket typeOperationsOutSocket) throws NullPointerException {
        if (typeOperationsOutSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }
        List<OperationField> operationFieldList = new ArrayList<OperationField>();
        for (Object field : typeOperationsOutSocket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
            if (field instanceof TypeOperationField) {
                TypeOperationField typeOperationField = (TypeOperationField) field;
                operationFieldList
                        .add(new OperationField(typeOperationField.getName(), typeOperationField.getOperationId()));
            } else if (field instanceof TypeExpressionField) {
                TypeExpressionField typeOperationField = (TypeExpressionField) field;
                operationFieldList
                        .add(new OperationField(typeOperationField.getName(), typeOperationField.getExpressionId()));
            }
        }
        return operationFieldList;
    }

    /**
     * Extracts the {@link List&lt;MapField&gt;} object from the
     * {@link TypeOperationsOutSocket} object passed as a parameter
     * <p>
     *
     * @param typeOperationsOutSocket the {@link TypeOperationsOutSocket} object which contain
     *                                information of fields like pass-through fields, map fields,
     *                                etc.
     * @return a {@link List&lt;MapField&gt;} object
     * @throws NullPointerException when {@code outSocket} is null
     */
    private static List<MapField> extractMapFieldsListFromOutSockets(TypeOperationsOutSocket typeOperationsOutSocket)
            throws NullPointerException {

        if (typeOperationsOutSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }
        List<MapField> mapFieldsList = new ArrayList<MapField>();
        for (Object field : typeOperationsOutSocket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
            if (field instanceof TypeMapField) {
                TypeMapField typeMapField = (TypeMapField) field;
                MapField mapField = new MapField(typeMapField.getSourceName(), typeMapField.getName(),
                        typeMapField.getInSocketId());
                mapFieldsList.add(mapField);
            }
        }
        return mapFieldsList;
    }

    public static List<JoinKeyFields> extractKeyFieldsListFromOutSockets(List<TypeKeyFields> list)
            throws NullPointerException {

        if (list == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
        String[] fieldNames;

        for (TypeKeyFields keyField : list) {
            int i = 0;
            fieldNames = new String[keyField.getField().size()];
            for (TypeFieldName keyFields : keyField.getField()) {
                fieldNames[i] = keyFields.getName();
                i++;
            }

            JoinKeyFields mapFields = new JoinKeyFields(keyField.getInSocketId(), keyField.isRecordRequired(),
                    fieldNames);
            keyFieldsList.add(mapFields);
        }

        return keyFieldsList;
    }

    public static List<OutSocket> extractOutSocketList(List<TypeOperationsOutSocket> outSocket) {

        if (outSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<OutSocket> outSocketList = new ArrayList<OutSocket>();

        for (TypeOperationsOutSocket socket : outSocket) {
            OutSocket outSockets = new OutSocket(socket.getId());
            if (socket.getType() != null)
                outSockets.setSocketType(socket.getType());
            outSockets.setMapFieldsList(extractMapFieldsListFromOutSockets(socket));
            outSockets.setPassThroughFieldsList(extractPassThroughFieldsListFromOutSockets(socket));
            outSockets.setOperationFieldList(extractOperationFieldFromOutSockets(socket));
            outSocketList.add(outSockets);
            if (socket.getCopyOfInsocket() != null)
                outSockets.setCopyOfInSocketId(socket.getCopyOfInsocket().getInSocketId());
        }

        return outSocketList;

    }


    public static List<InSocket> extractInSocketList(List<TypeBaseInSocket> inSocket) {

        if (inSocket == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<InSocket> inSocketList = new ArrayList<InSocket>();

        for (TypeBaseInSocket socket : inSocket) {

            InSocket inSock = new InSocket(socket.getFromComponentId(), socket.getFromSocketId(), socket.getId());

            inSock.setInSocketType(socket.getType() != null ? socket.getType() : "");
            inSock.setFromSocketType(socket.getFromSocketType() != null ? socket.getFromSocketType() : "");

            inSocketList.add(inSock);

        }

        return inSocketList;

    }

	/*
     * public static Map<String, InSocket>
	 * extractInSocketMap(List<TypeBaseInSocket> inSocketList) {
	 * 
	 * if (inSocketList == null) { throw new NullPointerException(
	 * "In socket cannot be null"); } Map<String, InSocket> inSocketMap = new
	 * HashMap<String, InSocket>(); for (TypeBaseInSocket inSocket :
	 * inSocketList) { InSocket socket = new
	 * InSocket(inSocket.getFromComponentId(), inSocket.getFromSocketId(),
	 * inSocket.getId());
	 * 
	 * if (inSocket.getFromSocketType() != null)
	 * socket.setFromSocketType(inSocket.getFromSocketType()); if
	 * (inSocket.getType() != null) socket.setInSocketType(inSocket.getType());
	 * inSocketMap.put(inSocket.getId(), socket); }
	 * 
	 * return inSocketMap; }
	 */

    public static List<JoinKeyFields> extractKeyFieldsListFromOutSocketsForLookup(
            List<hydrograph.engine.jaxb.lookup.TypeKeyFields> list) {
        if (list == null) {
            throw new NullPointerException("Out socket cannot be null");
        }

        List<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
        String[] fieldNames;

        for (hydrograph.engine.jaxb.lookup.TypeKeyFields keyField : list) {
            int i = 0;
            fieldNames = new String[keyField.getField().size()];
            for (TypeFieldName keyFields : keyField.getField()) {
                fieldNames[i] = keyFields.getName();
                i++;
            }

            JoinKeyFields mapFields = new JoinKeyFields(keyField.getInSocketId(), true, fieldNames);
            keyFieldsList.add(mapFields);
        }

        return keyFieldsList;
    }

    /**
     * Extracts the key fields from the
     * {@link hydrograph.engine.jaxb.aggregate.TypeSecondaryKeyFields} object
     * passed as a parameter
     * <p>
     * TypeSecondaryKeyFields The method returns {@code null} if the
     * {@code typeSecondaryKeyFields} parameter is null
     *
     * @param typeSecondaryKeyFields the object of
     *                               {@link hydrograph.engine.jaxb.aggregate.TypeSecondaryKeyFields}
     *                               which contain information of secondary key fields for the
     *                               component
     * @return an array of {@link KeyField} containing the secondary key fields
     * for an operation
     */
    public static KeyField[] extractSecondaryKeyFields(
            hydrograph.engine.jaxb.aggregate.TypeSecondaryKeyFields typeSecondaryKeyFields) {
        if (typeSecondaryKeyFields == null || typeSecondaryKeyFields.getField() == null) {
            return null;
        }
        KeyField[] keyFields = new KeyField[typeSecondaryKeyFields.getField().size()];
        int i = 0;
        for (hydrograph.engine.jaxb.aggregate.TypeSecondayKeyFieldsAttributes eachTypeFieldName : typeSecondaryKeyFields
                .getField()) {
            KeyField eachKeyField = new KeyField();
            eachKeyField.setName(eachTypeFieldName.getName());
            eachKeyField.setSortOrder(eachTypeFieldName.getOrder().value());
            keyFields[i] = eachKeyField;
            i++;
        }
        return keyFields;
    }

    /**
     * Extracts the key fields from the
     * {@link hydrograph.engine.jaxb.aggregate.TypePrimaryKeyFields} object
     * passed as a parameter
     * <p>
     * TypePrimaryKeyFields The method returns {@code null} if the
     * {@code typePrimaryKeyFields} parameter is null
     *
     * @param typePrimaryKeyFields the object of
     *                             {@link hydrograph.engine.jaxb.aggregate.TypePrimaryKeyFields}
     *                             which contain information of key fields for the component
     * @return an array of {@link KeyField} containing the key fields for an
     * operation
     */
    public static KeyField[] extractKeyFields(
            hydrograph.engine.jaxb.aggregate.TypePrimaryKeyFields typePrimaryKeyFields) {
        if (typePrimaryKeyFields == null) {
            return null;
        } else if (typePrimaryKeyFields.getNone() != null) {
            return null;
        } else if (typePrimaryKeyFields.getField() == null) {
            return null;
        }
        KeyField[] keyFields = new KeyField[typePrimaryKeyFields.getField().size()];
        int i = 0;
        for (TypeFieldName eachTypeFieldName : typePrimaryKeyFields.getField()) {
            KeyField eachKeyField = new KeyField();
            eachKeyField.setName(eachTypeFieldName.getName());
            // eachKeyField.setSortOrder(eachTypeFieldName.getOrder().value());
            keyFields[i] = eachKeyField;
            i++;
        }
        return keyFields;
    }

    /**
     * Extracts the key fields from the
     * {@link hydrograph.engine.jaxb.groupcombine.TypePrimaryKeyFields} object
     * passed as a parameter
     * <p>
     * TypePrimaryKeyFields The method returns {@code null} if the
     * {@code typePrimaryKeyFields} parameter is null
     *
     * @param typePrimaryKeyFields the object of
     *                             {@link hydrograph.engine.jaxb.groupcombine.TypePrimaryKeyFields}
     *                             which contain information of key fields for the component
     * @return an array of {@link KeyField} containing the key fields for an
     * operation
     */
    public static KeyField[] extractKeyFields(
            hydrograph.engine.jaxb.groupcombine.TypePrimaryKeyFields typePrimaryKeyFields) {
        if (typePrimaryKeyFields == null) {
            return null;
        } else if (typePrimaryKeyFields.getNone() != null) {
            return null;
        } else if (typePrimaryKeyFields.getField() == null) {
            return null;
        }
        KeyField[] keyFields = new KeyField[typePrimaryKeyFields.getField().size()];
        int i = 0;
        for (TypeFieldName eachTypeFieldName : typePrimaryKeyFields.getField()) {
            KeyField eachKeyField = new KeyField();
            eachKeyField.setName(eachTypeFieldName.getName());
            // eachKeyField.setSortOrder(eachTypeFieldName.getOrder().value());
            keyFields[i] = eachKeyField;
            i++;
        }
        return keyFields;
    }

    /**
     * Extracts the key field names and sort order from the
     * {@link hydrograph.engine.jaxb.cumulate.TypePrimaryKeyFields
     * TypePrimaryKeyFields} object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code typePrimaryKeyFields}
     * parameter is {@code null}
     *
     * @param typePrimaryKeyFields the object of
     *                             {@link hydrograph.engine.jaxb.cumulate.TypePrimaryKeyFields
     *                             TypePrimaryKeyFields} which contain information of key fields
     *                             for the component
     * @return an array of {@link KeyField} containing the field name and sort
     * order
     */
    public static KeyField[] extractKeyFields(
            hydrograph.engine.jaxb.cumulate.TypePrimaryKeyFields typePrimaryKeyFields) {
        if (typePrimaryKeyFields == null) {
            return null;
        } else if (typePrimaryKeyFields.getNone() != null) {
            return null;
        } else if (typePrimaryKeyFields.getField() == null) {
            return null;
        }
        KeyField[] keyFields = new KeyField[typePrimaryKeyFields.getField().size()];
        int i = 0;
        for (TypeFieldName eachTypeFieldName : typePrimaryKeyFields.getField()) {
            KeyField eachKeyField = new KeyField();
            eachKeyField.setName(eachTypeFieldName.getName());
            // eachKeyField.setSortOrder(eachTypeFieldName.getOrder().value());
            keyFields[i] = eachKeyField;
            i++;
        }
        return keyFields;
    }

    /**
     * Extracts the key field names and sort order from the
     * {@link hydrograph.engine.jaxb.cumulate.TypeSecondaryKeyFields
     * TypeSecondaryKeyFields} object passed as a parameter
     * <p>
     * The method returns {@code null} if the {@code typeSecondaryKeyFields}
     * parameter is {@code null}
     *
     * @param typeSecondaryKeyFields the object of
     *                               {@link hydrograph.engine.jaxb.cumulate.TypeSecondaryKeyFields
     *                               TypeSecondaryKeyFields} which contain information of key
     *                               fields for the component
     * @return an array of {@link KeyField} containing the field name and sort
     * order
     */
    public static KeyField[] extractSecondaryKeyFields(
            hydrograph.engine.jaxb.cumulate.TypeSecondaryKeyFields typeSecondaryKeyFields) {
        if (typeSecondaryKeyFields == null || typeSecondaryKeyFields.getField() == null) {
            return null;
        }
        KeyField[] keyFields = new KeyField[typeSecondaryKeyFields.getField().size()];
        int i = 0;
        for (hydrograph.engine.jaxb.cumulate.TypeSecondayKeyFieldsAttributes eachTypeFieldName : typeSecondaryKeyFields
                .getField()) {
            KeyField eachKeyField = new KeyField();
            eachKeyField.setName(eachTypeFieldName.getName());
            eachKeyField.setSortOrder(eachTypeFieldName.getOrder().value());
            keyFields[i] = eachKeyField;
            i++;
        }
        return keyFields;
    }

    public static String extractOutputRecordCount(
            TypeOutputRecordCount outputRecordCount) {
        if (outputRecordCount != null) {
            return outputRecordCount.getValue();
        }
        return "";
    }

    public static void checkIfOutputRecordCountIsPresentInCaseOfExpressionProcessing(
            List<Operation> operationsList,
            TypeOutputRecordCount outputRecordCount) {
        if (operationsList.get(0).getExpression() != null
                && !"".equals(operationsList.get(0).getExpression())) {
            if (outputRecordCount == null) {
                throw new RuntimeException(
                        "Output Record Count is a mandatory parameter in case of Expression Processing.");
            }
        }
    }
}