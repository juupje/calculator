package com.github.juupje.calculator.algorithms;

import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

/**
 * Algorithms can be executed in two ways:
 * <ol>
 * <li>using {@link #execute(MathObject...)}, the arguments are passed directly
 * to the method. Before the algorithm starts, the arguments are checked by the
 * {@link #prepare(MathObject[])} method, which will throw an error if one or
 * more arguments do not meet the requirements.</li>
 * <li>using {@link #execute()}, the arguments have to be passed via the
 * constructor when initializing the algorithm. This is intended for internal
 * use only, as it allows the program to skip the prepare steps, as the
 * arguments passed via the constructor always fit the requirements.</li>
 * </ol>
 * 
 * @author Joep Geuskens
 *
 */
public abstract class Algorithm {
	
	protected boolean prepared = false;
	
	public abstract MathObject execute(); // Intended for internal use, arguments have to be passed via constructor.

	protected abstract MathObject execute(MathObject... args);
	
	public MathObject execute(String... args) {
		try {
			return execute(Parser.toMathObjects(args));
		} catch (ShapeException | UnexpectedCharacterException | InvalidFunctionException | TreeException e) {
			Calculator.errorHandler.handle(e);
			return MReal.NaN();
		}
	}
	
	public abstract Shape shape(Shape... shapes) ;

	protected void prepare(MathObject[] args) {
		prepared = true;
	}
	
	protected void prepare(String[] args) {
		prepared = true;
	}

	public final String argTypesToString(MathObject[] args) {
		String s = "(";
		for (int i = 0; i < args.length; i++)
			s += Tools.type(args[i]) + (i == args.length-1 ? "" : ", ");
		return s + ")";
	}
	
	public final String argTypeToString(MathObject arg) {
		return Tools.type(arg);
	}
}
