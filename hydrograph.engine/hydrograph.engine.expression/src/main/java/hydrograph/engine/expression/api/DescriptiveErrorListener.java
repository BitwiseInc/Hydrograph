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
package hydrograph.engine.expression.api;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * The Class DescriptiveErrorListener .
 *
 * @author Bitwise
 */
public class DescriptiveErrorListener extends BaseErrorListener {
	public static DescriptiveErrorListener INSTANCE = new DescriptiveErrorListener();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		// if (!REPORT_SYNTAX_ERRORS) {
		// return;
		// }

		String sourceName = recognizer.getInputStream().getSourceName();
		if (!sourceName.isEmpty()) {
			sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
		}
		// System.err.println(sourceName+"line "+line+":"+charPositionInLine+"
		// "+msg);
		throw new HydrographExpressionError(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
	}

	public static class HydrographExpressionError extends RuntimeException {
		public HydrographExpressionError(String s) {
			super(s);
		}
	}
}