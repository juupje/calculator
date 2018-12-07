package algorithms.calculus;

import algorithms.Algorithm;
import helpers.Setting;
import helpers.Shape;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import mathobjects.MFunction;
import mathobjects.MReal;
import mathobjects.MReal;
import mathobjects.MVector;
import mathobjects.MathObject;

public class Integrator extends Algorithm{
	
	MFunction f;
	MReal[] a;
	MReal[] b;
	int steps;
	int multiplier;
	
	public Integrator() {};
	
	public Integrator(MFunction f, MReal[] a, MReal b[], int steps) {
		f = (MFunction) f.evaluate();
		this.a = a;
		this.b = b;
		this.steps = steps;
		this.multiplier = 1;
		prepared = true;
	}
	
	public Integrator(MFunction f, MReal[] a, MReal[] b) {
		this(f, a, b, Setting.getInt(Setting.DEF_INT_STEPS));
	}
	
	public Integrator(MFunction f, MReal a, MReal b, int steps) {
		this(f, new MReal[] {a}, new MReal[] {b}, steps);
	}
	
	public Integrator(MFunction f, MReal a, MReal b) {
		this(f, a, b, Setting.getInt(Setting.DEF_INT_STEPS));
	}
	
	public MReal execute() {
		if(!prepared) return null;
		multiplier = 1;
		for(int i = 0; i < a.length; i++) {
			if(a[i].getValue() > b[i].getValue()) {
				MReal c = a[i];
				a[i] = b[i];
				b[i] = c;
				multiplier *= -1;
			} else if(a[i].equals(b[i]))
				return new MReal(0);
		}
		MReal[] x = new MReal[a.length];
		for(int i = 0; i < a.length; i++)
			x[i] = (MReal) a[i].copy();
		return new MReal(integrate(f, x, a, b, steps, 0)*multiplier);
	}
	
	@Override
	public MReal execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	private double integrate(MFunction f, MReal[] x, MReal[] a, MReal[] b, int steps, int index) {
		//System.out.println("Integrating " + index + " from " + a[index] + " to " + b[index]);
		double delta = (b[index].getValue() - a[index].getValue()) / (double) steps;
		double sum = 0;
		try {
			for (x[index].setValue(a[index].getValue()); x[index].getValue() < b[index].getValue(); x[index]
					.add(delta)) {
				if (index == x.length - 1)
					sum += ((MReal) f.evaluateAt(x)).getValue();
				else {
					sum += integrate(f, x, a, b, steps, index + 1);
					//reset x to the start values in all higher dimensions
					for(int i = index+1; i < a.length; i++)
						x[i] = (MReal) a[i].copy();
				}
			}
		} catch (TreeException e) {
			e.printStackTrace();
		}
		//System.out.println("Sum: " + sum);
		return sum * delta;
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		super.prepare(args);
		//Evaluate the second and third parameter (lower and upper bound of the integration).
		//This also copies the values (preventing them from being chanced during the algorithm) and replaces Variables with their values.
		args[1] = args[1].evaluate();
		args[2] = args[2].evaluate();
		//Check if all arguments are of the correct type.
		if((args.length == 4 || args.length == 3) && args[0] instanceof MFunction && (
				(args[1] instanceof MVector && args[2] instanceof MVector && ((MVector) args[1]).size() == ((MVector) args[2]).size()) || 
				(args[1] instanceof MReal && args[2] instanceof MReal))) {
			//Set the function to the first parameter
			f = (MFunction) args[0].evaluate();
			
			//Check argument 2 and 3
			if(args[1] instanceof MVector && ((MVector) args[1]).isOfType(MReal.class) && ((MVector) args[2]).isOfType(MReal.class)) {
				//the upper and lower bound are vectors, so the integration has more than one dimension.
				MVector v = (MVector) args[1];
				MVector w = (MVector) args[2];
				a = new MReal[v.size()];
				b = new MReal[v.size()];
				for(int i = 0; i < v.size(); i++) {
					a[i] = (MReal) v.get(i);
					b[i] = (MReal) w.get(i);
				}
			} else if(args[1] instanceof MReal) {
				//to bounds are scalars, so its a standard 1D integral.
				a = new MReal[1];
				b = new MReal[1];
				a[0] = (MReal) args[1];
				b[0] = (MReal) args[2];
			} else
				throw new IllegalArgumentException("Argument 2 and 3 have to be MVectors or MReals");
			
			//Check argument 4
			if(args.length == 4) {
				args[3] = args[3].evaluate();
				if(args[3] instanceof MReal && ((MReal) args[3]).isInteger())
					steps = (int) ((MReal) args[3]).getValue();
				else
					throw new IllegalArgumentException("Argument 4 has to be an integer valued scalar, got " + args[3].toString());
			} else
				//If there is no 4th argument, set steps to the default (10,000).
				steps = Setting.getInt(Setting.DEF_INT_STEPS);
		} else
			throw new IllegalArgumentException("Arguments " + argTypesToString(args) + " not applicable for Integration algorithm see help for the correct use.");			
	}
	
	@Override
	public Shape shape(Shape... shapes)  {
		if(shapes[0].dim()==0 && shapes[1].equals(shapes[2]) && shapes[1].dim()<=1 && ((shapes.length==4 && shapes[3].dim()==0) || shapes.length==3))
			return new Shape();
		String msg = "";
		for(int i = 0; i < shapes.length; i++)
			msg += (i!=0 ? ", " : "") +shapes[i].toString();
		throw new ShapeException("Integration algorithm is not defined for arguments with shapes: " + msg);
	}
}