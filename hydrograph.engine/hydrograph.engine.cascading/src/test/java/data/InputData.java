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
package data;

public interface InputData
{
public static final String TEST_DATA_PATH = "test.data.path";

String inputPath = System.getProperty( TEST_DATA_PATH, "src/test/resources/data/" );

String discardInput = inputPath + "delimitedFile.txt";
String inputFileDelimited = inputPath + "file.txt";
String inputFileFixedWidth = inputPath + "FixedWidthFile.txt";
String inputFileParquet = inputPath + "part-00000-m-00000.parquet";
String outputFileDelimited = inputPath + "outputFileDelimitedInput";
String outputFileFixedWidth = inputPath + "file.txt";
String outputFileParquet = inputPath + "delimitedFile.txt";
String uniqueSequenceInput = inputPath + "input.txt";
String delimitedUtf8inputfilewindowsEOL = inputPath + "delimitedUtf8inputfilewindowsEOL";
String delimitedIsoinputfilewindowsEOL = inputPath + "delimitedIsoinputfilewindowsEOL";
String delimitedUtf8inputfilelinuxEOL = inputPath + "delimitedUtf8inputfilelinuxEOL";
String delimitedIsoinputfilelinuxEOL = inputPath + "delimitedIsoinputfilelinuxEOL";
String fixedWidthUtf8inputfilewindowsEOL=inputPath+"fixedWidthUtf8inputfilewindowsEOL";
String fixedWidthUtf8inputfilelinuxEOL=inputPath+"fixedWidthUtf8inputfilelinuxEOL";
String fixedWidthIsoinputfilewindowsEOL=inputPath+"fixedWidthIsoinputfilewindowsEOL";
String fixedWidthIsoinputfilelinuxEOL=inputPath+"fixedWidthIsoinputfilelinuxEOL";
String utf8inputfilewindowsEOL=inputPath+"utf8inputfilewindowsEOL";
String isoinputfilewindowsEOL=inputPath+"isoinputfilewindowsEOL";
String utf8inputfilelinuxEOL=inputPath+"utf8inputfilelinuxEOL";
String isoinputfilelinuxEOL=inputPath+"isoinputfilelinuxEOL";
String itShouldProduceValidResultsForSimpleMixedScheme=inputPath+"itShouldProduceValidResultsForSimpleMixedScheme.txt";
String itShouldProduceValidResultsForAllRecordsInOneLine=inputPath+"itShouldProduceValidResultsForAllRecordsInOneLine.txt";
String itShouldProduceValidResultsForCedillaDelimitedRecords=inputPath+"itShouldProduceValidResultsForCedillaDelimitedRecords.txt";
String itShouldProduceValidResultsForRecordSpanningMultipleLines=inputPath+"itShouldProduceValidResultsForRecordSpanningMultipleLines.txt";
String itShouldProduceValidResultsForRecordWithLastFixedWidthField=inputPath+"itShouldProduceValidResultsForRecordWithLastFixedWidthField.txt";
String itShouldProduceValidResultsForSimpleMixedSchemeWithFixedNewlineField=inputPath+"itShouldProduceValidResultsForSimpleMixedSchemeWithFixedNewlineField.txt";
String itShouldProduceValidResultsForAllRecordsInOneLineWithFixedNewlineField=inputPath+"itShouldProduceValidResultsForAllRecordsInOneLineWithFixedNewlineField.txt";
String itShouldProduceValidResultsForCedillaDelimitedRecordsWithFixedNewlineField=inputPath+"itShouldProduceValidResultsForCedillaDelimitedRecordsWithFixedNewlineField.txt";
String itShouldProduceValidResultsForRecordSpanningMultipleLinesWithFixedNewlineField=inputPath+"itShouldProduceValidResultsForRecordSpanningMultipleLinesWithFixedNewlineField.txt";
String itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndFixedNewlineField=inputPath+"itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndFixedNewlineField.txt";
String itShouldProduceValidResultsForSimpleMixedSchemeWithDelimitedNewlineField=inputPath+"itShouldProduceValidResultsForSimpleMixedSchemeWithDelimitedNewlineField.txt";
String itShouldProduceValidResultsForAllRecordsInOneLineWithDelimitedNewlineField=inputPath+"itShouldProduceValidResultsForAllRecordsInOneLineWithDelimitedNewlineField.txt";
String itShouldProduceValidResultsForCedillaDelimitedRecordsWithDelimitedNewlineField=inputPath+"itShouldProduceValidResultsForCedillaDelimitedRecordsWithDelimitedNewlineField.txt";
String itShouldProduceValidResultsForRecordSpanningMultipleLinesWithDelimitedNewlineField=inputPath+"itShouldProduceValidResultsForRecordSpanningMultipleLinesWithDelimitedNewlineField.txt";
String itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndDelimitedNewlineField=inputPath+"itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndDelimitedNewlineField.txt";
String itShouldProduceValidResultsForFixedWidthRecordsInOneLine=inputPath+"itShouldProduceValidResultsForFixedWidthRecordsInOneLine.txt";
String itShouldProduceValidResultsForDelimiterPresentInFixedWidthData=inputPath+"itShouldProduceValidResultsForDelimiterPresentInFixedWidthData.txt";
String itShouldProduceValidResultsForAllDataTypes=inputPath+"itShouldProduceValidResultsForAllDataTypes.txt";
String itShouldProduceValidResultsForSimpleMixedSchemeWithQuoteChar=inputPath+"itShouldProduceValidResultsForSimpleMixedSchemeWithQuoteChar.txt";
}