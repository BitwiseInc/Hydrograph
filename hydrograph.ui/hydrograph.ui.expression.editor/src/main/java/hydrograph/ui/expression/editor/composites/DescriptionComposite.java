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

import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.browser.JavaDocBrowser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DescriptionComposite extends Composite {

	private JavaDocBrowser descriptionStyledText;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param functionsComposite 
	 * @param categoriesComposite 
	 * @param style
	 */
	public DescriptionComposite(Composite parent, FunctionsComposite functionsComposite, CategoriesComposite categoriesComposite, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Composite upperComposite = new Composite(this, SWT.BORDER);
		upperComposite.setLayout(new GridLayout(1, false));
		GridData gd_upperComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_upperComposite.heightHint = 35;
		upperComposite.setLayoutData(gd_upperComposite);
		
		Label lblDescription = new Label(upperComposite, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblDescription.setText("Description");
		
		
		final Composite composite = new Composite(this, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		composite.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				descriptionStyledText.setSize(composite.getSize().x-5,composite.getSize().y);
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		descriptionStyledText=new JavaDocBrowser(composite,  SWT.NORMAL);
		descriptionStyledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		descriptionStyledText.setFont(new Font(null,"Arabic Transparent", 18, SWT.NORMAL));
		descriptionStyledText.setText(Constants.HTML_START_TAG+Constants.HTML_END_TAG);
		linkFunctionAndDescription(functionsComposite);
		linkCategoriesAndDescription(categoriesComposite);
	}

	private void linkCategoriesAndDescription(CategoriesComposite categoriesComposite) {
		categoriesComposite.setDescriptionStyledText(descriptionStyledText);
	}

	private void linkFunctionAndDescription(FunctionsComposite functionsComposite) {
		functionsComposite.setDescriptionStyledText(descriptionStyledText);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
}
