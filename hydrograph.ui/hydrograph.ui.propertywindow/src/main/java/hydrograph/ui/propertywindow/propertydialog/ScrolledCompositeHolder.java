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

 
package hydrograph.ui.propertywindow.propertydialog;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 08, 2015
 * 
 */

public class ScrolledCompositeHolder {
	private Composite composite;
	private ScrolledComposite scrolledComposite;
		
	/**
	 * Instantiates a new scrolled composite holder.
	 * 
	 * @param scrolledComposite
	 *            the scrolled composite
	 * @param composite
	 *            the composite
	 */
	public ScrolledCompositeHolder(ScrolledComposite scrolledComposite , Composite composite) {
		super();
		this.composite = composite;
		this.scrolledComposite = scrolledComposite;
	}

	public Composite getComposite() {
		return composite;
	}

	public ScrolledComposite getScrolledComposite() {
		return scrolledComposite;
	}
	
}
