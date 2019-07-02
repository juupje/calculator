package helpers;

import helpers.exceptions.ShapeException;

/**
 * A class for storing information about the shape of a mathematical object.
 * The shape is stored as an integer array whose values represent the size of the object.
 * A vector of size n will be saves as {@code (n)}, a Matrix of size {@code n x m} will be saved as (n,m) etc.
 * A scalar value will be saved as either {@code (0)} or a {@code null} value.
 */
public class Shape {
	public static final Shape SCALAR = new Shape();
	
	int[] shape;
	int dimension;
	
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
	
	/**
	 * Returns the {@code shape} array.
	 * @return an {@code this.shape}.
	 */
	public int[] asArray() {
		return shape;
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
	
	public Shape transpose() {
		if(shape.length>2)
			throw new ShapeException("Can't transpose shape rin" + toString());
		if(shape.length==2) {
			int temp = shape[0];
			shape[0]=shape[1];
			shape[1] = temp;
		} else if(shape.length==1)
			shape = new int[] {1, shape[0]};
		return this;
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
		if(a.dim()!=1)
			return a;
		throw new ShapeException("Objects of shape " + a + " cannot be inverted.");
	}
	
	public static Shape transpose(Shape a) {
		return a.copy().transpose();
	}
	
	public int rows() {
		return shape[0];
	}

	public int cols() {
		return shape[1];
	}
	
	public boolean isSquare() {
		int i = shape[0];
		for(int j = 1; j < shape.length; j++)
			if(shape[j] != i)
				return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "("+Tools.join(", ", shape)+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || obj instanceof Number)
			return dim()==0;
		if(obj instanceof Shape && ((Shape) obj).dim() == dim()) {
			int[] other = ((Shape) obj).asArray();
			for(int i = 0; i < shape.length; i++)
				if(other[i] != shape[i])
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