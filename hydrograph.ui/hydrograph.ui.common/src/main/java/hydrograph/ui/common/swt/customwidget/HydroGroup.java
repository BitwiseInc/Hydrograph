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

package hydrograph.ui.common.swt.customwidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import hydrograph.ui.common.util.CustomColorRegistry;

/**
 * The custom SWT group widget
 * 
 * @author shrirangk
 *
 */
public class HydroGroup extends Composite {
	private CLabel hydroGroupLabel;
	private Composite hydroGroupBorder;
	private Composite hydroGroupClientArea;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HydroGroup(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		setData("org.eclipse.e4.ui.css.id", "HydrographGroup");
		
		hydroGroupLabel = new CLabel(this, SWT.NONE);
		hydroGroupLabel.setText("Hydro Group Label");
		hydroGroupLabel.setData("org.eclipse.e4.ui.css.id", "HydroGroupLabel");
		
		hydroGroupBorder = new Composite(this, SWT.NONE);
		hydroGroupBorder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_hydroGroupBorder = new GridLayout(1, false);
		gl_hydroGroupBorder.verticalSpacing = 1;
		gl_hydroGroupBorder.marginWidth = 1;
		gl_hydroGroupBorder.marginHeight = 1;
		gl_hydroGroupBorder.horizontalSpacing = 1;
		hydroGroupBorder.setLayout(gl_hydroGroupBorder);
		hydroGroupBorder.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 0, 0));
		hydroGroupBorder.setData("org.eclipse.e4.ui.css.id", "HydroGroupBorder");
		
		hydroGroupClientArea = new Composite(hydroGroupBorder, SWT.NONE);
		hydroGroupClientArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		hydroGroupClientArea.setData("org.eclipse.e4.ui.css.id", "HydroGroupClientArea");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public String getHydroGroupText() {
		return hydroGroupLabel.getText();
	}
	public void setHydroGroupText(String text) {
		hydroGroupLabel.setText(text);
	}
	public Image getHydroGroupImage() {
		return hydroGroupLabel.getImage();
	}
	public void setHydroGroupImage(Image image) {
		hydroGroupLabel.setImage(image);
	}	
	
	public Color getHydroGroupBorderBackground() {
		return hydroGroupBorder.getBackground();
	}
	public void setHydroGroupBorderBackground(Color background) {
		hydroGroupBorder.setBackground(background);
	}
	
	public Color getHydroGroupWidgetBackground() {
		return getBackground();
	}
	public void setHydroGroupWidgetBackground(Color background) {
		setBackground(background);
		hydroGroupLabel.setBackground(background);
	}	
	public Color getHydroGroupWidgetForeground() {
		return hydroGroupLabel.getBackground();
	}
	public void setHydroGroupWidgetForeground(Color background) {
		hydroGroupLabel.setForeground(background);
	}
	public Font getHydroGroupLabelFont() {
		return hydroGroupLabel.getFont();
	}
	public void setHydroGroupLabelFont(Font font) {
		hydroGroupLabel.setFont(font);
	}
	public Color getHydroGroupClientAreaBackgroundColor() {
		return hydroGroupClientArea.getBackground();
	}
	public void setHydroGroupClientAreaBackgroundColor(Color background) {
		hydroGroupClientArea.setBackground(background);
	}
	public Composite getHydroGroupClientArea() {
		return hydroGroupClientArea;
	}
}
