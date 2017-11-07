package hydrograph.ui.propertywindow.widgets.customwidgets;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialogs.GenericExportFileDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;

public class ExportXSDWidget extends AbstractWidget {

	private Button exportButton;
	private static final String W3C_NameSpaceURI = "http://www.w3.org/2001/XMLSchema";
	private static final String ETL_NameSpaceURI = "http://www.hydrograph.org/ui/graph/schema";
	private static final String ROOT_TAG = "rootTag";
	private static final String ROW_TAG = "rowTag";
	private static final String ERROR = "Error";
	private static final String INFORMATION = "Information";

	/**
	 * Instantiates a new ELT file path widget.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ExportXSDWidget(ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {

		ELTDefaultSubgroupComposite exportFieldComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		exportFieldComposite.createContainerWidget();

		// Create Label and into Composite.
		AbstractELTWidget exportXSDLableWidget = new ELTDefaultLable("Export XSD");
		exportFieldComposite.attachWidget(exportXSDLableWidget);
		setPropertyHelpWidget((Control) exportXSDLableWidget.getSWTWidgetControl());

		// Create export button and into Composite.
		AbstractELTWidget exportXSDButtonWidget = new ELTDefaultButton("Export").grabExcessHorizontalSpace(false);
		exportFieldComposite.attachWidget(exportXSDButtonWidget);
		exportButton = (Button) exportXSDButtonWidget.getSWTWidgetControl();
		GridData gridData = (GridData) exportButton.getLayoutData();
		gridData.widthHint = 90;

		// On export button click.
		exportButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// Validation : Check whether schema is define or not before
				// exporting the file.
				Schema schema = (Schema) getComponent().getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
				if ((null == schema || (schema != null && schema.getGridRow().size() == 0))) {
					// Show message dialog and return.
					MessageDialog.openError(exportButton.getShell(), "Error", "Schema not defined in Schema tab.");
					return;
				}
				exportSchemaFile();
			}

		});

	}

	private void exportSchemaFile() {

		GenericExportFileDialog exportXSDFileDialog = new GenericExportFileDialog(exportButton.getShell());
		exportXSDFileDialog.setFileName("XMLOut");
		exportXSDFileDialog.setTitle("Select location for saving XSD file");
		exportXSDFileDialog.setFilterNames(new String[] { "Schema File (*.xsd)" });
		exportXSDFileDialog.setFilterExtensions(new String[] { "*.xsd" });

		String filePath = exportXSDFileDialog.open();
		if (filePath != null) {
			generateSchema(filePath);
		}
	}

	private void generateSchema(String filePath) {

		Schema schema = (Schema) getComponent().getProperties().get(Constants.SCHEMA);
		List<GridRow> gridRows = schema.getGridRow();
		
		//TODO: Add in constant below message
		String rowTag = getPropertyValue(ROW_TAG);
		String rootElementName = getPropertyValue(ROOT_TAG);
		if (StringUtils.isBlank(rootElementName)) {
			MessageDialog.openError(exportButton.getShell(), ERROR, Messages.ROOT_ELEMENT_NOT_FOUND_ERROR);
			return;
		}
		if (StringUtils.isBlank(rowTag)) {
			MessageDialog.openError(exportButton.getShell(), ERROR, Messages.ROW_TAG_NOT_FOUND_ERROR);
			return;
		}
		
		if(rowTag.contains(":")){
			rowTag=rowTag.split(":")[1];
		}
		if(rootElementName.contains(":")){
			rootElementName=rootElementName.split(":")[1];
			
		}
        
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			document.setXmlStandalone(true);

			// Create Schema element which is root of XSD file.
			Element schemaElement = document.createElementNS(W3C_NameSpaceURI, "xs:schema");
			schemaElement.setAttribute("xmlns:etl", ETL_NameSpaceURI);
			document.appendChild(schemaElement);

			// Create root element under schema.
			Element rootElement = getElement(document, rootElementName, null);
			Element rootComplexElement = getComplexTypeElement(document, null);
			rootElement.appendChild(rootComplexElement);
			schemaElement.appendChild(rootElement);
			
			Element relativePathParent = rootComplexElement;
			
				
			String rowTagTypeFirstLetter = rowTag.substring(0, 1).toUpperCase();
			String complexTypeFirstLetter = rowTagTypeFirstLetter.substring(0, 1).toUpperCase();
			String complexTypeNameForRow = complexTypeFirstLetter
					+ rowTag.substring(1, rowTag.length()).toLowerCase();

			Element newParentForRow = null;
			if (rootElementName.equals(rowTag)) {

				NodeList nodes = (NodeList) document.getElementsByTagName("xs:element");
				newParentForRow = checkElementPresent(nodes, rowTag);

			} else {

				NodeList nodes = (NodeList) document.getElementsByTagName("xs:complexType");
				newParentForRow = checkElementPresent(nodes, complexTypeNameForRow);

			}

			if (newParentForRow == null) {

				Element ele = getElement(document, rowTag, complexTypeNameForRow);
				relativePathParent.getFirstChild().appendChild(ele);

				Element complexTypeElement = getComplexTypeElement(document, complexTypeNameForRow);
				schemaElement.appendChild(complexTypeElement);

				relativePathParent = (Element) complexTypeElement.getFirstChild();

			} else {

				if(rowTag.equals(rootElementName)) {
					newParentForRow = (Element) newParentForRow.getFirstChild().getFirstChild();
				}else {
					newParentForRow = (Element) newParentForRow.getFirstChild();
				}
				relativePathParent = newParentForRow;
			}
				
			

			// Create element and insert into XSD DOM according to XPath given.
			for (GridRow gridRow : gridRows) {

				if (XPathGridRow.class.isAssignableFrom(gridRow.getClass())) {

					boolean isRelative = false;
					XPathGridRow xPathGridRow = (XPathGridRow) gridRow;
					if(!rowTag.equals(xPathGridRow.getXPath())) {
						isRelative = true;
					}
					String[] elements = xPathGridRow.getXPath().split("/");
					String elementName=elements[elements.length-1];
					if( elements[elements.length-1].contains(":")){
						elementName=elementName.split(":")[1];
						elementName=elementName.trim();
					}
					
					Element elementToAdd = getElement(document, elementName,
							getXSDType(xPathGridRow.getDataTypeValue()));
					
					
					Element parent = null;
					if(isRelative) {
						parent  = (Element) relativePathParent;
					}
					else {
						parent = (Element) rootComplexElement.getFirstChild();
					}


					for (String elementTagName : elements) {
						if( elementTagName.contains(":")){
							elementTagName=elementTagName.split(":")[1];
							elementTagName=elementTagName.trim();
						}
						if (!StringUtils.isBlank(elementTagName)) {

							String complexTypeFirtLetter = elementTagName.substring(0, 1).toUpperCase();
							String complexTypeName = complexTypeFirtLetter
									+ elementTagName.substring(1, elementTagName.length()).toLowerCase();

							Element newParent = null;
							if (rootElementName.equals(elementTagName)) {

								NodeList nodes = (NodeList) document.getElementsByTagName("xs:element");
								newParent = checkElementPresent(nodes, elementTagName);

							} else {

								NodeList nodes = (NodeList) document.getElementsByTagName("xs:complexType");
								newParent = checkElementPresent(nodes, complexTypeName);

							}

							if (newParent == null) {

								Element ele = getElement(document, elementTagName, complexTypeName);
								parent.appendChild(ele);

								Element complexTypeElement = getComplexTypeElement(document,
										complexTypeName);
								schemaElement.appendChild(complexTypeElement);

								parent = (Element) complexTypeElement.getFirstChild();

							} else {

								if(elementTagName.equals(rootElementName)) {
									newParent = (Element) newParent.getFirstChild().getFirstChild();
								}else {
									newParent = (Element) newParent.getFirstChild();
								}
								parent = newParent;

							}
						}
					}
					parent.appendChild(elementToAdd);
				}
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			DOMSource source = new DOMSource(document);

			StreamResult file = new StreamResult(new File(filePath));

			transformer.transform(source, file);
			MessageDialog.openInformation(exportButton.getShell(), INFORMATION, Messages.XSD_EXPORTED);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getPropertyValue(String propertyName){
		
		String value = (String) getComponent().getProperties().get(propertyName);
		if(value.startsWith("/")){
			value = value.substring(1);
		}
		return value;
	}
	
	private Element checkElementPresent(NodeList nodes, String elementName) {

		Element foundElement = null;
		for (int i = 0; i < nodes.getLength(); i++) {

			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				Element element = (Element) node;
				if (element.hasAttribute("name") && elementName.equals(element.getAttribute("name"))) {
					foundElement = (Element) element;
					break;
				}
			}
		}

		return foundElement;
	}

	private String getXSDType(String dataType) {

		switch (dataType) {
		
		case "java.lang.String":
			return "xs:string";

		case "java.lang.Decimal":
		case "java.math.BigDecimal":
			return "xs:decimal";
		case "java.lang.Float":
			return "xs:float";
		case "java.lang.Double":
			return "xs:double";
		case "java.lang.Long":
			return "xs:long";
			
		case "java.lang.Integer":
		case "java.lang.Short":
			return "xs:integer";

		case "java.lang.Boolean":
			return "xs:boolean";

		case "java.util.Date":
			return "xs:date";

		default:
			return "";
			
		}
	}

	private String getRootElementName(String rowTag) {

		String rootElementName = null;

		if (StringUtils.isBlank(rowTag) || !rowTag.startsWith("/")) {
			return null;
		}

		// TODO: Ask for requirement how row tag will be come
		if (rowTag.startsWith("/")) {
			rootElementName = StringUtils.substringBetween(rowTag, "/", "/");
		}
		if (StringUtils.isBlank(rootElementName)) {
			rootElementName = StringUtils.substringAfter(rowTag, "/");
		}

		return rootElementName;
	}
	
	private Element getComplexTypeElement(Document document, String complexTypeName) {

		Element complexType = document.createElement("xs:complexType");
		if (!StringUtils.isBlank(complexTypeName)) {
			complexType.setAttribute("name", complexTypeName);
		}
		Element sequenceElement = document.createElement("xs:sequence");
		complexType.appendChild(sequenceElement);
		return complexType;

	}
	
	private Element getElement(Document document, String elementName, String type) {

		Element element = document.createElement("xs:element");
		element.setAttribute("name", elementName);
		if (!StringUtils.isBlank(type)) {
			element.setAttribute("type", type);
		}
		return element;

	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		return null;
	}

	@Override
	public boolean isWidgetValid() {
		return true;
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {

	}
}