package com.github.juupje.calculator.helpers.exceptions;

public class PluginException extends RuntimeException {
	private static final long serialVersionUID = -6711004550271986717L;
	
	public PluginException() {
		super();
	}
	
	public PluginException(String s) {
		super(s);
	}
}
