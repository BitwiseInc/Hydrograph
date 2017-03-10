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

package hydrograph.ui.dataviewer.find;

import hydrograph.ui.logging.factory.LogFactory;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;


/**
 * The Class StyledTextEventListener
 * This Listener is used in View Data Find window text area. 
 * @author Bitwise
 *
 */
public class StyledTextEventListener {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(StyledTextEventListener.class);
	public final static StyledTextEventListener INSTANCE = new StyledTextEventListener();
	
	private StyledTextEventListener() {
	}
	
	/**
	 *  The function will move cursor in reverse direction.
	 * @param styledText
	 * @param text
	 * @param prevLineIndex
	 * @param nextLineIndex
	 * @return int[]
	 */
	public int[] prevButtonListener(StyledText styledText, String text, int prevLineIndex, int nextLineIndex){
		logger.debug("StyledText prev button selected");
		if(prevLineIndex == 0){
			prevLineIndex = styledText.getText().length() - 1;
		}
		int lastIndex = StringUtils.lastIndexOf(StringUtils.lowerCase(styledText.getText()), StringUtils.lowerCase(text), prevLineIndex-1);
		
		if(lastIndex < 0 ){
			prevLineIndex = styledText.getText().length() - 1;
			prevLineIndex= StringUtils.lastIndexOf(StringUtils.lowerCase(styledText.getText()), StringUtils.lowerCase(text), prevLineIndex-1);
			styledText.setSelection(prevLineIndex);
			setStyledRange(styledText, prevLineIndex, text.length(), null, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
			nextLineIndex = prevLineIndex + 1;
			return new int[]{prevLineIndex,nextLineIndex};
		}else{
			setStyledRange(styledText, lastIndex, text.length(), null, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
			styledText.setSelection(lastIndex);
			prevLineIndex = lastIndex;
			nextLineIndex = lastIndex + 1;
			styledText.redraw();
		}
		return new int[]{prevLineIndex,nextLineIndex};
	}
	
	/**
	 * The function will move the cursor in forward direction.
	 * @param styledText
	 * @param text
	 * @param prevLineIndex
	 * @param nextLineIndex
	 * @return int[]
	 */
	public int[] nextButtonListener(StyledText styledText, String text, int prevLineIndex, int nextLineIndex){
		logger.debug("StyledText next button selected");
		int txtIndex = StringUtils.indexOf(StringUtils.lowerCase(styledText.getText()), StringUtils.lowerCase(text), nextLineIndex);
		
		if(txtIndex < 0){
			nextLineIndex = 0;
			prevLineIndex =  StringUtils.indexOf(StringUtils.lowerCase(styledText.getText()), StringUtils.lowerCase(text), nextLineIndex);
			nextLineIndex = prevLineIndex + 1;
			styledText.setSelection(prevLineIndex);
			setStyledRange(styledText, prevLineIndex, text.length(), null, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
			return new int[]{prevLineIndex,nextLineIndex};
		}else{
			setStyledRange(styledText, txtIndex, text.length(), null, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
			styledText.setSelection(txtIndex);
			prevLineIndex = txtIndex;
			nextLineIndex = txtIndex+1;
			styledText.redraw();
		}
		return new int[]{prevLineIndex,nextLineIndex};
	}
	
	/**
	 * The function will change selected text background color.
	 * @param styledText
	 * @param text
	 * @param foreground
	 * @param background
	 */
	public void allButtonListener(StyledText styledText, String text, Color foreground, Color background, Label label){
		logger.debug("StyledText All button selected");
		int index = 0;
		int recordCount = 0;
		if(styledText == null){return;}
		for(;index < styledText.getText().length();){
			  int lastIndex = StringUtils.indexOf(StringUtils.lowerCase(styledText.getText()), StringUtils.lowerCase(text), index);
			  
			  if(lastIndex < 0){return;}
			  else{
				  setStyledRange(styledText, lastIndex, text.length(), null, background);
				  index = lastIndex + 1;
				  recordCount++ ;
				  label.setVisible(true);
				  label.setText("Matching count - " + recordCount);
			  }
		  }
	}
	
	/**
	 * The function will change selected text foreground and background color.
	 * @param styledText
	 * @param startIndex
	 * @param textLength
	 * @param foreground
	 * @param background
	 */
	private void setStyledRange(StyledText styledText, int startIndex, int textLength, Color foreground, Color background){
		StyleRange[] prevRanges = new StyleRange[1];
		prevRanges[0] = new StyleRange(startIndex, textLength, foreground, background);
		styledText.replaceStyleRanges(startIndex, textLength, prevRanges);
	}
}
