/********************************************************************************
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
 ******************************************************************************/
package hydrograph.ui.graph.canvas.search;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * SearchCanvasLabelProvider class provides images and description of Components
 * @author Bitwise
 *
 */
public class SearchCanvasLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object element) {
		if(element !=null && element instanceof ComponentContentProposal){
			ComponentContentProposal proposal = (ComponentContentProposal) element;
			ComponentDetails componentDetails = proposal.getComponentDetails();
			if(componentDetails != null && componentDetails.getDescriptor() !=null){
				return (Image)componentDetails.getDescriptor().createImage();
			}
		}
		return super.getImage(element);
	}
	
	@Override
	public String getText(Object element) {
		
		if(element !=null && element instanceof ComponentContentProposal){
			ComponentContentProposal proposal = (ComponentContentProposal) element;
			ComponentDetails componentDetails = proposal.getComponentDetails();
			if(componentDetails != null && componentDetails.getName() !=null){
				return componentDetails.getCategoryAndPalletteName();
			}
		}
		return super.getText(element);
	}
}
