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
 
package hydrograph.ui.propertywindow.widgets.gridwidgets.container;

import org.eclipse.swt.widgets.Composite;

import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;

/**
 * 
 * @author Bitwise
 * Sep 18, 2015
 * 
 */

public abstract class AbstractELTContainerWidget {
	protected Composite inputContainer;
	protected Composite outputContainer;
	
	/**
	 * Instantiates a new abstract elt container widget.
	 * 
	 * @param container
	 *            the container
	 */
	public AbstractELTContainerWidget(Composite container){
		this.inputContainer = container;
	};
	
	
	public AbstractELTContainerWidget(Composite container,Composite clientArea){
		this.inputContainer = container;
		this.outputContainer = clientArea;
	};
	
	/**
	 * Creates the container widget.
	 */
	public abstract void createContainerWidget();
	
	/**
	 * Number of basic widgets.
	 * 
	 * @param subWidgetCount
	 *            the sub widget count
	 * @return the abstract elt container widget
	 */
	public abstract AbstractELTContainerWidget numberOfBasicWidgets(int subWidgetCount);
	
	/**
	 * Attach widget.
	 * 
	 * @param eltWidget
	 *            the elt widget
	 */
	public abstract void attachWidget(AbstractELTWidget eltWidget);
	
	public Composite getContainerControl(){
		return outputContainer;
	}
}
