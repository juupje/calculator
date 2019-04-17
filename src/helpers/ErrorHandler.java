package helpers;

import main.Calculator;

public class ErrorHandler {
	public void handle(Exception e) {
		if(Setting.getBool(Setting.SHOW_STACKTRACE)) {
			e.printStackTrace();
		} else
			Calculator.ioHandler.err("Error: " + e.getMessage());
	}

	public void handle(String string, Exception e) {
		Calculator.ioHandler.err("Error: " + string);
		if(Setting.getBool(Setting.SHOW_STACKTRACE)) {
			e.printStackTrace();
		}
	}
}
