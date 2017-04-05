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
package hydrograph.engine.core.xmlparser.parametersubstitution;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Stack;
/**
 * The Class ParameterSubstitutor.
 *
 * @author Bitwise
 */
public class ParameterSubstitutor {

	public static final String VARIABLE_PREFIX = "@{";
	public static final String VARIABLE_SUFFIX = "}";
	private static final Logger LOG = LoggerFactory
			.getLogger(ParameterSubstitutor.class);

	private IParameterBank parameterBank;
	private HashMap<String, String> resolvedParameterCache;

	public ParameterSubstitutor(IParameterBank parameterBank) {

		if (parameterBank == null) {
			throw new ParameterSubstitutorException(
					"Parameter bank is null, which means there will be no parameter to substitute. Please provide valid parameter bank.");
		}

		this.parameterBank = parameterBank;

		resolvedParameterCache = new HashMap<String, String>();
	}

	public String substitute(String input) {

		return substitute(input, null);

	}

	private String substitute(String input, Stack<String> unresolvedParameters) {

		StringBuilder mutable;

		int startIndex = input.indexOf(VARIABLE_PREFIX);
		int endIndex = input.indexOf(VARIABLE_SUFFIX, startIndex);

		if (startIndex == -1 || endIndex == -1) {
			// If there is no parameter syntax then just return as nothing to
			// substitute
			return input;
		}

		// string builder is required to work with substitution
		mutable = new StringBuilder(input);

		// stack is required to keep track of unresolved parameters to detect
		// circular dependencies
		if (unresolvedParameters == null) {
			unresolvedParameters = new Stack<String>();
		}

		substituteMutable(mutable, unresolvedParameters);

		return mutable.toString();

	}

	private void substituteMutable(StringBuilder mutable,
			Stack<String> unresolvedParameters) {

		int startIndex = mutable.indexOf(VARIABLE_PREFIX);
		int endIndex = mutable.indexOf(VARIABLE_SUFFIX, startIndex);

		// return if nothing to substitute
		if (startIndex == -1 || endIndex == -1) {
			return;
		}

		// get parameter name
		String parameterName = mutable.substring(
				startIndex + VARIABLE_PREFIX.length(), endIndex);

		// raise exception if parameter name is blank
		if (parameterName == null || parameterName.trim().length() == 0) {
			throw new ParameterSubstitutorException(
					"Parameter name can not be blank. Please correct.");
		}

		parameterName = parameterName.trim();

		String parameterValue = null;

		if (resolvedParameterCache.containsKey(parameterName)) {
			// obtain value from cache if already present
			parameterValue = resolvedParameterCache.get(parameterName);
			LOG.info("cache used for " + parameterName);
		} else {

			// check if the parameter is already on the stack then raise
			// exception
			// that it is circular substitution
			if (unresolvedParameters.search(parameterName) != -1) {
				throw new ParameterSubstitutorException(
						"Found a circular depencency between parameter "
								+ parameterName
								+ " and "
								+ unresolvedParameters.peek()
								+ ". Both are referencing each other and cannot be resolved. Please correct.");
			}

			// get parameter value
			parameterValue = parameterBank.getParameter(parameterName);

			// if value is null then raise exception
			if (parameterValue == null) {
				throw new ParameterSubstitutorException(
						"No value is found for the parameter " + parameterName
								+ " to substitute");
			}

			// if parameter key to be substituted is in quotes("") then escape
			// special characters from its value
			if (isParameterPresentInQuotes(mutable, startIndex, endIndex)) {
				parameterValue = StringEscapeUtils.escapeXml(parameterValue);
			}
			
			// add current parameter to stack to check for circular loop later
			unresolvedParameters.push(parameterName);

			// check of substitution if there is a parameter reference in
			// parameter
			// value(internal substitution)
			parameterValue = substitute(parameterValue, unresolvedParameters);

			// remove parameter from stack as soon as it is resolved
			unresolvedParameters.pop();

			// add resolved value to cache
			resolvedParameterCache.put(parameterName, parameterValue);
		}
		// delete parameter syntax
		mutable.delete(startIndex, endIndex + VARIABLE_SUFFIX.length());

		// insert parameter value
		mutable.insert(startIndex, parameterValue);

		// check for next substitution and do it if available
		substituteMutable(mutable, unresolvedParameters);

	}

	private boolean isParameterPresentInQuotes(StringBuilder mutable,
			int startIndex, int endIndex) {
		return "\"".equals(mutable.substring(startIndex - 1, startIndex))
				&& "\"".equals(mutable.substring(endIndex + 1, endIndex + 2));
	}

	public class ParameterSubstitutorException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -9191434762420324225L;

		public ParameterSubstitutorException(String msg) {
			super(msg);
		}
	}

}
