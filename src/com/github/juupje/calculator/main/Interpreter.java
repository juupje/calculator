package com.github.juupje.calculator.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.github.juupje.calculator.commands.Command;
import com.github.juupje.calculator.commands.Commands;
import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.CircularDefinitionException;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MathObject;

public class Interpreter {

	static int position = -1, ch;

	public static void interpret(String s)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException, CircularDefinitionException, ShapeException {
		if (s == null || s.length() == 0)
			return;
		int index = s.indexOf("=");
		if (index != -1 && !Tools.insideBrackets(s, index)) {
			boolean containsOp = "+-*/:".contains("" + s.charAt(index - 1));
			assign(s.substring(0, index - (containsOp ? 1 : 0)), containsOp ? s.substring(index - 1, index) : "",
					s.substring(index + 1));
			return;
		}
		
		Command c = Commands.findCommand(s);
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
		
		Operator operator = null;
		MathObject result = null;
		if (name.contains("(")) {
			result = MFunction.create(name, expr, op.equals(":"));
			name = name.substring(0, name.indexOf("("));
		} else {
			if(!Tools.checkNameValidity(name))
				throw new UnexpectedCharacterException(name + " is not a valid name.");
			if(expr.startsWith("{") || expr.startsWith("r{"))
				result = MSequence.parse(expr, op.equals(":"));
			else {
				switch (op) {
				case ":":
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
					result = new Parser(expr).evaluate();
					break;
				}
				if (operator != null)
					result = operator.evaluate(Variables.get(name), new Parser(expr).evaluate());
			}
		}
		if(result != null) {
			if(result instanceof MExpression || result instanceof MSequence) {
				try {
					result.shape();
				} catch(ShapeException | IllegalArgumentException e) {
					Calculator.errorHandler.handle(e);
					return;
				}
				MathObject old = Variables.get(name);
				Variables.set(name, result);
				if(Calculator.dependencyGraph.isCyclic()) {
					if(old==null)
						Variables.remove(name);
					else
						Variables.set(name, old);
					throw new CircularDefinitionException(result);
				}
			} else
				Variables.set(name, result);
			Variables.ans(result.copy());
			Calculator.ioHandler.out(result.toString());
		}
	}

	public static void execute(File f) throws UnexpectedCharacterException, InvalidFunctionException, TreeException, CircularDefinitionException, ShapeException {
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if(line.startsWith("#"))
					continue;
				Calculator.ioHandler.out(">>> " + line);
				if (line.equals("exit") || line.equals("quit"))
					Calculator.exit();
				interpret(line);
			}
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}
}
