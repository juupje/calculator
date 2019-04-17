package algorithms.linalg;

import algorithms.Algorithm;
import helpers.Shape;
import helpers.Tools;
import helpers.exceptions.ShapeException;
import mathobjects.MMatrix;
import mathobjects.MVector;
import mathobjects.MathObject;

public class LUDecomposition extends Algorithm {

	Shape shape;
	MatrixToolkit mtk;
	
	public LUDecomposition() {}
	
	public LUDecomposition(MMatrix m) {
		mtk = new MatrixToolkit(m);
		shape = new Shape(mtk.rows, mtk.cols);
		prepared = true;
	}
	
	@Override
	public MVector execute() {
		if(!prepared) return null;
		return lu();
	}
	
	protected MVector lu() {
		int n = mtk.cols;
		double[][] L = new double[n][n];
		double[][] U = new double[n][n];
		double[][] P = mtk.getPivotMatrix();
		mtk.matrix = MatrixToolkit.multiply(P, mtk.matrix);
		for(int col = 0; col < n; col++) {
			L[col][col]=1;
			for(int row = 0; row < col+1; row++) {
				double s = 0;
				for(int k = 0; k < row; k++) {
					s += U[k][col]*L[row][k];
				}
				U[row][col] = mtk.matrix[row][col]-s;
			}
			for(int row = col; row < n; row++) {
				double s = 0;
				for(int k = 0; k < col; k++)
					s += U[k][col] * L[row][k];
				L[row][col] = (mtk.matrix[row][col] - s)/U[col][col];
			}
		}
		return new MVector(new MMatrix(L), new MMatrix(U), new MMatrix(P));
	}
	
	@Override
	public MVector execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	public void prepare(MathObject[] args) {
		super.prepare(args);
		if(args.length == 1 && args[0] instanceof MMatrix)
			mtk = new MatrixToolkit((MMatrix) args[0]); //no need to copy as the toolkit only works with the double values.
		else
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
