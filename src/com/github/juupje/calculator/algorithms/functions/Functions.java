package com.github.juupje.calculator.algorithms.functions;

import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MRealError;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.settings.Settings;

public class Functions {
	// ############### SCALAR FUNCTIONS ###############
		/**
		 * returns the natural logarithm of the given <tt>MReal</tt>.
		 * @param m the argument
		 * @return a <tt>MReal</tt> containing the natural logarithm of <tt>m</tt>
		 * @see Math#log(double)
		 */
		public static MReal ln(MReal m) {
			return new MReal(Math.log(m.getValue()));
		}
		/**
		 * returns the natural logarithm of the given <tt>MComplex</tt>.
		 * @param m the argument
		 * @return a <tt>MComplex</tt> containing the principle value of the natural logarithm of <tt>m</tt>:
		 * 	{@code ln(z)=ln(r)+i*phi}, where {@code -pi<=phi<=pi}.
		 */
		public static MComplex ln(MComplex m) {
			return new MComplex(Math.log(m.getR()), m.arg());
		}
		/**
		 * returns the natural logarithm and propagates the error of the given <tt>MRealError</tt>.
		 * Note that the error propagation is unreliable for x~0.
		 * @param m the argument
		 * @return a <tt>MRealError</tt> containing the natural logarithm of <tt>m</tt>
		 * @see Math#log(double)
		 */
		public static MRealError ln(MRealError m) {
			return new MRealError(Math.log(m.getValue()), m.err()/m.getValue());
		}
		
		/**
		 * Returns the base-10 logarithm of the given <tt>MReal</tt>.
		 * @param m the argument
		 * @return a <tt>MReal</tt> containing the logarithm of <tt>m</tt>
		 * @see Math#log10(double)
		 */
		public static MReal log(MReal m) {
			return new MReal(Math.log10(m.getValue()));
		}
		/**
		 * returns the base-10 logarithm of the given <tt>MComplex</tt>.
		 * @param m the argument
		 * @return a <tt>MComplex</tt> containing the principle value of the logarithm of <tt>m</tt>:
		 * 	{@code log(z)=log(r)+i*phi}, where {@code -pi<=phi<=pi}.
		 */
		public static MComplex log(MComplex m) {
			return ln(m).divide(Math.log(10));
		}
		/**
		 * returns the base-10 logarithm and propagates the error of the given <tt>MRealError</tt>.
		 * Note that the error propagation is unreliable for x~0.
		 * @param m the argument
		 * @return a <tt>MRealError</tt> containing the logarithm of <tt>m</tt>
		 * @see Math#log10(double)
		 */
		public static MRealError log(MRealError m) {
			return new MRealError(Math.log10(m.getValue()), m.err()/(m.getValue()*Math.log(10)));
		}	

		/**
		 * Returns the value of the exponential function evaluated at the given
		 * <tt>MReal</tt>.
		 * 
		 * @param m the argument, an {@code MReal}
		 * @return a <tt>MReal</tt> containing the exponential of <tt>m</tt>
		 * @see Math#exp(double)
		 */
		public static MReal exp(MReal m) {
			return new MReal(Math.exp(m.getValue()));
		}
		
		/**
		 * Returns the value of the complex exponential function evaluated at the given
		 * <tt>MComplex</tt> which is a complex number with absolute value
		 * {@code r=exp(Re(z))} and phase {$code psi=Im(z)}.
		 * @param z the argument, an {@code MComplex}
		 * @return an <tt>MComplex</tt> containing the exponential of <tt>z</tt>
		 */
		public static MComplex exp(MComplex z) {
			return MComplex.fromPolar(Math.exp(z.real()), z.imag());
		}
		
		/**
		 * Returns the value of the exponential function evaluated at the given
		 * <tt>MRealError</tt> and propagates the error.
		 * 
		 * @param m the argument, an {@code MRealError}
		 * @return a <tt>MRealError</tt> containing the exponential of <tt>m</tt>
		 * @see Math#exp(double)
		 */
		public static MRealError exp(MRealError m) {
			double exp = Math.exp(m.getValue());
			return new MRealError(exp, exp*m.err());
		}

		/**
		 * Returns the square root of the given <tt>MReal</tt>.
		 * 
		 * @param m the argument
		 * @return an <tt>MReal</tt> containing the square root of <tt>m</tt> if m>=0. 
		 * If the setting {@code COMPLEX_ENABLED} is true, a complex number will be
		 * returned for negative arguments, otherwise NaN will be returned.
		 * @see Math#sqrt(double)
		 */
		public static MScalar sqrt(MReal m) {
			if (m.getValue() < 0 && (Settings.getBool(Settings.COMPLEX_ENABLED)))
				return new MComplex(0, Math.sqrt(m.getValue() * -1));
			return new MReal(Math.sqrt(m.getValue())); //will return NaN for negative numbers
		}
		
		/**
		 * Returns the square root of the given <tt>MComplex</tt>.
		 * 
		 * @param m the argument
		 * @return an <tt>MComplex</tt> containing the square root of <tt>m</tt>.
		 */
		public static MComplex sqrt(MComplex m) {
			return m.copy().power(0.5);
		}
		
		/**
		 * Returns the square root of the given <tt>MRealError</tt>.
		 * 
		 * @param m the argument
		 * @return an <tt>MRealError</tt> containing the square root of <tt>m</tt> if m>=0.
		 * @see Math#sqrt(double)
		 */
		public static MRealError sqrt(MRealError m) {
			if (m.getValue() < 0)
				throw new InvalidOperationException("Cannot propagate error of a negative value through a square root.");
			return new MRealError(Math.sqrt(m.getValue()), m.err()/(2*Math.sqrt(m.getValue()))); //will return NaN for negative numbers
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
			return m.copy().power(n).invert();
		}

		/**
		 * Calculates n! (n factorial) using {@link Tools#fact(int)}.
		 * 
		 * @param m a MathObject
		 * @return n! (where n is the value of m).
		 * @throws IllegalArgumentException if n is not a positive integer value.
		 */
		public static MReal fact(MReal m) {
			if (m.isInteger()) {
				return new MReal(Tools.fact((int) m.getValue()));
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
}
