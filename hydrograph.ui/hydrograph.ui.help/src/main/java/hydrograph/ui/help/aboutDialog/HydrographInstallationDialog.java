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

package hydrograph.ui.help.aboutDialog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.about.IInstallationPageContainer;
import org.eclipse.ui.about.InstallationPage;
import org.eclipse.ui.internal.ConfigurationInfo;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;

import hydrograph.ui.datastructure.property.InstallationWindowDetails;
import hydrograph.ui.datastructure.property.JarInformationDetails;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * 
 *  Rewritten HydrographInstallationDialog class from InstallationDialog class for managing TabFolder items. 
 * @author Bitwise
 *
 */
public class HydrographInstallationDialog extends TrayDialog implements
		IInstallationPageContainer {
	class ButtonManager {

		private Composite composite;
		HashMap buttonMap = new HashMap(); // page id->Collection of page

		// buttons

		public ButtonManager(Composite composite) {
			this.composite = composite;
		}

		public Composite getParent() {
			return composite;
		}

		public void update(String currentPageId) {
			if (composite == null || composite.isDisposed())
				return;
			GC metricsGC = new GC(composite);
			FontMetrics metrics = metricsGC.getFontMetrics();
			metricsGC.dispose();
			List buttons = (List) buttonMap.get(currentPageId);
			Control[] children = composite.getChildren();

			int visibleChildren = 0;
			Button closeButton = getButton(IDialogConstants.CLOSE_ID);

			for (int i = 0; i < children.length; i++) {
				Control control = children[i];
				if (closeButton == control)
					closeButton.dispose();
				else {
					control.setVisible(false);
					setButtonLayoutData(metrics, control, false);
				}
			}
			if (buttons != null) {
				for (int i = 0; i < buttons.size(); i++) {
					Button button = (Button) buttons.get(i);
					button.setVisible(true);
					setButtonLayoutData(metrics, button, true);
					GridData data = (GridData) button.getLayoutData();
					data.exclude = false;
					visibleChildren++;
				}
			}

			GridLayout compositeLayout = (GridLayout) composite.getLayout();
			compositeLayout.numColumns = visibleChildren;
			composite.layout(true);
		}

		protected void setButtonLayoutData(FontMetrics metrics, Control button,
				boolean visible) {
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			int widthHint = Dialog.convertHorizontalDLUsToPixels(metrics,
					IDialogConstants.BUTTON_WIDTH);
			Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			data.widthHint = Math.max(widthHint, minSize.x);
			data.exclude = !visible;
			button.setLayoutData(data);
		}

		public void addPageButton(String id, Button button) {
			List list = (List) buttonMap.get(id);
			if (list == null) {
				list = new ArrayList(1);
				buttonMap.put(id, list);
			}
			list.add(button);
		}

		public void clear() {
			buttonMap = new HashMap();
		}
	}

	protected static final String ID = "ID"; //$NON-NLS-1$
	private static final String DIALOG_SETTINGS_SECTION = "InstallationDialogSettings"; //$NON-NLS-1$
	private final static int TAB_WIDTH_IN_DLUS = 440;
	private final static int TAB_HEIGHT_IN_DLUS = 320;
	private TableViewer tableViewer;
	private Composite composite_1;
	private URL fileUrl;
	private InstallationWindowDetails installationWindowDetails;
	private Bundle bundle = Platform.getBundle("hydrograph.ui.help");
	private IPath path = new Path("/xml/About_Window_Installation_Details.xml");
	private static String lastSelectedTabId = null;
	private Logger logger = LogFactory.INSTANCE.getLogger(HydrographInstallationDialog.class);
	private CTabFolder folder;
	IServiceLocator serviceLocator;
	private ButtonManager buttonManager;
	private Map pageToId = new HashMap();
	private Dialog modalParent;

	/**
	 * @param parentShell
	 * @param locator
	 */
	public HydrographInstallationDialog(Shell parentShell) {
		super(parentShell);
		
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		String productName = ""; //$NON-NLS-1$
		IProduct product = Platform.getProduct();
		if (product != null && product.getName() != null)
			productName = product.getName();
		newShell.setText(NLS.bind(
				WorkbenchMessages.InstallationDialog_ShellTitle, productName));
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.getShell().setMinimumSize(950, 500);
		folder = new CTabFolder(composite, SWT.NONE);
		configureFolder();
		createFolderItems(folder);

		GridData folderData = new GridData(SWT.FILL, SWT.FILL, true, true);
		folderData.widthHint = convertHorizontalDLUsToPixels(TAB_WIDTH_IN_DLUS);
		folderData.heightHint = convertVerticalDLUsToPixels(TAB_HEIGHT_IN_DLUS);
		folder.setLayoutData(folderData);
		folder.addSelectionListener(createFolderSelectionListener());
		folder.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				releaseContributions();
			}
		});
		return composite;
	}

	protected void createFolderItems(CTabFolder folder) {
		IConfigurationElement[] elements = ConfigurationInfo
				.getSortedExtensions(loadElements());
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];
			if (!element.getAttribute(IWorkbenchRegistryConstants.ATT_NAME).equalsIgnoreCase("Features")) {
				CTabItem item = new CTabItem(folder, SWT.NONE);

				item.setText(element.getAttribute(IWorkbenchRegistryConstants.ATT_NAME));
				item.setData(element);
				item.setData(ID, element.getAttribute(IWorkbenchRegistryConstants.ATT_ID));

				Composite control = new Composite(folder, SWT.NONE);
				control.setLayout(new GridLayout());
				item.setControl(control);
			}
		}
		
		
		
		CTabItem tbtmLibraries = new CTabItem(folder, SWT.NONE);
		tbtmLibraries.setText("Libraries");

		composite_1 = new Composite(folder, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		tbtmLibraries.setControl(composite_1);
		
		URL url = FileLocator.find(bundle, path, null);
		try {
			fileUrl = FileLocator.toFileURL(url);
		} catch (IOException e2) {
			logger.error(e2.getMessage());
		}
		tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		readFromXMLFile(fileUrl);
		createTableViewerColumns(tableViewer, "Name");
		createTableViewerColumns(tableViewer, "Version No");
		createTableViewerColumns(tableViewer, "Group Id");
		createTableViewerColumns(tableViewer, "Artifact Id");
		TableViewerColumn tableLicense = createTableViewerColumns(tableViewer, "License Info");

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnItem = tableViewerColumn.getColumn();
		tblclmnItem.setWidth(0);
		tblclmnItem.setResizable(false);
		tblclmnItem.setText("Path");

		tableViewer.setLabelProvider(new InstallationDetailsLabelProvider());
		tableViewer.setContentProvider(new InstallationDetailsContentProvider());
		tableViewer.setInput(installationWindowDetails.getJarInfromationDetails());
		tableLicense.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				Object element = cell.getElement();
				if (element instanceof JarInformationDetails) {
					JarInformationDetails jarInfo = (JarInformationDetails) cell.getElement();

					/* make text look like a link */
					StyledString text = new StyledString();
					StyleRange myStyledRange = new StyleRange(0, jarInfo.getLicenseInfo().length(),
							Display.getCurrent().getSystemColor(SWT.COLOR_BLUE), null);
					myStyledRange.underline = true;
					text.append(jarInfo.getLicenseInfo(), StyledString.DECORATIONS_STYLER);
					cell.setText(text.toString());

					StyleRange[] range = { myStyledRange };
					cell.setStyleRanges(range);

					super.update(cell);

				}
			}
		});
		tableViewer.refresh();

		tableViewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
				JarInformationDetails details = (JarInformationDetails) selection.getFirstElement();
				if(details != null){
					IPath iPath = new Path(details.getPath());
					try {
						URL url = FileLocator.find(bundle, iPath, null);
						URL fileUrlForPath = FileLocator.toFileURL(url);
						PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(fileUrlForPath);
					} catch (PartInitException | IOException e1) {
						logger.error(e1.getMessage());
						WidgetUtility.errorMessage("Unable to open URL in external browser");
					}
				}

			}
		});
	}

	
	/**
	 * Creates columns for the Table Viewer
	 * 
	 * @param tableViewer
	 * @return tableViewerColumn
	 */
	public TableViewerColumn createTableViewerColumns(TableViewer tableViewer, String columnName) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnItem = tableViewerColumn.getColumn();
		tblclmnItem.setWidth(180);
		tblclmnItem.setText(columnName);

		return tableViewerColumn;
	}

	/**
	 * Reads the XML file(About_Window_Installation_Details.xml) to display in
	 * Installation Window
	 * 
	 * @param file
	 * 
	 */
	public void readFromXMLFile(URL file) {

		XStream xstream = new XStream();
		xstream.alias("InstallationWindowDetails", InstallationWindowDetails.class);
		xstream.alias("JarInformationDetail", JarInformationDetails.class);

		installationWindowDetails = (InstallationWindowDetails) xstream.fromXML(file);

	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		boolean selected = false;
		if (folder.getItemCount() > 0) {
			if (lastSelectedTabId != null) {
				CTabItem[] items = folder.getItems();
				for (int i = 0; i < items.length; i++)
					if (items[i].getData(ID).equals("30.PluginPage")) {
						folder.setSelection(i);
						tabSelected(items[i]);
						selected = true;
						break;
					}
			}
			if (!selected)
				tabSelected(folder.getItem(0));
		}
		// need to reapply the dialog font now that we've created new
		// tab items
		Dialog.applyDialogFont(folder);
		return control;
	}

	private SelectionAdapter createFolderSelectionListener() {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				tabSelected((CTabItem) e.item);
			}
		};
	}

	/*
	 * Must be called after contributions and button manager are created.
	 */
	private void tabSelected(CTabItem item) {
		if (item.getData() instanceof IConfigurationElement) {
			final IConfigurationElement element = (IConfigurationElement) item
					.getData();

			Composite pageComposite = (Composite) item.getControl();
			try {
				final InstallationPage page = (InstallationPage) element
						.createExecutableExtension(IWorkbenchRegistryConstants.ATT_CLASS);
				page.createControl(pageComposite);
				// new controls created since the dialog font was applied, so
				// apply again.
				Dialog.applyDialogFont(pageComposite);
				page.setPageContainer(this);
				// Must be done before creating the buttons because the control
				// button creation methods
				// use this map.
				pageToId.put(page, element
						.getAttribute(IWorkbenchRegistryConstants.ATT_ID));
				createButtons(page);
				item.setData(page);
				item.addDisposeListener(new DisposeListener() {

					@Override
					public void widgetDisposed(DisposeEvent e) {
						page.dispose();
					}
				});
				pageComposite.layout(true, true);

			} catch (CoreException e1) {
				Label label = new Label(pageComposite, SWT.NONE);
				label.setText(e1.getMessage());
				item.setData(null);
			}

		}
		String id = (String) item.getData(ID);
		rememberSelectedTab(id);
		buttonManager.update(id);
		Button button = createButton(buttonManager.getParent(),
				IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
		GridData gd = (GridData) button.getLayoutData();
		gd.horizontalAlignment = SWT.BEGINNING;
		gd.horizontalIndent = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH) / 2;
		// Layout the button bar's parent and all of its children.  We must
		// cascade through all children because the buttons have changed and
		// because tray dialog inserts an extra composite in the button bar
		// hierarchy.
		getButtonBar().getParent().layout(true, true);

	}

	protected void createButtons(InstallationPage page) {
		page.createPageButtons(buttonManager.getParent());
		Dialog.applyDialogFont(buttonManager.getParent());
	}

	private void rememberSelectedTab(String pageId) {
		lastSelectedTabId = pageId;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// The button manager will handle the correct sizing of the buttons.
		// We do not want columns equal width because we are going to add some
		// padding in the final column (close button).
		GridLayout layout = (GridLayout) parent.getLayout();
		layout.makeColumnsEqualWidth = false;
		buttonManager = new ButtonManager(parent);
	}

	private void configureFolder() {
	}

	private IConfigurationElement[] loadElements() {
		IExtensionPoint point = Platform.getExtensionRegistry()
				.getExtensionPoint("org.eclipse.ui", "installationPages"); //$NON-NLS-1$ //$NON-NLS-2$
		return point.getConfigurationElements();
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = WorkbenchPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS_SECTION);
		if (section == null) {
			section = settings.addNewSection(DIALOG_SETTINGS_SECTION);
		}
		return section;
	}

	protected void releaseContributions() {
		buttonManager.clear();
	}

	@Override
	public void closeModalContainers() {
		close();
		if (modalParent != null)
			modalParent.close();
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.CLOSE_ID == buttonId) {
			okPressed();
		}
	}

	@Override
	public void registerPageButton(InstallationPage page, Button button) {
		buttonManager.addPageButton(pageToId(page), button);
	}

	protected String pageToId(InstallationPage page) {
		String pageId = (String) pageToId.get(page);
		Assert.isLegal(pageId != null);
		return pageId;
	}

	/**
	 * Set the modal parent dialog that was used to launch this dialog. This
	 * should be used by any launching dialog so that the {
	 * {@link #closeModalContainers()} method can be properly implemented.
	 *
	 * @param parent
	 *            the modal parent dialog that launched this dialog, or
	 *            <code>null</code> if there was no parent.
	 *
	 *            This is an internal method and should not be used outside of
	 *            platform UI.
	 */
	public void setModalParent(Dialog parent) {
		this.modalParent = parent;
	}
}
