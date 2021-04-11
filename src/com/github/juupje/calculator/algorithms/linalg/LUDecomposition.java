package com.github.juupje.calculator.algorithms.linalg;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MIndexable;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class LUDecomposition extends Algorithm {

	Shape shape;
	DoubleMatrixToolkit mtk;
	int permutations = 0;
	private static final double TOLERANCE = 1e-10;
	boolean degenerate = false;
	
	public LUDecomposition() {}
	
	public LUDecomposition(MMatrix m) {
		MatrixToolkit<?> tk = MatrixToolkit.getToolkit(m);
		if(!(tk instanceof DoubleMatrixToolkit))
			throw new IllegalArgumentException("LU-Decomposition only works for real-values matrices.");
		mtk = (DoubleMatrixToolkit) tk;
		shape = new Shape(mtk.rows, mtk.cols);
		prepared = true;
	}
	
	@Override
	public MIndexable execute() {
		if(!prepared) return null;
		permutations = 0;
		int[] order = lu();
		return expand(mtk.matrix, order);
	}
	
	@Override
	public MIndexable execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	/**
	 * Returns vector {@code x} that solves the equation {@code Ax=b} using the results of the LU decomposition.
	 * @param b MVector
	 * @return a MVector which equals {@code x=inv(A)b}
	 */
	public MVector solve(MVector b) {
		if(!prepared) return null;
		permutations = 0;
		int[] order = lu();
		int n = mtk.matrix.length;
		double[] y = new double[n];
		//First, we solve Ly=Pb -> Forward solve
		try {
			for(int i = 0; i < n; i++) {
				y[i] = ((MReal)b.get(order[i])).getValue(); //apply matrix P to b
				/*   /1 0 0 ... 0 \ /y0\    /b[P[0]]\
				 *   |* 1 0 ... 0 | |y1|  = |b[P[1]]|
				 * 
				 *   \* * * ... 1 / \yn/    \b[P[n]]/
				 */
				for(int j = 0; j < i; j++)
					y[i] -= mtk.matrix[i][j]*y[j];
			}
			
			//Solve Ux=y -> backward solve
			double[] x = y;
			for(int i = n-1; i >= 0; i--) {
				for(int j = i+1; j < n; j++)
					x[i] -= mtk.matrix[i][j]*x[j];
				x[i] /= mtk.matrix[i][i];
			}
			return new MVector(x);
		} catch(ClassCastException e) {
			throw new IllegalArgumentException("Could not solve for non-numeric or complex vector");
		}
	}
	
	/**
	 * Calculates the determinant using the lu decomposition.
	 * if {@code PA=LU}, then {@code det(P)det(A)=det(L)det(U) => det(A)=det(P)*det(U)}
	 * because {@code det(L)=prod_i L_ii=1} and {@code det(P)=+-1}.
	 * The determinant of U can be found by multiplying the diagonal entries together.
	 * @return the determinant of the matrix
	 */
	public double det() {
		if(!prepared) return Double.NaN;
		permutations = 0;
		lu();
		double dot = permutations%2==0 ? 1 : -1;
		for(int i = 0; i < mtk.matrix.length; i++)
			dot *= mtk.matrix[i][i];
		return dot;
	}
	
	/**
	 * Changes mtk.matrix such that it equals (L-I)+U where L is a lower triangular matrix
	 * with 1's on the diagonal and U an upper triangular matrix, such that LU=PA, where P is a permutation matrix.
	 * The permutation matrix is stored in an integer array which i-th value corresponds to the element in the i-th row 
	 * of P which equals 1.
	 * L can be reconstructed by taking the lower triangular part of mtk.matrix and setting the diagonals to 1.
	 * U can be reconstructed by taking the upper triangular part of mtk.matrix (including the diagonal).
	 * @return the order vector which can be used to construct P
	 */
	protected int[] lu() {
		int n = mtk.cols;
		int[] order = new int[n];
		
		//set the order to unity
		for(int i = 0; i < n; i++) order[i] = i;
		for(int col = 0; col < n; col++) {
			//Find pivot row
			int pivot = findPivotIndex(col);
			//Check if the matrix is degenerate
			if(Math.abs(mtk.matrix[pivot][col])<TOLERANCE) {
				degenerate=true;
				//throw new RuntimeException("LU-Decomposition failed, matrix is (near-)degenerate");
				continue;
			}
			
			if(pivot != col) {
				//We need to pivot row 'pivot' with row 'col'
				//Switch order[col]<->order[pivot]
				int temp = order[col];
				order[col] = order[pivot];
				order[pivot] = temp;
				
				mtk.switchRows(col, pivot);
				permutations++;
			}
			
			for(int row = col+1; row < n; row++) {
				mtk.matrix[row][col] /= mtk.matrix[col][col];
				for(int k = col+1; k < n; k++)
					mtk.matrix[row][k] -= mtk.matrix[row][col]*mtk.matrix[col][k];
			}
		}
		return order;
	}
	
	/**
	 * Using the output of {@link #lu()} this method reconstructs the matrices L, U and P.
	 * @param matrix the changed mtk.matrix of {@code lu()}
	 * @param order the returned array of {@code lu()}
	 * @return an MIndexedObject which equals [L, U, P]
	 */
	private MIndexedObject expand(Double[][] matrix, int[] order) {
		int n = matrix.length;
		MReal[][] L = new MReal[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(i==j) L[i][i] = new MReal(1); //Diagonal
				else if(i>j) L[i][j] = new MReal(mtk.matrix[i][j]); //row>col -> below diagonal
				else L[i][j] = new MReal(0); //above diagonal
			}
		}
		
		MReal[][] U = new MReal[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(j>=i) U[i][j] = new MReal(mtk.matrix[i][j]);//col>=row -> diagonal or above
				else U[i][j] = new MReal(0);
			}
		}
		
		MReal[][] P = new MReal[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(order[i]==j) P[i][j] = new MReal(1);
				else P[i][j] = new MReal(0);
			}
		}
		return new MIndexedObject(new Shape(3), new MathObject[] {new MMatrix(L), new MMatrix(U), new MMatrix(P)});
	}
	
	/**
	 * Finds the row at index {@code row>=col} which contains the highest absolute value in column {@code col}.
	 * This row can be pivoted with the {@code col}-th row for numerical stability.
	 * @param col the index of the column
	 * @return the pivot row of column {@code col}
	 */
	private int findPivotIndex(int col) {
		double max = 0, curr_abs;
		int indexMax = col;
		for(int row = col; row < mtk.cols; row++) {
			if((curr_abs = Math.abs(mtk.matrix[row][col])) > max) {
				max = curr_abs;
				indexMax = row;
			}
		}
		return indexMax;
	}
	
	public int getPermutationCount() {
		return permutations;
	}
	
	@Override
	public void prepare(MathObject[] args) {
		super.prepare(args);
		if(args.length == 1 && args[0] instanceof MMatrix) {
			//no need to copy as the toolkit only works with the double values.
			MatrixToolkit<?> tk = MatrixToolkit.getToolkit((MMatrix) args[0]);
			if(!(tk instanceof DoubleMatrixToolkit))
				throw new IllegalArgumentException("LU-Decomposition only works for real-values matrices.");
			mtk = (DoubleMatrixToolkit) tk; 
		}else
			throw new IllegalArgumentException("Arguments " + argTypesToString(args) + " not applicable for LU-decomposition, see help for correct use.");
		shape = new Shape(mtk.rows, mtk.cols);
	}
	
	@Override
	public Shape shape(Shape... shapes)  {
		if(shapes.length==0)
			throw new IllegalArgumentException("No arguments found.");
		if(shapes.length==1 && shapes[0].dim()==2) {
			if(shapes[0].rows() == shapes[0].cols())
				return shapes[0];
			else
				throw new ShapeException("LU-decomposition only works for square matrices.");
		}
		throw new IllegalArgumentException("Algorithm not defined for shapes " + Tools.join(", ", (Object[]) shapes));
	}
}