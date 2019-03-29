package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import main.Calculator;

public class Helper {
	static JSONObject json = null;

	private static void init() {
		try {
			File file = new File(Calculator.class.getResource("/files/help.json").toURI());
			FileInputStream in = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			in.read(data);
			in.close();
			String s = new String(data, "UTF-8");
			json = new JSONObject(s);
		} catch (IOException | URISyntaxException e) {
			if (Setting.getBool(Setting.SHOW_STACKTRACE))
				e.printStackTrace();
			else
				Calculator.ioHandler.err(e.getMessage());
		}
	}

	public static void printHelp(String command) {
		if (json == null)
			init();
		JSONObject jobj = null;
		String type = "";
		try {
			jobj = json.getJSONObject("commands").getJSONObject(command);
			type = "command";
		} catch (JSONException e) {
			try {
				jobj = json.getJSONObject("functions").getJSONObject(command);
				type = "function";
			} catch (JSONException e2) {
				try {
					jobj = json.getJSONObject("algorithms").getJSONObject(command);
					type = "algorithm";
				} catch(JSONException e3) {
					try {
						jobj = json.getJSONObject("settings").getJSONObject(command);
						type = "setting";
					} catch(JSONException e4) {
						Calculator.ioHandler.err("No known help page for " + command + ", type list for a list of commands/functions/algorithms");
						return;
					}
				}
			}
		}
		String help = "Help for " + command + " (" + type + "):\n";
		if(type.equals("setting"))
			help += "\tName: " + jobj.getString("name")
					+ "\n\tType: " + jobj.getString("type")
					+ "\n\tDescription: " + jobj.getString("description")
					+ "\n\tDefault: " + jobj.get("default");
		else
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
		if(args.equals(""))
			list = "Algorithms:\n\t" + Tools.join("\n\t", json.getJSONObject("algorithms").keySet())
					+ "\n\nFunctions:\n\t" + Tools.join("\n\t", json.getJSONObject("functions").keySet()) 
					+ "\n\nCommands:\n\t" + Tools.join("\n\t", json.getJSONObject("commands").keySet())
					+ "\n\nSettings:\n\t" + Tools.join("\n\t", json.getJSONObject("settings").keySet());
		else
			list = args + ":\n\t" + Tools.join("\n\t", json.getJSONObject(args).keySet());
		Calculator.ioHandler.out(list);
	}
}