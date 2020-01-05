package com.github.juupje.calculator.algorithms.calculus;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.Shape;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MVectorFunction;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class Gradient extends Algorithm {
	
	MFunction f;
	MathObject value;
	
	public Gradient() {}
	
	public Gradient(MFunction f) {
		this.f = f;
	}
	
	public Gradient(MFunction f, MathObject value) {
		this(f);
		this.value = value;
	}

	@Override
	public MathObject execute() {
		if(!prepared)
			return MReal.NaN();
		String[] params = f.getParameters();
		MVector v = new MVector(params.length);
		for(int i = 0; i < v.size(); i++) {
			v.set(i, (value == null ? new Deriver(f, new Variable(params[i])) : new Deriver(f, new Variable(params[i]), value)).execute());
		}
		if(value != null)
			return new MFunction(params, f.getParamShapes(), new Tree(new Node<MVectorFunction>(new MVectorFunction(params, v, f.isDefined()))), f.isDefined());
		return v;
	}

	@Override
	public MathObject execute(String... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	public MathObject execute(MathObject... args) {
		throw new RuntimeException("This method should never be called, please send this stacktrace to the developer.");
	}
	
	@Override
	protected void prepare(String[] args) {
		if(args.length<1 || args.length>2 )
			throw new IllegalArgumentException("Expected one or two arguments, got " + args.length + ". see 'help grad' for more help.");
		MathObject obj  = Variables.get(args[0]);
		if(obj == null || !(obj instanceof MFunction))
			throw new IllegalArgumentException("First argument needs to be a function, got " + (obj==null ? args[0] : obj.getClass().getSimpleName()));
		f = (MFunction) obj;
		if(!f.shape().isScalar())
			throw new IllegalArgumentException("The Gradient is only defined for scalar functions, got function of shape " + f.shape());
		for(Shape s : f.getParamShapes())
			if(!s.isScalar())
				throw new IllegalArgumentException("Cannot derive functions with non-scalar arguments.");
		if(args.length==2) {
			value = new Parser(args[2]).evaluate();
		}
		prepared = true;
	}

	@Override
	public Shape shape(Shape... shapes) {
		// TODO Auto-generated method stub
		return null;
	}

}
