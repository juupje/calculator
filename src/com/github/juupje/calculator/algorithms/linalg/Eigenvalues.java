package com.github.juupje.calculator.algorithms.linalg;

import static com.github.juupje.calculator.algorithms.linalg.MatrixToolkit.*;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.algorithms.algebra.ABCFormula;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MSubMatrix;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class Eigenvalues extends Algorithm {

	MatrixToolkit<?> mtk;
	int matrixType;
	MMatrix m;
	int n;
	double epsilon = 1e-6;
	
	@Override
	public MVector execute() {
		if ((matrixType & UTRIANGULAR) == UTRIANGULAR || (matrixType & LTRIANGULAR) == LTRIANGULAR)
			return eigDiag(m);
		if(mtk.rows==1)
			return eig1(m);
		if(mtk.rows==2) {
			if(mtk instanceof DoubleMatrixToolkit)
				return eig2((DoubleMatrixToolkit)mtk);
			else
				return new MVector(eig2((ScalarMatrixToolkit)mtk));
		} else if(mtk.rows==3) {
			if(mtk instanceof DoubleMatrixToolkit)
				return eig3((DoubleMatrixToolkit)mtk);
			else
				return eig3((ScalarMatrixToolkit)mtk);
		}
		
		if(mtk instanceof DoubleMatrixToolkit) {
			Double[][] U = null;
			DoubleMatrixToolkit dmtk = (DoubleMatrixToolkit) mtk;
			if((matrixType & UHESSENBERG) != UHESSENBERG)
				U = hessenberg(dmtk);
			//Matrix is now tri-diagonal (as only symmetric matrices have guaranteed real eigenvalues)
			Double[][] Q = eig_francis(dmtk);
			//if(U != null)
			//	Q = DoubleMatrixToolkit.multiply(U, Q);
			double[] d = new double[n];
			for(int i = 0; i < n; i++)
				d[i] = dmtk.matrix[i][i];
			return new MVector(d);
		} else {
			MMatrix U_hess = hessenberg(m);
			ScalarMatrixToolkit smtk = new ScalarMatrixToolkit(m);
			ScalarMatrixToolkit Uhess = new ScalarMatrixToolkit(U_hess);
			ScalarMatrixToolkit Uwilk = QR_wilkinson(smtk);
			MScalar[] eigs = new MScalar[n];
			for(int i = 0; i < n; i++)
				eigs[i] = smtk.get(i, i);
			Uhess.transpose().multiplyRight(Uwilk.matrix);
			return new MVector(eigs);
		}
	}
	
	private MVector eig1(MMatrix m) {
		return new MVector(m.get(0, 0).copy());
	}
	
	private MVector eigDiag(MMatrix m) {
		int n = m.shape().rows();
		MathObject[] v = new MathObject[n];
		for(int i = 0; i <n; i++)
			v[i] = m.get(i, i).copy();
		return new MVector(v);
	}
	
	private MVector eig2(DoubleMatrixToolkit m) {
		double tr = m.get(0, 0)+m.get(1, 1);
		double det = m.get(0, 0)*m.get(1, 1)-m.get(1, 0)*m.get(0, 1);
		double d = tr*tr-4*det;
		double a = 0.5d*tr;
		if(d>=0) {
			double b = 0.5d*Math.sqrt(d);
			return new MVector(new MReal(a+b), new MReal(a-b));
		}
		double b = 0.5d*Math.sqrt(-d);
		return new MVector(new MComplex(a,b), new MComplex(a,-b));
	}
	
	private MScalar[] eig2(ScalarMatrixToolkit m) {
		MScalar tr = m.get(0, 0).copy().add(m.get(1, 1));
		MScalar det = m.get(0, 0).copy().multiply(m.get(1, 1)).subtract(m.get(1, 0).copy().multiply(m.get(0, 1)));
		MScalar a = tr.copy().multiply(0.5d);
		MScalar b = tr.multiply(tr).subtract(det.multiply(4)).sqrt().multiply(0.5d);

		return new MScalar[] {a.copy().add(b), a.subtract(b)};
	}
	
	private MVector eig3(DoubleMatrixToolkit m) {
		Double[][] matrix = m.matrix;
		double b = matrix[0][0]+matrix[1][1]+matrix[2][2];
		double c = matrix[0][1]*matrix[1][0]+matrix[2][1]*matrix[1][2]+matrix[0][2]*matrix[2][0]
					-matrix[0][0]*matrix[1][1]-matrix[1][1]*matrix[2][2]-matrix[0][0]*matrix[2][2];
		double d = matrix[0][0]*matrix[1][1]*matrix[2][2]+matrix[2][0]*matrix[0][1]*matrix[1][2]+matrix[0][2]*matrix[1][0]*matrix[2][1]
					-matrix[2][2]*matrix[1][0]*matrix[0][1]-matrix[2][1]*matrix[1][2]*matrix[0][0]-matrix[2][0]*matrix[0][2]*matrix[1][1];
		
		return new ABCFormula(-1, b, c, d).execute();
	}
	
	private MVector eig3(ScalarMatrixToolkit m) {
		MScalar[][] matrix = m.matrix;
		MScalar B = matrix[0][0].copy().add(matrix[1][1]).add(matrix[2][2]);
		MScalar C = matrix[1][0].copy().multiply(matrix[0][1]).copy().add(matrix[1][2].copy().multiply(matrix[2][1])).add(matrix[2][0].copy().multiply(matrix[0][2]))
				.subtract(matrix[0][0].copy().multiply(matrix[1][1])).subtract(matrix[1][1].copy().multiply(matrix[2][2])).subtract(matrix[0][0].copy().multiply(matrix[2][2]));
		MScalar D = matrix[0][0].copy().multiply(matrix[1][1]).multiply(matrix[2][2]).add(matrix[2][0].copy().multiply(matrix[0][1]).multiply(matrix[1][2]))
				.add(matrix[0][2].copy().multiply(matrix[1][0]).multiply(matrix[2][1]))
				.subtract(matrix[2][2].multiply(matrix[1][0]).multiply(matrix[0][1])).subtract(matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][1]))
				.subtract(matrix[1][1].multiply(matrix[2][0]).multiply(matrix[0][2]));
		return new ABCFormula(new MReal(-1), B, C,D).execute();
	}
	
	/**
	 * Calculates Schur decomposition of the given matrix. That is, an upper triangular matrix T
	 * and an unitary matrix U such that A=UTU*. The Eigenvalues of the matrix can then be read on the diagonal of T
	 * @param A the input matrix. It is overwritten with T
	 * @return the unitary matrix U
	 */
	private ScalarMatrixToolkit QR_wilkinson(ScalarMatrixToolkit A) {
		ScalarMatrixToolkit U = ScalarMatrixToolkit.identity(n);
		int iters = 0;
		for(int m = n-1; m>0; m--) {
			ScalarMatrixToolkit B = A.sub(0,m, 0,m);
			int count = 0;
			while(B.get(m, m-1).abs()>1e-30 && count++<100) {
				MScalar[] eigs = eig2(B.sub(m-1,m, m-1,m));
				MScalar sigma;
				if(eigs[0].copy().subtract(B.get(m, m)).abs() < eigs[1].copy().subtract(B.get(m, m)).abs())
					sigma = eigs[0];
				else
					sigma = eigs[1];
				QR_step(B.addDiag(sigma.negate()), U); //sigma -> -sigma
				B.addDiag(sigma.negate());
			}
			iters += count;
		}
		Calculator.ioHandler.debug("EIGENVALUES: iterations="+iters);
		return U;
	}
	
	/**
	 * Calculates the eigenvalues of a symmetric tridiagonal matrix.
	 * This algorithm has a drawback though: <br/>
	 * <b>IT DOESN'T FRICKIN' WORK!! WHY TF NOT?</b>
	 * @param tk A symmetric tridiagonal matrix
	 * @return the matrix Q containing the eigenvectors of the matrix in its columns
	 */
	private Double[][] eig_francis(DoubleMatrixToolkit tk) {
		int p = n-1;
		int count = 0;
		Double[][] H = tk.matrix;
		while(p>1 && count++ < 1000) {
			int q = p-1;
			double s = H[q][q]+H[p][p];
			double t = H[q][q]*H[p][p]-H[q][p]*H[p][q];
			double x = H[0][0]*H[0][0]+H[0][1]*H[1][0]-s*H[0][0]+t;
			double y = H[1][0]*(H[0][0]+H[1][1]-s);
			double z = H[1][0]*H[2][1];
			for(int k = 0; k <= p-2; k++) {
				double norm = y*y+z*z;
				x += Math.copySign(Math.sqrt(norm+x*x), x);
				norm = Math.sqrt(norm+x*x);
				x /= norm;
				y /= norm;
				double z1 = z / norm;
				double[][] P = {{1-2*x*x, -2*x*y, -2*x*z1}, {-2*y*x, 1-2*y*y, -2*y*z1}, {-2*z1*x, -2*z1*y, 1-2*z1*z1}};
				int r = Math.max(0, k-1);
				tk.sub(k,k+2, r, n-1).multiplyLeft(P);
				r = Math.min(k+3, p);
				tk.sub(0,r, k,k+2).multiplyRight(P);
				x = H[k+1][k];
				y = H[k+2][k];
				if(k+3<=p)
					z = H[k+3][k];
			}
			double[] g = givens(x,y);
			double[][] P = {{g[0], -g[1]}, {g[1], g[0]}};
			tk.sub(q,p, p-2,n-1).multiplyLeft(P);
			P[0][1] = -P[0][1]; P[1][0] = -P[1][0];
			tk.sub(0,p, p-1, p).multiplyRight(P);
			if(Math.abs(H[p][q])<1e-40*(Math.abs(H[q][q])+Math.abs(H[p][p]))) {
				H[p][q] = 0d; p--;	
			} else if(Math.abs(H[p-1][q-1])<1e-40*(Math.abs(H[q-1][q-1]+Math.abs(H[q][q])))) {
				H[p-1][q-1] = 0d;
				p -= 2;
			}
		}
		if(count>=1000)
			Calculator.ioHandler.err("Eigenvalue algorithm did not converge within 1000 iterations, the answer is probably incorrect.");
		else {
			Calculator.ioHandler.debug("EIGENVALUES: iterations="+count);
		}
		return null;
	}
	
	/*
	 * This uses Householder reflections to transform the given matrix
	 * to a similar Hessenberg matrix. 
	 */
	private Double[][] hessenberg(DoubleMatrixToolkit tk) {
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
			u[0] += Math.signum(u[0])*Math.sqrt(n+u[0]*u[0]);
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
	
	/**
	 * Transforms a matrix A to an upper hessenberg form using householder reflections.
	 * The resulting matrix H is similar to A, so A=UAU*. A is overwritten with H
	 * @param A the Matrix to be similarly transformed to a Hessenberg matrix
	 * @return the transformation matrix U
	 */
	private MMatrix hessenberg(MMatrix A) {
		MMatrix U = MMatrix.identity(n);
		for(int k = 0; k < n-2; k++) {
			MVector v = new MVector(householder(A, k)); //column vector
			
			//Multiply A with the householder reflector P=I-2*v*v' -> A:=P*A (in a more efficient way) from the left
			MSubMatrix B = A.getSubMatrix(k+1, n, k, n);
			MVector vB2 = ((MVector) B.multiplyRight(v.transpose())).multiply(2); //row vector, v->v'
			v.transpose(); //v'->v
			B.subtract((MMatrix) v.multiply(vB2)); //The new B is copied into A automatically
			//B is now B-2*v*(v'*B)
			
			//Multiply A with the householder reflector P from the right A:=A*P
			B = A.getSubMatrix(0, n, k+1, n);
			B.subtract((MMatrix)((MVector)B.multiplyLeft(v)).multiply(v.transpose().multiply(2))); //v -> 2v'
			//A is now PAP
			
			//Update U to be U:=U*P
			v.transpose(); //v->2v
			B = U.getSubMatrix(0, n, k+1, n);
			B.subtract((MMatrix)((MVector)B.multiplyLeft(v)).multiply(v.transpose().divide(2))); // v->v'
		}
		return U;
	}
	
	private MScalar[] householder(MMatrix A, int k) {
		MScalar[] u = new MScalar[n-k-1];
		double norm = 0;
		for(int i = k+2, j=1; i<n; i++, j++) {
			u[j] = (MScalar) A.get(i, k).copy();
			norm += u[j].abs2();
		}
		u[0] = (MScalar) A.get(k+1, k).copy();
		u[0].add(Math.sqrt(norm+u[0].abs2())*(u[0].real()>0 ? 1 : -1)); //can't use signum in case u[0]==0
		norm = Math.sqrt(norm+u[0].abs2());
		for(int i = 0; i < u.length; i++)
			u[i].divide(norm);
		return u;
	}
	
	public double[] givens(double x, double y) {
		double d = Math.sqrt(x*x+y*y);
		return new double[] {x/d, -y/d};
	}
	
	public MScalar[] givens(MScalar a, MScalar b) {
		double d = Math.sqrt(a.abs2()+b.abs2());
		MScalar c = a.copy().divide(d);
		MScalar s = b.copy().divide(-d);
		if(!a.isComplex() && !b.isComplex()) {
			return new MScalar[] {c, s.copy().negate(), s, c.copy()};
		} else {
			return new MScalar[] {MScalar.conj(c), MScalar.conj(s).negate(), s, c};
		}
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
	
	/**
	 * Calculates the QR decomposition of H and replaces H with H'=RQ.
	 * This happens implicitly.
	 * @param H the upper hessenberg matrix on which a QR step should be applied.
	 * @param U the transformation matrix which keeps track of all transformations applied to H (can be {@code null}).
	 */
	private void QR_step(ScalarMatrixToolkit H, ScalarMatrixToolkit U) {
		int n = H.rows;
		MScalar[][] g = new MScalar[n-1][4];
		for(int k = 0; k<n-1; k++) {
			g[k] = givens(H.matrix[k][k], H.matrix[k+1][k]);
			H.sub(k,k+1, k,n-1).multiplyLeft(new MScalar[][] {{g[k][0], g[k][1]}, {g[k][2], g[k][3]}});
		}
		for(int k = 0; k < n-1; k++) {
			MScalar[][] G = new MScalar[][] {{g[k][3], g[k][2].conjugate()}, {g[k][1].conjugate(), g[k][0]}};
			H.sub(0,k+1, k,k+1).multiplyRight(G);
			if(U != null)
				U.sub(0,n-1, k,k+1).multiplyRight(G);
		}
	}
	
	/*private Double[][][] QRDecomposition(Double[][] H) {
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
	}*/
	
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
		n = 0;
		if (args.length == 1 && args[0] instanceof MMatrix) {
			if (((MMatrix) args[0]).isSquare()) {
				m = (MMatrix) args[0];				
				//check symmetry
				boolean symmetric = true;
				for(int i = 1; i < m.shape().rows() && symmetric; i++) {
					for(int j = 0; j < i; j++)
						if(!m.get(i, j).equals(m.get(j, i))) {
							symmetric = false;
							break;
						}
				}
				if(symmetric)				
					mtk = getToolkit(m);
				else
					mtk = new ScalarMatrixToolkit(m);
				matrixType = mtk.classify();
				n = mtk.rows;
				if((matrixType & REAL) == REAL) {
					prepared = true;
					return;
				} else if(m.shape().rows()<=3) {
					prepared = true;
					return;
				} else
					throw new IllegalArgumentException("Eigenvalues of complex matrices are not yet supported for matrix with size>3");
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
