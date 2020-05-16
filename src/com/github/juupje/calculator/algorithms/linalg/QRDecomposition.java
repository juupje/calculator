package com.github.juupje.calculator.algorithms.linalg;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class QRDecomposition extends Algorithm {

	MatrixToolkit<?> tk;
	
	@Override
	public MathObject execute() {
		if(tk instanceof DoubleMatrixToolkit)
			return qr((DoubleMatrixToolkit) tk);
		else
			return qr((ScalarMatrixToolkit) tk);
	}
	
	private MVector qr(DoubleMatrixToolkit tk) {
		DoubleMatrixToolkit Q = null;
		int end = tk.cols >= tk.rows ? tk.rows-1 : tk.cols;
		for(int k = 0; k < end; k++) {
			DoubleMatrixToolkit sub = tk.sub(k, tk.rows-1, k, tk.cols-1);
			double[] v = householder(tk, k);
			double[] v2 = cmultiply(v, -2);
			sub.add(dyadic(v2, sub.multiplyLeft(v)));
			for(int i = k+1; i < tk.rows; i++)
				tk.matrix[i][k] = 0d;
			if(k==0) {
				//construct an identity matrix
				double[][] Q1 = new double[tk.rows][tk.rows];
				for(int i = 0; i < Q1.length; i++) Q1[i][i]=1;
				Q = new DoubleMatrixToolkit(Q1);
				Q.add(dyadic(v,v2));
			} else {
				sub = Q.sub(0, Q.rows-1, k, Q.cols-1);
				sub.add(dyadic(sub.multiplyRight(v2),v));
			}
		}
		return new MVector(Q.toMMatrix(), tk.toMMatrix());
	}
	
	private MVector qr(ScalarMatrixToolkit tk) {
		return null;
	}
	
	private double[] cmultiply(double[] v, double d) {
		double[] w = new double[v.length];
		for(int i = 0; i < v.length; i++)
			w[i] = v[i]*d;
		return w;
	}
	
	private double[][] dyadic(double[] v, double[] w) {
		if(v.length==w.length) {
			double[][] A = new double[v.length][v.length];
			for(int i = 0; i < v.length; i++) {
				A[i][i] = v[i]*w[i];
				for(int j = i+1; j < v.length; j++) {
					A[j][i] = v[i]*w[j];
					A[i][j] = v[j]*w[i];
				}
			}
			return A;
		} else {
			double[][] A = new double[v.length][w.length];
			for(int i = 0; i < v.length; i++) {
				for(int j = 0; j < w.length; j++)
					A[i][j] = v[i]*w[j];
			}
			return A;
		}
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		if(args.length != 1)
			throw new IllegalArgumentException("Expected one argument, got " + args.length);
		if(args[0] instanceof MMatrix) {
			tk = MatrixToolkit.getToolkit((MMatrix) args[0]);
			prepared = true;
		} else
			throw new IllegalArgumentException("Expected the first argument to be a matrix, got " + argTypeToString(args[0]));
	}

	@Override
	public Shape shape(Shape... shapes) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private double[] householder(DoubleMatrixToolkit dmtk, int col) {
		double[] u = new double[dmtk.rows - col];
		double norm = 0;
		u[0] = dmtk.get(col,col);
		for(int i = col+1, j=1; i<dmtk.rows; i++, j++) {
			u[j] = dmtk.get(i, col);
			norm += u[j]*u[j];
		}
		u[0] += Math.sqrt(norm+u[0]*u[0])*(u[0]>0 ? 1 : -1); //can't use signum in case u[0]==0
		norm = Math.sqrt(norm+u[0]*u[0]);
		for(int i = 0; i < u.length; i++)
			u[i] /= norm;
		return u;
	}
}