
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

package hydrograph.engine.jaxb.oredshift;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * The Class ObjectFactory .
 *
 * @author Bitwise
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.oredshift
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypePrimaryKeys }
     * 
     */
    public TypePrimaryKeys createTypePrimaryKeys() {
        return new TypePrimaryKeys();
    }

    /**
     * Create an instance of {@link TypeOutputRedshiftInSocket }
     * 
     */
    public TypeOutputRedshiftInSocket createTypeOutputRedshiftInSocket() {
        return new TypeOutputRedshiftInSocket();
    }

    /**
     * Create an instance of {@link TypeOutputRedshiftBase }
     * 
     */
    public TypeOutputRedshiftBase createTypeOutputRedshiftBase() {
        return new TypeOutputRedshiftBase();
    }

    /**
     * Create an instance of {@link TypeRedshiftRecord }
     * 
     */
    public TypeRedshiftRecord createTypeRedshiftRecord() {
        return new TypeRedshiftRecord();
    }

    /**
     * Create an instance of {@link TypeRedshiftField }
     * 
     */
    public TypeRedshiftField createTypeRedshiftField() {
        return new TypeRedshiftField();
    }

    /**
     * Create an instance of {@link TypeLoadChoice }
     * 
     */
    public TypeLoadChoice createTypeLoadChoice() {
        return new TypeLoadChoice();
    }

    /**
     * Create an instance of {@link TypeUpdateKeys }
     * 
     */
    public TypeUpdateKeys createTypeUpdateKeys() {
        return new TypeUpdateKeys();
    }

}
