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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProcessor;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyGenericTypeProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.expression.editor.sourceviewer.SourceViewer;


@SuppressWarnings("restriction")
public class HydrographJavaCompletionProcessor extends JavaCompletionProcessor{

	public HydrographJavaCompletionProcessor(ContentAssistant assistant, String partition) {
        super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), assistant, partition);
    }
	
	protected List<ICompletionProposal> filterAndSortProposals(List<ICompletionProposal> proposals, IProgressMonitor monitor,
            ContentAssistInvocationContext context) {
		proposals = super.sortProposals(proposals, monitor, context);
        Map<String, ICompletionProposal> completionProposalMap = new HashMap<String, ICompletionProposal>();
        boolean globalFieldsDone = false;
        for (Object object : proposals) {
            ICompletionProposal proposal = (ICompletionProposal) object;
            String displayString = proposal.getDisplayString();
            int indexOfDisplayString = displayString.indexOf("-"); 
            boolean shouldRemove = false;
            if (indexOfDisplayString > 0) {
                if (displayString.substring(indexOfDisplayString + 2, displayString.length()).equals(SourceViewer.getClassName())) {
                    shouldRemove = true;
                }
            }
            
            if (proposal instanceof JavaCompletionProposal) {
                JavaCompletionProposal javaProposal = ((JavaCompletionProposal) proposal);
                if (javaProposal.getJavaElement() instanceof SourceField) {
                    globalFieldsDone = true;
                }
                if (javaProposal.getJavaElement() == null && globalFieldsDone) {
                    shouldRemove = true;
                }
            }
            
            if (proposal instanceof LazyGenericTypeProposal) {
            	LazyGenericTypeProposal javaProposal = ((LazyGenericTypeProposal) proposal);
            	javaProposal.setReplacementString(javaProposal.getQualifiedTypeName());
            	
            }
            
            if (proposal.getDisplayString().startsWith(SourceViewer.VIEWER_CLASS_NAME)) {
                shouldRemove = true;
            }
            if (!shouldRemove) {
                completionProposalMap.put(displayString, proposal);
            }
        }

        List<ICompletionProposal> newProposals = new ArrayList<ICompletionProposal>(completionProposalMap.values());
        Collections.sort(newProposals, new Comparator<ICompletionProposal>() {

            @Override
            public int compare(ICompletionProposal arg0, ICompletionProposal arg1) {
                if (arg1 instanceof HydrographCompletionProposal && (!(arg0 instanceof HydrographCompletionProposal))) {
                    return 1;
                } else if (arg0 instanceof HydrographCompletionProposal && (!(arg1 instanceof HydrographCompletionProposal))) {
                    return -1;
                } else if (arg1 instanceof HydrographCompletionProposal && (arg0 instanceof HydrographCompletionProposal)) {
                    HydrographCompletionProposal a = (HydrographCompletionProposal) arg0;
                    HydrographCompletionProposal b = (HydrographCompletionProposal) arg1;
                    return a.getType().compareTo(b.getType());
                }
                return 0;
            }

        });
		return newProposals;
    }
	
	@Override
    protected ContentAssistInvocationContext createContext(ITextViewer viewer, int offset) 
	{
          	ICompilationUnit compilationUnit = ((SourceViewer) viewer).getCompilatioUnit();
            if (compilationUnit != null) {
           
                CompletionProposalCollector completionProposalCollector = new CompletionProposalCollector(compilationUnit);
                JavaContentAssistInvocationContext invocContext = new JavaContentAssistInvocationContext(viewer, offset,
                		new NullEditorPart());
                
                completionProposalCollector.setInvocationContext(invocContext);
                return invocContext;
            } else {
                return null;
            }
        
    }

   private class NullEditorPart implements IEditorPart {

       
        @Override
        public IEditorInput getEditorInput() {
            return null;
        }

        
        @Override
        public IEditorSite getEditorSite() {
            return null;
        }

        @Override
        public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        }

        
        @Override
        public void addPropertyListener(IPropertyListener listener) {
        }

        @Override
        public void createPartControl(Composite parent) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public IWorkbenchPartSite getSite() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public Image getTitleImage() {
            return null;
        }

        @Override
        public String getTitleToolTip() {
            return null;
        }

        @Override
        public void removePropertyListener(IPropertyListener listener) {
        }

        @Override
        public void setFocus() {
        }

        @Override
        public Object getAdapter(Class adapter) {
            return null;
        }
        @Override
        public void doSave(IProgressMonitor monitor) {
        }
        @Override
        public void doSaveAs() {
        }
        @Override
        public boolean isDirty() {
            return false;
        }
        @Override
        public boolean isSaveAsAllowed() {
            return false;
        }
        @Override
        public boolean isSaveOnCloseNeeded() {
            return false;
        }
    }
}
