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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * The Class Model.
 * <p>
 * The Base Model class for the Hydrograph components. 
 * 
 * @author Bitwise
 */
public abstract class Model implements Serializable {

	private static final long serialVersionUID = -4073149938391231758L;
	
	private final PropertyChangeSupport propertyChangeSupport;
		
	/**
	 * Instantiates a new model.
	 */
	Model(){
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	/**
	 * Attach a non-null PropertyChangeListener to this object.
	 * 
	 * @param listner a non-null PropertyChangeListener instance
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener listner) {
		propertyChangeSupport.addPropertyChangeListener(listner);
	}
	
	
	/**
	 * Remove a PropertyChangeListener from this component.
	 * @param listner PropertyChangeListener instance
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener listner) {
		if (listner != null) {
			propertyChangeSupport.removePropertyChangeListener(listner);
		}
	}
	
	/**
	 * Report a property change to registered listeners (for example editparts).
	 * @param property the programmatic name of the property that changed
	 * @param oldValue the old value of this property
	 * @param newValue the new value of this property
	 */
	protected void firePropertyChange(String property, Object oldValue,
			Object newValue) {
		if (propertyChangeSupport.hasListeners(property)) {
			propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
		}
	}
}
