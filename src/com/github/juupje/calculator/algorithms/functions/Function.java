package com.github.juupje.calculator.algorithms.functions;

import com.github.juupje.calculator.algorithms.Norm;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.*;
import static com.github.juupje.calculator.algorithms.functions.TrigFunctions.*;
import static com.github.juupje.calculator.algorithms.functions.Functions.*;

public enum Function {
	TODEG {
		@Override
		public MReal evaluate(MReal m) {
			return toDegree(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return toDegree(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return toDegree(m);
		}
	},
	TORAD {
		@Override
		public MReal evaluate(MReal m) {
			return toRadians(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return toRadians(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return toRadians(m);
		}
	},
	SIN {
		@Override
		public MReal evaluate(MReal m) {
			return sin(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return sin(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return sin(m);
		}
	},
	COS {
		@Override
		public MReal evaluate(MReal m) {
			return cos(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return cos(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return cos(m);
		}
	},
	TAN {
		@Override
		public MReal evaluate(MReal m) {
			return tan(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return tan(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return tan(m);
		}
	},
	SIND {
		@Override
		public MReal evaluate(MReal m) {
			return sin(toRadians(m));
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return sin(toRadians(m));
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return sin(toRadians(m));
		}
	},
	COSD {
		@Override
		public MReal evaluate(MReal m) {
			return cos(toRadians(m));
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return cos(toRadians(m));
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return cos(toRadians(m));
		}
	},
	TAND {
		@Override
		public MReal evaluate(MReal m) {
			return tan(toRadians(m));
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return tan(toRadians(m));
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return tan(toRadians(m));
		}
	},
	SINH {
		@Override
		public MReal evaluate(MReal m) {
			return sinh(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return sinh(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return sinh(m);
		}
	},
	COSH {
		@Override
		public MReal evaluate(MReal m) {
			return cosh(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return cosh(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return cosh(m);
		}
	},
	TANH {
		@Override
		public MReal evaluate(MReal m) {
			return tanh(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return (MComplex) sinh(m).divide(cosh(m));
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return sinh(m).divide(cosh(m));
		}
	},
	ASIN {
		@Override
		public MReal evaluate(MReal m) {
			return asin(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return asin(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return asin(m);
		}
	},
	ACOS {
		@Override
		public MReal evaluate(MReal m) {
			return acos(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return acos(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return acos(m);
		}
	},
	ATAN {
		@Override
		public MReal evaluate(MReal m) {
			return atan(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return atan(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return atan(m);
		}
	},
	ASINH {
		@Override
		public MReal evaluate(MReal m) {
			return asinh(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return asinh(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return asinh(m);
		}
	},
	ACOSH {
		@Override
		public MScalar evaluate(MReal m) {
			return acosh(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return acosh(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return acosh(m);
		}
	},
	ATANH {
		@Override
		public MScalar evaluate(MReal m) {
			return atanh(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return atanh(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return atanh(m);
		}
	},
	SQRT {
		@Override
		public MScalar evaluate(MReal m) {
			return sqrt(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return sqrt(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return sqrt(m);
		}
	},
	ROOT {
		@Override
		public MScalar evaluate(MReal m) {
			throw new IllegalArgumentException(
					"This method should be called with an MVector, please contact the developer.");
		}
		
		@Override
		public MScalar evaluate(MComplex m) {
			throw new IllegalArgumentException(
					"This method should be called with an MVector, please contact the developer.");
		}
		
		@Override
		public MScalar evaluate(MRealError m) {
			throw new IllegalArgumentException(
					"This method should be called with an MVector, please contact the developer.");
		}
		
		@Override
		public MScalar evaluate(MVector m) {
			if(m.size()!=2)
				throw new IllegalArgumentException("Expected two elements, got " + m.size());
			try {
				return root((MScalar) m.elements()[0], (MScalar) m.elements()[1]);
			} catch(ClassCastException e) {
				throw new IllegalArgumentException("Expected two scalar values, got " + m.get(0) + " and " + m.get(1));
			}
		}
	},
	ABS {
		@Override
		public MReal evaluate(MReal m) {
			return new MReal(m.abs());
		}
		@Override
		public MReal evaluate(MComplex m) {
			return new MReal(m.abs());
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return new MRealError(m.abs(), m.err());
		}
		@Override
		public MReal evaluate(MVector v) {
			return Norm.eucl(v);
		}
		@Override
		public MScalar evaluate(MMatrix m) {
			return det(m);
		}
		@Override
		public Shape shape(Shape s) {
			return Shape.SCALAR.copy();
		}
	},
	LN {
		@Override
		public MReal evaluate(MReal m) {
			return ln(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return ln(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return ln(m);
		}
	},
	LOG {
		@Override
		public MReal evaluate(MReal m) {
			return log(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return log(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return log(m);
		}
	},
	EXP {
		@Override
		public MReal evaluate(MReal m) {
			return exp(m);
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return exp(m);
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return exp(m);
		}
	},
	FACT {
		@Override
		public MReal evaluate(MReal m) {
			return fact(m);
		}
	},
	CONJ {
		@Override
		public MReal evaluate(MReal m) {
			return m.copy();
		}
		@Override
		public MComplex evaluate(MComplex m) {
			return m.conjugate();
		}
		@Override
		public MRealError evaluate(MRealError m) {
			return m.copy();
		}
		@Override
		public MMatrix evaluate(MMatrix m) {
			return m.forEach(z -> evaluate(z));
		}
		@Override
		public MVector evaluate(MVector m) {
			return m.forEach(z -> evaluate(z));
		}
	},
	DET {
		@Override
		public MScalar evaluate(MMatrix m) {
			return det(m);
		}
		
		@Override
		public Shape shape(Shape s) {
			if(s.dim() == 2 && s.isSquare())
				return Shape.SCALAR.copy();
			throw new ShapeException("DET is only defined for square matrices, got shape " + s);
		}
	},
	TOMATRIX {
		@Override
		public MMatrix evaluate(MReal s) {
			return new MMatrix(new MathObject[][] {{s.copy()}});
		}
		@Override
		public MMatrix evaluate(MComplex s) {
			return new MMatrix(new MathObject[][] {{s.copy()}});
		}
		@Override
		public MMatrix evaluate(MRealError s) {
			return new MMatrix(new MathObject[][] {{s.copy()}});
		}
		@Override
		public MMatrix evaluate(MVector v) {
			return v.toMatrix();
		}
		@Override
		public MMatrix evaluate(MMatrix m) {
			return m.copy();
		}
		@Override
		public MMatrix evaluate(MIndexedObject m) {
			return m.toMatrix();
		}
	},
	TOVECTOR {
		@Override
		public MVector evaluate(MReal s) {
			return new MVector(s);
		}
		@Override
		public MVector evaluate(MComplex s) {
			return new MVector(s);
		}
		@Override
		public MVector evaluate(MRealError s) {
			return new MVector(s);
		}
		@Override
		public MVector evaluate(MVector v) {
			return v.copy();
		}
		@Override
		public MVector evaluate(MMatrix m) {
			if(m.shape().rows()==1)
				return m.getRow(0).transpose();
			if(m.shape().cols()==1)
				return m.getColumn(0);
			throw new ShapeException("Cannot interpret shape " + m.shape() + " as vector");
		}
		@Override
		public MVector evaluate(MIndexedObject m) {
			return m.toVector();
		}
	};

	public MathObject evaluate(MReal s) {
		throw new IllegalArgumentException(toString() + " is not defined for real numbers");			
	}
	public MathObject evaluate(MComplex s) {
		throw new IllegalArgumentException(toString() + " is not defined for complex numbers");			
	}
	public MathObject evaluate(MRealError s) {
		throw new IllegalArgumentException("Cannot propagate error through " + toString());			
	}
	
	public MathObject evaluate(MVector v) {
		throw new IllegalArgumentException(toString() + " is not defined for vectors");
	}
	
	public MathObject evaluate(MMatrix m) {
		throw new IllegalArgumentException(toString() + " is not defined for matrices");
	}
	
	public MathObject evaluate(MIndexedObject m) {
		throw new IllegalArgumentException(toString() + " is not defined for indexed object");			
	}
	
	public MathObject evaluate(MathObject m) {
		if(m instanceof MRealError)
			return evaluate((MRealError) m);
		else if(m instanceof MReal)
			return evaluate((MReal) m);
		else if(m instanceof MComplex)
			return evaluate((MComplex) m);
		else if(m instanceof MVector)
			return evaluate((MVector) m);
		else if(m instanceof MMatrix)
			return evaluate((MMatrix) m);
		else if(m instanceof MIndexedObject)
			return evaluate((MIndexedObject) m);
		else
			throw new IllegalArgumentException(toString() + " is not defined for " + Tools.type(m) + ", see help("+toString()+")");
	}
	
	public Shape shape(Shape s) {
		return s.copy();
	}

	public String toString() {
		return name().toLowerCase();
	}
	
	public static boolean isFunction(String s) {
		try {
			Function.valueOf(s.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static Function getFunction(String name) {
		return Function.valueOf(name.toUpperCase());
	}
}