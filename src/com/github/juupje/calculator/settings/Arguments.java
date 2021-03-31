package com.github.juupje.calculator.settings;

import java.io.File;
import java.util.HashMap;

import com.github.juupje.calculator.main.Calculator;

public class Arguments {
	public static final String DEBUG = "debug";
	public static final String ANSI_COLORS = "ansicolors";
	public static final String SILENT_ANSWERS = "silentanswers";
	public static final String PLUGIN_DIR = "plugindir";
	public static final String WORK_DIR = "workdir";
	public static final String RUN = "run";
	
	
	private static HashMap<String, Object> arguments = new HashMap<>();
	static {
		//set default booleans
		arguments.put("debug", false);
		arguments.put("ansicolors", false);
		arguments.put("silentanswers", false);
	}
	
	public static void parse(String[] args) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-")) {
				String arg = args[i].substring(1).toLowerCase();
				try {
					switch(arg) {
					case PLUGIN_DIR:
						arguments.put(arg, getNext(args[i+1]));
						i++;
						break;
					case WORK_DIR:
						String path = getNext(args[i+1]);
						File file = new File(path);
						if(file.exists() && file.isDirectory()) {
							if(!path.endsWith("/") && !path.endsWith("\\"))
								path += (path.contains("/") ? "/" : "\\");
							arguments.put(arg, path);
						} else
							Calculator.ioHandler.err(path + " is not a valid work directory");
						i++;
						break;
					case DEBUG:
						arguments.put(arg, true);
						break;
					case RUN:
							arguments.put(arg, args[i+1]);
						i++;
						break;
					case ANSI_COLORS:
						arguments.put(arg, true);
						break;
					case SILENT_ANSWERS:
						arguments.put(arg, true);
						break;
					default:
						Calculator.ioHandler.err("Unknown argument " + args[i]);						
					}
				} catch(NullPointerException e) {
					Calculator.ioHandler.err("Expected argument after " + args[i]);
				} catch(ArrayIndexOutOfBoundsException e) {
					Calculator.ioHandler.err("Expected argument after " + args[i]);					
				}
			} else {
				Calculator.ioHandler.err("Cannot parse argument " + args[i]);
			}
		}
	}
	
	public static boolean exists(String key) {
		return arguments.get(key)!=null;
	}
	
	public static String get(String key) {
		return (String) arguments.get(key);
	}
	
	public static boolean getBool(String key) {
		if(exists(key))
			return (boolean) arguments.get(key);
		return false;
	}
	
	private static String getNext(String arg) {
		if(arg.startsWith("-"))
			throw new NullPointerException();
		return arg;
	}
	
}
