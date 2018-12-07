package algorithms;

import helpers.Shape;
import mathobjects.MathObject;

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

	public abstract MathObject execute(MathObject... args);
	
	public abstract Shape shape(Shape... shapes) ;

	protected void prepare(MathObject[] args) {
		prepared = true;
	}

	public final String argTypesToString(MathObject[] args) {
		String s = "(";
		for (int i = 0; i < args.length; i++)
			s += args[i].getClass().getSimpleName() + (i == args.length-1 ? "" : ", ");
		return s + ")";
	}
}
