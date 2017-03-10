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

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.figure.CommentBoxFigure;
import hydrograph.ui.graph.model.CommentBox;

/**
 * 
 * This class is an editor for comment box. The class allows user to perform normal edit functionality on comment box.
 * 
 * @author Bitwise
 *
 */
public class CommentBoxEditor extends Dialog {

	protected Object result;
	protected Shell shell;

	private CommentBoxFigure commentFigure;
	private CommentBox commentBoxModel;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 * @param commentBox
	 */
	public CommentBoxEditor(Shell parent, int style, CommentBoxFigure commentFigure, CommentBox commentBox) {
		super(parent, SWT.CLOSE);
		this.commentFigure = commentFigure;
		commentBoxModel = commentBox;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {

		Rectangle commentBoxEditorBounds = getCommentBoxEditorBounds();

		createCommentBoxShell(commentBoxEditorBounds);

		StyledText styledText = createCommentBoxEditorArea();

		attachCommentBoxEditorListeners(styledText);

	}

	private void attachCommentBoxEditorListeners(StyledText styledText) {
		addCommentBoxEditorFocusListener(styledText);
	}

	/**
	 * 
	 * This listener is called when user click outside text area
	 * 
	 */
	private void addCommentBoxEditorFocusListener(StyledText styledText) {
		styledText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				validateAndCloseCommentEditor(styledText);
			}

			@Override
			public void focusGained(FocusEvent e) {
				// DO Nothing
			}
		});
		
		styledText.addTraverseListener(new TraverseListener() {
			
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.stateMask == 0 && e.detail == SWT.TRAVERSE_RETURN ) {
				    validateAndCloseCommentEditor(styledText);
				}
			}
		});
		
	}

	private void validateAndCloseCommentEditor(StyledText styledText) {
		commentFigure.setText(styledText.getText(),commentBoxModel);

		if (!StringUtils.equals(styledText.getText(), commentBoxModel.getLabelContents())) {
			commentBoxModel.setLabelContents(styledText.getText());
			((ELTGraphicalEditor) commentFigure.getComponentCanvas()).setDirty(true);
		}

		shell.close();
	}
	
	private StyledText createCommentBoxEditorArea() {
		StyledText styledText = new StyledText(shell, SWT.NONE);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		addDefaultCommentTextInEditor(styledText);
		styledText.selectAll();
		return styledText;
	}

	/**
	 * 
	 * Adds existing comment text in editor text area
	 * 
	 * @param styledText
	 */
	private void addDefaultCommentTextInEditor(StyledText styledText) {
		styledText.setText(commentFigure.getText());
	}

	private void createCommentBoxShell(org.eclipse.swt.graphics.Rectangle commentBoxEditorBounds) {
		shell = new Shell(getParent(), SWT.NONE);
		shell.setBounds(commentBoxEditorBounds);
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		gl_shell.horizontalSpacing = 0;
		shell.setLayout(gl_shell);
	}

	private Rectangle getCommentBoxEditorBounds() {
		org.eclipse.draw2d.geometry.Rectangle commentBoxFigureBounds = getCommentBoxFigureBounds();
		Point commentBoxLocationOnWorkbench = commentFigure.getLocationOnWorkbench();
		Rectangle commentBoxEditorBounds = new Rectangle(commentBoxLocationOnWorkbench.x,
				commentBoxLocationOnWorkbench.y, commentBoxFigureBounds.width, commentBoxFigureBounds.height);
		return commentBoxEditorBounds;
	}

	private org.eclipse.draw2d.geometry.Rectangle getCommentBoxFigureBounds() {
		org.eclipse.draw2d.geometry.Rectangle commentBoxFigureBounds = commentFigure.getBounds().getCopy();
		return commentBoxFigureBounds;
	}

	public Shell getShell() {
		return shell;
	}
}
