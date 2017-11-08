
/*
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
 */
package hydrograph.engine.jaxb.commontypes;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.commontypes package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TypeOperationsComponentExpression_QNAME = new QName("", "expression");
    private final static QName _TypeOperationsComponentIncludeExternalExpression_QNAME = new QName("", "includeExternalExpression");
    private final static QName _TypeOperationsComponentOperation_QNAME = new QName("", "operation");
    private final static QName _TypeOperationsComponentIncludeExternalOperation_QNAME = new QName("", "includeExternalOperation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.commontypes
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link KeyfieldDescriptionType }
     * 
     */
    public KeyfieldDescriptionType createKeyfieldDescriptionType() {
        return new KeyfieldDescriptionType();
    }

    /**
     * Create an instance of {@link KeyfieldDescriptionType.KeyFields }
     * 
     */
    public KeyfieldDescriptionType.KeyFields createKeyfieldDescriptionTypeKeyFields() {
        return new KeyfieldDescriptionType.KeyFields();
    }

    /**
     * Create an instance of {@link TypeProperties }
     * 
     */
    public TypeProperties createTypeProperties() {
        return new TypeProperties();
    }

    /**
     * Create an instance of {@link TypeInputField }
     * 
     */
    public TypeInputField createTypeInputField() {
        return new TypeInputField();
    }

    /**
     * Create an instance of {@link TypeFieldName }
     * 
     */
    public TypeFieldName createTypeFieldName() {
        return new TypeFieldName();
    }

    /**
     * Create an instance of {@link TypeOutputInSocket }
     * 
     */
    public TypeOutputInSocket createTypeOutputInSocket() {
        return new TypeOutputInSocket();
    }

    /**
     * Create an instance of {@link TypeOperationOutputFields }
     * 
     */
    public TypeOperationOutputFields createTypeOperationOutputFields() {
        return new TypeOperationOutputFields();
    }

    /**
     * Create an instance of {@link TypeBaseField }
     * 
     */
    public TypeBaseField createTypeBaseField() {
        return new TypeBaseField();
    }

    /**
     * Create an instance of {@link TypeInputOutSocket }
     * 
     */
    public TypeInputOutSocket createTypeInputOutSocket() {
        return new TypeInputOutSocket();
    }

    /**
     * Create an instance of {@link TypeBaseRecord }
     * 
     */
    public TypeBaseRecord createTypeBaseRecord() {
        return new TypeBaseRecord();
    }

    /**
     * Create an instance of {@link TypeBaseInSocket }
     * 
     */
    public TypeBaseInSocket createTypeBaseInSocket() {
        return new TypeBaseInSocket();
    }

    /**
     * Create an instance of {@link ElementValueStringType }
     * 
     */
    public ElementValueStringType createElementValueStringType() {
        return new ElementValueStringType();
    }

    /**
     * Create an instance of {@link TypeExternalSchema }
     * 
     */
    public TypeExternalSchema createTypeExternalSchema() {
        return new TypeExternalSchema();
    }

    /**
     * Create an instance of {@link TypeOperationInputFields }
     * 
     */
    public TypeOperationInputFields createTypeOperationInputFields() {
        return new TypeOperationInputFields();
    }

    /**
     * Create an instance of {@link BooleanValueType }
     * 
     */
    public BooleanValueType createBooleanValueType() {
        return new BooleanValueType();
    }

    /**
     * Create an instance of {@link TypeTransformOperation }
     * 
     */
    public TypeTransformOperation createTypeTransformOperation() {
        return new TypeTransformOperation();
    }

    /**
     * Create an instance of {@link ElementValueIntegerType }
     * 
     */
    public ElementValueIntegerType createElementValueIntegerType() {
        return new ElementValueIntegerType();
    }

    /**
     * Create an instance of {@link TypeBaseComponent }
     * 
     */
    public TypeBaseComponent createTypeBaseComponent() {
        return new TypeBaseComponent();
    }

    /**
     * Create an instance of {@link TypeExpressionOutputFields }
     * 
     */
    public TypeExpressionOutputFields createTypeExpressionOutputFields() {
        return new TypeExpressionOutputFields();
    }

    /**
     * Create an instance of {@link TypeKeyFields }
     * 
     */
    public TypeKeyFields createTypeKeyFields() {
        return new TypeKeyFields();
    }

    /**
     * Create an instance of {@link TypeOutputRecordCount }
     * 
     */
    public TypeOutputRecordCount createTypeOutputRecordCount() {
        return new TypeOutputRecordCount();
    }

    /**
     * Create an instance of {@link TypeBaseInSocketFixedIn0 }
     * 
     */
    public TypeBaseInSocketFixedIn0 createTypeBaseInSocketFixedIn0() {
        return new TypeBaseInSocketFixedIn0();
    }

    /**
     * Create an instance of {@link TypeStraightPullOutSocket }
     * 
     */
    public TypeStraightPullOutSocket createTypeStraightPullOutSocket() {
        return new TypeStraightPullOutSocket();
    }

    /**
     * Create an instance of {@link TypeTrueFalse }
     * 
     */
    public TypeTrueFalse createTypeTrueFalse() {
        return new TypeTrueFalse();
    }

    /**
     * Create an instance of {@link TypeMapField }
     * 
     */
    public TypeMapField createTypeMapField() {
        return new TypeMapField();
    }

    /**
     * Create an instance of {@link TypeExpressionField }
     * 
     */
    public TypeExpressionField createTypeExpressionField() {
        return new TypeExpressionField();
    }

    /**
     * Create an instance of {@link TypeOutSocketAsInSocket }
     * 
     */
    public TypeOutSocketAsInSocket createTypeOutSocketAsInSocket() {
        return new TypeOutSocketAsInSocket();
    }

    /**
     * Create an instance of {@link TypeBaseOutSocket }
     * 
     */
    public TypeBaseOutSocket createTypeBaseOutSocket() {
        return new TypeBaseOutSocket();
    }

    /**
     * Create an instance of {@link TypeOperationsOutSocket }
     * 
     */
    public TypeOperationsOutSocket createTypeOperationsOutSocket() {
        return new TypeOperationsOutSocket();
    }

    /**
     * Create an instance of {@link TypeTransformExpression }
     * 
     */
    public TypeTransformExpression createTypeTransformExpression() {
        return new TypeTransformExpression();
    }

    /**
     * Create an instance of {@link TypeOperationField }
     * 
     */
    public TypeOperationField createTypeOperationField() {
        return new TypeOperationField();
    }

    /**
     * Create an instance of {@link KeyfieldDescriptionType.KeyFields.Field }
     * 
     */
    public KeyfieldDescriptionType.KeyFields.Field createKeyfieldDescriptionTypeKeyFieldsField() {
        return new KeyfieldDescriptionType.KeyFields.Field();
    }

    /**
     * Create an instance of {@link TypeProperties.Property }
     * 
     */
    public TypeProperties.Property createTypePropertiesProperty() {
        return new TypeProperties.Property();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypeTransformExpression }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "expression", scope = TypeOperationsComponent.class)
    public JAXBElement<TypeTransformExpression> createTypeOperationsComponentExpression(TypeTransformExpression value) {
        return new JAXBElement<TypeTransformExpression>(_TypeOperationsComponentExpression_QNAME, TypeTransformExpression.class, TypeOperationsComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypeExternalSchema }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "includeExternalExpression", scope = TypeOperationsComponent.class)
    public JAXBElement<TypeExternalSchema> createTypeOperationsComponentIncludeExternalExpression(TypeExternalSchema value) {
        return new JAXBElement<TypeExternalSchema>(_TypeOperationsComponentIncludeExternalExpression_QNAME, TypeExternalSchema.class, TypeOperationsComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypeTransformOperation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "operation", scope = TypeOperationsComponent.class)
    public JAXBElement<TypeTransformOperation> createTypeOperationsComponentOperation(TypeTransformOperation value) {
        return new JAXBElement<TypeTransformOperation>(_TypeOperationsComponentOperation_QNAME, TypeTransformOperation.class, TypeOperationsComponent.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypeExternalSchema }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "includeExternalOperation", scope = TypeOperationsComponent.class)
    public JAXBElement<TypeExternalSchema> createTypeOperationsComponentIncludeExternalOperation(TypeExternalSchema value) {
        return new JAXBElement<TypeExternalSchema>(_TypeOperationsComponentIncludeExternalOperation_QNAME, TypeExternalSchema.class, TypeOperationsComponent.class, value);
    }

}
