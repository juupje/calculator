package com.github.juupje.calculator.algorithms.algebra;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UndefinedException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MVectorFunction;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.tree.DFSTask;
import com.github.juupje.calculator.tree.Tree;

public class Distribute extends Algorithm {
	MExpression expr;
	MFunction f;
	MVectorFunction v;
	
	public Distribute() {}
	
	public Distribute(MExpression expr) {
		this.expr = expr;
		prepared = true;
	}
	
	public Tree distribute(Tree tr) {
		DFSTask distributor = Simplifier.distribute;
		Tree tree = tr.copy();
		tree.DFS(distributor);
		return tree;
	}
	
	@Override
	public MathObject execute() {
		if(!prepared) return null;
		if(f != null)
			return new MFunction(f.getParameters(), f.getParamShapes(), distribute(f.getTree()), f.isDefined());
		if(expr != null)
			return new MExpression(distribute(expr.getTree()));
		if(v != null) {
			Tree[] trees = new Tree[v.size()];
			for(int i = 0 ; i < trees.length; i++)
				trees[i] = distribute(v.get(i).getTree());
			return new MVectorFunction(v.getParameters(), v.get(0).getParamShapes(), v.isDefined(), trees);
		}
		return null;	
	}

	@Override
	public MathObject execute(String... args) {
		try {
			prepare(args);
			return execute();
		} catch (ShapeException | UnexpectedCharacterException | InvalidFunctionException | TreeException e) {
			Calculator.errorHandler.handle(e);
			return MReal.NaN();
		}
	}
	
	@Override
	protected MathObject execute(MathObject... args) {
		throw new RuntimeException("Unexpected method call.");
	}
	
	@Override
	public void prepare(String[] args) {
		expr = null;
		v = null;
		f = null;
		if(args.length != 1)
			throw new IllegalArgumentException("Simplifier requires 1 argument, got " +args.length);
		if(Variables.exists(args[0])) {
			MathObject m = Variables.get(args[0]);
			if(m instanceof MExpression)
				expr = (MExpression) m;
			else if(m instanceof MFunction) {
				f = (MFunction) m;
			} else if(m instanceof MVectorFunction)
				v = (MVectorFunction) m;
			else
				throw new IllegalArgumentException("First argument needs to be a function or an expression, got " + Tools.type(args[0]));
		} else
			throw new UndefinedException("Unknown variable " + args[0]);
		prepared = true;
	}

	@Override
	public Shape shape(Shape... shapes) {
		if(shapes.length != 1)
			throw new IllegalArgumentException("Simplifier requires 1 argument, got " + shapes.length);
		return shapes[0];
	}
}