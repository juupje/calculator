package com.github.juupje.calculator.mathobjects;

import java.util.HashMap;
import java.util.HashSet;

import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.tree.DFSTask;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;
import com.github.juupje.calculator.tree.TreeFunction;

public class MFunction extends MExpression {

	public static final int FLAG_INTERNAL = 1;
	
	String[] vars;
	Shape[] varShapes;
	boolean defined;
	HashMap<String, MathObject> paramMap;

	public MFunction(String vars[], Shape[] varShapes, FunctionTree tr, boolean defined) {
		super(tr);
		this.vars = vars;
		this.defined = defined;
		paramMap = new HashMap<String, MathObject>(vars.length);
		for (int i = 0; i < varShapes.length; i++)
			varShapes[i] = new Shape();
	}

	public MFunction(String vars[], Shape[] varShapes, Tree tr, boolean defined) {
		super(tr);
		this.vars = vars;
		this.defined = defined;
		this.varShapes = varShapes;
		tree = new FunctionTree(processTree(tr, defined, vars).getRoot());
		paramMap = new HashMap<String, MathObject>(vars.length);
	}

	@Override
	public MFunction copy() {
		return new MFunction(vars, varShapes, tree.copy(new TreeFunction() {
			@Override
			public Node<?> apply(Node<?> n) {
				return new Node<Object>(n.data);
			}
		}), defined);
	}

	@Override
	public HashSet<Variable> getDependencies() {
		HashSet<Variable> dependencies = super.getDependencies();
		for (String var : vars) {
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
		return new MFunction(vars, varShapes, tree.copy(new TreeFunction() {
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
	 * Creates a new {@code MFunction} from an expression. This method extracts the
	 * names of the function arguments from the first parameter and returns
	 * {@link #create(String[], String, boolean)} with those names. The example
	 * <tt>f(x,y)=x^2+a*y^2</tt> will be used in the params section.
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
	 *         {@code MExpression}.
	 * @throws UnexpectedCharacterException as thrown by
	 *                                      {@link #create(String[], String, boolean)}
	 */
	public static MFunction create(String s, String expr, boolean defined) throws UnexpectedCharacterException {
		int brIndex = s.indexOf("(");
		String[] vars = s.substring(brIndex + 1, s.lastIndexOf(")")).replace(" ", "").split(",");
		return create(vars, expr, defined);
	}

	/**
	 * Creates a new {@code MFunction} from an expression, treating the given
	 * variable names as its arguments.
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
	 * @param vars    the array containing the names of the function's arguments. In
	 *                this case ["x", "y"]
	 * @param expr    the part after the "=" or ":=" sign. In this case "x^2+a*y^2".
	 *                This will be parsed into a {@code MExpression} using
	 *                {@link Parser#getTree()}.
	 * @param defined whether or not the command contains a ":=". If <tt>false</tt>
	 *                then <tt>a</tt> will be evaluated and its numerical value will
	 *                be saved instead.
	 * @param flags   an integer containing the flags necessary for computing the
	 *                function.
	 * @return a new {@code MFunction} containing the variables array and the
	 *         {@code MExpression}.
	 * @throws UnexpectedCharacterException as thrown by {@code Parser.getTree()}
	 */
	public static MFunction create(String[] vars, String expr, boolean defined, int flags) {
		// Add all variables temporary to the Variables list, in order to prevent
		// warnings in the console.
		String varNames[] = new String[vars.length];
		Shape varShapes[] = new Shape[vars.length];
		for (int i = 0; i < vars.length; i++) {
			String v = vars[i];
			if (v.matches("\\w+\\[(\\d+|\\d+,\\d+)\\]")) {
				String[] dims = Tools.extractFirst(v, '[', ']').split(",");
				try {
					if (dims.length == 1)
						varShapes[i] = new Shape(Integer.valueOf(dims[0]));
					else if (dims.length == 2)
						varShapes[i] = new Shape(Integer.valueOf(dims[0]), Integer.valueOf(dims[1]));
					else
						throw new ShapeException("Functions with more than 2D arguments are not supported.");
				} catch (NumberFormatException e) {
					throw new UnexpectedCharacterException("Expected positive integer argument, got "
							+ (dims.length == 1 ? dims[0] : dims[0] + " and " + dims[1]));
				}
				v = v.substring(0, v.indexOf('['));
			} else
				varShapes[i] = new Shape();
			if (!Tools.checkNameValidity(v, (flags & FLAG_INTERNAL)==FLAG_INTERNAL))
				throw new UnexpectedCharacterException("Invalid name: " + v);
			if (Variables.get(v) == null)
				Variables.set(v, null);
			varNames[i] = v;
		}
		Tree tr = new Parser(expr).getTree();
		tr = processTree(tr, defined, varNames);

		// Remove the temporary variables.
		for (String v : varNames)
			if (Variables.get(v) == null)
				Variables.remove(v);
		return new MFunction(varNames, varShapes, tr, defined);
	}
	
	/**
	 * @see {{@link #create(String[], String, boolean, int)}
	 */
	public static MFunction create(String[] vars, String expr, boolean defined) {
		return create(vars, expr, defined, 0);
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

				// If the node is not internal and this function is not defined, replace this
				// node with its evaluated value.
				if (defined)
					return;
				if (n.data instanceof Variable) {
					for (String var : vars)
						if (n.data.equals(var))
							return;
					//MathObject obj = ((Variable) n.data).get();

					n.replace(new Node<MathObject>(tree.evaluateNode(n)));//new Node<MathObject>(obj.evaluate()));
				} else if (n.data instanceof MathObject)
					n.replace(new Node<MathObject>(tree.evaluateNode(n)));//n.replace(new Node<MathObject>(((MathObject) n.data).evaluate()));
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
		for (int i = 0; i < paramVals.length; i++)
			if (!paramVals[i].shape().equals(varShapes[i]))
				throw new IllegalArgumentException("Shape of argument " + i + " with name " + vars[i]
						+ " is not compatible with the function definition: expected shape " + varShapes[i] + ".");
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
		return evaluateAt(Parser.getArgumentsAsMathObject(s));
	}

	private MathObject evaluateAt(HashMap<String, MathObject> map) throws TreeException {
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
	private MathObject evaluateAt() throws TreeException {
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

	public Shape[] getParamShapes() {
		return varShapes;
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

		public FunctionTree copy() {
			return new FunctionTree(super.copy().getRoot());
		}

		@Override
		public Shape getShape(Node<?> n) {
			if (n.data instanceof Variable) {
				Variable v = (Variable) n.data;
				for (int i = 0; i < vars.length; i++)
					if (v.getName().equals(vars[i]))
						return varShapes[i];
				if (v.get() != null)
					return v.get().shape();
				else {
					Calculator.ioHandler.err("Variable " + v.getName() + " not defined, assuming scalar shape.");
					return Shape.SCALAR;
				}
			} else
				return super.getShape(n);
		}

		@Override
		public MathObject evaluateNode(Node<?> n) throws TreeException {
			if (n.data instanceof Variable) {
				MathObject var = paramMap.get(((Variable) n.data).getName());
				if(var == null)
					return super.evaluateNode(n);
				else if (var instanceof MFunction) {
					System.out.println("YO! Dude, what the hell is this? Please check why this line is executed.");
					if (n.left() == null)
						return ((MFunction) var).evaluateAt(paramMap);
					else
						return ((MFunction) var).evaluateAt(((MVector) evaluateNode(n.left())).elements());
				} else
					return var;
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