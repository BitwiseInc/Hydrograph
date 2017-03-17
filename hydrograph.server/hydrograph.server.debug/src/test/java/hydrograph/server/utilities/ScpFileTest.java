/*******************************************************************************
 *  Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/
package hydrograph.server.utilities;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

public class ScpFileTest {
	public static void main(String[] args) {



		String remoteFileSeparator = "/";
		String remoteFilePath = remoteFileSeparator+"tmp"+remoteFileSeparator+"generateRecord_input1_out0.csv";
		String localFilePath = ".";

		System.out.println("+++ Start: "
				+ new Timestamp((new Date()).getTime()));
		ScpFrom scpFrom = new ScpFrom();
		scpFrom.scpFileFromRemoteServer("10.130.248.53", "hduser",
				"Bitwise2012", remoteFilePath,
				localFilePath);
		System.out.println("+++ End: "
				+ new Timestamp((new Date()).getTime()));
	}
}
