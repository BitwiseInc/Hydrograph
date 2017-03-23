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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.core.component.utils;

import java.util.*;

/**
 * The Class OrderedProperties.
 *
 * @author Bitwise
 *
 */
public class OrderedProperties extends Properties {

	private static final long serialVersionUID = -1037857012056350940L;

	/**
	 * This object stores the property keys in order. This object is merely a
	 * copy of the property keys and has no connection to the actual underlying
	 * properties object. Care should be taken to properly maintain the keys in
	 * this object. Also, this object should not be directly exposed to the
	 * consumers. Any additions / deletion of the keys to this object will have
	 * no effect on the underlying property object.
	 */
	private Set<Object> keySet = new LinkedHashSet<Object>();

	/**
	 * Creates an empty property list with no default values.
	 */
	public OrderedProperties() {
		super();
	}

	/**
	 * Returns an enumeration of the property keys. The order of keys is
	 * maintained.
	 *
	 * @return an enumeration of the property keys in order.
	 * @see Enumeration
	 * @see java.util.Hashtable#elements()
	 * @see #keySet()
	 * @see Map
	 * @see java.util.Hashtable#keys()
	 */
	@Override
	public Enumeration<Object> keys() {
		return Collections.enumeration(keySet);
	}

	/**
	 * Returns a {@link Set} of the property keys in order. This method just
	 * returns a read-only object. Changes made on this object have no effect on
	 * the actual properties object. Use {@link #put(Object, Object) put()} or
	 * {@link #remove(Object) remove()} methods to add or remove keys / values
	 * from the properties object.
	 * 
	 * @return a {@link Set} of the property keys in order.
	 */
	@Override
	public Set<Object> keySet() {
		// Defensive copying to avoid modifying internal object by the consumer
		return new LinkedHashSet<Object>(keySet);
	}

	/**
	 * Maps the specified <code>key</code> to the specified <code>value</code>
	 * in this properties object. Neither the key nor the value can be
	 * <code>null</code>.
	 * <p>
	 *
	 * The value can be retrieved by calling the <code>get</code> method with a
	 * key that is equal to the original key.
	 * <p>
	 * 
	 * The order of the keys is retained in this properties object. Keys can be
	 * retreived in the same order they are put in the properties object.
	 * 
	 * @param key
	 *            the property key
	 * @param value
	 *            the value
	 * @return the previous value of the specified key, or <code>null</code> if
	 *         it did not have one
	 * @exception NullPointerException
	 *                if the key or value is <code>null</code>
	 * @see Object#equals(Object)
	 * @see #get(Object)
	 * 
	 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public synchronized Object put(Object key, Object value) {
		if (!keySet.contains(key)) {
			keySet.add(key);
		}
		return super.put(key, value);
	}

	/**
	 * Removes the key (and its corresponding value) from this properties
	 * object. This method does nothing if the key is not present.
	 *
	 * @param key
	 *            the key that needs to be removed
	 * @return the value to which the key had been mapped in this properties
	 *         object, or <code>null</code> if the key did not have a mapping
	 * @throws NullPointerException
	 *             if the key is <code>null</code>
	 * @see java.util.Hashtable#remove(java.lang.Object)
	 */
	@Override
	public synchronized Object remove(Object key) {
		keySet.remove(key);
		return super.remove(key);
	}

	/**
	 * Copies all of the mappings from the specified map to this properties
	 * object. These mappings will replace any mappings that this properties
	 * object had for any of the keys currently in the specified map.
	 * <p>
	 *
	 * The map implementation used for <code>values</code> parameter drives the
	 * order of the keys in this properties object.
	 *
	 * @param values
	 *            mappings to be stored in this map
	 * @throws NullPointerException
	 *             if the specified map is null
	 * 
	 * @see java.util.Hashtable#putAll(java.util.Map)
	 */
	@Override
	public synchronized void putAll(Map values) {
		for (Object key : values.keySet()) {
			if (!containsKey(key)) {
				keySet.add(key);
			}
		}
		super.putAll(values);
	}

	/**
	 * Returns an enumeration of all the keys in this property list in the
	 * order.
	 *
	 * @return an enumeration of all the keys in this property list in order
	 * @throws ClassCastException
	 *             if any key in this property list is not a string.
	 * @see java.util.Enumeration
	 * @see java.util.Properties#defaults
	 * @see #stringPropertyNames
	 * @see java.util.Properties#propertyNames()
	 */
	@Override
	public Enumeration<?> propertyNames() {
		return Collections.enumeration(keySet);
	}

	/**
	 * Returns a {@link Set} view of the mappings contained in this properties
	 * object. The order of property keys is maintained.
	 * 
	 * @see java.util.Hashtable#entrySet()
	 */
	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		Map<Object, Object> map = new LinkedHashMap<Object, Object>();
		for (Object key : keySet) {
			map.put(key, this.get(key));
		}
		return map.entrySet();
	}

	/**
	 * Returns an enumeration of all the keys in this property list in the
	 * order. The key fields are explicitly cast to String.
	 *
	 * @return an enumeration of all the keys in this property list in order
	 * @throws ClassCastException
	 *             if any key in this property list is not a string.
	 * @see java.util.Enumeration
	 * @see java.util.Properties#defaults
	 * @see #propertyNames
	 * @see java.util.Properties#stringPropertyNames()
	 */
	@Override
	public Set<String> stringPropertyNames() {
		Set<String> stringKeySet = new LinkedHashSet<String>();
		for (Object key : keySet) {
			stringKeySet.add(key.toString()); // Convert Set<Object> to
												// Set<String>
		}
		return stringKeySet;
	}
}