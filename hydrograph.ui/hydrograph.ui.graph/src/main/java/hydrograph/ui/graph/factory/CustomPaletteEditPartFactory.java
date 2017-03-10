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

package hydrograph.ui.graph.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.internal.ui.palette.editparts.DrawerEditPart;
import org.eclipse.gef.internal.ui.palette.editparts.DrawerFigure;
import org.eclipse.gef.internal.ui.palette.editparts.SliderPaletteEditPart;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteEditPartFactory;
import org.eclipse.swt.graphics.Color;



/**
 * A factory for creating CustomPaletteEditPart objects.
 */
public class CustomPaletteEditPartFactory extends PaletteEditPartFactory {

    private Color palatteTextColor;
    private Color palatteBackgroundColor;
    
    private List<DrawerFigure> drawerFigures;
    private IFigure paletteTextFigure;
	/**
	 * Instantiates a new custom palette edit part factory.
	 * 
	 * @param palatteTextColor
	 * @param palattebackgroundcolor2 
	 */
    public CustomPaletteEditPartFactory(Color palatteTextColor, Color palattebackgroundcolor) {
    	this.palatteTextColor = palatteTextColor;
    	this.palatteBackgroundColor=palattebackgroundcolor;
    	drawerFigures = new ArrayList<>();
    }
    
    /**
     * Returns list of palette drawers
     * @return List of {@link DrawerFigure}
     */
    public List<DrawerFigure> getDrawerFigures() {
		return drawerFigures;
	}
    
    /**
     * 
     * Returns paletteTextFigure
     * 
     * @return {@link IFigure}
     */
    public IFigure getPaletteTextFigure() {
		return paletteTextFigure;
	}
    
    @Override
    protected EditPart createMainPaletteEditPart(EditPart parentEditPart, Object model) {
		return new SliderPaletteEditPart((PaletteRoot) model) {
			@Override
			public IFigure createFigure() {
				paletteTextFigure = super.createFigure();
				paletteTextFigure.setForegroundColor(palatteTextColor);
				paletteTextFigure.setBackgroundColor(palatteBackgroundColor);
				return paletteTextFigure;
			}
		};
    }
    @Override
	protected EditPart createDrawerEditPart(EditPart parentEditPart, Object model) {
		return new DrawerEditPart((PaletteDrawer) model) {
			@Override
			public IFigure createFigure() {
				DrawerFigure drawerFigure = (DrawerFigure) super.createFigure();
				drawerFigure.getContentPane().setBackgroundColor(palatteBackgroundColor);
				drawerFigures.add(drawerFigure);
				return drawerFigure;
			}

		};
	}
        
}
