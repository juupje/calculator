package main;

import java.io.File;

import helpers.IOHandler;
import helpers.Setting;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;

public class Calculator {
	
	public static IOHandler ioHandler;
	
	public Calculator() {
		ioHandler.startConsoleInput();
	}
	
	public static void setHandler(IOHandler hl) {
		ioHandler = hl;
	}
	
	public static void main(String[] args) {
		setHandler(new IOHandler());
		Setting.loadPrefs();
		if(args != null && args.length > 0) {
			if(args[0].equals("run"))
				try {
					Interpreter.execute(new File(args[1]));
				} catch (UnexpectedCharacterException | InvalidFunctionException | TreeException e) {
					e.printStackTrace();
				}
		}
		new Calculator();
	}
}
