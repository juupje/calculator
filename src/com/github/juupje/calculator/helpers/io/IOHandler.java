package com.github.juupje.calculator.helpers.io;

import java.util.Scanner;

import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Interpreter;
import com.github.juupje.calculator.settings.Arguments;
import com.github.juupje.calculator.settings.Settings;

public class IOHandler {
	
	protected Scanner scan;
	private boolean reading;
	private boolean enabled = true;
	
	public void disable() {
		enabled = false;
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void err(String str) {
		if(enabled)
			System.err.println(str);
	}
	
	public void out(String str) {
		if(enabled)
			System.out.println(str);
	}
	
	public void out(Object obj) {
		if(enabled)
			out(obj.toString());
	}
	
	public void debug(String str) {
		if(enabled && (Arguments.getBool(Arguments.DEBUG) || Settings.getBool(Settings.DEBUG)))
			out("DEBUG: " + str);
	}
	
	public void outNoLine(String str) {
		if(enabled)
			System.out.print(str);
	}
	
	public void startConsoleInput() {
		reading = true;
		while(reading) {
			outNoLine(">>> ");
			String line = in();
			if(line.equals("quit") || line.equals("exit")) {
				Calculator.exit();
				break;
			}
			try {
				Interpreter.interpret(line);
			} catch (Exception e) {
				Calculator.errorHandler.handle(e);
			}
		}
		close();
	}
	
	public void stopConsoleInput() {
		reading = false;
	}
	
	public String in() {
		if(scan == null)
			scan = new Scanner(System.in);
		return scan.nextLine();
	}
	
	public void close() {
		scan.close();
		scan = null;
	}
	
	public boolean askYesNo(String question) {
		if(ask(question, new String[] {"yes", "y", "1"}, new String[] {"no", "n", "0"}).equals("yes"))
			return true;
		return false;
	}
	
	public String ask(String question, String[]... options) {
		if(options.length == 0) return null;
		String optionString = "";
		for(String[] option : options)
			optionString += option[0] + "/";
		out(question + " [" + optionString.substring(0, optionString.length()-1) + "]");
		String result = in();
		for(String[] option : options)
			for(String s : option)
				if(s.equals(result))
					return option[0];
		return options[0][0];
	}
	
	public static String ansi(String str, ANSITags tag) {
		if(Arguments.getBool(Arguments.ANSI_COLORS)==true)
			return tag.getString() + str + ANSITags.ANSI_RESET.getString();
		return str;
	}
}