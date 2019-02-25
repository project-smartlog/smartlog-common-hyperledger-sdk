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

package com.propentus.common.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.security.MessageDigest;

public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * Calculate MD5 hash for file
	 * @param file
	 * @return
	 */
	public static String calculateHash(File file) {
		
		String hash = "";
		try {		
			//Use MD5 algorithm
			MessageDigest digest = MessageDigest.getInstance("MD5");
			
			//Get file input stream for reading the file content
		    FileInputStream fis = new FileInputStream(file);
		     
		    //Create byte array to read data in chunks
		    byte[] byteArray = new byte[1024];
		    int bytesCount = 0; 
		      
		    //Read file data and update in message digest
		    while ((bytesCount = fis.read(byteArray)) != -1) {
		        digest.update(byteArray, 0, bytesCount);
		    };
		     
		    //close the stream; We don't need it now.
		    fis.close();
		     
		    //Get the hash's bytes
		    byte[] bytes = digest.digest();
		     
		    //This bytes[] has bytes in decimal format;
		    //Convert it to hexadecimal format
		    StringBuilder sb = new StringBuilder();
		    for(int i=0; i< bytes.length ;i++)
		    {
		        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
		     
		    hash = sb.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}
	
	public static void downloadAndSave(String sourceURL, String targetDirectory, String fileName) throws IOException {
	    URL url = new URL(sourceURL);
	    Path targetPath = new File(targetDirectory + File.separator + fileName).toPath();
	    Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static File readFile(String filePath) {
		return new File(filePath);
	}
	
	public static void renameFile(String fullFilePath, String newFileName) throws IOException {
	    File file = new File(fullFilePath);
	    if(file.exists()) {
	    	File newFile = new File(file.getParent(), newFileName);
	    	Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	    }    
	}

	/**
	 * Read file as a String
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readFileAsString(String path) throws IOException {

		logger.info("Reading file from path: " + path);

		byte[] content = Files.readAllBytes(Paths.get(path));
		String fileAsString = new String(content, Charset.forName("UTF-8"));

		return fileAsString;
	}

	/**
	 * Read file as byte[]
	 *
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileBytes(String filename) throws IOException
	{
		logger.info("Reading file " + filename);

		Path path = Paths.get(filename);
		return Files.readAllBytes(path);
	}

	/**
	 * Writes file to disk, file can be overridden with override parameter
	 *
	 * @param path
	 * @param filename
	 * @param content
	 * @param override
	 * @throws IOException
	 */
	public static void writeFile(String path, String filename, String content, boolean override) throws IOException {

		logger.info("Writing file to disk. Path: {0}, filename: {1} ", path, filename);

		//  Create new folder for configtx if not exists
		Path pathToFile = Paths.get(path + filename);
		File folder = new File(path);
		folder.mkdirs();

		if (override) {
			Files.write(pathToFile, content.getBytes());
		}
		else {
			//	Write new file to disk
			Files.write(pathToFile, content.getBytes(), StandardOpenOption.CREATE_NEW,  StandardOpenOption.APPEND);
		}
	}

	/**
	 * Deletes directory if it exists
	 *
	 * @param path
	 * @throws IOException
	 */
	public static void deleteDirectory(String path) throws IOException {

		logger.info("Deleting directory: " + path);

		Path p = Paths.get(path);

		if (p.toFile().exists()) {
			Files.walk(Paths.get(path))
					.map(Path::toFile)
					.sorted((o1, o2) -> -o1.compareTo(o2))
					.forEach(File::delete);
		}
	}

	/**
	 * Deletes file from the disk
	 *
	 * @param filePath
	 */
	public static boolean deleteFile(String filePath) {

		logger.info("Deleting file: " + filePath);
		File file = new File(filePath);
		return file.delete();
	}
}
