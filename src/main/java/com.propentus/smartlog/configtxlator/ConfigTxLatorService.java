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

package com.propentus.smartlog.configtxlator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.propentus.common.exception.ConfigurationException;
import com.propentus.iot.configs.ConfigReader;
import com.propentus.smartlog.common.configs.AdminClientConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.propentus.common.util.file.FileUtil;
import com.propentus.smartlog.util.MultipartUtility;

/**
 * Class that holds utility methods to handle using of configtxlator.
 * 
 *  - Translate configBlock to JSON.
 *  - Wrap old and new config protos to channel update proto
 *  - Translate JSON-config to proto
 *
 */
public class ConfigTxLatorService {

	private static final String CONFIG_DECODE_URL = ConfigtxlatorConfiguration.getDecodeUrl();
	private static final String CONFIG_ENCODE_URL = ConfigtxlatorConfiguration.getEncodeUrl();
	private static final String CONFIGS_TO_PROTO_URL = ConfigtxlatorConfiguration.getConfigsToProtoUrl();
	
	/**
	 * Sends configBlock to configtxlator and gets JSON-version back.
	 * @param configBlock
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static String getConfigAsJson(byte[] configBlock) throws IOException {

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(CONFIG_DECODE_URL);
        httppost.setEntity(new ByteArrayEntity(configBlock));
        HttpResponse response = httpclient.execute(httppost);
		
		return EntityUtils.toString(response.getEntity());
	}
	
	/**
	 * Get config as a proto from configtxlator
	 * @param config
	 * @return
	 * @throws IOException 
	 */
	public static byte[] getConfigAsProto(String config) throws IOException {

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(CONFIG_ENCODE_URL);
        httppost.setEntity(new StringEntity(config));
        HttpResponse response = httpclient.execute(httppost);
		
		return EntityUtils.toByteArray(response.getEntity());
	}
	
	/**
	 * Sends old and new version of the config to configtxlator and gets back proto
	 * which is then used to configure the channel.
	 *
	 * @param oldConfig
	 * @param newConfig
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static byte[] wrapNewAndOldConfig(String oldConfig, String newConfig) throws ClientProtocolException, IOException, ConfigurationException {
		
		//	Get old config as a proto
		byte[] oldConfigProto = getConfigAsProto(oldConfig);
		
		//	Get new config as a proto
		byte[] newConfigProto = getConfigAsProto(newConfig);
		
		//	Get config update proto from configtxlator
		return getUpdateProto(oldConfigProto, newConfigProto);
	}
	
	/**
	 * 
	 * Do a POST to configtxlator with old config proto, new config proto and a channel name.
	 * 
	 * @param oldConfigProto
	 * @param newConfigProto
	 * 
	 * @return 
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static byte[] getUpdateProto(byte[] oldConfigProto, byte[] newConfigProto) throws ClientProtocolException, IOException, ConfigurationException {
		
		HttpClient httpclient = HttpClients.createDefault();
	    HttpPost httppost = new HttpPost(CONFIGS_TO_PROTO_URL);
	    ConfigReader configReader = new ConfigReader();
		
	    HttpEntity multipartEntity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("original", oldConfigProto, ContentType.APPLICATION_OCTET_STREAM, "config.pb")
                .addBinaryBody("updated", newConfigProto, ContentType.APPLICATION_OCTET_STREAM, "updated_config.pb")
                .addBinaryBody("channel", configReader.getOrganisationConfiguration().channel.getBytes()).build();
		
	    httppost.setEntity(multipartEntity);
	    HttpResponse response = httpclient.execute(httppost);
	    
		return EntityUtils.toByteArray(response.getEntity());

	}	
}
