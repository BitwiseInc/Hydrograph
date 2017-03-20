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

import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.datastructure.ClassDetails;
import hydrograph.ui.expression.editor.datastructure.MethodDetails;
import hydrograph.ui.expression.editor.dialogs.ExpressionEditorDialog;
import hydrograph.ui.expression.editor.repo.ClassRepo;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;

public class HydrographCompletionProposalComputer implements IJavaCompletionProposalComputer {

	private Logger LOGGER = LogFactory.INSTANCE.getLogger(HydrographCompletionProposalComputer.class);
	private static final String CUSTOM_TYPE = "customType";
	private static final String CONTEXT_PREFIX = "context.";

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		String prefix = "";
		try {
			if (context != null) {
				prefix = context.computeIdentifierPrefix().toString();

				String tmpPrefix = "";
				IDocument doc = context.getDocument();
				if ((!prefix.equals("")) || (doc.get().length() == 0)) {
					tmpPrefix = prefix;
				} else {
					int offset = context.getInvocationOffset();
					if (doc.getChar(offset - 1) == '.') {
						tmpPrefix = ".";
						if (offset >= CONTEXT_PREFIX.length()
								&& doc.get(offset - CONTEXT_PREFIX.length(), CONTEXT_PREFIX.length()).equals(
										CONTEXT_PREFIX)) {
							tmpPrefix = CONTEXT_PREFIX;
						}
					}
				}
				prefix = tmpPrefix;

			}
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}

		return computeCompletionProposals(context.getViewer(), prefix, context.getInvocationOffset(), monitor);
	}

	private void filterProposalsOnPrefix(String prefix, List<ICompletionProposal> props) {
		if (prefix != null && prefix.trim().length() > 0) {
			Iterator<ICompletionProposal> iterator = props.iterator();
			String prefixLc = prefix.toLowerCase();

			{
				while (iterator.hasNext()) {
					ICompletionProposal item = (ICompletionProposal) iterator.next();
					String content = item.getDisplayString().toLowerCase(Locale.ENGLISH);
					if (!content.toLowerCase(Locale.ENGLISH).startsWith(prefixLc)) {
						iterator.remove();
					}
				}
			}
		}
	}

	public List<ICompletionProposal> computeCompletionProposals(ITextViewer textViewer, String prefix, int offset,
			IProgressMonitor monitor) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		try {
			int replacementLength = textViewer.getSelectedRange().y;
			if (replacementLength == 0) {
				replacementLength = prefix.length();
			}
			String displayLabel;
			String replacementString;
			List<ClassDetails> classList = ClassRepo.INSTANCE.getClassList();
			Image image=ImagePathConstant.INTELLISENCE_IMAGE.getImageFromRegistry();
			
			for (ClassDetails classDetails : classList) {
//				if (!classDetails.isUserDefined()) {
					List<MethodDetails> methodlist = classDetails.getMethodList();
					for (MethodDetails methodDetails : methodlist) {
						displayLabel = classDetails.getcName() + Constants.DOT + methodDetails.getSignature();
						replacementString = methodDetails.getPlaceHolder();

						HydrographCompletionProposal customProposal=new HydrographCompletionProposal(replacementString,offset-prefix.length(),replacementLength,replacementString.length(),
								image,displayLabel,null,null);
			        	customProposal.setType(CUSTOM_TYPE);
			        	proposals.add(customProposal);
					}
//				}
			}
			addAvailableFieldsProposals(textViewer,image,proposals,prefix,offset,replacementLength);
		} catch (RuntimeException exception) {
			LOGGER.error("Error occurred while building custom proposals", exception);
		}
		filterProposalsOnPrefix(prefix, proposals);
		System.gc();
		return proposals;
	}
	
	
	@SuppressWarnings("unchecked")
	private void addAvailableFieldsProposals(ITextViewer textViewer,Image image, List<ICompletionProposal> proposals,String prefix, int offset,int replacementLength) {
		Map<String,Class<?>> fieldMap=(Map<String, Class<?>>)textViewer.getTextWidget().getData(ExpressionEditorDialog.FIELD_DATA_TYPE_MAP); 
		
		for(Entry<String, Class<?>> entry:fieldMap.entrySet()){
			String display = entry.getKey()+SWT.SPACE+Constants.DASH+entry.getValue().getSimpleName();
			String replacementString=SWT.SPACE+entry.getKey()+SWT.SPACE;
			HydrographCompletionProposal customProposal=new HydrographCompletionProposal(replacementString,offset-prefix.length(),replacementLength,replacementString.length(),
					image,display,null,null);
        	customProposal.setType(CUSTOM_TYPE);
        	
        	proposals.add(customProposal);
		}
	}

	@Override
	public void sessionStarted() {

	}

	@Override
	public List<IContextInformation> computeContextInformation(
			ContentAssistInvocationContext paramContentAssistInvocationContext, IProgressMonitor paramIProgressMonitor) {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void sessionEnded() {

	}
}
