package com.github.juupje.calculator.algorithms.algebra;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.settings.Settings;

public class ABCFormula extends Algorithm {
	
	private double a, b, c, d;
	private MScalar A, B, C, D;
	private boolean cubic = false;
	
	public ABCFormula() {};

	public ABCFormula(MReal a, MReal b, MReal c) {
		this.a = a.getValue();
		this.b = b.getValue();
		this.c = c.getValue();
		cubic = false;
		prepared = true;
	}
	
	public ABCFormula(MScalar a, MScalar b, MScalar c) {
		A = a;
		B = b;
		C = c;
		cubic = false;
		prepared = true;
	}
	
	public ABCFormula(MReal a, MReal b, MReal c, MReal d) {
		this.a = a.getValue();
		this.b = b.getValue();
		this.c = c.getValue();
		this.d = d.getValue();
		cubic = true;
		prepared = true;
	}
	
	public ABCFormula(MScalar a, MScalar b, MScalar c, MScalar d) {
		A = a;
		B = b;
		C = c;
		D = d;
		cubic = true;
		prepared = true;
	}
	
	public ABCFormula(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
		prepared = true;
	}
	
	public ABCFormula(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		cubic = true;
		prepared = true;
	}
	
	@Override
	public MVector execute() {
		if(!prepared) return null;
		if(!cubic) {
			if(A == null)
				return abc();
			else
				return ABC();
		} else {
			if(A == null)
				return abcd();
			else
				return ABCD();
		}
	}
	
	public MVector abc() {
		double D = b*b-4*a*c;
		MathObject[] result = new MathObject[2];
		if(D>0) {
			result[0] = new MReal((-b+Math.sqrt(D))/(2*a));
			result[1] = new MReal((-b-Math.sqrt(D))/(2*a));
		} else if(D==0) {
			result[0]=new MReal(-b/(2*a));
			result[1]= MReal.NaN();
		} else {
			if(Settings.getBool(Settings.COMPLEX_ENABLED)) {
				result[0] = new MComplex(-b, Math.sqrt(-D)).divide(2*a);
				result[1] = new MComplex(-b, -Math.sqrt(-D)).divide(2*a);
			} else {
				result[0] = MReal.NaN();
				result[1] = MReal.NaN();
			}
		
		}
		if(Settings.getBool(Settings.ABC_SHOW_TEXT)) {
			Calculator.ioHandler.out("ABC-Formula: a=" + a + ", b=" + b + ", c=" + c + "\n\tD=b^2-4ac="+D+"\n\tx_+="+result[0] + "\n\tx_-=" + result[1]);
		}
		return new MVector(result);
	}
	
	public MVector ABC() {
		D = B.copy().multiply(B).subtract(C.multiply(A).multiply(4));
		B.negate();
		D=D.sqrt();
		A.multiply(2);
		return new MVector(B.copy().add(D).divide(A), B.add(D).divide(A));
	}
	
	public MVector abcd() {
		double delta0 = b*b-3*a*c;
		double delta1 = 2*b*b*b-9*a*b*c+27*a*a*d;
		double D = delta1*delta1-4*delta0*delta0*delta0;
		MComplex ksi = new MComplex(-0.5, Math.sqrt(3)*0.5);
		MScalar x2, x3;
		MReal x1;
		if(D<0) {
			double absD = Math.abs(D);
			double r2 = delta1*delta1/4+absD/4;
			//there is always one real root
			x1 = new MReal((b+Math.pow(r2, 1./6)*2*Math.cos(Math.atan2(Math.sqrt(absD), delta1)/3))/-3/a);
			MComplex C = new MComplex(delta1/2, Math.sqrt(absD)/2).power(1/3.);
			MComplex C1 = C.multiply(ksi); //C1==C
			MComplex C2 = ksi.multiply(C1); //C2==ksi
			x2 = C1.add(new MReal(delta0).divide(C1)).add(b).divide(-3*a);
			x3 = C2.add(new MReal(delta0).divide(C2)).add(b).divide(-3*a);
		} else {
			double C = Math.pow((delta1+Math.signum(delta1)*Math.sqrt(D))/2, 1./6);
			x1 = new MReal((b+C+delta0/C)/-3/a);
			MComplex C1 = new MComplex(C,0).multiply(ksi);
			x2 = C1.copy().add(new MReal(delta0).divide(C1)).add(b).divide(-3*a);
			x3 = C1.multiply(ksi).add(new MReal(delta0).divide(C1)).add(b).divide(-3*a);
		}
		if(x2.imag()==0)//because sometimes it is apparently?
			x2 = new MReal(x2.real());
		if(x3.imag()==0)
			x3 = new MReal(x3.real());
		return new MVector(x1, x2, x3);
	}
	
	public MVector ABCD() {
		MScalar E = B.copy();
		E.multiply(E); //E->E^2
		C.multiply(3).multiply(A); //C->3*C*A
		MScalar delta0 = E.copy().subtract(C);
		MScalar delta1 = E.multiply(B).multiply(2).subtract(B.copy().multiply(C).multiply(3)).add(D.multiply(27).multiply(A).multiply(A)); //E->delta1, D->27*D*A^2
		D = delta1.copy().multiply(delta1).subtract(delta0.copy().power(3).multiply(4)); //D redefined
		
		MComplex ksi = new MComplex(-0.5, 0.5*Math.sqrt(3));
		D = D.sqrt(); //D->sqrt(D)
		C = delta1.add(D).divide(2).power(1./3); //C redefined, delta1->((delta1+D)/2)^1/3
		A.multiply(-3).invert(); //A -> -1/3A
		delta0 = delta0.divide(C);//delta0->delta0/C
		MScalar x1 = C.copy().add(B).add(delta0).multiply(A); 
		C=C.multiply(ksi);
		delta0 = delta0.divide(ksi);//delta0->delta0/ksi
		MScalar x2 = C.copy().add(B).add(delta0).multiply(A); 
		C=C.multiply(ksi);
		MScalar x3 = C.add(B).add(delta0.divide(ksi)).multiply(A); //delta0 -> delta0/ksi
		return new MVector(x1, x2, x3);
	}
	

	@Override
	public MVector execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		prepared = false;
		cubic = false;
		A = B = C = D = null;
		a = b = c = d = 0;
		if(args.length == 3) {
			cubic = false;
			if(args[0] instanceof MReal && args[1] instanceof MReal && args[2] instanceof MReal) {
				a = ((MReal) args[0]).getValue();
				b = ((MReal) args[1]).getValue();
				c = ((MReal) args[2]).getValue();
				prepared = true;
			} else if(args[0] instanceof MScalar && args[1] instanceof MScalar && args[2] instanceof MScalar) {
				A = (MScalar) args[0];
				B = (MScalar) args[1];
				C = (MScalar) args[2];
				prepared = true;
			} else
				throw new IllegalArgumentException("ABC formula expects 3 scalars, got " + argTypesToString(args));
		} else if(args.length==4) {
			cubic = true;
			if(args[0] instanceof MReal && args[1] instanceof MReal && args[2] instanceof MReal && args[3] instanceof MReal) {
				a = ((MReal) args[0]).getValue();
				b = ((MReal) args[1]).getValue();
				c = ((MReal) args[2]).getValue();
				d = ((MReal) args[3]).getValue();
				prepared = true;
			} else if(args[0] instanceof MScalar && args[1] instanceof MScalar && args[2] instanceof MScalar && args[3] instanceof MScalar) {
				A = (MScalar) args[0];
				B = (MScalar) args[1];
				C = (MScalar) args[2];
				D = (MScalar) args[3];
				prepared = true;
			} else
				throw new IllegalArgumentException("ABC formula expects 3 or 4 scalars, got " + argTypesToString(args));
		} else
			throw new IllegalArgumentException("ABC-Formula requires 3 or 4 arguments, got " + args.length);		
	}
	
	@Override
	public Shape shape(Shape... shapes) {
		return new Shape(2);
	}
}