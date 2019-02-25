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

package com.propentus.common.util.http;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthenticationParser {

    private HttpServletRequest request;
    private final String HEADER_AUTH = "Authorization";
    private final String SWAGGER_REFERER = "webjars/swagger-ui/";

    private String authHeader;
    private boolean swaggerRequest;

    private String username;
    private String password;

    public BasicAuthenticationParser(HttpServletRequest request) {
        this.request = request;
    }

    public void parse() throws UnsupportedEncodingException, ArrayIndexOutOfBoundsException {
        this.authHeader = this.request.getHeader(HEADER_AUTH);
        String refererHeader = this.request.getHeader("Referer");

        if(refererHeader != null) {
            swaggerRequest = refererHeader.contains(SWAGGER_REFERER);
        }

        if(containsAuth()) {
            //Parse username and password
            String credendials = this.authHeader.split(" ")[1];
            //Credendials contain username:password in base64 decode form
            String decoded = new String(Base64.getDecoder().decode(credendials), StandardCharsets.UTF_8);
            this.username = decoded.split(":")[0];
            this.password = decoded.split(":")[1];
        }
    }

    public boolean containsAuth() {
        return authHeader != null;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
    public String getAuthToken() {return this.authHeader; }

    public void setAuthRequiredResponse(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        if(!swaggerRequest) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"User Visible Realm\"");
        }
    }

    public static String createAuthnToken(String username, String password) throws UnsupportedEncodingException {
        String creds = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }


}
