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

package hydrograph.ui.tooltip.tooltips;

import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.tooltip.utils.ToolTipUtils;

import java.nio.CharBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;


/**
 * 
 * This is custom tooltip, to be shown in Component Palette
 * 
 * @author Bitwise
 *
 */

public class PaletteToolTip extends Shell {
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(PaletteToolTip.class);
	
	private PaletteToolTip thisTestPaletteToolTip;
	private Composite toolTipComposite;
	private Label toolTipText;
	private String blankCharacters;
	private Link helpLink;	
	private String componentName;
	
	/**
	 * 
	 * Computes height and width of tooltip and saves it in Point,
	 * where Point.x is width and Point.y is height
	 * 
	 * @return org.eclipse.swt.graphics.Point
	 */
	private Point getToolTipWidthHeight() {
		Point tooltipSize = toolTipComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		logger.debug("Computed Width=" + tooltipSize.x + "Height=" + tooltipSize.y);
		return tooltipSize;
	}
	
	/**
	 * 
	 * Returns tooltip bounds
	 * 
	 * @return org.eclipse.swt.graphics.Rectangle
	 */
	public Rectangle getToolTipBounds(){
		Point tooltipSize = getToolTipWidthHeight();
		Rectangle bounds = new Rectangle(0, 0, tooltipSize.x, tooltipSize.y);
		logger.debug("tooltip bounds=" + bounds);
		return bounds;
	}



	
	/**
	 * set tooltip text
	 * 
	 * @param text - text message to set
	 */
	public void setToolTipText(String text){
		String[] lines = ToolTipUtils.getLines(text);
		int maxLength = ToolTipUtils.getMaxLength(lines);		
		toolTipText.setText(text.replace("\\n", "\n")+" ");
		setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		logger.debug("set tooltip text - " + text);
	}
	
	
	/**
	 * Create the shell.
	 * @param display
	 */
	public PaletteToolTip(Display display) {
		super(display, SWT.NONE);		
		thisTestPaletteToolTip = this;
				
		setLayoutToOuterMostContainer();
		addToolTipComposite();
		logger.debug("created tooltip box");
		
		addToolTipTextArea();
		logger.debug("added tooltip textarea");
		addSeparator();
		logger.debug("added separator");
		addHelpLink();
		logger.debug("added help link");
		
		addListenersToHideToolTip();
		logger.debug("tooltip hide listener");
		
		createContents();
	}

	/**
	 * Create link for "Help"
	 */
	private void addHelpLink() {
		helpLink = new Link(toolTipComposite, SWT.NONE);
		helpLink.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		helpLink.setText("<a>Help</a> ");
		helpLink.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		helpLink.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String helpFilePath = XMLConfigUtil.INSTANCE.getComponent(componentName).getHelpFilePath();
				PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(
						helpFilePath);
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//Do Nothing				
			}
		});
		logger.debug("Added help link");
	}

	/**
	 * Create a separator between tooltip text area and add "Help" link
	 */
	private void addSeparator() {
		Label label = new Label(toolTipComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		logger.debug("Added help link-tooltip text separator");
	}

	/**
	 * Add tooltip text area - toolTipText(Label hydrograph.ui.tooltip.tooltips.PaletteToolTip.toolTipText)
	 */
	private void addToolTipTextArea() {
		toolTipText = new Label(toolTipComposite, SWT.NONE);
		toolTipComposite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		toolTipText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		toolTipComposite.setSize(toolTipComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));		
		toolTipText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		logger.debug("Initialized Tooltip text area");
	}

	private void addListenersToHideToolTip() {
		addToolTipTextAreaMouseTrackListener();
		addToolTipHelpLinkMouseTrackListener();
		logger.debug("added listeners to hide tooltip");
	}

	/**
	 * help link Mouse exit event to hide tooltip 
	 */
	private void addToolTipHelpLinkMouseTrackListener() {
		helpLink.addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {
				//Do Nothing
				
			}
			
			@Override
			public void mouseExit(MouseEvent e) {
				logger.debug("PaletteToolTip.addToolTipHelpLinkMouseTrackListener(mouseExit) :" +
						" (e.x , toolTipText.getBounds().width , e.y , toolTipText.getBounds().height)= " + 
						e.x + " , " + toolTipText.getBounds().width + e.x + " , " +  e.y + e.x + " , "
						+  toolTipText.getBounds().height );
				
				if(e.x < 0 || e.x > toolTipText.getBounds().width ||  e.y > toolTipText.getBounds().height){
					logger.debug("PaletteToolTip.addToolTipHelpLinkMouseTrackListener: hiding tooltip");
					thisTestPaletteToolTip.setVisible(false);
				}
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				helpLink.setFocus();
			}
		});
	}

	/**
	 * toolTipText area Mouse exit event to hide tooltip 
	 */
	private void addToolTipTextAreaMouseTrackListener() {
		toolTipText.addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {
				//Do Nothing
				
			}
			
			@Override
			public void mouseExit(MouseEvent e) {
				logger.debug("PaletteToolTip.addToolTipTextAreaMouseTrackListener(mouseExit) :" +
						" (e.x , toolTipText.getBounds().width , e.y , toolTipText.getBounds().height)= " + 
						e.x + " , " + toolTipText.getBounds().width + e.x + " , " +  e.y + e.x + " , "
						+  toolTipText.getBounds().height );
				
				if(e.y < 0 || e.x < 0 || e.x > toolTipText.getBounds().width){
					logger.debug("PaletteToolTip.addToolTipHelpLinkMouseTrackListener: hiding tooltip");
					thisTestPaletteToolTip.setVisible(false);
				}
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				//Do Nothing
			}
		});
	}

	/**
	 * Add main tooltip composite
	 */
	private void addToolTipComposite() {
		toolTipComposite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		toolTipComposite.setLayout(gl_composite);
	}

	/**
	 * Set Layout To Outer Most Container
	 */
	private void setLayoutToOuterMostContainer() {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String name) {
		this.componentName = name;
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(this.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}
	
	@Override 
    protected void checkSubclass() { 
            // Disable the check that prevents subclassing of SWT components 
    }
}
