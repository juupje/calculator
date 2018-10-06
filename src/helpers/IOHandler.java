package helpers;

import java.util.Scanner;

import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Interpreter;

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
	
	public void outNoLine(String str) {
		if(enabled)
			System.out.print(str);
	}
	
	public void startConsoleInput() {
		reading = true;
		while(reading) {
			outNoLine(">>> ");
			String line = in();
			if(line.equals("quit") || line.equals("exit")) break;
			try {
				Interpreter.Interpret(line);
			} catch (Exception e) {
				e.printStackTrace();
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
}
