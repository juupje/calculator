package mathobjects;

import helpers.Printer;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Operator;
import main.Parser;
import tree.Node;
import tree.Tree;

public class MExpression implements MathObject {

	Tree tree;
	
	public MExpression(Tree tr) {
		this.tree = tr;
	}
	
	public MExpression(String str) {
		try {
			tree = new Parser(str).getTree();
		} catch (UnexpectedCharacterException e) {
			e.printStackTrace();
		}
	}
	
	public MExpression add(MScalar other) {
		Node<Operator> n = new Node<Operator>(Operator.ADD);
		n.right(new Node<MScalar>(other));
		tree.insert(tree.getRoot(), n, Node.LEFT);
		return this;
	}
	
	public MExpression add(MExpression other) {
		Node<Operator> n = new Node<Operator>(Operator.ADD);
		n.right(other.getTree().copy().getRoot());
		tree.insert(tree.getRoot(), n, Node.LEFT);
		return this;
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
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Printer.toText(this);
	}

	public Tree getTree() {
		return tree;
	}
}
