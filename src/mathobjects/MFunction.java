package mathobjects;

import java.util.HashMap;

import helpers.Printer;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Operator;
import main.Parser;
import main.Variable;
import main.Variables;
import tree.Node;
import tree.Tree;

public class MFunction extends MExpression {

	String[] vars;
	boolean defined;
	HashMap<String, MathObject> paramMap;
	
	public MFunction(String[] vars, FunctionTree tr, boolean defined) {
		super(tr);
		this.vars = vars;
		this.defined = defined;
		paramMap = new HashMap<String, MathObject>();
	}
	
	public MFunction(String[] vars, Tree tr, boolean defined) {
		super(tr);
		this.vars = vars;
		this.defined = defined;
		tree = new FunctionTree(tr.getRoot());
		paramMap = new HashMap<String, MathObject>();
	}

	@Override
	public MathObject negate() {
		Node<Operator> opNode = new Node<Operator>(Operator.NEGATE);
		opNode.left(tree.root);
		tree.updateRoot();
		return this;
	}

	@Override
	public MathObject invert() {
		Node<Operator> opNode = new Node<Operator>(Operator.INVERT);
		opNode.left(tree.root);
		tree.updateRoot();
		return this;
	}

	@Override
	public MathObject copy() {
		return new MFunction(vars, ((FunctionTree) tree).copy((n) -> {
			return new Node<Object>(n.data);
		}), defined);
	}

	/**
	 * Evaluates every {@link Node} in the {@link Tree} defined by this function. If
	 * the following is true for a node <tt>n</tt><br/>
	 * {@code
	 * n.data instanceof Variable && n.data.equals(var)
	 * } <br/>
	 * for a function parameter <tt>var</tt>, then that <tt>Node</tt> will be
	 * skipped.
	 * 
	 * @return a new {@code MFunction} with the same parameters, and the evaluated
	 *         tree as described above.
	 */
	@Override
	public MathObject evaluate() {
		return new MFunction(vars, new FunctionTree(tree.copy((n) -> {
			if (n.data instanceof Variable) {
				for (String v : vars)
					if (!n.data.equals(v)) {
						return new Node<MathObject>(((Variable) n.data).evaluate());
					} else {
						return new Node<Variable>(new Variable(v));
					}
			} else if (n.data instanceof MathObject)
				return new Node<MathObject>(((MathObject) n.data).evaluate());
			return new Node<Object>(n.data);
		}).getRoot()), defined);
	}

	/**
	 * Creates a new {@code MFunction} and stores in the Variables list.
	 * <p>
	 * If the declaration of the new function contains a := (which means that it's a
	 * definition), then the expression is simply parsed to a {@link MExpresssion}
	 * and the variables given between the brackets after the name of the function
	 * are stored in the new {@code MFunction}
	 * </p>
	 * <p>
	 * If the declaration of the function does not use ":=", but "=" instead, then a
	 * DFS on tree in the {@code MExpression} is performed, in which all variables,
	 * except the ones on which the function depends, are evaluated.
	 * </p>
	 * The example <tt>f(x,y)=x^2+a*y^2</tt> will be used in the params section.
	 * 
	 * @param s
	 *            the part before the "=" or ":=" sign in the declaration, in this
	 *            case <tt>f(x,y)</tt>. The variables will be {@code ["x", "y"]},
	 *            and the name <tt>f</tt>.
	 * @param expr
	 *            the part after the "=" or ":=" sign. In this case "x^2+a*y^2".
	 *            This will be parsed into a {@code MExpression} using
	 *            {@link Parser#getTree()}.
	 * @param defined
	 *            whether or not the command contains a ":=". If <tt>false</tt> then
	 *            <tt>a</tt> will be evaluated and its numerical value will be saved
	 *            instead.
	 * @return a new {@code MFunction} containing the variables array and the
	 *         {@code MExpression}. Note that this {@code MFunction} will
	 *         automatically be added to {@link Variables}.
	 * @throws UnexpectedCharacterException
	 *             as thrown by {@code Parser.getTree()}
	 */
	public static MFunction createAndStore(String s, String expr, boolean defined) throws UnexpectedCharacterException {
		int brIndex = s.indexOf("(");
		String[] vars = s.substring(brIndex + 1, s.lastIndexOf(")")).replace(" ", "").split(",");
		// Add all variables temporary to the Variables list, in order to prevent
		// warnings in the console.
		for (String v : vars)
			if (Variables.get(v) == null)
				Variables.set(v, null);
		Tree tr = new Parser(expr).getTree();
		if (!defined)
			tr.DFS(tr.getRoot(), n -> {
				if (n.isInternal())
					return;
				if (n.data instanceof Variable) {
					for (String var : vars)
						if (n.data.equals(var))
							return;
					n.replace(new Node<MathObject>(((Variable) n.data).evaluate()));
				} else if (n.data instanceof MathObject)
					n.replace(new Node<MathObject>(((MathObject) n.data).evaluate()));
			});
		// Remove the temporary variables.
		for (String v : vars)
			if (Variables.get(v) == null)
				Variables.remove(v);
		return (MFunction) new Variable(s.substring(0, brIndex), new MFunction(vars, tr, defined)).get();
	}

	/**
	 * Evaluates the function at the values as given by the arguments. In order to
	 * evaluate the function, the values for the parameters are temporarily stored
	 * in {@link Variables} before {@link MFunction#evaluateAt()} is called. After
	 * the evaluation is complete, the original values for the parameters in
	 * {@code Variables} are restored (if there was no entry in {@code Variables}
	 * with the same name as a parameter, the created entry will be removed from
	 * {@code Variables}.
	 * 
	 * @param paramVals
	 *            an array of {@link MathObject}s containing the values at which the
	 *            function should be evaluated. Note that the order of the
	 *            <tt>MathObject</tt>s in the array should correspond to the order
	 *            of the parameters in the {@code MFunction}'s declaration.
	 * @return A {@link MathObject} containing the result of the evaluation.
	 * @throws TreeException
	 * @throws IllegalArgumentException
	 *             if {@code paramVals.length != vars.length = true}.
	 * @see #evaluateAt()
	 */
	public MathObject evaluateAt(MathObject... paramVals) throws TreeException {
		if (paramVals.length != vars.length)
			throw new IllegalArgumentException(
					"Function expected " + vars.length + " arguments, got " + paramVals.length);
		paramMap.clear();
		for(int i = 0; i < paramVals.length; i++)
			paramMap.put(vars[i], paramVals[i]);
		return evaluateAt();
	}
	
	public void putVariable(String name, MathObject mo) {
		paramMap.put(name, mo);
	}

	/**
	 * Evaluates the {@link Tree} defined by this {@code MFunction}
	 * 
	 * @return A {@link MathObject} containing the result of the evaluation.
	 * @throws TreeException
	 *             as thrown by {@link Tree#evaluateTree()}
	 * @see Tree#evaluateTree()
	 */
	public MathObject evaluateAt() throws TreeException {
		return ((FunctionTree) tree).evaluateTree();
	}

	/**
	 * Evaluates the function at the values as given in the String. The values will
	 * be retrieved by {@link Parser#getArgumentsAsMathObject(String)}.
	 * 
	 * @param s
	 *            the String containing the values of the parameters, separated by
	 *            commas. For example: when calling <tt>f(1,2,a)</tt>, <tt>s</tt>
	 *            will be {@code "1,2,a"}. The function will be evaluated at
	 *            {@code (1,2,a')} where <tt>a'</tt> is the result of
	 *            {@code Variables.get("a").evaluate()}.
	 * @return A {@link MathObject} containing the result of the evaluation.
	 * @throws UnexpectedCharacterException
	 *             as thrown by {@link Parser#getArgumentsAsMathObject(String)}
	 * @throws InvalidFunctionException
	 *             as thrown by {@link Parser#getArgumentsAsMathObject(String)}
	 * @throws TreeException
	 *             as thrown by {@link #evaluateAt()}
	 * @throws IllegalArgumentException
	 *             if the amount of parameters in <tt>s</tt> does not equal the
	 *             amount of function parameters.
	 */
	public MathObject evaluateAt(String s)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException {
		MathObject[] paramVals = Parser.getArgumentsAsMathObject(s);
		if (paramVals.length != vars.length)
			throw new IllegalArgumentException(
					"Function expected " + vars.length + " arguments, got " + paramVals.length);
		return evaluateAt(paramVals);
	}

	public boolean isDefined() {
		return defined;
	}

	public String[] getParameters() {
		return vars;
	}

	/**
	 * Calls {@link Printer#toText(MExpression)} and returns the result
	 * 
	 * @return {@code Printer.toText(this);}
	 */
	@Override
	public String toString() {
		return Printer.toText(this);
	}
	
	private class FunctionTree extends Tree {
		public FunctionTree(Node<?> root) {
			super(root);
		}

		@Override
		protected MathObject evaluateNode(Node<?> n) throws TreeException {
			if(n.data instanceof Variable) {
				MathObject var = paramMap.get(((Variable) n.data).getName());
				return var == null ? super.evaluateNode(n) : var;
			} 
			return super.evaluateNode(n);
		}
	}
}