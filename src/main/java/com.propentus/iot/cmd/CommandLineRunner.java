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

package com.propentus.iot.cmd;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Utility class for running commands in CLI
 */
public class CommandLineRunner {

	private static Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);

	public static String executeCommand(String[] envp, String command, Object... args) throws InterruptedException, IOException, CommandLineException {

		int i = 0;
		for (Object object : args) {
			command = command.replace("{" + i + "}", object.toString());
			i++;
		}
		Process process = null;
		try {
			System.out.println("Executing command:" + command);
			process = Runtime.getRuntime().exec(command, envp);
		} catch (IOException e) {
            System.out.println("Error while executing command:" + command);
			e.printStackTrace();
		}

        String output = IOUtils.toString(process.getInputStream());

		int state = process.waitFor();

		//  Everything went well
		if (state == 0) {
		    return output;
        }
        else {
		    throw new CommandLineException(IOUtils.toString(process.getErrorStream()));
        }
	}
}
