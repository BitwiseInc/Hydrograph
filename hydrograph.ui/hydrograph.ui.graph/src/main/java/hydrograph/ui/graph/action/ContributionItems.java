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

 
package hydrograph.ui.graph.action;

import java.util.ArrayList;
import java.util.HashSet;

public enum ContributionItems {
	MENU_BAR_ITEMS_LIST {
		public ArrayList<String> getRequiredItems() {
			ArrayList<String> requiredToolItems = new ArrayList<>();
			requiredToolItems.add("cu&t	ctrl+x");
			requiredToolItems.add("&copy	ctrl+c");
			requiredToolItems.add("&paste	ctrl+v");
			requiredToolItems.add("&delete	delete");			
			requiredToolItems.add("select &all	ctrl+a");

			return requiredToolItems;
		}
	},
	UNDO_REDO_ITEMS_LIST {
		public ArrayList<String> getRequiredItems() {
			ArrayList<String> requiredToolItems = new ArrayList<>();
			requiredToolItems.add("&undo	ctrl+z");
			requiredToolItems.add("&redo	ctrl+y");
			return requiredToolItems;
		}
	},
	MENU_LIST{
		public ArrayList<String> getRequiredItems() {
			ArrayList<String> requiredToolItems = new ArrayList<>();
			requiredToolItems.add("menuitem {&edit}");
			return requiredToolItems;
		}
	},
	TOOL_BAR_ITEMS_LIST {
		public ArrayList<String> getRequiredItems() {
			ArrayList<String> requiredToolItems = new ArrayList<>();

			return requiredToolItems;
		}
	};

	public ArrayList<String> getRequiredItems() {
		ArrayList<String> requiredToolItems = new ArrayList<>();

		return requiredToolItems;
	}

}
