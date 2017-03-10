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

import java.util.LinkedHashMap;
import java.util.Map;

public class TextGridRowLayout {
	int numberOfColumn=0;
	private Map<Integer,TextGridColumnLayout> textGridColumns;
	
	public TextGridRowLayout(){
		textGridColumns = new LinkedHashMap<>();
	}
	
	public void addColumn(TextGridColumnLayout textGridColumnLayout){
		textGridColumns.put(numberOfColumn, textGridColumnLayout);
		numberOfColumn++;
	}

	public int getNumberOfColumn() {
		return numberOfColumn;
	}

	public Map<Integer, TextGridColumnLayout> getTextGridColumns() {
		return textGridColumns;
	}
	
	public void resetColumnData(int columnNumber,TextGridColumnLayout columnData){
		if(textGridColumns.containsKey(columnNumber)){
			textGridColumns.put(columnNumber, columnData);
		}
	}
	
}
