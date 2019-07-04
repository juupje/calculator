package algorithms.linalg;

import mathobjects.MComplex;
import mathobjects.MMatrix;
import mathobjects.MReal;
import mathobjects.MScalar;

public abstract class MatrixToolkit<T> {
	
	public final static int REAL 		= 0b00000001;
	public final static int SYMMETRIC 	= 0b10000010;
	public final static int HERMITIAN 	= 0b10000100;
	public final static int UHESSENBERG = 0b10001000;
	public final static int LHESSENBERG = 0b10010000;
	public final static int UTRIANGULAR = 0b10101000;
	public final static int LTRIANGULAR = 0b11010000;
	public final static int SQUARE		= 0b10000000;
	public final static int DIAGONAL	= 0b11111010;
	
	T[][] matrix;
	int rows, cols, augmcols = 0;
	
	public static MatrixToolkit<?> getToolkit(MMatrix m) {
		boolean allReal = true;
		boolean complex = true;
		for(int i = 0; i < m.shape().rows(); i++) {
			for(int j = 0; j < m.shape().cols(); j++) {
				if(!(m.get(i, j) instanceof MReal)) {
					allReal = false;
					if(!(m.get(i, j) instanceof MComplex)) {
						complex = false;
						break;
					}
				}
			}
			if(!complex)
				break;
		}
		if(allReal)
			return new DoubleMatrixToolkit(m);
		else if(complex) {
			MScalar[][] s = new MScalar[m.shape().rows()][m.shape().cols()];
			for(int i = 0; i < s.length; i++)
				for(int j = 0; j < s[i].length; j++)
					s[i][j] = (MScalar) m.get(i, j);
			return new ScalarMatrixToolkit(s);
		}
		else
			throw new IllegalArgumentException("Matrix operations are only supported for scalar valued matrices.");
	}
	
	public void setAugmCols(int cols) {
		augmcols = cols;
	}

	/**
	 * Switches rows i and j.
	 * @param i index of the row to be replaced by the <tt>j</tt>-th row.
	 * @param j index of the row to be replaced by the <tt>i</tt>-th row.
	 */
	public void switchRows(int i, int j) {
		if (i == j)
			return;
		T[] temp = matrix[i];
		matrix[i] = matrix[j];
		matrix[j] = temp;
	}
	
	public abstract MMatrix toMMatrix();
	
	public abstract boolean isReal();

	/**
	 * Adds the i-th row multiplied with c to the n-th row. Like so: <br/>
	 * <tt>M_nj -> M_nj+c*M_ij   with   j=1....m</tt>
	 * @param n the index of the row to which the <tt>i</tt>-th row will be added.
	 * @param i the row to be added to the <tt>n</tt>-th row
	 * @param c a constant with which the i-th row will be multiplied before being added to the <tt>n</tt>-th row.
	 */
	public abstract void addToRow(int n, int i, T c);
	
	/**
	 * Multiplies the <tt>n</tt>-th row with a constant scalar value.
	 * @param n the index of the row to be multiplied with <tt>c</tt>
	 * @param c the value with which the row will be multiplied.
	 */
	public abstract void multiplyRow(int n, T c);
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M
	 */
	public abstract void multiply(T[][] M);
	
	/**
	 * Recursively reorders the rows of this matrix.
	 * This means that the rows with the most leading zeros will end up on the bottom,
	 * and the rows with no leading zeros will end up at the top.
	 * @author Siemen Geurts
	 * @param maxRow the row at which the reordering stops (used for the recursion. To reorder the whole matrix, set <tt>maxRow=matrix.rows-1</tt>)
	 */
	public abstract void reorder(int maxRow);
	
	public boolean isSquare() {
		return rows==cols;
	}
	
	public boolean isSymmetric() {
		return isSymmetric(isSquare() ? SQUARE : 0);
	}
	
	public boolean isSymmetric(int mask) {
		if((mask & SQUARE) != SQUARE)
			return false;
		for(int i = 0; i < rows; i++)
			for(int j = i+1; j < cols; j++)
				if(!matrix[i][j].equals(matrix[j][i]))
					return false;
		return true;
	}
	
	public boolean isUTriangular() {
		return isUTriangular(isUHessenberg() ? UHESSENBERG : 0);
	}
	
	public boolean isUTriangular(int mask) {
		if((mask & UHESSENBERG) != UHESSENBERG) return false;
		//because the matrix is upper hessenberg,
		//only the first negative subdiagonal needs to contain no non-zero elements.
		for(int i = 1; i < rows; i++)
			if(!matrix[i][i-1].equals(0d)) return false;
		return true;				
	}
	
	public boolean isLTriangular() {
		return isLTriangular(isLHessenberg() ? LHESSENBERG : 0);
	}
	
	public boolean isLTriangular(int mask) {
		if((mask & LHESSENBERG) != LHESSENBERG) return false;
		//because the matrix is lower hessenberg,
		//only the first positive subdiagonal needs to contain no non-zero elements.
		for(int i = 0; i < rows-1; i++)
				if(!matrix[i][i+1].equals(0d)) return false;
		return true;				
	}
	
	public boolean isUHessenberg() {
		return isUHessenberg(isSquare() ? SQUARE : 0);
	}
	
	public boolean isUHessenberg(int mask) {
		if((mask & SQUARE) != SQUARE) return false;
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < i-1; j++)
				if(!matrix[i][j].equals(0d)) return false;
		return true;				
	}
	
	public boolean isLHessenberg() {
		return isLHessenberg(isSquare() ? SQUARE : 0);
	}
	
	public boolean isLHessenberg(int mask) {
		if((mask & SQUARE) != SQUARE) return false;
		for(int i = 0; i < rows; i++)
			for(int j = i+2; j < cols; j++)
				if(!matrix[i][j].equals(0d)) return false;
		return true;				
	}
	
	public boolean isHermitian() {
		return isHermitian((0 | (isReal() ? REAL : 0)) | (isSymmetric() ? SYMMETRIC : 0));
	}
	
	public abstract boolean isHermitian(int mask);
	
	public int classify() {
		int mask = 0;
		if(isSquare()) mask |= SQUARE;
		else return mask;
		if(rows < 2) return mask;
		
		if(isReal()) mask |= REAL;
		
		if(isSymmetric(mask)) mask |= SYMMETRIC;
		if(isHermitian(mask)) mask |= HERMITIAN;
		
		if(isUHessenberg(mask)) mask |= UHESSENBERG;
		if(isUTriangular(mask)) mask |= UTRIANGULAR;
		if((mask & UTRIANGULAR) == UTRIANGULAR && (mask & SYMMETRIC) == SYMMETRIC)
			return mask |= DIAGONAL;
		if(isLHessenberg(mask)) mask |= LHESSENBERG;
		if(isLTriangular(mask)) mask |= LTRIANGULAR;
		return mask;
	}
	
	public String maskAsString(int mask) {
		String s = "";
		if((mask & REAL)==REAL)
			s += " real";
		if((mask & SQUARE) == SQUARE)
			s += " square";
		if((mask & SYMMETRIC) == SYMMETRIC)
			s += " symmetric";
		if((mask & HERMITIAN) == HERMITIAN)
			s += " hermitian";
		if((mask & UHESSENBERG) == UHESSENBERG)
			s += " upper-hessenberg";
		if((mask & LHESSENBERG) == LHESSENBERG)
			s += " lower-hessenberg";
		if((mask & UTRIANGULAR) == UTRIANGULAR)
			s += " upper-triangular";
		if((mask & LTRIANGULAR) == LTRIANGULAR)
			s += " lower-triangular";
		if((mask & DIAGONAL) == DIAGONAL)
			s += " diagonal";
		return s;
	}
}
