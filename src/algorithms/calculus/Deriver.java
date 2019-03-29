package algorithms.calculus;

import algorithms.Algorithm;
import algorithms.Functions.Function;
import helpers.Shape;
import helpers.exceptions.InvalidOperationException;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import main.Calculator;
import main.Operator;
import main.Parser;
import main.Variable;
import main.Variables;
import main.VectorParser;
import mathobjects.MConst;
import mathobjects.MExpression;
import mathobjects.MFunction;
import mathobjects.MReal;
import mathobjects.MScalar;
import mathobjects.MVector;
import mathobjects.MathObject;
import tree.Node;
import tree.Tree;

public class Deriver extends Algorithm {
	
	MFunction f;
	Variable var;
	MVector dir; //for directional derivatives (df/dn=grad(f)*n where n is a vector)
	MathObject value;
	Node<?> n;
	
	public Deriver() {}
	
	public Deriver(MFunction f) {
		this.f = f;
	}
	
	public Deriver(MFunction f, Variable var, MathObject value) {
		this(f, var);
		this.value = value;
	}
	
	public Deriver(MFunction f, Variable var) {
		this(f);
		this.var = var;
		prepared = true;
	}
	
	public Deriver(MFunction f, MVector dir, MVector value) {
		this(f, dir);
		this.value = value;
	}
	
	public Deriver(MFunction f, MVector dir) {
		this(f);
		this.dir = dir;
		prepared = true;
	}

	@Override
	public MathObject execute() {
		if(!prepared)
			return MReal.NaN();
		MFunction func = null;
		if(dir==null) { //derive w.r.t. single variable
			func = derive(f, var.getName());
		} else {
			String[] params = f.getParameters();
			if(params.length==dir.size()) {
				func = derive(f, params[0]).multiply((MReal) dir.get(0));
				for(int i = 1; i < params.length; i++)
					func.add(derive(f, params[i]).multiply((MReal) dir.get(i)));
			}
		}
		if(value!=null)
			try {
				return func.evaluateAt((value instanceof MVector ? ((MVector) value).elements() : new MathObject[] {value}));
			} catch (TreeException e) {
				Calculator.ioHandler.printException(e);
			}
		return func;
	}

	@Override
	public MathObject execute(MathObject... args) {
		throw new RuntimeException("This method should never be called, please send this stacktrace to the developer.");
	}
	
	@Override
	public MathObject execute(String... args) {
		prepare(args);
		return execute();
	}

	private MFunction derive(MFunction f, String var) {
		for(String s : f.getParameters())
			if(s.equals(var))
				return new MFunction(f.getParameters(), new Tree(derive(f.getTree().getRoot(), var)), f.isDefined());
		return new MFunction(f.getParameters(), new Tree(new Node<MReal>(new MReal(0))), false);
	}
	
	private Node<?> derive(Node<?> n, String var) {
		if(n.data instanceof Variable) {
			if(n.data.equals(var))
				return new Node<MReal>(new MReal(1));
			else
				return new Node<MReal>(new MReal(0));
		}
		if(n.data instanceof MathObject) {
			if(n.data instanceof MFunction)
				return derive(((MFunction) n.data), var).getTree().getRoot();
			return new Node<MReal>(new MReal(0));
		} else if(n.data instanceof Operator) {
			Node<?> node;
			Operator op = (Operator) n.data;
			switch(op) {
			case ADD:
			case SUBTRACT://f(x)+-g(x)=f'(x)+-g'(x)
				node = applyOperator(derive(n.left(), var), op, derive(n.right(), var));
				break;
			case DIVIDE:
				boolean varInTop = dependsOn(n.left(), var);
				boolean varInBottom = dependsOn(n.left(), var);
				if(varInTop && !varInBottom) { //f(x)/a=f'(x)/a
					node = applyOperator(derive(n.left(), var), op, n.right().copy());
				} else if(!varInTop && varInBottom) {//a/f(x)=-a/(f(x))^2*f'(x)
					//First derive a/y [a/y]'=-a/y^2
					Node<Operator> n2 = new Node<Operator>(op);
					n2.left(applyOperator(new Node<MReal>(new MReal(-1)), Operator.MULTIPLY, n.left().copy()));
					n2.right(applyOperator(n.right().copy(), Operator.POWER, new Node<MReal>(new MReal(2))));
					//Chain rule: multiply n2 with the derivative of f(x)
					node = applyOperator(derive(n.right(), var), Operator.MULTIPLY, n2);
				} else if(varInTop && varInBottom) { //f(x)/g(x)=(g(x)*f'(x)-f(x)*g'(x))/(g(x))^2
					//Calculate the numerator of the derivative
					Node<Operator> n2 = new Node<Operator>(Operator.SUBTRACT);
					n2.left(applyOperator(n.right().copy(), Operator.MULTIPLY, derive(n.left(), var)));
					n2.right(applyOperator(n.left().copy(), Operator.MULTIPLY, derive(n.right(), var)));
					//Divide the n2 by the denominator squared.
					node = new Node<Operator>(Operator.DIVIDE);
					node.left(n2);
					node.right(applyOperator(n.right().copy(), Operator.POWER, new Node<MReal>(new MReal(2))));
				} else //[a/b]'=0
					node = new Node<MReal>(new MReal(0));
				break;
			case MULTIPLY:
				boolean varLeft = dependsOn(n.left(), var);
				boolean varRight = dependsOn(n.right(), var);
				if(varLeft && varRight) { //[f(x)*g(x)]'=f'(x)*g(x)+f(x)*g'(x)
					node = new Node<Operator>(Operator.ADD);
					node.right(applyOperator(derive(n.right(), var), Operator.MULTIPLY, n.left().copy()));
					node.left(applyOperator(n.right().copy(), Operator.MULTIPLY, derive(n.left(), var)));
				} else if(varLeft || varRight) {
					node = new Node<Operator>(Operator.MULTIPLY);
					node.right(varLeft ? n.right().copy() : n.left().copy());
					node.left(varLeft ? derive(n.left(), var) : derive(n.right(), var));
				} else
					node = new Node<MReal>(new MReal(0));
				break;
			case NEGATE:
				node = new Node<Operator>(op);
				node.left(derive(n.left(), var));
				break;
			case POWER:
				boolean varLeft2 = dependsOn(n.left(), var);
				boolean varRight2 = dependsOn(n.right(), var);
				if(varLeft2 && !varRight2) { //[f(x)^a]'=a*f'(x)*f(x)^(a-1)
					node = new Node<Operator>(Operator.MULTIPLY);
					node.left(applyOperator(n.right().copy(), Operator.MULTIPLY, derive(n.left(), var)));
					node.right(new Node<Operator>(Operator.POWER));
					node.right().left(n.left().copy());
					if(n.right().data instanceof MScalar) //if a is a scalar, we can subtract 1 directly off it.
						node.right().right(new Node<MScalar>(((MScalar) n.right().data).copy().subtract(1)));
					else
						node.right().right(applyOperator(n.right().copy(), Operator.SUBTRACT, new Node<MReal>(new MReal(1))));
				} else if(!varRight2 && !varLeft2) //[a^b]'=0
					node = new Node<MReal>(new MReal(0));
				else if(!varLeft2 && varRight2) { //[a^f(x)]'=ln(a)*f'(x)*a^f(x)
					Node<?> n2 = applyOperator(derive(n.right(), var), Operator.MULTIPLY, n.copy());
					if(!n.left().data.equals(MConst.E.evaluate())) {
						//Multiply n2 with ln(a)=ln(n.left)
						Node<Function> fnode = new Node<>(Function.LN);
						fnode.left(n.left().copy());
						node = applyOperator(fnode, Operator.MULTIPLY, n2);
					} else
						node = n2;
				} else { //[f(x)^g(x)]'=f(x)^g(x)*g'(x)*ln(f(x))+f(x)^(g(x)-1)*g(x)*f'(x)
					//Left of the + sign
					//multiply n with g'(x)
					Node<Operator> n2 = applyOperator(n.copy(), Operator.MULTIPLY, derive(n.right(), var));
					Node<Function> fnode = new Node<>(Function.LN);
					fnode.left(n.left().copy());
					//Multiply n2 with ln(f(x))
					Node<Operator> left = applyOperator(n2, Operator.MULTIPLY, fnode);
					
					//right of the + sign
					Node<?> n3 = n.copy(); //n=f(x)^g(x)
					//subtract 1 of g(x): f(x)^(g(x)-1)
					n3.right(applyOperator(n3.right(), Operator.SUBTRACT, new Node<MReal>(new MReal(1))));
					//multiply n3 with g(x)
					Node<Operator> n4 = applyOperator(n3, Operator.MULTIPLY, n.right().copy());
					//multiply n4 with f'(x)
					Node<Operator> right = applyOperator(n4, Operator.MULTIPLY, derive(n.left(), var));
					
					//add left and right
					node = applyOperator(left, Operator.ADD, right);
				}
				break;
			case TRANSPOSE:
				node = new Node<Operator>(op);
				node.left(derive(n.left(), var));
				break;
			default:
				throw new InvalidOperationException("Can't derive operator " + op);
			}
			return node;
		} else if(n.data instanceof Function) {
			if(!dependsOn(n.left(), var))
				return new Node<MReal>(new MReal(0));
			Function func = (Function) n.data;
			Node<?> node = null;
			switch(func) {
			case ABS:
				node = applyOperator(n.left().copy(), Operator.DIVIDE, n.copy());
				break;
			case ACOS:
			case ASIN:
				Node<Operator> n2 = applyOperator(new Node<MReal>(new MReal(1)),  Operator.SUBTRACT, 
						applyOperator(n.left().copy(), Operator.POWER, new Node<MReal>(new MReal(2))));
				node = applyOperator(new Node<MReal>(new MReal(func == Function.ACOS ? -1 : 1)), Operator.DIVIDE, applyFunction(Function.SQRT, n2));
				break;
			case ATAN:
				Node<Operator> n3 = applyOperator(new Node<MReal>(new MReal(1)),  Operator.ADD, 
						applyOperator(n.left().copy(), Operator.POWER, new Node<MReal>(new MReal(2))));
				node = applyOperator(new Node<MReal>(new MReal(1)), Operator.DIVIDE, n3);
				break;
			case COS:
				node = applyOperator(new Node<MReal>(new MReal(-1)), Operator.MULTIPLY, applyFunction(Function.SIN, n.left().copy()));
				break;
			case COSD:
				applyOperator(new Node<MReal>(new MReal(-1)), Operator.MULTIPLY, applyFunction(Function.SIND, n.left().copy()));
				break;
			case LN:
				node = applyOperator(new Node<MReal>(new MReal(1)), Operator.DIVIDE, n.left().copy());
				break;
			case LOG:
				node = applyOperator(new Node<MReal>(new MReal(1)), Operator.DIVIDE,
						applyOperator(n.left().copy(), Operator.MULTIPLY,
								applyFunction(Function.LN, new Node<MReal>(new MReal(10)))));
				break;
			case SIN:
				node = applyFunction(Function.COS, n.left().copy());
				break;
			case SIND:
				node = applyFunction(Function.COSD, n.left().copy());
				break;
			case SQRT:
				node = applyOperator(new Node<MReal>(new MReal(1)), Operator.DIVIDE, applyOperator(new Node<MReal>(new MReal(2)), Operator.MULTIPLY, n.copy()));
				break;
			case TAN:
			case TAND:
				node = applyOperator(applyOperator(n.copy(), Operator.POWER, new Node<MReal>(new MReal(2))), Operator.ADD, new Node<MReal>(new MReal(1)));
				break;
			default:
				throw new InvalidOperationException("Can't derive function " + func.toString().toLowerCase());
			}
			return applyOperator(node, Operator.MULTIPLY, derive(n.left(), var));
		}
		throw new RuntimeException("Can't derive " + n.data.toString());
	}
	
	private boolean dependsOn(Node<?> n, String var) {
		if(n.data instanceof Operator) {
			if(n.right() != null)
				return dependsOn(n.right(), var) || dependsOn(n.left(), var);
			else
				return dependsOn(n.left(), var);
		} else if(n.data instanceof Function)
			return dependsOn(n.left(), var);
		else if(n.data instanceof MExpression) {
			return dependsOn(((MExpression) n.data).getTree().getRoot(), var);
		} else if(n.data instanceof Variable)
			return ((Variable) n.data).getName().equals(var);
		return false;
	}
	
	private Node<Operator> applyOperator(Node<?> left, Operator op, Node<?> right) {
		Node<Operator> n = new Node<>(op);
		n.right(right);
		n.left(left);
		return n;
	}
	
	private Node<Function> applyFunction(Function func, Node<?> left) {
		Node<Function> n = new Node<>(func);
		n.left(left);
		return n;
	}
	
	@Override
	protected void prepare(String[] args) {
		if(args.length==0)
			throw new IllegalArgumentException("No arguments found. See 'help derivative' for more info.");
		MathObject obj  = Variables.get(args[0]);
		if(obj == null || !(obj instanceof MFunction))
			throw new IllegalArgumentException("First argument needs to be a function, got " + obj.getClass().getName());
		f = (MFunction) obj;
		String[] params = f.getParameters();
		if(args.length == 1) {
			if(params.length==1) {
				var = new Variable(params[0]);
				prepared = true;
				return;
			} else
				throw new IllegalArgumentException("A second argument is required for a multivariate function. See help for more info.");
		} else {
			if(args[1].trim().startsWith("[")) {
				try {
					dir = (MVector) new VectorParser(args[1]).parse();
					dir = dir.evaluate();
				} catch(ClassCastException e) {
					throw new RuntimeException("Got a matrix as argument for directional derivative, expected a vector.");
				}
			} else {
				for(String param : params) {
					if(param.equals(args[1])) {
						var = new Variable(param);
						break;
					}
				}
			}
			if(args.length==3)
				value = new Parser(args[2]).evaluate();
		}
		prepared = true;
	}
	
	@Override
	public Shape shape(Shape... shapes) {
		if(shapes.length!=1 && shapes.length!=2)
			throw new IllegalArgumentException("Derivative requires 1 or 2 arguments, got " +shapes.length);
		if(shapes[0].dim()!=0) {
			if(shapes.length==2 && shapes[1].dim()!=0)
				throw new ShapeException("Can't derive vector function with respect to a non-scalar, got " + shapes[1]);
			return shapes[0];
		} else
			return shapes[0];
	}
}