package com.github.juupje.calculator.algorithms.linalg;

import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;

public class ScalarMatrixToolkit extends MatrixToolkit<MScalar> {

	public ScalarMatrixToolkit(MScalar[][] matrix) {
		super(matrix);
	}
	
	public ScalarMatrixToolkit(MMatrix m) {
		super(m.shape().rows(), m.shape().cols());
		try {
			matrix = new MScalar[rows][cols];
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					matrix[i][j] = (MScalar) m.get(i, j);
		} catch(ClassCastException e) {
			throw new IllegalArgumentException("Cannot calculate eigenvalues of non-numeric matrix.");
		}
	}
	
	public ScalarMatrixToolkit(MScalar[][] matrix, int rstart, int rend, int cstart, int cend) {
		super(matrix, rstart, rend, cstart, cend);
	}

	/**
	 * Adds the i-th row multiplied with c to the n-th row. Like so: <br/>
	 * <tt>M_nj -> M_nj+c*M_ij   with   j=1....m</tt>
	 * @param n the index of the row to which the <tt>i</tt>-th row will be added.
	 * @param i the row to be added to the <tt>n</tt>-th row
	 * @param c a constant with which the i-th row will be multiplied before being added to the <tt>n</tt>-th row. Left unchanged.
	 */
	@Override
	public void addToRow(int n, int i, MScalar c) {
		i += rstart; n += rstart;
		for(int j = cstart; j <= cend; j++)
			matrix[n][j] = matrix[n][j].add(c.copy().multiply(matrix[i][j]));
	}
	
	/**
	 * Multiplies the <tt>n</tt>-th row with a constant scalar value.
	 * @param n the index of the row to be multiplied with <tt>c</tt>
	 * @param c the value with which the row will be multiplied.
	 */
	@Override
	public void multiplyRow(int n, MScalar c) {
		n += rstart;
		for(int j = cstart; j <= cend; j++)
			matrix[n][j] = matrix[n][j].multiply(c);
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: C_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M the matrix B
	 */
	public void multiplyRight(MScalar[][] M) {
		if(cols != M.length)
			throw new ShapeException("Can't multiply matrices of size ("
					+ rows + "x" + cols + ") and (" + M.length + "x" + M[0].length + ")");
		if(isSubMatrix && M[0].length != cols)
			throw new ShapeException("Cannot multiply submatrix with non-square matrix! Shape (" + M.length + ", "  +M[0].length + ")");
		MScalar[][] result = new MScalar[rows][M[0].length];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = new MReal(0);
				for(int k = 0; k < cols; k++)
					result[i][j] = result[i][j].add(matrix[i+rstart][k+cstart].copy().multiply(M[k][j]));
			}
		}
		rows = result.length;
		cols = result[0].length;
		if(isSubMatrix) {
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					matrix[i+rstart][j+cstart] = result[i][j];
		} else			
			matrix = result;
	}
	
	/**
	 * Multiplies the matrix (from the left) with the given matrix using a matrix product.
	 * In Einstein notation: C_ij=B_ik*A_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M the matrix B
	 */
	public void multiplyLeft(MScalar[][] M) {
		if(M[0].length != rows)
			throw new ShapeException("Can't multiply matrices of size ("
					+ M.length + "x" + M[0].length + ") and (" + rows + "x" + cols + ")");
		if(isSubMatrix && M.length != rows)
			throw new ShapeException("Cannot multiply submatrix with non-square matrix! Shape (" + M.length + ", "  +M[0].length + ")");
		MScalar[][] result = new MScalar[M.length][cols];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = new MReal(0);
				for(int k = 0; k < M[0].length; k++)
					result[i][j] = result[i][j].add(M[i][k].copy().multiply(matrix[k+rstart][j+cstart]));
			}
		}
		rows = result.length;
		cols = result[0].length;
		if(isSubMatrix) {
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					matrix[i+rstart][j+cstart] = result[i][j];
		} else			
			matrix = result;
	}
	
	public ScalarMatrixToolkit addDiag(MScalar a) {
		int max = Math.min(rows, cols);
		for(int i = 0; i < max; i++)
			matrix[i+rstart][i+cstart]=matrix[i+rstart][i+cstart].add(a);
		return this;
	}
	
	@Override
	public MMatrix toMMatrix() {
		return new MMatrix(matrix);
	}
	
	@Override
	public boolean isReal() {
		for(int i = rstart; i<= rend; i++)
			for(int j = cstart; j <= cend; j++)
				if(matrix[i][j].isComplex()) return false;
		return true;
	}
	
	/**
	 * Checks if the matrix is hermitian. This means that the matrix equals its conjugated transpose.
	 * If the matrix is real and symmetric, it is also hermitian (this is not valid the other way around).
	 * @param mask the mask integer holding (at least) the flags REAL and SYMMETRIC.
	 * @return {@code true} if A_ij=A_ji* for every element A_ij in the matrix, {@code false} otherwise.
	 */
	@Override
	protected final boolean isHermitian(int mask) {
		if((mask & REAL) == REAL)
			return (mask & SYMMETRIC) == SYMMETRIC;
		for(int i = rstart; i <= rend; i++) {
			if(matrix[i][i].isComplex()) return false;
			for(int j = i+1; j<= cend; j++)
				if(!matrix[i][j].equals(matrix[j][i].copy().conjugate()))
					return false;
		}
		return true;
	}
	
	/**
	 * Recursively reorders the rows of this matrix.
	 * This means that the rows with the most leading zeros will end up on the bottom,
	 * and the rows with no leading zeros will end up at the top.
	 * @author Siemen Geurts
	 * @param maxRow the row at which the reordering stops (used for the recursion. To reorder the whole matrix, set <tt>maxRow=matrix.rows-1</tt>)
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void reorder(int maxRow) {
		if(maxRow <= rstart) return;
		maxRow = Math.min(maxRow, rows);
		int prevZeros = 0, curZeros = 0;
		for(int i = rstart; i < maxRow; i++) {
			int j = cstart;
			curZeros = 0;
			while(j < cols-augmcols && matrix[i][j].equals(0)) { j++; curZeros++;}
			if(curZeros < prevZeros)
				switchRows(i-rstart, i-rstart-1);
			else
				prevZeros = curZeros;
		}
		reorder(maxRow-1);
	}
	
	@Override
	public ScalarMatrixToolkit transpose() {
		if(rows==cols)
			return (ScalarMatrixToolkit) super.transpose();
		else {
			if(isSubMatrix) {
				throw new ShapeException("Cannot transpose a non-square submatrix");
			} else {
				MScalar[][] m = new MScalar[cols][rows];
				for(int i = 0; i < rows; i++) {
					for(int j = 0; j < cols; j++)
						m[j][i] = matrix[i][j];
				}
				return new ScalarMatrixToolkit(m);
			}
		}
	}

	public ScalarMatrixToolkit sub(int rstart, int rend, int cstart, int cend) {
		return new ScalarMatrixToolkit(matrix, rstart+this.rstart, rend+this.rstart, cstart+this.cstart, cend+this.cstart);
	}
	
	@Override
	public String toString() {
		return Printer.toText(matrix, rstart, rend, cstart, cend);
	}
	
	public static ScalarMatrixToolkit identity(int n) {
		MScalar[][] matrix = new MScalar[n][n];
		for(int  i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				matrix[i][j] = new MReal(i==j ? 1 : 0);
		return new ScalarMatrixToolkit(matrix);
	}
}