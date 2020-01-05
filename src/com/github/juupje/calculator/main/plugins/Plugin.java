package com.github.juupje.calculator.main.plugins;

import org.json.JSONObject;

public interface Plugin {
	public String getName();
	public void run();
	public void exit();
	public int version();
	public JSONObject initHelp();
}