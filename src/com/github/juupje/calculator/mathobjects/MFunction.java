package com.github.juupje.calculator.mathobjects;

import java.util.HashMap;
import java.util.HashSet;

import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.tree.DFSTask;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;
import com.github.juupje.calculator.tree.TreeFunction;

public class MFunction extends MExpression {

	public static final short INIT_PARAM_CAP = 4;

	String[] vars;
	boolean defined;
	HashMap<String, MathObject> paramMap;

	public MFunction(String vars[], FunctionTree tr, boolean defined) {
		super(tr);
		this.vars = vars;
		this.defined = defined;
		paramMap = new HashMap<String, MathObject>(INIT_PARAM_CAP);
	}

	public MFunction(String[] vars, Tree tr, boolean defined) {
		super(tr);
		this.vars = vars;
		this.defined = defined;
		tree = new FunctionTree(processTree(tr, defined, vars).getRoot());
		paramMap = new HashMap<String, MathObject>(INIT_PARAM_CAP);
	}

	@Override
	public MFunction copy() {
		return new MFunction(vars, tree.copy(new TreeFunction() {
			@Override
			public Node<?> apply(Node<?> n) {
				return new Node<Object>(n.data);
			}
		}), defined);
	}
	
	@Override
	public HashSet<Variable> getDependencies() {
		HashSet<Variable> dependencies = super.getDependencies();
		for(String var : vars) {
			dependencies.remove(new Variable(var));
		}
		return dependencies;
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
	public MFunction evaluate() {
		return new MFunction(vars, tree.copy(new TreeFunction() {
			@Override
			public Node<?> apply(Node<?> n) {
				if (n.data instanceof Variable) {
					for (String v : vars)
						if (n.data.equals(v))
							return new Node<Variable>(new Variable(v));
					return new Node<MathObject>(((Variable) n.data).evaluate());
				} else if (n.data instanceof MathObject) {
					if (n.parent != null
							&& (n.parent.data instanceof MFunction || (n.parent.data instanceof Variable
									&& ((Variable) n.parent.data).get() instanceof MFunction))
							&& n.data instanceof MVector)
						// this node is a vector containing the variables for a function, so don't
						// change it.
						return new Node<MVector>((MVector) n.data);
					return new Node<MathObject>(((MathObject) n.data).evaluate());
				}
				return new Node<Object>(n.data);
			}
		}), defined);
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
	 * <p>
	 * Note that if the function to be created depends on other functions there are
	 * several options:
	 * <ul>
	 * <li><tt>f(x)=g+h</tt>, g and h being functions with the same parameter(s) as
	 * f: g and h will be copied into f</li>
	 * <li><tt>f(x)=g(x)+h(x)</tt>: g and h will be referenced in f. This is the
	 * same as <tt>f(x):=g+h</tt></li>
	 * <li><tt>f(x)=g(x)+h</tt>: g will be referenced and h will be
	 * copied.</tt></li>
	 * </ul>
	 * </p>
	 * The example <tt>f(x,y)=x^2+a*y^2</tt> will be used in the params section.
	 * 
	 * @param s       the part before the "=" or ":=" sign in the declaration, in
	 *                this case <tt>f(x,y)</tt>. The variables will be
	 *                {@code ["x", "y"]}, and the name <tt>f</tt>.
	 * @param expr    the part after the "=" or ":=" sign. In this case "x^2+a*y^2".
	 *                This will be parsed into a {@code MExpression} using
	 *                {@link Parser#getTree()}.
	 * @param defined whether or not the command contains a ":=". If <tt>false</tt>
	 *                then <tt>a</tt> will be evaluated and its numerical value will
	 *                be saved instead.
	 * @return a new {@code MFunction} containing the variables array and the
	 *         {@code MExpression}. Note that this {@code MFunction} will
	 *         automatically be added to {@link Variables}.
	 * @throws UnexpectedCharacterException as thrown by {@code Parser.getTree()}
	 */
	public static MFunction create(String s, String expr, boolean defined) throws UnexpectedCharacterException {
		int brIndex = s.indexOf("(");
		String[] vars = s.substring(brIndex + 1, s.lastIndexOf(")")).replace(" ", "").split(",");
		// Add all variables temporary to the Variables list, in order to prevent
		// warnings in the console.
		for (String v : vars)
			if (Variables.get(v) == null)
				Variables.set(v, null);
		Tree tr = new Parser(expr).getTree();
		tr = processTree(tr, defined, vars);

		// Remove the temporary variables.
		for (String v : vars)
			if (Variables.get(v) == null)
				Variables.remove(v);
		return new MFunction(vars, tr, defined);
	}

	public static Tree processTree(Tree tree, boolean defined, String[] vars) {
		tree.DFS(new DFSTask(false) {
			@Override
			public void accept(Node<?> n) {
				// If the node is a vector containing functions: turn it into a vector-function
				// (field)
				if (n.data instanceof MVector) {
					for (MathObject e : ((MVector) n.data).elements()) {
						if (e instanceof MExpression || e instanceof MFunction) {
							tree.replace(n,
									new Node<MVectorFunction>(new MVectorFunction(vars, ((MVector) n.data), defined)));
							return;
						}
					}
				}

				// If the node is not internal and this function is not defined, replace this node
				// with its evaluated value.
				if (defined)
					return;
				if (n.data instanceof Variable) { //this implies that the node is not internal
					for (String var : vars)
						if (n.data.equals(var))
							return;
					MathObject obj = ((Variable) n.data).get();

					n.replace(new Node<MathObject>(obj.evaluate()));
				} else if (n.data instanceof MathObject)
					n.replace(new Node<MathObject>(((MathObject) n.data).evaluate()));
			}
		});
		return tree;
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
	 * @param paramVals an array of {@link MathObject}s containing the values at
	 *                  which the function should be evaluated. Note that the order
	 *                  of the <tt>MathObject</tt>s in the array should correspond
	 *                  to the order of the parameters in the {@code MFunction}'s
	 *                  declaration.
	 * @return A {@link MathObject} containing the result of the evaluation.
	 * @throws TreeException
	 * @throws IllegalArgumentException if
	 *                                  {@code paramVals.length != vars.length = true}.
	 * @see #evaluateAt()
	 */
	public MathObject evaluateAt(MathObject... paramVals) throws TreeException {
		if (paramVals.length != vars.length)
			throw new IllegalArgumentException(
					"Function expected " + vars.length + " arguments, got " + paramVals.length);
		paramMap.clear();
		for (int i = 0; i < paramVals.length; i++)
			paramMap.put(vars[i], paramVals[i]);
		return evaluateAt();
	}

	/**
	 * Evaluates the function at the values as given in the String. The values will
	 * be retrieved by {@link Parser#getArgumentsAsMathObject(String)}.
	 * 
	 * @param s the String containing the values of the parameters, separated by
	 *          commas. For example: when calling <tt>f(1,2,a)</tt>, <tt>s</tt> will
	 *          be {@code "1,2,a"}. The function will be evaluated at
	 *          {@code (1,2,a')} where <tt>a'</tt> is the result of
	 *          {@code Variables.get("a").evaluate()}.
	 * @return A {@link MathObject} containing the result of the evaluation.
	 * @throws UnexpectedCharacterException as thrown by
	 *                                      {@link Parser#getArgumentsAsMathObject(String)}
	 * @throws InvalidFunctionException     as thrown by
	 *                                      {@link Parser#getArgumentsAsMathObject(String)}
	 * @throws TreeException                as thrown by {@link #evaluateAt()}
	 * @ @throws IllegalArgumentException if the amount of parameters in <tt>s</tt>
	 *           does not equal the amount of function parameters.
	 */
	public MathObject evaluateAt(String s)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException, ShapeException {
		MathObject[] paramVals = Parser.getArgumentsAsMathObject(s);
		if (paramVals.length != vars.length)
			throw new IllegalArgumentException(
					"Function expected " + vars.length + " arguments, got " + paramVals.length);
		return evaluateAt(paramVals);
	}

	public MathObject evaluateAt(HashMap<String, MathObject> map) throws TreeException {
		setParamMap(map);
		return evaluateAt();
	}

	/**
	 * Evaluates the {@link Tree} defined by this {@code MFunction}
	 * 
	 * @return A {@link MathObject} containing the result of the evaluation.
	 * @throws TreeException as thrown by {@link Tree#evaluateTree()}
	 * @see Tree#evaluateTree()
	 */
	public MathObject evaluateAt() throws TreeException {
		return ((FunctionTree) tree).evaluateTree();
	}

	public void putVariable(String name, MathObject mo) {
		paramMap.put(name, mo);
	}

	public void setParamMap(HashMap<String, MathObject> map) {
		paramMap.clear();
		paramMap.putAll(map);
	}

	public boolean isDefined() {
		return defined;
	}

	public String[] getParameters() {
		return vars;
	}

	public MFunction add(MathObject other) {
		addOperation(Operator.ADD, other);
		return this;
	}

	public MFunction subtract(MathObject other) {
		addOperation(Operator.SUBTRACT, other);
		return this;
	}

	public MFunction multiply(MathObject other) {
		addOperation(Operator.MULTIPLY, other);
		return this;
	}

	public MFunction divide(MathObject other) {
		addOperation(Operator.DIVIDE, other);
		return this;
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
			if (n.data instanceof Variable) {
				MathObject var = paramMap.get(((Variable) n.data).getName());
				if (var instanceof MFunction) {
					if (n.left() == null)
						return ((MFunction) var).evaluateAt(paramMap);
					else
						return ((MFunction) var).evaluateAt(((MVector) evaluateNode(n.left())).elements());
				}
				return var == null ? super.evaluateNode(n) : var;
			} else if (n.data instanceof MVectorFunction) {
				if (n.left() == null)
					return ((MVectorFunction) n.data).evaluateAt(paramMap);
				else
					return ((MVectorFunction) n.data).evaluateAt(((MVector) evaluateNode(n.left())).elements());
			} else if (n.data instanceof MFunction) {
				if (n.left() == null)
					return ((MFunction) n.data).evaluateAt(paramMap);
				else
					return ((MFunction) n.data).evaluateAt(((MVector) evaluateNode(n.left())).elements());
			}
			return super.evaluateNode(n);
		}
	}
}