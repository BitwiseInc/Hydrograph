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

package hydrograph.ui.dataviewer.actions;

import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.SWTResourceManager;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.dataviewer.window.DebugDataViewer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * The class CopyAction.
 * Used to copy contents from view data window.
 * 
 * @author Bitwise
 *
 */
public class CopyAction extends Action {
	
	private static final String LABEL="&Copy@Ctrl+C";
	private DebugDataViewer debugDataViewer;
	
	public CopyAction(DebugDataViewer debugDataViewer) {
		super(LABEL);
		this.debugDataViewer = debugDataViewer;
		setAccelerator(SWT.MOD1 | 'c');
		setImageDescriptor(ImageDescriptor.createFromImage(ImagePathConstant.COPY_ICON.getImageFromRegistry()));
	}
	@Override
	public void run() {
		if (debugDataViewer.getUnformattedViewTextarea()!=null && debugDataViewer.getUnformattedViewTextarea().isVisible()){
			debugDataViewer.getUnformattedViewTextarea().copy();
		}else if (debugDataViewer.getFormattedViewTextarea()!=null && debugDataViewer.getFormattedViewTextarea().isVisible()){
			debugDataViewer.getFormattedViewTextarea().copy();
		}else{
			copySelectedAsTabDelimited();
		}	
	}
	
	// This method is used to copy all selected rows as tab delimited
	private void copySelectedAsTabDelimited() {
		StringBuffer stringBuffer = new StringBuffer();
		int totalRowCount = debugDataViewer.getTableViewer().getTable().getItemCount();
		int totalColumnCount = debugDataViewer.getTableViewer().getTable().getColumnCount();
		boolean hasRow=false;
		for (int rowCount = 0; rowCount < totalRowCount; rowCount++) {
			for (int columnCount = 0; columnCount < totalColumnCount; columnCount++) {
				Point cell = new Point(rowCount, columnCount);
				if(debugDataViewer.getSelectedCell().contains(cell)){
					stringBuffer.append(debugDataViewer.getTableViewer().getTable().getItem(rowCount).getText(columnCount) + "\t");
					hasRow=true;
				}
				cell=null;
			}
			if(hasRow){
				stringBuffer.append("\n");
				hasRow=false;
			}				
		}
		Clipboard cb = new Clipboard(Display.getCurrent());
		TextTransfer textTransfer = TextTransfer.getInstance();
		String textData = stringBuffer.toString();
		cb.setContents(new Object[] { textData }, new Transfer[] { textTransfer });
		cb.dispose();
		
	}
	
}
