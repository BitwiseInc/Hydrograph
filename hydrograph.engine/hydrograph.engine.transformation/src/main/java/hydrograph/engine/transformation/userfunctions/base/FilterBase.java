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
import java.util.ArrayList;
import java.util.Properties;

/**
 * The Interface FilterBase.
 *
 * @author Bitwise
 *
 */
public interface FilterBase extends Serializable {
	Properties properties=new Properties();
	/**
	 * This method is called before processing the first record in the input.
	 * This method can be called for initialization / instantiation of of
	 * artifacts to be used in the filter expressions.
	 * 
	 * @param props
	 *            the properties object which holds the operation properties
	 *            passed in xml. The individual properties are wrapped as
	 *            key-value pairs in the props object.
	 * @param inputFields
	 *            the list of input fields to the filter operation.
	 */
	//@Deprecated
	void prepare(Properties props, ArrayList<String> inputFields);

	/**
	 * This method is the operation function and is called for each input row.
	 * The custom filtering logic should be performed in this function. This
	 * function returns a boolean value which determines if the record is to be
	 * excluded from the output.
	 * 
	 * <p>
	 * If the function returns true the record is rejected from the output, i.e.
	 * transmitted to the unused port of the filter component.
	 * </p>
	 * 
	 * <p>
	 * If the function returns false the record is retained in the output, i.e.
	 * transmitted to the out port of the filter component.
	 * </p>
	 * 
	 * @param inputRow
	 *            the {@link ReusableRow} object that holds the current input
	 *            row for the operation.
	 * @return a boolean value determining whether the record is to be removed.
	 *         <p>
	 *         If the function returns true the record is rejected from the
	 *         output, i.e. transmitted to the unused port of the filter
	 *         component.
	 *         </p>
	 * 
	 *         <p>
	 *         If the function returns false the record is retained in the
	 *         output, i.e. transmitted to the out port of the filter component.
	 *         </p>
	 */
	boolean isRemove(ReusableRow inputRow);

	/**
	 * This method is called after processing all the records in the input. This
	 * function can be typically used to do cleanup activities as the name
	 * suggests.
	 */
	@Deprecated
	default void cleanup(){}
}
