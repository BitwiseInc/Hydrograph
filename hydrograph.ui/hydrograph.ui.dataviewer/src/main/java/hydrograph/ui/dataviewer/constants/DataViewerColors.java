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
package hydrograph.ui.dataviewer.constants;

import org.eclipse.swt.graphics.Color;

import hydrograph.ui.common.util.CustomColorRegistry;

/**
 * The Class DataViewerColors.
 * Provides Color constants to be used in watcher windows.
 * 
 * @author Bitwise
 *
 */
public class DataViewerColors {
	public static final Color COLOR_WHITE=CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255);
	public static final Color COLOR_CELL_SELECTION=CustomColorRegistry.INSTANCE.getColorFromRegistry( 218, 234, 255);
}
