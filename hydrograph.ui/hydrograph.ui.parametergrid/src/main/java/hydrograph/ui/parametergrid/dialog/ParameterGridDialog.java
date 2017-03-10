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

 
package hydrograph.ui.parametergrid.dialog;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.slf4j.Logger;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.parametergrid.constants.ErrorMessages;
import hydrograph.ui.parametergrid.constants.MessageType;
import hydrograph.ui.parametergrid.textgridwidget.TextGrid;
import hydrograph.ui.parametergrid.textgridwidget.columns.TextGridColumnLayout;
import hydrograph.ui.parametergrid.textgridwidget.columns.TextGridRowLayout;
import hydrograph.ui.parametergrid.utils.ParameterFileManager;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;


public class ParameterGridDialog extends Dialog {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ParameterGridDialog.class);
	
	private TextGrid textGrid;
	private boolean runGraph;
	private Button headerCompositeCheckBox;
	private Text paramterFileTextBox;
	private String parameterFile;
	private ControlDecoration txtDecorator;
	private TraverseListener lastRowLastColumnTraverseListener;
	
	private boolean visibleParameterGirdNote=true;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ParameterGridDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
		runGraph=false;
		
		
		lastRowLastColumnTraverseListener=new TraverseListener() {
			
			@Override
			public void keyTraversed(TraverseEvent e) {
				if(e.detail == SWT.TRAVERSE_TAB_NEXT)
					addRowToTextGrid();
			}
		};
		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite container = createBaseContainer(parent);
		logger.debug("Created base container");
		
		Composite composite = attachButtonContainer(container);
		logger.debug("attached button container");
		
		attachFileParameterFileBrowser(container);
		logger.debug("added file browser");
		
		attachTextGrid(container);
		logger.debug("attached TextGrid");

		Label separator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator1.setVisible(false);
		Label separator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator2.setVisible(false);
		
		attachNote(container);
		
		addGridHeader();
		logger.debug("attached Grid header");
		
		attachAddRowButton(composite);
		logger.debug("attached add row button");

		attachRemoveRowButton(composite);
		logger.debug("attached remove button");

		fetchParameterFilePath();
		logger.debug("Retrived parameter file path");

		loadGridData();
		logger.debug("loaded grid data");

		addBaseContainerListeners(container);
		logger.debug("added listeners to base container");
		
		if(textGrid.getLastAddedRow() != null){
			((Text)textGrid.getLastAddedRow().getChildren()[2]).addTraverseListener(lastRowLastColumnTraverseListener);
		}
		
		return container;
	}

	private void attachNote(final Composite container) {
		Label lblParameterGridNote=new Label(container, SWT.NONE);
		FontData fontData = lblParameterGridNote.getFont().getFontData()[0];
		Font font = new Font(lblParameterGridNote.getDisplay(), new FontData(fontData.getName(), fontData
		    .getHeight(), SWT.ITALIC));
		lblParameterGridNote.setText("Note - New parameters will be visible only after you save the job.");
		lblParameterGridNote.setFont(font);
		
		if(!visibleParameterGirdNote)
			lblParameterGridNote.setVisible(false);
	}
	
	public void setVisibleParameterGridNote(boolean visibleParameterGirdNote){
		this.visibleParameterGirdNote=visibleParameterGirdNote;
	}

	/**
	 * Add listeners to base container
	 * 
	 * @param container
	 */
	private void addBaseContainerListeners(final Composite container) {
		addControlListener(container);
		logger.debug("Added control listener");
	}

	/**
	 * Resizes base container on outer shell resize
	 * 
	 * @param container
	 */
	private void addControlListener(final Composite container) {
		container.getParent().addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				//textGrid.setHeight(container.getParent().getBounds().height - 150);
				textGrid.setHeight(container.getParent().getBounds().height - 165);
			}
		});
	}

	/**
	 * populate parameterFile field variable
	 * 
	 */
	private void fetchParameterFilePath() {
		if(getComponentCanvas().getCurrentParameterFilePath() ==null){
			if(getComponentCanvas().getParameterFile().startsWith("/") && getComponentCanvas().getParameterFile().contains(":")){
				parameterFile = getComponentCanvas().getParameterFile().replaceFirst("/", "");	
			}else{
				parameterFile = getComponentCanvas().getParameterFile();
			}

		}else{
			parameterFile = getComponentCanvas().getCurrentParameterFilePath();
		}
	}

	
	private void attachRemoveRowButton(Composite composite) {
		Label btnRemove = new Label(composite, SWT.NONE);
		btnRemove.setLayoutData(getGridControlButtonLayout());
		btnRemove.setText("");
		btnRemove.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		addRemoveRowButtonListener(btnRemove);
	}

	private void addRemoveRowButtonListener(Label btnRemove) {
		btnRemove.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				if(textGrid.getLastAddedRow() != null){
					((Text)textGrid.getLastAddedRow().getChildren()[2]).removeTraverseListener(lastRowLastColumnTraverseListener);
				}
				textGrid.removeSelectedRows();
				if(textGrid.getLastAddedRow() != null){
					((Text)textGrid.getLastAddedRow().getChildren()[2]).addTraverseListener(lastRowLastColumnTraverseListener);
					if(((Text)textGrid.getLastAddedRow().getChildren()[1]).getEditable()){
						((Text)textGrid.getLastAddedRow().getChildren()[1]).setFocus();
					}else{
						((Text)textGrid.getLastAddedRow().getChildren()[2]).setFocus();						
					}
				}	
				
				if(textGrid.getGrid().size() < 1){
					headerCompositeCheckBox.setEnabled(false);
					headerCompositeCheckBox.setSelection(false);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				//Do Nothing
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				//Do Nothing
			}
		});
	}

	private void attachAddRowButton(Composite composite) {
		Label btnAdd = new Label(composite, SWT.NONE);
		GridData gd_btnAdd = getGridControlButtonLayout();
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setText("");
		btnAdd.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		
		attachAddRowButtonListener(btnAdd);
	}
	
	
	
	private void addRowToTextGrid() {
		
		if(textGrid.getLastAddedRow() != null){
			((Text)textGrid.getLastAddedRow().getChildren()[2]).removeTraverseListener(lastRowLastColumnTraverseListener);
		}
		
		TextGridRowLayout textGridRowLayout = getTextGridRowLayout();
		
		Composite emptyRow = textGrid.addEmptyRow(textGridRowLayout);
		
		((Text)emptyRow.getChildren()[1]).setFocus();
		addRowCheckBoxListener(emptyRow);
		
		headerCompositeCheckBox.setSelection(false);
		
		for(Composite row:textGrid.getGrid()){
			final Text text=((Text)row.getChildren()[1]);
			txtDecorator=WidgetUtility.addDecorator(text,Messages.CHARACTERSET);
			txtDecorator.hide();
			attachKeyValidator(text);
			attachKeyFocusListener(text); 
		}
		
		((Text)textGrid.getLastAddedRow().getChildren()[2]).addTraverseListener(lastRowLastColumnTraverseListener);
		
		textGrid.refresh();
		textGrid.scrollToLastRow();
	}
	
	// +++ Code Refactoring is in progress
	private void attachAddRowButtonListener(Label btnAdd) {
		btnAdd.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				addRowToTextGrid();
				headerCompositeCheckBox.setEnabled(true);
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// Do Nothing
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// Do Nothing
			}
		});
	}

	
	private TextGridRowLayout getTextGridRowLayout() {
		TextGridRowLayout textGridRowLayout = new TextGridRowLayout();
		textGridRowLayout.addColumn(new TextGridColumnLayout.Builder().columnWidth(90).editable(true).build());
		textGridRowLayout.addColumn(new TextGridColumnLayout.Builder().grabHorizantalAccessSpace(true).editable(true).build());
		return textGridRowLayout;
	}

	private void addRowCheckBoxListener(Composite emptyRow) {
		((Button)emptyRow.getChildren()[0]).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);
				changeHeaderCheckboxSelection();
			}

		});
	}
	
	private void attachTextGrid(final Composite container) {
		textGrid = new TextGrid(container);
		textGrid.clear();
	}

	private Composite attachButtonContainer(final Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		ColumnLayoutData cld_composite = new ColumnLayoutData();
		cld_composite.horizontalAlignment = ColumnLayoutData.RIGHT;
		cld_composite.heightHint = 30;
		composite.setLayoutData(cld_composite);
		return composite;
	}

	private Composite createBaseContainer(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		ColumnLayout cl_container = new ColumnLayout();
		cl_container.verticalSpacing = 0;
		cl_container.maxNumColumns = 1;
		container.setLayout(cl_container);
		container.getShell().setText("Parameter Grid");
		return container;
	}

	private void attachKeyFocusListener(final Text text) {
		text.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				txtDecorator.hide();
			}
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void attachKeyValidator(final Text text) {
		text.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				txtDecorator.hide();
				String currentText = ((Text) e.widget).getText();
				String newName = (currentText.substring(0, e.start) + e.text + currentText.substring(e.end));
				Matcher matchName = Pattern.compile("[\\@]{1}[\\{]{1}[\\w]*[\\}]{1}||[\\w]*").matcher(newName);
				if(!matchName.matches()){
					text.setToolTipText(Messages.CHARACTERSET);
					txtDecorator=WidgetUtility.addDecorator(text,Messages.CHARACTERSET);
					txtDecorator.setDescriptionText(Messages.CHARACTERSET);
					txtDecorator.show();
					e.doit=false;
				}
				else
				{
					text.setToolTipText("");
					text.setMessage("");
					txtDecorator.hide();
				}
			}
		});
	}

	private void loadGridData() {
		
		Map<String, String> parameterMap=new LinkedHashMap<>();
		
		try {
			parameterMap = ParameterFileManager.getInstance().getParameterMap(parameterFile);
		} catch (IOException e) {
			//isValidParameterFile = false;
			logger.debug("Unable to get parameter Map ", e);
		}

		if(parameterFile != null){
			if(parameterFile.contains(":")){
				if(parameterFile.startsWith("/"))
					paramterFileTextBox.setText(parameterFile.replaceFirst("/", ""));
				else
					paramterFileTextBox.setText(parameterFile);
			}
			else
			{
				paramterFileTextBox.setText(parameterFile);
			}	
		}


		//List of rows, where each row is list of columns
		List<List<String>> graphGridData =  new LinkedList<>();
		List<List<String>> externalGridData =  new LinkedList<>();
		List<String> canvasParameterList = getComponentCanvas().getLatestParameterList();
		for(String key: parameterMap.keySet()){
			List<String> rowData = new LinkedList<>();
			if(canvasParameterList.contains(key)){
				rowData.add(key);
				rowData.add(parameterMap.get(key));
				graphGridData.add(rowData);
			}else{
				rowData.add(key);
				rowData.add(parameterMap.get(key));
				externalGridData.add(rowData);
			}
		}



		for(List<String> row: graphGridData){
			TextGridRowLayout textGridRowLayout = new TextGridRowLayout();
			textGridRowLayout.addColumn(new TextGridColumnLayout.Builder().columnWidth(90).editable(false).build());
			textGridRowLayout.addColumn(new TextGridColumnLayout.Builder().grabHorizantalAccessSpace(true).editable(true).build());
			textGrid.addDisabledRow(textGridRowLayout, row);
		}

		for(List<String> row: externalGridData){
			TextGridRowLayout textGridRowLayout = new TextGridRowLayout();
			textGridRowLayout.addColumn(new TextGridColumnLayout.Builder().columnWidth(90).editable(true).build());
			textGridRowLayout.addColumn(new TextGridColumnLayout.Builder().grabHorizantalAccessSpace(true).editable(true).build());
			textGrid.addRow(textGridRowLayout, row);
		}


		textGrid.refresh();

		addGridRowSelectionListener();
	}


	private void loadParameterFile(){
		if(parameterFile == null){
			return;
		}
		textGrid.clear();

		//isValidParameterFile();

		loadGridData();
	}

	private List<String> getAllLines(String newParameterFile) throws Exception{
		Path filePath = Paths.get(newParameterFile, "");

		List<String> lines=null;
		Charset charset = Charset.forName(StandardCharsets.US_ASCII.name());

		lines = Files.readAllLines(filePath, charset);
		return lines;
	}
	private boolean isValidParameterFile(String newParameterFile) {
		boolean isValidParameterFile = true;
		try{
			List<String> fileContents = getAllLines(newParameterFile);
			for(String line: fileContents){
				if(!line.startsWith("#") && !line.isEmpty()){
					String[] keyvalue=line.split("=");
					if(keyvalue.length != 2){
						isValidParameterFile = false;
						MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK );

						/*messageBox.setText("Error");
						messageBox.setMessage("Unable to load parameter file.\nPlease Check file contents.\nContent should be in key=value format" +
								"\nEach line should have single key=value pair");*/
						messageBox.setText(MessageType.ERROR.toString());
						messageBox.setMessage(ErrorMessages.UNABLE_TO_LOAD_PARAM_FILE1);
						messageBox.open();
					}else{
						if(keyvalue[0].trim().contains(" ")){
							isValidParameterFile = false;
							MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK );

							/*messageBox.setText("Error");
							messageBox.setMessage("Unable to load parameter file.\nPlease Check file contents.\nContent should be in key=value format" +
									"\nEach line should have single key=value pair" +
									"\nParameter key should not contain spaces");*/
							messageBox.setText(MessageType.ERROR.toString());
							messageBox.setMessage(ErrorMessages.UNABLE_TO_LOAD_PARAM_FILE2);
							messageBox.open();
						}
					}
				}
			}

		}catch(Exception e){
			isValidParameterFile=false;
			MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK );

			/*messageBox.setText("Error");
			messageBox.setMessage("Unable to load parameter file.\nPlease Check file format.\nExpected file format - US-ASCII");*/
			//ErrorMessage as;
			
			messageBox.setText(MessageType.ERROR.toString());
			messageBox.setMessage(ErrorMessages.UNABLE_TO_LOAD_PARAM_FILE3);
			messageBox.open();
		}


		return isValidParameterFile;
	}

	private void attachFileParameterFileBrowser(Composite container){
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));

		Label lblFile = new Label(composite, SWT.NONE);
		lblFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFile.setText("Parameter File ");

		paramterFileTextBox = new Text(composite, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 179;
		paramterFileTextBox.setLayoutData(gd_text);
		

		final Button btnReloadParameterFile = new Button(composite, SWT.NONE);
		
		btnReloadParameterFile.setText("View Parameters");
		btnReloadParameterFile.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				parameterFile = paramterFileTextBox.getText();
				if(isValidParameterFile(parameterFile)){
					getComponentCanvas().setCurrentParameterFilePath(parameterFile);
					loadParameterFile();
				}
				
			}

		});

		final Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(btnNewButton.getShell(), SWT.OPEN);
				fd.setText("Open");
				Path path;
				if(parameterFile.contains(":"))
					path = Paths.get(parameterFile.replaceFirst("/", ""));
				else
					path = Paths.get(parameterFile);
				
				if(path.getParent() != null)
					fd.setFilterPath(path.getParent().toString());
				else{
					fd.setFilterPath(path.toAbsolutePath().getParent().toString());
				}


				String[] filterExt = { "*.properties" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();

				if(selected!=null){
					if(isValidParameterFile(selected)){
						paramterFileTextBox.setText(selected);
					}
				}
				if(!parameterFile.equals(paramterFileTextBox.getText())){
					parameterFile=paramterFileTextBox.getText();
					getComponentCanvas().setCurrentParameterFilePath(parameterFile);
					loadParameterFile();	
				}
			}
		});
		btnNewButton.setText("...");
	}


	public String getParameterFile(){
		return parameterFile;
	}

	private void changeHeaderCheckboxSelection() {
		boolean allRowsSelected = true;
		for(Composite row:textGrid.getGrid()){
			if(((Button)((Composite)row).getChildren()[0]).isEnabled()){
				if(!((Button)row.getChildren()[0]).getSelection()){
					allRowsSelected = false;
					break;
				}
			}
		}

		if(allRowsSelected==true){
			headerCompositeCheckBox.setSelection(true);
		}else{
			headerCompositeCheckBox.setSelection(false);
		}
	}

	public void addGridRowSelectionListener(){
		for(Composite row: textGrid.getGrid()){

			//((Button)row.getChildren()[0]).

			((Button)row.getChildren()[0]).addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					super.widgetSelected(e);
					changeHeaderCheckboxSelection();
				}
			});
		}
	}

	private void addGridHeader() {
		List<String> header= new LinkedList<>();
		header.add("Name");
		header.add("Value");
		TextGridRowLayout textGridRowLayout = new TextGridRowLayout();
		textGridRowLayout.addColumn(new TextGridColumnLayout.Builder().columnWidth(90).enabled(false).build());
		textGridRowLayout.addColumn(new TextGridColumnLayout.Builder().grabHorizantalAccessSpace(true).enabled(false).build());
		textGrid.addHeaderRow(textGridRowLayout, header);


		((Button)textGrid.getHeaderComposite().getChildren()[0]).addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				super.widgetSelected(e);

				if(((Button)textGrid.getHeaderComposite().getChildren()[0]).getSelection()){
					textGrid.selectAllRows();
				}else{
					textGrid.clearSelections();
				}
			}

		});

		headerCompositeCheckBox = ((Button)textGrid.getHeaderComposite().getChildren()[0]);
		headerCompositeCheckBox.setEnabled(false);
	}

	private GridData getGridControlButtonLayout() {
		GridData gridControlButtonLayout = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gridControlButtonLayout.widthHint = 21;
		gridControlButtonLayout.heightHint = 19;
		return gridControlButtonLayout;
	}
	
	private IPath getParameterFileIPath(){
		IFileEditorInput input = (IFileEditorInput)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput() ;
	    IFile file = input.getFile();
	    IProject activeProject = file.getProject();
	    String activeProjectName = activeProject.getName();
	    
	    //java.nio.file.Path path= java.nio.file.Path(parameterFile)
	    Path filePath = Paths.get(parameterFile, "");
	    
	    IPath parameterFileIPath =new org.eclipse.core.runtime.Path("/"+activeProjectName+"/param/"+filePath.getFileName().toString().replace("job", "properties"));
	    
		return parameterFileIPath;
	}

	@Override
	protected void okPressed() {
		boolean error=false;


		//ParameterFileManager parameterFileManager = new ParameterFileManager(getComponentCanvas().getParameterFile());
		if(!paramterFileTextBox.getText().equals(parameterFile)){

			MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL );	        
			/*messageBox.setText("Error");
			messageBox.setMessage("The file " + paramterFileTextBox.getText() + " is not loded in grid\n" +
					"Pressing OK will override the existing file if any \n"
					+ "Press Reload Button to load the file in grid");*/
			messageBox.setText(MessageType.WARNING.toString());
			messageBox.setMessage(ErrorMessages.PARAMETER_FILE_NOT_LOADED.replace("{@}", paramterFileTextBox.getText()));
			int buttonID = messageBox.open();
			if(buttonID == SWT.OK){
				parameterFile = paramterFileTextBox.getText();
				getComponentCanvas().setCurrentParameterFilePath(parameterFile);
			}else{
				return;
			}

		}

		textGrid.clearSelections();

		Map<String,String> dataMap = new LinkedHashMap<>();
		int rowId=0;
		for(List<String> row: textGrid.getData()){
			dataMap.put(row.get(0), row.get(1));
			if(row.get(0) == null || row.get(0).equals("")){
				textGrid.selectRow(rowId);
				error=true;
			}
			rowId++;
		}
		if(error == false){
			try {
				ParameterFileManager.getInstance().storeParameters(dataMap,null,parameterFile);
			} catch (IOException e) {
				MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK );

				/*messageBox.setText("Error");
				messageBox.setMessage("Unable to store parameters to the file - \n" + e.getMessage());*/
				
				messageBox.setText(MessageType.ERROR.toString());
				messageBox.setMessage(ErrorMessages.UNABLE_TO_STORE_PARAMETERS);
				messageBox.open();
			}
			runGraph=true;
			super.okPressed();
		}else{
			MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK | SWT.CANCEL );

			/*messageBox.setText("Error");
			messageBox.setMessage("Parameter name can not be blank..please correct selected rows");*/
			messageBox.setText(MessageType.ERROR.toString());
			messageBox.setMessage(ErrorMessages.PARAMETER_NAME_CAN_NOT_BE_BLANK);
			int buttonID = messageBox.open();
			switch(buttonID) {
			case SWT.OK:
				runGraph = true;
				break;
			case SWT.CANCEL:
				super.okPressed();
				break;
			}
		}		
		
		IFile file=ResourcesPlugin.getWorkspace().getRoot().getFile(getParameterFileIPath());
		try {
				file.refreshLocal(IResource.DEPTH_ZERO, null);
			} catch (CoreException e) {
				logger.debug("Unable to refresh parameter file ", e);
			}
	}



	@Override
	protected void cancelPressed() {
		// TODO Auto-generated method stub
		runGraph = false;
		super.cancelPressed();
	}

	public boolean canRunGraph(){
		return runGraph;
	}

	private DefaultGEFCanvas getComponentCanvas() {		
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas)
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		else
			return null;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 423);
		//return new Point(450, 430);
	}

	public TextGrid getTextGrid() {
		return textGrid;
	}

}
