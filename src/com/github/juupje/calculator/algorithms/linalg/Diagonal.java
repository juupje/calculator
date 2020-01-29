package com.github.juupje.calculator.algorithms.linalg;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class Diagonal extends Algorithm {

	MVector v;
	MMatrix m;
	int index = 0;
	Shape s;
	MScalar obj;
	
	public Diagonal(MathObject... mo) {
		for(MathObject m : mo)
			if(!(m instanceof MScalar || m instanceof MVector || (m instanceof MMatrix && m.shape().isSquare())))
				throw new IllegalArgumentException("Arguments need to be either scalars or square matrices, got " + m);
		v = new MVector(mo);
	}
	
	public Diagonal(MMatrix m) {
		this.m = m;
		prepared = true;
	}
	
	public Diagonal() {}

	@Override
	public MathObject execute() {
		int rowshift = index < 0 ? -index : 0;
		int colshift = index > 0 ? index : 0;
		if (m != null) { // a matrix is given: extract the diagonal
			if (colshift > m.shape().cols() || rowshift > m.shape().rows())
				return new MVector(new Shape(0));
			v = new MVector(Math.min(m.shape().cols() - colshift, m.shape().rows() - rowshift));
			for (int i = 0; i < v.size(); i++)
				v.set(i, m.get(i + rowshift, i + colshift));
			return v;
		}
		if (v != null) { // fill a matrix with this MVector on its diagonal
			int size = 0;
			for(MathObject mo : v.elements()) {
				if(mo instanceof MScalar)
					size+=1;
				else
					size += mo.shape().rows();
			}
			MMatrix matrix = MMatrix.zeros(size+rowshift, size+colshift);
			int index = 0;
			for(int i = 0; i < v.size(); i++) {
				if(v.get(i) instanceof MScalar)
					matrix.set(index+rowshift, (index++)+colshift, v.get(i));
				else if(v.get(i) instanceof MVector) {
					MathObject[] m = ((MVector) v.get(i)).elements();
					for(int j = 0; j < m.length; j++)
						matrix.set(index+rowshift+j, index+colshift+j, m[j]);
					index += m.length;
				} else {
					MathObject[][] m = ((MMatrix) v.get(i)).elements();
					for(int j = 0; j < m.length; j++)
						for(int k = 0; k < m[0].length; k++)
							matrix.set(index+rowshift+j, index+colshift+k, m[j][k]);
					index += m.length;
				}
			}
			return matrix;
		}
		// s != null
		m = new MMatrix(s);
		for (int i = 0; i < m.shape().rows(); i++) {
			for (int j = 0; j < m.shape().cols(); j++) {
				if (j - colshift == i - rowshift)
					m.set(i, j, obj.copy());
				else
					m.set(i, j, new MReal(0));
			}
		}
		return m;
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}

	@Override
	protected void prepare(MathObject[] args) {
		m = null;
		v = null;
		index = 0;
		s = null;
		obj = null;
		if ((args.length == 1 || args.length == 2) && args[0] instanceof MVector) {
			v = (MVector) args[0];
			for(MathObject mo : v.elements())
				if(!(mo instanceof MScalar || mo instanceof MVector || (mo instanceof MMatrix && mo.shape().isSquare())))
					throw new IllegalArgumentException("Diagonal entries can only be scalars, vectors or square matrices, got " + mo);
		} else if ((args.length == 1 || args.length == 2) && args[0] instanceof MMatrix) {
			m = (MMatrix) args[0];
		} else if ((args.length == 3 || args.length == 4) && args[0] instanceof MScalar && args[1] instanceof MReal
				&& args[2] instanceof MReal) {
			MReal a = (MReal) args[1], b = (MReal) args[2];
			if (a.isInteger() && a.getValue() > 0 && b.isInteger() && b.getValue() > 0) {
				obj = (MScalar) args[0];
				s = new Shape((int) ((MReal) args[1]).getValue(), (int) ((MReal) args[2]).getValue());
			} else
				throw new IllegalArgumentException(
						"Argument 2 and 3 need to be positive, non-zero integers, got " + a + " and " + b);
		} else
			throw new IllegalArgumentException(
					"Diag is not applicable for the given objects, " + argTypesToString(args) + ". See help(diag).");
		if (args.length == 2) {
			if (args[1] instanceof MReal && ((MReal) args[1]).isInteger())
				index = (int) ((MReal) args[1]).getValue();
			else
				throw new IllegalArgumentException("Argument 2 needs to be an integer, got " + args[1]);
		} else if (args.length == 4) {
			if (args[3] instanceof MReal && ((MReal) args[3]).isInteger())
				index = (int) ((MReal) args[3]).getValue();
			else
				throw new IllegalArgumentException("Argument 4 needs to be an integer, got " + args[3]);
		}
		prepared = true;
	}

	@Override
	public Shape shape(Shape... shapes) {
		return new Shape(0, 0);
	}

}
