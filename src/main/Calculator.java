package main;

import java.io.File;

import graph.Graph;
import helpers.ErrorHandler;
import helpers.IOHandler;
import helpers.Setting;
import helpers.exceptions.CircularDefinitionException;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;

public class Calculator {
	
	public static IOHandler ioHandler;
	public static ErrorHandler errorHandler;
	public static Graph<Variable> dependencyGraph;
	
	public Calculator() {
		ioHandler.startConsoleInput();
	}
	
	public static void setIOHandler(IOHandler hl) {
		ioHandler = hl;
	}
	
	public static void setErrorHandler(ErrorHandler eh) {
		errorHandler = eh;
	}
	
	public static void start(String[] args) {
		dependencyGraph = new Graph<Variable>();
		Setting.loadPrefs();
		if(args != null && args.length > 0) {
			Setting.setArgument(args);
			if(args[0].equals("run"))
				try {
					Interpreter.execute(new File(args[1]));
				} catch (UnexpectedCharacterException | InvalidFunctionException | TreeException | CircularDefinitionException | ShapeException e) {
					errorHandler.handle(e);
				}
		}
	}
	
	public static void main(String[] args) {
		setIOHandler(new IOHandler());
		setErrorHandler(new ErrorHandler());
		start(args);
		new Calculator();
	}
}