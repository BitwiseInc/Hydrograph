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
package hydrograph.engine.commandtype.component;


import hydrograph.engine.core.component.entity.HplSqlEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class HplSqlComponent extends BaseCommandComponent {

	private static Logger LOG = LoggerFactory.getLogger(HplSqlComponent.class);
	private HplSqlEntity hplSqlEntity;

	public HplSqlComponent(AssemblyEntityBase runProgramEntity) throws Throwable {
		super(runProgramEntity);
	}

	@Override
	public void castEntityFromBase(AssemblyEntityBase baseComponentEntity) {
		this.hplSqlEntity = (HplSqlEntity) baseComponentEntity;
	}

	@Override
	public int executeCommand() {

		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				LOG.error("hplsql component can't be run on windows environment");

			}
			LOG.debug("Executing Command.");
			String[] cmdarray = getCommand();

			Process p = Runtime.getRuntime().exec(cmdarray, null, null);

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// read any errors from the attempted command and throw the
			// exception, if any
			String errorMessage = "";
			String s;
			while ((s = stdError.readLine()) != null) {
				errorMessage += s;
			}

			return p.waitFor();

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private String[] getCommand() {
		StringTokenizer st = new StringTokenizer(hplSqlEntity.getCommand());
		String[] cmdarray = new String[st.countTokens() + 1];
		for (int i = 0; st.hasMoreTokens(); i++)
			cmdarray[i] = st.nextToken();

		if (!(cmdarray[0].contains("hplsql") && ((cmdarray[1].equals("-e") || (cmdarray[1]
				.equals("-f")))))) {
			throw new RuntimeException("Wrong command parameters : "
					+ hplSqlEntity.getCommand());
		}

		if (hplSqlEntity.getQuery() != null) {
			cmdarray[cmdarray.length - 1] = "\"" + hplSqlEntity.getQuery()
					+ "\"";
			return cmdarray;
		}
		if (hplSqlEntity.getUri() != null) {
			cmdarray[cmdarray.length - 1] = hplSqlEntity.getUri();
			return cmdarray;
		}

		throw new RuntimeException(
				"query or uri should be present with command");

	}
}