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
package hydrograph.engine.spark.components.utils;

import hydrograph.engine.transformation.userfunctions.base.ListBasedReusableRow;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
/**
 * The Class SparkReusableRow.
 *
 * @author Bitwise
 *
 */
public class SparkReusableRow extends ListBasedReusableRow implements Serializable{

	public SparkReusableRow(LinkedHashSet<String> fields) {
		super(fields);
	}

	/**
	 * The generic method to set a value to field
	 * 
	 * @param fieldName
	 *            The name of the field
	 * @param value
	 *            The value to set
	 */
	@Override
	public void setField(String fieldName, Comparable value) {
		if (value instanceof Date) {
			Date date = (Date) value;
			setFieldInternal(fieldName,date.getTime());
			// } else if (value == null
			// || (value instanceof String && ((String) value).equals(""))) {
			// valueMap.put(fieldName, null);
		} else {
			setFieldInternal(fieldName, value);
		}
	}
	/**
	 * The generic method to set a value to field
	 * 
	 * @param index
	 *            The index of the field
	 * @param value
	 *            The value to set
	 */
	@Override
	public void setField(int index, Comparable value) {
		if (value instanceof Date) {
			Date date = (Date) value;
			setFieldInternal(index,date.getTime());
			// } else if (value == null
			// || (value instanceof String && ((String) value).equals(""))) {
			// values.set(index, null);
		} else {
			setFieldInternal(index, value);
		}
	}
	/**
	 * Sets a date value to the field
	 * 
	 * @param fieldName
	 *            The name of the field to set the value
	 * @param value
	 *            The value to be set
	 */
	public void setDate(String fieldName, Comparable value) {
		if (value instanceof Date) {
			Date date = (Date) value;
			setFieldInternal(fieldName, date.getTime());
		} else if (value == null || (value instanceof String && ((String) value).equals(""))) {
			setFieldInternal(fieldName, null);
		}
	}

	/**
	 * Sets a date value to the field
	 * 
	 * @param index
	 *            The index of the field to set the value
	 * @param value
	 *            The value to be set
	 */
	public void setDate(int index, Comparable value) {
		if (value instanceof Date) {
			Date date = (Date) value;
			setFieldInternal(index, date.getTime());
		} else if (value == null || (value instanceof String && ((String) value).equals(""))) {
			setFieldInternal(index, null);
		}
	}
	
	/**
	 * Fetches a date field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Date getDate(String fieldName){
		if (getFieldInternal(fieldName) != null) {
			Long date = (Long) getFieldInternal(fieldName);
			return new Date(date);
		}
		return null;
	}
	
	/**
	 * Fetches a date field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Date getDate(int index) {
		if (getFieldInternal(index) != null) {
			Long date = (Long) getFieldInternal(index);
			return new Date(date);
		}
		return null;
	}
}
