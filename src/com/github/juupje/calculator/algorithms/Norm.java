package com.github.juupje.calculator.algorithms;

import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class Norm extends Algorithm {
	
	enum NormType {NORM1, NORM2, NORM_INF, NORM_FRO};
	
	NormType type;
	MathObject mo;
	
	public static MReal eucl(MVector v) {
		try {
			double d = 0;
			for(int i = 0; i < v.size(); i++) {
				double d1 = ((MScalar) v.get(i)).abs();
				d += d1*d1;
			}
			return new MReal(Math.sqrt(d));
		} catch(ClassCastException e) {
			throw new IllegalArgumentException("Cannot calculate norm of non-numeric vector.");
		}
	}
	
	public static MReal eucl(MMatrix m) {
		try {
			double d = 0;
			int rows = m.shape().rows();
			int cols = m.shape().cols();
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < cols; j++) {
					double d1 = ((MScalar) m.get(i,j)).abs();
					d += d1*d1;
				}
			}
			return new MReal(Math.sqrt(d));
		} catch(ClassCastException e) {
			throw new IllegalArgumentException("Cannot calculate norm of non-numeric matrix.");
		}
	}

	@Override
	public MReal execute() {
		if(!prepared) return null;
		if(mo instanceof MVector) {
			MVector v = (MVector) mo;
			switch(type) {
			case NORM2:
			case NORM_FRO:
				return eucl(v);
			case NORM1:
				double d = 0;
				for(int i = 0; i < v.size(); i++)
					d += ((MScalar) v.get(i).evaluate()).abs();
				return new MReal(d);
			case NORM_INF:
				double d1 = ((MScalar) v.get(0).evaluate()).abs();
				for(int i = 1; i < v.size(); i++) {
					double d2 = ((MScalar) v.get(i).evaluate()).abs();
					if(d2 > d1)
						d1 = d2;
				}
				return new MReal(d1);
			}
		} else {
			MMatrix m = (MMatrix) mo;
			switch(type) {
			case NORM2:
				throw new RuntimeException("2-Norm of matrices is not yet implemented.");
			case NORM_FRO:
				return eucl(m);
			case NORM1:
				int rows = m.shape().rows();
				int cols = m.shape().cols();
				double max = 0;
				for(int j = 0; j < cols; j++) {
					double d = 0;
					for(int i = 0; i < rows; i++)
						d += ((MScalar) m.get(i, j)).abs();
					if(d>max)
						max = d;
				}
				return new MReal(max);
			case NORM_INF:
				int rows2 = m.shape().rows();
				int cols2 = m.shape().cols();
				double max2 = 0;
				for(int i = 0; i < rows2; i++) {
					double d = 0;
					for(int j = 0; j < cols2; j++)
						d += ((MScalar) m.get(i, j)).abs();
					if(d>max2)
						max2 = d;
				}
				return new MReal(max2);
			}
		}
		return null;
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		type = null;
		mo = null;
		prepared = false;
		if(args.length==1) {
			if(args[0] instanceof MMatrix)
				type = NormType.NORM_FRO;
			else if(args[0] instanceof MVector)
				type = NormType.NORM2;
			else
				throw new IllegalArgumentException("Expected vector or matrix, got " + argTypesToString(args));
			mo = args[0];
		} else if(args.length==2) {
			if(args[0] instanceof MVector || args[0] instanceof MMatrix) {
				if(args[1] instanceof MReal) {
					double d = ((MReal) args[1]).getValue();
					if(d==1)
						type = NormType.NORM1;
					else if(d==2)
						type = NormType.NORM2;
					else if(d==Double.POSITIVE_INFINITY)
						type = NormType.NORM_INF;
					mo = args[0];
				} else 
					throw new IllegalArgumentException("Expected 2nd argument to be either 1, 2 or inf, got " + args[1].toString());
			} else
				throw new IllegalArgumentException("Expected 1st argument to be vector or matrix, got " + argTypesToString(args));
		} else
			throw new IllegalArgumentException("Norm expects 1 or 2 arguments, got " + args.length);
		if(mo.isNumeric())
			prepared = true;
		else
			throw new IllegalArgumentException("Cannot calculate norm of a non-numeric object.");
	}

	@Override
	public Shape shape(Shape... shapes) {
		// TODO Auto-generated method stub
		return null;
	}
}
