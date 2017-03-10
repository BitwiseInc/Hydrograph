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


import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * The Class ComponentLabelFigure.
 * Represents the component label displayed on the component in canvas.
 * 
 * @author Bitwise
 */

public class ComponentLabelFigure extends Figure {


	/** The inner TextFlow **/
	private TextFlow textFlow = new TextFlow();
	private FlowPage flowPage;
	private Font font;

	
	public ComponentLabelFigure() {

	}

	/**
	 * Creates a new LabelFigure with a MarginBorder that is the given size and
	 * a FlowPage containing a TextFlow with the style WORD_WRAP_SOFT.
	 * 
	 * @param borderSize
	 *            the size of the MarginBorder
	 */
	public ComponentLabelFigure(int borderSize) {
		setBorder(new MarginBorder(borderSize));
		flowPage = new FlowPage();

		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
				ParagraphTextLayout.WORD_WRAP_SOFT));
		

		flowPage.add(textFlow);
		flowPage.setHorizontalAligment(PositionConstants.CENTER);

		setLayoutManager(new StackLayout());
		add(flowPage);
		font = new Font( Display.getDefault(), "Arial", 9,
				SWT.NORMAL );
		setFont(font);
		setForegroundColor(ColorConstants.black);
		
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
	 * Sets the text of the TextFlow to the given value.
	 * 
	 * @param newText
	 *            the new text value.
	 */
	public void setText(String newText) {
		textFlow.setText(newText);
		Dimension lineDimensions = TextUtilities.INSTANCE.getStringExtents(textFlow.getText(), getFont());
		if(lineDimensions.width >= ELTFigureConstants.compLabelOneLineLengthLimitForText)
		{
			getBounds().setSize(ELTFigureConstants.compLabelOneLineLengthLimitForText, ELTFigureConstants.componentTwoLineLabelMargin);
		}else if(lineDimensions.width < ELTFigureConstants.compLabelOneLineLengthLimitForText){
			getBounds().setSize(ELTFigureConstants.compLabelOneLineLengthLimitForText, ELTFigureConstants.componentOneLineLabelMargin);
		}
	}
	
	/**
	 * Calls dispose method on Fonts. Called by EditPart.
	 */
	public void disposeFont(){
		if(this.font!=null){
			this.font.dispose();
		}
	}
	
}
