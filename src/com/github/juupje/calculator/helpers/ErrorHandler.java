package com.github.juupje.calculator.helpers;

import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.settings.Settings;

public class ErrorHandler {
	public void handle(Exception e) {
		if(Settings.getBool(Settings.SHOW_STACKTRACE)) {
			e.printStackTrace();
		} else
			Calculator.ioHandler.err("Error: " + e.getMessage());
	}

	public void handle(String string, Exception e) {
		Calculator.ioHandler.err("Error: " + string);
		if(Settings.getBool(Settings.SHOW_STACKTRACE)) {
			e.printStackTrace();
		}
	}
}
