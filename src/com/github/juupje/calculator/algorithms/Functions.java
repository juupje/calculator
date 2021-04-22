package com.github.juupje.calculator.algorithms;

import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.settings.Settings;

public class Functions {

	public enum Function {
		TODEG {
			@Override
			public MScalar evaluate(MScalar m) {
				return toDegree(m);
			}
		},
		TORAD {
			@Override
			public MScalar evaluate(MScalar m) {
				return toRadians(m);
			}
		},
		SIN {
			@Override
			public MScalar evaluate(MScalar m) {
				return sin(m);
			}
		},
		COS {
			@Override
			public MScalar evaluate(MScalar m) {
				return cos(m);
			}
		},
		TAN {
			@Override
			public MScalar evaluate(MScalar m) {
				return tan(m);
			}
		},
		SIND {
			@Override
			public MScalar evaluate(MScalar m) {
				return sind(m);
			}
		},
		COSD {
			@Override
			public MScalar evaluate(MScalar m) {
				return cosd(m);
			}
		},
		TAND {
			@Override
			public MScalar evaluate(MScalar m) {
				return tand(m);
			}
		},
		SINH {
			@Override
			public MScalar evaluate(MScalar m) {
				return sinh(m);
			}
		},
		COSH {
			@Override
			public MScalar evaluate(MScalar m) {
				return cosh(m);
			}
		},
		TANH {
			@Override
			public MScalar evaluate(MScalar m) {
				return tanh(m);
			}
		},
		ASIN {
			@Override
			public MScalar evaluate(MScalar m) {
				return asin(m);
			}
		},
		ACOS {
			@Override
			public MScalar evaluate(MScalar m) {
				return acos(m);
			}
		},
		ATAN {
			@Override
			public MScalar evaluate(MScalar m) {
				return atan(m);
			}
		},
		ASINH {
			@Override
			public MScalar evaluate(MScalar m) {
				return asinh(m);
			}
		},
		ACOSH {
			@Override
			public MScalar evaluate(MScalar m) {
				return acosh(m);
			}
		},
		ATANH {
			@Override
			public MScalar evaluate(MScalar m) {
				return atanh(m);
			}
		},
		SQRT {
			@Override
			public MScalar evaluate(MScalar m) {
				return sqrt(m);
			}
		},
		ROOT {
			@Override
			public MScalar evaluate(MScalar m) {
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
			public MScalar evaluate(MScalar m) {
				return abs(m);
			}
			@Override
			public MScalar evaluate(MVector v) {
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
			public MScalar evaluate(MScalar m) {
				return ln(m);
			}
		},
		LOG {
			@Override
			public MScalar evaluate(MScalar m) {
				return log(m);
			}
		},
		EXP {
			@Override
			public MScalar evaluate(MScalar m) {
				return exp(m);
			}
		},
		FACT {
			@Override
			public MReal evaluate(MScalar m) {
				return fact(m);
			}
		},
		CONJ {
			@Override
			public MScalar evaluate(MScalar m) {
				return conj(m);
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
			public MMatrix evaluate(MScalar s) {
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
			public MVector evaluate(MScalar s) {
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

		public MathObject evaluate(MScalar s) {
			throw new IllegalArgumentException(toString() + " is not defined for scalars");			
		}
		
		public MathObject evaluate(MVector v) {
			/*Function f = this;
			return v.copy().forEach(new java.util.function.Function<MathObject, MathObject>() {
				@Override
				public MathObject apply(MathObject m) {
					return f.evaluate(m);
				}
			});*/
			throw new IllegalArgumentException(toString() + " is not defined for vectors");
		}
		public MathObject evaluate(MMatrix m) {
			throw new IllegalArgumentException(toString() + " is not defined for matrices");
		}
		
		public MathObject evaluate(MIndexedObject m) {
			throw new IllegalArgumentException(toString() + " is not defined for indexed object");			
		}
		
		public MathObject evaluate(MathObject m) {
			if(m instanceof MScalar)
				return evaluate((MScalar) m);
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
	}

	// ############### TRIGONOMETRIC FUNCTIONS ###############
	/**
	 * Calculates the sine of the given <tt>MathObject</tt>. This works only if
	 * <tt>m</tt> is a <tt>MScalar</tt>. For complex numbers the formula
	 * {@code sin(a+bi)=sin(a)cosh(b)+i*cos(a)sinh(b)} is used.
	 * 
	 * @param m a <tt>MScalar</tt> of which the sine should be calculated (in
	 *          radians).
	 * @return a <tt>MScalar</tt> containing the sine of <tt>m</tt> (real arguments
	 *         give a real result, complex arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar sin(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.sin(((MReal) m).getValue()));
		MComplex z = (MComplex) m;
		return new MComplex(Math.sin(z.real()) * Math.cosh(z.imag()),
					-1 * Math.cos(z.real()) * Math.sinh(z.imag()));
	}

	/**
	 * Calculates the cosine of the given <tt>MathObject</tt>. This works only if
	 * <tt>m</tt> is a <tt>MScalar</tt>. For complex numbers the formula
	 * {@code cos(a+bi)=cos(a)cosh(b)-i*sin(a)sinh(b)} is used.
	 * 
	 * @param m a <tt>MScalar</tt> of which the cosine should be calculated (in
	 *          radians).
	 * @return a <tt>MScalar</tt> containing the cosine of <tt>m</tt> (real
	 *         arguments give a real result, complex arguments give a complex
	 *         result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar cos(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.cos(((MReal) m).getValue()));
		MComplex z = (MComplex) m;
		return new MComplex(Math.cos(z.real()) * Math.cosh(z.imag()), Math.sin(z.real()) * Math.sinh(z.imag()));
	}

	/**
	 * Calculates the tangent of the given <tt>MathObject</tt>. This works only if
	 * <tt>m</tt> is a <tt>MScalar</tt>. For complex numbers the formula
	 * {@code tan(a+bi)=(sin(2a)+i*sinh(2b))/(cos(2a)+cosh(2b))} is used.
	 * 
	 * @param m a <tt>MScalar</tt> of which the tangent should be calculated (in
	 *          radians).
	 * @return a <tt>MScalar</tt> containing the tangent of <tt>m</tt> (real
	 *         arguments give a real result, complex arguments give a complex
	 *         result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar tan(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.tan(((MReal) m).getValue()));
		MComplex z = (MComplex) m;
		double d = Math.cos(2 * z.real()) + Math.cosh(2 * z.imag());
		return new MComplex(Math.sin(2 * z.real()) / d, Math.sinh(2 * z.imag()) / d);
	}

	/**
	 * calls <tt>sin(toDegree(m))</tt>.
	 * 
	 * @return the <tt>MScalar</tt> returned by the call.
	 * @see #sin(MathObject)
	 * @see #toDegree(MathObject)
	 */
	public static MScalar sind(MScalar m) {
		return sin(toRadians(m));
	}

	/**
	 * calls <tt>cos(toDegree(m))</tt>.
	 * 
	 * @return the <tt>MScalar</tt> returned by the call.
	 * @see #cos(MathObject)
	 * @see #toDegree(MathObject)
	 */
	public static MScalar cosd(MScalar m) {
		return cos(toRadians(m));
	}

	/**
	 * calls <tt>tan(toDegree(m))</tt>.
	 * 
	 * @return the <tt>MScalar</tt> returned by the call.
	 * @see #tan(MathObject)
	 * @see #toDegree(MathObject)
	 */
	public static MScalar tand(MScalar m) {
		return tan(toRadians(m));
	}

	/**
	 * Calculates the hyperbolic sine of the given <tt>MathObject</tt>. This works
	 * only if <tt>m</tt> is a <tt>MScalar</tt>. The hyperbolic sine is defined (and
	 * calculated by) {@code sinh(z)=(e^z-e^-z)/2} for all z, either real or
	 * complex.
	 * 
	 * @param m a <tt>MScalar</tt> of which the hyperbolic sine should be
	 *          calculated.
	 * @return a <tt>MScalar</tt> containing the hyperbolic sine of <tt>m</tt> (real
	 *         arguments give a real result, complex arguments give a complex
	 *         result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar sinh(MScalar m) {
		MScalar s = m.copy();
		return exp(s).subtract(exp(s.negate())).divide(2);
	}

	/**
	 * Calculates the hyperbolic cosine of the given <tt>MathObject</tt>. This works
	 * only if <tt>m</tt> is a <tt>MScalar</tt>. The hyperbolic cosine is defined
	 * (and calculated by) {@code cosh(z)=(e^z+e^-z)/2} for all z, either real or
	 * complex.
	 * 
	 * @param m a <tt>MScalar</tt> of which the hyperbolic cosine should be
	 *          calculated.
	 * @return a <tt>MScalar</tt> containing the hyperbolic cosine of <tt>m</tt>
	 *         (real arguments give a real result, complex arguments give a complex
	 *         result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar cosh(MScalar m) {
		MScalar s = m.copy();
		return exp(s).add(exp(s.negate())).divide(2);
	}

	/**
	 * Calculates the hyperbolic tangent of the given <tt>MathObject</tt>. This
	 * works only if <tt>m</tt> is a <tt>MScalar</tt>. The hyperbolic tangent is
	 * defined (and calculated by) {@code tanh(z)=sinh(z)/cosh(z)} for all z, either
	 * real or complex.
	 * 
	 * @param m a <tt>MScalar</tt> of which the hyperbolic tangent should be
	 *          calculated.
	 * @return a <tt>MScalar</tt> containing the hyperbolic tangent of <tt>m</tt>
	 *         (real arguments give a real result, complex arguments give a complex
	 *         result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar tanh(MScalar m) {
		return sinh(m).divide(cosh(m));
	}

	/**
	 * Calculates the arc sine of the given <tt>MathObject</tt>. This works only if
	 * <tt>m</tt> is a <tt>MScalar</tt>.
	 * <ul>
	 * <li>If the argument is real, it has to be in the interval [-1,1].</li>
	 * <li>If the argument is complex, the formula
	 * {@code asin(z)=-i*ln(iz+sqrt(1-z^2))} is used, using
	 * {@link #ln(MathObject)}.</li>
	 * </ul>
	 * 
	 * @param m a <tt>MScalar</tt> of which the arc sine should be calculated (in
	 *          radians).
	 * @return a <tt>MScalar</tt> containing the arc sine of <tt>m</tt>. (real
	 *         arguments give a real result, complex arguments give a complex
	 *         result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar asin(MScalar m) {
		if (m instanceof MReal) {
			double val = ((MReal) m).getValue();
			if (val > 1 || val < -1)
				throw new IllegalArgumentException("Domain error: arcsin is defined on [-1,1], got " + val);
			return new MReal(Math.asin(((MReal) m).getValue()));
		}
		MComplex z = (MComplex) m;
		MComplex w = z.copy().power(2).negate().add(1);
		MScalar v = w.power(0.5).add(z.copy().multiply(0, 1));
		return ((MComplex) ln(v)).multiply(0, -1);
	}

	/**
	 * Calculates the arc cosine of the given <tt>MathObject</tt>. This works only
	 * if <tt>m</tt> is a <tt>MScalar</tt>.
	 * <ul>
	 * <li>If the argument is real, it has to be in the interval [-1,1].</li>
	 * <li>If the argument is complex, the formula
	 * {@code acos(z)=-i*ln(z+i*sqrt(1-z^2))} is used, using
	 * {@link #ln(MathObject)}.</li>
	 * </ul>
	 * 
	 * @param m a <tt>MScalar</tt> of which the arc cosine should be calculated.
	 * @return a <tt>MScalar</tt> containing the arc cosine of <tt>m</tt> (in
	 *         radians). (real arguments give a real result, complex arguments give
	 *         a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar acos(MScalar m) {
		if (m instanceof MReal) {
			double val = ((MReal) m).getValue();
			if (val > 1 || val < -1)
				throw new IllegalArgumentException("Domain error: arccos is defined on [-1,1], got " + val);
			return new MReal(Math.acos(((MReal) m).getValue()));
		}
		MComplex z = (MComplex) m;
		MComplex w = z.copy().multiply(z).negate().add(1);
		return ((MComplex) ln(w.power(0.5).multiply(0, 1).add(z))).multiply(0, -1);
	}

	/**
	 * Calculates the arc tangent of the given <tt>MathObject</tt>. This works only
	 * if <tt>m</tt> is a <tt>MScalar</tt>. If the argument is complex, the
	 * principle value will be returned using {@code atan(z)=1/(2i)ln((i-z)/(i+z))}
	 * using {@link #ln(MathObject)}.
	 * 
	 * @param m a <tt>MScalar</tt> of which the arc tangent should be calculated.
	 * @return a <tt>MScalar</tt> containing the arc tangent of <tt>m</tt> (in
	 *         radians). (real arguments give a real result, complex arguments give
	 *         a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar atan(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.atan(((MReal) m).getValue()));
		double a = ((MComplex) m).real();
		double b = ((MComplex) m).imag();
		double c = a * a + (b + 1) * (b + 1);
		return ((MComplex) ln(new MComplex((-a * a - b * b + 1) / c, 2 * a / c))
					.multiply(MConst.i.evaluate().multiply(-0.5)));
	}

	/**
	 * Calculates the inverse hyperbolic sine of the given <tt>MathObject</tt>. This
	 * works only if <tt>m</tt> is a <tt>MScalar</tt>. The inverse hyperbolic sine
	 * is defined (and calculated by) {@code asinh(z)=ln(z+sqrt(z^2+1))} for all z,
	 * either real or complex.
	 * 
	 * @param m a <tt>MScalar</tt> of which the inverse hyperbolic sine should be
	 *          calculated.
	 * @return a <tt>MScalar</tt> containing the number whose hyperbolic sine equals
	 *         <tt>m</tt> (real arguments give a real result, complex arguments give
	 *         a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar asinh(MScalar m) {
		MScalar s = m.copy();
		return ln(s.add(sqrt(s.copy().multiply(s).add(1))));
	}

	/**
	 * Calculates the inverse hyperbolic cosine of the given <tt>MathObject</tt>.
	 * This works only if <tt>m</tt> is a <tt>MScalar</tt>. The inverse hyperbolic
	 * cosine is defined (and calculated by) {@code asinh(z)=ln(z+sqrt(z^2-1))} for
	 * all z, either real or complex.
	 * 
	 * @param m a <tt>MScalar</tt> of which the inverse hyperbolic cosine should be
	 *          calculated.
	 * @return a <tt>MScalar</tt> containing the number whose hyperbolic cosine
	 *         equals <tt>m</tt> (real arguments give a real result, complex
	 *         arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar acosh(MScalar m) {
		MScalar s = m.copy();
		return ln(s.add(sqrt(s.copy().multiply(s).subtract(1))));
	}

	/**
	 * Calculates the inverse hyperbolic tangent of the given <tt>MathObject</tt>.
	 * This works only if <tt>m</tt> is a <tt>MScalar</tt>. The inverse hyperbolic
	 * tangent is defined (and calculated by) {@code atanh(z)=ln((z+1)/(z-1))/2} for
	 * all z, either real or complex.
	 * 
	 * @param m a <tt>MScalar</tt> of which the inverse hyperbolic tangent should be
	 *          calculated.
	 * @return a <tt>MScalar</tt> containing the number whose hyperbolic tangent
	 *         equals <tt>m</tt> (real arguments give a real result, complex
	 *         arguments give a complex result).
	 * @throws InvalidOperationException if <tt>m</tt> is not an <tt>MScalar</tt>
	 */
	public static MScalar atanh(MScalar m) {
		MScalar s = m.copy();
		return ln(s.copy().add(1).divide(s.subtract(1).negate())).divide(2);
	}

	// ############### CONVERSION FUNCTIONS ###############
	/**
	 * Converts the given <tt>MathObject</tt> from radians to degrees, element-wise
	 * if necessary
	 * 
	 * @param m <tt>MathObject</tt> to be converted to degrees.
	 * @return a <tt>MathObject</tt> of the same type as m.
	 * @throws InvalidOperationException if the given <tt>MathObject</tt> cannot be
	 *                                   converted to degrees.
	 */
	public static MScalar toDegree(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.toDegrees(((MReal) m).getValue()));
		else
			throw new InvalidOperationException("Can't convert " + Tools.type(m) + " to degrees.");
	}

	/**
	 * Converts the given <tt>MathObject</tt> from degrees to radians, element-wise
	 * if necessary
	 * 
	 * @param m <tt>MathObject</tt> to be converted to radians.
	 * @return a <tt>MathObject</tt> of the same type as m.
	 * @throws InvalidOperationException if the given <tt>MathObject</tt> cannot be
	 *                                   converted to radians.
	 */
	public static MScalar toRadians(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.toRadians(((MReal) m).getValue()));
		throw new InvalidOperationException("Can't convert " + Tools.type(m) + " to degrees.");
	}

	// ############### SCALAR FUNCTIONS ###############
	/**
	 * Calculates the absolute value (distance to the origin) of the given
	 * <tt>MathObject</tt>, for vectors and complex values this equals the Euclidean
	 * norm.
	 * 
	 * @param m <tt>MathObject</tt> of which the absolute value should be
	 *          calculated.
	 * @return a <tt>MReal</tt> containing the absolute value.
	 * @throws InvalidOperationException if the absolute value is not defined for
	 *                                   the type of <tt>m</tt>.
	 */
	public static MReal abs(MScalar m) {
		return new MReal(((MScalar) m).abs());
		//else if (m instanceof MVector)
			//return Norm.eucl((MVector) m);
	}

	/**
	 * returns the natural logarithm of the given <tt>MathObject</tt>. This works
	 * only if m is a <tt>MScalar</tt>. If the argument is complex the principle
	 * value is returned: {@code log(z)=ln(r)+i*phi}, where {@code -pi<=phi<=pi}.
	 * 
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the natural logarithm of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#log(double)
	 */
	public static MScalar ln(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.log(((MReal) m).getValue()));
		return new MComplex(Math.log(((MComplex) m).getR()), ((MComplex) m).arg());
	}

	/**
	 * returns the logarithm with base 10 of the given <tt>MathObject</tt>. This
	 * works only if m is a <tt>MScalar</tt>. If the argument is complex
	 * {@code ln(m)/ln(10)} is returned using {@link #ln(MathObject)}.
	 * 
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the 10-logarithm of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#log10(double)
	 */
	public static MScalar log(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.log10(((MReal) m).getValue()));
		return ((MComplex) ln(m)).divide(Math.log(10));
	}

	/**
	 * returns the value of the exponential function evaluated at the given
	 * <tt>MScalar</tt>. If the argument is complex
	 * {@code exp(Re(m))*(cos(Im(m))+�sin(Im(m)))} (according to the definition) is
	 * returned.
	 * 
	 * @param m the argument, an {@code MScalar}
	 * @return a <tt>MScalar</tt> containing the exponential of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#exp(double)
	 */
	public static MScalar exp(MScalar m) {
		if (m instanceof MReal)
			return new MReal(Math.exp(((MReal) m).getValue()));
		MComplex z = (MComplex) m;
		return new MComplex(Math.cos(z.imag()), Math.sin(z.imag())).multiply(Math.exp(z.real()));
//		} else if (m instanceof MMatrix)
	//		throw new InvalidOperationException(
		//			"The matrix exponential, though defined, has not yet been implemented.");
	}

	/**
	 * returns the square root of the given <tt>MathObject</tt>. This works only if
	 * m is a <tt>MScalar</tt>. If the argument is complex {@code m^0.5} is returned
	 * using {@link MComplex#power(double)}.
	 * 
	 * @param m the argument
	 * @return a <tt>MScalar</tt> containing the square root of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see Math#sqrt(double)
	 */
	public static MScalar sqrt(MScalar m) {
		if (m instanceof MReal) {
			if (((MReal) m).getValue() < 0 && (Settings.getBool(Settings.COMPLEX_ENABLED)))
				return new MComplex(0, Math.sqrt(((MReal) m).getValue() * -1));
			return new MReal(Math.sqrt(((MReal) m).getValue()));
		}
		return ((MComplex) m.copy()).power(0.5);
	}

	/**
	 * returns the n-th root {@code =m^(1/n)} of the given <tt>MathObject</tt>. This
	 * works only if m is a <tt>MScalar</tt>
	 * 
	 * @param m the argument
	 * @param n the base of the root (can be any real or complex number)
	 * @return a <tt>MScalar</tt> containing the n-th root of <tt>m</tt>
	 * @throws InvalidOperationException if <tt>m</tt> is not a <tt>MScalar</tt>
	 * @see MScalar#power(double)
	 */
	public static MScalar root(MScalar m, MScalar n) {
		return ((MScalar) m.copy()).power(((MScalar) n).invert());
	}
	
	public static MScalar conj(MScalar m) {
		return m.copy().conjugate();
	}

	/**
	 * Calculates n! (n factorial) using {@link Tools#fact(int)}.
	 * 
	 * @param m a MathObject
	 * @return n! (where n is the value of m).
	 * @throws IllegalArgumentException if n is not a positive integer value.
	 */
	public static MReal fact(MScalar m) {
		if (m instanceof MReal && ((MReal) m).isInteger()) {
			int n = (int) ((MReal) m).getValue();
			return new MReal(Tools.fact(n));
		}
		throw new IllegalArgumentException("Factorial is only defined for integer values, got " + m.toString());
	}

	public static MScalar det(MMatrix m) {
		if (m.isSquare()) {
			return m.det();
		}
		throw new IllegalArgumentException(
				"The determinant is only defined for (square) matrices, got shape " + m.shape());
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
