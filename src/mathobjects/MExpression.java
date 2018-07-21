package mathobjects;

import helpers.Parser;
import helpers.exceptions.UnexpectedCharacterException;
import main.Operator;
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
		return tree.evaluateTree();
	}
	
	@Override
	public String toString() {
		return null;
	}

	public Tree getTree() {
		return tree;
	}
}
