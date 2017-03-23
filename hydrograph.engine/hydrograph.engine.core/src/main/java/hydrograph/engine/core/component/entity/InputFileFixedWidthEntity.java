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
 * The Class InputFileFixedWidthEntity.
 *
 * @author Bitwise
 *
 */
public class InputFileFixedWidthEntity extends InputOutputEntityBase {

	private boolean strict = true;
	private boolean safe = false;
	private String charset = "UTF-8";
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isStrict() {
		return strict;
	}

	public boolean isSafe() {
		return safe;
	}

	public String getCharset() {
		return charset;
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
				"Input file fixed width entity info:\n");
		str.append(super.toString());
		str.append("Path: " + getPath());
		str.append(" | strict: " + strict);
		str.append(" | safe: " + safe);
		str.append(" | charset: " + charset);

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
