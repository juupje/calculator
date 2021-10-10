package com.github.juupje.calculator.tree;

import com.github.juupje.calculator.algorithms.functions.Function;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MathObject;

public class Node<T> {
	
	public static final byte LEFT = 0, RIGHT = 1;
	
	public Node<?> left = null, right = null, parent = null;
	public T data;
	public int flags = 0;
	
	public Node(T data) {
		this.data = data;
	}
	
	public boolean isNumeric() {
		return data instanceof MConst || (data instanceof MathObject && ((MathObject) data).isNumeric());
	}
	
	public MathObject asMathObject() {
		if(!isNumeric()) return MReal.NaN();
		return data instanceof MConst ? ((MConst) data).evaluate() : ((MathObject) data).evaluate();
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
	public void insert(Node<?> n, byte dir) {
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
		} else {
			if(n.right() != null)
				throw new RuntimeException("Trying to insert a node which already has a right child.");
			n.right(this);
		}
	}
	
	/**
	 * Replaces this {@code Node} with {@code n}. This conserves children of {@code n},
	 * which means that the children of this {@code Node} (if any) will be discarded. 
	 * @param n the {@code Node} with which this one will be replaced.
	 */
	public void replace(Node<?> n) {
		if (parent != null) {
			if (parent.left() == this)
				parent.left(n);
			else
				parent.right(n);
		} else {
			throw new TreeException("Cannot replace root of tree");
		}
		if(right != null || left != null)
			Calculator.ioHandler.debug("ATTENTION: node was replaced and it's children discarded, old: " + left.parent + "[" + left + ", " + right + "] " + 
		" new: " + n + (n != null ? "[" + n.left + ", " + n.right + "]": ""));
	}
	
	/**
	 * Replaces this {@code Node} with {@code n}. This conserves children of this {@code Node},
	 * which means that the children of {@code n} (if any) will be discarded. 
	 * @param n the {@code Node} with which this one will be swapped.
	 */
	public void swap(Node<?> n) {
		if (parent != null) {
			if (parent.left() == this)
				parent.left(n);
			else
				parent.right(n);
		}
		n.left(left);
		n.right(right); 
	}
	
	public void switchChildren() {
		Node<?> temp = left;
		left = right;
		right = temp;
	}
	
	public void shiftUp() {
		parent.replace(this);
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
	
	public void setData(T data) {
		this.data = data;
	}
	
	public Node<?> copy() {
		Node<?> n;
		if(data instanceof MathObject)
			n = new Node<MathObject>(((MathObject) data).copy());
		else
			n = new Node<T>(data);
		if(left != null)
			n.left(left.copy());
		if(right != null)
			n.right(right.copy());
		return n;
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
	
	public String toHTMLLabel() {
		return toString() + "<br/><font point-size=\"10\">"+ hashCode() + "</font>";
	}
}
