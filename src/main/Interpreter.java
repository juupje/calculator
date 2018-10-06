package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import helpers.Printer;
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
			String name = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
			Calculator.ioHandler.out(Variables.get(name).getClass());
		} else if(s.startsWith("time")) {
			String args = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
			Calculator.ioHandler.out(Timer.time(args).toString());
		} else if (s.startsWith("latex")) {
			String args = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
			Printer.latex(args);
		} else if (s.startsWith("dot")) {
			String args = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
			Printer.dot(args);
		} else if (s.startsWith("print")) {
			MathObject mo = Variables.get(s.substring(s.indexOf("(") + 1, s.lastIndexOf(")")));
			if (mo == null)
				Calculator.ioHandler.err("There exists no variable with that name.");
			else
				Calculator.ioHandler.out(Printer.toText(mo));
		} else {
			Calculator.ioHandler.out(new Parser(s).evaluate().toString());
		}
	}

	private static void assign(String name, String op, String expr)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException {
		Operator operator = null;
		switch (op) {
		case ":":
			if (name.contains("("))
				MFunction.createAndStore(name, expr, true);
			else
				Variables.set(name, new MExpression(expr));
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
				MFunction.createAndStore(name, expr, false);
				Calculator.ioHandler.out(Variables.get(name.substring(0, name.indexOf("("))).toString());
			} else {
				Variables.set(name, new Parser(expr).evaluate());
				Calculator.ioHandler.out(Variables.get(name).toString());
			}
			break;
		}
		if (operator != null)
			Variables.set(name, operator.evaluate(Variables.get(name), new Parser(expr).evaluate()));
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
