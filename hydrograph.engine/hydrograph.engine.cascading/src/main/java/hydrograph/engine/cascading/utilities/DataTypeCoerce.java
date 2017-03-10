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
package hydrograph.engine.cascading.utilities;

import cascading.tuple.coerce.Coercions;
import hydrograph.engine.cascading.coercetype.ImplicitBigDecimalType;
import hydrograph.engine.cascading.coercetype.StrictDateType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;

public class DataTypeCoerce {

	public static Type convertClassToCoercibleType(Class<?> clazz,
			String fieldFormat, int fieldScale, String fieldScaleType) {
		Type coercibleType;
		if (clazz.equals(Date.class)) {
			if (fieldFormat == null) {
				throw new RuntimeException("Date Format is not specified.");
			}
			coercibleType = new StrictDateType(fieldFormat);
		} else if (clazz.equals(BigDecimal.class)) {
			coercibleType = new ImplicitBigDecimalType(fieldScale,
					isImplicitScaleType(fieldScaleType));
		} else {
			coercibleType = Coercions.coercibleTypeFor(clazz);
		}
		return coercibleType;
	}

	private static boolean isImplicitScaleType(String fieldScaleType) {
		return fieldScaleType != null && fieldScaleType.equals("implicit");
	}
}