package mathobjects;

import helpers.Tools;

public class MComplex extends MScalar {

	double a, b, r, phi;
	boolean polar, cartesian;
	
	public MComplex(double a, double b) {
		this.a = a;
		this.b = b;
		polar = false;
		cartesian = true;
	}
	
	public MComplex(MReal real) {
		a = r = real.getValue();
		b = phi = 0;
		polar = cartesian = true;
	}
	
	public MComplex() {
		a = b = r = phi = 0;
		cartesian = polar = true;
	}
	
	public double getA() {
		if(!cartesian)
			updateCartesian();
		return a;
	}

	public void setA(double a) {
		if(!cartesian)
			b = r*Math.sin(phi);
		this.a = a;
		cartesian = true;
		polar = false;
	}

	public double getB() {
		if(!cartesian)
			updateCartesian();
		return b;
	}

	public void setB(double b) {
		if(!cartesian)
			a = r*Math.cos(phi);
		this.b = b;
		cartesian = true;
		polar = false;
	}
	
	public void setCartesian(double a, double b) {
		this.a = a;
		this.b = b;
		cartesian = true;
		polar = false;
	}
	
	public double getR() {
		if(!polar)
			updatePolar();
		return r;
	}

	public void setR(double r) {
		this.r = r;
		if(!polar)
			phi = Math.atan2(b,a);
		polar = true;
		cartesian = false;
	}

	public double arg() {
		if(!polar)
			updatePolar();
		return phi;
	}

	public void setArg(double phi) {
		this.phi = phi;
		if(!polar)
			r = Math.sqrt(a*a+b*b);
		polar = true;
		cartesian = false;
	}
	
	public void setPolar(double r, double phi) {
		this.r = r;
		this.phi = phi;
		cartesian = false;
		polar = true;
	}

	private void updateCartesian() {
		a = r*Math.cos(phi);
		b = r*Math.sin(phi);
		cartesian = true;
	}
	
	private void updatePolar() {
		r = Math.sqrt(a*a+b*b);
		phi = Math.atan2(b, a);
		polar = true;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public MScalar add(MScalar other) {
		if(!cartesian)
			updateCartesian();
		if(other.isComplex()) {
			a += ((MComplex) other).getA();
			b += ((MComplex) other).getB();
		} else
			a += ((MReal) other).getValue();
		polar = other.equals(0);
		return this;
	}

	@Override
	public MComplex add(double d) {
		if(!cartesian)
			updateCartesian();
		a += d;
		polar = d==0;
		return this;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public MComplex subtract(MScalar other) {
		if(!cartesian)
			updateCartesian();
		if(other.isComplex()) {
			a -= ((MComplex) other).getA();
			b -= ((MComplex) other).getB();
		} else
			a -= ((MReal) other).getValue();
		polar = other.equals(0);
		return this;
	}

	@Override
	public MScalar subtract(double d) {
		if(!cartesian)
			updateCartesian();
		a -= d;
		polar = d==0;
		return this;
	}

	@Override
	public MComplex multiply(MScalar other) {
		if(polar) {
			if(other.isComplex()) {
				r *= ((MComplex) other).getR();
				phi = Tools.reduce(phi + ((MComplex) other).arg(), -Math.PI, Math.PI);
			} else
				r*= ((MReal) other).getValue();
			cartesian = false;
		} else {
			if(other.isComplex()) {
				double c = ((MComplex) other).getA();
				double d = ((MComplex) other).getB();
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

	@Override
	public MComplex multiply(double d) {
		if(polar) {
			r*=d;
			cartesian = false;
		} else {
			a *= d;
			b *= d;
			polar = false;
		}
		return this;
	}
	
	public MComplex multiply(double c, double d) {
		if(!cartesian)
			updateCartesian();
		double oldA = a;
		a = a*c-b*d;
		b = b*c+oldA*d;
		polar = false;
		return this;
	}

	@Override
	public MScalar divide(MScalar other) {
		if(polar) {
			if(other.isComplex()) {
				r /= ((MComplex) other).getR();
				phi = Tools.reduce(phi - ((MComplex) other).arg(), -Math.PI, Math.PI);
			} else
				r /= ((MReal) other).getValue();
			cartesian = false;
		} else {
			if(other.isComplex()) {
				double c = ((MComplex) other).getA();
				double d = ((MComplex) other).getB();
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

	@Override
	public MScalar divide(double d) {
		if(polar) {
			r /= d;
			cartesian = false;
		} else {
			a /= d;
			b /= d;
			polar = false;
		}
		return this;
	}

	@Override
	public MComplex power(MScalar other) {
		if(other.isComplex()) {
			if(!polar)
				updatePolar();
			MComplex z = (MComplex) new MReal(r).power(other);
			z.multiply(Math.exp(-((MComplex) other).getB()*phi)).multiply(fromPolar(1, phi*((MComplex) other).getA()));
			r = z.getR();
			phi = z.arg();
		} else
			return power(((MReal) other).getValue());
		return null;
	}

	@Override
	public MComplex power(double d) {
		if(!polar)
			updatePolar();
		phi = Tools.reduce(phi * d, -Math.PI, Math.PI);
		r = Math.pow(r, d);
		polar = true;
		cartesian = false;
		return this;
	}
	
	@Override
	public double abs() {
		if(!polar)
			updatePolar();
		return r;
	}

	@Override
	public boolean isComplex() {
		return true;
	}
	
	@Override
	public MComplex negate() {
		if(polar) {
			phi += Math.PI * (phi >= Math.PI ? -1 : 1);
			cartesian = false;
		} else {
			a *= -1;
			b *= -1;
		}
		return this;
	}
	
	@Override
	public MComplex invert() {
		if(polar) {
			phi *= -1;
			cartesian = false;
		} else
			b *= -1;
		return this;
	}
	
	@Override
	public MComplex copy() {
		if(polar)
			return fromPolar(r, phi);
		else
			return new MComplex(a, b);
	}
	
	@Override
	public MComplex evaluate() {
		return copy();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof MComplex) {
			if(polar)
				return r==((MComplex) other).getR() && phi==((MComplex) other).arg();
			else
				return a==((MComplex) other).getA() && b == ((MComplex) other).getB();
		}
		if((polar && phi==0) || (cartesian && b==0)) {
			if(other instanceof MReal || other instanceof Number)
				return other.equals(polar ? r : a);
		}
		return false;
	}
	
	public static MComplex fromPolar(double r, double phi) {
		MComplex z = new MComplex();
		z.r = r;
		z.phi = phi;
		z.polar = true;
		z.cartesian = false;
		return z;
	}
}