/********************************************************************************
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
 ******************************************************************************/

 
package hydrograph.ui.parametergrid.textgridwidget.columns;


public class TextGridColumnLayout {
	
	private final int columnWidth;
	private final boolean enabled;
	private final boolean grabHorizantalAccessSpace;
	private final boolean editable;
	
	public static class Builder{
		//Required parameters
			//none
		
		//Optional Parameter
		private int columnWidth=0;
		private boolean disabled=true;
		private boolean grabHorizantalAccessSpace=false;
		private boolean editable=true;
		
		public Builder columnWidth(int columnWidth){
			this.columnWidth = columnWidth;
			return this;
		}
		
		public Builder enabled(boolean disabled){
			this.disabled = disabled;
			return this;
		}
		
		public Builder grabHorizantalAccessSpace(boolean grabHorizantalAccessSpace){
			this.grabHorizantalAccessSpace = grabHorizantalAccessSpace;
			return this;
		}
		
		public Builder editable(boolean nonEditable){
			this.editable = nonEditable;
			return this;
		}
		
		
		public TextGridColumnLayout build(){
			return new TextGridColumnLayout(this);
		}
	}
	
	private TextGridColumnLayout(Builder builder){
		this.columnWidth = builder.columnWidth;
		this.enabled = builder.disabled;
		this.grabHorizantalAccessSpace = builder.grabHorizantalAccessSpace;
		this.editable = builder.editable;
	}

	public int getColumnWidth() {
		return columnWidth;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean grabHorizantalAccessSpace() {
		return grabHorizantalAccessSpace;
	}

	public boolean isEditable() {
		return editable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + columnWidth;
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + (grabHorizantalAccessSpace ? 1231 : 1237);
		result = prime * result + (editable ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextGridColumnLayout other = (TextGridColumnLayout) obj;
		if (columnWidth != other.columnWidth)
			return false;
		if (enabled != other.enabled)
			return false;
		if (grabHorizantalAccessSpace != other.grabHorizantalAccessSpace)
			return false;
		if (editable != other.editable)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ColumnData [columnWidth=" + columnWidth + ", disabled="
				+ enabled + ", grabHorizantalAccessSpace="
				+ grabHorizantalAccessSpace + ", nonEditable=" + editable
				+ "]";
	}
}
