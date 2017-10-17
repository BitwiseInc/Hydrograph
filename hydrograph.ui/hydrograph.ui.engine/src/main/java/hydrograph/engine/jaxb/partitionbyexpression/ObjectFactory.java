
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
package hydrograph.engine.jaxb.partitionbyexpression;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.partitionbyexpression package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.partitionbyexpression
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypePbeInputFields }
     * 
     */
    public TypePbeInputFields createTypePbeInputFields() {
        return new TypePbeInputFields();
    }

    /**
     * Create an instance of {@link TypePbeOperationInputField }
     * 
     */
    public TypePbeOperationInputField createTypePbeOperationInputField() {
        return new TypePbeOperationInputField();
    }

    /**
     * Create an instance of {@link TypePbeInSocket }
     * 
     */
    public TypePbeInSocket createTypePbeInSocket() {
        return new TypePbeInSocket();
    }

    /**
     * Create an instance of {@link TypePbeOutSocket }
     * 
     */
    public TypePbeOutSocket createTypePbeOutSocket() {
        return new TypePbeOutSocket();
    }

    /**
     * Create an instance of {@link PartitionByExpressionBase }
     * 
     */
    public PartitionByExpressionBase createPartitionByExpressionBase() {
        return new PartitionByExpressionBase();
    }

    /**
     * Create an instance of {@link TypePbeOperation }
     * 
     */
    public TypePbeOperation createTypePbeOperation() {
        return new TypePbeOperation();
    }

}
