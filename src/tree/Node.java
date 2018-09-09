package tree;

import algorithms.Functions.Function;
import main.Operator;
import main.Variable;
import mathobjects.MConst;
import mathobjects.MScalar;

public class Node<T> {
	private Node<?> left = null, right = null;
	public Node<?> parent = null;
	public T data;
	
	public Node(T data) {
		this.data = data;
	}
	
	public boolean isNumeric() {
		return data instanceof MConst || data instanceof MScalar;
	}
	
	public boolean isInternal() {
		return !(left == null && right == null);
	}
	
	/**
	 * Inserts a new node at the location of this one.
	 * The current node will be shifted to the left or right (see <tt>dir</tt> parameter) child of the newly inserted node.
	 * Note that this method has no knowledge of the structure of the tree, meaning that for example the root-node of the tree might be changed.
	 * In order to prevent corrupted trees, please use {@link Tree#insert(Node, Node, int)}.
	 * @param n The node to be inserted.
	 * @param dir the direction in which <tt>n</tt> will be moved down. 0=left, 1=right;
	 */
	public void insert(Node<?> n, int dir) {
		if(parent != null) {
			if(parent.left() == this)
				parent.left(n);
			else
				parent.right(n);
		}
		if(dir == 0) {//left
			if(n.left() != null)
				throw new RuntimeException("Trying to insert a node which already has a left child.");
			n.left(this);
		} else
			if(n.right() != null)
				throw new RuntimeException("Trying to insert a node which already has a right child.");
			n.right(this);
	}
	
	public void replace(Node<?> n) {
		if(parent.left() == this)
			parent.left(n);
		else
			parent.right(n);
		n.left(left);
		n.right(right);
	}
	
	public void left(Node<?> l) {
		if(l != null)
			l.parent = this;
		left = l;
	}
	
	public void right(Node<?> r) {
		if(r != null)
			r.parent = this;
		right = r;
	}
	
	public Node<?> left() {
		return left;
	}
	
	public Node<?> right() {
		return right;
	}
	
	public T getData() {
		return data;
	}
	
	@Override
	public String toString() {
		if(data instanceof Variable)
			return ((Variable) data).toString();
		if(data instanceof MConst)
			return ((MConst) data).name();
		else if(data instanceof Operator)
			return ((Operator) data).toString();
		else if(data instanceof Function)
			return ((Function) data).toString();
		return data.toString();
	}
}
