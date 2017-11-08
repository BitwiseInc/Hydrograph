package hydrograph.ui.engine.converter.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.ofxml.TypeOutputXmlInSocket;
import hydrograph.engine.jaxb.outputtypes.XmlFile;
import hydrograph.engine.jaxb.outputtypes.XmlFile.AbsoluteXPath;
import hydrograph.engine.jaxb.outputtypes.XmlFile.RootTag;
import hydrograph.engine.jaxb.outputtypes.XmlFile.RowTag;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.OutputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Converter for Xml Output component
 * 
 * @author Bitwise
 */
public class OutputXmlConverter extends OutputConverter {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OutputXmlConverter.class);

	public OutputXmlConverter(Component component) {
		super(component);
		this.component = component;
		this.properties = component.getProperties();
		this.baseComponent = new XmlFile();
	}

	@Override
	public void prepareForXML() {
		super.prepareForXML();
		XmlFile xmlFile = (XmlFile) baseComponent;

		XmlFile.Path path = new XmlFile.Path();
		path.setUri((String) properties.get(PropertyNameConstants.PATH.value()));
		xmlFile.setPath(path);

		XmlFile.AbsoluteXPath absoluteXPath = new AbsoluteXPath();
		absoluteXPath.setValue((String) properties.get(PropertyNameConstants.ABSOLUTE_XPATH.value()));
		xmlFile.setAbsoluteXPath(absoluteXPath);

		XmlFile.Charset charset = new XmlFile.Charset();
		charset.setValue(getCharset());
		xmlFile.setCharset(charset);

		//TODO: setRuntimeProperties
		//xmlFile.setRuntimeProperties(getRuntimeProperties());

		XmlFile.RootTag rootTag = new RootTag();
		rootTag.setValue((String) properties.get(PropertyNameConstants.ROOT_TAG.value()));
		xmlFile.setRootTag(rootTag);

		XmlFile.RowTag rowTag = new RowTag();
		rowTag.setValue((String) properties.get(PropertyNameConstants.ROW_TAG.value()));
		xmlFile.setRowTag(rowTag);
		xmlFile.setOverWrite(getTrueFalse(PropertyNameConstants.OVER_WRITE.value()));

	}

	@Override
	protected List<TypeOutputInSocket> getOutInSocket() {
		
		logger.debug("Generating TypeOutputInSocket data for {}", properties.get(Constants.PARAM_NAME));
		List<TypeOutputInSocket> outputinSockets = new ArrayList<>();
		for (Link link : component.getTargetConnections()) {
			TypeOutputXmlInSocket outSocket = new TypeOutputXmlInSocket();
			outSocket.setId(link.getTargetTerminal());
			outSocket.setType(link.getTarget().getPort(link.getTargetTerminal()).getPortType());
			outSocket.setFromSocketId(converterHelper.getFromSocketId(link));
			outSocket.setFromSocketType(link.getSource().getPorts().get(link.getSourceTerminal()).getPortType());
			outSocket.setSchema(getSchema());
			outSocket.getOtherAttributes();
			outSocket.setFromComponentId(link.getSource().getComponentId());
			outputinSockets.add(outSocket);
		}
		return outputinSockets;
	}


	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> gridList) {
		
		logger.debug("Generating data for {} for property {}",
				new Object[] { properties.get(Constants.PARAM_NAME), PropertyNameConstants.SCHEMA.value() });
		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (gridList != null && gridList.size() != 0) {
			for (GridRow object : gridList) {
				XPathGridRow xPathGridRow = (XPathGridRow) object;
				TypeBaseField gridRow = converterHelper.getSchemaGridTargetData(object);
				if (StringUtils.isNotBlank(xPathGridRow.getXPath())) {
					gridRow.getOtherAttributes().put(new QName(Constants.ABSOLUTE_OR_RELATIVE_XPATH_QNAME),
							xPathGridRow.getXPath());
				}
				typeBaseFields.add(gridRow);
			}
		}
		return typeBaseFields;
	}
}