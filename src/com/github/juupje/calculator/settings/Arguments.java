package com.github.juupje.calculator.settings;

import java.io.File;
import java.util.HashMap;

import com.github.juupje.calculator.main.Calculator;

public class Arguments {
	private static HashMap<String, Object> arguments = new HashMap<>();
	
	public static void parse(String[] args) {
		//set default booleans
		arguments.put("debug", false);
		arguments.put("ansicolors", false);
		for(int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-")) {
				String arg = args[i].substring(1);
				try {
					switch(arg) {
					case "plugindir":
						arguments.put(arg, getNext(args[i+1]));
						i++;
						break;
					case "workdir":
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
					case "debug":
						arguments.put(arg, true);
						break;
					case "run":
							arguments.put(arg, args[i+1]);
						i++;
						break;
					case "ansicolors":
						arguments.put(arg, true);
						break;
					default:
						Calculator.ioHandler.err("Unknown argument " + args[i]);						
					}
				} catch(NullPointerException e) {
					Calculator.ioHandler.err("Expected argument after " + arg);
				} catch(ArrayIndexOutOfBoundsException e) {
					Calculator.ioHandler.err("Expected argument after " + arg);					
				}
			} else {
				Calculator.ioHandler.err("Cannot parse argument " + args[i]);
			}
		}
	}
	
	public static boolean exists(String key) {
		return arguments.get(key.toLowerCase())!=null;
	}
	
	public static Object get(String key) {
		return arguments.get(key.toLowerCase());
	}
	
	private static String getNext(String arg) {
		if(arg.startsWith("-"))
			throw new NullPointerException();
		return arg;
	}
	
}
