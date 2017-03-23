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
 * The Interface NormalizeTransformBase.
 *
 * @author Bitwise
 *
 */
public interface NormalizeTransformBase extends Serializable {

	/**
	 * This method is called before processing the first record in the input.
	 * This method can be called for initialization / instantiation of artifacts
	 * to be used in the normalize operation.
	 * 
	 * @param props
	 *            the properties object which holds the operation properties
	 *            passed in xml. The individual properties are wrapped as
	 *            key-value pairs in the props object.
	 */
	public void prepare(Properties props);

	/**
	 * This method is the operation function and is called for each input row.
	 * The custom normalize logic should be written in this function.
	 * 
	 * <p>
	 * Since this function is called for each record in the input, the values of
	 * variables local to this function are not persisted for every call. Use
	 * {@link # prepare(Properties, ArrayList, ArrayList, ArrayList)} and
	 * {@link # onCompleteGroup(ReusableRow)} functions to initialize / reset the
	 * required variables.
	 * </p>
	 * 
	 * @param inputRow
	 *            the {@link ReusableRow} object that holds the current input
	 *            row for the operation.
	 * @param outputRow
	 *            the {@link ReusableRow} object that holds the output row for
	 *            the operation.
	 * @param outputDispatcher
	 *            {@code outputDispatcher} is used to dispatch the current
	 *            output row to the out port of the normalize component.
	 *            <p>
	 *            The method {@link # OutputDispatcher.sendOutput()} in
	 *            {@code OutputDispatcher} sends the current output row i.e.
	 *            {@code outputRow} variable to the out port of the normalize
	 *            component. The {@code outputRow} variable can then be reused
	 *            to create a new record.
	 *            </p>
	 */
	public void Normalize(ReusableRow inputRow, ReusableRow outputRow, OutputDispatcher outputDispatcher);

	/**
	 * The method cleanup() is called after processing all the records in the
	 * input. This function can be typically used to do cleanup activities as
	 * the name suggests.
	 */
	public void cleanup();

}
