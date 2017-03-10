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
package hydrograph.ui.graph.action;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.CellEditorActionHandler;

import hydrograph.ui.graph.figure.CommentBoxFigure;
/**
 * The Class CommentBoxLabelEditManager.
 * 
 * @author Bitwise
 * 
 */
public class CommentBoxLabelEditManager 
	extends DirectEditManager
{

private IActionBars actionBars;
private CellEditorActionHandler actionHandler;
private IAction copy, cut, paste, undo, redo, find, selectAll, delete;
private double cachedZoom = -1.0;
private Font scaledFont;
private ZoomListener zoomListener = new ZoomListener() {
	public void zoomChanged(double newZoom) {
		updateScaledFont(newZoom);
	}
};
/**
 * Instantiates a new CommentBoxLabelEditManager.
 * 
 * @param source
 *            the source
 * @param locator
 *            the locator
 */
public CommentBoxLabelEditManager(GraphicalEditPart source, CellEditorLocator locator) {
	super(source, null, locator);
}


/**
 * Cleanup is done here. Any feedback is erased and listeners unhooked.
 */
protected void bringDown() {
	ZoomManager zoomMgr = (ZoomManager)getEditPart().getViewer()
			.getProperty(ZoomManager.class.toString());
	if (zoomMgr != null)
		zoomMgr.removeZoomListener(zoomListener);

	if (actionHandler != null) {
		actionHandler.dispose();
		actionHandler = null;
	}
	if (actionBars != null) {
		restoreSavedActions(actionBars);
		actionBars.updateActionBars();
		actionBars = null;
	}
	
	super.bringDown();
	disposeScaledFont();
}
/**
 * Creates the cell editor on the given composite. The cell editor is
 * created by instantiating the cell editor type passed into this
 * DirectEditManager's constuctor.
 * 
 * @param composite
 *            the composite to create the cell editor on
 * @return the newly created cell editor
 */
protected CellEditor createCellEditorOn(Composite composite) {
	return new TextCellEditor(composite, SWT.MULTI | SWT.WRAP);
}

/**
 * dispose the font those are scaled
 */
private void disposeScaledFont() {
	if (scaledFont != null) {
		scaledFont.dispose();
		scaledFont = null;
	}
}

/**
 * Initializes the cell editor. Subclasses should implement this to set the
 * initial text and add things such as {@link VerifyListener
 * VerifyListeners}, if needed.
 */
protected void initCellEditor() {
	CommentBoxFigure comment = (CommentBoxFigure)getEditPart().getFigure();
	getCellEditor().setValue(comment.getText());
	ZoomManager zoomMgr = (ZoomManager)getEditPart().getViewer()
			.getProperty(ZoomManager.class.toString());
	if (zoomMgr != null) {
		cachedZoom = -1.0;
		updateScaledFont(zoomMgr.getZoom());
		zoomMgr.addZoomListener(zoomListener);
	} else
		getCellEditor().getControl().setFont(comment.getFont());

	actionBars = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.getActiveEditor().getEditorSite().getActionBars();
	saveCurrentActions(actionBars);
	actionHandler = new CellEditorActionHandler(actionBars);
	actionHandler.addCellEditor(getCellEditor());
	actionBars.updateActionBars();
}
/**
 * restores the saved actions
 * @param actionbars
 * 					at actionbars 
 */
private void restoreSavedActions(IActionBars actionBars){
	actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
	actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), paste);
	actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), delete);
	actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), selectAll);
	actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), cut);
	actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), find);
	actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undo);
	actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redo);
}

/**
 * save the current actions
 * @param actionbars
 * 					at actionbars 
 */
private void saveCurrentActions(IActionBars actionBars) {
	copy = actionBars.getGlobalActionHandler(ActionFactory.COPY.getId());
	paste = actionBars.getGlobalActionHandler(ActionFactory.PASTE.getId());
	delete = actionBars.getGlobalActionHandler(ActionFactory.DELETE.getId());
	selectAll = actionBars.getGlobalActionHandler(ActionFactory.SELECT_ALL.getId());
	cut = actionBars.getGlobalActionHandler(ActionFactory.CUT.getId());
	find = actionBars.getGlobalActionHandler(ActionFactory.FIND.getId());
	undo = actionBars.getGlobalActionHandler(ActionFactory.UNDO.getId());
	redo = actionBars.getGlobalActionHandler(ActionFactory.REDO.getId());
}

/**
 * update scaledFonts
 * @param zoom
 * 				at zoom 
 */
private void updateScaledFont(double zoom) {
	if (cachedZoom == zoom)
		return;
	
	Text text = (Text)getCellEditor().getControl();
	Font font = getEditPart().getFigure().getFont();
	
	disposeScaledFont();
	cachedZoom = zoom;
	if (zoom == 1.0)
		text.setFont(font);
	else {
		FontData fd = font.getFontData()[0];
		fd.setHeight((int)(fd.getHeight() * zoom));
		text.setFont(scaledFont = new Font(null, fd));
	}
}

}