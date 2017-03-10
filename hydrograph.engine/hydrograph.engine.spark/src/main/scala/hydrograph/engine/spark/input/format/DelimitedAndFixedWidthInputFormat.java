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
package hydrograph.engine.spark.input.format;

import hydrograph.engine.spark.recordreader.DelimitedAndFixedWidthRecordReader;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
/**
 * The Class DelimitedAndFixedWidthInputFormat.
 *
 * @author Bitwise
 *
 */
public class DelimitedAndFixedWidthInputFormat extends
		FileInputFormat<LongWritable, Text> {

	@Override
	public RecordReader<LongWritable, Text> getRecordReader(
			InputSplit genericSplit, JobConf job, Reporter reporter)
			throws IOException {

		reporter.setStatus(genericSplit.toString());
		return new DelimitedAndFixedWidthRecordReader(job,
				(FileSplit) genericSplit);
	}

	@Override
	protected boolean isSplitable(FileSystem fs, Path filename) {
		return false;
	}
}
