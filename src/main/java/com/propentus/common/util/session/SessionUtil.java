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

package com.propentus.common.util.session;

import org.grails.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Containts utility methods related to HTTP session
 */
public class SessionUtil {

	private static final Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	/**
	 * Invalidates old HTTP session and creates new session. Copies all session
	 * attributes from old session to new
	 *
	 * @param request
	 * @return new session
	 */
	public static HttpSession createNewSession(HttpServletRequest request) {
		HttpSession oldSession = request.getSession();
		// Copy attributes from old session
		Enumeration<String> attrs = oldSession.getAttributeNames();
		Map<String, Object> temp = new HashMap<String, Object>();

		while (attrs.hasMoreElements()) {
			String key = attrs.nextElement();
			temp.put(key, oldSession.getAttribute(key));
		}

		oldSession.invalidate();

		// Create new session and copy old attributes to it
		HttpSession newSession = request.getSession();
		for (Entry<String, Object> entry : temp.entrySet()) {
			newSession.setAttribute(entry.getKey(), entry.getValue());
		}
		return newSession;
	}

	/**
	 * Get current session anywhere in application
	 *
	 * @return current session
	 */
	public static HttpSession getCurrentSession() {
		HttpServletRequest request = WebUtils.retrieveGrailsWebRequest().getCurrentRequest();
		return request.getSession(false);
	}

	public static HttpServletRequest getCurrentRequest() {
		return WebUtils.retrieveGrailsWebRequest().getCurrentRequest();
	}

	public static HttpServletResponse getCurrentResponse() {
		return WebUtils.retrieveGrailsWebRequest().getCurrentResponse();
	}
}
