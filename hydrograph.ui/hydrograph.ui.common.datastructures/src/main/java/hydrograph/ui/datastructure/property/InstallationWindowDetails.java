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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class InstallationWindowDetails.
 * Creates the List for JarInformationDetails for Installation Detail Window
 * 
 * @author Bitwise
 */
public class InstallationWindowDetails {

	
	List<JarInformationDetails> jarInformationDetails=new ArrayList<>();
	
	/**
	 * Returns the information about JarInformationDetails present in List
	 * @return jarInformationDetails
	 */
	public List<JarInformationDetails> getJarInfromationDetails() {
		return jarInformationDetails;
	}
}
