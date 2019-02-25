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
