package com.github.juupje.calculator.helpers;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.juupje.calculator.helpers.io.ANSITags;
import com.github.juupje.calculator.helpers.io.IOHandler;
import com.github.juupje.calculator.helpers.io.JSONReader;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.plugins.PluginLoader;

public class Helper {
	static JSONObject json = null;
	static final String[] keys = {"commands", "functions", "algorithms", "settings", "constants"};
	private static void init() {
		try {
			json = JSONReader.parse(Helper.class.getResourceAsStream("/com/github/juupje/calculator/files/help.json"));
			PluginLoader.initHelp();
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}
	
	public static void insertHelpFile(JSONObject jobj) {
		if(jobj == null) return;
		for(String key : keys) {
			if(jobj.has(key)) {
				JSONObject obj = json.getJSONObject(key);
				JSONObject jobjkey = jobj.getJSONObject(key);
				for(Iterator<String> kIter = jobjkey.keys(); kIter.hasNext();) {
					String k = kIter.next();
					obj.put(k, jobjkey.get(k));
				}
			}
		}
	}

	public static void printHelp(String command) {
		if (json == null)
			init();
		JSONObject jobj = null;
		String type = "";
		for(String s : keys) {
			try {
				jobj = json.getJSONObject(s).getJSONObject(command);
				type = s.substring(0, s.length()-1); //remove the last 's';
			} catch(JSONException e) {}
		}
		if(jobj == null) {
			Calculator.ioHandler.err("No known help page for " + command + ", type list for a list of commands/functions/algorithms/settings/constants");
			return;			
		}
		
		String help = "Help for " + command + " (" + type + "):\n";
		if(jobj.has("plugin"))
			help += "\tFrom plugin: " + jobj.getString("plugin") + "\n";
		if(type.equals("setting"))
			help += "\t"+IOHandler.ansi("Name: ", ANSITags.ANSI_RED) + jobj.getString("name")
					+ "\n\t"+IOHandler.ansi("Type: ", ANSITags.ANSI_RED) + jobj.getString("type")
					+ "\n\t"+IOHandler.ansi("Description: ", ANSITags.ANSI_RED) + jobj.getString("description")
					+ "\n\t"+IOHandler.ansi("Default: ", ANSITags.ANSI_RED) + jobj.get("default");
		else if(type.equals("constant")) {
			help += "\tName: " + jobj.getString("name")
			+ "\n\tValue: " + jobj.getString("value");
		} else
			help += "\t" + IOHandler.ansi("Syntax: ", ANSITags.ANSI_RED) + jobj.getString("syntax")
					+ "\n\t" + IOHandler.ansi("Description: ", ANSITags.ANSI_RED)+ jobj.getString("description")
					+ "\n\t" + IOHandler.ansi("Arguments: ", ANSITags.ANSI_RED) + jobj.getString("arguments")
					+ "\n\t" + IOHandler.ansi("Result: ", ANSITags.ANSI_RED) + jobj.getString("result");
		Calculator.ioHandler.out(help);
	}

	public static void printList(String args) {
		String list = null;
		if(json == null)
			init();
		if(args.equals("")) {
			for(String s : keys)
				list += "\n\n" + s.substring(0, 1).toUpperCase() + s.substring(1) + ":\n\t" + Tools.join("\n\t", json.getJSONObject(s).keySet());
			list = list.substring(2); //remove the first two linebreaks
		} else {
			List<String> keyList = json.getJSONObject(args).keySet().stream().collect(Collectors.toList());
			Collections.sort(keyList);
			list = args + ":\n\t" + Tools.join("\n\t", keyList);
		}
		Calculator.ioHandler.out(list);
	}
}