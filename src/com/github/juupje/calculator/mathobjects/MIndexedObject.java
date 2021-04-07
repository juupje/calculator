package com.github.juupje.calculator.mathobjects;

import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;

public class MIndexedObject extends MIndexable {
	
	MathObject[] values;
	
	public MIndexedObject(Shape shape, MathObject[] m) {
		int length = 1;
		for(int i = 0; i < shape.dim(); i++)
			length *= shape.get(i);
		if(m.length == length)
			values = m;
		this.shape = shape;
	}
	
	public MIndexedObject(Shape shape) {
		this.shape = shape;
		int length = 1;
		for(int i = 0; i < shape.dim(); i++)
			length *= shape.get(i);
		values = new MathObject[length];
	}
	
	private int indicesToPos(int[] indices) {
		if(indices.length != shape.dim() && !(shape.dim()==0 && indices[0]==0))
			throw new IndexException("Index dimension mismatch (" + indices.length + "D index, " + shape.dim()+"D object)");
		int pos = 0;
		int multiplier = 1;
		for(int i = indices.length -1; i>=0; i--) {
			if(indices[i]>=shape.get(i))
				throw new IndexException("Index (" + Tools.join(", ", indices) + ") out of bounds for shape " + shape);
			else {
				pos += indices[i]*multiplier;
				multiplier *= shape.get(i);
			}
		}
		return pos;
	}
	
	@SuppressWarnings("unused")
	private int[] posToIndices(int pos) {
		if(pos<0 || pos>=values.length)
			throw new IndexException("Position " + pos + " out of range for IndexedObject with " + values.length + " elements");
		int multiplier = values.length;
		int[] index = new int[shape.dim()];
		for(int i = 0; i < index.length; i++) {
			multiplier /= shape.get(i);
			index[i] = (int) pos/multiplier;
			pos = pos % multiplier;
		}
		return index;
	}
	
	@Override
	public MathObject get(int... indices) {
		return values[indicesToPos(indices)];
	}
	
	@Override
	public void set(MathObject m, int... index) {
		values[indicesToPos(index)] = m;
	}
	
	@Override
	public MIndexedObject multiply(MScalar s) {
		for(MathObject m : values)
			m.multiply(s);
		return this;
	}
	
	/**
	 * Negates the object by negating all elements
	 * @return {@code this}
	 */
	@Override
	public MathObject negate() {
		for(MathObject m : values)
			m.negate();
		return this;
	}

	/**
	 * Inverts the indexed object, this operation is not mathematically defined.
	 * @throws InvalidOperationException the inverse of vector is not defined.
	 */
	@Override
	public MIndexedObject invert() {
		throw new InvalidOperationException("Inverse of vector is not defined.");
	}

	
	@Override
	public MIndexedObject copy() {
		MathObject[] copy = new MathObject[values.length];
		for(int i = 0; i < values.length; i++)
			copy[i] = values[i].copy();
		return new MIndexedObject(shape.copy(), copy);
	}  

	@Override
	public MathObject evaluate() {
		MathObject[] copy = new MathObject[values.length];
		for(int i = 0; i < values.length; i++)
			copy[i] = values[i].evaluate();
		return new MIndexedObject(shape.copy(), copy);
	}
	
	public MathObject[] elements() {
		return values;
	}
	
	public MMatrix toMatrix() {
		if(shape.dim()==2) {
			MathObject[][] matrix = new MathObject[shape.get(0)][shape.get(1)];
			int k = 0;
			for(int i = 0; i < matrix.length; i++)
				for(int j = 0; j < matrix[0].length; j++, k++)
					matrix[i][j] = values[k] ;
			return new MMatrix(matrix);
		}
		throw new ShapeException("Cannot interpret object of shape " + shape + " as a matrix");
	}
	
	public MVector toVector() {
		if(shape.dim()==1)
			return new MVector(values);
		else if(shape.dim()==2 && shape.get(0)==1)
			return new MVector(true, values);
			
		throw new ShapeException("Cannot interpret object of shape " + shape + " as a vector");
	}

	@Override
	public Shape shape() {
		return shape;
	}

	@Override
	public boolean isNumeric() {
		for(MathObject m : values)
			if(!m.isNumeric())
				return false;
		return true;
	}
	
	@Override
	public String toString() {
		return Printer.toText(this);
	}

}