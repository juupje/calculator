package com.github.juupje.calculator.main;

import static com.github.juupje.calculator.main.Operator.*;

import java.util.ArrayList;
import java.util.Map;

import com.github.juupje.calculator.algorithms.Algorithms;
import com.github.juupje.calculator.algorithms.Functions;
import com.github.juupje.calculator.algorithms.Functions.Function;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFraction;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MIndexable;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MVectorFunction;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.printer.TextPrinter;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class Parser {

	int pos = -1, ch = -1;
	String expr;
	Tree tree;
	Node<?> p;
	Map<String, Class<? extends MathObject>> extraVariables;
	
	public Parser(String s) {
		expr = s;
	}
	
	/**
	 * Initializes a new parser based on a given string representing the expression and a map containing the names 
	 * of variables and their types which are treated the same as variables stored in the Variables class.
	 * @param s the expression
	 * @param extraVariables a map of variable names and types which are treated as if they were stored in Variables.
	 * Note that this only applies to expression parses with the {@link #getTree()} method.
	 */
	public Parser(String s, Map<String, Class<? extends MathObject>> extraVariables) {
		expr = s;
		this.extraVariables = extraVariables;
	}

	/**
	 * Sets the character to the next character in the expression string (or -1 if
	 * there is no next character) and increases <code>position</code> by 1.
	 */
	protected int nextChar() {
		if (++pos < expr.length())
			ch = expr.charAt(pos);
		else
			ch = -1;
		return ch;
	}
	
	protected int getPrevChar() {
		return expr.charAt(pos-1);
	}

	/**
	 * Compares the given character with the current character. The current
	 * character is consumed if it's the same.
	 * 
	 * @param c
	 *            The character to compare the current character to.
	 * @return <code>true</code> if the characters match, otherwise
	 *         <code>false</code>.
	 */
	protected boolean consume(int c) {
		while (ch == ' ')
			nextChar();
		if (ch == c) {
			nextChar(); // Consume character by moving the position pointer on to the next one.
			return true;
		} else
			return false;
	}

	// Method to process string to number value
	public Tree getTree() throws UnexpectedCharacterException {
		if(expr.length()==0)
			throw new UnexpectedCharacterException("Got empty expression");
		tree = new Tree();
		nextChar();
		tree.root = p = getFactor();
		while (ch > 0 && p != null)
			p = getTerm();
		tree.updateRoot();
		return tree;
	}

	private Node<?> getSubTree() throws UnexpectedCharacterException {
		//save variables into temporary ones.
		Tree oldTree = tree;
		Node<?> oldP = p;
		tree = new Tree();
		nextChar();
		tree.root = p = getFactor();
		while (ch > 0 && p != null)
			p = getTerm();
		Node<?> root = tree.root;
		//Restore temp variables.
		tree = oldTree;
		p = oldP;
		return root;
	}

	/**
	 * Processes the whole expression.
	 * 
	 * @return the arithmetic solution of the expression.
	 * @throws UnexpectedCharacterException
	 *             if the expression contains an unknown or unexpected character
	 * @throws InvalidFunctionException
	 *             if the expression contains an unknown function.
	 * @throws TreeException 
	 * @ 
	 */
	public MathObject evaluate() throws UnexpectedCharacterException, InvalidFunctionException, TreeException, ShapeException {
		if(expr.length()==0)
			throw new UnexpectedCharacterException("Got empty expression");
		nextChar();
		MathObject val = processExpression();
		if (pos < expr.length())
			throw new UnexpectedCharacterException(expr, pos);
		return val;
	}

	// ################ Parse expression into a tree ################
	/**
	 * Parses the following expression as a vector or matrix object. Vectors have
	 * these forms:
	 * <ul>
	 * <li>[1,2,3] -> vector with elements 1,2,3</li>
	 * <li>[[1,2,3],[4,5,6]] vector of two vectors with elements 1,2,3 and 4,5,6
	 * respectively</li>
	 * <li>[1,2,3;4,5,6] 2D matrix with elements 1,2,3 in row 1 and elements 4,5,6
	 * in row 2</li>
	 * </ul>
	 * @throws UnexpectedCharacterException 
	 */
	protected MathObject getVector(boolean defined) throws UnexpectedCharacterException {
		int count = 1;
		int position = pos;
		while(count != 0 && ch > 0) {
			if(ch == '[') count++;
			if(ch == ']') count--;
			nextChar();
		}
		return new VectorParser(expr.substring(position, pos-1), extraVariables).parse(defined);
	}
	

	protected MVector getParameters() throws UnexpectedCharacterException {
		int count = 1;
		int position = pos;
		while(count != 0 && ch > 0) {
			if(ch == '(') count++;
			if(ch == ')') count--;
			nextChar();
		}
		try {
			return (MVector) new VectorParser(expr.substring(position, pos-1), extraVariables).parse(false);
		} catch(ClassCastException e) {
			throw new UnexpectedCharacterException("Unexpected ';' in parameters");
		}
	}
	
	protected MIndexedObject toIndexedObject(MVector v, MVector shape) {
		int[] dims = new int[shape.size()];
		for(int i = 0; i < dims.length; i++) {
			if(shape.get(i) instanceof MReal && ((MReal) shape.get(i)).isPosInteger())
				dims[i] = (int) ((MReal) shape.get(i)).getValue();
			else
				throw new ShapeException("Unexpected value in shape argument " + TextPrinter.toText(shape));
		}
		return new MIndexedObject(new Shape(dims), v.elements());
	}
	
	@SuppressWarnings("unchecked")
	private Node<?> getFactor() throws UnexpectedCharacterException {
		Node<?> n = null;
		int position = pos;
		if (ch == '(')
			n =  getSubTree();
		else if (ch == ')')
			return null;
		else if (ch == '[') {
			consume('[');
			MathObject v = getVector(true);
			if(v instanceof MVector && !(v instanceof MVectorFunction) && consume('(')) {
				v = toIndexedObject((MVector)v, getParameters());
			}
			n = new Node<MathObject>(v);
		} else if (ch == ']')
			return null;
		else if(consume('&')) {
			n = new Node<Operator>(CONJUGATE);
			n.left(getFactor());
		}
		else if (Character.isDigit(ch) || ch == '.' || ch == '-') {
			do {
				nextChar();
			} while (Character.isDigit(ch) || ch == '.' || ch=='E' || (ch=='-' && getPrevChar()=='E'));
			String s1 = expr.substring(position, pos);
			if (s1.equals("-")) {
				if (p == null)
					tree.root = p = new Node<Operator>(MULTIPLY);
				else
					p = new Node<Operator>(MULTIPLY);
				p.left(n = new Node<MScalar>(new MReal(-1.0)));
				p.right(getFactor());
				n = p;
				//return p.right();
			} else
				n = new Node<MScalar>(new MReal(Double.valueOf(s1)));
			if(consume('i'))
				((Node<MScalar>) n).setData(new MComplex(0,((MReal) n.getData()).getValue()));
		} else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z' || ch=='_')) {
			while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch=='_' || (ch>='0' && ch<='9'))
				nextChar();
			String str = expr.substring(position, pos);
			if (MConst.isConstant(str))
				n = new Node<MConst>(MConst.get(str));
			else if (Functions.isFunction(str)) {
				n = new Node<Function>(Functions.getFunction(str));
				n.left(getFactor());
			} else if(str.equals("inf")) {
				n = new Node<MReal>(new MReal(Double.POSITIVE_INFINITY));
			} else {
				boolean isExtra = (extraVariables != null && extraVariables.containsKey(str));
				if (!Variables.exists(str) && !isExtra)
					Calculator.ioHandler.err("The variable " + str + " is undefined. Statement was parsed regardless.");
				Variable v = new Variable(str);
				n = new Node<Variable>(v);
				if((!isExtra && v.get() instanceof MFunction) || (isExtra && MFunction.class.isAssignableFrom(extraVariables.get(str))))
					if(consume('(')) 
						n.left(new Node<MVector>(getParameters()));
				if(consume('[')) {
					MathObject mo = getVector(true);
					if(!(mo instanceof MVector)) throw new UnexpectedCharacterException(expr, expr.indexOf(";", pos));
					MVector vec = (MVector) mo;
					Node<?> m = n;
					n = new Node<Operator>(ELEMENT);
					n.left(m);
					if(vec.size() == 1) {
						if(vec.get(0) instanceof MExpression && !(vec.get(0) instanceof MFunction))
							n.right(((MExpression) vec.get(0)).getTree().getRoot());
						else if(vec.get(0) instanceof MReal)
							n.right(new Node<MReal>((MReal) vec.get(0)));
						else
							throw new UnexpectedCharacterException("Expected expression or real number as index, got " + vec.get(0));
					} else
						n.right(new Node<MVector>(vec));
				}
			}
		} else if(consume('i'))
			n = new Node<MConst>(MConst.i);
		else
			throw new UnexpectedCharacterException(expr, pos);
		
		// Operators which work immediately on this factor (e.q. they don't connect terms)
		if (consume('^')) {
			Node<Operator> op = new Node<>(Operator.POWER);
			op.left(n);
			op.right(getFactor());
			n = op;
		} else if (consume('%')) {
			Node<Operator> op = new Node<>(Operator.MOD);
			op.left(n);
			op.right(getFactor());
			n = op;
		} else if(consume('\'')) {
			Node<Operator> op = new Node<>(Operator.TRANSPOSE);
			op.left(n);
			n = op;
		}
		return n;
	}

	private Node<?> getTerm() throws UnexpectedCharacterException {
		Node<?> n;
		if (consume(')'))
			return null;
		else if (consume('+')) {
			n = new Node<Operator>(ADD);
			n.left(tree.root);
			n.right(getFactor());
			tree.root = n;
			return n.right();
		} else if (consume('-')) {
			n = new Node<Operator>(SUBTRACT);
			n.left(tree.root);
			n.right(getFactor());
			tree.root = n;
			return n.right();
		} else if (consume('*')) {
			tree.insert(p, new Node<Operator>(MULTIPLY), Node.LEFT);
			p = p.parent;
			p.right(getFactor());
			return p;
		} else if (consume('\u00D7') || consume('~')) {
			tree.insert(p, new Node<Operator>(CROSS), Node.LEFT);
			p = p.parent;
			p.right(getFactor());
			return p.right();
		} else if (consume('/')) {
			tree.insert(p, new Node<Operator>(DIVIDE), Node.LEFT);
			p = p.parent;
			p.right(getFactor());
			return p;
		} else if(consume('\\')) {
			tree.insert(p, new Node<Operator>(SOLVE), Node.LEFT);
			p = p.parent;
			p.right(getFactor());
		/*} else if (consume('^')) {
			tree.insert(p, new Node<Operator>(POWER), Node.LEFT);
			p = p.parent;
			p.right(getFactor());
			return p;
		} else if(consume('\'')) {
			tree.insert(p, new Node<Operator>(TRANSPOSE), Node.LEFT);
			p = p.parent;
			return p; */
		} else if (!Character.isDigit(ch)) { // Does the same as consume('*')
			tree.insert(p, new Node<Operator>(MULTIPLY), Node.LEFT);
			p = p.parent;
			p.right(getFactor());
			return p;
		} else if (consume('(')) {
			return getSubTree();
		}
		throw new UnexpectedCharacterException(expr, pos);
	}

	// ################ Evaluate expression to numeric value ################
	// expression = term. or expression '+' term, or expression '-' term
	private MathObject processExpression() throws InvalidFunctionException, UnexpectedCharacterException, TreeException, ShapeException {
		MathObject d = processTerm();
		while (true) {
			if (consume('+'))
				d = ADD.evaluate(d, processTerm());
			else if (consume('-'))
				d = SUBTRACT.evaluate(d, processTerm());
			else
				return d;
		}
	}

	// term = factor, or term '*' factor, or term '/' factor.
	private MathObject processTerm() throws InvalidFunctionException, UnexpectedCharacterException, TreeException, ShapeException {
		MathObject d = processFactor();
		while (true) {
			if (consume('*'))
				d = MULTIPLY.evaluate(d, processFactor());
			else if (consume('\u00d7') || consume('~'))
				d = CROSS.evaluate(d, processFactor());
			else if (consume('/'))
				d = DIVIDE.evaluate(d, processFactor());
			else if(consume('\\'))
				d = SOLVE.evaluate(d, processFactor());
			else if (pos < expr.length() && Character.isLetter(ch))
				d = MULTIPLY.evaluate(d, processFactor());
			else if (consume('[')) {
				// we want to get an element of the current object
				d = ELEMENT.evaluate(d, toSliceObject(Interpreter.extractIndex(expr,pos-1), d.shape()));
				findEndOfBrackets();
			}
			else
				return d;
		}
	}

	// factor = '+' factor (positive) or '-' factor (negative), or '('
	// expression ')', or number (double), or factor^factor, or function(factor)
	// (e.g. sine, cosine, etc.)
	private MathObject processFactor() throws InvalidFunctionException, UnexpectedCharacterException, TreeException, ShapeException {
		if (consume('+'))
			return processFactor();
		if (consume('-'))
			return NEGATE.evaluate(processFactor());
		if(consume('&'))
			return CONJUGATE.evaluate(processFactor());

		MathObject d = null;

		if (consume('(')) { // new expression within the parentheses
			d = processExpression();
			consume(')');
		} else if(consume('[')) {
			d = getVector(false); //no need to evaluate, as the parameter false ensures that no expressions are in the vector
			if(d instanceof MVector && consume('(')) {
				d = toIndexedObject((MVector)d, getParameters());
			}
		} else if (consume('|')) {
			d = Functions.Function.ABS.evaluate(processExpression());
			consume('|');
		} else if ((ch >= '0' && ch <= '9') || ch == '.') {// number
			int p = pos;
			while ((ch >= '0' && ch <= '9') || ch == '.')
				nextChar();
			d = new MReal(Double.parseDouble(expr.substring(p, pos)));
			if(pos+2<expr.length() && expr.substring(pos, pos+2).equals("//")) {
				nextChar();nextChar();
				MathObject b = processFactor();
				if(b instanceof MReal)
					d = MFraction.create((MReal) d, (MReal) b);
				else
					throw new UnexpectedCharacterException("Can't create a fraction from " + d + " and " + b);
			}
			if (ch == 'E' || ch=='e') {
				p = pos;
				do {
					nextChar();
				} while(ch>='0' && ch<= '9' || ch=='-' || ch=='+');
				try {					
					d = MULTIPLY.evaluate(d, new MReal(Math.pow(10, Integer.parseInt(expr.substring(p+1,pos)))));
				} catch(NumberFormatException e) {
					if(expr.charAt(p) == 'E')
						throw new UnexpectedCharacterException(expr, p,pos);
					pos = p;
					ch = expr.charAt(pos);
				}
			}
			if(consume('i')) {
				if(((MScalar) d).isComplex())
					((MScalar) d).multiply(MConst.i.evaluate());
				else
					d = new MComplex(0, ((MReal) d).getValue());
			}
			// Letter, which is part of a variable or function
		} else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
			int p = pos;
			while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_' || ch >= '0' && ch <= '9')
				nextChar();
			String letters = expr.substring(p, pos);
			if (Variables.exists(letters)) {
				d = Variables.get(letters);
				if(d instanceof MFunction) {
					if(consume('('))
						d = ((MFunction) d).evaluateAt(findEndOfBrackets());
					//else
						//throw new UnexpectedCharacterException("Expected '(' after a function instead of '" + (char) ch + "'.");
				} //No 'else if' because if the result from the MFunction is a matrix or vector, it the following if-statement can be applied as well.
				if(d instanceof MIndexable) {
					do {
						if(consume('[')) {
							d = ELEMENT.evaluate(d, toSliceObject(Interpreter.extractIndex(expr,pos-1), d.shape()));
							findEndOfBrackets();
						}
						else break;
					} while(d instanceof MVector || d instanceof MMatrix);
				} else
					d = d.evaluate();
			} else if (MConst.isConstant(letters))
				d = MConst.get(letters).evaluate();
			else if(letters.equals("inf"))
				d = new MReal(Double.POSITIVE_INFINITY);
			else if(Algorithms.isAlgorithm(letters)) {
				if(!consume('(')) {
					if(letters.equals("ans"))
						d = Variables.ans();
					else
						throw new UnexpectedCharacterException("Expected '(' after a algorithm name instead of '" + (char) ch + "'.");
				} else
					d = Algorithms.getAlgorithm(letters).execute(getArguments(findEndOfBrackets()));
			} else {
				if(Functions.isFunction(letters)) {
					if(!consume('('))
						throw new UnexpectedCharacterException("Expected '(' after a function name instead of '" + (char) ch + "'.");
					d = new Parser(findEndOfBrackets()).evaluate(); // Gets the factor inside the function
					d = Functions.getFunction(letters).evaluate(d);
				} else if(letters.equals("inv"))
					d = Operator.INVERT.evaluate(processFactor());
				else
					throw new UnexpectedCharacterException(expr, p, pos);
			}
		} else if(consume('i')) {
			d = MConst.i.evaluate();
		} else {
			throw new UnexpectedCharacterException(expr, pos);
		}

		// Operators which work immediately on this factor (e.q. they don't connect terms)
		if (consume('^'))
			d = POWER.evaluate(d, processFactor());
		if (consume('%'))
			d = MOD.evaluate(d, processFactor());
		if(consume('\''))
			d = TRANSPOSE.evaluate(d);
		return d;
	}
	
	public String findEndOfBrackets() throws UnexpectedCharacterException {
		int count = 1, position = pos;
		count += (ch == '(' || ch == '{' || ch == '[' ? 1 : (ch == ')' || ch == '}' || ch == ']' ? -1 : 0));
		while(count > 0) {
			nextChar();
			if(ch == -1)
				throw new UnexpectedCharacterException("Expression ended while searching for end of brackets.");
			count += (ch == '(' || ch == '{' || ch == '[' ? 1 : (ch == ')' || ch == '}' || ch == ']' ? -1 : 0));
		}
		nextChar(); //move on to the next character after the closing bracket.
		return expr.substring(position, pos-1); //-1 because of nextChar() the line above
	}
	
	public static MathObject[] getArgumentsAsMathObject(String s) throws UnexpectedCharacterException, InvalidFunctionException, TreeException, ShapeException {
		return toMathObjects(getArguments(s));
	}
	
	public static MathObject[] toMathObjects(String... s) throws ShapeException, UnexpectedCharacterException, InvalidFunctionException, TreeException {
		MathObject[] moArgs = new MathObject[s.length];
		for(int i = 0; i < s.length; i++) {
				moArgs[i] = new Parser(s[i]).evaluate();
		}
		return moArgs;
	}
	
	/**
	 * Splits a string containing substrings separated by commas into those substrings. This method respects brackets.
	 * <br>
	 * The string {@code "1,(3+4), f(x,y)=[x^2,y^2]"} will be split into {@code ["1", "(3+4)", "f(x,y)=[x^2,y^2]"]}
	 * @param s the string to be split
	 * @return the individual arguments in the string.
	 */
	public static String[] getArguments(String s) {
		int brCount = 0;
		int lastPos = 0;
		ArrayList<String> arguments = new ArrayList<String>();
		for(int i = 0; i < s.length(); i++) {
			char c= s.charAt(i);
			if(c==' ') continue;
			if(c == ',' && brCount == 0) {
				arguments.add(s.substring(lastPos, i).trim()); 
				//increment i because s.charAt(i)==',' which shouldn't be included in the next argument
				lastPos = i+1;
			} else if(c == '(' || c=='[' || c == '{')
				brCount++;
			else if(c==')' || c==']' || c=='}')
				brCount--;
		}
		arguments.add(s.substring(lastPos).trim());
		String[] args = new String[arguments.size()];
		return arguments.toArray(args);
	}
	
	private static MathObject[] toSliceObject(int[][] indices, Shape shape) {
		if(shape.dim()==1) {
			if(indices[1].length==0) {
				if(indices[0].length==1)
					return new MReal[] {new MReal(indices[0][0])};
				else
					return new MVector[] {new MVector(indices[0][0], indices[0][1] == 0 ? shape.get(0) : indices[0][1])};
			} else
				throw new IndexException("Got 2 indices for object with dimension 1");
		}
		if(shape.dim() == 2) {
			MathObject a, b;
			if(indices[1].length==0)
				throw new IndexException("Got 1 index for object with dimension 2");
			if(indices[0].length == 1)
				a = new MReal(indices[0][0]);
			else if(indices[0][1]==0)
				a = new MVector(indices[0][0], shape.get(0));
			else
				a = new MVector(indices[0][0], indices[0][1]);
			
			if(indices[1].length == 1)
				b = new MReal(indices[1][0]);
			else if(indices[1][1]==0)
				b = new MVector(indices[1][0], shape.get(1));
			else 
				b = new MVector(indices[1][0], indices[1][1]);
			return new MathObject[] {a, b};
		}
		return null;
	}
}
