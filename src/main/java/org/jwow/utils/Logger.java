/**
 * Copyright (C) 2013 @Le.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jwow.utils;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
/**
 * @author le
 *
 */
public class Logger {
	private static boolean off = false;
	
	public static void silence() {
		off = true;
	}
	public static void info(String format, Object ... args) {
		if(!off)
			out.printf(new StringBuilder(format).append("\n").toString(), args);
	}
	
	public static void info(String info) {
		if(!off)
			out.println(info);
	}
	
	public static void println(String info) {
			out.println(info);
	}
	
	public static void warning(String info) {
		if(!off)
			err.println(info);
	}
	
	public static void write(String fileName, String info) {
		write(fileName, info, true);
	}
	
	public static void write(String fileName, String info, boolean interactive) {
		File file = new File(fileName);
		if(file.exists()) {
			Logger.warning(fileName + " exists. Should I remove it and continue ? [y/N]");
			Scanner scanner = new Scanner(System.in); 
			String go = scanner.nextLine();
			if(!go.trim().equals("y"))
				return;
		}
		
		try {
	        FileOutputStream fos = new FileOutputStream(fileName);
	        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	        osw.write(info);
	        osw.flush();
	        osw.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		/*
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(fileName));
			bw.write(info);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
}
