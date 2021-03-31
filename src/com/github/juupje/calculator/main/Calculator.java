package com.github.juupje.calculator.main;

import java.io.File;

import com.github.juupje.calculator.graph.Graph;
import com.github.juupje.calculator.helpers.ErrorHandler;
import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.exceptions.CircularDefinitionException;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.helpers.io.IOHandler;
import com.github.juupje.calculator.main.plugins.PluginLoader;
import com.github.juupje.calculator.settings.Arguments;
import com.github.juupje.calculator.settings.Settings;
import com.github.juupje.calculator.settings.SettingsHandler;

public class Calculator {
	
	public static IOHandler ioHandler;
	public static ErrorHandler errorHandler;
	public static SettingsHandler settingsHandler;
	public static Graph<Variable> dependencyGraph;
	
	public static boolean running = false;
	
	public Calculator() {
		ioHandler.startConsoleInput();
	}
	
	public static void setIOHandler(IOHandler hl) {
		ioHandler = hl;
	}
	
	public static void setErrorHandler(ErrorHandler eh) {
		errorHandler = eh;
	}
	
	public static void setSettingsHandler(SettingsHandler sh) {
		settingsHandler = sh;
	}
	
	public static void parseArgs(String[] args) {
		Arguments.parse(args);
	}
	
	public static void start() {
		dependencyGraph = new Graph<Variable>();
		Settings.loadPrefs();
		running = true;
		if(Arguments.exists(Arguments.RUN))
			try {
				Interpreter.execute(new File(Printer.getDefaultPath() + Arguments.get(Arguments.RUN)));
			} catch (UnexpectedCharacterException | InvalidFunctionException | TreeException | CircularDefinitionException | ShapeException e) {
				errorHandler.handle(e);
			}
	}
	
	public static boolean isRunning() {
		return running;
	}
	
	public static void finish() {
		running = false;
		PluginLoader.exit();		
	}
	
	public static void exit() {
		finish();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		setIOHandler(new IOHandler());
		setErrorHandler(new ErrorHandler());
		setSettingsHandler(new SettingsHandler());
		parseArgs(args);
		if(Arguments.exists(Arguments.PLUGIN_DIR))
			PluginLoader.load(new File(Arguments.get(Arguments.PLUGIN_DIR)));
		start();
		new Calculator();
	}
}