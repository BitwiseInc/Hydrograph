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
package hydrograph.engine.cascading.assembly.handlers;

public class CustomTransformException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6187825096773554492L;

	public CustomTransformException() {
		super();
	}

	public CustomTransformException(String msg) {
		super(msg);

	}

	public CustomTransformException(String msg, Throwable thr) {
		super(msg, thr);

	}

	public CustomTransformException(Throwable thr) {
		super(thr);

	}
}
