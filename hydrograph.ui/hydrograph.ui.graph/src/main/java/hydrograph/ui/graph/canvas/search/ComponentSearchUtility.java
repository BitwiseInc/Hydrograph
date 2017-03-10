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
import java.util.Iterator;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.slf4j.Logger;

import hydrograph.ui.common.component.config.Component;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.graph.command.CommentBoxCommand;
import hydrograph.ui.graph.command.ComponentCreateCommand;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.processor.DynamicClassProcessor;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * CanvasComponentUtility class creates the search box with content assist
 * @author Bitwise
 */
public class ComponentSearchUtility {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ComponentSearchUtility.class);
	private GraphicalViewer graphicViewer;
	private Control graphicControl;
	private Text assistText;
	private char triggerChar;
	private ContentProposalAdapter contentProposalAdapter;
	private IBindingService bindingService = null;
	private boolean isKeyFilterEnabled = true;
	private static ArrayList<IContentProposal> proposalList;
	private PaletteRoot paletteRoot;
	private String LABEL = "Label";
	private String COMMENT_BOX = "Comment Box";
	private ELTGraphicalEditor editor;

	public ComponentSearchUtility() {
		proposalList = new ArrayList<IContentProposal>();
		Object service = PlatformUI.getWorkbench().getService(IBindingService.class);
		if (service != null && service instanceof IBindingService) {
			bindingService = (IBindingService) service;
			isKeyFilterEnabled = bindingService.isKeyFilterEnabled();
		}
	}

	/**
	 * Creation of content assist
	 * @param event
	 * @param viewer
	 * @param paletteRoot
	 */
	public void showComponentCreationOnCanvas(KeyEvent event, GraphicalViewer viewer, PaletteRoot paletteRoot) {
		this.graphicViewer = viewer;
		this.graphicControl = viewer.getControl();
		this.triggerChar = event.character;
		this.paletteRoot = paletteRoot;
		Point cursorRelativePosition = calculatePosition();
		if (cursorRelativePosition == null) {
			return;
		}

		disposeAssistText();

		createAssistText(cursorRelativePosition);

		initializeListneres();

		activateAssist(triggerChar);

	}

	/**
	 * @param triggerChar
	 */
	private void activateAssist(char triggerChar) {
		assistText.setText(triggerChar + "");
		assistText.setSelection(assistText.getText().length());

		Event event = new Event();
		event.character = triggerChar;
		assistText.notifyListeners(SWT.KeyDown, event);
		assistText.notifyListeners(SWT.Modify, event);
	}

	/**
	 * Added listeners
	 */
	private void initializeListneres() {

		assistText.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					disposeAssistText();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		assistText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (!contentProposalAdapter.isProposalPopupOpen()) {
					disposeAssistText();
				}
			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		contentProposalAdapter.addContentProposalListener(new IContentProposalListener2() {

			@Override
			public void proposalPopupOpened(ContentProposalAdapter adapter) {

			}

			@Override
			public void proposalPopupClosed(ContentProposalAdapter adapter) {
				if (assistText != null && !assistText.isFocusControl()) {
					disposeAssistText();
				}
			}
		});

		contentProposalAdapter.addContentProposalListener(new IContentProposalListener() {

			@Override
			public void proposalAccepted(IContentProposal proposal) {
				acceptProposal();
			}
		});
	}

	
	private void acceptProposal() {

		String componentName = assistText.getText().trim();
		Iterator<IContentProposal> iter = proposalList.iterator();
		ComponentDetails componentDetails = null;
		while (iter.hasNext()) {
			IContentProposal proposal = iter.next();
			if (proposal instanceof ComponentContentProposal && componentName.equalsIgnoreCase(proposal.getLabel())) {
				componentDetails = ((ComponentContentProposal) proposal).getComponentDetails();
				break;
			}

		}
		acceptProposal(componentDetails);
	}

	
	private void acceptProposal(ComponentDetails componentDetails) {
		Point componentLocation = assistText.getLocation();
		componentLocation.y += assistText.getLineHeight();
		disposeAssistText();
		Object createNode = createComponent(componentDetails, componentLocation);
		selectComponent(createNode);
	}

	/**
	 * Select the component present on Editor
	 * @param createdNode
	 */
	private void selectComponent(Object createdNode) {
		Object nodePart = graphicViewer.getEditPartRegistry().get(createdNode);
		if (nodePart != null) {
			graphicViewer.select((EditPart) nodePart);
			if(OSValidator.isMac()){
				graphicViewer.getControl().forceFocus();
			}
		}
	}

	/**
	 * Creation of components and comment box
	 * @param componentDetails
	 * @param componentLocation
	 * @return
	 */
	private Object createComponent(ComponentDetails componentDetails, Point componentLocation) {

		Event event = new Event();
		event.x = componentLocation.x;
		event.y = componentLocation.y;
		event.button = 1;
		event.count = 1;
		event.widget = graphicControl;
		Object newInstance = null;
		if (componentDetails != null) {
			if (componentDetails.getName().equalsIgnoreCase(COMMENT_BOX)) {
				newInstance	= createCommentBox(event);
			} else {
				newInstance = createComponent(componentDetails, event, newInstance);
			}
			if (bindingService != null) {
				bindingService.setKeyFilterEnabled(true);
			}
		}
		return newInstance;
	}

	private Object createComponent(ComponentDetails componentDetails, Event event,
			Object newInstance) {
		Component component = XMLConfigUtil.INSTANCE.getComponent(componentDetails.getName());
		Container container = (Container) graphicViewer.getContents().getModel();
		Class<?> class1 = DynamicClassProcessor.INSTANCE.getClazz(component.getName());
		ComponentCreateCommand componentCreateCommand;
		try {
			newInstance = (hydrograph.ui.graph.model.Component) class1.newInstance();
			checkComponentCoordinates(event, container);
			componentCreateCommand = new ComponentCreateCommand((hydrograph.ui.graph.model.Component)newInstance, container,
					new Rectangle(event.x, event.y, 0, 0));
			componentCreateCommand.execute();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Component cannot be created:" + e);
			WidgetUtility.createMessageBox("Component cannot be created", Messages.ERROR, SWT.ICON_ERROR);
		}
		return newInstance;
	}

	private Object createCommentBox(Event event) {
		CommentBox commentBox = null;
		editor = (ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		if (editor != null) {
			Container container = editor.getContainer();
			checkComponentCoordinates(event, container);
			org.eclipse.draw2d.geometry.Point point = new org.eclipse.draw2d.geometry.Point(event.x, event.y);
			commentBox = new CommentBox(LABEL);
			commentBox.setSize(new Dimension(300, 60));
			commentBox.setLocation(point);
			CommentBoxCommand command = new CommentBoxCommand(commentBox, LABEL, container);
			command.execute();
		}
		return commentBox;
	}

	/**
	 * Check the coordinates for Component creation
	 * @param event
	 * @param container
	 */
	private void checkComponentCoordinates(Event event, Container container) {
		boolean isCordinateChanged = false;
		for (Object component : container.getChildren()) {
			if (component instanceof hydrograph.ui.graph.model.Component) {
				if (((hydrograph.ui.graph.model.Component) component).getLocation().x == event.x
						&& ((hydrograph.ui.graph.model.Component) component).getLocation().y == event.y) {
					event.x = event.x + 20;
					event.y = event.y + 20;
					isCordinateChanged = true;
				}
			} else if (component instanceof hydrograph.ui.graph.model.CommentBox) {
				if (((hydrograph.ui.graph.model.CommentBox) component).getLocation().x == event.x
						&& ((hydrograph.ui.graph.model.CommentBox) component).getLocation().y == event.y) {
					event.x = event.x + 20;
					event.y = event.y + 20;
					isCordinateChanged = true;
				}
			}
		}
		if (!isCordinateChanged) {
			return;
		} else {
			checkComponentCoordinates(event, container);
		}
	}

	/**
	 * Dispose the Content Assist Search box
	 */
	private void disposeAssistText() {
		if (assistText != null && !assistText.isDisposed()) {
			assistText.dispose();
		}
		assistText = null;
		if (bindingService != null) {
			bindingService.setKeyFilterEnabled(isKeyFilterEnabled);
		}

	}

	/**
	 * Creation of Search box
	 * @param cursorRelativePosition
	 */
	private void createAssistText(Point cursorRelativePosition) {
		if (bindingService != null) {
			bindingService.setKeyFilterEnabled(false);
		}
		createAssitTextBox(cursorRelativePosition);

		createContentProposal();
	}

	private void createContentProposal() {
		HydrographComponentProposalProvider hydrographComponentProposalProvider = new HydrographComponentProposalProvider(
				this, proposalList, paletteRoot);
		contentProposalAdapter = new ContentProposalAdapter(assistText, new TextContentAdapter(),
				hydrographComponentProposalProvider, null, null);
		contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		contentProposalAdapter.setLabelProvider(new SearchCanvasLabelProvider());
	}

	private void createAssitTextBox(Point cursorRelativePosition) {
		assistText = new Text((Composite) graphicControl, SWT.BORDER);
		assistText.setLocation(cursorRelativePosition.x, cursorRelativePosition.y - assistText.getLineHeight());
		assistText.setSize(200, assistText.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		assistText.setTextLimit(51);
		assistText.setFocus();
	}

	/**
	 * Calculate the cursor position on current editor
	 * @return cursorRelativePosition
	 */
	private Point calculatePosition() {

		Point cursorAbsLocation = graphicControl.getDisplay().getCursorLocation();
		Point cursorRelativePosition = graphicControl.getDisplay().map(null, graphicControl, cursorAbsLocation);
		if (!isInsideGraphic(cursorRelativePosition, graphicControl.getSize())) {
			return null;
		}
		return cursorRelativePosition;
	}

	/**
	 * This used to judge the cursor is inside the editor or not
	 * @param cursorPosition
	 * @param graphicSize
	 * @return
	 */
	private boolean isInsideGraphic(Point cursorPosition, Point graphicSize) {
		if (cursorPosition.x < 0 || cursorPosition.y < 0) {
			return false;
		}
		if (cursorPosition.x > graphicSize.x || cursorPosition.y > graphicSize.y) {
			return false;
		}
		return true;
	}
}
