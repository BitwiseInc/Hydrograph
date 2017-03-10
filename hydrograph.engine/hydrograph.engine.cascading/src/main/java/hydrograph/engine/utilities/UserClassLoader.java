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
package hydrograph.engine.utilities;

import hydrograph.engine.cascading.assembly.handlers.CustomTransformException;

public class UserClassLoader {

	@SuppressWarnings("unchecked")
	public static <T> T loadAndInitClass(String className, Class<T> type) {

		Class<T> loadedClass;

		try {
			loadedClass = (Class<T>) Class.forName(className);
		} catch (Exception e) {

			throw new CustomTransformException("Given custom transform class "
					+ className + " could not be loaded with type "
					+ type.getName() + ". ", e);
		}

		try {
			return loadedClass.newInstance();
		} catch (Exception e) {

			throw new CustomTransformException("Given custom transform class "
					+ className + " could not be instantiated "
					+ type.getName() + ". ", e);

		}

	}

	public static Object loadAndInitClass(String className) {

		@SuppressWarnings("rawtypes")
		Class loadedClass;

		try {
			loadedClass = Class.forName(className);
		} catch (Exception e) {

			throw new CustomTransformException("Given custom transform class "
					+ className + " could not be loaded.", e);
		}

		try {
			return loadedClass.newInstance();
		} catch (Exception e) {

			throw new CustomTransformException("Given custom transform class "
					+ className + " could not be instantiated.", e);

		}

	}
	
	public static Object loadAndInitClass(String className,String classTypeForMags) {

		@SuppressWarnings("rawtypes")
		Class loadedClass;

		try {
			loadedClass = Class.forName(className);
		} catch (Exception e) {

			throw new CustomTransformException("Given "+classTypeForMags+" class "
					+ className + " could not be loaded.", e);
		}

		try {
			return loadedClass.newInstance();
		} catch (Exception e) {

			throw new CustomTransformException("Given "+classTypeForMags+" class "
					+ className + " could not be instantiated.", e);

		}

	}

}
