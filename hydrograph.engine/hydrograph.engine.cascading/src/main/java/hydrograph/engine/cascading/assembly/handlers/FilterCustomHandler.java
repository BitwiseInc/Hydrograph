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
package hydrograph.engine.cascading.assembly.handlers;

import cascading.operation.FilterCall;
import cascading.operation.OperationCall;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.context.CustomHandlerContext;
import hydrograph.engine.cascading.assembly.context.RecordFilterContext;
import hydrograph.engine.cascading.utilities.ReusableRowHelper;
import hydrograph.engine.expression.api.ValidationAPI;
import hydrograph.engine.expression.userfunctions.FilterForExpression;
import hydrograph.engine.transformation.userfunctions.base.FilterBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class FilterCustomHandler implements RecordFilterHandlerBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8155434168060955013L;
	private Fields inputFields;
	private String transformClass;
	private Properties userProperties;
	private ValidationAPI expressionValidationAPI;
	private boolean isUnused = false;

	private static Logger LOG = LoggerFactory.getLogger(FilterCustomHandler.class);

	public FilterCustomHandler(Fields inputFields, String transformClass, Properties userProperties) {

		this.inputFields = inputFields;
		this.transformClass = transformClass;
		this.userProperties = userProperties;
		LOG.trace("FilterCustomHandler object created for: " + transformClass);
	}

	public FilterCustomHandler(Fields inputFields, String transformClass, Properties userProperties, boolean isUnused,
			ValidationAPI validationAPI) {

		this.inputFields = inputFields;
		this.transformClass = transformClass;
		this.userProperties = userProperties;
		this.isUnused = isUnused;
		this.expressionValidationAPI = validationAPI;
		if (expressionValidationAPI == null)
			LOG.trace("FilterCustomHandler object created for: " + transformClass);
		else
			LOG.trace("FilterCustomHandler object created for: " + expressionValidationAPI);
	}

	@Override
	public Object prepare() {

		CustomHandlerContext<FilterBase> context = new CustomHandlerContext<FilterBase>(inputFields, transformClass,
				expressionValidationAPI);
		if (context.getSingleExpressionInstances() != null)
			LOG.trace("calling expression : " + context.getSingleExpressionInstances());
		else {
			try {
				LOG.trace("calling prepare method of: " + context.getSingleTransformInstance().getClass().getName());
				context.getSingleTransformInstance().prepare(userProperties,
						context.getSingleInputRow().getFieldNames());
			} catch (Exception e) {
				LOG.error("Exception in prepare method of: " + context.getSingleTransformInstance().getClass().getName()
						+ ".\nArguments passed to prepare() method are: \nProperties: " + userProperties
						+ "\nInput Fields: " + Arrays.toString(context.getSingleInputRow().getFieldNames().toArray()),
						e);
				throw new RuntimeException("Exception in prepare method of: "
						+ context.getSingleTransformInstance().getClass().getName()
						+ ".\nArguments passed to prepare() method are: \nProperties: " + userProperties
						+ "\nInput Fields: " + Arrays.toString(context.getSingleInputRow().getFieldNames().toArray()),
						e);
			}
		}
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isRemove(FilterCall<RecordFilterContext> call) {

		CustomHandlerContext<FilterBase> context = (CustomHandlerContext<FilterBase>) call.getContext()
				.getHandlerContext();

		if (context.getSingleExpressionInstances() != null) {
			try {
				if(context.getTransformInstance(0) instanceof FilterForExpression)
					((FilterForExpression)context.getTransformInstance(0)).setValidationAPI(expressionValidationAPI);
				boolean isRemove = context.getSingleTransformInstance().isRemove(ReusableRowHelper
						.extractFromTuple(call.getArguments().getTuple(), context.getSingleInputRow()));

				return isUnused ? !isRemove : isRemove;
			} catch (Exception e) {
				LOG.error(
						"Exception in isRemove method of: " + context.getSingleTransformInstance().getClass().getName()
								+ ".\nRow being processed: " + call.getArguments(),
						e);
				throw new RuntimeException(
						"Exception in isRemove method of: " + context.getSingleTransformInstance().getClass().getName()
								+ ".\nRow being processed: " + call.getArguments(),
						e);
			}
		} else {
			try {
				boolean isRemove = context.getSingleTransformInstance().isRemove(ReusableRowHelper
						.extractFromTuple(call.getArguments().getTuple(), context.getSingleInputRow()));

				return isUnused ? !isRemove : isRemove;
			} catch (Exception e) {
				LOG.error(
						"Exception in isRemove method of: " + context.getSingleTransformInstance().getClass().getName()
								+ ".\nRow being processed: " + call.getArguments(),
						e);
				throw new RuntimeException(
						"Exception in isRemove method of: " + context.getSingleTransformInstance().getClass().getName()
								+ ".\nRow being processed: " + call.getArguments(),
						e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void cleanup(OperationCall<RecordFilterContext> call) {

		CustomHandlerContext<FilterBase> context = (CustomHandlerContext<FilterBase>) call.getContext()
				.getHandlerContext();
		if (context.getSingleExpressionInstances() == null) {
			LOG.trace("calling cleanup method of: " + context.getSingleTransformInstance().getClass().getName());
			context.getSingleTransformInstance().cleanup();
		}
	}

	public String getTransformClass() {
		return transformClass;
	}

}