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

 
package hydrograph.ui.datastructure.property;


import hydrograph.ui.common.cloneableinterface.IDataStructure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;



/**
 * The Class Schema
 * This Schema class contains accessors methods for component schema.
 * 
 * @author Bitwise
 */

public class Schema implements IDataStructure{
	private static final String RELATIVE_PATH_PREFIX = "../";
	private String externalSchemaPath;
	private Boolean isExternal;
	private List<GridRow> gridRow;
	private List<GridRow> clonedGridRow;
   
	/**
	 * Instantiates a new schema.
	 */
	public Schema(){
		isExternal =false;
	}
	
	/**
	 * Gets the external schema path.
	 * 
	 * @return the external schema path
	 */
	public String getExternalSchemaPath() {
		return externalSchemaPath;
	}

	/**
	 * Sets the external schema path.
	 * 
	 * @param externalSchemaPath
	 *            the new external schema path
	 */
	public void setExternalSchemaPath(String externalSchemaPath) {
		if(StringUtils.startsWith(externalSchemaPath, RELATIVE_PATH_PREFIX))
			externalSchemaPath=StringUtils.replace(externalSchemaPath, RELATIVE_PATH_PREFIX,"");
		this.externalSchemaPath = externalSchemaPath;
	}

	/**
	 * Gets the checks if is external.
	 * 
	 * @return the checks if is external
	 */
	public Boolean getIsExternal() {
		return isExternal;
	}

	/**
	 * Sets the checks if is external.
	 * 
	 * @param isExternalPath
	 *            the new checks if is external
	 */
	public void setIsExternal(Boolean isExternalPath) {
		this.isExternal = isExternalPath;
	}

	/**
	 * Gets the grid row.
	 * 
	 * @return the grid row
	 */
	public List<GridRow> getGridRow() {
		if(gridRow==null)
			gridRow=new ArrayList<>();
		return gridRow;
	}

	/**
	 * Sets the grid row.
	 * 
	 * @param gridRow
	 *            the new grid row
	 */
	public void setGridRow(List<GridRow> gridRow) {
		this.gridRow = gridRow;
	}
     
	@Override
	public Schema clone(){ 
		Schema schema=new Schema();	
		clonedGridRow=new ArrayList<>();
		if (gridRow != null) {
			for (int i = 0; i < gridRow.size(); i++) {
				if (gridRow.get(i) instanceof FixedWidthGridRow){
					clonedGridRow.add(((FixedWidthGridRow) gridRow.get(i)).copy());
				}
				else if (gridRow.get(i) instanceof MixedSchemeGridRow){
					clonedGridRow.add(((MixedSchemeGridRow) gridRow.get(i)).copy());
				}
				else if (gridRow.get(i) instanceof XPathGridRow){
					clonedGridRow.add(((XPathGridRow) gridRow.get(i)).copy());
				}
				else if (gridRow.get(i) != null){
					clonedGridRow.add(((BasicSchemaGridRow) gridRow.get(i)).copy());
				}
			}
		}
		schema.setExternalSchemaPath(getExternalSchemaPath());
		schema.setGridRow(clonedGridRow);
		schema.setIsExternal(getIsExternal());
		return schema;
	}
    
	@Override
	public String toString() {
		return "ExternalSchema [externalSchemaPath=" + externalSchemaPath
				+ ", isExternalPath=" + isExternal + ", gridRow=" + gridRow
				+ "]";
	}
	
	/**
	 * This method fetches single row from current schema rows. 
	 * 
	 * @param fieldName
	 * @return gridRow
	 */
	public GridRow getGridRow(String fieldName) {
		if (!getGridRow().isEmpty()) {
			for (GridRow row : getGridRow()) {
				if (StringUtils.equalsIgnoreCase(fieldName, row.getFieldName()))
					return row.copy();
			}
		}
		return null;
	}
	
}
