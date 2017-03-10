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
package hydrograph.ui.graph.figure;

import java.awt.MouseInfo;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.interfaces.tooltip.ComponentCanvas;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.graph.model.CommentBox;

/**
 * A Figure with a bent corner and an embedded TextFlow within a FlowPage that contains text.
 * 
 * @author Bitwise
 * 
 */
public class CommentBoxFigure extends BentCornerFigure {

	/** The inner TextFlow **/
	private TextFlow textFlow;
	private Font font;
	private Point locationOnWorkbench;
	private Dimension defaultCommentBoxSize;

	/**
	 * Creates a new CommentBoxFigure with a default MarginBorder size of DEFAULT_CORNER_SIZE - 3 and a FlowPage
	 * containing a TextFlow with the style WORD_WRAP_SOFT.
	 */
	public CommentBoxFigure() {
		this(BentCornerFigure.DEFAULT_CORNER_SIZE - 3);
		setInitialColor();
		defaultCommentBoxSize = new Dimension(300, 60);
		addCommentBoxListener();
		
	}

	/**
	 * Creates a new CommentBoxFigure with a MarginBorder that is the given size and a FlowPage containing a TextFlow
	 * with the style WORD_WRAP_SOFT.
	 * 
	 * @param borderSize
	 *            the size of the MarginBorder
	 */
	public CommentBoxFigure(int borderSize) {
		setBorder(new MarginBorder(5));
		FlowPage flowPage = new FlowPage();

		textFlow = new TextFlow();

		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));

		flowPage.add(textFlow);

		setLayoutManager(new StackLayout());
		add(flowPage);
		font = new Font(Display.getDefault(), "Arial", 9, SWT.NORMAL);
		setFont(font);
		setForegroundColor(ColorConstants.black);
		setOpaque(false);

	}

	/**
	 * 
	 * Returns instance of {@link ComponentCanvas} of commentbox
	 * 
	 * @return {@link ComponentCanvas}
	 */
	public ComponentCanvas getComponentCanvas() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor() instanceof ComponentCanvas)
			return (ComponentCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
		else
			return null;
	}

	private void addCommentBoxListener() {
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Nothing todo
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				Point figureMouseLocation = new Point(arg0.x, arg0.y);
				locationOnWorkbench = getFigureLocationOnWorkbench(figureMouseLocation);
			}

			@Override
			public void mouseDoubleClicked(MouseEvent arg0) {
				// Nothing todo. this method never gets called
			}
		});
	}

	/**
	 * 
	 * Returns location of Figure on active workbench
	 * 
	 * @return
	 */
	public Point getLocationOnWorkbench() {
		return locationOnWorkbench;
	}

	/**
	 * 
	 * Computes location of comment box figure relative to workbench
	 * 
	 * @param figureMouseLocation
	 * @return
	 */
	private Point getFigureLocationOnWorkbench(Point figureMouseLocation) {

		int subtractFromMouseX, addToMouseY;
		java.awt.Point workbenchAWTMouseLocation = MouseInfo.getPointerInfo().getLocation();
		Point workbenchMouseLocation = new Point(workbenchAWTMouseLocation.x, workbenchAWTMouseLocation.y);

		subtractFromMouseX = figureMouseLocation.x - getBounds().x;
		addToMouseY = getBounds().y - figureMouseLocation.y;

		return new org.eclipse.swt.graphics.Point((workbenchMouseLocation.x - subtractFromMouseX),
				(workbenchMouseLocation.y + addToMouseY));
	}

	/**
	 * Returns the text inside the TextFlow.
	 * 
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return textFlow.getText();
	}

	/**
	 * Sets the initial color for border of comment box
	 */
	private void setInitialColor() {
		CustomColorRegistry.INSTANCE.getColorFromRegistry( ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[0],
				ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[1], ELTColorConstants.COMPONENT_BORDER_SELECTED_RGB[2]);
	}

	/**
	 * Sets the text of the TextFlow to the given value.
	 * 
	 * @param newText
	 *            the new text value.
	 */
	public void setText(String newText, CommentBox commentBoxModel) {		
		textFlow.setText(newText);
		
		
		//TODO - Please do not remove this code. Need to discuss on auto resize of comment box
		/*Dimension preferredCommentBoxSize = getPreferredSize();
		Dimension newCoomentBoxSize = defaultCommentBoxSize.getCopy();
				
		if(preferredCommentBoxSize.height > defaultCommentBoxSize.height){
			newCoomentBoxSize.setHeight(preferredCommentBoxSize.height);
		}
		
		if(preferredCommentBoxSize.width > defaultCommentBoxSize.width){
			newCoomentBoxSize.setWidth(preferredCommentBoxSize.width + 10);
		}

		commentBoxModel.setSize(newCoomentBoxSize);
		setSize(newCoomentBoxSize);*/
	}

}
