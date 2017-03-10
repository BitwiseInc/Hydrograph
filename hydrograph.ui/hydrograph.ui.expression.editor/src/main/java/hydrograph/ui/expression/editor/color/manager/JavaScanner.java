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

package hydrograph.ui.expression.editor.color.manager;

import hydrograph.ui.expression.editor.datastructure.ClassDetails;
import hydrograph.ui.expression.editor.datastructure.MethodDetails;
import hydrograph.ui.expression.editor.repo.ClassRepo;

import java.util.Hashtable;
import java.util.List;

/**
 * A simple scanner for Java
 */
public class JavaScanner {

	protected Hashtable<String, Integer> fgKeys = null;

	protected Hashtable<String, Integer> classKeys = null;

	protected Hashtable<String, Integer> functionKeys = null;

	
		
	protected StringBuffer fBuffer = new StringBuffer();

	protected String fDoc;

	protected int fPos;

	protected int fEnd;

	protected int fStartToken;

	protected boolean fEofSeen = false;

	private String[] fgKeywords = { "abstract", "boolean", "break", "byte", "case", "catch", "char", "class",
			"continue", "default", "do", "double", "else", "extends", "false", "final", "finally", "float", "for",
			"if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package",
			"private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this",
			"throw", "throws", "transient", "true", "try", "void", "volatile", "while" };

	private Hashtable<String, Integer> functionsKeys;

	private Hashtable<String, Integer> fieldKeys;

	public JavaScanner(List<String> selectedInputFields) {
		initialize();
		fieldKeys=new Hashtable<String,Integer>();
		Integer k = new Integer(JavaLineStyler.FIELDS);
		for(String fieldName:selectedInputFields){
			fieldKeys.put(fieldName,k);
		}
			
	}

	/**
	 * Returns the ending location of the current token in the document.
	 */
	public final int getLength() {
		return fPos - fStartToken;
	}

	/**
	 * Initialize the lookup table.
	 */
	private void initialize() {
		fgKeys = new Hashtable<String, Integer>();
		Integer k = new Integer(JavaLineStyler.KEY);
		for (int i = 0; i < fgKeywords.length; i++)
			fgKeys.put(fgKeywords[i], k);

		loadClassColor();
	}

	public void loadClassColor() {
		classKeys = new Hashtable<String, Integer>();
		Integer k = new Integer(JavaLineStyler.CLASSES);
		for (ClassDetails classDetails : ClassRepo.INSTANCE.getClassList()) {
			classKeys.put(classDetails.getcName(), k);
		}

		functionsKeys = new Hashtable<String, Integer>();
		Integer j = new Integer(JavaLineStyler.FUNCTIONS);
		for (ClassDetails classDetails : ClassRepo.INSTANCE.getClassList()) {
			for (MethodDetails details : classDetails.getMethodList())
				functionsKeys.put(details.getMethodName(), j);
		}
	}

	/**
	 * Returns the starting location of the current token in the document.
	 */
	public final int getStartOffset() {
		return fStartToken;
	}

	/**
	 * Returns the next lexical token in the document.
	 */
	public int nextToken() {
		int c;
		fStartToken = fPos;
		while (true) {
			switch (c = read()) {
			case JavaLineStyler.EOF:
				return JavaLineStyler.EOF;
			case '/': // comment
				c = read();
				if (c == '/') {
					while (true) {
						c = read();
						if ((c == JavaLineStyler.EOF) || (c == JavaLineStyler.EOL)) {
							unread(c);
							return JavaLineStyler.COMMENT;
						}
					}
				} else {
					unread(c);
				}
				return JavaLineStyler.OTHER;
			case '\'': // char constant
				for (;;) {
					c = read();
					switch (c) {
					case '\'':
						return JavaLineStyler.STRING;
					case JavaLineStyler.EOF:
						unread(c);
						return JavaLineStyler.STRING;
					case '\\':
						c = read();
						break;
					}
				}

			case '"': // string
				for (;;) {
					c = read();
					switch (c) {
					case '"':
						return JavaLineStyler.STRING;
					case JavaLineStyler.EOF:
						unread(c);
						return JavaLineStyler.STRING;
					case '\\':
						c = read();
						break;
					}
				}

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				do {
					c = read();
				} while (Character.isDigit((char) c));
				unread(c);
				return JavaLineStyler.NUMBER;
			default:
				if (Character.isWhitespace((char) c)) {
					do {
						c = read();
					} while (Character.isWhitespace((char) c));
					unread(c);
					return JavaLineStyler.WHITE;
				}
				if (Character.isJavaIdentifierStart((char) c)) {
					fBuffer.setLength(0);
					do {
						fBuffer.append((char) c);
						c = read();
					} while (Character.isJavaIdentifierPart((char) c));
					unread(c);
					Integer i = (Integer) fgKeys.get(fBuffer.toString());
					if (i != null) {
						return i.intValue();
					} else if (classKeys.get(fBuffer.toString()) != null) {
						return classKeys.get(fBuffer.toString()).intValue();
					} else if (functionsKeys.get(fBuffer.toString()) != null) {
						return functionsKeys.get(fBuffer.toString()).intValue();
					}else if (fieldKeys.get(fBuffer.toString()) != null) {
						return fieldKeys.get(fBuffer.toString()).intValue();
					}
					
					return JavaLineStyler.WORD;
				}
				return JavaLineStyler.OTHER;
			}
		}
	}

	/**
	 * Returns next character.
	 */
	private int read() {
		if (fPos <= fEnd) {
			return fDoc.charAt(fPos++);
		}
		return JavaLineStyler.EOF;
	}

	public void setRange(String text) {
		fDoc = text;
		fPos = 0;
		fEnd = fDoc.length() - 1;
	}

	private void unread(int c) {
		if (c != JavaLineStyler.EOF)
			fPos--;
	}
}
