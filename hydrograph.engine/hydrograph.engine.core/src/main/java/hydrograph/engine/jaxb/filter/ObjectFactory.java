
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

package hydrograph.engine.jaxb.filter;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * The Class ObjectFactory .
 *
 * @author Bitwise
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.filter
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypeFilterInSocket }
     * 
     */
    public TypeFilterInSocket createTypeFilterInSocket() {
        return new TypeFilterInSocket();
    }

    /**
     * Create an instance of {@link TypeFilterOperation }
     * 
     */
    public TypeFilterOperation createTypeFilterOperation() {
        return new TypeFilterOperation();
    }

    /**
     * Create an instance of {@link TypeOutSocketAsInSocketIn0 }
     * 
     */
    public TypeOutSocketAsInSocketIn0 createTypeOutSocketAsInSocketIn0() {
        return new TypeOutSocketAsInSocketIn0();
    }

    /**
     * Create an instance of {@link TypeFilterOperationInputField }
     * 
     */
    public TypeFilterOperationInputField createTypeFilterOperationInputField() {
        return new TypeFilterOperationInputField();
    }

    /**
     * Create an instance of {@link TypeFilterOutSocket }
     * 
     */
    public TypeFilterOutSocket createTypeFilterOutSocket() {
        return new TypeFilterOutSocket();
    }

    /**
     * Create an instance of {@link TypeFilterOperationInputFields }
     * 
     */
    public TypeFilterOperationInputFields createTypeFilterOperationInputFields() {
        return new TypeFilterOperationInputFields();
    }

}
