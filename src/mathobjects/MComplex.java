package mathobjects;

import helpers.Tools;

public class MComplex extends MScalar {

	double a, b, r, phi;
	boolean polar, cartesian;
	
	/**
	 * Constructs a Cartesian complex number of the form a+bi.
	 * @param a the real component of the complex number.
	 * @param b the imaginary component of the complex number.
	 */
	public MComplex(double a, double b) {
		this.a = a;
		this.b = b;
		polar = false;
		cartesian = true;
	}
	
	/**
	 * Constructs a new complex number consisting only of a real component.
	 * @param real the real component of the new complex number.
	 */
	public MComplex(MReal real) {
		a = r = real.getValue();
		b = phi = 0;
		polar = cartesian = true;
	}
	
	/**
	 * Constructs an empty complex number (that is, both imaginary and real part are zero).
	 */
	public MComplex() {
		a = b = r = phi = 0;
		cartesian = polar = true;
	}
	
	/**
	 * returns the real component of the complex number.
	 * This recalculates the Cartesian form.
	 * @return {@code Re(z)}, where z is this number.
	 */
	public double real() {
		if(!cartesian)
			updateCartesian();
		return a;
	}

	/**
	 * Sets the real component of the complex number.
	 * This invalidates the polar form and recalculates the Cartesian form.
	 * @param a the new real component.
	 */
	public void real(double a) {
		if(!cartesian)
			b = r*Math.sin(phi);
		this.a = a;
		cartesian = true;
		polar = false;
	}

	/**
	 * Returns the imaginary component of this number.
	 * This recalculates the Cartesian form.
	 * @return {@code Im(z)}, where z is this number.
	 */
	public double imag() {
		if(!cartesian)
			updateCartesian();
		return b;
	}

	/**
	 * Sets the imaginary component of this number.
	 * This invalidates the polar form and recalculates the Cartesian form.
	 * @param b the new imaginary component.
	 */
	public void imag(double b) {
		if(!cartesian)
			a = r*Math.cos(phi);
		this.b = b;
		cartesian = true;
		polar = false;
	}
	
	/**
	 * Sets both Cartesian components. This invalidates the polar form.
	 * @param a the new real component.
	 * @param b the new imaginary component.
	 */
	public void setCartesian(double a, double b) {
		this.a = a;
		this.b = b;
		cartesian = true;
		polar = false;
	}
	
	/**
	 * Returns the radial component of the polar form. This recalculates the polar form.
	 * @return r from the form z=r*e^(i*phi).
	 */
	public double getR() {
		if(!polar)
			updatePolar();
		return r;
	}

	/**
	 * Sets the radial component of the polar form.
	 * This invalidates the Cartesian form and recalculates the polar form.
	 * @param r the new radial component.
	 */
	public void setR(double r) {
		this.r = r;
		if(!polar)
			phi = Math.atan2(b,a);
		polar = true;
		cartesian = false;
	}

	/**
	 * Returns the argument of this complex number. This recalculates the polar form.
	 * @return {@code phi} in {@code z=r*e^(i*phi)}.
	 */
	public double arg() {
		if(!polar)
			updatePolar();
		return phi;
	}

	/**
	 * Sets the argument for this number.
	 * This invalidates the Cartesian form and recalculates the polar form.
	 * @param phi the new phi in the form {@code z=r*e^(i*phi)}.
	 */
	public void setArg(double phi) {
		this.phi = phi;
		if(!polar)
			r = Math.sqrt(a*a+b*b);
		polar = true;
		cartesian = false;
	}
	
	/**
	 * Sets both the polar coordinates.
	 * @param r the new radial component.
	 * @param phi the new polar angle.
	 */
	public void setPolar(double r, double phi) {
		this.r = r;
		this.phi = phi;
		cartesian = false;
		polar = true;
	}

	/**
	 * Recalculates the Cartesian coordinates from the polar coordinates using the formulas:
	 * <p>{@code a=r*cos(phi)}</p>
	 * <p>{@code b=r*sin(phi)}</p>
	 */
	private void updateCartesian() {
		a = r*Math.cos(phi);
		b = r*Math.sin(phi);
		cartesian = true;
	}
	
	/**
	 * Recalculates the polar coordinates from the Cartesian coordinates using the formulas:
	 * <p>{@code r=sqrt(a^2+b^2)}</p>
	 * <p>{@code phi=atan2(b,a)} such that {@code -pi<=phi<=pi}</p>
	 */
	private void updatePolar() {
		r = Math.sqrt(a*a+b*b);
		phi = Math.atan2(b, a);
		polar = true;
	}
	
	/**
	 * Adds a scalar to this number.
	 * This invalidates the polar form if {@code !other.equals(0)} and recalculates the Cartesian form.
	 * @param other the {@code MScalar} to be added to this one.
	 * @return {@code this}
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public MScalar add(MScalar other) {
		if(!cartesian)
			updateCartesian();
		if(other.isComplex()) {
			a += ((MComplex) other).real();
			b += ((MComplex) other).imag();
		} else
			a += ((MReal) other).getValue();
		polar = polar && other.equals(0);
		return this;
	}

	/**
	 * Adds <tt>d</tt> to the real component of this number.
	 * This invalidates the polar form if {@code d!=0} and it recalculates the Cartesian form.
	 * @param d the double to be added to the real component.
	 * @return {@code this}
	 */
	@Override
	public MComplex add(double d) {
		if(!cartesian)
			updateCartesian();
		a += d;
		polar = polar && d==0;
		return this;
	}

	/**
	 * Subtracts a scalar from this number.
	 * This invalidates the polar form if {@code !other.equals(0)} and recalculates the Cartesian form.
	 * @param other the {@code MScalar} to be subtracted off the this one.
	 * @return {@code this}
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public MComplex subtract(MScalar other) {
		if(!cartesian)
			updateCartesian();
		if(other.isComplex()) {
			a -= ((MComplex) other).real();
			b -= ((MComplex) other).imag();
		} else
			a -= ((MReal) other).getValue();
		polar = polar && other.equals(0);
		return this;
	}

	/**
	 * Subtracts <tt>d</tt> from the real component of this number.
	 * This invalidates the polar form if {@code d!=0} and it recalculates the Cartesian form.
	 * @param d the double to be subtracted off the real component.
	 * @return {@code this}
	 */
	@Override
	public MScalar subtract(double d) {
		if(!cartesian)
			updateCartesian();
		a -= d;
		polar = polar && d==0;
		return this;
	}

	/**
	 * Multiplies this number with another scalar (can be complex).
	 * This number's form is preserved, the other is invalidated if {@code d!=1}.
	 * @param other a {@code MComplex} to multiply this with.
	 * @return {@code this}
	 */
	@Override
	public MComplex multiply(MScalar other) {
		if(polar) {
			if(other.isComplex()) {
				r *= ((MComplex) other).getR();
				phi += ((MComplex) other).arg();
				fixPhi();
			} else
				r*= ((MReal) other).getValue();
			cartesian = false;
		} else {
			if(other.isComplex()) {
				double c = ((MComplex) other).real();
				double d = ((MComplex) other).imag();
				double old_A = a;
				a = a*c-b*d;
				b = c*b+old_A*d;
			} else {
				a *= ((MReal) other).getValue();
				b *= ((MReal) other).getValue();
			}
			polar = false;
		}
		return this;
	}

	/**
	 * Multiplies this number with a real number.
	 * This number's form is preserved, the other is invalidated if {@code d!=1}.
	 * @param d the real number to multiply this one with.
	 * @return {@code this}
	 */
	@Override
	public MComplex multiply(double d) {
		if(polar) {
			r*=d;
			cartesian = cartesian && d==1;
		} else {
			a *= d;
			b *= d;
			polar = polar && d==1;
		}
		return this;
	}
	
	/**
	 * Multiplies this number with a complex number {@code z} with Cartesian coordinates <tt>a</tt> and <tt>b</tt>.
	 * The polar form is invalidated if {@code c!=1 or d!=0}.
	 * @param c the real component of <tt>z</tt>
	 * @param d the imaginary component of <tt>z</tt>
	 * @return {@code this}
	 */
	public MComplex multiply(double c, double d) {
		if(!cartesian)
			updateCartesian();
		double oldA = a;
		a = a*c-b*d;
		b = b*c+oldA*d;
		polar = polar && c==1 && d==0;
		return this;
	}

	/**
	 * Divides this number by a {@code MScalar} (can be complex).
	 * The form of this number is preserved, the other is invalidated.
	 * @param other the {@code MScalar} this one will be divided by.
	 * @return {@code this}
	 */
	@Override
	public MScalar divide(MScalar other) {
		if(polar) {
			if(other.isComplex()) {
				r /= ((MComplex) other).getR();
				phi -= ((MComplex) other).arg();
				fixPhi();
			} else
				r /= ((MReal) other).getValue();
			cartesian = false;
		} else {
			if(other.isComplex()) {
				double c = ((MComplex) other).real();
				double d = ((MComplex) other).imag();
				double r = ((MComplex) other).getR();
				double oldA = a;
				a = (a*c+b*d)/(r*r);
				b = (b*c-oldA*d)/(r*r);
			} else {
				a /= ((MReal) other).getValue();
				b /= ((MReal) other).getValue();
			}
			polar = false;
		}
		return this;
	}

	/**
	 * Divides this number by a real number <tt>d</tt>.
	 * The form of this number is preserved, the other is invalidated if {@code d!=1}.
	 * @param d the real number this one will be divided by.
	 * @return {@code this}
	 */
	@Override
	public MComplex divide(double d) {
		if(polar) {
			if(d>0)
				r /= d;
			else {
				r /= -d;
				phi +=Math.PI;
				fixPhi();
			}
			cartesian = cartesian && d==1;
		} else {
			a /= d;
			b /= d;
			polar = polar && d==1;
		}
		return this;
	}

	/**
	 * Raises this number to the power of a {@code MScalar} (can be complex).
	 * This invalidates the Cartesian form.
	 * @param other the exponent.
	 * @return {@code this}
	 */
	@Override
	public MComplex power(MScalar other) {
		if(other.isComplex()) {
			if(!polar)
				updatePolar();
			MComplex z = (MComplex) new MReal(r).power(other);
			z.multiply(Math.exp(-((MComplex) other).imag()*phi)).multiply(fromPolar(1, phi*((MComplex) other).real()));
			r = z.getR();
			phi = z.arg();
		} else
			return power(((MReal) other).getValue());
		return null;
	}

	/**
	 * Raises this number to the power of a real number <tt>d</tt>.
	 * This invalidates the Cartesian form if {@code d!=1}.
	 * @param d the (real) exponent
	 * @return {@code this}
	 */
	@Override
	public MComplex power(double d) {
		if(!polar)
			updatePolar();
		phi *= d;
		fixPhi();
		r = Math.pow(r, d);
		polar = true;
		cartesian = cartesian && d==1;
		return this;
	}
	
	/**
	 * Returns the absolute value of this number, defined as {@code abs(z)=sqrt(a^2+b^2)} in Cartesian form or {@code abs(z)=r} in polar form.
	 * This recalculates the polar form.
	 * @return {@code abs(z)} where z is this number.
	 */
	@Override
	public double abs() {
		if(!polar)
			updatePolar();
		return r;
	}

	/**
	 * Returns a boolean indicating if this number is complex.
	 * @return true
	 */
	@Override
	public boolean isComplex() {
		return true;
	}
	
	/**
	 * Negates this number.
	 * <ul><li>In the polar form, pi will be added to phi (after which phi will be reduced to the interval [0, 2*pi].</li>
	 * <li>In the Cartesian form, both components will be negated.</li></ul>
	 * @return {@code this}
	 */
	@Override
	public MComplex negate() {
		if(polar) {
			phi += Math.PI;
			fixPhi();
			cartesian = false;
		} else {
			a *= -1;
			b *= -1;
		}
		return this;
	}

	/*
	 * Turns this number into its inverse: 1/z=z'/|z|^2 where z' is the complex conjugate.
	 * @return {@code conjugate().divide(abs()*abs());}
	 */
	@Override
	public MComplex invert() {
		return conjugate().divide(abs()*abs());
	}
	
	/**
	 * Turns this number into its complex conjugate.
	 * <ul><li>In the polar form, phi will be negated.</li>
	 * <li>In the Cartesian form, the imaginary component will be negated.</li></ul>
	 * @return {@code this}
	 */
	public MComplex conjugate() {
		phi *= -1;
		b  *= -1;
		return this;
	}
	
	/**
	 * @return a copy of this number.
	 */
	@Override
	public MComplex copy() {
		if(polar)
			return fromPolar(r, phi);
		else
			return new MComplex(a, b);
	}
	
	/**
	 * @return {@code copy()}
	 */
	@Override
	public MComplex evaluate() {
		return copy();
	}
	
	private void fixPhi() {
		if(r==0)
			phi = 0;
		else
			phi = Tools.reduce(phi, 0, 2*Math.PI);
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof MComplex) {
			if(polar)
				return r==((MComplex) other).getR() && phi==((MComplex) other).arg();
			else
				return a==((MComplex) other).real() && b == ((MComplex) other).imag();
		}
		if((polar && phi==0) || (cartesian && b==0)) {
			if(other instanceof MReal || other instanceof Number)
				return other.equals(polar ? r : a);
		}
		return false;
	}
	
	/**
	 * Constructs a new {@code MComplex} in polar form.
	 * @param r the radial component of the new complex number.
	 * @param phi the angle of the the new complex number.
	 * @return the new complex number of the form {@code z=r*e^(i*phi)}.
	 */
	public static MComplex fromPolar(double r, double phi) {
		MComplex z = new MComplex();
		z.r = r;
		z.phi = phi;
		z.polar = true;
		z.cartesian = false;
		return z;
	}
}