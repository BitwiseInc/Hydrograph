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
package hydrograph.engine.transformation.userfunctions.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

/**
 * The Class ReusableRow.
 *
 * @author Bitwise
 *
 */
public abstract class ReusableRow implements Comparable<ReusableRow>, Serializable{

	private LinkedHashSet<String> fields;

	/**
	 * Instantiates a new ReusableRow object with the provided fields
	 * 
	 * @param fields
	 *            The field names of the row
	 */
	public ReusableRow(LinkedHashSet<String> fields) {
		this.fields = fields;
	}

	/**
	 * Resets all the fields to null
	 */
	public void reset() {
		for (int i = 0; i < this.fields.size(); i++) {
			setFieldInternal(i, null);
		}
	}

	/**
	 * Returns all the field names in the row
	 * 
	 * @return An ArrayList<String> containing all the field names
	 */
	public ArrayList<String> getFieldNames() {
		ArrayList<String> values = new ArrayList<String>();
		for (String value : fields) {
			values.add(value);
		}
		return values;
	}

	/**
	 * Returns the field name corresponding to the index
	 * 
	 * @param index
	 *            The index whose field name is to be retrieved
	 * @return The field name corresponding to the index
	 */
	public String getFieldName(int index) {
		verifyFieldExists(index);
		int counter = -1;
		for (String value : fields) {
			counter++;
			if (counter == index)
				return value;
			else
				continue;
		}
		return null;
	}

	protected abstract Comparable getFieldInternal(int index);

	protected abstract Comparable getFieldInternal(String fieldName);

	protected abstract void setFieldInternal(int index, Comparable value);

	protected abstract void setFieldInternal(String fieldName, Comparable value);

	/**
	 * The generic method to set a value to field
	 * 
	 * @param fieldName
	 *            The name of the field
	 * @param value
	 *            The value to set
	 */
	public void setField(String fieldName, Comparable value) {
		//verifyFieldExists(fieldName);
		setFieldInternal(fieldName, value);
	}

	/**
	 * The generic method to set a value to field
	 * 
	 * @param index
	 *            The index of the field
	 * @param value
	 *            The value to set
	 */
	public void setField(int index, Comparable value) {
		verifyFieldExists(index);
		setFieldInternal(index, value);
	}

	/**
	 * A generic method to fetch a field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Comparable getField(String fieldName) {
		//verifyFieldExists(fieldName);
		return getFieldInternal(fieldName);
	}

	/**
	 * A generic method to fetch a field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Comparable getField(int index) {
		verifyFieldExists(index);
		return getFieldInternal(index);
	}

	/**
	 * A generic method to fetch a field values as collection instance
	 * 
	 * @return The collection of the fields
	 */
	public Collection<Comparable> getFields() {
		ArrayList<Comparable> values = new ArrayList<Comparable>();
		for (int i = 0; i < this.fields.size(); i++) {
			values.add(getField(i));
		}
		return values;
	}

	/**
	 * Fetches a string field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public String getString(String fieldName) {
		return (String) getField(fieldName);
	}

	/**
	 * Fetches a string field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public String getString(int index) {
		return (String) getField(index);
	}

	/**
	 * Fetches a float field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Float getFloat(String fieldName) {
		return (Float) getField(fieldName);
	}

	/**
	 * Fetches a float field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Float getFloat(int index) {
		return (Float) getField(index);
	}

	/**
	 * Fetches a double field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Double getDouble(String fieldName) {
		return (Double) getField(fieldName);
	}

	/**
	 * Fetches a double field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Double getDouble(int index) {
		return (Double) getField(index);
	}

	/**
	 * Fetches an integer field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Integer getInteger(String fieldName) {
		return (Integer) getField(fieldName);
	}

	/**
	 * Fetches an integer field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Integer getInteger(int index) {
		return (Integer) getField(index);
	}

	/**
	 * Fetches a long field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Long getLong(String fieldName) {
		return (Long) getField((fieldName));
	}

	/**
	 * Fetches a long field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Long getLong(int index) {
		return (Long) getField(index);
	}

	/**
	 * Fetches a short field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Short getShort(String fieldName) {
		return (Short) getField(fieldName);
	}

	/**
	 * Fetches a short field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Short getShort(int index) {
		return (Short) getField(index);
	}

	/**
	 * Fetches a boolean field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Boolean getBoolean(String fieldName) {
		return (Boolean) getField(fieldName);
	}

	/**
	 * Fetches a boolean field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Boolean getBoolean(int index) {
		return (Boolean) getField(index);
	}

	/**
	 * Fetches a big decimal field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public BigDecimal getBigDecimal(String fieldName) {
		return (BigDecimal) getField(fieldName);
	}

	/**
	 * Fetches a big decimal field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public BigDecimal getBigDecimal(int index) {
		return (BigDecimal) getField(index);
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
		setField(fieldName, value);
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
		setField(index, value);
	}

	/**
	 * Fetches a date field value
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Date getDate(String fieldName) {
		return (Date) getField(fieldName);
	}

	/**
	 * Fetches a date field value
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @return The value of the field
	 */
	public Date getDate(int index) {
		return (Date) getField(index);
	}

	/**
	 * Fetches a date field value in specified format
	 * 
	 * @param fieldName
	 *            The name of the field whose value to be fetched
	 * @param dateFormat
	 *            The format in which the date value is to be fetched
	 * @return The value of the field as string
	 */
	public String getDate(String fieldName, String dateFormat){
		if (getDate(fieldName) != null) {
			Long date = getDate(fieldName).getTime();
			DateFormat df = new SimpleDateFormat(dateFormat);
			return df.format(new Date(date));
		}
		return null;
	}

	/**
	 * Fetches a date field value in specified format
	 * 
	 * @param index
	 *            The index of the field whose value to be fetched
	 * @param dateFormat
	 *            The format in which the date value is to be fetched
	 * @return The value of the field as string
	 */
	public String getDate(int index, String dateFormat){
		if (getDate(index) != null) {
			Long date = getDate(index).getTime();
			DateFormat df = new SimpleDateFormat(dateFormat);
			return df.format(new Date(date));
		}
		return null;
	}

	/**
	 * Checks if the field name has been declared in the xml
	 * 
	 * @param fieldName
	 *            The field name that is to be validated
	 */
	private void verifyFieldExists(String fieldName) {
		// method called in all get field methods. If the code flow
		// encounters a field which is not declared in operation input fields,
		// exception will be raised. The exception will always be raised for the
		// first invalid field

		if (!fields.contains(fieldName)) {
			throw new ReusableRowException(
					"ReusableRow can only be used to fetch values for fields it has been instantiated with. Missing field: '"
							+ fieldName + "'");

		}
	}

	/**
	 * Checks whether the index is within bounds
	 * 
	 * @param index
	 *            The field index to be checked
	 */
	private void verifyFieldExists(int index) {
		// method called in all get field methods. If the code flow
		// encounters an index which is greater than the # of input fields
		// declared, an exception will be raised. The exception will always be
		// raised for the first invalid field

		if (fields.size() < index) {
			throw new ReusableRowException(
					"ReusableRow can only be used to fetch values for fields it has been instantiated with. Index out of bounds: Index: "
							+ index + ", Size: " + fields.size());

		}
	}

	public class ReusableRowException extends RuntimeException {

		private static final long serialVersionUID = 4847210775806480201L;

		public ReusableRowException(String msg) {
			super(msg);
		}
	}

	@Override
	public int compareTo(ReusableRow other) {

		int c = 0;
		if (other == null || other.fields == null || other.fields.size() == 0)
			return 1;

		if (this.fields == null || this.fields.size() == 0)
			return -1;

		if (this.fields.size() != other.fields.size())
			return this.fields.size() - other.fields.size();

		for (int i = 0; i < this.fields.size(); i++) {
			Object lhs = this.getField(i);
			Object rhs = other.getField(i);

			if (lhs == null && rhs == null) {
				continue;
			} else if (lhs == null && rhs != null) {
				return -1;
			} else if (lhs != null && rhs == null) {
				return 1;
			} else {
				// guaranteed to not be null
				if (lhs != null) { // additional check added for SonarQube
					c = ((Comparable) lhs).compareTo((Comparable) rhs);
				}
			}
			if (c != 0)
				return c;
		}

		return 0;
	}

	@Override
	public int hashCode() {
		int hash = 1;

		for (int i = 0; i < this.fields.size(); i++) {
			hash = 31 * hash
					+ (getField(i) != null ? getField(i).hashCode() : 0);
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		String result = "";
		final String fieldDelim = ":";
		final String fieldNameDelim = "=";

		for (String field : fields) {
			result += fieldDelim + field + fieldNameDelim + getField(field);
		}

		return result;
	}
}
