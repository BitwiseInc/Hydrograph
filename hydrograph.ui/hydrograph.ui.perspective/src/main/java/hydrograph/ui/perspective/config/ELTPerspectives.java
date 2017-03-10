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

 
package hydrograph.ui.perspective.config;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Aug 28, 2015
 *
 */

public enum ELTPerspectives {
	ETLPerspective("hydrograph.ui.perspective.ETLPerspective");
	
	private final String perspectiveID;
	
	private ELTPerspectives(final String perspectiveID){
		this.perspectiveID = perspectiveID;
	}
	
	/**
	 * Contains.
	 * 
	 * @param perspectiveID
	 *            the perspective id
	 * @return true, if successful
	 */
	public static boolean contains(String perspectiveID){
		for(ELTPerspectives eltPerspective : ELTPerspectives.values()){
			if(eltPerspective.toString().equals(perspectiveID)){
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return perspectiveID;
	}
}
