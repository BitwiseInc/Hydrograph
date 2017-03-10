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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.about.AboutBundleGroupData;
import org.eclipse.ui.internal.about.AboutItem;
import org.eclipse.ui.internal.about.AboutTextManager;
import org.eclipse.ui.internal.about.InstallationDialog;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.help.Activator;
import hydrograph.ui.help.Messages;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * The Class CustomAboutDialog. Creates Custom AboutDialog of Hydrograph
 * Application
 * 
 * @author Bitwise
 */
public class CustomAboutDialog extends TrayDialog {
	private static final String ECLIPSE_BUILD_ID = "eclipse.buildId";
	private final static int MAX_IMAGE_WIDTH_FOR_TEXT = 250;
	private final static int TEXT_MARGIN = 5;

	private final static int DETAILS_ID = IDialogConstants.CLIENT_ID + 1;

	private String productName;

	private IProduct product;

	private ArrayList<Image> images = new ArrayList<Image>();

	private Logger logger = LogFactory.INSTANCE.getLogger(CustomAboutDialog.class);
	private StyledText text;
	private Shell parentShell;
	private AboutTextManager aboutTextManager;
	private Cursor handCursor;
	private AboutItem item;
	private GridData data_1;
	private AboutBundleGroupData[] bundleGroupInfos;

	/**
	 * Create an instance of the AboutDialog for the given window.
	 * 
	 * @param parentShell
	 *            The parent of the dialog.
	 */
	public CustomAboutDialog(Shell parentShell) {
		super(parentShell);
		this.parentShell = parentShell;
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.WRAP);
		product = Platform.getProduct();

		if (product != null) {
			productName = product.getName();
		}
		if (productName == null) {
			productName = WorkbenchMessages.AboutDialog_defaultProductName;
		}

		// setDialogHelpAvailable(true);
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case DETAILS_ID:
			BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
				public void run() {
					IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					InstallationDialog dialog = new InstallationDialog(getShell(), workbenchWindow);
					dialog.setModalParent(CustomAboutDialog.this);
					dialog.open();
				}
			});
			break;
		default:
			super.buttonPressed(buttonId);
			break;
		}
	}

	public boolean close() {
		// dispose all images
		for (int i = 0; i < images.size(); ++i) {
			Image image = (Image) images.get(i);
			image.dispose();
		}

		return super.close();
	}

	/*
	 * (non-Javadoc) Method declared on Window.
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(NLS.bind(WorkbenchMessages.AboutDialog_shellTitle, productName));

	}

	/**
	 * Creates and returns the contents of the upper part of the dialog (above
	 * the button bar).
	 *
	 * Subclasses should overide.
	 *
	 * @param parent
	 *            the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		// brand the about box if there is product info
		Image aboutImage = null;
		item = null;
		if (product != null) {
			Bundle bundle = Platform.getBundle(Constants.ABOUT_DIALOG_IMAGE_BUNDLE_NAME);
			URL fullPathString = BundleUtility.find(bundle, Constants.ABOUT_DIALOG_IMAGE_PATH);
			aboutImage = ImageDescriptor.createFromURL(fullPathString).createImage();

			parent.getShell().setMinimumSize(450, 267);

			// if the about image is small enough, then show the text
			if (aboutImage == null || aboutImage.getBounds().width <= MAX_IMAGE_WIDTH_FOR_TEXT) {
				String aboutText = Messages.ABOUT_HEADER_TEXT + Messages.ABOUT_TEXT;

				if (aboutText != null) {
					String buildNumber = System.getProperty(ECLIPSE_BUILD_ID);
					if (StringUtils.isBlank(buildNumber)) {
						buildNumber = Platform.getBundle(Activator.PLUGIN_ID).getVersion().toString();
					}
					item = AboutTextManager.scan(Messages.ABOUT_VERSION_INFO + "\n" + "Build Number : " + buildNumber
							+ "\n \n" + "License Information : " + Messages.ABOUT_LICENSE_INFO + "\n \n "
							+ Messages.ABOUT_COPYRIGHT_INFO + "\n \n");
				}
			}

			if (aboutImage != null) {
				images.add(aboutImage);
			}
		}

		// create a composite which is the parent of the top area and the bottom
		// button bar, this allows there to be a second child of this composite
		// with
		// a banner background on top but not have on the bottom
		Composite workArea = new Composite(parent, SWT.NONE);
		GridLayout workLayout = new GridLayout();
		workLayout.marginHeight = 0;
		workLayout.marginWidth = 0;
		workLayout.verticalSpacing = 0;
		workLayout.horizontalSpacing = 0;
		workArea.setLayout(workLayout);
		workArea.setLayoutData(new GridData(GridData.FILL_BOTH));

		// page group
		Color background = JFaceColors.getBannerBackground(parent.getDisplay());
		Color foreground = JFaceColors.getBannerForeground(parent.getDisplay());
		Composite top = (Composite) super.createDialogArea(workArea);

		// override any layout inherited from createDialogArea
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		top.setLayout(layout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		top.setBackground(background);
		top.setForeground(foreground);
		

		// the image & text
		final Composite topContainer = new Composite(top, SWT.NONE);
		topContainer.setBackground(background);
		topContainer.setForeground(foreground);

		layout = new GridLayout();
		layout.numColumns = (aboutImage == null || item == null ? 1 : 2);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		topContainer.setLayout(layout);
		topContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Calculate a good height for the text
		GC gc = new GC(parent);
		int lineHeight = gc.getFontMetrics().getHeight();
		gc.dispose();


		// image on left side of dialog
		if (aboutImage != null) {
			Label imageLabel = new Label(topContainer, SWT.NONE);
			imageLabel.setBackground(background);
			imageLabel.setForeground(foreground);

			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
			imageLabel.setLayoutData(data);
			imageLabel.setImage(aboutImage);
		}

		Composite newComposite = new Composite(topContainer, SWT.NONE);
		newComposite.setLayout(new GridLayout(1, true));
		newComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		newComposite.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		topContainer.setLayoutData(data);
		// used only to drive initial size so that there are no hints in the
		// layout data
		final Link link = new Link(newComposite, SWT.NONE);
		GridData data1 = new GridData(SWT.FILL, SWT.FILL, true, false, 0, 0);
		link.setLayoutData(data1);
		link.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
		link.setText("<a>" + Messages.ABOUT_HEADER_TEXT + "</a>" + Messages.ABOUT_TEXT);

		link.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
							.openURL(new URL(Messages.HYDROGRAPH_URL));
				} catch (IllegalArgumentException | PartInitException | MalformedURLException e1) {
					logger.error(e1.getMessage());
					WidgetUtility.errorMessage(Messages.ERROR_MESSAGE_FOR_GITHUB_URL);
				}

			}
		});
		if (item != null) {

			text = new StyledText(newComposite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
			configureText(newComposite);

		}

		// horizontal bar
		Label bar = new Label(workArea, SWT.HORIZONTAL | SWT.SEPARATOR);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		bar.setLayoutData(data);

		// add image buttons for bundle groups that have them
		Composite bottom = (Composite) super.createDialogArea(workArea);
		// override any layout inherited from createDialogArea
		layout = new GridLayout();
		bottom.setLayout(layout);
		data_1 = new GridData();
		data_1.heightHint = 0;
		data_1.horizontalAlignment = SWT.FILL;
		data_1.verticalAlignment = SWT.FILL;
		data_1.grabExcessHorizontalSpace = true;

		bottom.setLayoutData(data_1);

		// spacer
		bar = new Label(bottom, SWT.NONE);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		bar.setLayoutData(data);

		return workArea;
	}

	void recreateWrappedText(Composite parent, boolean withScrolling) {
		int style = text.getStyle();
		if (withScrolling) {
			style |= SWT.V_SCROLL;
		} else {
			style ^= SWT.V_SCROLL;
		}
		boolean hasFocus = text.isFocusControl();
		Point selection = text.getSelection();
		text.dispose();
		text = new StyledText(parent, style);
		configureText(parent);
		if (hasFocus) {
			// text.setFocus();
		}
		text.setSelection(selection);
	}

	void configureText(final Composite parent) {
		// Don't set caret to 'null' as this causes
		// https://bugs.eclipse.org/293263.
		// text.setCaret(null);
		Color background = JFaceColors.getBannerBackground(parent.getDisplay());
		Color foreground = JFaceColors.getBannerForeground(parent.getDisplay());

		text.setFont(parent.getFont());
		text.setText(item.getText());

		text.setBackground(background);
		text.setForeground(foreground);
		text.setMargins(TEXT_MARGIN, TEXT_MARGIN, TEXT_MARGIN, 0);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		text.setLayoutData(gd);

		aboutTextManager = new AboutTextManager(text);
		aboutTextManager.setItem(item);

		createTextMenu();

	}

	/**
	 * Create the context menu for the text widget.
	 * 
	 * @since 3.4
	 */
	private void createTextMenu() {
		final MenuManager textManager = new MenuManager();
		textManager.add(new CommandContributionItem(new CommandContributionItemParameter(PlatformUI.getWorkbench(),
				null, IWorkbenchCommandConstants.EDIT_COPY, CommandContributionItem.STYLE_PUSH)));
		textManager.add(new CommandContributionItem(new CommandContributionItemParameter(PlatformUI.getWorkbench(),
				null, IWorkbenchCommandConstants.EDIT_SELECT_ALL, CommandContributionItem.STYLE_PUSH)));
		text.setMenu(textManager.createContextMenu(text));
		text.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				textManager.dispose();
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	protected boolean isResizable() {
		return true;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button button = createButton(parent, 0, "", false);
		Bundle bundle = Platform.getBundle(Constants.ABOUT_DIALOG_IMAGE_BUNDLE_NAME);
		URL fullPathString = BundleUtility.find(bundle, Constants.ABOUT_DIALOG_FEATURE_IMAGE_PATH);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
		button.setToolTipText("Help");
		Image image = ImageDescriptor.createFromURL(fullPathString).createImage();
		button.setImage(image);
		images.add(image);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActivePart();
				IHandlerService handlerService = (IHandlerService) workbenchPart.getSite()
						.getService(IHandlerService.class);
				try {
					handlerService.executeCommand("org.eclipse.ui.help.displayHelp", null);
				} catch (Exception ex) {
					throw new RuntimeException("Help File Not Found!");
				}
			}
		});

		Button installationButton = new Button(parent, SWT.PUSH);
		installationButton.setLayoutData(new GridData());
		GridLayout layout = (GridLayout) parent.getLayout();
		installationButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		layout.numColumns++;
		layout.makeColumnsEqualWidth = false;
		installationButton.setText("Installation Details");
		installationButton.setToolTipText("Installation Details");
		installationButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				HydrographInstallationDialog dialog = new HydrographInstallationDialog(getShell());
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// {Do-nothing}
			}
		});

		Label installationLabel = new Label(parent, SWT.NONE);
		installationLabel.setLayoutData(new GridData());
		GridLayout installationLabelLayout = (GridLayout) parent.getLayout();
		installationLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		installationLabelLayout.numColumns++;
		installationLabelLayout.makeColumnsEqualWidth = false;

		Button ok = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		ok.setFocus();

	}
}
