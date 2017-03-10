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
 * limitations under the License
 *******************************************************************************/

package hydrograph.engine.cascading.scheme;

import cascading.jdbc.RedshiftScheme;
import cascading.jdbc.db.DBInputFormat;
import cascading.jdbc.db.DBOutputFormat;
import cascading.tuple.Fields;

public class RedshiftSchemeForUpdate extends RedshiftScheme{

	 
	private static final long serialVersionUID = 7323771470413856096L;

	
	
	public RedshiftSchemeForUpdate( Class<? extends DBInputFormat> inputFormatClass, Class<? extends DBOutputFormat> outputFormatClass,
		      Fields columnFields, String[] columns, String[] orderBy, Fields updateByFields, String[] updateBy )
	    {
		 super( columns, orderBy, updateBy );

		    setSinkFields( columnFields );
		    setSourceFields( columnFields );

		  
	    }
	
	
	
}
