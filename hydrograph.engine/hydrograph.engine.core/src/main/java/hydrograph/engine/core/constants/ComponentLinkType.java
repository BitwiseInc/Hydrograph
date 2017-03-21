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
package hydrograph.engine.core.constants;
/**
 * The Enum ComponentLinkType.
 *
 * @author Bitwise
 */
public enum ComponentLinkType {
	unused, reject, error, out, in;

	public String toString() {
		switch (this) {
		case unused:
			return "unused";

		case reject:
			return "reject";

		case error:
			return "error";

		case out:
			return "out";

		case in:
			return "in";

		default:
			return "unknown";

		}
		// return null;
	}
}
