package com.github.juupje.calculator.mathobjects;

public class MFraction extends MReal {
	int a, b;
	
	public MFraction(int a, int b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public double real() {
		return getValue();
	}	

	public MFraction add(MFraction other) {
		b*= other.b;
		a = a*other.b + other.a*b;
		return this;
	}
	
	@Override
	public MScalar add(MScalar other) {
		if(other instanceof MFraction) {
			return add((MFraction) other);
		} else if(other.isInteger())
			return add((int) other.real());
		return toMReal().add(other);
	}

	@Override
	public MScalar add(double d) {
		if(isInteger(d))
			return add((int) d);
		return toMReal().add(d);
	}
	
	public MFraction add(int i) {
		a += b*i;
		return this;
	}

	@Override
	public MScalar subtract(MScalar other) {
		if(other instanceof MFraction) {
			b*= ((MFraction) other).b;
			a = a*((MFraction) other).b - ((MFraction) other).a*b;
			return this;
		} else if(other.isInteger())
			return subtract((int) other.real());
		return super.subtract(other);
	}

	@Override
	public MScalar subtract(double d) {
		if(isInteger(d))
			return subtract((int) d);
		return toMReal().subtract(d);
	}
	
	public MFraction subtract(int i) {
		a-=b*i;
		return this;
	}

	@Override
	public MScalar multiply(MScalar other) {
		if(other instanceof MFraction) {
			a *= ((MFraction) other).a;
			b *= ((MFraction) other).b;
			return this;
		}
		if(other.isInteger())
			return multiply((int) other.real());
		return toMReal().multiply(other);
	}

	@Override
	public MScalar multiply(double d) {
		if(isInteger(d))
			return multiply((int) d);
		return toMReal().multiply(d);
	}
	
	public MFraction multiply(int i) {
		a*=i;
		return this;
	}

	@Override
	public MScalar divide(MScalar other) {
		if(other instanceof MFraction) {
			a*=((MFraction) other).b;
			b*=((MFraction) other).a;
			return this;
		} else if(other.isInteger())
			return divide((int) other.real());
		return toMReal().divide(other);
	}

	@Override
	public MScalar divide(double d) {
		if(Math.floor(d)==d)
			return divide((int) d);
		return toMReal().divide(d);
	}
	
	public MFraction divide(int i) {
		b*=i;
		return this;
	}

	@Override
	public MScalar power(MScalar other) {
		if(other.isInteger())
			return power((int) other.real());
		return toMReal().power(other);
	}

	@Override
	public MReal power(double d) {
		if(isInteger(d))
			return power((int) d);
		return toMReal().power(d);
		
	}

	public MFraction power(int i) {
		if(i>0) {
			a = (int) Math.pow(a, i);
			b = (int) Math.pow(b, i);
		} else if(i==0)
			a=b=1;
		else {
			b = (int) Math.pow(a, -i);
			a = (int) Math.pow(b, -i);
		}
		return this;
	}
	
	@Override
	public MScalar sqrt() {
		double new_a = Math.sqrt(a);
		double new_b = Math.sqrt(b);
		if(isInteger(new_a) && isInteger(new_b)) {
			a = (int)new_a;
			b = (int)new_b;
			return this;
		} else
			return new MReal(new_a/new_b);
	}

	@Override
	public MReal mod(MReal other) {
		return toMReal().mod(other);
	}

	@Override
	public MScalar mod(int d) {
		return toMReal().mod(d);
	}

	@Override
	public boolean isInteger() {
		return Math.abs(b) == 1 || (int) Math.abs(a)%(int) Math.abs(b)==0;
	}
	
	@Override
	public boolean isPosInteger() {
		return isInteger() && a*b>0;
	}
	
	@Override
	public boolean isFraction() {
		return true;
	}

	@Override
	public double getValue() {
		return a/(double) b;
	}

	public int getNominator() {
		return a;
	}
	
	public int getDenominator() {
		return b;
	}
	
	public void setNominator(int a) {
		this.a = a;
	}
	
	public void setDenominator(int b) {
		this.b = b;
	}
	
	@Override
	public MFraction negate() {
		a *= -1;
		return this;
	}

	@Override
	public MFraction invert() {
		int c = a;
		a = b;
		b = c;
		return this;
	}

	@Override
	public MReal conjugate() {
		return this;
	}

	@Override
	public MFraction copy() {
		return new MFraction(a,b);
	}

	@Override
	public MFraction evaluate() {
		return copy();
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof MFraction)
			return ((MFraction) other).a == a && ((MFraction) other).b == b;
		return super.equals(other);
	}

	@Override
	public boolean isNaN() {
		return b==0;
	}
	
	public MReal toMReal() {
		return new MReal(a/(double) b);
	}

	@Override
	public void setValue(double d) {
		if(isInteger(d)) {
			a = (int) d;
			b = 1;
		} else
			throw new IllegalArgumentException("Can't set fraction to double value.");
	}

	@Override
	public String toString() {
		return a + "//" + b;
	}
	
	public static MReal create(MReal a, MReal b) {
		if(a.isInteger() && b.isInteger())
			return new MFraction((int) a.getValue(), (int) b.getValue());
		return new MReal(a.getValue()/b.getValue());
	}
}