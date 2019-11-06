package com.github.juupje.calculator.tree;

import com.github.juupje.calculator.algorithms.Functions.Function;
import com.github.juupje.calculator.helpers.Shape;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;

public class Tree {
	public Node<?> root = null;
	
	public Tree() {}
	public Tree(Node<?> root) {
		this.root = root;
	}
	
	public Node<?> getRoot() {
		return root;
	}
	
	/**
	 * Inserts a new node at the location of <tt>n</tt>.
	 * <tt>n</tt> itself will be shifted to the left or right (see <tt>dir</tt> parameter) child of the newly inserted node.
	 * This method uses the <tt>insert()</tt> method in the <tt>Node<?></tt> class to insert the new node and will call {@link #updateRoot()} after the node is inserted.
	 * @param n the node at the location on which the new Node will be inserted.
	 * @param toBeInserted The node to be inserted.
	 * @param dir the direction in which <tt>n</tt> will be moved down. 0=left, 1=right;
	 * @see Node#insert(Node, int)
	 */
	public void insert(Node<?> n, Node<?> toBeInserted, byte dir) {
		n.insert(toBeInserted, dir);
		updateRoot();
	}
	
	public MathObject evaluateTree() throws TreeException {
		return evaluateNode(root);
	}
	
	protected MathObject evaluateNode(Node<?> n) throws TreeException {
		if(n.isInternal()) {
			if(n.data instanceof Function)
				return ((Function) n.data).evaluate(evaluateNode(n.left()));
			else if(n.data instanceof Operator) {
				if(n.right() != null)
					return ((Operator) n.data).evaluate(evaluateNode(n.left()), evaluateNode(n.right()));
				return ((Operator) n.data).evaluate(evaluateNode(n.left()));
			}else if(n.data instanceof Variable && ((Variable) n.data).get() instanceof MFunction) {
				return ((MFunction) ((Variable) n.data).get()).evaluateAt(((MVector) evaluateNode(n.left())).elements());
			}
		} //else
		if(n.data instanceof Variable)
			return ((Variable) n.data).evaluate();
		else if(n.data instanceof MConst)
			return ((MConst) n.data).evaluate();
		else
			return ((MathObject) n.data).evaluate();
	}
	
	public void replace(Node<?> toBeReplaced, Node<?> newNode) {
		if(toBeReplaced.parent == null) {
			newNode.left(toBeReplaced.left());
			newNode.right(toBeReplaced.right());
			root = newNode;
		} else
			toBeReplaced.swap(newNode);
	}
	
	public Node<?> updateRoot() {
		while(root.parent != null)
			root = root.parent;
		return root;
	}
	
	private void DFS(Node<?> n, DFSTask c) {
		if(n.left() != null)
			DFS(n.left(), c);
		if(n.right() != null)
			DFS(n.right(), c);
		c.accept(n);
	}
	
	public void DFS(DFSTask c) {
		DFS(root, c);
		if(c.usesFlags)
			DFS(resetFlags);
	}
	
	/**
	 * Recursively calculates the shape of the result of {@link #evaluateTree()}.
	 * Note that unknown variables will be assumed to have a scalar shape.
	 * @param n the node on which the DFS will be started/continued.
	 * @return the {@link Shape} of the object returned by {@link #evaluateNode(n)}.
	 * @
	 */
	public Shape getShape(Node<?> n)  {
		if(n.data instanceof Operator) {
			if(n.right != null)
				return ((Operator) n.data).shape(getShape(n.left()), getShape(n.right()));
			return ((Operator) n.data).shape(getShape(n.left()));
		} else if(n.data instanceof MathObject)
			return ((MathObject) n.data).shape();
		else if(n.data instanceof Variable)
			if(((Variable) n.data).get()!=null)
				return ((Variable) n.data).get().shape();
		return Shape.SCALAR; //scalar shape
	}
	
	public Node<?> copy(Node<?> n, java.util.function.Function<Node<?>, Node<?>> func) {
		Node<?> copy = func.apply(n);
		if(n.left() != null)
			copy.left(copy(n.left(), func));
		if(n.right() != null)
			copy.right(copy(n.right(), func));
		return copy;
	}
	
	public Tree copy() {
		return copy(new java.util.function.Function<Node<?>, Node<?>>() {
			@Override
			public Node<?> apply(Node<?> n) {
				return new Node<Object>(n.data);
			}
		});
	}
	
	public Tree copy(java.util.function.Function<Node<?>, Node<?>> func) {
		return new Tree(copy(root, func));
	}
	
	private static final DFSTask resetFlags = new DFSTask(false) {

		@Override
		public void accept(Node<?> t) {
			t.flags = 0;
		}
		
	};
}