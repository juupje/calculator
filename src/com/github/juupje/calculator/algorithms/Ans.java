package com.github.juupje.calculator.algorithms;

import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class Ans extends Algorithm {

	int index = 0;
	
	public Ans() {
		prepared = true;
	}
	
	public Ans(int i) {
		index = i;
		prepared = true;
	}
	
	@Override
	public MathObject execute() {
		return Variables.ans(index);
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		prepared = false;
		if(args.length==0)
			index = 0;
		else if(args.length==1) {
			if(args[0] instanceof MReal && ((MReal) args[0]).isInteger())
				index = (int) ((MReal) args[0]).getValue();
			else
				throw new IllegalArgumentException("First argument has to be a real integer, got " + args[0]);
		} else
			throw new IllegalArgumentException("ans expects one or no arguments, got " + args.length);
		prepared = true;
	}

	@Override
	public Shape shape(Shape... shapes) {
		return new Shape();
	}

}
