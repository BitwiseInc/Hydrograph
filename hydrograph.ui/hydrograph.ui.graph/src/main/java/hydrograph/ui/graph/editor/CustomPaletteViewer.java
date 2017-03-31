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

 
package hydrograph.ui.graph.editor;

import hydrograph.ui.common.component.config.CategoryType;
import hydrograph.ui.common.component.config.Component;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.graph.model.processor.DynamicClassProcessor;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.xml.sax.SAXException;


/**
 *Creates Custom Palette Viewer 
 * @author Bitwise
 *
 */
public class CustomPaletteViewer extends PaletteViewer {
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(CustomPaletteViewer.class);
	private Label label;

	/**
	 * create searchTextBox and show components according to text entered in search box
	 * @param parent
	 * @param paletteRoot
	 * @param editor
	 * @return Control
	 */
	public Control creatSearchTextBox(Composite parent, final PaletteRoot paletteRoot, final ELTGraphicalEditor editor) {
		final Map<String, PaletteDrawer> categoryPaletteContainer = new HashMap<>();
		Composite container = createCompositeForSearchTextBox(parent);
		final Text text = createSearchTextBox(container,SWT.BORDER);
		try {
			final List<Component> componentsConfig = XMLConfigUtil.INSTANCE.getComponentConfig();
			refreshThePaletteBasedOnSearchString(paletteRoot, editor, categoryPaletteContainer, text,
					componentsConfig, container);

		} catch (RuntimeException|SAXException|IOException exception) {
			LOGGER.error(exception.getMessage(),exception);
		}
		return container;
	}

	private void refreshThePaletteBasedOnSearchString(final PaletteRoot paletteRoot,
			final ELTGraphicalEditor editor, final Map<String, PaletteDrawer> categoryPaletteContainer,
			final Text text, final List<Component> componentsConfig, final Composite container) {
		text.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				boolean matchFound = false;
				final List<Component> matchingComponents = new ArrayList<>();
				paletteRoot.getChildren().clear();
				String currentText = ((Text) e.widget).getText();
				createPaletteContainers(paletteRoot, categoryPaletteContainer, editor);
				String searchedString = (currentText.substring(0, e.start) + e.text + currentText.substring(e.end))
						.toUpperCase();
					if (StringUtils.isEmpty(searchedString)) {
						showAllContainers(paletteRoot, editor, categoryPaletteContainer, componentsConfig);
					} else {
						showOpenPaletteContainers(paletteRoot.getChildren());
						matchFound = checkSearchedComponentFoundInPalette(editor, categoryPaletteContainer, componentsConfig,
								 matchingComponents, searchedString);
						showMessageWhenComponentNotFound(container, matchFound);
						showMatchingContainers(paletteRoot, categoryPaletteContainer, matchingComponents);
					}
			}
		});
	}
	
	private void showMessageWhenComponentNotFound(final Composite container, boolean matchFound) {
		if (!matchFound) {
			if (label != null)
				label.dispose();
			label = new Label(container, SWT.LEFT);
			label.setText("No component found");
		}
	}

	private boolean checkSearchedComponentFoundInPalette(final ELTGraphicalEditor editor,
			final Map<String, PaletteDrawer> categoryPaletteContainer, final List<Component> componentsConfig,
			List<Component> matchingComponents, String searchedString) {
		boolean matchFound = false;
		for (Component componentConfig : componentsConfig) {
			String componentName = componentConfig.getNameInPalette().toUpperCase();
			if (Constants.UNKNOWN_COMPONENT.equalsIgnoreCase(componentConfig.getName()))
				continue;
			if (componentName.contains(searchedString.trim())) {
				CombinedTemplateCreationEntry component = getComponentToAddInContainer(editor, componentConfig);
				categoryPaletteContainer.get(componentConfig.getCategory().name()).add(component);
				matchingComponents.add(componentConfig);
				matchFound = true;
				if (label != null) {
					label.dispose();

				}
			}
		}
		return matchFound;
	}

	private void showAllContainers(final PaletteRoot paletteRoot, final ELTGraphicalEditor editor,
			final Map<String, PaletteDrawer> categoryPaletteContainer, final List<Component> componentsConfig) {
		for (Component componentConfig : componentsConfig) {
			if (Constants.UNKNOWN_COMPONENT.equalsIgnoreCase(componentConfig.getName()))
				continue;
			CombinedTemplateCreationEntry component = getComponentToAddInContainer(editor, componentConfig);
			
			categoryPaletteContainer.get(componentConfig.getCategory().name()).add(component);
			showClosedPaletteContainersWhenSearchTextBoxIsEmpty(paletteRoot.getChildren());
		}
		if (label != null) {
			label.dispose();
		}
	}

	private void showClosedPaletteContainersWhenSearchTextBoxIsEmpty(List list) {
		for (int i = 0; i < list.size(); i++) {
			PaletteDrawer paletteDrawer = (PaletteDrawer) list.get(i);
			paletteDrawer.setInitialState(PaletteDrawer.INITIAL_STATE_CLOSED);
			paletteDrawer.setVisible(true);
		}
	}
	private void showOpenPaletteContainers(List list) {
		for (int i = 0; i < list.size(); i++) {
			PaletteDrawer paletteDrawer = (PaletteDrawer) list.get(i);
			paletteDrawer.setInitialState(PaletteDrawer.INITIAL_STATE_OPEN);
			paletteDrawer.setVisible(true);
		}
	}

	private void showMatchingContainers(PaletteRoot paletteRoot,
			Map<String, PaletteDrawer> categoryPaletteContainer, List<Component> matchingComponents) {
		List<PaletteContainer> children = paletteRoot.getChildren();
		for (PaletteContainer paletteContainer : children) {
			paletteContainer.setVisible(false);

		}
		for (Component component : matchingComponents) {
			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).equals(categoryPaletteContainer.get(component.getCategory().name()))) {
					children.get(i).setVisible(true);
				}
			}
		}

	}

	private void createPaletteContainers(PaletteRoot paletteRoot, Map<String, PaletteDrawer> categoryPaletteContainer,
			ELTGraphicalEditor eLEtlGraphicalEditor) {
		for (CategoryType category : CategoryType.values()) {
			if (Constants.UNKNOWN_COMPONENT_CATEGORY.equalsIgnoreCase(category.name()))
				continue;
			PaletteDrawer paletteDrawer = eLEtlGraphicalEditor.createPaletteContainer(category.name());
			eLEtlGraphicalEditor.addContainerToPalette(paletteRoot, paletteDrawer);
			categoryPaletteContainer.put(category.name(), paletteDrawer);
		}
	}

	private CombinedTemplateCreationEntry getComponentToAddInContainer(ELTGraphicalEditor eLEtlGraphicalEditor,
			Component componentConfig) {
		Class<?> clazz = DynamicClassProcessor.INSTANCE.createClass(componentConfig);

		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(componentConfig.getNameInPalette(),
				null, clazz, new SimpleFactory(clazz),
				ImageDescriptor.createFromURL(eLEtlGraphicalEditor.prepareIconPathURL(componentConfig
						.getPaletteIconPath())), ImageDescriptor.createFromURL(eLEtlGraphicalEditor
						.prepareIconPathURL(componentConfig.getPaletteIconPath())));
		return component;
	}

	private Composite createCompositeForSearchTextBox(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.type = SWT.VERTICAL;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.spacing = 7;
		container.setLayout(layout);
		return container;
	}

	private Text createSearchTextBox(Composite container,int border) {
		Text text = new Text(container, border);
		text.setToolTipText("Enter component name");
		text.setMessage("Search component");
		return text;
	}

	public LightweightSystem getLightweightSys() {
		return getLightweightSystem();
	}

	public void setFigureCanvas(FigureCanvas canvas) {
		setControl(canvas);
		callHookRootFigure();
	}


	private void callHookRootFigure() {
		if (getFigureCanvas() == null)
			return;
		if (((GraphicalEditPart) getRootEditPart()).getFigure() instanceof Viewport) {
			getFigureCanvas().setViewport((Viewport) ((GraphicalEditPart) getRootEditPart()).getFigure());
		} else {
			getFigureCanvas().setContents(((GraphicalEditPart) getRootEditPart()).getFigure());
		}
	}
}
