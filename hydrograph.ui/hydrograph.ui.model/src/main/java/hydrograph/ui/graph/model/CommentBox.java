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
import org.eclipse.draw2d.geometry.Point;

import hydrograph.ui.graph.model.Component.Props;

/**
 * The Class CommentBox.
 * Represents the Comments for Job in the job canvas.
 * 
 * @author Bitwise
 */

public class CommentBox extends Model{

	private static final long serialVersionUID = 1201944115887893345L;
	private String text;
	private Dimension size;
	private  int count;
	private Container parent;
	private Point location = new Point(0, 0);
	private boolean newInstance;
	/**
	 * Instantiates a new CommentBox.
	 * 
	 * @param compLabel
	 *            the comp label
	 */
	public CommentBox(String compLabel){
		size = new Dimension(300, 60);
		this.text = compLabel;
		newInstance = true;
		if(count>0){
			location = new Point(getLocation().x+count*10, getLocation().y+count*10);
		}
		
	}

	/**
	 * Gets the label contents.
	 * 
	 * @return the label contents
	 */
	public String getLabelContents(){
		return text;
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
	public void setSize(Dimension d){
		if (size.equals(d))
			return;
		size = d;
		firePropertyChange("Size", null, size);
	}

	/**
	 * Sets the label contents.
	 * 
	 * @param s
	 *            the new label contents
	 */
	public void setLabelContents(String s){
		text = s;
		firePropertyChange("labelContents", null, text); //$NON-NLS-2$//$NON-NLS-1$
	}

	public String toString(){
		return "Label"
				+ "=" + getLabelContents(); //$NON-NLS-1$ 
	}
	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public Container getParent(){
		return parent;
	}
	/**
	 * Sets the parent.
	 * 
	 * @return the parent
	 */
	public void setParent(Container parent){
		this.parent = parent;
	}
	
	/**
	 * Gets the newLocation.
	 * 
	 * @return the newLocation
	 */
	public void setLocation(Point newLocation){
		resetLocation(newLocation);
		location.setLocation(newLocation);
		firePropertyChange(Props.LOCATION_PROP.getValue(), null, location);
	}

	/**
	 * reset if x or y coordinates of components are negative
	 * 
	 * @param newLocation
	 */
	private void resetLocation(Point newLocation) {
		if (newLocation.x < 0) {
			newLocation.x = 0;
		}

		if (newLocation.y < 0) {
			newLocation.y = 0;
		}
	}

	/**
	 * Return the Location of this shape.
	 * 
	 * @return a non-null location instance
	 */
	public Point getLocation() {
		return location.getCopy();
	}

	/**
	 * Checks if it's a new instance.
	 * 
	 * @return true, if is new instance
	 */
	public boolean isNewInstance() {
		return newInstance;
	}

	/**
	 * Sets the new instance.
	 *
	 * @param newInstance the new new instance
	 */
	public void setNewInstance(boolean newInstance) {
		this.newInstance = newInstance;
	}
	/**
	 * Sets the count.
	 * 
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * Sets the count.
	 * 
	 * @return the count
	 */
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public CommentBox clone() throws CloneNotSupportedException{
		CommentBox label = null;
		label = new CommentBox(getLabelContents());
			
		if(label!=null){
		label.setParent(getParent());
		label.setLabelContents(getLabelContents());
		label.setSize(getSize());
		label.setLocation(getLocation());
		}
		return label;
	}

}
