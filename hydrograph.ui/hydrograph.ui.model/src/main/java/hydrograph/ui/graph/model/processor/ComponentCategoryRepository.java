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

 
package hydrograph.ui.graph.model.processor;

import hydrograph.ui.common.component.config.CategoryType;
import hydrograph.ui.graph.model.categories.InputCategory;
import hydrograph.ui.graph.model.categories.OutputCategory;
import hydrograph.ui.graph.model.categories.StraightPullCategory;


// TODO: Auto-generated Javadoc
/**
 * The Class ComponentCategoryRepository.
 */
public class ComponentCategoryRepository {
	public static ComponentCategoryRepository INSTANCE = new ComponentCategoryRepository();
	
	private ComponentCategoryRepository() {}

	/**
	 * Gets the class by categoty type.
	 * 
	 * @param categoryType
	 *            the category type
	 * @return the class by categoty type
	 */
	public Class<?> getClassByCategotyType(CategoryType categoryType){
		switch(categoryType){
			
			case INPUT:
					return InputCategory.class;
					
			case OUTPUT:
					return OutputCategory.class;
			
			case STRAIGHTPULL:
				return StraightPullCategory.class;			
					
			default :
				//TODO : Add Logger
				throw new RuntimeException();
		}
	}
}
