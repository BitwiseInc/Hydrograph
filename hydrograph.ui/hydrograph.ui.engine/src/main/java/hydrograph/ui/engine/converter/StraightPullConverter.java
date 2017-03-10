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

 
package hydrograph.ui.engine.converter;

import hydrograph.ui.graph.model.Component;

import java.util.List;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;

/**
 * Converter implementation for StraightPull type components
 */
public abstract class StraightPullConverter extends Converter {

	public StraightPullConverter(Component comp) {
		super(comp);
	}

	@Override
	public void prepareForXML(){
		super.prepareForXML();
		((TypeStraightPullComponent) baseComponent).setRuntimeProperties(getRuntimeProperties());
		((TypeStraightPullComponent) baseComponent).getInSocket().addAll(getInSocket());
		((TypeStraightPullComponent) baseComponent).getOutSocket().addAll(getOutSocket());
	}

	/**
	 *  Returns {@link List} of classes of type {@link TypeStraightPullOutSocket}
	 * @return {@link List}
	 */
	protected abstract List<TypeStraightPullOutSocket> getOutSocket();
	public abstract List<TypeBaseInSocket> getInSocket();


}
