package com.github.juupje.calculator.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.github.juupje.calculator.commands.Command;
import com.github.juupje.calculator.commands.Commands;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.CircularDefinitionException;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.printer.TextPrinter;
import com.github.juupje.calculator.settings.Arguments;

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
			if(result != null) {
				Variables.ans(result.copy());
				if(!Arguments.getBool(Arguments.SILENT_ANSWERS))
					Calculator.ioHandler.out(TextPrinter.toText(result));
			}
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
		
		int brIndex = name.indexOf('[');
		//check if there is an index appended to the name.
		if(brIndex != -1 && !Tools.insideBrackets(name, brIndex)) {
			assignElement(name.substring(0,brIndex), extractIndex(name, brIndex), op, expr);
		//if there are brackets in the name, it has to be a function
		} else if (name.contains("(")) {
			result = MFunction.create(name, expr, op.equals(":"));
			name = name.substring(0, name.indexOf("("));
		//its not a function, so just parse the expression behind the equals sign
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
				if (operator != null) {
					//check for index
					result = operator.evaluate(Variables.get(name), new Parser(expr).evaluate());
				}
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
				Variables.set(name, result);
			} else
				Variables.set(name, result);
			if(!Arguments.getBool(Arguments.SILENT_ANSWERS))
				Calculator.ioHandler.out(result.toString());
			Variables.ans(result.copy());
		}
	}
	
	/**
	 * Extracts an index slice from the name of an expression
	 * @param name the name of the expression (the part before the =-sign)
	 * @param brIndex the starting point of the index in the name string
	 * @return an 2D int array, the first row containing the slice of the 
	 * first dimension and the second row the slice of the second dimension (if present).
	 */
	static int[][] extractIndex(String name, int brIndex) {
		//find the closing bracket
		int brEnd = Tools.findEndOfBrackets(name, brIndex);
		String indexStr = name.substring(brIndex+1,brEnd);
		String[] parts = Parser.getArguments(indexStr);
		if(parts.length==1) {
			return new int[][] {extractRange(indexStr),{}};
		} else if(parts.length==2) {
			return new int[][] {extractRange(parts[0]), extractRange(parts[1])};
		} else
			throw new IndexException("Found " + parts.length + " indices, expected 1 or 2.");
	}
	
	private static void assignElement(String name, int[][] index, String op, String expr) {
		if(!Variables.exists(name))
			throw new UnexpectedCharacterException("Variable '" + name +"' unknown.");
		MathObject mo = Variables.get(name);
		Shape shape = indicesToShape(index, mo.shape());
		Calculator.ioHandler.debug("Assigning shape: " + shape);
		Operator oper = operatorFromString(op);
		if(mo instanceof MVector) {
			MVector v = (MVector) mo;
			MathObject obj = new Parser(expr).evaluate();
			if(!obj.shape().equals(shape))
				throw new ShapeException("Shape " + shape + " cannot be used to set object of shape " + mo.shape());
			if(shape.dim()==0) {
				if(oper == null)
					v.set(index[0][0], obj);
				else
					v.set(index[0][0], oper.evaluate(v.get(index[0][0]), obj));
			} else {
				MVector v2 = (MVector) obj;
				if(oper == null) {
					for(int i = index[0][0]; i < index[0][1]; i++)
						v.set(i, v2.get(i-index[0][0]));
				} else {
					for(int i = index[0][0]; i < index[0][1]; i++)
						v.set(i, oper.evaluate(v.get(i), v2.get(i-index[0][0])));
				}
			}
		} else if(mo instanceof MMatrix) {
			MMatrix m = (MMatrix) mo;
			MathObject obj = new Parser(expr).evaluate();
			if(!obj.shape().equals(shape))
				throw new ShapeException("Shape " + obj.shape() + " cannot be used to set object of shape " + shape);
			if(shape.dim()==0) {
				if(oper==null)
					m.set(index[0][0], index[1][0], obj);
				else
					m.set(index[0][0], index[1][0], oper.evaluate(m.get(index[0][0], index[1][0]), obj));
			} else if(shape.dim()==1) {
				int a = index[0][0];
				int b = index[0].length==1 ? a+1 : index[0][1];
				int c = index[1][0];
				int d = index[1].length==1 ? c+1 : index[1][1];
				int k = 0;
				MVector v = (MVector) obj;
				for(int i = a; i < b; i++)
					for(int j = c; j < d; j++)
						m.set(i, j, oper == null ? v.get(k++) : oper.evaluate(m.get(i, j), v.get(k++)));
			} else {
				int k = 0;
				int l = 0;
				MMatrix m2 = (MMatrix) obj;
				for(int i = index[0][0]; i < index[0][1]; i++) {
					for(int j = index[1][0]; j < index[1][1]; j++) {
						m.set(i, j, oper == null ? m2.get(k,l) : oper.evaluate(m.get(i, j), m2.get(k,l)));
						l++;
					}
					k++; l=0;
				}
			}
		} else
			throw new IndexException(name + " has no indexed elements.");
	}
	
	private static Operator operatorFromString(String op) {
		switch (op) {
		case "+":
			return Operator.ADD;
		case "-":
			return Operator.SUBTRACT;
		case "*":
			return Operator.MULTIPLY;
		case "/":
			return Operator.DIVIDE;
		default:
			return null;
		}
	}
	
	private static Shape indicesToShape(int[][] indices, Shape shape) {
		//possible arrays: [[x],[]], [[xmin, xmax],[]], [[x],[y]], [[xmin,xmax],[y]]
		//[[xmin,xmax],[ymin,ymax]], [[x],[ymin,ymax]]
		if(shape.dim()==0)
			throw new IndexException("Object not indexable");
		if(shape.dim()==1) {
			if(indices[1].length != 0)
				throw new IndexException("Got 2 indices for object with dimension 1");
			if(indices[0].length==1) {
				if(indices[0][0] > shape.get(0))
					throw new IndexException("Index " + indices[0][0] + " in dimension 1 out of bounds");
				return new Shape();
			} else {
				if(indices[0][1] > shape.get(0) || indices[0][0] > shape.get(0))
					throw new IndexException("Slice in dimension 1 out of bounds");
				if(indices[0][1]==0) indices[0][1] = shape.get(0);
				return new Shape(indices[0][1]-indices[0][0]);
			}	
		}
		if(shape.dim()==2) {
			if(indices[1].length==0) //[[...],[]]
				throw new IndexException("Got 1 index for object with dimension 2");
			if(indices[0].length==1) { //[[a],[...]]
				if(indices[0][0] > shape.get(0)) // a out of bounds
					throw new IndexException("Index " + indices[0][0] + " in dimension 1 out of bounds");
				if(indices[1].length==1) { //[[a],[b]]
					if(indices[1][0] > shape.get(1)) //b out of bounds
						throw new IndexException("Index " + indices[1][0] + " in dimension 2 out of bounds");
					return new Shape();
				} else { //[[a],[b,c]]
					if(indices[1][1] > shape.get(1)) //c out of bounds (we needn't test b: b<c)
						throw new IndexException("Slice in dimension 2 out of bounds");
					if(indices[1][1] == 0) indices[1][1] = shape.get(1);
					return new Shape(indices[1][1]-indices[1][0]);
				}
			} else if(indices[1].length==1) { //[[a,b],[c]]
				if(indices[0][1] > shape.get(0))
					throw new IndexException("Slice in dimension 1 out of bounds");
				if(indices[1][0] > shape.get(1))
					throw new IndexException("Index " + indices[1][0] + " in dimension 2 out of bounds");
				if(indices[0][1]==0) indices[0][1] = shape.get(0);
				return new Shape(indices[0][1]-indices[0][0]);
			} else {//[[a,b],[c,d]]
				if(indices[0][1] > shape.get(0))
					throw new IndexException("Slice in dimension 1 out of bounds");
				if(indices[1][1] > shape.get(1))
					throw new IndexException("Slice in dimension 2 out of bounds");
				if(indices[0][1]==0) indices[0][1] = shape.get(0);
				if(indices[1][1]==0) indices[1][1] = shape.get(1);
				return new Shape(indices[0][1]-indices[0][0], indices[1][1]-indices[1][0]);
			}		
		}
		return null;
	}
	
	/**
	 * Extracts a index range from a string. The string have the form {@code start:end}.
	 * If the start of the range is not given, 0 will be assumed.
	 * If the end of the range is not given, it will be set to -1, representing no end
	 * @param s the string to be parsed
	 * @return an integer array of length two {@code {start, end}}.
	 * @throws IndexException, if either start or end is negative or end<start.
	 */
	private static int[] extractRange(String s) {
		if(s.matches("-?\\d*:-?\\d*")) {
			int index = s.indexOf(":");
			int begin = index == 0 ? 0 : Tools.extractInteger(s.substring(0,index));
			int end = index == s.length()-1 ? 0 : Tools.extractInteger(s.substring(index+1));
			if(begin<0 || end < 0)
				throw new IndexException("Negative index");
			if(begin==end && end != 0)
				throw new IndexException("Slice begin cannot equal slice end");
			return new int[] {begin,end};
		}
		int index = Tools.extractInteger(s);
		if(index<0)
			throw new IndexException("Negative index");
		return new int[] {index};
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
