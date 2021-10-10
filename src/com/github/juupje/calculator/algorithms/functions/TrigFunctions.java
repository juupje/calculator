package com.github.juupje.calculator.algorithms.functions;

import com.github.juupje.calculator.mathobjects.*;

public class TrigFunctions {
	// ############### TRIGONOMETRIC FUNCTIONS ###############
	/**
	 * Calculates the sine of the given <tt>MReal</tt>. 
	 * 
	 * @param m a <tt>MReal</tt> of which the sine should be calculated (in radians).
	 * @return a <tt>MReal</tt> containing the sine of <tt>m</tt>.
	 * @see Math#sin(double)
	 */
	public static MReal sin(MReal m) {
		return new MReal(Math.sin(m.getValue()));
	}
	
	/**
	 * Calculates the sine of the given <tt>MComplex</tt>.
	 * The formula {@code sin(a+bi)=sin(a)cosh(b)+i*cos(a)sinh(b)} is used.
	 * @param z a <tt>MComplex</tt> of which the sine should be calculated (in radians).
	 * @return a <tt>MComplex</tt> containing the sine of <tt>m</tt>.
	 */
	public static MComplex sin(MComplex z) {
		return new MComplex(Math.sin(z.real()) * Math.cosh(z.imag()),
				-1 * Math.cos(z.real()) * Math.sinh(z.imag()));
	}
	
	/**
	 * Calculates the sine of the given <tt>MRealError</tt>.
	 * The formula {@code sin(x+-sx)=sin(x)+-|cos(x)|*sx} is used.
	 * @param z a <tt>MRealError</tt> of which the sine should be calculated (in radians).
	 * @return a <tt>MRealError</tt> containing the sine of <tt>m</tt>.
	 * @see Math#sin(double)
	 */
	public static MRealError sin(MRealError m) {
		//sigma=cos(x)*sigma_x
		//absolute value is taken care of by constructor
		return new MRealError(Math.sin(m.getValue()), Math.cos(m.getValue())*m.err());
	}
	
	/**
	 * Calculates the cosine of the given <tt>MReal</tt>. 
	 * 
	 * @param m a <tt>MReal</tt> of which the cosine should be calculated (in radians).
	 * @return a <tt>MReal</tt> containing the cosine of <tt>m</tt>.
	 * @see Math#cos(double)
	 */
	public static MReal cos(MReal m) {
		return new MReal(Math.cos(m.getValue()));
	}
	
	/**
	 * Calculates the cosine of the given <tt>MComplex</tt>.
	 * The formula {@code cos(a+bi)=cos(a)cosh(b)-i*sin(a)sinh(b)} is used.
	 * @param z a <tt>MComplex</tt> of which the cosine should be calculated (in radians).
	 * @return a <tt>MComplex</tt> containing the cosine of <tt>m</tt>.
	 */
	public static MComplex cos(MComplex z) {
		return new MComplex(Math.cos(z.real()) * Math.cosh(z.imag()), Math.sin(z.real()) * Math.sinh(z.imag()));
		}
	
	/**
	 * Calculates the cosine of the given <tt>MRealError</tt>.
	 * The formula {@code cos(x+-sx)=cos(x)+-|sin(x)|*sx} is used.
	 * @param z a <tt>MRealError</tt> of which the cosine should be calculated (in radians).
	 * @return a <tt>MRealError</tt> containing the cosine of <tt>m</tt>.
	 * @see Math#cos(double)
	 */
	public static MRealError cos(MRealError m) {
		//sigma=sin(x)*sigma_x
		//absolute value is taken care of by constructor
		return new MRealError(Math.cos(m.getValue()), Math.sin(m.getValue())*m.err());
	}
	
	/**
	 * Calculates the tangent of the given <tt>MReal</tt>. 
	 * 
	 * @param m a <tt>MReal</tt> of which the tangent should be calculated (in radians).
	 * @return a <tt>MReal</tt> containing the tangent of <tt>m</tt>.
	 * @see Math#tan(double)
	 */
	public static MReal tan(MReal m) {
		return new MReal(Math.tan(m.getValue()));
	}
	
	/**
	 * Calculates the tangent of the given <tt>MComplex</tt>.
	 * The formula {@code tan(a+bi)=(sin(2a)+i*sinh(2b))/(cos(2a)+cosh(2b))} is used.
	 * @param z a <tt>MComplex</tt> of which the tangent should be calculated (in radians).
	 * @return a <tt>MComplex</tt> containing the tangent of <tt>m</tt>.
	 */
	public static MComplex tan(MComplex z) {
		double d = Math.cos(2 * z.real()) + Math.cosh(2 * z.imag());
		return new MComplex(Math.sin(2 * z.real()) / d, Math.sinh(2 * z.imag()) / d);
	}
	
	/**
	 * Calculates the tangent of the given <tt>MRealError</tt>.
	 * The formula {@code tan(x+-sx)=tan(x)+-sx/cos(x)^2} is used.
	 * @param z a <tt>MRealError</tt> of which the tangent should be calculated (in radians).
	 * @return a <tt>MRealError</tt> containing the tangent of <tt>m</tt>.
	 * @see Math#tan(double)
	 */
	public static MRealError tan(MRealError m) {
		//sigma=sigma_x/(cos(x)^2)
		double cos = Math.cos(m.getValue());
		return new MRealError(Math.tan(m.getValue()), m.err()/(cos*cos));
	}

	/**
	 * Calculates the hyperbolic sine of the given <tt>MReal</tt>.
	 * The hyperbolic sine is defined as
	 * {@code sinh(z)=(e^z-e^-z)/2} for all z.
	 * 
	 * @param m a <tt>MReal</tt> of which the hyperbolic sine should be
	 *          calculated.
	 * @return a <tt>MReal</tt> containing the hyperbolic sine of <tt>m</tt>.
	 * @see Math#sinh(double)
	 */
	public static MReal sinh(MReal m) {
		return new MReal(Math.sinh(m.getValue()));
	}
	
	/**
	 * Calculates the hyperbolic sine of the given <tt>MComplex</tt>.
	 * The hyperbolic sine is defined (and calculated) as
	 * {@code sinh(z)=(e^z-e^-z)/2} for all z.
	 * 
	 * @param m a <tt>MComplex</tt> of which the hyperbolic sine should be
	 *          calculated.
	 * @return a <tt>MComplex</tt> containing the hyperbolic sine of <tt>m</tt>.
	 */
	public static MComplex sinh(MComplex m) {
		MComplex z = m.copy();
		return Functions.exp(z).subtract(Functions.exp(z.negate())).divide(2);
	}
	
	/**
	 * Calculates the hyperbolic sine and propagates the error of the given <tt>MRealError</tt>.
	 * The hyperbolic sine is defined as
	 * {@code sinh(z)=(e^z-e^-z)/2} for all z.
	 * 
	 * @param m a <tt>MRealError</tt> of which the hyperbolic sine should be
	 *          calculated.
	 * @return a <tt>MRealError</tt> containing the hyperbolic sine of <tt>m</tt>.
	 * @see Math#sinh(double)
	 */
	public static MRealError sinh(MRealError m) {
		return new MRealError(Math.sinh(m.getValue()), Math.cosh(m.getValue())*m.err());
	}
	
	/**
	 * Calculates the hyperbolic cosine of the given <tt>MReal</tt>.
	 * The hyperbolic cosine is defined as
	 * {@code cosh(z)=(e^z+e^-z)/2} for all z.
	 * 
	 * @param m a <tt>MReal</tt> of which the hyperbolic cosine should be
	 *          calculated.
	 * @return a <tt>MReal</tt> containing the hyperbolic cosine of <tt>m</tt>.
	 * @see Math#cosh(double)
	 */
	public static MReal cosh(MReal m) {
		return new MReal(Math.cosh(m.getValue()));
	}
	
	/**
	 * Calculates the hyperbolic cosine of the given <tt>MComplex</tt>.
	 * The hyperbolic cosine is defined (and calculated) as
	 * {@code cosh(z)=(e^z+e^-z)/2} for all z.
	 * 
	 * @param m a <tt>MComplex</tt> of which the hyperbolic cosine should be
	 *          calculated.
	 * @return a <tt>MComplex</tt> containing the hyperbolic cosine of <tt>m</tt>.
	 */
	public static MComplex cosh(MComplex m) {
		MComplex exp = Functions.exp(m);
		return exp.add(exp.copy().invert()).divide(2);
	}
	
	/**
	 * Calculates the hyperbolic cosine and propagates the error of the given <tt>MRealError</tt>.
	 * The hyperbolic cosine is defined as
	 * {@code cosh(z)=(e^z+e^-z)/2} for all z.
	 * 
	 * @param m a <tt>MRealError</tt> of which the hyperbolic cosine should be
	 *          calculated.
	 * @return a <tt>MRealError</tt> containing the hyperbolic cosine of <tt>m</tt>.
	 * @see Math#cosh(double)
	 */
	public static MRealError cosh(MRealError m) {
		return new MRealError(Math.cosh(m.getValue()), Math.sinh(m.getValue())*m.err());
	}
	
	/**
	 * Calculates the hyperbolic tangent or the given <tt>MReal</tt>.
	 * @param m a <tt>MReal</tt> of which the hyperbolic tangent should be
	 *          calculated.
	 * @return a <tt>MReal</tt> containing the hyperbolic tangent of <tt>m</tt>.
	 * @see Math#tanh(double)
	 */
	public static MReal tanh(MReal m) {
		return new MReal(Math.tanh(m.getValue()));
	}
	//other tanh's are calculated using sinh/cosh
	
	/**
	 * Calculates the arc sine of the given <tt>MReal</tt>. 
	 * The argument has to be in the interval [-1,1]
	 * 
	 * @param m a <tt>MReal</tt> of which the arc sine should be calculated.
	 * @return a <tt>MReal</tt> containing the arc sine of <tt>m</tt> in the range <tt>[-pi/2, pi/2]</tt>.\
	 * @see Math#asin(double)
	 */
	public static MReal asin(MReal m) {
		double val = m.getValue();
		if (val > 1 || val < -1)
			throw new IllegalArgumentException("Domain error: arcsin is defined on [-1,1], got " + val);
		return new MReal(Math.asin(val));
	}
	
	/**
	 * Calculates the arc sine of the given <tt>MComplex</tt>. 
	 * The formula {@code asin(z)=-i*ln(iz+sqrt(1-z^2))} is used, using
	 * {@link #ln(MathObject)}.
	 * 
	 * @param z a <tt>MComplex</tt> of which the arc sine should be calculated.
	 * @return a <tt>MComplex</tt> containing the arc sine of <tt>z</tt>.
	 */
	public static MComplex asin(MComplex z) {
		MComplex v = z.copy().power(2).negate().add(1);
		v.power(0.5).add(z.copy().multiply(0, 1));
		return Functions.ln(v).multiply(0, -1);
	}
	
	/**
	 * Calculates the arc sine and propagates the error of the given <tt>MRealError</tt>. 
	 * The argument has to be in the interval [-1,1] (excluding the error).
	 * The error is propagates using {@code sigma=sigma_x/sqrt(1-x^2)}.
	 * Note that this calculation is not reliable for |x| close to 1.
	 * 
	 * @param m a <tt>MRealError</tt> of which the arc sine should be calculated.
	 * @return a <tt>MRealError</tt> containing the arc sine of <tt>m</tt> in the range <tt>[-pi/2, pi/2]</tt>.
	 * @see Math#asin(double)
	 */
	public static MRealError asin(MRealError m) {
		double val = m.getValue();
		if (val > 1 || val < -1)
			throw new IllegalArgumentException("Domain error: arcsin is defined on [-1,1], got " + val);
		return new MRealError(Math.asin(val), m.err()/Math.sqrt(1-val*val));
	}
	
	/**
	 * Calculates the arc cosine of the given <tt>MReal</tt>. 
	 * The argument has to be in the interval [-1,1]
	 * 
	 * @param m a <tt>MReal</tt> of which the arc cosine should be calculated.
	 * @return a <tt>MReal</tt> containing the arc cosine of <tt>m</tt> in the range <tt>[0, pi]</tt>.
	 * @see Math#acos(double)
	 */
	public static MReal acos(MReal m) {
		double val = m.getValue();
		if (val > 1 || val < -1)
			throw new IllegalArgumentException("Domain error: arccos is defined on [-1,1], got " + val);
		return new MReal(Math.acos(val));
	}
	
	/**
	 * Calculates the arc cosine of the given <tt>MComplex</tt>. 
	 * The formula {@code acos(z)=-i*ln(z+i*sqrt(1-z^2))} is used, using
	 * {@link #ln(MComplex)}.
	 * 
	 * @param z a <tt>MComplex</tt> of which the arc cosine should be calculated.
	 * @return a <tt>MComplex</tt> containing the arc cosine of <tt>z</tt>.
	 */
	public static MComplex acos(MComplex z) {
		MComplex w = z.copy().multiply(z).negate().add(1);
		return Functions.ln(w.power(0.5).multiply(0, 1).add(z)).multiply(0, -1);
	}
	
	/**
	 * Calculates the arc cosine and propagates the error of the given <tt>MRealError</tt>. 
	 * The argument has to be in the interval [-1,1] (excluding the error).
	 * The error is propagates using {@code sigma=sigma_x/sqrt(1-x^2)}.
	 * Note that this calculation is not reliable for |x| close to 1.
	 * 
	 * @param m a <tt>MRealError</tt> of which the arc cosine should be calculated.
	 * @return a <tt>MRealError</tt> containing the arc cosine and propagated error of <tt>m</tt> in the range <tt>[0, pi]</tt>.
	 * @see Math#acos(double)
	 */
	public static MRealError acos(MRealError m) {
		double val = m.getValue();
		if (val > 1 || val < -1)
			throw new IllegalArgumentException("Domain error: arccos is defined on [-1,1], got " + val);
		return new MRealError(Math.acos(m.getValue()), m.err()/Math.sqrt(1-val*val));
	}
	
	/**
	 * Calculates the arc tangent of the given <tt>MReal</tt>.
	 * 
	 * @param m a <tt>MReal</tt> of which the arc tangent should be calculated.
	 * @return a <tt>MReal</tt> containing the arc tangent of <tt>m</tt> in the range <tt>[-pi/2, pi/2]</tt>.
	 * @see Math#atan(double)
	 */
	public static MReal atan(MReal m) {
		return new MReal(Math.atan(m.getValue()));
	}
	
	/**
	 * Calculates the arc tangent of the given <tt>MComplex</tt>. 
	 * The formula {@code acos(z)=-i*ln(z+i*sqrt(1-z^2))} is used, using
	 * {@link #ln(MComplex)}.
	 * 
	 * @param z a <tt>MComplex</tt> of which the arc tangent should be calculated.
	 * @return a <tt>MComplex</tt> containing the arc tangent of <tt>z</tt>.
	 */
	public static MComplex atan(MComplex z) {
		double a = z.real();
		double b = z.imag();
		double c = a * a + (b + 1) * (b + 1);
		return Functions.ln(new MComplex((-a * a - b * b + 1) / c, 2 * a / c)).multiply(0, -0.5);
	}
	
	/**
	 * Calculates the arc tangent and propagates the error of the given <tt>MRealError</tt>.
	 * The error is propagates using {@code sigma=sigma_x/sqrt(1+x^2)}.
	 * 
	 * @param m a <tt>MRealError</tt> of which the arc tangent should be calculated.
	 * @return a <tt>MRealError</tt> containing the arc tangent and propagated error of <tt>m</tt> in the range <tt>[-pi/2, pi/2]</tt>.
	 * @see Math#atan(double)
	 */
	public static MRealError atan(MRealError m) {
		double val = m.getValue();
		return new MRealError(Math.atan(val), m.err()/Math.sqrt(1+val*val));
	}

	public static double asinh(double x) {
		return Math.log(x + Math.sqrt(x*x + 1));
	}
	/**
	 * Calculates the inverse hyperbolic sine of the given <tt>MReal</tt>.
	 * The inverse hyperbolic sine is defined (and calculated) as
	 * {@code asinh(x)=ln(x+sqrt(x^2+1))} for all x.
	 * 
	 * @param m an <tt>MReal</tt> of which the inverse hyperbolic sine should be
	 *          calculated.
	 * @return an <tt>MReal</tt> containing the number whose hyperbolic sine equals
	 *         <tt>m</tt>.
	 */	
	public static MReal asinh(MReal m) {
		return new MReal(asinh(m.getValue()));
	}
	
	/**
	 * Calculates the inverse hyperbolic sine of the given <tt>MComplex</tt>.
	 * The inverse hyperbolic sine is defined (and calculated) as
	 * {@code asinh(z)=ln(z+sqrt(z^2+1))} for all z.
	 * 
	 * @param m an <tt>MComplex</tt> of which the inverse hyperbolic sine should be
	 *          calculated.
	 * @return an <tt>MComplex</tt> containing the number whose hyperbolic sine equals
	 *         <tt>m</tt>.
	 */	
	public static MComplex asinh(MComplex z) {
		MComplex s = z.copy();
		return Functions.ln(s.add(Functions.sqrt(s.copy().multiply(s).add(1))));
	}
	
	/**
	 * Calculates the inverse hyperbolic sine and propagates the error of the given <tt>MRealError</tt>.
	 * The inverse hyperbolic sine is defined (and calculated) as
	 * {@code asinh(x)=ln(x+sqrt(x^2+1))} for all x.
	 * 
	 * @param m an <tt>MRealError</tt> of which the inverse hyperbolic sine should be
	 *          calculated.
	 * @return an <tt>MRealError</tt> containing the number whose hyperbolic sine equals
	 *         <tt>m</tt>.
	 */	
	public static MRealError asinh(MRealError m) {
		return new MRealError(asinh(m.getValue()), m.err()/Math.sqrt(m.abs2()+1));
	}

	public static double acosh(double x) {
		return Math.log(x + Math.sqrt(x*x - 1));
	}
	
	/**
	 * Calculates the inverse hyperbolic cosine of the given <tt>MReal</tt>.
	 * The inverse hyperbolic cosine is defined (and calculated) as
	 * {@code acosh(x)=ln(x+sqrt(x^2-1))} for all x.
	 * 
	 * @param m an <tt>MReal</tt> of which the inverse hyperbolic cosine should be
	 *          calculated.
	 * @return an <tt>MReal</tt> containing the number whose hyperbolic cosine equals
	 *         <tt>m</tt>.
	 */	
	public static MScalar acosh(MReal m) {
		if(m.getValue()>1)
			return new MReal(acosh(m.getValue()));
		else if(m.getValue()==1)
			return new MReal(0);
		else {
			return acosh(new MComplex(m));
		}
	}
	
	/**
	 * Calculates the inverse hyperbolic sine of the given <tt>MComplex</tt>.
	 * The inverse hyperbolic sine is defined (and calculated) as
	 * {@code acosh(z)=ln(z+sqrt(z^2-1))} for all z.
	 * 
	 * @param m an <tt>MComplex</tt> of which the inverse hyperbolic sine should be
	 *          calculated.
	 * @return an <tt>MComplex</tt> containing the number whose hyperbolic sine equals
	 *         <tt>m</tt>.
	 */	
	public static MComplex acosh(MComplex z) {
		MComplex s = z.copy();
		return Functions.ln(s.add(Functions.sqrt(s.copy().multiply(s).subtract(1))));
	}
	
	/**
	 * Calculates the inverse hyperbolic cosine and propagates the error of the given <tt>MRealError</tt>.
	 * The inverse hyperbolic cosine is defined (and calculated) as
	 * {@code acosh(x)=ln(x+sqrt(x^2-1))} for all x>=1.
	 * Note that the error calculation breaks down if x~1.
	 * 
	 * @param m an <tt>MRealError</tt> of which the inverse hyperbolic cosine should be
	 *          calculated.
	 * @return an <tt>MRealError</tt> containing the number whose hyperbolic cosine equals
	 *         <tt>m</tt>.
	 */	
	public static MRealError acosh(MRealError m) {
		double val = m.getValue();
		if(val>1)
			return new MRealError(acosh(m.getValue()), m.err()/Math.sqrt(m.abs2()-1));
		else if(val==1)
			return new MRealError(0, Double.NaN);
		else
			throw new IllegalArgumentException("Domain error: the real acosh is defined on [1,infinity), got " + val);
	}
	
	public static double atanh(double x) {
		return Math.log((1+x)/(1-x))/2;
	}
	
	/**
	 * Calculates the inverse hyperbolic tangent of the given <tt>MReal</tt>.
	 * The inverse hyperbolic tangent is defined (and calculated) as
	 * {@code atanh(x)=ln((1+z)/(1-z))/2} for all x.
	 * 
	 * @param m an <tt>MReal</tt> of which the inverse hyperbolic tangent should be
	 *          calculated.
	 * @return if {@code -1<x<1}: an <tt>MReal</tt> containing the number whose hyperbolic tangent equals
	 *         <tt>m</tt>.
	 *         otherwise: an <tt>MComplex</tt> containing the number whose hyperbolic tangent equals <tt>m</tt>.
	 */	
	public static MScalar atanh(MReal m) {
		if(m.abs2()<1)
			return new MReal(atanh(m.getValue()));
		else if(m.abs2()==1)
			return new MReal(Double.NaN);
		else {
			return atanh(new MComplex(m));
		}
	}
	
	/**
	 * Calculates the inverse hyperbolic tangent of the given <tt>MComplex</tt>.
	 * The inverse hyperbolic tangent is defined (and calculated) as
	 * {@code atanh(z)=ln((1+z)/(1-z))/2} for all z.
	 * 
	 * @param m an <tt>MComplex</tt> of which the inverse hyperbolic tangent should be
	 *          calculated.
	 * @return an <tt>MComplex</tt> containing the number whose hyperbolic tangent equals
	 *         <tt>m</tt>.
	 */	
	public static MComplex atanh(MComplex z) {
		MComplex s = z.copy();
		return Functions.ln(s.copy().add(1).divide(s.subtract(1).negate())).divide(2);
	}
	
	/**
	 * Calculates the inverse hyperbolic tangent and propagates the error of the given <tt>MRealError</tt>.
	 * The inverse hyperbolic tangent is defined (and calculated) as
	 * {@code atanh(x)=ln((1+x)/(1-x))/2} for all {@code -1<x<1}.
	 * Note that the error calculation breaks down if |x|~1.
	 * 
	 * @param m an <tt>MRealError</tt> of which the inverse hyperbolic tangent should be
	 *          calculated.
	 * @return an <tt>MRealError</tt> containing the number whose hyperbolic tangent equals
	 *         <tt>m</tt>.
	 */	
	public static MRealError atanh(MRealError m) {
		if(m.abs2()<1)
			return new MRealError(atanh(m.getValue()), m.err()/(m.abs2()-1));
		else if(m.abs2()==1)
			return new MRealError(Double.NaN, Double.NaN);
		else
			throw new IllegalArgumentException("Domain error: the real acosh is defined on (-1,1), got " + m.getValue());
	}
	

	// ############### CONVERSION FUNCTIONS ###############
	/**
	 * Converts the given <tt>MReal</tt> from radians to degrees.
	 * 
	 * @param m <tt>MReal</tt> to be converted to degrees.
	 * @return the same angle but in degrees
	 */
	public static MReal toDegree(MReal m) {
		return new MReal(Math.toDegrees(m.getValue()));
	}
	/**
	 * Converts the given <tt>MComplex</tt> from radians to degrees.
	 * 
	 * @param m <tt>MComplex</tt> to be converted to degrees.
	 * @return the same angle but in degrees
	 */
	public static MComplex toDegree(MComplex z) {
		return new MComplex(Math.toDegrees(z.real()), Math.toDegrees(z.imag()));
	}
	/**
	 * Converts the given <tt>MRealError</tt> from radians to degrees.
	 * 
	 * @param m <tt>MRealError</tt> to be converted to degrees.
	 * @return the same angle but in degrees
	 */
	public static MRealError toDegree(MRealError m) {
		return new MRealError(Math.toDegrees(m.getValue()), Math.toDegrees(m.err()));
	}

	/**
	 * Converts the given <tt>MReal</tt> from degrees to radians.
	 * 
	 * @param m <tt>MReal</tt> to be converted to radians.
	 * @return the same angle in radians
	 */
	public static MReal toRadians(MReal m) {
		return new MReal(Math.toRadians(m.getValue()));
	}

	/**
	 * Converts the given <tt>MComplex</tt> from degrees to radians.
	 * @param m <tt>MReal</tt> to be converted to radians.
	 * @return the same angle in radians
	 */
	public static MComplex toRadians(MComplex m) {
		return new MComplex(Math.toRadians(m.real()), Math.toRadians(m.imag()));
	}
	
	/**
	 * Converts the given <tt>MRealError</tt> from degrees to radians.
	 * @param m <tt>MReal</tt> to be converted to radians.
	 * @return the same angle in radians
	 */
	public static MRealError toRadians(MRealError m) {
		return new MRealError(Math.toRadians(m.getValue()), Math.toRadians(m.err()));
	}
}