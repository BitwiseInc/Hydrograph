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
package hydrograph.ui.datastructure.property;

import hydrograph.ui.common.cloneableinterface.IDataStructure;

/**
 * The Class FTPAuthOperationDetails holds FTP operation details
 * @author bitwise
 *
 */
public class FTPAuthOperationDetails implements IDataStructure{
	private String field1;
 	private String field2;
 	private String field3;
 	private String field4;
 	private String field5;
 	private String protocolSelection="FTP";
 	
 	
 	/**
 	 * @param field1
 	 * @param field2
 	 * @param field3
 	 * @param field4
 	 * @param field5
 	 */
 	public FTPAuthOperationDetails(String field1, String field2,String field3, String field4, String field5, String protocolSelection) {
 		this.field1 = field1;
 		this.field2 = field2;
 		this.field3 = field3;
 		this.field4 = field4;
 		this.field5 = field5;
 		this.protocolSelection = protocolSelection;
 	}

 	
	/**
	 * @return selected text
	 */
	public String getProtocolSelection() {
		return protocolSelection;
	}


	/**
	 * @param protocolSelection
	 */
	public void setProtocolSelection(String protocolSelection) {
		this.protocolSelection = protocolSelection;
	}


	/**
	 * @return
	 */
	public String getField4() {
		return field4;
	}

	/**
	 * @param field4
	 */
	public void setField4(String field4) {
		this.field4 = field4;
	}

	/**
	 * @return
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * @param field1
	 */
	public void setField1(String field1) {
		this.field1 = field1;
	}

	/**
	 * @return
	 */
	public String getField2() {
		return field2;
	}

	/**
	 * @param field2
	 */
	public void setField2(String field2) {
		this.field2 = field2;
	}

	/**
	 * @return
	 */
	public String getField3() {
		return field3;
	}

	/**
	 * @param field3
	 */
	public void setField3(String field3) {
		this.field3 = field3;
	}

	/**
	 * @return
	 */
	public String getField5() {
		return field5;
	}

	/**
	 * @param field5
	 */
	public void setField5(String field5) {
		this.field5 = field5;
	}
	
	@Override
	public FTPAuthOperationDetails clone() {
		FTPAuthOperationDetails details = new FTPAuthOperationDetails(getField1(), 
				getField2(), getField3(), getField4(), getField5(), getProtocolSelection());
		return details;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field1 == null) ? 0 : field1.hashCode());
		result = prime * result + ((field2 == null) ? 0 : field2.hashCode());
		result = prime * result + ((field3 == null) ? 0 : field3.hashCode());
		result = prime * result + ((field4 == null) ? 0 : field4.hashCode());
		result = prime * result + ((field5 == null) ? 0 : field5.hashCode());
		result = prime * result + ((protocolSelection == null) ? 0 : protocolSelection.hashCode());
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
		FTPAuthOperationDetails other = (FTPAuthOperationDetails) obj;
		if (field1 == null) {
			if (other.field1 != null)
				return false;
		} else if (!field1.equals(other.field1))
			return false;
		if (field2 == null) {
			if (other.field2 != null)
				return false;
		} else if (!field2.equals(other.field2))
			return false;
		if (field3 == null) {
			if (other.field3 != null)
				return false;
		} else if (!field3.equals(other.field3))
			return false;
		if (field4 == null) {
			if (other.field4 != null)
				return false;
		} else if (!field4.equals(other.field4))
			return false;
		if (field5 == null) {
			if (other.field5 != null)
				return false;
		} else if (!field5.equals(other.field5))
			return false;
		if (protocolSelection == null) {
			if (other.protocolSelection != null)
				return false;
		} else if (!protocolSelection.equals(other.protocolSelection))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "FTPAuthOperationDetails [field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + ", field4="
				+ field4 + ", field5=" + field5 + ", protocolSelection=" + protocolSelection + "]";
	}
 	
}
