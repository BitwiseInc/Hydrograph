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

import javax.xml.bind.JAXBElement;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.engine.jaxb.transform.TypeTransformOutSocket;

/**
 * @author Bitwise Transform Converter
 */

public abstract class TransformConverter extends Converter {
	
	public TransformConverter(Component comp) {
		super(comp);
	}

	@Override
	public void prepareForXML(){
		super.prepareForXML();
		((TypeOperationsComponent) baseComponent).getInSocket().addAll(getInSocket());
		((TypeOperationsComponent)baseComponent).getOutSocket().addAll(getOutSocket());
		((TypeOperationsComponent)baseComponent).setRuntimeProperties(getRuntimeProperties()); 
		
	}
	
	/**
	 * Returns {@link List} of classes of type {@link TypeTransformOutSocket}
	 * @return {@link List}
	 */
	protected abstract  List<TypeOperationsOutSocket> getOutSocket();
	
	/**
	 * Returns {@link List} of classes of type {@link TypeTransformOperation} 
	 * @return {@link List}
	 */
	protected abstract List<JAXBElement<?>> getOperations();
	public abstract List<TypeBaseInSocket> getInSocket();
	
}
