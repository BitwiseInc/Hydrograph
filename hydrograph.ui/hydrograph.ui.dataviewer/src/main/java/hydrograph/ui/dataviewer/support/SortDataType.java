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

package hydrograph.ui.dataviewer.support;

import hydrograph.ui.logging.factory.LogFactory;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;


/**
 * The Enum SortDataType.
 * Specifies sort data type and their compare logics.
 * @author Bitwise
 */
public enum SortDataType {
	
	
	STRING {
		@Override
		protected int compareTo(String cell_1, String cell_2, String parameter) {
			return cell_1.compareToIgnoreCase(cell_2);
		}
		@Override
		public String getDataType() {
			return String.class.getName();
		}
	},
	INTEGER {
		@Override
		protected int compareTo(String cell_1, String cell_2, String parameter) {
			return Integer.valueOf(cell_1).compareTo(Integer.valueOf(cell_2));
		}
		@Override
		public String getDataType() {
			return Integer.class.getName();
		}
	},
	DOUBLE {
		@Override
		protected int compareTo(String cell_1, String cell_2, String parameter) {
			return Double.valueOf(cell_1).compareTo(Double.valueOf(cell_2));
		}
		@Override
		public String getDataType() {
			return Double.class.getName();
		}
	},
	FLOAT {
		@Override
		protected int compareTo(String cell_1, String cell_2, String parameter) {
			return Float.valueOf(cell_1).compareTo(Float.valueOf(cell_2));
		}
		@Override
		public String getDataType() {
			return Float.class.getName();
		}
	},
	SHORT {
		@Override
		protected int compareTo(String cell_1, String cell_2, String parameter) {
			return Short.valueOf(cell_1).compareTo(Short.valueOf(cell_2));
		}
		@Override
		public String getDataType() {
			return Short.class.getName();
		}
	},
	BOOLEAN {
		@Override
		protected int compareTo(String cell_1, String cell_2, String parameter) {
			return Boolean.valueOf(cell_1).compareTo(Boolean.valueOf(cell_2));
		}
		@Override
		public String getDataType() {
			return Boolean.class.getName();
		}
	},
	DATE {
		@Override
		protected int compareTo(String cell_1, String cell_2, String dateFormat) {
			DateFormat format = new SimpleDateFormat(dateFormat);
			Date date1 = null;
			Date date2 = null;
			try {
				date1 = format.parse(cell_1);
				date2 = format.parse(cell_2);
			} catch (ParseException e) {
				logger.error("Date parsing exception - unable to sort on this date",e);
			}
			return date1.compareTo(date2);
		}
		
		@Override
		public String getDataType() {
			return Date.class.getName();
		}
	},
	BIGDECIMAL {
		@Override
		protected int compareTo(String cell_1, String cell_2, String parameter) {
			return new BigDecimal(cell_1).compareTo(new BigDecimal(cell_2));
		}
		@Override
		public String getDataType() {
			return BigDecimal.class.getName();
		}
	},
	LONG {
		@Override
		protected int compareTo(String cell_1, String cell_2, String parameter) {
			return Long.valueOf(cell_1).compareTo(Long.valueOf(cell_2));
		}
		@Override
		public String getDataType() {
			return Long.class.getName();
		}
	};
	
	/**
	 *
	 * Compares cell_1 with cell_2 
	 * 
	 * @param cell_1
	 * @param cell_2
	 * @param dateFormat
	 * @return
	 */
	protected abstract int compareTo(String cell_1, String cell_2, String dateFormat);
	
	/**
	 * 
	 * Returns data type
	 * 
	 * @return - datatype
	 */
	public abstract String getDataType();
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(SortDataType.class);
}
