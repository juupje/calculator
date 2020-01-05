package com.github.juupje.calculator.mathobjects;

import java.util.HashSet;

import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.Shape;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.tree.DFSTask;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

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
	
	public MExpression addOperationRight(Operator op, MathObject other) {
		join(op, new Node<MathObject>(other), Node.RIGHT);
		return this;
	}
	
	public MExpression addOperation(Operator op, MathObject other) {
		join(op, new Node<MathObject>(other), Node.LEFT);
		return this;
	}
	
	public MExpression addOperation(Operator op, MExpression other) {
		join(op, other.getTree().getRoot(), Node.LEFT);
		return this;
	}
	
	private void join(Operator op, Node<?> n, byte dir) {
		Node<Operator> node = new Node<Operator>(op);
		if(dir == Node.LEFT)
			node.right(n);
		else
			node.left(n);
		tree.insert(tree.getRoot(), node, dir);
	}
	
	/**
	 * Inserts {@link Operator#NEGATE} at the root of the tree.
	 */
	@Override
	public MExpression negate() {
		tree.insert(tree.getRoot(), new Node<Operator>(Operator.NEGATE), Node.LEFT);
		return this;
	}

	/**
	 * Inserts {@link Operator#INVERT} at the root of the tree.
	 */
	@Override
	public MExpression invert() {
		tree.insert(tree.getRoot(), new Node<Operator>(Operator.INVERT), Node.LEFT);
		return this;
	}

	/**
	 * Copies the tree of this expression and creates a new expression with that tree.
	 * @return a deep copy of this expression using {@code new MExpression(tree.copy()}
	 * @see Tree#copy()
	 */
	@Override
	public MExpression copy() {
		return new MExpression(tree.copy());
	}

	/**
	 * Evaluates the tree and returns the (numeric) result.
	 * @return the result of {@code tree.evaluateTree();}
	 * @see Tree#evaluateTree()
	 */
	@Override
	public MathObject evaluate() {
		try {
			return tree.evaluateTree();
		} catch (TreeException e) {
			Calculator.errorHandler.handle(e);
			return null;
		}
	}
	
	/**
	 * Checks if this object is numeric (which always false for expression)
	 * @return false
	 */
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