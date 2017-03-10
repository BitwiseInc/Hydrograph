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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import hydrograph.ui.common.util.CustomColorRegistry;

public class JavaLineStyler implements LineStyleListener {
	

	JavaScanner scanner ;

	int[] tokenColors;

	Color[] colors;

	Vector<int[]> blockComments = new Vector<int[]>();

	public static final int EOF = -1;

	public static final int EOL = 10;

	public static final int WORD = 0;

	public static final int WHITE = 1;

	public static final int KEY = 2;

	public static final int COMMENT = 3;

	public static final int STRING = 5;

	public static final int OTHER = 6;

	public static final int NUMBER = 7;

	public static final int MAXIMUM_TOKEN = 10;

	public static final int CLASSES =4;
	
	public static final int FUNCTIONS=8;
	
	public static final int FIELDS=9;

	public JavaLineStyler(List<String> selectedInputFields) {
		initializeColors();
		scanner = new JavaScanner(selectedInputFields);
	}

	Color getColor(int type) {
		if (type < 0 || type >= tokenColors.length) {
			return null;
		}
		return colors[tokenColors[type]];
	}

	boolean inBlockComment(int start, int end) {
		for (int i = 0; i < blockComments.size(); i++) {
			int[] offsets = (int[]) blockComments.elementAt(i);
			// start of comment in the line
			if ((offsets[0] >= start) && (offsets[0] <= end))
				return true;
			// end of comment in the line
			if ((offsets[1] >= start) && (offsets[1] <= end))
				return true;
			if ((offsets[0] <= start) && (offsets[1] >= end))
				return true;
		}
		return false;
	}

	void initializeColors() {
		colors = new Color[] {CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 0, 0), // black
				CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0), // red
				CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 255, 0), // green
				CustomColorRegistry.INSTANCE.getColorFromRegistry(  0, 0, 255), // blue
				CustomColorRegistry.INSTANCE.getColorFromRegistry( 139, 69, 19), //brown
				CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 100, 0), // custom
				CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,20,147)// deep -pink
		};
		tokenColors = new int[MAXIMUM_TOKEN];
		tokenColors[WORD] = 0;
		tokenColors[WHITE] = 0;
		tokenColors[KEY] = 3;
		tokenColors[COMMENT] = 1;
		tokenColors[STRING] = 3;
		tokenColors[OTHER] = 0;
		tokenColors[NUMBER] = 0;
		tokenColors[FUNCTIONS] = 4;
		tokenColors[CLASSES] = 5;
		tokenColors[FIELDS] = 6;
	}


	/**
	 * Event.detail line start offset (input) Event.text line text (input) LineStyleEvent.styles Enumeration of
	 * StyleRanges, need to be in order. (output) LineStyleEvent.background line background color (output)
	 */
	public void lineGetStyle(LineStyleEvent event) {
		Vector<StyleRange> styles = new Vector<StyleRange>();
		int token;
		StyleRange lastStyle;
		// If the line is part of a block comment, create one style for the
		// entire line.
		if (inBlockComment(event.lineOffset, event.lineOffset + event.lineText.length())) {
			styles.addElement(new StyleRange(event.lineOffset, event.lineText.length(), getColor(COMMENT), null));
			event.styles = new StyleRange[styles.size()];
			styles.copyInto(event.styles);
			return;
		}
		Color defaultFgColor = ((Control) event.widget).getForeground();
		scanner.setRange(event.lineText);
		token = scanner.nextToken();
		while (token != EOF) {
			if (token == OTHER) {
				// do nothing for non-colored tokens
			} else if (token != WHITE) {
				Color color = getColor(token);
				// Only create a style if the token color is different than the
				// widget's default foreground color and the token's style is
				// not
				// bold. Keywords are bolded.
				if ((!color.equals(defaultFgColor)) || (token == KEY)) {
					StyleRange style = new StyleRange(scanner.getStartOffset() + event.lineOffset, scanner.getLength(),
							color, null);
					if (token == KEY) {
						style.fontStyle = SWT.BOLD;
					}
					if (styles.isEmpty()) {
						styles.addElement(style);
					} else {
						// Merge similar styles. Doing so will improve
						// performance.
						lastStyle = (StyleRange) styles.lastElement();
						if (lastStyle.similarTo(style) && (lastStyle.start + lastStyle.length == style.start)) {
							lastStyle.length += style.length;
						} else {
							styles.addElement(style);
						}
					}
				}
			} else if ((!styles.isEmpty()) && ((lastStyle = (StyleRange) styles.lastElement()).fontStyle == SWT.BOLD)) {
				int start = scanner.getStartOffset() + event.lineOffset;
				lastStyle = (StyleRange) styles.lastElement();
				// A font style of SWT.BOLD implies that the last style
				// represents a java keyword.
				if (lastStyle.start + lastStyle.length == start) {
					// Have the white space take on the style before it to
					// minimize the number of style ranges created and the
					// number of font style changes during rendering.
					lastStyle.length += scanner.getLength();
				}
			}
			token = scanner.nextToken();
		}
		event.styles = new StyleRange[styles.size()];
		styles.copyInto(event.styles);
	}

	public void parseBlockComments(String text) {
		blockComments = new Vector<int[]>();
		StringReader buffer = new StringReader(text);
		int ch;
		boolean blkComment = false;
		int cnt = 0;
		int[] offsets = new int[2];
		boolean done = false;

		try {
			while (!done) {
				switch (ch = buffer.read()) {
				case -1: {
					if (blkComment) {
						offsets[1] = cnt;
						blockComments.addElement(offsets);
					}
					done = true;
					break;
				}
				case '/': {
					ch = buffer.read();
					if ((ch == '*') && (!blkComment)) {
						offsets = new int[2];
						offsets[0] = cnt;
						blkComment = true;
						cnt++;
					} else {
						cnt++;
					}
					cnt++;
					break;
				}
				case '*': {
					if (blkComment) {
						ch = buffer.read();
						cnt++;
						if (ch == '/') {
							blkComment = false;
							offsets[1] = cnt;
							blockComments.addElement(offsets);
						}
					}
					cnt++;
					break;
				}
				default: {
					cnt++;
					break;
				}
				}
			}
		} catch (IOException e) {// ignore errors
		}
	}
}
