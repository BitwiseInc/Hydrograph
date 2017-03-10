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
package hydrograph.ui.expression.editor.javasourceviewerconfiguration;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;

public class HydrographCompletionProposal implements ICompletionProposal  {
        
	    private String fDisplayString;
	    private String fReplacementString;
	    private int fReplacementOffset;
        protected int fReplacementLength;
 	    private int fCursorPosition;
	    private Image fImage;
	    private IContextInformation fContextInformation;
	    private String fAdditionalProposalInfo;
	    private String type;

	    public HydrographCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition) {
	        this(replacementString, replacementOffset, replacementLength, cursorPosition, null, null, null, null);
	    }

	   public HydrographCompletionProposal(String replacementString, int replacementOffset, int replacementLength, int cursorPosition,
	            Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo) {
	        Assert.isNotNull(replacementString);
	        Assert.isTrue(replacementOffset >= 0);
	        Assert.isTrue(replacementLength >= 0);
	        Assert.isTrue(cursorPosition >= 0);

	        fReplacementString = replacementString;
	        fReplacementOffset = replacementOffset;
	        fReplacementLength = replacementLength;
	        fCursorPosition = cursorPosition;
	        fImage = image;
	        fDisplayString = displayString;
	        fContextInformation = contextInformation;
	        fAdditionalProposalInfo = additionalProposalInfo;
	    }

	    public void apply(IDocument document) {
	        try {
	            document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
	        } catch (BadLocationException e) {
	            
	        }
	    }

	    public Point getSelection(IDocument document) {
	        return new Point(fReplacementOffset + fCursorPosition, 0);
	    }

	     public IContextInformation getContextInformation() {
	        return fContextInformation;
	    }

	     public Image getImage() {
	        return fImage;
	    }

	    
	     public String getDisplayString() {
	        if (fDisplayString != null)
	            return fDisplayString;
	        return fReplacementString;
	    }

	    public String getAdditionalProposalInfo() {
	        return fAdditionalProposalInfo;
	    }

	    public String getType() {
	        return this.type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    @Override
	    public String toString() {
	        return fDisplayString;
	    }
}
