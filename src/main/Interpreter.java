package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import helpers.Printer;
import helpers.Setting;
import helpers.Timer;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import mathobjects.MExpression;
import mathobjects.MFunction;
import mathobjects.MathObject;

public class Interpreter {

	static int position = -1, ch;

	public static void Interpret(String s)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException {
		if (s == null || s.length() == 0)
			return;
		int index = s.indexOf("=");
		if (index != -1) {
			boolean containsOp = "+-*/:".contains("" + s.charAt(index - 1));
			assign(s.substring(0, index - (containsOp ? 1 : 0)), containsOp ? s.substring(index - 1, index) : "",
					s.substring(index + 1));
		} else if (s.startsWith("type")) {
			Calculator.ioHandler.out(Variables.get(argsFromString(s)).getClass());
		} else if(s.startsWith("time")) {
			Calculator.ioHandler.out(Timer.time(argsFromString(s)).toString());
		} else if (s.startsWith("latex")) {
			Printer.latex(argsFromString(s));
		} else if (s.startsWith("dot")) {
			Printer.dot(argsFromString(s));
		} else if (s.startsWith("execute")) {
			execute(new File(argsFromString(s)));
		} else if (s.startsWith("delete") || s.startsWith("del")) {
			for(String name : Parser.getArguments(argsFromString(s)))
				Variables.remove(name);
		} else if (s.startsWith("print")) {
			MathObject mo = Variables.get(argsFromString(s));
			if (mo == null)
				Calculator.ioHandler.err("There exists no variable with that name.");
			else
				Calculator.ioHandler.out(Printer.toText(mo));
		} else if (s.startsWith("setting")){
			Setting.processCommand(argsFromString(s));
		} else {
			MathObject result = new Parser(s).evaluate();
			Variables.ans(result);
			Calculator.ioHandler.out(result.toString());
		}
	}
	
	private static String argsFromString(String s) {
		return s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
	}

	private static void assign(String name, String op, String expr)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException {
		Operator operator = null;
		MathObject result = null;
		switch (op) {
		case ":":
			if (name.contains("("))
				result = MFunction.create(name, expr, true);
			else
				result = new MExpression(expr);
			break;
		case "+":
			operator = Operator.ADD;
			break;
		case "-":
			operator = Operator.SUBTRACT;
			break;
		case "*":
			operator = Operator.MULTIPLY;
			break;
		case "/":
			operator = Operator.DIVIDE;
			break;
		default:
			if (name.contains("(")) {
				result = MFunction.create(name, expr, false);
				name = name.substring(0, name.indexOf("("));
			} else {
				result = new Parser(expr).evaluate();
			}
			break;
		}
		if (operator != null)
			result = operator.evaluate(Variables.get(name), new Parser(expr).evaluate());
		
		if(result != null) {
			Variables.set(name, result);
			Variables.ans(result);
			Calculator.ioHandler.out(result.toString());
		}
			
	}

	public static void execute(File f) throws UnexpectedCharacterException, InvalidFunctionException, TreeException {
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				Calculator.ioHandler.out(">>> " + line);
				if (line.equals("exit"))
					System.exit(0);
				Interpret(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
