package helpers;

import static main.Operator.ADD;
import static main.Operator.DIVIDE;
import static main.Operator.MULTIPLY;
import static main.Operator.SUBTRACT;

import algorithms.Functions;
import algorithms.Functions.Function;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Operator;
import main.Variable;
import main.Variables;
import mathobjects.MConst;
import mathobjects.MScalar;
import mathobjects.MathObject;
import tree.Node;
import tree.Tree;

public class Parser {

	int pos = -1, ch = -1;
	String expr;
	Tree tree;
	Node<?> p;

	public Parser(String s) {
		expr = s;
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

	/**
	 * Compares the given character with the current character. The current
	 * character is consumed if it's the same.
	 * 
	 * @param chToCheck
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
		tree = new Tree();
		nextChar();
		tree.root = p = getFactor();
		while (ch > 0 && p != null)
			p = getTerm();
		return tree;
	}

	public Node<?> getSubTree() throws UnexpectedCharacterException {
		tree = new Tree();
		nextChar();
		tree.root = p = getFactor();
		while (ch > 0 && p != null)
			p = getTerm();
		return tree.root;
	}

	/**
	 * Processes the whole expression.
	 * 
	 * @return the arithmetic solution of the expression.
	 * @throws UnexpectedCharacterException
	 *             if the expression contains an unknown or unexpected character
	 * @throws InvalidFunctionException
	 *             if the expression contains an unknown function.
	 */
	public MathObject evaluate() throws UnexpectedCharacterException, InvalidFunctionException {
		nextChar();
		MathObject val = processExpression();
		if (pos < expr.length())
			throw UnexpectedCharacterException.create(expr, pos);
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
	protected MathObject getVector() throws UnexpectedCharacterException {
		int count = 1;
		int position = pos;
		while(count != 0 && ch > 0) {
			if(ch == '[') count++;
			if(ch == ']') count--;
			nextChar();
		}	
		return new VectorParser("[" + expr.substring(position, pos)).parse();
	}

	private Node<?> getFactor() throws UnexpectedCharacterException {
		Node<?> n = null;
		int position = pos;
		if (ch == '(')
			return getSubTree();
		else if (ch == ')')
			return null;
		else if (ch == '[') {
			consume('[');
			return new Node<MathObject>(getVector());
		} else if (ch == ']')
			return null;
		else if (Character.isDigit(ch) || ch == '.' || ch == '-') {
			do {
				nextChar();
			} while (Character.isDigit(ch) || ch == '.');
			String s1 = expr.substring(position, pos);
			if (s1 == "-") {
				if (p == null)
					tree.root = p = new Node<Operator>(Operator.MULTIPLY);
				else
					p = new Node<Operator>(Operator.MULTIPLY);
				p.left(new Node<MScalar>(new MScalar(-1.0)));
				p.right(getFactor());
				return p.right();
			} else
				n = new Node<MScalar>(new MScalar(Double.valueOf(s1)));
		} else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
			while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
				nextChar();
			String str = expr.substring(position, pos);
			if (MConst.isConstant(str))
				n = new Node<MConst>(MConst.get(str));
			else if (Functions.isFunction(str)) {
				n = new Node<Function>(Functions.getFunction(str));
				n.left(getFactor());
			} else {
				n = new Node<Variable>(new Variable(str));
				if (!Variables.exists(str))
					System.err.println("The variable " + str + " is undefined. Statement was parsed regardless.");
			}
		}
		return n;
	}

	private Node<?> getTerm() throws UnexpectedCharacterException {
		Node<?> n;
		if (consume(')'))
			return null;
		else if (consume('+')) {
			n = new Node<Operator>(Operator.ADD);
			n.left(tree.root);
			n.right(getFactor());
			tree.root = n;
			return n.right();
		} else if (consume('-')) {
			n = new Node<Operator>(Operator.SUBTRACT);
			n.left(tree.root);
			n.right(getFactor());
			tree.root = n;
			return n.right();
		} else if (consume('*')) {
			tree.insert(p, new Node<Operator>(Operator.MULTIPLY), 0);
			n = p;
			p = p.parent;
			n = new Node<>(p.data);
			p.right(getFactor());
			return p.right();
		} else if (consume('/')) {
			tree.insert(p, new Node<Operator>(Operator.DIVIDE), 0);
			n = p;
			p = p.parent;
			n = new Node<>(p.data);
			p.right(getFactor());
			return p.right();
		} else if (consume('^')) {
			tree.insert(p, new Node<Operator>(Operator.POWER), 0);
			n = p;
			p = p.parent;
			n = new Node<>(p.data);
			p.right(getFactor());
			return p;
		} else if (!Character.isDigit(ch)) { // Does the same as consume('*')
			tree.insert(p, new Node<Operator>(Operator.MULTIPLY), 0);
			n = p;
			p = p.parent;
			n = new Node<>(p.data);
			p.right(getFactor());
			return p.right();
		} else if (consume('(')) {
			return getSubTree();
		}
		throw UnexpectedCharacterException.create(expr, pos);
	}

	// ################ Evaluate expression to numeric value ################
	// expression = term. or expression '+' term, or expression '-' term
	private MathObject processExpression() throws InvalidFunctionException, UnexpectedCharacterException {
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
	private MathObject processTerm() throws InvalidFunctionException, UnexpectedCharacterException {
		MathObject d = processFactor();
		while (true) {
			if (consume('*'))
				d = MULTIPLY.evaluate(d, processFactor());
			else if (consume('/'))
				d = DIVIDE.evaluate(d, processFactor());
			else if (pos < expr.length() && Character.isLetter(ch))
				d = MULTIPLY.evaluate(d, processFactor());
			else
				return d;
		}
	}

	// factor = '+' factor (positive) or '-' factor (negative), or '('
	// expression ')', or number (double), or factor^factor, or function(factor)
	// (e.g. sine, cosine, etc.)
	private MathObject processFactor() throws InvalidFunctionException, UnexpectedCharacterException {
		if (consume('+'))
			return processFactor();
		if (consume('-'))
			return processFactor().negate();

		MathObject d = null;
		int p = pos;

		if (consume('(')) { // new expression within the parentheses
			d = processExpression();
			consume(')');
		} else if(consume('[')) {
			d = getVector().evaluate();
		} else if (consume('|')) {
			d = Functions.abs(processExpression());
			consume('|');
		} else if ((ch >= '0' && ch <= '9') || ch == '.') {// number
			while ((ch >= '0' && ch <= '9') || ch == '.')
				nextChar();
			d = new MScalar(Double.parseDouble(expr.substring(p, pos)));
			if (consume('E')) {
				d = Operator.MULTIPLY.evaluate(d, Operator.POWER.evaluate(new MScalar(10), processFactor()));
			}
			// Letter, which is part of a variable or function
		} else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
			while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_' || ch >= '0' && ch <= '9')
				nextChar();
			String letters = expr.substring(p, pos);
			if (Variables.exists(letters))
				d = Variables.get(letters).evaluate();
			else if (MConst.isConstant(letters))
				d = MConst.get(letters).evaluate();
			else {
				d = processFactor(); // Gets the factor inside the function
				switch (letters) {
				case "sin":
					d = Functions.sin(d);
					break;
				case "sind":
					d = Functions.sind(d);
					break;
				case "cos":
					d = Functions.cos(d);
					break;
				case "cosd":
					d = Functions.cosd(d);
					break;
				case "tan":
					d = Functions.tan(d);
					break;
				case "tand":
					d = Functions.tand(d);
					break;
				case "sqrt":
					d = Functions.sqrt(d);
					break;
				case "ln":
					d = Functions.ln(d);
					break;
				case "log":
					d = Functions.log(d);
					break;
				case "abs":
					d = Functions.abs(d);
					break;
				default:
					if (consume('('))
						throw new InvalidFunctionException(letters);
					else
						throw UnexpectedCharacterException.create(expr, pos);
				}
			}
		} else {
			throw UnexpectedCharacterException.create(expr, pos);
		}

		// If the factor has to be risen to a power
		if (consume('^'))
			d = Operator.POWER.evaluate(d, processFactor());
		if (consume('%'))
			d = Operator.MOD.evaluate(d, processFactor());
		return d;
	}
}
