package algorithms;

import helpers.Shape;
import helpers.exceptions.InvalidOperationException;
import helpers.exceptions.ShapeException;
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
			@Override
			public Shape shape(Shape s)  {
				if(s.dim()<3)
					return new Shape();
				throw new ShapeException("Function TODEG is not defined for arguments of shape " + s);
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
		};

		public abstract MathObject evaluate(MathObject m);
		public Shape shape(Shape s)  {
			if(s.dim()==0)
				return s;
			throw new ShapeException("Function " + name() + " not defined for shape " + s);
		}
	}

	//############### TRIGONOMETRIC FUNCTIONS ###############
	/**
	 * Calculates the sine of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * @param m a <tt>MScalar</tt> of which the sine should be calculated (in radians).
	 * @return a <tt>MScalar</tt> containing the sine of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar sin(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.sin(((MScalar) m).getValue()));
		else
			throw new InvalidOperationException("Sine is not defined for " + m.getClass());
	}

	/**
	 * Calculates the cosine of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * @param m a <tt>MScalar</tt> of which the cosine should be calculated (in radians).
	 * @return a <tt>MScalar</tt> containing the cosine of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar cos(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.cos(((MScalar) m).getValue()));
		else
			throw new InvalidOperationException("Cosine is not defined for " + m.getClass());
	}

	/**
	 * Calculates the tangent of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * @param m a <tt>MScalar</tt> of which the tangent should be calculated (in radians).
	 * @return a <tt>MScalar</tt> containing the tangent of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar tan(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.tan(((MScalar) m).getValue()));
		else
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
	 * @param m a <tt>MScalar</tt> of which the arc sine should be calculated (in radians).
	 * @return a <tt>MScalar</tt> containing the arc sine of <tt>m</tt>.
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar asin(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.asin(((MScalar) m).getValue()));
		else
			throw new InvalidOperationException("Arc sine is not defined for " + m.getClass());
	}

	/**
	 * Calculates the arc cosine of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * @param m a <tt>MScalar</tt> of which the arc cosine should be calculated.
	 * @return a <tt>MScalar</tt> containing the arc cosine of <tt>m</tt> (in radians).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar acos(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.acos(((MScalar) m).getValue()));
		else
			throw new InvalidOperationException("Arc cosine is not defined for " + m.getClass());
	}

	/**
	 * Calculates the arc tangent of the given <tt>MathObject</tt>. This works only if <tt>m</tt> is a <tt>MScalar</tt>.
	 * @param m a <tt>MScalar</tt> of which the arc tangent should be calculated.
	 * @return a <tt>MScalar</tt> containing the arc tangent of <tt>m</tt> (in radians).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar atan(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.atan(((MScalar) m).getValue()));
		else
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
		if(m instanceof MScalar)
			return new MScalar(Math.toDegrees(((MScalar) m).getValue()));
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
		if(m instanceof MScalar)
			return new MScalar(Math.toRadians(((MScalar) m).getValue()));
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
	 * Calculates the absolute value (distance to the origin) of the given <tt>MathObject</tt>, for vectors this equals the Euclidean norm..
	 * @param m <tt>MathObject</tt> of which the absolute value should be calculated.
	 * @return a <tt>MScalar</tt> containing the absolute value.
	 * @throws InvalidOperationException if the absolute value is not defined for the type of <tt>m</tt>.
	 */
	public static MScalar abs(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.abs(((MScalar) m).getValue()));
		else if (m instanceof MVector)
			return Norm.eucl((MVector) m);
		throw new InvalidOperationException("The absolute value is not defined for " + m.getClass());
	}
	
	/**
	 * returns the natural logarithm of the given <tt>MathObject</tt>. This works only if m is a <tt>MScalar</tt>
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the natural logarithm of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#log(double)
	 */
	public static MScalar ln(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.log(((MScalar) m).getValue()));
		throw new InvalidOperationException("The logarithm is not defined for " + m.getClass());
	}
	
	/**
	 * returns the logarithm with base 10 of the given <tt>MathObject</tt>. This works only if m is a <tt>MScalar</tt>
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the 10-logarithm of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#log10(double)
	 */
	public static MScalar log(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.log10(((MScalar) m).getValue()));
		throw new InvalidOperationException("The logarithm is not defined for " + m.getClass());
	}
	
	/**
	 * returns the square root of the given <tt>MathObject</tt>. This works only if m is a <tt>MScalar</tt>
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the square root of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#sqrt(double)
	 */
	public static MScalar sqrt(MathObject m) {
		if (m instanceof MScalar)
			return new MScalar(Math.sqrt(((MScalar) m).getValue()));
		throw new InvalidOperationException("The square root is not defined for " + m.getClass());
	}
	
	/**
	 * returns the n-th root of the given <tt>MathObject</tt>. This works only if m is a <tt>MScalar</tt>
	 * @param m the argument
	 * @param n the base of the root
	 * @return a <tt>MScalar</tt> containing the n-th root of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#sqrt(double)
	 */
	public static MScalar root(MathObject m, int n) {
		if (m instanceof MScalar)
			return new MScalar(Math.pow(((MScalar) m).getValue(), 1d/n));
		throw new InvalidOperationException("The root is not defined for " + m.getClass());
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
