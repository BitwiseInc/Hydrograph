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

package hydrograph.ui.propertywindow.widgets.dialog.hiveInput;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * The class is responsible for calling SingleCLick listener property.
 * 
 * @author Bitwise
 *
 */
public class SingleClickEvent extends MouseAdapter {

	public static final int LEFT_BUTTON = 1;
	private final Runnable action;
	private static Boolean armed;

	public SingleClickEvent(Runnable action) {
		this.action = action;
	}

	@Override
	public void mouseUp(MouseEvent event) {
		if (armed && inRange(event)) {
			action.run();
		}
		armed = false;
	}
	@Override
	public void mouseDown(MouseEvent event) {
		if (event.button == LEFT_BUTTON) {
			armed = true;
		}
	}
	
	private static boolean inRange(MouseEvent event) {
		Point size = ((Control) event.widget).getSize();
		return event.x >= 0 && event.x <= size.x && event.y >= 0 && event.y <= size.y;
	}
}
