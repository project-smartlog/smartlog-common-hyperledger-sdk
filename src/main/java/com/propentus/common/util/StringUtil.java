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

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;

/**
 * Utility class for content related string formatting
 */
public class StringUtil {

	/**
	 * removes eval, script, javascript -words and <> chars from toBeCleaned
	 * 
	 * @param String
	 *            toBeCleaned
	 * @return cleaned String
	 */
	public static String cleanHtmlFromString(String toBeCleaned) {
		toBeCleaned = toBeCleaned.replaceAll("<", "").replaceAll(">", "");
		toBeCleaned = toBeCleaned.replaceAll("eval\\((.*)\\)", "");
		toBeCleaned = toBeCleaned.replaceAll(
			"[\\\"\\\'][\\s]*((?i)javascript):(.*)[\\\"\\\']", "\"\"");
		toBeCleaned = toBeCleaned.replaceAll("(?i)script\\b", "");
		return toBeCleaned;
	}

	/**
	 * removes eval, script, javascript -words from toBeCleaned
	 * 
	 * @param String
	 *            toBeCleaned
	 * @return cleaned String
	 */
	public static String cleanJavascriptFromString(String toBeCleaned) {
		toBeCleaned = toBeCleaned.replaceAll("eval\\((.*)\\)", "");
		toBeCleaned = toBeCleaned.replaceAll(
			"[\\\"\\\'][\\s]*((?i)javascript):(.*)[\\\"\\\']", "\"\"");
		toBeCleaned = toBeCleaned.replaceAll("(?i)script\\b", "");
		return toBeCleaned;
	}

	/**
	 * 
	 * @param url
	 * @return if url doesn't start with http or https we return url that begins
	 *         with http
	 */

	public static String checkUrlStartsWithHttp(String url) {
		if (url != null && !url.isEmpty()) {
			url = url.trim();
			if (!url.startsWith("http") && !url.startsWith("/")) {
				url = "http://" + url;
			}
		}

		return url;

	}
	
	public static String replaceTokens(String str, Object... args) {

		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				String token = "{" + String.valueOf(i) + "}";
				Object arg = args[i];
				String tokenValue = "null";
				if (arg != null) {
					tokenValue = arg.toString();
				}
				str = str.replace(token, tokenValue);
			}
		} else {
			str = str.replace("{0}", "null");
		}
		return str;
	}
	
	/**
	 * Format XML string to more readable form. Very helpful especially when printing XML to log file, for example.
	 * @param xml
	 * @return
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static String prettyFormatXML(String xml) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource(new StringReader(xml));
	        Document doc = db.parse(is);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result = new StreamResult(xml);
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			xml = result.getWriter().toString();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return xml;
	}
	
	public static boolean notEmpty(String str) {
		return str != null && !str.equals("");
	}
	
	  /**
     * Check if String is null or contains empty value. Given String is trimmed before check so "  " and like return false.
     * @return
     */
    public static boolean isEmpty(String str) {
        //Check null first so we don't get NullPointerException on str.trim()
        if(str == null) {
            return true;
        }
        str = str.trim();
        return str.equals("");
    }
	
}
