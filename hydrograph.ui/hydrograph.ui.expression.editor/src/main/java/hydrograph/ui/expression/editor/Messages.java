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
 *******************************************************************************/

package hydrograph.ui.expression.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "resources.messages"; //$NON-NLS-1$
	public static String SEARCH_TEXT_BOX_TOOLTIP;
	public static String VALID_EXPRESSION_TITLE;
	public static String CANNOT_OPEN_EDITOR;
	public static String MESSAGE_TO_EXIT_WITHOUT_SAVE;
	public static String JAVA_DOC_NOT_AVAILABLE;
	public static String OPERATOR_FILE_NOT_FOUND;
	public static String WARNING;
	public static String ERROR_TITLE;
	public static String JAR_FILE_COPY_ERROR;
	public static String DUPLICATE_JAR_FILE_COPY_ERROR;
	public static String INVALID_EXPRESSION_TITLE;
	public static String ERROR_OCCURRED_WHILE_EVALUATING_EXPRESSION;
	public static String TITLE_FOR_PROBLEM_IN_LOADING_EXPRESSION_EDITOR;
	public static String ERROR_WHILE_LOADING_CONFIGURATIONS_FOR_EXTERNAL_JOBS;
	public static String EXPRESSION_EDITOR_TITLE;
	public static String EXPRESSION_EDITOR_EVALUATE_DIALOG_TITLE;
	public static String EVALUATE_BUTTON_TOOLTIP;
	public static String VALIDATE_BUTTON_TOOLTIP;
	public static String WORD_WRAP_BUTTON_TOOLTIP;
	public static String OPERATORS_TOOLTIP;
	public static String EXTERNAL_JAR_DIALOG_BROWSE_BUTTON_TOOLTIP;
	public static String EXTERNAL_JAR_DIALOG_DELETE_BUTTON_TOOLTIP;
	public static String CANNOT_SEARCH_INPUT_STRING;
	public static String EVALUATE_EXPRESSION_EDITOR_GROUP_HEADER;
	public static String EVALUATE_FIELD_NAMES_GROUP_HEADER;
	public static String EVALUATE_OUTPUT_CONSOLE_GROUP_HEADER;
	public static String AVAILABLE_INPUT_FIELDS;
	public static String DATA_TYPE;
	public static String FIELD_NAME;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
