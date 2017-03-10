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
package hydrograph.ui.expression.editor.styletext;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.sourceviewer.SourceViewer;


public class ExpressionEditorStyledText extends StyledText{

	private SourceViewer viewer;
	public static final String RETURN_STATEMENT = "\t\treturn \n";
	
    public ExpressionEditorStyledText(Composite parent, int style, SourceViewer viewer) {
        super(parent, style);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,255,255));
        this.viewer = viewer;
       
    }

    @Override
    public String getText() {
        IRegion region = viewer.getViewerRegion();
        String text=Constants.EMPTY_STRING;
        try {
            text = viewer.getDocument().get(region.getOffset(), region.getLength());
        } catch (BadLocationException e) {
            text = super.getText();
        }
        return getExpressionText(text);
    }

    private String getExpressionText(String expressionText) {
		StringBuffer buffer=new StringBuffer(expressionText);
		int startIndex=buffer.indexOf(RETURN_STATEMENT);
		if(startIndex>-1){
			buffer.delete(0, startIndex);
			buffer.delete(0, buffer.indexOf("\n"));
		}
		return StringUtils.trim(buffer.toString());
	}
    
    @Override
    public void setText(String text) {
    	super.setText(text);
        if (viewer.getUndoManager() != null) {
            viewer.getUndoManager().reset();
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.swt.custom.StyledText#insert(java.lang.String)
     */
    @Override
	public void insert(String string) {
		if (!StringUtils.startsWith(StringUtils.trim(string),Messages.CANNOT_SEARCH_INPUT_STRING)) {
			super.insert(string);
			this.setFocus();
			this.setSelection(this.getText().length() + 100);
		}
	}

}
