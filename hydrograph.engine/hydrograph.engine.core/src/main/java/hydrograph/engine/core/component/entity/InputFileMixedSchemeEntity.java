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
 * The Class InputFileMixedSchemeEntity.
 *
 * @author Bitwise
 *
 */
public class InputFileMixedSchemeEntity extends InputOutputEntityBase {

	private String path;
	private String charset;
	private boolean safe;
	private boolean strict;
	private String quote = "";

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

	public boolean getSafe() {
		return safe;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	public boolean getStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}
		public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
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
				"Input file mixed scheme entity info:\n");
		str.append(super.toString());
		str.append("Path: " + path);
		str.append(" | strict: " + strict);
		str.append(" | safe: " + safe);
		str.append(" | charset: " + charset);
		str.append(" | quote: " + quote);

		str.append("\nfields: ");
		if (getFieldsList() != null) {
			str.append(Arrays.toString(getFieldsList().toArray()));
		}

		str.append("\nout socket(s): ");
		if (getOutSocketList() != null) {
			str.append(Arrays.toString(getOutSocketList().toArray()));
		}
		return str.toString();
	}
}