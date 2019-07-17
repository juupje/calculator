package algorithms.linalg;

import algorithms.Algorithm;
import helpers.Shape;
import helpers.Tools;
import helpers.exceptions.ShapeException;
import mathobjects.MMatrix;
import mathobjects.MReal;
import mathobjects.MVector;
import mathobjects.MathObject;
import static algorithms.linalg.MatrixToolkit.*;

public class Eigenvalues extends Algorithm {

	MatrixToolkit<?> mtk;
	int matrixType;
	MMatrix m;
	double epsilon = 1e-6;
	
	@Override
	public MathObject execute() {
		if ((matrixType & UTRIANGULAR) == UTRIANGULAR || (matrixType & LTRIANGULAR) == LTRIANGULAR)
			return new Diagonal(m).execute();
		if ((matrixType & REAL) == REAL) {
			DoubleMatrixToolkit tk = (DoubleMatrixToolkit) mtk;
			if((matrixType & UHESSENBERG) != UHESSENBERG)
				householder(tk);
			Double[][][] qr;
			double[] lambda = new double[tk.rows];
			
			double x = 0;
			int counter = 0;
			do {
				for(int j = 0; j <lambda.length; j++)
					lambda[j] = tk.matrix[j][j];
				
				for(int i = 0; i < 5; i++) {
					qr = QR(tk.matrix);
					tk.matrix = DoubleMatrixToolkit.multiply(qr[1], qr[0]);
				}
				counter += 5;
				x=0;
				for(int j = 0; j < lambda.length; j++)
					x += Math.abs(lambda[j]/tk.matrix[j][j]);
				x/=lambda.length;
			} while(Math.abs(x-1)>epsilon && counter<100);
			System.out.println(tk.toMMatrix().toString());
			System.out.println("Used " + counter + " QR-steps");
			//The eigenvalues can be read along the diagonal of the matrix
			MVector vec = new MVector(tk.rows);
			for(int i = 0; i < vec.size(); i++)
				vec.set(i, new MReal(tk.matrix[i][i]));
			return vec;
		}
		return MReal.NaN();
	}
	
	/*
	 * This uses Householder reflections to transform the given matrix
	 * to a similar Hessenberg matrix. 
	 */
	private Double[][] householder(DoubleMatrixToolkit tk) {
		Double[][] A = tk.matrix;
		Double[][] U = null;
		for(int k = 0; k < tk.cols-2;k++) {	
			//calculate the householder vector
			double[] u = new double[tk.rows-k-1];
			double n = 0; //normalization
			for(int i = 1; i<u.length;i++) {
				u[i] = A[i+k+1][k];
				n += u[i]*u[i];
			}
			u[0] = A[k+1][k];
			u[0] -= Math.signum(u[0])*Math.sqrt(n+u[0]*u[0]);
			n = Math.sqrt(n+u[0]*u[0]);
			for(int i = 0; i < u.length; i++)
				u[i] /= n;
			Double[][] P = computeP(tk.rows, u);
			if(U == null) U = P;
			else U = DoubleMatrixToolkit.multiply(U, P);
			A = DoubleMatrixToolkit.multiply(DoubleMatrixToolkit.multiply(P, A), P);
		}
		tk.matrix = A;
		return U;
	}
	
	private Double[][] computeP(int size, double[] u) {
		Double[][] A = new Double[size][size];
		int offset = size-u.length;
		for(int i = -offset; i < A.length-offset; i++)
			for(int j = -offset; j < A[0].length-offset; j++) {
				if(i < 0 || j < 0)
					A[i+offset][j+offset] = (i==j ? 1d : 0d);
				else
					A[i+offset][j+offset] = (i==j ? 1d : 0d) - 2*u[i]*u[j];
			}
		return A;
	}
	
	private Double[][][] QR(Double[][] H) {
		Double[][] G = DoubleMatrixToolkit.identity(H.length);
		Double[][] Q = DoubleMatrixToolkit.identity(H.length);
		for(int k = 0; k < H.length-1; k++) {
			//calculate the givens matrix G(k, k+1, phi)
			double n = Math.sqrt(H[k][k]*H[k][k]+H[k+1][k]*H[k+1][k]);
			double c = H[k][k]/n;
			double s = -H[k+1][k]/n;
			G[k+1][k+1] = G[k][k] = c;
			G[k][k+1] = -s;
			G[k+1][k] = s;
			H = DoubleMatrixToolkit.multiply(G, H);
			
			//transpose the matrix
			G[k][k+1] = s;
			G[k+1][k] = -s;
			Q = DoubleMatrixToolkit.multiply(Q, G);
			G[k+1][k+1] = G[k][k] = 1d;
			G[k+1][k] = G[k][k+1] = 0d;
		}
		
		return new Double[][][] {Q, H};
		
	}
	
	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}

	@Override
	protected void prepare(MathObject[] args) {
		mtk = null;
		prepared = false;
		matrixType = 0;
		if (args.length == 1 && args[0] instanceof MMatrix) {
			if (((MMatrix) args[0]).isSquare()) {
				m = (MMatrix) args[0];
				mtk = getToolkit(m);
				matrixType = mtk.classify();
				prepared = true;
				return;
			} else
				throw new ShapeException("Only square matrices have eigenvalues, got shape " + args[0].shape());
		} else
			throw new IllegalArgumentException(
					"Eig expects one argument (a square matrix), got: " + argTypesToString(args) + ". See help(eig).");
	}

	@Override
	public Shape shape(Shape... shapes) {
		if (shapes.length == 1 && shapes[0].dim() == 2 && shapes[0].isSquare())
			return new Shape(shapes[0].rows());
		throw new ShapeException(
				"eig is only applicable to one square matrix, got shape(s): " + Tools.join(", ", (Object[]) shapes));
	}

}
