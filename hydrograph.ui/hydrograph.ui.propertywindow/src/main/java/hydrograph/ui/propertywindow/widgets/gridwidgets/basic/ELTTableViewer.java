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

 
package hydrograph.ui.propertywindow.widgets.gridwidgets.basic;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * The Class ELTTableViewer.
 * 
 * @author Bitwise
 */
public class ELTTableViewer extends AbstractELTWidget{

	IStructuredContentProvider iStructuredContentProvider;
	ITableLabelProvider iTableLabelProvider;
	
	/**
	 * Instantiates a new ELT table viewer.
	 * 
	 * @param iStructuredContentProvider
	 *            the i structured content provider
	 * @param iTableLabelProvider
	 *            the i table label provider
	 */
	public ELTTableViewer(
			IStructuredContentProvider iStructuredContentProvider,
			ITableLabelProvider iTableLabelProvider) {
		super();
		this.iStructuredContentProvider = iStructuredContentProvider;
		this.iTableLabelProvider = iTableLabelProvider;
	}

	@Override
	public void attachWidget(Composite container) {
		TableViewer tableViewer = new TableViewer(container, SWT.BORDER|SWT.CENTER | SWT.MULTI | SWT.FULL_SELECTION |SWT.H_SCROLL |SWT.V_SCROLL|SWT.RESIZE);
		tableViewer.setContentProvider(iStructuredContentProvider);
		tableViewer.setLabelProvider(iTableLabelProvider);
		jfaceWidgets = tableViewer;
		widget = tableViewer.getTable();
		
	}

}
