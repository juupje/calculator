package helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.InvalidOperationException;
import helpers.exceptions.UndefinedException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Operator;
import main.Variables;
import mathobjects.MExpression;

public class Interpreter {

	static int position = -1, ch;

	public static void Interpret(String s) {
		int index = s.indexOf("=");
		if (index != -1) {
			boolean containsOp = "+-*/:".contains("" + s.charAt(index - 1));
			assign(s.substring(0, index - (containsOp ? 1 : 0)), containsOp ? s.substring(index - 1, index) : "",
					s.substring(index+1));
		} else if(s.startsWith("type")) {
			String name = s.substring(s.indexOf("(")+1, s.lastIndexOf(")"));
			System.out.println(Variables.get(name).getClass());
		} else if(s.startsWith("latex")) {
			String args = s.substring(s.indexOf("(")+1, s.lastIndexOf(")"));
			Printer.latex(args);
		} else if(s.startsWith("dot")) {
			String args = s.substring(s.indexOf("(")+1, s.lastIndexOf(")"));
			Printer.dot(args);
		} else {
			try {
				System.out.println(new Parser(s).evaluate().toString());
			} catch (UnexpectedCharacterException | InvalidFunctionException | InvalidOperationException | UndefinedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void assign(String name, String op, String expr) {
		try {
			Operator operator = null;
			switch (op) {
			case ":":
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
				Variables.set(name, new Parser(expr).evaluate());
				System.out.println(Variables.get(name).toString());
				break;
			}
			if (operator != null)
				Variables.set(name, operator.evaluate(Variables.get(name), new Parser(expr).evaluate()));
		} catch (UnexpectedCharacterException |InvalidFunctionException | UndefinedException | InvalidOperationException e) {
			e.printStackTrace();
		}
	}
	
	public static void execute(File f) {
		try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				System.out.println(">>> " + line);
				if(line.equals("exit")) System.exit(0);
				Interpret(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
