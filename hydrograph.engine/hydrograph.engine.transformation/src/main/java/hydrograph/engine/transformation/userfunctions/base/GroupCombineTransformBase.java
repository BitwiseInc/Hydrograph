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
package hydrograph.engine.transformation.userfunctions.base;

import hydrograph.engine.transformation.schema.Schema;

import java.io.Serializable;

/**
 * The AggregateTransformBase interface is the base interface for all the custom
 * classes defined for transformations in aggregate component in Hydrograph.
 * This interface exposes methods that enable users to perform custom aggregate
 * operation.
 * <p>
 * For a sample implementation of this interface refer any class under
 * {@link hydrograph.engine.transformation.userfunctions.aggregate} package.
 * </p>
 *
 * @author bitwise
 */
public interface GroupCombineTransformBase extends Serializable {

//    public void prepare(Properties props, ArrayList<String> inputFields, ArrayList<String> outputFields,
//                        ArrayList<String> keyFields);

    public Schema initBufferSchema(Schema inputSchema,Schema outputSchema );

    public void initialize(ReusableRow bufferRow);

    public void update(ReusableRow bufferRow, ReusableRow inputRow);

    public void merge(ReusableRow bufferRow1, ReusableRow bufferRow2);

    public void evaluate(ReusableRow bufferRow, ReusableRow outRow);

//    public void cleanup();

}