package mathobjects;

import java.util.HashSet;

import helpers.Printer;
import helpers.Shape;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Calculator;
import main.Operator;
import main.Parser;
import main.Variable;
import tree.DFSTask;
import tree.Node;
import tree.Tree;

public class MExpression implements MathObject {

	Tree tree;
	Shape s;
	
	public MExpression(Tree tr) {
		this.tree = tr;
	}
	
	public MExpression(String str) {
		try {
			tree = new Parser(str).getTree();
		} catch (UnexpectedCharacterException e) {
			Calculator.errorHandler.handle(e);
		}
	}
	
	public MExpression add(MScalar other) {
		join(Operator.ADD, new Node<MScalar>(other));
		return this;
	}
	
	public MExpression add(MExpression other) {
		join(Operator.ADD, other.getTree().getRoot());
		return this;
	}
	
	public MExpression subtract(MScalar other) {
		join(Operator.SUBTRACT, new Node<MScalar>(other));
		return this;
	}
	
	public MExpression subtract(MExpression other) {
		join(Operator.SUBTRACT, other.getTree().getRoot());
		return this;
	}
	
	public MExpression multiply(MScalar other) {
		join(Operator.MULTIPLY, new Node<MScalar>(other));
		return this;
	}
	
	public MExpression multiply(MExpression other) {
		join(Operator.MULTIPLY, other.getTree().getRoot());
		return this;
	}
	
	public MExpression divide(MScalar other) {
		join(Operator.DIVIDE, new Node<MScalar>(other));
		return this;
	}
	
	public MExpression divide(MExpression other) {
		join(Operator.DIVIDE, other.getTree().getRoot());
		return this;
	}
	
	private void join(Operator op, Node<?> n) {
		Node<Operator> node = new Node<Operator>(op);
		node.right(n);
		tree.insert(tree.getRoot(), node, Node.LEFT);
	}
	
	@Override
	public MathObject negate() {
		tree.insert(tree.getRoot(), new Node<Operator>(Operator.NEGATE), Node.LEFT);
		return this;
	}

	@Override
	public MathObject invert() {
		tree.insert(tree.getRoot(), new Node<Operator>(Operator.INVERT), Node.LEFT);
		return this;
	}

	@Override
	public MathObject copy() {
		return new MExpression(tree.copy());
	}

	@Override
	public MathObject evaluate() {
		try {
			return tree.evaluateTree();
		} catch (TreeException e) {
			Calculator.errorHandler.handle(e);
			return null;
		}
	}
	
	@Override
	public boolean isNumeric() {
		return false;
	}
	
	@Override
	public Shape shape()  {
		if(s == null)
			s = tree.getShape(tree.getRoot());
		return s;
	}
	
	public HashSet<Variable> getDependencies() {
		HashSet<Variable> dependencies = new HashSet<>();
		tree.DFS(new DFSTask(false) {
			@Override
			public void accept(Node<?> n) {
			if(n.data instanceof Variable)
				dependencies.add((Variable) n.data);
			else if(n.data instanceof MVector)
				for(MathObject mo : ((MVector) n.data).elements())
					if(mo instanceof MExpression)
						dependencies.addAll(((MExpression) mo).getDependencies());
			}
		});
		return dependencies;
	}
	
	@Override
	public String toString() {
		return Printer.toText(this);
	}

	public Tree getTree() {
		return tree;
	}
}