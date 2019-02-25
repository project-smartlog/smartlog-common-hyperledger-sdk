/*
 *
 *  * Copyright 2016-2019
 *  *
 *  * Interreg Central Baltic 2014-2020 funded project
 *  * Smart Logistics and Freight Villages Initiative, CB426
 *  *
 *  * Kouvola Innovation Oy, FINLAND
 *  * Region Örebro County, SWEDEN
 *  * Tallinn University of Technology, ESTONIA
 *  * Foundation Valga County Development Agency, ESTONIA
 *  * Transport and Telecommunication Institute, LATVIA
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.propentus.common.util;

public class DataFormatter {

	/**
	 * Formats given arguments to single String line. Each argument's String
	 * representation is delimited with given delimiter.
	 * 
	 * @param delimiter Used delimiter character
	 * @param args
	 * @return
	 */
	public static String createDelimitedString(String delimiter, Object... args) {
		StringBuilder sb = new StringBuilder();
		int i = 1;
		for (Object arg : args) {
			String value = "";
			if (arg != null) {
				value = arg.toString();
			}
			sb.append(value);
			//Don't add delimiter to last argument
			if (i < args.length) {
				sb.append(delimiter);
			}
			i++;
		}
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * Gets data from String with delimiter at specific index
	 * 
	 * @param line
	 * @param delimiter Delimiter character
	 * @param index
	 * @return
	 */
	public static String getDataFromFormattedString(String line, String delimiter, int index) {
		//Second parameter in split method is negative, because then the result array will include any trailing empty strings.
		String data = line.split(delimiter, -1)[index];
		if (data == null) {
			data = "";
		}
		return data;
	}

	public static String fillTemplate(String template, Object... values) {

		Integer i = 0;
		for(Object value : values) {
			String indexToReplace = "{" + i.toString() + "}";

			if(value != null) {
				template = template.replace(indexToReplace, value.toString());
			}
			else {
				template = template.replace(indexToReplace, "null");
			}
			i++;
		}
		return template;
	}

}
