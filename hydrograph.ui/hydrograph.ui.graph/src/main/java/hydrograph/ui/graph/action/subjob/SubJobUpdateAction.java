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

 
package hydrograph.ui.graph.action.subjob;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.datastructures.tooltip.PropertyToolTipInformation;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.engine.ui.util.SubjobUiConverterUtil;
import hydrograph.ui.graph.action.PasteAction;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobStatus;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.utility.SubJobUtility;
import hydrograph.ui.propertywindow.widgets.utility.SubjobUtility;


/**
 * The Class SubJobUpdateAction use to update sub graph property.
 * 
 * @author Bitwise
 */
public class SubJobUpdateAction extends SelectionAction {

	PasteAction pasteAction;

	ComponentEditPart componentEditPart;

	/**
	 * Instantiates a new cut action.
	 * 
	 * @param part
	 *            the part
	 * @param action
	 *            the action
	 */
	public SubJobUpdateAction(IWorkbenchPart part, IAction action) {
		super(part);
		this.pasteAction = (PasteAction) action;
		setLazyEnablementCalculation(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
	 */
	@Override
	protected void init() {
		super.init();

		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setText(Constants.SUBJOB_UPDATE);
		setId(Constants.SUBJOB_UPDATE);
		setHoverImageDescriptor(getImageDisDescriptor());
		setImageDescriptor(getImageDisDescriptor());
		setDisabledImageDescriptor(getImageDisDescriptor());
		setEnabled(false);
	}

	private ImageDescriptor getImageDisDescriptor() {
		ImageDescriptor imageDescriptor = new ImageDescriptor() {

			@Override
			public ImageData getImageData() {
				return new ImageData(XMLConfigUtil.INSTANCE.CONFIG_FILES_PATH+"/icons/refresh.png");
			}
		};
		return imageDescriptor;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	@Override
	protected boolean calculateEnabled() {
		List<Object> selectedObjects = getSelectedObjects();
		if (selectedObjects != null && !selectedObjects.isEmpty() && selectedObjects.size() == 1) {
			for (Object obj : selectedObjects) {
				if (obj instanceof ComponentEditPart) {
					if (Constants.SUBJOB_COMPONENT.equalsIgnoreCase(((ComponentEditPart) obj).getCastedModel()
							.getComponentName())){
						ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
						String currentJobName = editor.getActiveProject() + "." + editor.getJobName();
						Job job = editor.getJobInstance(currentJobName);
						if (job != null && ((StringUtils.equalsIgnoreCase(job.getJobStatus(),JobStatus.RUNNING)) || (StringUtils.equalsIgnoreCase(job.getJobStatus(),JobStatus.SSHEXEC)))){
							return false;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * Updates selected subjob property from subjob's job file.
	 */
	@Override
	public void run() {
		String filePath=null;
		Component selectedSubjobComponent = null;
		componentEditPart=(ComponentEditPart) getSelectedObjects().get(0);
		if (getSelectedObjects() != null && !getSelectedObjects().isEmpty() && getSelectedObjects().size() == 1) {
			selectedSubjobComponent = componentEditPart.getCastedModel();
			if (StringUtils.equals(Constants.SUBJOB_COMPONENT, selectedSubjobComponent.getComponentName()) && selectedSubjobComponent.getProperties().get(Constants.PATH_PROPERTY_NAME)!=null) {
				filePath=(String) selectedSubjobComponent.getProperties().get(Constants.PATH_PROPERTY_NAME);
				SubJobUtility subJobUtility=new SubJobUtility();
				Container container=subJobUtility.updateSubjobPropertyAndGetSubjobContainer(null,filePath, selectedSubjobComponent,false);
				SubjobUtility.INSTANCE.showOrHideErrorSymbolOnComponent(container,selectedSubjobComponent);
				
				//Hide the tooltip message on subjob component
				PropertyToolTipInformation information = subJobUtility.createPropertyToolTipInformation("", Constants.HIDE_TOOLTIP);
				selectedSubjobComponent.getTooltipInformation().put(Constants.SUBJOB_TOOLTIP_INFO, information);
				
				componentEditPart.changePortSettings();
				componentEditPart.updateComponentStatus();
				componentEditPart.refresh();
				ELTGraphicalEditor editor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				editor.setDirty(true);
				
			}
		}
	}
}
