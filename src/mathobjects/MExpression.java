package mathobjects;

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
	
	@Override
	public MathObject negate() {
		tree.insert(tree.getRoot(), new Node<Operator>(Operator.NEGATE), 0);
		return this;
	}

	@Override
	public MathObject invert() {
		tree.insert(tree.getRoot(), new Node<Operator>(Operator.INVERT), 0);
		return this;
	}

	@Override
	public MathObject copy() {
		return new MExpression(tree);
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
		return null;
	}

	public Tree getTree() {
		return tree;
	}
}
