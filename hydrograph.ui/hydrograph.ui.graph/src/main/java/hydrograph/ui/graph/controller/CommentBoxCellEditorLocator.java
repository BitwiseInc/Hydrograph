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
package hydrograph.ui.graph.controller;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.graph.figure.CommentBoxFigure;

/**
 * The Class CommentBoxCellEditorLocator.
 * 
 * @author Bitwise
 * 
 */
final public class CommentBoxCellEditorLocator
	implements CellEditorLocator
{

private CommentBoxFigure comment;
/**
 * Instantiates a new CommentBoxCellEditorLocator.
 * 
 * @param comment
 *            the comment
*/
public CommentBoxCellEditorLocator(CommentBoxFigure comment) {
	setLabel(comment);
}

@Override
public void relocate(CellEditor celleditor) {
	Text text = (Text)celleditor.getControl();
	Rectangle rect = comment.getClientArea();
	comment.translateToAbsolute(rect);
	org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
	rect.translate(trim.x, trim.y);
	rect.width += trim.width;
	rect.height += trim.height;
	//text.setBounds(rect.x, rect.y, rect.width, rect.height);
	
	text.setBounds(rect.x - 5, rect.y - 8, rect.width+10, rect.height+16);
}

/**
 * Returns the comment box figure.
 * @return commentBoxFigure
 */
protected CommentBoxFigure getLabel() {
	return comment;
}

/**
 * Sets the comment box figure.
 * @param comment box The comment box to set
 */
protected void setLabel(CommentBoxFigure comment) {
	this.comment = comment;
}

}