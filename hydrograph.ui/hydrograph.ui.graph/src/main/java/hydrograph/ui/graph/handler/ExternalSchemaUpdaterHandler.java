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

package hydrograph.ui.graph.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.engine.ui.util.ImportedSchemaPropagation;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.graph.utility.SubJobUtility;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * External Schema Updater Used by refresh command hooker for updating schema of
 * component.
 * 
 * @author Bitwise
 *
 */
public class ExternalSchemaUpdaterHandler extends AbstractHandler {

	public static final String SCHEMA_FILE_EXTENSION = ".schema";
	public static final String XML_FILE_EXTENSION = ".xml";
	private static final String JOB_FILE_EXTENSION = "job";
	private static final String HYDROGRAPH_PROJECT_NAVIGATOR_ID = "hydrograph.ui.project.structure.navigator";
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(ExternalSchemaUpdaterHandler.class);

	/**
	 * This method update schema of component. for all selectedJobFiles.
	 */
	private void updatedExternalSchema(List<File> selectedJobFiles) {

		LOGGER.debug("Updating external schema");
		for (File file : selectedJobFiles) {

			Container container = null;
			try {
				container = (Container) CanvasUtils.INSTANCE.fromXMLToObject(file.getContents());
			} catch (CoreException e) {
				LOGGER.error("Error while converting job file to container.");
			}
			if (container != null) {
				List<Component> externalSchemaComps = getExternalSchemaComponent(container.getUIComponentList());
				updateExternalSchema(container, externalSchemaComps, file);
			}
		}
	}

	private void updateExternalSchema(Container container, List<Component> externalSchemaComps, File file) {
		for (Component component : externalSchemaComps) {
			Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA);
			// May be the job not open so need to load property file.
			Utils.INSTANCE.loadProperties(file);
			String externalSchemaPath = schema.getExternalSchemaPath();

			if (ParameterUtil.containsParameter(externalSchemaPath, '/')) {
				externalSchemaPath = Utils.INSTANCE.getParamValue(externalSchemaPath);
				if ("Parameter is not Found!".equals(externalSchemaPath)) {
					LOGGER.error("Parameter '" + externalSchemaPath
							+ "' not found for loading external schema. Please check param file.");
					return;
				}
			}
			java.io.File schemaFile = getSchemaFile(externalSchemaPath);
			if (schemaFile != null && schemaFile.exists()) {
				schema.getGridRow().clear();
				ConverterUiHelper converterUiHelper = new ConverterUiHelper(component);
				List<GridRow> gridRows = converterUiHelper.loadSchemaFromExternalFile(schemaFile.getPath(),
						component.getGridRowType());
				schema.setGridRow(gridRows);
				updateComponentOutputSchema(component, gridRows);
			}
		}
		ImportedSchemaPropagation.INSTANCE.initiateSchemaPropagationAfterImport(container, true);
		saveJob(container, file);
		updateSchemaToSubJob(container);
		// Need to re-propagate after sub-job propagation.
		ImportedSchemaPropagation.INSTANCE.initiateSchemaPropagationAfterImport(container, true);
		saveJob(container, file);
	}

	private void updateSchemaToSubJob(Container container) {
		List<SubjobComponent> subjobComponents = getSubJobComonent(container);
		for (SubjobComponent subjobComponent : subjobComponents) {
			File subJobFile = null;
			Container subJobContainer = null;
			SubJobUtility subJobUtility = new SubJobUtility();
			String pathProperty = (String) subjobComponent.getProperties().get(Constants.PATH_PROPERTY_NAME);
			if (StringUtils.isNotBlank(pathProperty)) {
				IPath subJobFilePath = new Path(pathProperty);
				if (SubJobUtility.isFileExistsOnLocalFileSystem(subJobFilePath)) {
					subJobFile = (File) ResourcesPlugin.getWorkspace().getRoot().getFile(subJobFilePath);
					if (subJobFile.exists()) {
						try {
							subJobContainer = (Container) CanvasUtils.INSTANCE
									.fromXMLToObject(subJobFile.getContents());

							for (Component component : subJobContainer.getUIComponentList()) {
								subJobUtility.propogateSchemaToSubjob(subjobComponent, component);
								ImportedSchemaPropagation.INSTANCE
										.initiateSchemaPropagationAfterImport(subJobContainer, true);
							}
							
						} catch (CoreException exception) {
							LOGGER.error("Error while converting job file to container.",exception);
						}
					}

				}
			}
			if (subJobFile != null && subJobContainer != null) {
				removeSchemaFromInputOutputSubJobComponent(subJobContainer);
				saveJob(subJobContainer, subJobFile);
			}
		}
	}

	private void removeSchemaFromInputOutputSubJobComponent(Container subJobContainer) {
		for (Component component : subJobContainer.getUIComponentList()) {
			if (component.getComponentName().equals(Constants.INPUT_SUBJOB)
					|| component.getComponentName().equals(Constants.OUTPUT_SUBJOB)) {
				component.getProperties().remove(Constants.SCHEMA_PROPERTY_NAME);
			}
		}
	}

	private List<SubjobComponent> getSubJobComonent(Container container) {
		List<SubjobComponent> subjobComponents = new ArrayList<>();
		for (Component component : container.getUIComponentList()) {
			if (component instanceof SubjobComponent) {
				subjobComponents.add((SubjobComponent) component);
			}
		}
		return subjobComponents;
	}

	private void updateComponentOutputSchema(Component component, List<GridRow> gridRows) {
		if (UIComponentsConstants.INPUT_CATEGORY.value().equals(component.getCategory())) {
			Map<String, ComponentsOutputSchema> schemaMap = new LinkedHashMap<String, ComponentsOutputSchema>();
			ComponentsOutputSchema componentsOutputSchema = new ComponentsOutputSchema();
			for (GridRow gridRow : gridRows) {
				componentsOutputSchema.addSchemaFields(gridRow);
			}
			schemaMap.put(Constants.FIXED_OUTSOCKET_ID, componentsOutputSchema);
			component.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);
		}
	}

	private void saveJob(Container container, File file) {
		LOGGER.debug("Saving the job");
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		CanvasUtils.INSTANCE.fromObjectToXML(container, outStream);
		try {
			file.setContents(new ByteArrayInputStream(outStream.toByteArray()), true, false, null);
			// Temp Fix if job already open need to reopen it to get
			// updated data (use setInput).
			if (closeEditorIfAlreadyOpen(file.getFullPath(), file.getName())) {
				openJob(file);
			}
			LOGGER.debug("Jon not open");
		} catch (CoreException e) {
			LOGGER.debug("Exception while saving job.");
		}
	}

	private void openJob(File file) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage != null && file.exists()) {
			try {
				if (IDE.openEditor(activePage, file) == null) {
					LOGGER.debug("Enable to open");
				}
			} catch (PartInitException e) {
				LOGGER.debug("Exception while opening job");
			}
		}
	}

	private boolean closeEditorIfAlreadyOpen(IPath jobFilePath, String fileName) {

		String jobPathRelative = StringUtils.removeStart(jobFilePath.toString(), "..");
		jobPathRelative = StringUtils.removeStart(jobPathRelative, "/");
		String jobPathAbsolute = StringUtils.replace(jobPathRelative, "/", "\\");
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
			for (IEditorReference editorRefrence : activePage.getEditorReferences()) {
				if (StringUtils.equals(editorRefrence.getTitleToolTip(), jobPathRelative)
						|| StringUtils.equals(editorRefrence.getTitleToolTip(), jobPathAbsolute)
						|| fileName.equals(editorRefrence.getTitleToolTip())) {
					IEditorPart editor = editorRefrence.getEditor(true);
					if (!activePage.closeEditor(editor, true)) {
						LOGGER.debug("Editor not closed");
					}
					LOGGER.debug("Editor closed");
					return true;
				}
			}
		}
		return false;
	}

	private java.io.File getSchemaFile(String externalSchemaPath) {
		java.io.File schemaFile = null;
		IPath location = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(externalSchemaPath)).getLocation();
		if (location != null) {
			if (!(location.toOSString().endsWith(SCHEMA_FILE_EXTENSION))
					&& !(location.toOSString().endsWith(XML_FILE_EXTENSION))) {
				schemaFile = new java.io.File(location.toOSString().concat(SCHEMA_FILE_EXTENSION));
			} else {
				schemaFile = new java.io.File(location.toOSString());
			}
		}
		return schemaFile;
	}

	private List<Component> getExternalSchemaComponent(List<Component> uiComponentList) {
		List<Component> externalSchemaComps = new ArrayList<>();
		for (Component component : uiComponentList) {
			if (UIComponentsConstants.INPUT_CATEGORY.value().equals(component.getCategory())
					|| UIComponentsConstants.OUTPUT_CATEGORY.value().equals(component.getCategory())) {

				Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA);
				if (schema != null && schema.getIsExternal()) {
					externalSchemaComps.add(component);
				}
			}
		}
		return externalSchemaComps;
	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {

		List<File> selectedJobFile = getSelectedJobFile();
		if (selectedJobFile.size() > 0) {
			updatedExternalSchema(selectedJobFile);
		}
		return null;
	}

	private List<File> getSelectedJobFile() {
		List<File> selectedJobFiles = new ArrayList<>();
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection(HYDROGRAPH_PROJECT_NAVIGATOR_ID);
		if (selection instanceof ITreeSelection) {
			List selectionFiles = ((ITreeSelection) selection).toList();
			selectedJobFiles = getSelectedJobFile(selectionFiles);
		}
		return selectedJobFiles;
	}

	private List<File> getSelectedJobFile(List selectionList) {
		List<File> selectedJobFiles = new ArrayList<>();
		for (Object obj : selectionList) {
			if (obj instanceof File) {
				File selectedFile = (File) obj;
				if (JOB_FILE_EXTENSION.equals(selectedFile.getFileExtension())) {
					selectedJobFiles.add(selectedFile);
				}
			}
		}
		return selectedJobFiles;
	}

}
