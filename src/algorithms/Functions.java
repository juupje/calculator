package algorithms;

import helpers.Setting;
import helpers.Shape;
import helpers.Tools;
import helpers.exceptions.InvalidOperationException;
import helpers.exceptions.ShapeException;
import mathobjects.MComplex;
import mathobjects.MConst;
import mathobjects.MMatrix;
import mathobjects.MReal;
import mathobjects.MScalar;
import mathobjects.MVector;
import mathobjects.MathObject;

public class Functions {

	public enum Function {
		TODEG {
			@Override
			public MathObject evaluate(MathObject m) {
				return toDegree(m);
			}
			@Override
			public Shape shape(Shape s)  {
				if(s.dim()<2)
					return s;
				throw new ShapeException("Function TODEG is not defined for arguments of shape " + s);
			}
		},
		TORAD {
			@Override
			public MathObject evaluate(MathObject m) {
				return toRadians(m);
			}
			@Override
			public Shape shape(Shape s)  {
				if(s.dim()<2)
					return s;
				throw new ShapeException("Function TORAD is not defined for arguments of shape " + s);
			}
		},
		SIN {
			@Override
			public MScalar evaluate(MathObject m) {
				return sin(m);
			}
		},
		COS {
			@Override
			public MScalar evaluate(MathObject m) {
				return cos(m);
			}
		},
		TAN {
			@Override
			public MScalar evaluate(MathObject m) {
				return tan(m);
			}
		},
		SIND {
			@Override
			public MScalar evaluate(MathObject m) {
				return sind(m);
			}
		},
		COSD {
			@Override
			public MScalar evaluate(MathObject m) {
				return cosd(m);
			}
		},
		TAND {
			@Override
			public MScalar evaluate(MathObject m) {
				return tand(m);
			}
		},
		ASIN {
			@Override
			public MScalar evaluate(MathObject m) {
				return asin(m);
			}
		},
		ACOS {
			@Override
			public MScalar evaluate(MathObject m) {
				return acos(m);
			}
		},
		ATAN {
			@Override
			public MScalar evaluate(MathObject m) {
				return atan(m);
			}
		},
		SQRT {
			@Override
			public MScalar evaluate(MathObject m) {
				return sqrt(m);
			}
		},
		ABS {
			@Override
			public MScalar evaluate(MathObject m) {
				return abs(m);
			}
		},
		LN {
			@Override
			public MScalar evaluate(MathObject m) {
				return ln(m);
			}
		},
		LOG {
			@Override
			public MScalar evaluate(MathObject m) {
				return log(m);
			}
		},
		FACT {
			@Override
			public MReal evaluate(MathObject m) {
				return fact(m);
			}
		},
		DET {
			@Override
			public MScalar evaluate(MathObject m) {
				return det(m);
			}
		};

		public abstract MathObject evaluate(MathObject m);
		public Shape shape(Shape s)  {
			if(s.dim()==0)
				return s;
			throw new ShapeException("Function " + name().toLowerCase() + " not defined for shape " + s);
		}
		
		public String toString() {
			return name().toLowerCase();
		}
	}

	//############### TRIGONOMETRIC FUNCTIONS ###############
	/**
	 * Calculates the sine of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * For complex numbers the formula {@code sin(a+bi)=sin(a)cosh(b)+i*cos(a)sinh(b)} is used.
	 * @param m a <tt>MScalar</tt> of which the sine should be calculated (in radians).
	 * @return a <tt>MScalar</tt> containing the sine of <tt>m</tt>
	 * (real arguments give a real result, complex arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar sin(MathObject m) {
		if (m instanceof MReal)
			return new MReal(Math.sin(((MReal) m).getValue()));
		else if(m instanceof MComplex) {
			MComplex z = (MComplex) m;
			return new MComplex(Math.sin(z.real())*Math.cosh(z.imag()), -1*Math.cos(z.real())*Math.sinh(z.imag()));
		} else
			throw new InvalidOperationException("Sine is not defined for " + m.getClass());
	}

	/**
	 * Calculates the cosine of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * For complex numbers the formula {@code cos(a+bi)=cos(a)cosh(b)-i*sin(a)sinh(b)} is used.
	 * @param m a <tt>MScalar</tt> of which the cosine should be calculated (in radians).
	 * @return a <tt>MScalar</tt> containing the cosine of <tt>m</tt>
	 * (real arguments give a real result, complex arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar cos(MathObject m) {
		if (m instanceof MReal)
			return new MReal(Math.cos(((MReal) m).getValue())); 
		else if(m instanceof MComplex) {
			MComplex z = (MComplex) m;
			return new MComplex(Math.cos(z.real())*Math.cosh(z.imag()), Math.sin(z.real())*Math.sinh(z.imag()));
		} else
			throw new InvalidOperationException("Cosine is not defined for " + m.getClass());
	}

	/**
	 * Calculates the tangent of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * For complex numbers the formula {@code tan(a+bi)=(sin(2a)+i*sinh(2b))/(cos(2a)+cosh(2b))} is used.
	 * @param m a <tt>MScalar</tt> of which the tangent should be calculated (in radians).
	 * @return a <tt>MScalar</tt> containing the tangent of <tt>m</tt>
	 * (real arguments give a real result, complex arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar tan(MathObject m) {
		if (m instanceof MReal)
			return new MReal(Math.tan(((MReal) m).getValue()));
		else if(m instanceof MComplex) {
			MComplex z = (MComplex) m;
			double d = Math.cos(2*z.real())+Math.cosh(2*z.imag());
			return new MComplex(Math.sin(2*z.real())/d, Math.sinh(2*z.imag())/d);
		} else
			throw new InvalidOperationException("Tangent is not defined for " + m.getClass());
	}
	
	/**
	 * calls <tt>sin(toDegree(m))</tt>.
	 * @return the <tt>MScalar</tt> returned by the call.
	 * @see #sin(MathObject)
	 * @see #toDegree(MathObject)
	 */
	public static MScalar sind(MathObject m) {
		return sin(toRadians(m));
	}

	/**
	 * calls <tt>cos(toDegree(m))</tt>.
	 * @return the <tt>MScalar</tt> returned by the call.
	 * @see #cos(MathObject)
	 * @see #toDegree(MathObject)
	 */
	public static MScalar cosd(MathObject m) {
		return cos(toRadians(m));
	}

	/**
	 * calls <tt>tan(toDegree(m))</tt>.
	 * @return the <tt>MScalar</tt> returned by the call.
	 * @see #tan(MathObject)
	 * @see #toDegree(MathObject)
	 */
	public static MScalar tand(MathObject m) {
		return tan(toRadians(m));
	}
	
	/**
	 * Calculates the arc sine of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * <ul><li>If the argument is real, it has to be in the interval [-1,1].</li>
	 * <li>If the argument is complex, the formula {@code asin(z)=-i*ln(iz+sqrt(1-z^2))} is used, using {@link #ln(MathObject)}.</li></ul>
	 * @param m a <tt>MScalar</tt> of which the arc sine should be calculated (in radians).
	 * @return a <tt>MScalar</tt> containing the arc sine of <tt>m</tt>.
	 * (real arguments give a real result, complex arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar asin(MathObject m) {
		if (m instanceof MReal) {
			double val = ((MReal) m).getValue();
			if(val>1 || val<-1)
				throw new IllegalArgumentException("Domain error: arcsin is defined on [-1,1], got " +val);
			return new MReal(Math.asin(((MReal) m).getValue()));
		}else if(m instanceof MComplex) {
			MComplex z = (MComplex) m;
			MComplex w = z.copy().power(2).negate().add(1);
			MScalar v = w.power(0.5).add(z.copy().multiply(0,1));
			return ((MComplex) ln(v)).multiply(0,-1);
		}else
			throw new InvalidOperationException("Arc sine is not defined for " + m.getClass());
	}

	/**
	 * Calculates the arc cosine of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * <ul><li>If the argument is real, it has to be in the interval [-1,1].</li>
	 * <li>If the argument is complex, the formula {@code acos(z)=-i*ln(z+i*sqrt(1-z^2))} is used, using {@link #ln(MathObject)}.</li></ul>
	 * @param m a <tt>MScalar</tt> of which the arc cosine should be calculated.
	 * @return a <tt>MScalar</tt> containing the arc cosine of <tt>m</tt> (in radians).
	 * (real arguments give a real result, complex arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar acos(MathObject m) {
		if (m instanceof MReal) {
			double val = ((MReal) m).getValue();
			if(val>1 || val<-1)
				throw new IllegalArgumentException("Domain error: arccos is defined on [-1,1], got " +val);
			return new MReal(Math.acos(((MReal) m).getValue()));
		} else if(m instanceof MComplex) {
			MComplex z = (MComplex) m;
			MComplex w = z.copy().multiply(z).negate().add(1);
			return ((MComplex) ln(w.power(0.5).multiply(0,1).add(z))).multiply(0,-1);
		} else
			throw new InvalidOperationException("Arc cosine is not defined for " + m.getClass());
	}

	/**
	 * Calculates the arc tangent of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * If the argument is complex, the principle value will be returned using {@code atan(z)=1/(2i)ln((i-z)/(i+z))} using {@link #ln(MathObject)}.
	 * @param m a <tt>MScalar</tt> of which the arc tangent should be calculated.
	 * @return a <tt>MScalar</tt> containing the arc tangent of <tt>m</tt> (in radians).
	 * (real arguments give a real result, complex arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar atan(MathObject m) {
		if (m instanceof MReal)
			return new MReal(Math.atan(((MReal) m).getValue()));
		else if(m instanceof MComplex) {
			double a = ((MComplex) m).real();
			double b = ((MComplex) m).imag();
			double c = a*a+(b+1)*(b+1);
			return ((MComplex) ln(new MComplex((-a*a-b*b+1)/c, 2*a/c)).multiply(MConst.i.evaluate().multiply(-0.5)));
		} else
			throw new InvalidOperationException("Arc tangent is not defined for " + m.getClass());
	}
	
	//############### CONVERSION FUNCTIONS ###############
	/**
	 * Converts the given <tt>MathObject</tt> from radians to degrees, element-wise if necessary
	 * @param m <tt>MathObject</tt> to be converted to degrees.
	 * @return a <tt>MathObject</tt> of the same type as m.
	 * @throws InvalidOperationException if the given <tt>MathObject</tt> cannot be converted to degrees.
	 */
	public static MathObject toDegree(MathObject m) {
		if(m instanceof MReal)
			return new MReal(Math.toDegrees(((MReal) m).getValue()));
		else if(m instanceof MVector) {
			MVector v = (MVector) m.copy();
			for(MathObject el: v.elements())
				el = toDegree(el);
			return v;
		}
		throw new InvalidOperationException("Can't convert " + m.getClass() + " to degrees.");
	}
	
	/**
	 * Converts the given <tt>MathObject</tt> from degrees to radians, element-wise if necessary
	 * @param m <tt>MathObject</tt> to be converted to radians.
	 * @return a <tt>MathObject</tt> of the same type as m.
	 * @throws InvalidOperationException if the given <tt>MathObject</tt> cannot be converted to radians.
	 */
	public static MathObject toRadians(MathObject m) {
		if(m instanceof MReal)
			return new MReal(Math.toRadians(((MReal) m).getValue()));
		else if(m instanceof MVector) {
			MVector v = (MVector) m.copy();
			for(MathObject el: v.elements())
				el = toRadians(el);
			return v;
		}
		throw new InvalidOperationException("Can't convert " + m.getClass() + " to degrees.");
	}

	//############### SCALAR FUNCTIONS ###############
	/**
	 * Calculates the absolute value (distance to the origin) of the given <tt>MathObject</tt>, for vectors and complex values this equals the Euclidean norm.
	 * @param m <tt>MathObject</tt> of which the absolute value should be calculated.
	 * @return a <tt>MReal</tt> containing the absolute value.
	 * @throws InvalidOperationException if the absolute value is not defined for the type of <tt>m</tt>.
	 */
	public static MReal abs(MathObject m) {
		if (m instanceof MScalar)
			return new MReal(((MScalar)m).abs());
		else if (m instanceof MVector)
			return Norm.eucl((MVector) m);
		throw new InvalidOperationException("The absolute value is not defined for " + m.getClass());
	}
	
	/**
	 * returns the natural logarithm of the given <tt>MathObject</tt>. This works only if m is a <tt>MScalar</tt>.
	 * If the argument is complex the principle value is returned: {@code log(z)=ln(r)+i*phi}, where {@code -pi<=phi<=pi}.
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the natural logarithm of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#log(double)
	 */
	public static MScalar ln(MathObject m) {
		if (m instanceof MReal)
			return new MReal(Math.log(((MReal) m).getValue()));
		else if(m instanceof MComplex) {
			return new MComplex(Math.log(((MComplex) m).getR()), ((MComplex) m).arg());
		}
		throw new InvalidOperationException("The logarithm is not defined for " + m.getClass());
	}
	
	/**
	 * returns the logarithm with base 10 of the given <tt>MathObject</tt>. This works only if m is a <tt>MScalar</tt>.
	 * If the argument is complex {@code ln(m)/ln(10)} is returned using {@link #ln(MathObject)}.
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the 10-logarithm of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#log10(double)
	 */
	public static MScalar log(MathObject m) {
		if (m instanceof MReal)
			return new MReal(Math.log10(((MReal) m).getValue()));
		else if(m instanceof MComplex)
			return ((MComplex) ln(m)).divide(Math.log(10));
		throw new InvalidOperationException("The logarithm is not defined for " + m.getClass());
	}
	
	/**
	 * returns the square root of the given <tt>MathObject</tt>. This works only if m is a <tt>MScalar</tt>.
	 * If the argument is complex {@code m^0.5} is returned using {@link MComplex#power(double)}.
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the square root of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#sqrt(double)
	 */
	public static MScalar sqrt(MathObject m) {
		if (m instanceof MReal) {
			if(((MReal) m).getValue() < 0 && (Setting.getBool(Setting.COMPLEX_ENABLED)))
				return new MComplex(0, Math.sqrt(((MReal) m).getValue()*-1));
			return new MReal(Math.sqrt(((MReal) m).getValue()));
		}
		if(m instanceof MComplex)
			return ((MComplex) m.copy()).power(0.5);
		throw new InvalidOperationException("The square root is not defined for " + m.getClass());
	}
	
	/**
	 * returns the n-th root of the given <tt>MathObject</tt>. This works only if m is a <tt>MScalar</tt>
	 * @param m the argument
	 * @param n the base of the root
	 * @return a <tt>MScalar</tt> containing the n-th root of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see MScalar#power(double)
	 */
	public static MScalar root(MathObject m, int n) {
		if (m instanceof MScalar)
			return ((MScalar) m.copy()).power(1d/n);
		throw new InvalidOperationException("The root is not defined for " + m.getClass());
	}
	
	/**
	 * Calculates n! (n factorial) using {@link Tools#fact(int)}.
	 * @param m a MathObject
	 * @return n! (where n is the value of m).
	 * @throws IllegalArgumentException if n is not a positive integer value.
	 */
	public static MReal fact(MathObject m) {
		if(m instanceof MReal && ((MReal) m).isInteger()) {
			int n = (int) ((MReal) m).getValue();
			return new MReal(Tools.fact(n));
		}
		throw new IllegalArgumentException("Factorial is only defined for integer values, got " + m.toString());
	}
	
	public static MScalar det(MathObject m) {
		if(m instanceof MMatrix && ((MMatrix) m).isSquare()) {
			return ((MMatrix)m).det();
		}
		throw new IllegalArgumentException("The determinant is only defined for (square) matrices, got " + m.getClass().getCanonicalName());
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
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
