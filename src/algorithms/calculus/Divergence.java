package algorithms.calculus;

import algorithms.Algorithm;
import helpers.Shape;
import main.Parser;
import main.Variable;
import main.Variables;
import mathobjects.MFunction;
import mathobjects.MReal;
import mathobjects.MVector;
import mathobjects.MVectorFunction;
import mathobjects.MathObject;
import tree.Node;
import tree.Tree;

public class Divergence extends Algorithm {
	
	MFunction f;
	MathObject value;
	
	public Divergence() {}
	
	public Divergence(MFunction f) {
		this.f = f;
	}
	
	public Divergence(MFunction f, MathObject value) {
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
			return new MFunction(params, new Tree(new Node<MVectorFunction>(new MVectorFunction(params, v, f.isDefined()))), f.isDefined());
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
			throw new IllegalArgumentException("Expected one or two arguments, got " + args.length + ". see 'help div' for more help.");
		MathObject obj  = Variables.get(args[0]);
		if(obj == null || !(obj instanceof MFunction))
			throw new IllegalArgumentException("First argument needs to be a function, got " + (obj==null ? args[0] : obj.getClass().getName()));
		f = (MFunction) obj;
		if(f.shape().dim()==0)
			throw new IllegalArgumentException("Divergence is only defined for vector functions, got function of scalar shape");
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
