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

package hydrograph.ui.engine.converter.impl;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.operationstypes.PartitionByExpression;
import hydrograph.engine.jaxb.operationstypes.PartitionByExpression.NoOfPartitions;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.converter.Converter;
import hydrograph.ui.engine.converter.TransformConverter;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.engine.helper.OperationsConverterHelper;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * 
 * Converter for PartitionByExpression type component.
 *
 * @author Bitwise
 */
public class PartitionByExpressionConverter extends TransformConverter {

	private static final String OUT_PORT_COUNT = "outPortCount";
	private static final Logger logger = LogFactory.INSTANCE.getLogger(PartitionByExpressionConverter.class);
	private OperationsConverterHelper operationConverterHelper;

	public PartitionByExpressionConverter(Component component) {
		super(component);
		this.baseComponent = new PartitionByExpression();
		this.component = component;
		this.properties = component.getProperties();
		converterHelper = new ConverterHelper(component);
		operationConverterHelper = new OperationsConverterHelper(component);
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for :{}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		
		PartitionByExpression partByExp = (PartitionByExpression) baseComponent;

		NoOfPartitions noOfPartitions = new NoOfPartitions();
		String partitionValue = (String) this.properties.get(OUT_PORT_COUNT);
		if (StringUtils.isNotBlank(partitionValue)) {
			int partitionValueInt = Integer.parseInt(partitionValue);
			noOfPartitions.setValue(partitionValueInt);
			partByExp.setNoOfPartitions(noOfPartitions);
		}
		partByExp.getOperationOrExpressionOrIncludeExternalOperation().addAll(getOperations());
	}

	@Override
	protected List<TypeOperationsOutSocket> getOutSocket() {
		return operationConverterHelper.getOutSocket();
	}

	@Override
	protected List<JAXBElement<?>> getOperations() {
		return operationConverterHelper.getFilterOperations(Converter.ID);
	}

	@Override
	public List<TypeBaseInSocket> getInSocket() {
		return operationConverterHelper.getInSocket();
	}

}