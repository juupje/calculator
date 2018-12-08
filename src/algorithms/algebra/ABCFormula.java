package algorithms.algebra;

import algorithms.Algorithm;
import helpers.Setting;
import helpers.Shape;
import main.Calculator;
import mathobjects.MComplex;
import mathobjects.MReal;
import mathobjects.MVector;
import mathobjects.MathObject;

public class ABCFormula extends Algorithm {
	
	double a, b, c;
	
	public ABCFormula() {};

	public ABCFormula(MReal a, MReal b, MReal c) {
		this.a = a.getValue();
		this.b = b.getValue();
		this.c = c.getValue();
		prepared = true;
	}
	
	public ABCFormula(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
		prepared = true;
	}
	
	@Override
	public MathObject execute() {
		if(!prepared) return null;
		double D = b*b-4*a*c;
		MathObject[] result = new MathObject[2];
		if(D>0) {
			result[0] = new MReal((b+Math.sqrt(D))/(2*a));
			result[1] = new MReal((-b-Math.sqrt(D))/(2*a));
		} else if(D==0) {
			result[0]=new MReal(-b/(2*a));
			result[1]=result[0].copy();
		} else {
			result[0] = new MComplex(-b/(2*a), Math.sqrt(-D));
			result[1] = new MComplex(-b/(2*a), -Math.sqrt(-D));
		}
		if(Setting.getBool(Setting.ABC_SHOW_TEXT)) {
			Calculator.ioHandler.out("ABC-Formula: a=" + a + ", b=" + b + ", c=" + c + "\n\tD=b^2-4ac="+D+"\n\tx_+="+result[0] + "\n\tx_-=" + result[1]);
		}
		return new MVector(result);
	}

	@Override
	public MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		prepared = false;
		if(args.length!=3)
			throw new IllegalArgumentException("ABC-Formula requires 3 arguments, got " + args.length);
		if(args[0] instanceof MReal && args[1] instanceof MReal && args[2] instanceof MReal) {
			a = ((MReal) args[0]).getValue();
			b = ((MReal) args[1]).getValue();
			c = ((MReal) args[2]).getValue();
			prepared = true;
		} else
			throw new IllegalArgumentException("ABC formula expects 3 real numbers, got " + argTypesToString(args));
	}
	
	@Override
	public Shape shape(Shape... shapes) {
		return new Shape(2);
	}
}