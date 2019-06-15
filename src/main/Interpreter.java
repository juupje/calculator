package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import algorithms.Algorithms;
import algorithms.Functions;
import helpers.Printer;
import helpers.exceptions.CircularDefinitionException;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import mathobjects.MExpression;
import mathobjects.MFunction;
import mathobjects.MathObject;

public class Interpreter {

	static int position = -1, ch;

	public static void Interpret(String s)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException, CircularDefinitionException, ShapeException {
		if (s == null || s.length() == 0)
			return;
		int index = s.indexOf("=");
		if (index != -1) {
			boolean containsOp = "+-*/:".contains("" + s.charAt(index - 1));
			assign(s.substring(0, index - (containsOp ? 1 : 0)), containsOp ? s.substring(index - 1, index) : "",
					s.substring(index + 1));
			return;
		}
		Command c = Command.findCommand(s);
		if(c!=null) {
			c.process(argsFromString(s));
		} else {
			MathObject result = new Parser(s).evaluate();
			Variables.ans(result.copy());
			Calculator.ioHandler.out(Printer.toText(result));
		}
	}
	
	private static String argsFromString(String s) {
		return s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
	}

	private static void assign(String name, String op, String expr)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException, CircularDefinitionException, ShapeException {
		name = name.trim();
		expr = expr.trim();
		if(!checkNameValidity(name))
			throw new UnexpectedCharacterException(name + " is not a valid name.");
		Operator operator = null;
		MathObject result = null;
		switch (op) {
		case ":":
			if (name.contains("(")) {
				result = MFunction.create(name, expr, true);
				name = name.substring(0, name.indexOf("("));
			} else
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
			if(result instanceof MExpression) {
				MathObject old = Variables.get(name);
				Variables.set(name, result);
				if(Calculator.dependencyGraph.isCyclic()) {
					Variables.set(name, old);
					throw new CircularDefinitionException(result);
				}
				try {
					result.shape();
				} catch(ShapeException | IllegalArgumentException e) {
					Variables.set(name, old);
					Calculator.errorHandler.handle(e);
				}
			} else
				Variables.set(name, result);
			Variables.ans(result.copy());
			Calculator.ioHandler.out(result.toString());
		}
	}
	
	private static boolean checkNameValidity(String name) {
		if(name.startsWith("_") || Character.isDigit(name.codePointAt(0))) return false;
		if(name.equals("pi") || name.equals("e") || name.equals("i") || Functions.isFunction(name) || Algorithms.isAlgorithm(name)) return false;
		for(int i = 0; i < name.length(); i++) {
			int c = name.codePointAt(i);
			if(c<65 && c>90 && c<97 && c>120 && c<945 && c>969 && c<913 && c>937 && c<30 && c<48 && c>57)
				return false;
		}
		return true;
	}

	public static void execute(File f) throws UnexpectedCharacterException, InvalidFunctionException, TreeException, CircularDefinitionException, ShapeException {
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				Calculator.ioHandler.out(">>> " + line);
				if (line.equals("exit"))
					System.exit(0);
				Interpret(line);
			}
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}
}