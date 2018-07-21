package tree;

import algorithms.Functions.Function;
import main.Operator;
import main.Variable;
import mathobjects.MConst;
import mathobjects.MathObject;

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
	public void insert(Node<?> n, Node<?> toBeInserted, int dir) {
		n.insert(toBeInserted, dir);
		updateRoot();
	}
	
	public MathObject evaluateTree() {
		return evaluateNode(root);
	}
	
	private MathObject evaluateNode(Node<?> n) {
		if(n.isInternal()) {
			if(n.data instanceof Function)
				return ((Function) n.data).evaluate(evaluateNode(n.left()));
			else
				return ((Operator) n.data).evaluate(evaluateNode(n.left()), evaluateNode(n.right()));
		} //else
		if(n.data instanceof Variable)
			return ((Variable) n.data).evaluate();
		else if(n.data instanceof MConst)
			return ((MConst) n.data).evaluate();
		else
			return ((MathObject) n.data).evaluate();
	}
	
	public Node<?> updateRoot() {
		while(root.parent != null)
			root = root.parent;
		return root;
	}
}
