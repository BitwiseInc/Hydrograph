package hydrograph.ui.common.datastructure.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;

/**
 * @author Bitwise
 * 
 * represents operation class data
 *
 */
public class OperationClassData implements IDataStructure{
	private String qualifiedClassName;
	private List<NameValueProperty> operationClassProperties;
	private ExternalWidgetData externalOperationClassData;
	private String id;	
	private List<String> inputFields;
	
	
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getInputFields() {
		if(inputFields==null){
			inputFields=new ArrayList<>();
		}
		return inputFields;
	}

	public OperationClassData() {
		id = "Operation:1";
		qualifiedClassName = "";
		operationClassProperties = new ArrayList<>();
		externalOperationClassData = new ExternalWidgetData(false, null);
	}

	/**
	 * @return the operationClassPath
	 */
	public String getQualifiedOperationClassName() {
		return qualifiedClassName;
	}

	
	public String getSimpleOperationClassName(){
		String[] names=StringUtils.split(qualifiedClassName,".");
		if(names.length>0){
			return names[names.length-1];
		}
		return qualifiedClassName;
	}
	/**
	 * @param operationClassPath the operationClassPath to set
	 */
	public void setQualifiedOperationClassName(String qualifiedClassName) {
		this.qualifiedClassName = qualifiedClassName;
	}

	/**
	 * @return the classProperties
	 */
	public List<NameValueProperty> getClassProperties() {
		if(operationClassProperties==null){
			operationClassProperties=new ArrayList<>();
		}
		return operationClassProperties;
	}

	/**
	 * @return the externalOperationClassData
	 */
	public ExternalWidgetData getExternalOperationClassData() {
		return externalOperationClassData;
	}

	/**
	 * @param externalOperationClassData the externalOperationClassData to set
	 */
	public void setExternalOperationClassData(ExternalWidgetData externalOperationClassData) {
		this.externalOperationClassData = externalOperationClassData;
	}

	public Object clone() {
		OperationClassData classData = new OperationClassData();
		classData.id=id;
		classData.qualifiedClassName = qualifiedClassName;
		classData.externalOperationClassData = (ExternalWidgetData) externalOperationClassData.clone();
		classData.inputFields = new ArrayList<>(getInputFields());
		classData.operationClassProperties = new ArrayList<NameValueProperty>();
		for (NameValueProperty property : operationClassProperties) {
			NameValueProperty newProperty = new NameValueProperty();
			newProperty.setPropertyName(property.getPropertyName());
			newProperty.setPropertyValue(property.getPropertyValue());
			classData.operationClassProperties.add(newProperty);
		}
		return classData;
	}

}
