package hydrograph.ui.datastructure.property.mapping;

import java.util.ArrayList;
import java.util.List;

import hydrograph.ui.common.cloneableinterface.IDataStructure;

public class ExternalWidgetData implements IDataStructure {

	private boolean isExternal;
	private String filePath;
	private List<String> errorLogs=new ArrayList<String>();

	public ExternalWidgetData(boolean isExternal,String filePath) {
		this.isExternal=isExternal;
		this.filePath=filePath;
	}
	
	public boolean isExternal() {
		return isExternal;
	}

	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<String> getErrorLogs() {
		return errorLogs;
	}

	public void setErrorLogs(List<String> errorLogs) {
		this.errorLogs = errorLogs;
	}

	public Object clone() {
		ExternalWidgetData data=new ExternalWidgetData(isExternal,filePath);
		data.errorLogs=new ArrayList<String>(errorLogs);
		return data;
	}

	public void clear() {
		filePath="";
		isExternal=false;
		errorLogs.clear();
	};
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;

		ExternalWidgetData externalWidgetData = (ExternalWidgetData) obj;
		if (this.isExternal != externalWidgetData.isExternal)
			return false;
		if (this.getFilePath() != null && !this.getFilePath().equals(externalWidgetData.getFilePath()))
			return false;
		if (externalWidgetData.getFilePath() != null && !externalWidgetData.getFilePath().equals(this.getFilePath()))
			return false;

		return true;
	}
}
