package com.github.juupje.calculator.mathobjects;

import java.util.HashMap;

import com.github.juupje.calculator.helpers.Shape;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class MVectorFunction extends MVector {

	String[] vars;
	Shape[] varShapes;
	boolean defined;
	HashMap<String, MathObject> paramMap;
	
	public MVectorFunction(String[] vars, Shape[] varShapes, boolean defined, Tree... trees) {
		super(trees.length);
		this.defined = defined;
		this.vars = vars;
		paramMap = new HashMap<String, MathObject>(vars.length);
		v = new MFunction[trees.length];
		for(int i = 0; i < trees.length; i++) {
			MFunction func = new MFunction(vars, varShapes, trees[i], defined);
			func.setParamMap(paramMap);
			v[i] = defined ? func : func.evaluate();
		}
	}
	
	public MVectorFunction(String[] vars, MVector vector, boolean defined) {
		super(vector.size());
		this.defined = defined;
		this.vars = vars;
		paramMap = new HashMap<String, MathObject>(vars.length);
		v = new MFunction[vector.size()];
		for(int i = 0; i < vector.size(); i++) {
			MFunction func = null;
			if(vector.get(i) instanceof MExpression) {
				func = new MFunction(vars, varShapes, ((MExpression) vector.get(i)).getTree(), defined);
			} else { //vector[i] is a constant
				func = new MFunction(vars, varShapes, new Tree(new Node<MathObject>(vector.get(i))), defined);
			}
			func.setParamMap(paramMap);
			v[i] = defined ? func : func.evaluate();
		}
	}

	public MVector evaluateAt(MathObject... paramVals) throws TreeException {
		if (paramVals.length != vars.length)
			throw new IllegalArgumentException(
					"Function expected " + vars.length + " arguments, got " + paramVals.length);
		paramMap.clear();
		for(int i = 0; i < paramVals.length; i++)
			paramMap.put(vars[i], paramVals[i]);
		return evaluateAt();
	}

	public MathObject evaluateAt(String s)
			throws UnexpectedCharacterException, InvalidFunctionException, TreeException {
		MathObject[] paramVals = Parser.getArgumentsAsMathObject(s);
		if (paramVals.length != vars.length)
			throw new IllegalArgumentException(
					"Function expected " + vars.length + " arguments, got " + paramVals.length);
		return evaluateAt(paramVals);
	}
	
	public MVector evaluateAt(HashMap<String, MathObject> map) throws TreeException {
		setParamMap(map);
		return evaluateAt();
	}
	
	public void setParamMap(HashMap<String, MathObject> map) {
		paramMap = map;
		for(int i = 0; i < size; i++)
			((MFunction) v[i]).setParamMap(map);
	}
	
	public MVector evaluateAt() throws TreeException {
		MathObject[] values = new MathObject[size];
		for(int i = 0; i < size; i++)
			values[i] = ((MFunction) v[i]).evaluateAt();
		return new MVector(values);
	}
	
	public String[] getParameters() {
		return vars;
	}
	
	public boolean isDefined() {
		return defined;
	}
	
	/**
	 * returns the i-th element of the vector. (Note that the vector starts at index 0).
	 * @param i the element's index.
	 * @return the i-the element.
	 */
	@Override
	public MFunction get(int i) {
		if(i < 0 || i >=size)
			throw new IndexOutOfBoundsException("You're trying to access component " + i + " of a vector with " + size + " elements.");
		return (MFunction) v[i];
	}

	@Override
	public String toString() {
		return super.toString();
	}	
}