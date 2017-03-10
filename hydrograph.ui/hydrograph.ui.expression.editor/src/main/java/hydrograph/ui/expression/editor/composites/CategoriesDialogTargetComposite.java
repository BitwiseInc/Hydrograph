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

package hydrograph.ui.expression.editor.composites;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.slf4j.Logger;

import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.PathConstant;
import hydrograph.ui.expression.editor.jar.util.BuildExpressionEditorDataSturcture;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.logging.factory.LogFactory;
public class CategoriesDialogTargetComposite extends Composite {
	private List targetList;
	private Logger LOGGER = LogFactory.INSTANCE.getLogger(CategoriesDialogTargetComposite.class);
	private Label addAllSelectedPackagesLabel;
	private Label addSelectedPackagesLabel;
	private List sourcePackageList;
	private Combo comboJarList;
	private CategoriesDialogSourceComposite categoriesDialogSourceComposite;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param categoriesDialogSourceComposite 
	 * @param style
	 */
	public CategoriesDialogTargetComposite(Composite parent, CategoriesDialogSourceComposite categoriesDialogSourceComposite, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		new Label(this, SWT.NONE);

		Composite upperComposite = new Composite(this, SWT.NONE);
		upperComposite.setLayout(new GridLayout(2, false));
		GridData gd_upperComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 0, 0);
		gd_upperComposite.heightHint = 34;
		upperComposite.setLayoutData(gd_upperComposite);

		Label lblSelectedCategories = new Label(upperComposite, SWT.NONE);
		lblSelectedCategories.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblSelectedCategories.setText("Selected Packages");

		createDelButton(upperComposite);
		
		createMiddleLayer();

		targetList = new List(this, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION|SWT.V_SCROLL|SWT.H_SCROLL);
		targetList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		loadPackagesFromPropertyFileSettingFolder();

		addDropSupport();
		
		linkSourceAndTarget(categoriesDialogSourceComposite);
	}

	private void createMiddleLayer() {
		Composite middleComposite = new Composite(this, SWT.NONE);
		middleComposite.setLayout(new GridLayout(1, false));
		middleComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		
		addAllSelectedPackagesLabel = new Label(middleComposite, SWT.NONE);
		addAllSelectedPackagesLabel.setBounds(6, 70, 25, 25);
		addAllSelectedPackagesLabel.setImage(ImagePathConstant.SELECT_ALL_ICON.getImageFromRegistry());
		addAllSelectedPackagesLabel.setToolTipText("Add all packages");
		addListenerToSelectAllLable(addAllSelectedPackagesLabel);
		addAllSelectedPackagesLabel.setEnabled(false);
		
		Label dummyLabel = new Label(middleComposite, SWT.NONE);
		
		addSelectedPackagesLabel = new Label(middleComposite, SWT.NONE);
		addSelectedPackagesLabel.setBounds(6, 110, 25, 25);
		addSelectedPackagesLabel.setImage(ImagePathConstant.SELECT_ICON.getImageFromRegistry());
		addSelectedPackagesLabel.setToolTipText("Add selected packages");
		addListenerToSelectLable(addSelectedPackagesLabel);
		addSelectedPackagesLabel.setEnabled(false);
	}

	private void addListenerToSelectAllLable(Label selectAllLabel) {
		selectAllLabel.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (comboJarList.getSelectionIndex() != -1) {
					for (String packageName : sourcePackageList.getItems()) {
						String formattedPackageName = packageName + Constants.DOT + Constants.ASTERISK + SWT.SPACE
								+ Constants.DASH + SWT.SPACE + comboJarList.getItem(comboJarList.getSelectionIndex());
						if (isFieldAvailable(formattedPackageName)) {
							targetList.add(formattedPackageName);
							removeAddedPackageNameFromSourceList(formattedPackageName);
						}
					}
					categoriesDialogSourceComposite.enableOrDisableAddLabelsOnComboSelection();
				}
			}
			
			@Override
			public void mouseDown(MouseEvent e) {}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
		});
	}

	private void addListenerToSelectLable(Label selectLabel) {
		selectLabel.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (comboJarList.getSelectionIndex() != -1) {
					for (String packageName : sourcePackageList.getSelection()) {
						String formattedPackageName = packageName + Constants.DOT + Constants.ASTERISK + SWT.SPACE
								+ Constants.DASH + SWT.SPACE + comboJarList.getItem(comboJarList.getSelectionIndex());
						if (isFieldAvailable(formattedPackageName)) {
							targetList.add(formattedPackageName);
							removeAddedPackageNameFromSourceList(formattedPackageName);
						}
					}
					categoriesDialogSourceComposite.enableOrDisableAddLabelsOnComboSelection();
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
		});
	}

	private void removeAddedPackageNameFromSourceList(String formattedPackageName) {
		if (sourcePackageList.indexOf(formattedPackageName) != -1) {
			sourcePackageList.remove(formattedPackageName);
		} else {
			String packageName = ExpressionEditorUtil.INSTANCE.getPackageNameFromFormattedText(formattedPackageName);
			if (sourcePackageList.indexOf(packageName) != -1) {
				sourcePackageList.remove(packageName);
			}
		}
	}

	private void linkSourceAndTarget(CategoriesDialogSourceComposite categoriesDialogSourceComposite) {
		categoriesDialogSourceComposite.setAddAllLabel(addAllSelectedPackagesLabel);
		categoriesDialogSourceComposite.setAddLabel(addSelectedPackagesLabel);
		this.sourcePackageList=categoriesDialogSourceComposite.getSourcePackageList();
		this.comboJarList=categoriesDialogSourceComposite.getComboJarList();
		categoriesDialogSourceComposite.setTargetList(targetList);
		categoriesDialogSourceComposite.setTargetComposite(this);
		this.categoriesDialogSourceComposite=categoriesDialogSourceComposite;
	}

	public void loadPackagesFromPropertyFileSettingFolder() {
		Properties properties = new Properties();
		IFolder folder = BuildExpressionEditorDataSturcture.INSTANCE.getCurrentProject().getFolder(
				PathConstant.PROJECT_RESOURCES_FOLDER);
		IFile file = folder.getFile(PathConstant.EXPRESSION_EDITOR_EXTERNAL_JARS_PROPERTIES_FILES);
		try {
			LOGGER.debug("Loading property file");
			targetList.removeAll();
			if (file.getLocation().toFile().exists()) {
				FileInputStream inStream = new FileInputStream(file.getLocation().toString());
				properties.load(inStream);
				for (Object key : properties.keySet()) {
					String jarFileName = StringUtils.trim(StringUtils.substringAfter((String) key, Constants.DASH));
					if (BuildExpressionEditorDataSturcture.INSTANCE.getIPackageFragment(jarFileName) != null) {
						targetList.add((String) key+SWT.SPACE+Constants.DASH+SWT.SPACE+properties.getProperty((String)key));
					}
				}
			}
		} catch (IOException | RuntimeException exception) {
			LOGGER.error("Exception occurred while loading jar files from projects setting folder", exception);
		}

	}

	private void createDelButton(Composite upperComposite) {
		Button deleteButton = new Button(upperComposite, SWT.NONE);
		deleteButton.setBounds(0, 0, 75, 25);
		try {
			deleteButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		} catch (Exception exception) {
			LOGGER.error("Exception occurred while attaching image to button", exception);
			deleteButton.setText("Delete");
		}
		deleteButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (String field : targetList.getSelection()) {
					targetList.remove(field);
				}
				categoriesDialogSourceComposite.populatePackageNameFromSelectedJar();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void addDropSupport() {
		DropTarget dropTarget = new DropTarget(targetList, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				for (String formattedPackageName : ExpressionEditorUtil.INSTANCE.getformatedData((String) event.data)) {
					if (isFieldAvailable(formattedPackageName)){
						targetList.add(formattedPackageName);
						removeAddedPackageNameFromSourceList(formattedPackageName);
						categoriesDialogSourceComposite.enableOrDisableAddLabelsOnComboSelection();
					}
				}
			}
		});
	}

	private boolean isFieldAvailable(String fieldName) {
		for (String string : targetList.getItems())
			if (StringUtils.equalsIgnoreCase(string, fieldName))
				return false;
		return true;
	}

	public List getTargetList() {
		return targetList;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
