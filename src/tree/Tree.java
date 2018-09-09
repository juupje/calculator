package tree;

import java.util.function.Consumer;

import algorithms.Functions.Function;
import helpers.exceptions.TreeException;
import main.Operator;
import main.Variable;
import mathobjects.MConst;
import mathobjects.MFunction;
import mathobjects.MVector;
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
	
	public MathObject evaluateTree() throws TreeException {
		return evaluateNode(root);
	}
	
	protected MathObject evaluateNode(Node<?> n) throws TreeException {
		if(n.isInternal()) {
			if(n.data instanceof Function)
				return ((Function) n.data).evaluate(evaluateNode(n.left()));
			else if(n.data instanceof Operator)
				return ((Operator) n.data).evaluate(evaluateNode(n.left()), evaluateNode(n.right()));
			else if(n.data instanceof Variable && ((Variable) n.data).get() instanceof MFunction) {
				return ((MFunction) ((Variable) n.data).get()).evaluateAt(((MVector) ((MVector) n.left().data).evaluate()).elements());
			}
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
	
	public void DFS(Node<?> n, Consumer<Node<?>> c) {
		if(n.left() != null)
			DFS(n.left(), c);
		if(n.right() != null)
			DFS(n.right(), c);
		c.accept(n);
	}
	
	public abstract class DFSTask implements Consumer<Node<?>>{
		Object[] obj;
		public DFSTask(Object... obj) {
			this.obj = obj;
		}
	}
	
	public Node<?> copy(Node<?> n, java.util.function.Function<Node<?>, Node<?>> func) {
		Node<?> copy = func.apply(n);
		if(n.left() != null)
			copy.left(copy(n.left(), func));
		if(n.right() != null)
			copy.right(copy(n.right(), func));
		return copy;
	}
	
	public Tree copy(java.util.function.Function<Node<?>, Node<?>> func) {
		return new Tree(copy(root, func));
	}

}
