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
package hydrograph.engine.transformation.userfunctions.filter;

import hydrograph.engine.transformation.userfunctions.base.FilterBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
/**
 * The Class RandomSampling.
 *
 * @author Bitwise
 *
 */
public class RandomSampling implements FilterBase{
	
	private double weight;
	private Random randomVal;

	@Override
	public void prepare(Properties props, ArrayList<String> inputFields) {
		this.randomVal = new Random();
		if (props != null) {
			if (props.getProperty("WEIGHT") != null)
				this.weight = Double.parseDouble(props.getProperty("WEIGHT"));
			else
				throw new SampleException(
						"WEIGHT property not passed in the filter");

			if (props.getProperty("SEED") != null) {
				randomVal.setSeed(Long.parseLong(props.getProperty("SEED")));
			} else
				throw new SampleException(
						"WEIGHT property not passed in the filter");
		} else {
			throw new RuntimeException(
					"properties 'WEIGHT' and 'SEED' are missing in the operation RandomSampling.");
		}
	}

	@Override
	public boolean isRemove(ReusableRow reusableRow) {
		double randomValue=randomVal.nextDouble();
		if(randomValue<weight)
			return false;
		else
			return true;
	}

	@Override
	public void cleanup() {
	}
	
	private class SampleException extends RuntimeException {

		private static final long serialVersionUID = 1444510307746123355L;

		public SampleException(String msg) {
			super(msg);
		}
	}

}
