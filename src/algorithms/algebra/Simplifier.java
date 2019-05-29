package algorithms.algebra;

import algorithms.Algorithm;
import helpers.Shape;
import main.Operator;
import mathobjects.MComplex;
import mathobjects.MConst;
import mathobjects.MExpression;
import mathobjects.MFunction;
import mathobjects.MMatrix;
import mathobjects.MReal;
import mathobjects.MVector;
import mathobjects.MathObject;
import tree.DFSTask;
import tree.Node;
import tree.Tree;

public class Simplifier extends Algorithm {
	
	MFunction func;
	MExpression expr;
	
	public Simplifier(MExpression expr) {
		this.expr = expr;
		prepared =true;
	}
	
	public Simplifier(MFunction func) {
		this.func = func;
		prepared =true;
	}
	
	public Simplifier() {}
	
	static DFSTask numericOperants = new DFSTask() {
		@Override
		public void accept(Node<?> n) {
			if(n.data instanceof Operator) {
				switch((Operator) n.data) {
				case ADD:
				case SUBTRACT:
				case DIVIDE:
				case MULTIPLY:
				case POWER:
				case MOD:
					if(n.left.isNumeric() && n.right.isNumeric())
						n.replace(new Node<MathObject>(((Operator) n.data).evaluate(n.left.asMathObject(), n.right.asMathObject())));
					break;
				case NEGATE:
				case INVERT:
					if(n.left.isNumeric())
						n.replace(new Node<MathObject>(((Operator) n.data).evaluate(n.left.asMathObject())));
					break;
				case CONJUGATE:
					if(n.left.data instanceof MReal || (n.left.data instanceof MComplex && ((MComplex) n.left.data).imag()==0))
						n.left.shiftUp();
				default:
					break;					
				}
			}
		}
	};
	
	static DFSTask simplifyOperators = new DFSTask() {
		@Override
		public void accept(Node<?> n) {
			//Whenever this gets called, it is not possible that both operants are numeric
			if(n.data instanceof Operator) {
				switch((Operator) n.data) {
				case ADD:
				case SUBTRACT:
					if(n.left.data.equals(0)) n.right.shiftUp();
					else if(n.right.data.equals(0)) n.left.shiftUp();
					break;
				case MULTIPLY:
					if(n.left.data.equals(0) || n.right.data.equals(0)) n.replace(new Node<MReal>(new MReal(0)));
					else if(n.left.data.equals(1)) n.right.shiftUp();
					else if(n.right.data.equals(1)) n.left.shiftUp();
					break;
				case DIVIDE:
					if(n.left.data.equals(0)) n.replace(new Node<MReal>(new MReal(0)));
					else if(n.right.data.equals(0)) n.replace(new Node<MReal>(MReal.NaN()));
					else if(n.right.data.equals(1)) n.left.shiftUp();
					break;
				case POWER:
					if(n.left.data.equals(0) || n.left.data.equals(1) || n.right.data.equals(1)) n.left.shiftUp();
					else if(n.right.data.equals(0)) n.replace(new Node<MReal>(new MReal(1)));
					break;
				case TRANSPOSE:
					if(n.left.data.equals(Operator.TRANSPOSE)) n.replace(n.left.left);
					else if(n.left.data instanceof MVector) ((MVector) n.left.data).transpose();
					else if(n.left.data instanceof MMatrix) ((MMatrix) n.left.data).transpose();
					else if(n.left.isNumeric())
						if(n.left.data instanceof MConst || ((MathObject) n.left.data).shape().equals(null)) n.left.shiftUp();
					break;
				case NEGATE:
					if(n.left.data.equals(0)) n.left.shiftUp();
					break;
				case CONJUGATE:
					if(n.left.data.equals(Operator.CONJUGATE)) n.replace(n.left.left);
					break;
				case ELEMENT:
					if((n.left.data instanceof MVector || n.left.data instanceof MMatrix) && n.right.isNumeric())
						try {
							n.replace(new Node<MathObject>(Operator.ELEMENT.evaluate((MathObject)n.left.data, (MathObject) n.right.data)));
						} catch(Exception e) {}
					break;
				default:
					break;					
				}
			}
		}
	};

	public Tree simplify(Tree tr) {
		tr.DFS(numericOperants);
		tr.DFS(simplifyOperators);
		return tr;
		/**
		 * Idea:
		 * Simplify functions in 4 steps:
		 * 1: process all operations containing scalars only.
		 * 2: remove all unnecessary operations (adding/subtracting 0, multiplying/dividing by 1 or 0, raising to power 0 or 1 etc.)
		 * 3: reorder the tree in such a way that numbers are moved to right and variables to the left
		 * 4: repeat step 1
		 */
	}

	@Override
	public MathObject execute() {
		if(!prepared) return null;
		return func != null ? new MFunction(func.getParameters(), simplify(func.getTree().copy()), func.isDefined()) : new MExpression(simplify(expr.getTree())) ;
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	public void prepare(MathObject[] args) {
		if(args.length != 1)
			throw new IllegalArgumentException("Simplifier requires 1 argument, got " +args.length);
		if(args[0] instanceof MFunction)
			func = (MFunction) args[0];
		else if(args[0] instanceof MExpression)
			expr = (MExpression) args[0];
		else 
			throw new IllegalArgumentException("First argument needs to be a function or an expression, got " + args[0].getClass().getName());
		prepared = true;
		
	}

	@Override
	public Shape shape(Shape... shapes) {
		if(shapes.length != 1)
			throw new IllegalArgumentException("Simplifier requires 1 argument, got " +shapes.length);
		return shapes[0];
	}
}
