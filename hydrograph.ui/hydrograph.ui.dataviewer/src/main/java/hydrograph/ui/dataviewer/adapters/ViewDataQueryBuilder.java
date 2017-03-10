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

package hydrograph.ui.dataviewer.adapters;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * The Class ViewDataQueryBuilder. 
 * Used to build basic sql query and has only functionality to form query to Select
 * columns from given table and given limit and offset with multiple where clause(with logical AND)
 * 
 * @author Bitwise
 * 
 */
public class ViewDataQueryBuilder {

	private List<String> columns = new ArrayList<String>();
	private String table;
	private Integer limit;
	private Long offset;

	/**
	 * 
	 * Set column name
	 * 
	 * @param table
	 */
	public ViewDataQueryBuilder(String table) {
		this.table = table;
	}

	/**
	 * 
	 * Add columns
	 * 
	 * @param name
	 * @return {@link ViewDataQueryBuilder}
	 */
	public ViewDataQueryBuilder column(String name) {
		columns.add(name);
		return this;
	}

	/**
	 * 
	 * Add where clause
	 * 
	 * @param name
	 * @return {@link ViewDataQueryBuilder}
	 */
	public ViewDataQueryBuilder setwhereCondition(String filterCondition) {
		return this;
	}

	/**
	 * 
	 * Add limit
	 * 
	 * @param name
	 * @return {@link ViewDataQueryBuilder}
	 */
	public ViewDataQueryBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}

	public ViewDataQueryBuilder offset(Long offset) {
		this.offset = offset;
		return this;
	}

	/**
	 * 
	 * Get final query
	 * 
	 * @return - sql
	 */
	public String getQuery(String filterCondition) {

		StringBuilder sql = new StringBuilder("SELECT ");

		if (columns.size() == 0) {
			sql.append("*");
		} else {
			appendClause(sql, columns, "", ", ");
		}

		appendTableName(sql, table, " FROM ");
		if (filterCondition != null && !filterCondition.isEmpty()) {
			appendWhereClause(sql, filterCondition, " WHERE ");
		}

		if (limit != null)
			appendLimit(sql, limit, " LIMIT ");

		if (offset != null && limit != null)
			appendOffset(sql, offset, " OFFSET ");

		return sql.toString();
	}
	private void appendWhereClause(StringBuilder sql,String whereCondition, String sectionName) {
		
		sql.append(sectionName+whereCondition);
		
	}

	private void appendClause(StringBuilder sql, List<String> list, String sectionName, String separator) {
		boolean first = true;

		for (String s : list) {
			if (first) {
				sql.append(sectionName);
			} else {
				sql.append(separator);
			}
			sql.append(s);
			first = false;
		}
	}

	private void appendLimit(StringBuilder sql, int limit, String sectionName) {
		sql.append(sectionName);
		sql.append(limit);
	}

	private void appendTableName(StringBuilder sql, String table, String sectionName) {
		sql.append(sectionName);
		sql.append(table);
	}

	private void appendOffset(StringBuilder sql, Long offset, String sectionName) {
		sql.append(sectionName);
		sql.append(offset);
	}
}
