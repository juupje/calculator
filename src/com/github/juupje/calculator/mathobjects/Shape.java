package com.github.juupje.calculator.mathobjects;

import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;

/**
 * A class for storing information about the shape of a mathematical object.
 * The shape is stored as an integer array whose values represent the size of the object.
 * A vector of size n will be saves as {@code (n)}, a Matrix of size {@code n x m} will be saved as (n,m) etc.
 * A scalar value will be saved as either {@code (0)} or a {@code null} value.
 */
public final class Shape {
	public static final Shape SCALAR = new Shape();
	
	private int[] shape;
	private int dimension;
	
	/**
	 * Constructs a new shape of the given integers.
	 * If no integers are given the {@code shape} array will be set to {@code null}.
	 * Any trailing zeros will be removed and not included in the array or dimension.
	 * @param shape an array of {@code int}s
	 */
	public Shape(int... shape) {
		int toBeRemoved = 0;
		for(int i = shape.length-1; i>=1; i--)
			if(shape[i]==0) toBeRemoved++;
		if(toBeRemoved==0)
			this.shape = shape == null ? new int[0] : shape;
		else {
			this.shape = new int[shape.length-toBeRemoved];
			for(int i = 0; i < shape.length-toBeRemoved; i++)
				this.shape[i] = shape[i];
		}
		
		dimension = 0;
		for(int i : shape)
			if(i!=1) dimension++;
	}
	
	public Shape(int i, Shape s) {
		if(s.dim()==0) {
			shape = new int[] {i};
			dimension = 1;
		} else {
			shape = new int[1+s.size()];
			shape[0] = i;
			for(int j = 1; j < shape.length; j++)
				shape[j] = s.get(j-1);
			dimension = 1+s.dim();
		}
		
	}
	
	public Shape copy() {
		int[] s2 = new int[shape.length];
		for(int i = 0; i < s2.length; i++)
			s2[i]=shape[i];
		return new Shape(s2);
	}
	
	/**
	 * Returns the length of this shape. This can be seen as the Dimension of the object where:
	 * <ul>
	 * <li>0 represents a scalar</li>
	 * <li>1 represents a vector</li>
	 * <li>2 represents a matrix</li>
	 * <li>{@code n>2} represents a tensor of dimension n</li>
	 * </ul>
	 * @return {@code this.shape.length}
	 */
	public int dim() {
		return dimension;
	}
	
	public int size() {
		return shape.length;
	}
	
	/**
	 * If the dimension of this Shape is 2, a new Shape with rows/columns switched is returned.
	 * If the dimension is 1, a new shape with 1 row and columns equal to {@code this.rows()} is returned.
	 * Otherwise, an exception is thrown.
	 * 
	 * @return A new, transposed Shape
	 */
	public Shape transpose() {
		if(shape.length==2)
			return new Shape(shape[1], shape[0]);
		else if(shape.length==1)
			return new Shape(1, shape[0]);
		else
			throw new ShapeException("Can't transpose shape " + toString());
	}
	
	/**
	 * Returns the i-th element of the {@code shape} array, or 1 if i is greater than the size of this shape.
	 * @param i the index of the element to be returned.
	 * @return {@code i>shape.length ? 1 : shape[i]}
	 */
	public int get(int i) {
		if(i>=shape.length)
			return 1;
		return shape[i];
	}
	
	public static Shape multiply(Shape a, Shape b)  {
		if(b.dim()==0) //b is scalar
			return a;
		if(a.dim()==0) // a is a scalar
			return b;
		//Now neither b nor a is a scalar
		if(a.dim()==1) {//vector: matrix of shape (n x 1)
			if(b.dim()==1 && b.equals(a)) //scalar product
				return Shape.SCALAR;
			if(b.dim()==2) //matrix
				if(b.get(0)==1) //b needs the shape (1 x m)
					return new Shape(a.get(0), b.get(1));
		} else if(a.dim()==2) {
			if(b.get(0) == a.get(1))
				return new Shape(a.get(0), b.get(1));
		}
		throw new ShapeException("Can't multiply shape " + a + " with " + b);
	}
	
	public static Shape divide(Shape a, Shape b)  {
		if(b.dim()==0)
			return a;
		throw new ShapeException("Division is only defined for quotients of shape (0)");
	}
	
	public static Shape add(Shape a, Shape b)  {
		if(b.equals(a))
			return a;
		throw new ShapeException("Can't add shapes " + a + " and " + b + " (shapes need to be equal).");
	}
	
	public static Shape subtract(Shape a, Shape b)  {
		if(b.equals(a))
			return a;
		throw new ShapeException("Can't subtract shape " + b + " off " + a + " (shapes need to be equal).");
	}
	
	public static Shape mod(Shape a, Shape b)  {
		if(a.dim()==0 && b.dim()==0)
			return a;
		throw new ShapeException("Modulo operator is only defined for scalars. Got shapes " + a + " and " + b);
	}
	
	public static Shape power(Shape a, Shape b)  {
		if(b.dim()==0)
			if(a.dim()==0 || a.dim()==2)
				return a;
		throw new ShapeException("Power operator is only defined for scalars and matrices, operator needs to be scalar valued. Got shapes " + a + " and " + b);
	}
	
	public static Shape negate(Shape a) {
		return a;
	}
	
	public static Shape invert(Shape a)  {
		if(a.isScalar() || (a.dim()==2 && a.isSquare()))
			return a;
		throw new ShapeException("Objects of shape " + a + " cannot be inverted.");
	}
	
	public int rows() {
		if(shape.length>0)
			return shape[0];
		else
			return 1;
	}

	public int cols() {
		if(shape.length>1)
			return shape[1];
		else return 1;
	}
	
	public boolean isSquare() {
		int i = shape[0];
		for(int j = 1; j < shape.length; j++)
			if(shape[j] != i)
				return false;
		return true;
	}
	
	public boolean isScalar() {
		return dimension == 0;
	}
	
	@Override
	public String toString() {
		return "("+Tools.join(", ", shape)+")";
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || obj instanceof Number)
			return isScalar();
		if(obj instanceof Shape && ((Shape) obj).dim() == dim()) {
			Shape other = (Shape) obj;
			for(int i = 0; i < shape.length; i++)
				if(other.get(i) != shape[i])
					return false;
			return true;
		}
		return false;
	}

	public Shape subShape(int i) {
		if(i>=shape.length)
			throw new IndexOutOfBoundsException("Dimension " + i + " is greater than the size of this shape: " + size());
		int[] s = new int[size()-i];
		for(int j = 0; j < s.length; j++)
			s[j]=shape[j+i];
		return new Shape(s);
	}
}