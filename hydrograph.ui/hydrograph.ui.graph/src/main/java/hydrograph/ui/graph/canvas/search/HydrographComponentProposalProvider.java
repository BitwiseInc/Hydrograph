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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;

import hydrograph.ui.common.component.config.Component;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.XMLConfigUtil;

/**
 * HydrographComponentProposalProvider class provides the proposal for content
 * assist.
 * @author Bitwise
 *
 */
public class HydrographComponentProposalProvider implements IContentProposalProvider {

	private ArrayList<IContentProposal> proposalList;
	private PaletteRoot paletteRoot;
	private ComponentDetails componentDetails;
	private String COMMENT_BOX = "Comment Box";

	public HydrographComponentProposalProvider(ComponentSearchUtility componentAssist,
			ArrayList<IContentProposal> proposalList, PaletteRoot paletteRoot) {
		super();
		this.proposalList = proposalList;
		this.paletteRoot = paletteRoot;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {

		proposalList.clear();
		List<PaletteContainer> paletteEntry = paletteRoot.getChildren();
		for (PaletteContainer paletteContainer : paletteEntry) {
			List<PaletteEntry> paletteDrawersList = paletteContainer.getChildren();
			for (PaletteEntry paletteDrawer : paletteDrawersList) {

				if (StringUtils.containsIgnoreCase(
						paletteDrawer.getParent().getLabel() + " : " + paletteDrawer.getLabel(), contents)) {
					String componentName = ((Class) ((CombinedTemplateCreationEntry) paletteDrawer).getTemplate())
							.getSimpleName();
					componentDetails = new ComponentDetails();
					Component component = XMLConfigUtil.INSTANCE.getComponent(componentName);
					componentDetails.setName(componentName);
					componentDetails.setCategoryAndPalletteName(
							paletteDrawer.getParent().getLabel() + " : " + paletteDrawer.getLabel());
					componentDetails.setDescription(component.getDescription());
					componentDetails.setDescriptor(paletteDrawer.getSmallIcon());
					proposalList.add(new ComponentContentProposal(componentDetails));
				}
			}
		}

		componentDetails = new ComponentDetails();
		componentDetails.setName(COMMENT_BOX);
		componentDetails.setCategoryAndPalletteName(COMMENT_BOX);
		componentDetails.setDescription(COMMENT_BOX);
		componentDetails.setDescriptor(getCommentBoxImageDisDescriptor());
		proposalList.add(new ComponentContentProposal(componentDetails));
		return proposalList.toArray(new IContentProposal[0]);
	}

	/**
	 * Provides the image descriptor for Comment box
	 * @return imageDescriptor
	 */
	private ImageDescriptor getCommentBoxImageDisDescriptor() {
		ImageDescriptor imageDescriptor = new ImageDescriptor() {

			@Override
			public ImageData getImageData() {
				return new ImageData(XMLConfigUtil.INSTANCE.CONFIG_FILES_PATH + Constants.COMMENT_BOX_IMAGE_PATH);
			}
		};
		return imageDescriptor;
	}
}
