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

package hydrograph.ui.expression.editor.browser;

import hydrograph.ui.expression.editor.Constants;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.jdt.internal.ui.text.javadoc.JavaDoc2HTMLTextReader;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class JavaDocBrowser extends Browser {

	public JavaDocBrowser(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public boolean setText(String html) {
		html=Constants.HTML_START_TAG+convertToHTMLString(html)+Constants.HTML_END_TAG;
		return super.setText(html);
	}
	private String convertToHTMLString(String comment) {
		JavaDoc2HTMLTextReader javaDoc2HTMLTextReader=new JavaDoc2HTMLTextReader(new StringReader(comment));
		try {
			return javaDoc2HTMLTextReader.getString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return comment;
	}
	
	protected void checkSubclass () {
		// Allow subclassing
	}
}
