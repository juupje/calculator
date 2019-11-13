package com.github.juupje.calculator.settings;

public class Setting {

	Class<?> type;
	String name;
	
	public Setting(String name, Class<?> type) {
		this.type = type;
		this.name = name;
	}
		
	public Class<?> getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
}
