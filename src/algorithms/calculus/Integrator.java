package algorithms.calculus;

import algorithms.Algorithm;
import helpers.CSVHandler;
import helpers.Setting;
import helpers.Shape;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import main.Calculator;
import mathobjects.MFunction;
import mathobjects.MReal;
import mathobjects.MVector;
import mathobjects.MathObject;

public class Integrator extends Algorithm {

	MFunction f;
	MReal[] a;
	MReal[] b;
	int steps;
	int multiplier;
	double[] weights;
	double[] abscissae;
	double[][] lcoef;

	public Integrator() {
	};

	public Integrator(MFunction f, MReal[] a, MReal b[], int steps) {
		f = (MFunction) f.evaluate();
		this.a = a;
		this.b = b;
		this.steps = steps;
		this.multiplier = 1;
		prepared = true;
	}

	public Integrator(MFunction f, MReal[] a, MReal[] b) {
		this(f, a, b, Setting.getInt(Setting.INT_DEF_STEPS));
	}

	public Integrator(MFunction f, MReal a, MReal b, int steps) {
		this(f, new MReal[] { a }, new MReal[] { b }, steps);
	}

	public Integrator(MFunction f, MReal a, MReal b) {
		this(f, a, b, Setting.getInt(Setting.INT_DEF_STEPS));
	}

	public MReal execute() {
		if (!prepared)
			return null;
		multiplier = 1;
		for (int i = 0; i < a.length; i++) {
			if (a[i].getValue() > b[i].getValue()) {
				MReal c = a[i];
				a[i] = b[i];
				b[i] = c;
				multiplier *= -1;
			} else if (a[i].equals(b[i]))
				return new MReal(0);
		}
		MReal[] x = new MReal[a.length];
		for (int i = 0; i < a.length; i++)
			x[i] = (MReal) a[i].copy();
		return new MReal(integrate(f, x, a, b, steps, 0) * multiplier);
	}

	@Override
	public MReal execute(MathObject... args) {
		prepare(args);
		return execute();
	}

	private void loadGaussQuadData(int n) {
		if (abscissae == null || weights == null || abscissae.length != n || weights.length != n) {
			abscissae = CSVHandler.readDoubleLine("/files/gaussquad_abscissae.dat", n - 2);
			weights = CSVHandler.readDoubleLine("/files/gaussquad_weights.dat", n - 2);
		}
		if(abscissae == null || weights == null)
			throw new RuntimeException("Could not read abscissae and/or weights from data files.");
	}

	private double gauss_quad(MFunction f, MReal[] x, MReal[] a, MReal[] b, int steps, int index) {
		double sum = 0;
		double c = (b[index].getValue() - a[index].getValue()) / 2;
		double d = (a[index].getValue() + b[index].getValue()) / 2;
		try {
			for (int i = 0; i < steps; i++) {
				x[index].setValue(c * abscissae[i] + d);
				if (index == x.length - 1)
					sum += ((MReal) f.evaluateAt(x)).getValue() * weights[i];
				else {
					sum += integrate(f, x, a, b, steps, index + 1) * weights[i];
					for (int j = index + 1; j < a.length; j++)
						x[j].setValue(a[j].getValue());
				}
			}
		} catch (TreeException e) {
			Calculator.errorHandler.handle(e);
		}
		return sum * c;
	}

	private double integrate(MFunction f, MReal[] x, MReal[] a, MReal[] b, int steps, int index) {
		if (steps < 2 || steps>64)
			throw new IllegalArgumentException("Can't integrate with less than 2 or more than 64 steps, got " + steps);
		loadGaussQuadData(steps);
		return gauss_quad(f, x, a, b, steps, index);
	}

	@Override
	protected void prepare(MathObject[] args) {
		super.prepare(args);
		// Evaluate the second and third parameter (lower and upper bound of the
		// integration).
		// This also copies the values (preventing them from being changed during the
		// algorithm) and replaces Variables with their values.
		if(args.length < 3)
			throw new IllegalArgumentException("Expected at least 3 arguments, type help(integral) for more info.");
		args[1] = args[1].evaluate();
		args[2] = args[2].evaluate();
		// Check if all arguments are of the correct type.
		if ((args.length == 4 || args.length == 3) && args[0] instanceof MFunction
				&& ((args[1] instanceof MVector && args[2] instanceof MVector
						&& ((MVector) args[1]).size() == ((MVector) args[2]).size())
						|| (args[1] instanceof MReal && args[2] instanceof MReal))) {
			// Set the function to the first parameter
			f = (MFunction) args[0].evaluate();

			// Check argument 2 and 3
			if (args[1] instanceof MVector && ((MVector) args[1]).isOfType(MReal.class)
					&& ((MVector) args[2]).isOfType(MReal.class)) {
				// the upper and lower bound are vectors, so the integration has more than one
				// dimension.
				MVector v = (MVector) args[1];
				MVector w = (MVector) args[2];
				a = new MReal[v.size()];
				b = new MReal[v.size()];
				for (int i = 0; i < v.size(); i++) {
					a[i] = (MReal) v.get(i);
					b[i] = (MReal) w.get(i);
				}
			} else if (args[1] instanceof MReal) {
				// to bounds are scalars, so its a standard 1D integral.
				a = new MReal[1];
				b = new MReal[1];
				a[0] = (MReal) args[1];
				b[0] = (MReal) args[2];
			} else
				throw new IllegalArgumentException("Argument 2 and 3 have to be MVectors or MReals");

			// Check argument 4
			if (args.length == 4) {
				args[3] = args[3].evaluate();
				if (args[3] instanceof MReal && ((MReal) args[3]).isInteger())
					steps = (int) ((MReal) args[3]).getValue();
				else
					throw new IllegalArgumentException(
							"Argument 4 has to be an integer valued scalar, got " + args[3].toString());
			} else
				// If there is no 4th argument, set steps to the default (10,000).
				steps = Setting.getInt(Setting.INT_DEF_STEPS);
		} else
			throw new IllegalArgumentException("Arguments " + argTypesToString(args)
					+ " not applicable for Integration algorithm see help for the correct use.");
	}

	@Override
	public Shape shape(Shape... shapes) {
		if (shapes[0].dim() == 0 && shapes[1].equals(shapes[2]) && shapes[1].dim() <= 1
				&& ((shapes.length == 4 && shapes[3].dim() == 0) || shapes.length == 3))
			return new Shape();
		String msg = "";
		for (int i = 0; i < shapes.length; i++)
			msg += (i != 0 ? ", " : "") + shapes[i].toString();
		throw new ShapeException("Integration algorithm is not defined for arguments with shapes: " + msg);
	}

/*	Methods for calculating abscissae and weights of Legendre Polynomials for Gaussian Quadrature.
 * Note that these methods only work for an even n, and n<44
 * private void legendreCoef(int k, int N) {
		if(k==0) {
			lcoef[0][0]=lcoef[1][1]=1;
			lcoef[1][0]=0;
			k=1;
		}
		//(n+1)P_(n+1)=(2n+1)*x*P_n-nP_(n-1)
		//lcoef[n+1][m]=((2n+1)*lcoef[n][m-1]-n*lcoef[n-1][m])/(n+1)
		for(int n = k; n <N; n++) {
			lcoef[n+1][0]=-n*lcoef[n-1][0]/(n+1);
			for(int m = 1; m <= N; m++) {
				lcoef[n+1][m]=((2*n+1)*lcoef[n][m-1]-n*lcoef[n-1][m])/(n+1);
			}
		}
	}

	private double legendre(int n, double x) {
		if (x == 0)
			return 0;
		double p = 0;
		for (int i = 0; i <= n; i += 2)
			p += lcoef[n][i] * Math.pow(x, i);
		return p;
	}

	private double legendreDiff(int n, double x) {
		if (x == 0)
			return 0;
		double p = 0;
		for (int i = 2; i <= n; i += 2) //skip the i=0 term, as the derivative of x^0=0
			p += i * lcoef[n][i] * Math.pow(x, i - 1);
		return p;
	}

	private void legendreRoots(int n) {
		double l, s, z, epsilon;
		int count;
		for (int i = 0; i < n / 2; i++) {
			l = Math.cos(Math.PI * (i + 0.75) / (n + 0.5));
			epsilon = 1E-10;
			count = 0;
			do {
				s = l;
				l -= legendre(n, l) / legendreDiff(n, l);
				count++;
				if(count>100) {
					count=0;
					epsilon*=10;
				}
			} while (Math.abs(l - s) > epsilon);
			if(epsilon!=1E-10)
				System.out.println("For n=" + n + ", i=" + i + " used epsilon: " + epsilon);
			abscissae[i] = l;
			abscissae[n - i - 1] = -l;
			z = legendreDiff(n, l);
			weights[i] = weights[n - i - 1] = 2 / ((1 - l * l) * z * z);
		}
	}

	private void calculateAbscissae(int n) {
		abscissae = new double[n];
		weights = new double[n];
		if(lcoef==null) {
			lcoef = new double[n + 1][n + 1];
			legendreCoef(0,n);
		} if(lcoef.length<n) {
			double[][] temp = lcoef;
			lcoef = new double[n + 1][n + 1];
			for(int i = 0; i < temp.length; i++)
				for(int j = 0; j < temp[i].length; j++)
					lcoef[i][j]=temp[i][j];
			legendreCoef(temp.length-1, n);
		}
		legendreRoots(n);
	}*/
}