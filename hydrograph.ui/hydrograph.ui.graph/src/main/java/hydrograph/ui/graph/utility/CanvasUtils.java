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

 
package hydrograph.ui.graph.utility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.util.XMLUtil;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * 
 * This class provides Canvas related utilites
 * @author Bitwise
 *
 */
public class CanvasUtils {
	
	public static final CanvasUtils INSTANCE = new CanvasUtils();
	private static final Logger logger = LogFactory.INSTANCE.getLogger(CanvasUtils.class);
	
	private CanvasUtils() {
	}
	/**
	 * 
	 * Returns instance of active canvas
	 * 
	 * @return {@link DefaultGEFCanvas}
	 */
	public DefaultGEFCanvas getComponentCanvas() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas)
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
		else
			return null;
	}

	/**
	 * 
	 * Returns true if canvas is dirty otherwise false
	 * 
	 * @return boolean
	 */
	public boolean isDirtyEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().isDirty();
	}
	
	/**
	 * From xml to object.
	 * 
	 * @param xml
	 *            the xml
	 * @return the object
	 */
	public Object fromXMLToObject(InputStream xml) {

		Object obj = null;

		XStream xs = XStreamUtil.INSTANCE.getXStreamInstance();
		try {

			obj = xs.fromXML(xml);
			logger.debug("Sucessfully converted JAVA Object from XML Data");
			xml.close();
		} catch (Exception e) {
			logger.error("Failed to convert from XML to Graph due to : {}", e);
			MessageDialog.openError(new Shell(), "Error", "Invalid graph file.");
		}
		return obj;
	}

	/**
	 * From object to xml.
	 * 
	 * @param object
	 *            the object
	 * @return the string
	 */
	public void fromObjectToXML(Serializable object,ByteArrayOutputStream outputStream) {

		String str = "<!-- It is recommended to avoid changes to xml data -->\n\n";

		XStream xs = new XStream();
		xs.autodetectAnnotations(true);
		try {
			xs.toXML(object, outputStream);
			logger.debug( "Sucessfully converted XML from JAVA Object");
		} catch (Exception e) {
			logger.error("Failed to convert from Object to XML", e);
		}
		XMLUtil.unformatXMLString(outputStream);
	}
	 
	

}
