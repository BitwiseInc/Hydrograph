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

package hydrograph.ui.common.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * 
 * Class to store Image path constants
 * 
 * @author Bitwise
 * 
 */
public enum ImagePathConstant {
	ADD_BUTTON("/icons/add.png"),
	DELETE_BUTTON("/icons/delete.png"),
	MULTI_PARAMETERGRID_DROP_BOX("/icons/multiparamterdropbox.png"),
	MAC_ADD_BUTTON("/icons/iconadd.png"),
	MAC_DELETE_BUTTON ("/icons/icondelete.png"),
	MOVEUP_BUTTON("/icons/up.png"),
	MOVEDOWN_BUTTON("/icons/down.png"),
	EXPORT_SCHEMA_BUTTON("/icons/export_schema.png"),
	IMPORT_SCHEMA_BUTTON("/icons/import_schema.png"),
	SAVE_BUTTON("/icons/save_o.png"),
	PULL_BUTTON("/icons/pull_schema.png"),
	EDIT_BUTTON("/icons/editImage.png"),
	APP_ICON("/icons/app_icon.png"),
	UNCHECKALL_ICON("/icons/uncheckall.png"),
	CHECKALL_ICON("/icons/checkall.png"),
	COMPONENT_WARN_ICON("/icons/warn.png"),
	COMPONENT_ERROR_ICON("/icons/error.png"),
	COMPONENT_UPDATE_ICON("/icons/update.png"),
	SCHEMA_PROPOGATION_STOP_ICON("/icons/stop.png"),

	DATA_VIEWER_EXPORT("/icons/advicons/export.png"),
	DATA_VIEWER_FIND("/icons/advicons/lookup.png"),
	DATA_VIEWER_RELOAD("/icons/advicons/refresh.png"),
	DATA_VIEWER_FILTER("/icons/advicons/filter.png"),
	DATA_VIEWER_SWITCH_VIEW("/icons/advicons/switchview.png"),
	WARNING_ICON("/icons/advicons/warning.png"),
	TABLE_ICON("/icons/advicons/table.png"),
	DESELECT_ALL_ICON("/icons/advicons/deselectAll.png"),
	DESELECT_ICON("/icons/advicons/deselect.png"),
	DOWN_ICON("/icons/advicons/down.png"),
	UP_ICON("/icons/advicons/up.png"),
	SELECT_ICON("/icons/advicons/select.png"),
	SELECT_ALL_ICON("/icons/advicons/selectAll.png"),

	SORT_ASC("/icons/advicons/sort_asc.png"),
	SORT_DESC("/icons/advicons/sort_desc.png"),
	RESET_SORT("/icons/advicons/reset_sort.png"),
	FIND_DATA("/icons/advicons/lookup.png"),
	AUTO_ADJUST_COLUMNS("/icons/advicons/autoadjustcolumns.png"),
	COPY_ICON( "/icons/advicons/copy_icon.png"),
	DELETE_ICON("/icons/delete_icon.png"),
	COMPONENT_RUNNING_ICON("/icons/componentRun.png"),
	COMPONENT_SUCCESS_ICON("/icons/componentSuccess.png"),
	COMPONENT_FAILED_ICON("/icons/componentFail.png"),
	COMPONENT_PENDING_ICON("/icons/componentPending.png"),
	PASTE_IMAGE_PATH("icons/paste_o.png"),


	CLEAR_EXEC_TRACKING_CONSOLE( "/icons/clear_console.png"),
	CONSOLE_SCROLL_LOCK("/icons/console_scroll_lock.png"),
	
	ENABLE_COPY_ICON("/icons/enable_copy_icon.png"),
	INTELLISENCE_IMAGE("/icons/intellisense_icon.png");
	
	private static final ImageRegistry IMAGE_REGISTRY=new ImageRegistry();
	private String value;

	/**
	 * Returns Image from image registry
	 * 
	 * @return
	 */
	public Image getImageFromRegistry() {
		Image image = IMAGE_REGISTRY.get(this.value);
		if (image == null) {
			ImageData imageData=new ImageData(XMLConfigUtil.CONFIG_FILES_PATH + this.value); 
			IMAGE_REGISTRY.put(this.value,
					ImageDescriptor.createFromImageData(imageData ));
		}
		return IMAGE_REGISTRY.get(value);
	}
	
	private ImagePathConstant(String value) {
		this.value = value;
	}


	/**
	 * Returns the value for enum
	*/
	public String getValue() {
		return this.value;
	}
	
}
