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

package hydrograph.ui.validators.impl.test;

import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.validators.impl.MixedSchemeGridValidationRule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MixedSchemeGridValidationRuleTest {

	MixedSchemeGridValidationRule mixedSchemeGridRule = new MixedSchemeGridValidationRule();
	private String expected;

	@Before
	public void setUP() {
		expected = "";
	}

	@Test
	public void testGridWithEmptyLengthANDEmptyDelimiter_fail() {
		expected = "Length Or Delimiter is mandatory";
		mixedSchemeGridRule.validate(getSchema("", ""), "schema");
		Assert.assertEquals(expected, mixedSchemeGridRule.getErrorMessage());
	}

	@Test
	public void testGridWithEmptyLengthANDProperDelimiter_success() {
		mixedSchemeGridRule.validate(getSchema("", "|"), "schema");
		Assert.assertTrue(StringUtils.isBlank(mixedSchemeGridRule
				.getErrorMessage()));
	}

	@Test
	public void testGridWithProperLengthANDEmptyDelimiter_success() {
		mixedSchemeGridRule.validate(getSchema("1", ""), "schema");
		Assert.assertTrue(StringUtils.isBlank(mixedSchemeGridRule
				.getErrorMessage()));
	}

	@Test
	public void testGridWithProperLengthANDProperDelimiter_fail() {
		expected = "Either Length Or Delimiter should be given";
		mixedSchemeGridRule.validate(getSchema("1", "|"), "schema");
		Assert.assertEquals(expected, mixedSchemeGridRule.getErrorMessage());
	}

	@Test
	public void testGridWithZeroLengthANDEmptyDelimiter_fail() {
		expected = "Length should not be zero or negative";
		mixedSchemeGridRule.validate(getSchema("0", ""), "schema");
		Assert.assertEquals(expected, mixedSchemeGridRule.getErrorMessage());
	}

	@Test
	public void testGridWithZeroLengthANDProperDelimiter_fail() {
		expected = "Length should not be zero or negative";
		mixedSchemeGridRule.validate(getSchema("0", "|"), "schema");
		Assert.assertEquals(expected, mixedSchemeGridRule.getErrorMessage());
	}

	private Schema getSchema(String length, String delimiter) {
		Schema schema = new Schema();
		MixedSchemeGridRow mixedSchemaGridRow = new MixedSchemeGridRow();
		List<GridRow> gridRows = new ArrayList<>();
		schema.setExternalSchemaPath("");
		schema.setIsExternal(false);
		mixedSchemaGridRow.setFieldName("emp");

		mixedSchemaGridRow.setLength(length);
		mixedSchemaGridRow.setDelimiter(delimiter);

		gridRows.add(mixedSchemaGridRow);
		schema.setGridRow(gridRows);
		return schema;
	}

}
