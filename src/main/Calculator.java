package main;

import java.io.File;

import graph.Graph;
import helpers.IOHandler;
import helpers.Setting;
import helpers.exceptions.CircularDefinitionException;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;

public class Calculator {
	
	public static IOHandler ioHandler;
	public static Graph<Variable> dependencyGraph;
	
	public Calculator() {
		ioHandler.startConsoleInput();
	}
	
	public static void setHandler(IOHandler hl) {
		ioHandler = hl;
	}
	
	public static void start(String[] args) {
		dependencyGraph = new Graph<Variable>();
		Setting.setArgument(args);
		Setting.loadPrefs();
		if(args != null && args.length > 0) {
			if(args[0].equals("run"))
				try {
					Interpreter.execute(new File(args[1]));
				} catch (UnexpectedCharacterException | InvalidFunctionException | TreeException | CircularDefinitionException | ShapeException e) {
					if(Setting.getBool(Setting.SHOW_STACKTRACE))
						e.printStackTrace();
					else
						ioHandler.err(e.getMessage());
				}
		}
	}
	
	public static void main(String[] args) {
		setHandler(new IOHandler());
		start(args);
		new Calculator();
	}
}