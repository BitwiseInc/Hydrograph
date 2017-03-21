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
package hydrograph.engine.core.component.entity;

import hydrograph.engine.core.component.entity.base.InputOutputEntityBase;

import java.util.Arrays;
/**
 * The Class OutputFileDelimitedEntity.
 *
 * @author Bitwise
 *
 */
public class OutputFileDelimitedEntity extends InputOutputEntityBase {

	private String path;
	private String charset;
	private String delimiter;
	private boolean safe;
	private boolean strict;
	private boolean hasHeader;
	String quote;
	private boolean overWrite;

	public boolean isOverWrite() {
		return overWrite;
	}

	public void setOverWrite(boolean overWrite) {
		this.overWrite = overWrite;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public void setHasHeader(boolean hasHeader) {

		this.hasHeader = hasHeader;
	}

	public boolean getHasHeader() {
		return hasHeader;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean getSafe() {
		return safe;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	/**
	 * @return the strict
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * @param strict
	 *            the strict to set
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/**
	 * Returns a string with the values for all the members of this entity
	 * object.
	 * <p>
	 * Use cautiously as this is a very heavy operation.
	 * 
	 * @see hydrograph.engine.core.component.entity.base.AssemblyEntityBase#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(
				"Output file delimited entity info:\n");
		str.append(super.toString());
		str.append("Path: " + path);
		str.append(" | delimiter: " + delimiter);
		str.append(" | quote: " + quote);
		str.append(" | has header: " + hasHeader);
		str.append(" | safe: " + safe);
		str.append(" | charset: " + charset);
		str.append(" | Overwrite: " + overWrite);

		str.append("\nfields: ");
		if (getFieldsList() != null) {
			str.append(Arrays.toString(getFieldsList().toArray()));
		}
		return str.toString();
	}
}
