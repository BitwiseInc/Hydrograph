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

import java.io.Serializable;
import java.util.Properties;

/**
 * The Interface CustomPartitionExpression.
 *
 * @author Bitwise
 *
 */
public interface CustomPartitionExpression extends Serializable {

	 Properties properties=new Properties();

	/**
	 * This method is called before processing the first record in the input.
	 * This method can be called for initialization / instantiation of of
	 * artifacts to be used in the partition operation.
	 * 
	 * @param props
	 *            the properties object which holds the operation properties
	 *            passed in xml. The individual properties are wrapped as
	 *            key-value pairs in the props object.
	 */
	@Deprecated
	default void prepare(Properties props){}

	/**
	 * This method is the operation function and is called for each input row.
	 * The custom partitioning logic should be written in this function.
	 * <p>
	 * This method should return the partition key 
	 * </p>
	 * 
	 * @param inputRow
	 *            the {@link ReusableRow} object that holds the current input
	 *            row for the operation.
	 * @param numOfPartitions
	 *            the number of partitions expected from this operation
	 * @return the partition key
	 */
	String getPartition(ReusableRow inputRow, int numOfPartitions);
}