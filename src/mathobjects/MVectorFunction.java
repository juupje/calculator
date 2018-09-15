package mathobjects;

import java.util.HashMap;

import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Parser;
import tree.Tree;

public class MVectorFunction extends MVector {

	String[] vars;
	boolean defined;
	HashMap<String, MathObject> paramMap;
	
	public MVectorFunction(String[] vars, boolean defined, Tree... trees) {
		this.defined = defined;
		this.vars = vars;
		this.size = trees.length;
		paramMap = new HashMap<String, MathObject>(MFunction.INIT_PARAM_CAP);
		v = new MFunction[trees.length];
		for(int i = 0; i < trees.length; i++) {
			MFunction func = new MFunction(vars, trees[i], defined);
			func.setParamMap(paramMap);
			v[i] = defined ? func : func.evaluate();
		}
	}
	
	public MVectorFunction(String[] vars, MVector vector, boolean defined) {
		this.defined = defined;
		this.vars = vars;
		this.size = vector.size();
		paramMap = new HashMap<String, MathObject>(MFunction.INIT_PARAM_CAP);
		v = new MFunction[vector.size()];
		for(int i = 0; i < vector.size(); i++) {
			if(vector.get(i) instanceof MExpression) {
				MFunction func = new MFunction(vars, ((MExpression) vector.get(i)).getTree(), defined);
				func.setParamMap(paramMap);
				v[i] = defined ? func : func.evaluate();
			}
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

	@Override
	public String toString() {
		return super.toString();
	}

	
}
 