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

package hydrograph.ui.graph.model.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * The class GenerateUniqueJobIdUtil 
 * Used for Generating Unique Job Id.
 * @author Bitwise
 * 
 */
public class GenerateUniqueJobIdUtil {
	public static final GenerateUniqueJobIdUtil INSTANCE = new GenerateUniqueJobIdUtil();

	/**
	 * Generates Unique Job Id.
	 * 
	 * @return {@link String}
	 */
	public String generateUniqueJobId() throws NoSuchAlgorithmException {
		String uniqueJobId = "";
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		int number = 0;
		for (int i = 0; i < 20; i++) {
			number = random.nextInt(21);
		}
		byte[] secureRandom = random.getSeed(number);
		long milliSeconds = System.currentTimeMillis();
		String timeStampLong = Long.toString(milliSeconds);

		/*
		 * String timeStamp = new
		 * SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		 * this.uniqueJobId=jobId.concat(""+secureRandom.hashCode()).concat(
		 * JOB_ID_STRING_SEPARATOR+timeStampLong) + JOB_ID_STRING_SEPARATOR +
		 * timeStamp;
		 */
		uniqueJobId = "Job_".concat("" + secureRandom.hashCode()).concat("_" + timeStampLong);

		return uniqueJobId;
	}
}