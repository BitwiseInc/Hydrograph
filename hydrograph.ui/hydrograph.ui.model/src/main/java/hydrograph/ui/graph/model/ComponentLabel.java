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

 
package hydrograph.ui.graph.model;

import java.io.IOException;

import org.eclipse.draw2d.geometry.Dimension;

/**
 * The Class ComponentLabel.
 * Represents the label ( Name ) for each components that is present in the job canvas.
 * 
 * @author Bitwise
 */

public class ComponentLabel extends Model{

	static final long serialVersionUID = 1;
	private String text;
	private Dimension size = new Dimension(-1, 16);;

	private static int count;

	/**
	 * Instantiates a new component label.
	 * 
	 * @param compLabel
	 *            the comp label
	 */
	public ComponentLabel(String compLabel) {
		this.text = compLabel;
	}

	/**
	 * Gets the label contents.
	 * 
	 * @return the label contents
	 */
	public String getLabelContents() {
		return text;
	}

	/**
	 * Gets the new ID.
	 * 
	 * @return the new ID
	 */
	protected String getNewID() {
		return Integer.toString(count++);
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public Dimension getSize() {
		return size;
	}

	private void readObject(java.io.ObjectInputStream s) throws IOException,
	ClassNotFoundException {
		s.defaultReadObject();
	}

	/**
	 * Sets the size.
	 * 
	 * @param d
	 *            the new size
	 */
	public void setSize(Dimension d) {
		if (size.equals(d))
			return;
		size = d;
		firePropertyChange("compLabelSize", null, size);
	}

	/**
	 * Sets the label contents.
	 * 
	 * @param s
	 *            the new label contents
	 */
	public void setLabelContents(String s) {
		text = s;
		firePropertyChange("labelContents", null, text); //$NON-NLS-2$//$NON-NLS-1$
	}

	public String toString() {
		return "Label"
				+ "=" + getLabelContents(); //$NON-NLS-1$ 
	}

}
