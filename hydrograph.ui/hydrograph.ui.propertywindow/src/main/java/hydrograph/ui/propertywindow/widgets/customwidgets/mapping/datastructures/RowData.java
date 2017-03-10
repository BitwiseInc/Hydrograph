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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.mapping.datastructures;

import org.eclipse.swt.widgets.Text;

public class RowData {
	Text in,out,clazz;
	
	public RowData(Text in, Text out, Text clazz) {
		super();
		this.in = in;
		this.out = out;
		this.clazz = clazz;
	}

	public Text getIn() {
		return in;
	}

	public void setIn(String in) {
		this.in.setText(in);
	}

	public Text getOut() {
		return out;
	}

	public void setOut( String out) {
		this.out.setText(out);
	}

	public Text getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz.setText(clazz);
	}
	
	@Override
	public String toString() {
		return "[ " + in.getText() + ", " + clazz.getText() + ", " + out.getText() + " ]";
	}
	
}
