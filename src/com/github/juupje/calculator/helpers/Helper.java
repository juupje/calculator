package com.github.juupje.calculator.helpers;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

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
				for(Iterator<String> kIter = jobj.getJSONObject(key).keys(); kIter.hasNext();) {
					String k = kIter.next();
					obj.append(k, jobj.get(k));
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
			help += "\tName: " + jobj.getString("name")
					+ "\n\tType: " + jobj.getString("type")
					+ "\n\tDescription: " + jobj.getString("description")
					+ "\n\tDefault: " + jobj.get("default");
		else if(type.equals("constant")) {
			help += "\tName: " + jobj.getString("name")
			+ "\n\tValue: " + jobj.getString("value");
		} else
			help += "\tSyntax: " + jobj.getString("syntax")
					+ "\n\tDescription: " + jobj.getString("description")
					+ "\n\tArguments: " + jobj.getString("arguments")
					+ "\n\tResult: " + jobj.getString("result");
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
		} else
			list = args + ":\n\t" + Tools.join("\n\t", json.getJSONObject(args).keySet());
		Calculator.ioHandler.out(list);
	}
}