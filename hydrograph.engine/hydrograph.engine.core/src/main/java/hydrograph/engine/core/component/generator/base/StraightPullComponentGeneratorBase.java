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
package hydrograph.engine.core.component.generator.base;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
/**
 * The Class StraightPullComponentGeneratorBase.
 *
 * @author Bitwise
 *
 */
public abstract class StraightPullComponentGeneratorBase extends ComponentGeneratorBase {
	
	public StraightPullComponentGeneratorBase(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	
}
