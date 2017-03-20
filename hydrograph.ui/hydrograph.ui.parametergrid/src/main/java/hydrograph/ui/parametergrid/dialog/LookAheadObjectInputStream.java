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
package hydrograph.ui.parametergrid.dialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Bitwise
 *
 */
public class LookAheadObjectInputStream extends ObjectInputStream{
	List<String> acceptedObject = new ArrayList<String>();

	 public LookAheadObjectInputStream(InputStream inputStream, List<String> acceptedObject)
	            throws IOException {
		 	super(inputStream);
		 	this.acceptedObject = acceptedObject;
	    }
	 
	 
	 /**
	     * Only deserialize instances of our expected class
	     */
	    @Override
	    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
	            ClassNotFoundException {
	        if (!acceptedObject.contains(desc.getName())) {
	            throw new InvalidClassException(
	                    "Unauthorized deserialization attempt",
	                    desc.getName());
	        }
	        return super.resolveClass(desc);
	    }
	 
}
